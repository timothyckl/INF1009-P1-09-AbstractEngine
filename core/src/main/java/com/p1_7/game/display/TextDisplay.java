package com.p1_7.game.display;

import com.badlogic.gdx.graphics.g2d.BitmapFont;

public class TextDisplay extends BaseTextDisplay {

    private String text;

    public TextDisplay(String text, float x, float y, BitmapFont font) {
        super(x, y, 0f, 0f, font);
        setText(text);
    }

    // New constructor to support centered UI text for Menu/Level scenes
    public TextDisplay(String text, float x, float y, BitmapFont font, boolean centered) {
        super(x, y, 0f, 0f, font);
        this.centered = centered;
        setText(text);
    }

    public void setText(String text) {
        this.text = text;
        updateLayout(text); // Recalculate physical size whenever text changes
    }

    @Override
    public String getText() {
        return text;
    }
}