package com.p1_7.demo.scenes;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.p1_7.abstractengine.entity.IEntityManager;
import com.p1_7.game.managers.AudioManager;
import com.p1_7.abstractengine.render.IRenderQueue;
import com.p1_7.abstractengine.scene.Scene;
import com.p1_7.abstractengine.scene.SceneContext;
import com.p1_7.demo.Settings;
import com.p1_7.demo.display.TextDisplay;
import com.p1_7.demo.display.VolumeSlider;
import com.p1_7.demo.entities.Background;

/**
 * pause scene shown when player pauses the game.
 *
 * displays current lives, current score, volume control slider,
 * and instructions to resume playing.
 */
public class PauseScene extends Scene {

    /** background image */
    private Background background;

    /** pause title display */
    private TextDisplay titleDisplay;

    /** lives display */
    private TextDisplay livesDisplay;

    /** score display */
    private TextDisplay scoreDisplay;

    /** volume label display */
    private TextDisplay volumeLabel;

    /** volume slider component */
    private VolumeSlider volumeSlider;

    /** resume prompt display */
    private TextDisplay resumePrompt;

    /** current lives to display */
    private int currentLives = 0;

    /** current score to display */
    private int currentScore = 0;

    /**
     * constructs a pause scene.
     */
    public PauseScene() {
        this.name = "pause";
    }

    /**
     * sets the game state to display.
     * should be called before transitioning to this scene.
     *
     * @param lives the current lives count
     * @param score the current score
     */
    public void setGameState(int lives, int score) {
        this.currentLives = lives;
        this.currentScore = score;
    }

    @Override
    public void onEnter(SceneContext context) {
        IEntityManager entityManager = context.get(IEntityManager.class);

        // 1. create background
        background = new Background(Settings.windowWidth, Settings.windowHeight);

        // 2. create title text (large, centred at top)
        String titleText = "PAUSED";
        float titleX = Settings.windowWidth / 2f - 60f; // approximate centring
        float titleY = Settings.windowHeight * 0.75f;
        titleDisplay = new TextDisplay(titleText, titleX, titleY, 2.0f);

        // 3. create lives text (upper middle)
        String livesText = "Lives: " + currentLives;
        float livesX = Settings.windowWidth / 2f - 50f;
        float livesY = Settings.windowHeight * 0.6f;
        livesDisplay = new TextDisplay(livesText, livesX, livesY, 1.2f);

        // 4. create score text (middle)
        String scoreText = "Score: " + currentScore;
        float scoreX = Settings.windowWidth / 2f - 50f;
        float scoreY = Settings.windowHeight * 0.5f;
        scoreDisplay = new TextDisplay(scoreText, scoreX, scoreY, 1.2f);

        // 5. create volume label with instructions
        String volumeText = "Volume: (Use <- -> to adjust)";
        float volumeLabelX = Settings.windowWidth / 2f - 120f;
        float volumeLabelY = Settings.windowHeight * 0.35f;
        volumeLabel = new TextDisplay(volumeText, volumeLabelX, volumeLabelY, 1.0f);

        // 6. create volume slider
        float sliderX = Settings.windowWidth / 2f - 100f;
        float sliderY = Settings.windowHeight * 0.27f;
        volumeSlider = (VolumeSlider) entityManager.createEntity(
            () -> new VolumeSlider(sliderX, sliderY, 200f)
        );

        // 7. create resume prompt (bottom)
        String promptText = "Press ESC or P to resume";
        float promptX = Settings.windowWidth / 2f - 120f;
        float promptY = Settings.windowHeight * 0.15f;
        resumePrompt = new TextDisplay(promptText, promptX, promptY, 1.0f);
    }

    @Override
    public void onExit(SceneContext context) {
        IEntityManager entityManager = context.get(IEntityManager.class);

        // dispose fonts
        if (titleDisplay != null) {
            titleDisplay.dispose();
        }
        if (livesDisplay != null) {
            livesDisplay.dispose();
        }
        if (scoreDisplay != null) {
            scoreDisplay.dispose();
        }
        if (volumeLabel != null) {
            volumeLabel.dispose();
        }
        if (resumePrompt != null) {
            resumePrompt.dispose();
        }

        // remove volume slider entity
        if (volumeSlider != null) {
            entityManager.removeEntity(volumeSlider.getId());
        }
    }

    @Override
    public void update(float deltaTime, SceneContext context) {
        AudioManager audioManager = context.get(AudioManager.class);

        // update volume slider
        if (volumeSlider != null) {
            volumeSlider.update(deltaTime);
        }

        // apply volume changes to music in real-time via the audio manager
        audioManager.setMusicVolume(Settings.musicVolume);

        // check for resume keys
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE) ||
            Gdx.input.isKeyJustPressed(Input.Keys.P)) {
            context.changeScene("game");
        }
    }

    @Override
    public void submitRenderable(SceneContext context) {
        IRenderQueue renderQueue = context.get(IRenderQueue.class);

        // queue background first
        renderQueue.queue(background);

        // queue text displays
        renderQueue.queue(titleDisplay);
        renderQueue.queue(livesDisplay);
        renderQueue.queue(scoreDisplay);
        renderQueue.queue(volumeLabel);

        // queue volume slider
        renderQueue.queue(volumeSlider);

        // queue resume prompt
        renderQueue.queue(resumePrompt);
    }
}
