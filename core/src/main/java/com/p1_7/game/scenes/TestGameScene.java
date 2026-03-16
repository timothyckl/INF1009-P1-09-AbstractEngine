package com.p1_7.game.scenes;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.p1_7.abstractengine.collision.CollisionManager;
import com.p1_7.abstractengine.movement.MovementManager;
import com.p1_7.abstractengine.render.ICustomRenderable;
import com.p1_7.abstractengine.render.IRenderItem;
import com.p1_7.abstractengine.render.IShapeRenderer;
import com.p1_7.abstractengine.render.ISpriteBatch;
import com.p1_7.abstractengine.scene.Scene;
import com.p1_7.abstractengine.scene.SceneContext;
import com.p1_7.abstractengine.transform.ITransform;
import com.p1_7.game.Settings;
import com.p1_7.game.core.Transform2D;
import com.p1_7.game.entities.AnswerTile;
import com.p1_7.game.entities.Enemy;
import com.p1_7.game.entities.ExitDoor;
import com.p1_7.game.entities.Player;
import com.p1_7.game.entities.Wall;
import com.p1_7.game.platform.GdxShapeRenderer;
import com.p1_7.game.platform.GdxSpriteBatch;

import java.util.ArrayList;
import java.util.List;

/**
 * minimal test scene that exercises every entity type.
 *
 * layout (1280×720 window):
 *   - rectangular arena with 4 border walls
 *   - player starts left side (WASD / arrow keys)
 *   - one patrolling enemy
 *   - three answer tiles: question "4 × 6 = ?" → correct answer 24
 *   - exit door (unlocks once a correct answer is collected)
 *
 * HUD drawn procedurally at the top: question, health, score.
 * status messages printed to Gdx.app.log so you can see events in the console.
 *
 * replace this scene with your real GameScene once you're ready.
 */
public class TestGameScene extends Scene {

    // ── arena geometry ───────────────────────────────────────────────────────
    private static final float ARENA_X = 100f;
    private static final float ARENA_Y = 80f;
    private static final float ARENA_W = 1080f;
    private static final float ARENA_H = 560f;
    private static final float WALL_T  = 32f;   // wall thickness

    // ── question ─────────────────────────────────────────────────────────────
    private static final String QUESTION    = "4 x 6 = ?";
    private static final int    ANSWER      = 24;
    private static final int[]  TILE_VALUES = { 18, 24, 30 };

    // ── invincibility frames after enemy hit ─────────────────────────────────
    private static final float INVINCIBLE_DURATION = 1.5f;
    private float invincTimer = 0f;

    // ── manager refs ─────────────────────────────────────────────────────────
    private final MovementManager  movementManager;
    private final CollisionManager collisionManager;

    // ── entities ─────────────────────────────────────────────────────────────
    private Player           player;
    private Enemy            enemy;
    private final List<Wall>        walls       = new ArrayList<>();
    private final List<AnswerTile>  answerTiles = new ArrayList<>();
    private ExitDoor         exitDoor;

    // ── UI ───────────────────────────────────────────────────────────────────
    private BitmapFont font;
    private HudOverlay hud;  // procedural HUD drawn via ICustomRenderable

    // tracks whether the scene is over (win/lose) to stop further updates
    private boolean finished = false;

    public TestGameScene(MovementManager movementManager,
                         CollisionManager collisionManager) {
        this.name              = "testgame";
        this.movementManager   = movementManager;
        this.collisionManager  = collisionManager;
    }

    // ── lifecycle ────────────────────────────────────────────────────────────

    @Override
    public void onEnter(SceneContext context) {
        finished   = false;
        invincTimer = 0f;

        font = new BitmapFont(); // default built-in LibGDX font
        font.getData().setScale(1.4f);

        // set world bounds for spatial collision optimisation
        collisionManager.setWorldBounds(
            new float[]{ 0f, 0f },
            new float[]{ Settings.WINDOW_WIDTH, Settings.WINDOW_HEIGHT });

        // ── walls: 4 borders ─────────────────────────────────────────────────
        addWall(context, ARENA_X,                      ARENA_Y,                       ARENA_W, WALL_T); // bottom
        addWall(context, ARENA_X,                      ARENA_Y + ARENA_H - WALL_T,    ARENA_W, WALL_T); // top
        addWall(context, ARENA_X,                      ARENA_Y,                       WALL_T,  ARENA_H); // left
        addWall(context, ARENA_X + ARENA_W - WALL_T,   ARENA_Y,                       WALL_T,  ARENA_H); // right

        // interior dividing wall with a gap (creates simple two-room layout)
        float divX = ARENA_X + ARENA_W * 0.5f;
        addWall(context, divX, ARENA_Y + WALL_T,       WALL_T, ARENA_H * 0.55f);      // bottom half divider

        // ── player ───────────────────────────────────────────────────────────
        float playerX = ARENA_X + WALL_T + 40f;
        float playerY = ARENA_Y + ARENA_H / 2f - Player.PLAYER_HEIGHT / 2f;
        player = (Player) context.entities().createEntity(() -> new Player(playerX, playerY));
        player.setEventHandler(() -> {
            if (invincTimer <= 0f) {
                player.loseHealth();
                invincTimer = INVINCIBLE_DURATION;
                System.out.println("[TEST] Enemy hit! Health: " + player.getHealth());
                if (!player.isAlive()) {
                    System.out.println("[TEST] Game over — player dead.");
                    finished = true;
                }
            }
        });
        movementManager.registerMovable(player);
        collisionManager.registerCollidable(player);

        // ── enemy ─────────────────────────────────────────────────────────────
        float enemyMinX = ARENA_X + WALL_T + 20f;
        float enemyMaxX = divX - Enemy.ENEMY_WIDTH - 10f;
        float enemyY    = ARENA_Y + WALL_T + 60f;
        enemy = (Enemy) context.entities().createEntity(
            () -> new Enemy(enemyMinX + 80f, enemyY, enemyMinX, enemyMaxX));
        movementManager.registerMovable(enemy);
        collisionManager.registerCollidable(enemy);

        // ── answer tiles (right half of arena) ───────────────────────────────
        float tileY     = ARENA_Y + ARENA_H / 2f - AnswerTile.TILE_SIZE / 2f;
        float tileStart = divX + WALL_T + 40f;
        for (int i = 0; i < TILE_VALUES.length; i++) {
            final int val = TILE_VALUES[i];
            final boolean correct = (val == ANSWER);
            final float tx = tileStart + i * (AnswerTile.TILE_SIZE + 30f);
            AnswerTile tile = (AnswerTile) context.entities().createEntity(
                () -> new AnswerTile(tx, tileY, val, correct, font));
            tile.setHandler((isCorrect, value) -> {
                if (isCorrect) {
                    player.addScore(100);
                    System.out.println("[TEST] Correct! Score: " + player.getScore());
                    exitDoor.unlock();
                    System.out.println("[TEST] Exit door unlocked.");
                } else {
                    player.loseHealth();
                    System.out.println("[TEST] Wrong answer (" + value + ")! Health: " + player.getHealth());
                    if (!player.isAlive()) {
                        System.out.println("[TEST] Game over — player dead.");
                        finished = true;
                    }
                }
            });
            answerTiles.add(tile);
            collisionManager.registerCollidable(tile);
        }

        // ── exit door ─────────────────────────────────────────────────────────
        float doorX = ARENA_X + ARENA_W - WALL_T - ExitDoor.DOOR_WIDTH - 20f;
        float doorY = ARENA_Y + ARENA_H / 2f - ExitDoor.DOOR_HEIGHT / 2f;
        exitDoor = (ExitDoor) context.entities().createEntity(
            () -> new ExitDoor(doorX, doorY));
        exitDoor.setHandler(() -> {
            System.out.println("[TEST] Level complete! Final score: " + player.getScore());
            finished = true;
            context.changeScene("levelcomplete");
        });
        collisionManager.registerCollidable(exitDoor);

        // ── HUD overlay ───────────────────────────────────────────────────────
        hud = new HudOverlay();
    }

    @Override
    public void onExit(SceneContext context) {
        // unregister from managers
        movementManager.unregisterMovable(player);
        collisionManager.unregisterCollidable(player);

        movementManager.unregisterMovable(enemy);
        collisionManager.unregisterCollidable(enemy);

        for (AnswerTile tile : answerTiles) {
            collisionManager.unregisterCollidable(tile);
            context.entities().removeEntity(tile.getId());
        }
        answerTiles.clear();

        collisionManager.unregisterCollidable(exitDoor);
        for (Wall wall : walls) {
            collisionManager.unregisterCollidable(wall);
            context.entities().removeEntity(wall.getId());
        }
        walls.clear();

        context.entities().removeEntity(player.getId());
        context.entities().removeEntity(enemy.getId());
        context.entities().removeEntity(exitDoor.getId());

        if (font != null) { font.dispose(); font = null; }
    }

    // ── per-frame ────────────────────────────────────────────────────────────

    @Override
    public void update(float deltaTime, SceneContext context) {
        if (finished) return;

        // tick invincibility timer
        if (invincTimer > 0f) invincTimer -= deltaTime;

        // player movement
        player.updateMovement(context.input());

        // enemy AI
        enemy.updateAI();
    }

    @Override
    public void submitRenderable(SceneContext context) {
        // walls
        for (Wall wall : walls) context.renderQueue().queue(wall);

        // answer tiles
        for (AnswerTile tile : answerTiles) context.renderQueue().queue(tile);

        // exit door
        context.renderQueue().queue(exitDoor);

        // entities
        context.renderQueue().queue(enemy);
        context.renderQueue().queue(player);

        // HUD last (on top)
        context.renderQueue().queue(hud);
    }

    // ── helpers ──────────────────────────────────────────────────────────────

    private void addWall(SceneContext context, float x, float y, float w, float h) {
        Wall wall = (Wall) context.entities().createEntity(() -> new Wall(x, y, w, h));
        walls.add(wall);
        collisionManager.registerCollidable(wall);
    }

    // ── inner HUD class ──────────────────────────────────────────────────────

    /**
     * procedural HUD drawn at the top of the screen.
     * shows the current question, health, and score.
     */
    private class HudOverlay implements IRenderItem, ICustomRenderable {

        private final Transform2D transform =
            new Transform2D(0, Settings.WINDOW_HEIGHT - 50f,
                            Settings.WINDOW_WIDTH, 50f);

        @Override public String     getAssetPath() { return null; }
        @Override public ITransform getTransform() { return transform; }

        @Override
        public void renderCustom(ISpriteBatch batch, IShapeRenderer shapeRenderer) {
            ShapeRenderer sr = ((GdxShapeRenderer) shapeRenderer).unwrap();
            SpriteBatch   sb = ((GdxSpriteBatch)   batch).unwrap();

            float barY = Settings.WINDOW_HEIGHT - 50f;

            // background bar
            sr.setColor(0.1f, 0.1f, 0.1f, 0.85f);
            sr.rect(0, barY, Settings.WINDOW_WIDTH, 50f);

            sr.end();
            sb.begin();

            font.setColor(Color.WHITE);

            // question
            font.draw(sb, "Question: " + QUESTION,
                20f, Settings.WINDOW_HEIGHT - 16f);

            // health
            String heartStr = "Health: " + (player != null ? player.getHealth() : 3);
            font.draw(sb, heartStr,
                Settings.WINDOW_WIDTH / 2f - 60f, Settings.WINDOW_HEIGHT - 16f);

            // score
            String scoreStr = "Score: " + (player != null ? player.getScore() : 0);
            font.draw(sb, scoreStr,
                Settings.WINDOW_WIDTH - 200f, Settings.WINDOW_HEIGHT - 16f);

            // invincibility hint
            if (invincTimer > 0f) {
                font.setColor(Color.YELLOW);
                font.draw(sb, "Invincible! " + String.format("%.1f", invincTimer) + "s",
                    20f, Settings.WINDOW_HEIGHT - 36f);
            }

            sb.end();
            sr.begin(ShapeRenderer.ShapeType.Filled);
        }
    }
}
