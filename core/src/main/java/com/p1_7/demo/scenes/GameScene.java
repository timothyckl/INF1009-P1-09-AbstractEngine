package com.p1_7.demo.scenes;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.math.MathUtils;
import com.p1_7.game.managers.AudioManager;
import com.badlogic.gdx.utils.Array;
import com.p1_7.abstractengine.collision.CollisionManager;
import com.p1_7.abstractengine.entity.IEntityManager;
import com.p1_7.abstractengine.entity.IEntityMutator;
import com.p1_7.abstractengine.input.IInputQuery;
import com.p1_7.abstractengine.movement.MovementManager;
import com.p1_7.abstractengine.render.IRenderQueue;
import com.p1_7.abstractengine.scene.Scene;
import com.p1_7.abstractengine.transform.ITransform;
import com.p1_7.abstractengine.scene.SceneContext;
import com.p1_7.demo.Settings;
import com.p1_7.demo.display.LivesDisplay;
import com.p1_7.demo.display.ScoreDisplay;
import com.p1_7.demo.entities.Background;
import com.p1_7.demo.entities.Bucket;
import com.p1_7.demo.entities.Cloud;
import com.p1_7.demo.entities.Droplet;

/**
 * main game scene for the "catch the droplet" demo.
 *
 * manages bucket, falling droplets, background, lives display, and
 * audio. spawns multiple droplets over time, detects caught/missed
 * droplets, and handles game over at 0 lives.
 */
public class GameScene extends Scene {

    /** initial number of lives */
    private static final int INITIAL_LIVES = 10;

    /** y coordinate where droplets spawn (top of screen) */
    private static final float SPAWN_Y = Settings.windowHeight;

    /** maximum concurrent droplets */
    private static final int MAX_DROPLETS = 5;

    /** seconds between droplet spawns */
    private static final float SPAWN_INTERVAL = 1.0f;
    private static final float HORIZONTAL_DRAG = 0.9f;
    private static final float MIN_HORIZONTAL_SPEED = 5f;

    // ==================== game entities ====================

    private Bucket bucket;
    private Array<Droplet> droplets = new Array<>();
    private Array<Cloud> clouds = new Array<>();
    private Background background;
    private LivesDisplay livesDisplay;
    private ScoreDisplay scoreDisplay;

    // ==================== audio ====================

    private AudioManager audioManager;

    // ==================== game state ====================

    private int score = 0;
    private boolean gameOver = false;
    private float spawnTimer = 0f;

    // ==================== manager references ====================

    private final MovementManager movementManager;
    private final CollisionManager collisionManager;

    /**
     * constructs a game scene with references to required managers.
     *
     * @param movementManager  the movement manager for bucket registration
     * @param collisionManager the collision manager for entity registration
     */
    public GameScene(MovementManager movementManager,
                     CollisionManager collisionManager) {
        this.name = "game";
        this.movementManager = movementManager;
        this.collisionManager = collisionManager;
    }

    // ==================== lifecycle hooks ====================

    @Override
    public void onEnter(SceneContext context) {
        IEntityManager entityManager = context.get(IEntityManager.class);
        this.audioManager = context.get(AudioManager.class);

        // 0. reset game state (for replays)
        score = 0;
        gameOver = false;
        spawnTimer = 0f;
        droplets.clear();

        // 1. set world bounds for broad-phase collision detection
        collisionManager.setWorldBounds(
            new float[]{0, 0},
            new float[]{Settings.windowWidth, Settings.windowHeight}
        );

        // 2. start music (assets pre-loaded by AudioManager.onInit)
        audioManager.playMusic("main", true);

        // 3. create background (not an entity)
        background = new Background(Settings.windowWidth, Settings.windowHeight);

        // 4. create lives display via entity manager
        livesDisplay = (LivesDisplay) entityManager.createEntity(() -> new LivesDisplay(INITIAL_LIVES));

        // 5. create score display via entity manager
        scoreDisplay = (ScoreDisplay) entityManager.createEntity(
            () -> new ScoreDisplay(520f, Settings.windowHeight - 10f, 0)
        );

        // 6. create bucket via entity manager
        float bucketX = (Settings.windowWidth / 2f) - (Bucket.BUCKET_WIDTH / 2f);
        float bucketY = 20f;
        bucket = (Bucket) entityManager.createEntity(() -> new Bucket(bucketX, bucketY));

        // 7. register bucket with managers
        movementManager.registerMovable(bucket);
        collisionManager.registerCollidable(bucket);

        // wire catch handler
        bucket.setCatchHandler(this::handleDropletCatch);

        // 8. create and register cloud deflectors
        createClouds(entityManager);

        // 9. spawn initial droplet
        spawnDroplet(entityManager);
    }

    @Override
    public void onExit(SceneContext context) {
        IEntityManager entityManager = context.get(IEntityManager.class);

        // audio is owned by AudioManager; clear local reference only
        this.audioManager = null;

        // dispose font
        if (livesDisplay != null) {
            livesDisplay.dispose();
        }

        // clean up bucket
        if (bucket != null) {
            movementManager.unregisterMovable(bucket);
            collisionManager.unregisterCollidable(bucket);
            entityManager.removeEntity(bucket.getId());
        }

        // clean up remaining droplets
        for (int i = 0; i < droplets.size; i++) {
            Droplet droplet = droplets.get(i);
            collisionManager.unregisterCollidable(droplet);
            entityManager.removeEntity(droplet.getId());
        }
        droplets.clear();

        // clean up clouds
        for (int i = 0; i < clouds.size; i++) {
            Cloud cloud = clouds.get(i);
            collisionManager.unregisterCollidable(cloud);
            entityManager.removeEntity(cloud.getId());
        }
        clouds.clear();

        // remove lives display entity
        if (livesDisplay != null) {
            entityManager.removeEntity(livesDisplay.getId());
        }

        // remove score display entity
        if (scoreDisplay != null) {
            scoreDisplay.dispose();
            entityManager.removeEntity(scoreDisplay.getId());
        }
    }

    @Override
    public void onSuspend(SceneContext context) {
        // pause music while pause menu is active
        audioManager.pauseMusic();
    }

    @Override
    public void onResume(SceneContext context) {
        // reapply volume (may have changed in pause menu) and resume
        audioManager.resumeMusic();
    }

    @Override
    public void update(float deltaTime, SceneContext context) {
        IEntityManager entityManager = context.get(IEntityManager.class);
        IInputQuery inputQuery = context.get(IInputQuery.class);

        // check for pause key press
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE) ||
            Gdx.input.isKeyJustPressed(Input.Keys.P)) {

            // get pause scene and pass current state
            PauseScene pauseScene = (PauseScene) context.getScene("pause");
            if (pauseScene != null) {
                pauseScene.setGameState(livesDisplay.getLives(), score);
            }

            // use suspendScene to preserve game state
            context.suspendScene("pause");
            return;
        }

        // early exit if game over
        if (gameOver) {
            return;
        }

        // 1. update bucket movement
        bucket.updateMovement(inputQuery);

        // 2. update spawn timer and spawn new droplets
        spawnTimer += deltaTime;
        if (spawnTimer >= SPAWN_INTERVAL && droplets.size < MAX_DROPLETS) {
            spawnDroplet(entityManager);
            spawnTimer = 0f;
        }

        // 3. update droplets (reverse iteration for safe removal)
        for (int i = droplets.size - 1; i >= 0; i--) {
            Droplet droplet = droplets.get(i);

            // manually move droplet (not registered with MovementManager)
            droplet.move(deltaTime);

            // reset to straight fall (no horizontal movement)
            float[] velocity = droplet.getVelocity();
            velocity[0] *= HORIZONTAL_DRAG;
            if (Math.abs(velocity[0]) < MIN_HORIZONTAL_SPEED) {
                velocity[0] = 0f;
            }
            velocity[1] = -Droplet.FALL_SPEED;
            droplet.setVelocity(velocity);

            // clamp droplet to horizontal bounds (prevent sliding off-screen)
            ITransform dropletTransform = droplet.getTransform();
            float dropletX = dropletTransform.getPosition(0);
            if (dropletX < 0) {
                dropletTransform.setPosition(0, 0);
            } else if (dropletX + Droplet.DROPLET_WIDTH > Settings.windowWidth) {
                dropletTransform.setPosition(0, Settings.windowWidth - Droplet.DROPLET_WIDTH);
            }

            // check if caught
            if (droplet.isCaught()) {
                // remove entity
                entityManager.removeEntity(droplet.getId());

                // unregister from collision manager
                collisionManager.unregisterCollidable(droplet);

                // remove from array
                droplets.removeIndex(i);
                continue;
            }

            // check if missed (fell below screen)
            if (droplet.getTransform().getPosition(1) < 0) {
                // decrement lives
                int currentLives = livesDisplay.getLives();
                livesDisplay.setLives(currentLives - 1);

                // remove entity
                entityManager.removeEntity(droplet.getId());

                // unregister from collision manager
                collisionManager.unregisterCollidable(droplet);

                // remove from array
                droplets.removeIndex(i);

                // check game over
                if (livesDisplay.getLives() == 0) {
                    gameOver = true;

                    // pass score to game over scene and transition
                    GameOverScene gameOverScene = (GameOverScene) context.getScene("gameover");
                    if (gameOverScene != null) {
                        gameOverScene.setScore(score);
                    }
                    context.changeScene("gameover");
                }
            }
        }
    }

    @Override
    public void submitRenderable(SceneContext context) {
        IRenderQueue renderQueue = context.get(IRenderQueue.class);

        // background first (draws behind)
        renderQueue.queue(background);

        // bucket
        renderQueue.queue(bucket);

        // all active droplets
        for (int i = 0; i < droplets.size; i++) {
            renderQueue.queue(droplets.get(i));
        }

        // clouds (draw above droplets but below ui)
        for (int i = 0; i < clouds.size; i++) {
            renderQueue.queue(clouds.get(i));
        }

        // ui displays last (draw on top)
        renderQueue.queue(livesDisplay);
        renderQueue.queue(scoreDisplay);
    }

    // ==================== helper methods ====================

    /**
     * spawns a new droplet at a random x position at the top of the screen.
     *
     * @param mutator the entity mutator for creating entities
     */
    private void spawnDroplet(IEntityMutator mutator) {
        float x = randomX();
        Droplet droplet = (Droplet) mutator.createEntity(() -> new Droplet(x, SPAWN_Y));

        // add to array
        droplets.add(droplet);

        // register with collision manager
        collisionManager.registerCollidable(droplet);
    }

    /**
     * returns a random x position that keeps the droplet fully on-screen.
     *
     * @return random x coordinate in valid range
     */
    private float randomX() {
        return MathUtils.random(Settings.windowWidth - Droplet.DROPLET_WIDTH);
    }

    /**
     * handles a droplet being caught by the bucket.
     *
     * increments score and plays catch sound. called by the bucket's
     * collision handler.
     *
     * @param droplet the droplet that was caught
     */
    private void handleDropletCatch(Droplet droplet) {
        // increment score
        score++;

        // update score display
        scoreDisplay.setScore(score);

        // play catch sound
        audioManager.playSound("drop");
    }

    /**
     * creates three cloud deflectors positioned to create obstacles
     * for falling droplets.
     *
     * @param mutator the entity mutator for creating entities
     */
    private void createClouds(IEntityMutator mutator) {
        // left cloud
        Cloud leftCloud = (Cloud) mutator.createEntity(
            () -> new Cloud(60f, 400f)
        );
        clouds.add(leftCloud);
        collisionManager.registerCollidable(leftCloud);

        // right cloud
        Cloud rightCloud = (Cloud) mutator.createEntity(
            () -> new Cloud(414f, 400f)
        );
        clouds.add(rightCloud);
        collisionManager.registerCollidable(rightCloud);

        // middle cloud (below the others)
        Cloud middleCloud = (Cloud) mutator.createEntity(
            () -> new Cloud(242f, 300f)
        );
        clouds.add(middleCloud);
        collisionManager.registerCollidable(middleCloud);
    }
}
