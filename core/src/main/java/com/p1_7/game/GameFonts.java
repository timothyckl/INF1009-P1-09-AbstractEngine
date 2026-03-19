package com.p1_7.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator.FreeTypeFontParameter;

/**
 * Shared font factory for game text presets built from the menu TTF asset.
 */
public final class GameFonts {

    private static final String TTF_ASSET = "menu/Kenney_Future.ttf";

    private GameFonts() { }

    public static BitmapFont createGoldDisplayFont(int size) {
        return createFont(size,
            new Color(1f, 0.92f, 0.55f, 1f),
            2,
            -2,
            new Color(0f, 0f, 0f, 0.5f));
    }

    public static BitmapFont createDarkTextFont(int size) {
        return createFont(size, new Color(0.10f, 0.16f, 0.24f, 1f), 0, 0, null);
    }

    public static BitmapFont createPromptFont() {
        return createFont(28,
            new Color(0.10f, 0.16f, 0.24f, 1f),
            1,
            -1,
            new Color(1f, 1f, 1f, 0.35f));
    }

    public static void dispose(BitmapFont... fonts) {
        for (BitmapFont font : fonts) {
            if (font != null) {
                font.dispose();
            }
        }
    }

    private static BitmapFont createFont(int size, Color color,
                                         int shadowOffsetX, int shadowOffsetY, Color shadowColor) {
        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal(TTF_ASSET));
        try {
            FreeTypeFontParameter params = new FreeTypeFontParameter();
            params.size = size;
            params.color = color;
            params.shadowOffsetX = shadowOffsetX;
            params.shadowOffsetY = shadowOffsetY;
            params.shadowColor = shadowColor;
            return generator.generateFont(params);
        } finally {
            generator.dispose();
        }
    }
}
