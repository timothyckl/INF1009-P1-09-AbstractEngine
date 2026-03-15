package com.p1_7.game.managers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.p1_7.game.Settings;

import java.util.HashMap;
import java.util.Map;

/**
 * Application-level singleton manager for handling all music and sound effects.
 * Encapsulates LibGDX audio operations, prevents memory leaks through central disposal,
 * and syncs globally with the game's Settings state.
 */
public class AudioManager {
    
    private static AudioManager instance;

    // Caches to prevent loading the same file multiple times
    private final Map<String, Music> musicCache = new HashMap<>();
    private final Map<String, Sound> soundCache = new HashMap<>();

    private Music currentMusic;
    private String currentMusicKey;

    // Private constructor enforces the Singleton pattern
    private AudioManager() {}

    public static AudioManager getInstance() {
        if (instance == null) {
            instance = new AudioManager();
        }
        return instance;
    }

    /**
     * Loads a music track into memory and caches it under the specified key.
     */
    public void loadMusic(String key, String filePath) {
        if (!musicCache.containsKey(key)) {
            Music music = Gdx.audio.newMusic(Gdx.files.internal(filePath));
            musicCache.put(key, music);
        }
    }

    /**
     * Loads a short sound effect into memory and caches it.
     */
    public void loadSound(String key, String filePath) {
        if (!soundCache.containsKey(key)) {
            Sound sound = Gdx.audio.newSound(Gdx.files.internal(filePath));
            soundCache.put(key, sound);
        }
    }

    /**
     * Plays a cached music track. Safely stops any currently playing music.
     */
    public void playMusic(String key, boolean loop) {
        // Prevent restarting the track if it's already the active music
        if (currentMusicKey != null && currentMusicKey.equals(key)) {
            return; 
        }
        
        if (currentMusic != null) {
            currentMusic.stop();
        }
        
        Music nextMusic = musicCache.get(key);
        if (nextMusic != null) {
            currentMusic = nextMusic;
            currentMusicKey = key;
            currentMusic.setLooping(loop);
            currentMusic.setVolume(Settings.VOLUME_LEVEL);
            currentMusic.play();
        } else {
            System.err.println("AudioManager Warning: Music key not found - " + key);
        }
    }

    /**
     * Plays a cached sound effect once. 
     * Can be called overlappingly for multiple rapid sound effects.
     */
    public void playSound(String key) {
        Sound sound = soundCache.get(key);
        if (sound != null) {
            sound.play(Settings.VOLUME_LEVEL);
        } else {
            System.err.println("AudioManager Warning: Sound key not found - " + key);
        }
    }

    /**
     * Polled by the main render loop to dynamically update the music volume 
     * when the player adjusts the Settings slider.
     */
    public void updateVolume() {
        if (currentMusic != null) {
            currentMusic.setVolume(Settings.VOLUME_LEVEL);
        }
    }

    /**
     * Disposes all loaded audio resources to prevent memory leaks on shutdown.
     */
    public void dispose() {
        for (Music music : musicCache.values()) {
            music.dispose();
        }
        for (Sound sound : soundCache.values()) {
            sound.dispose();
        }
        musicCache.clear();
        soundCache.clear();
        currentMusic = null;
        currentMusicKey = null;
    }
}