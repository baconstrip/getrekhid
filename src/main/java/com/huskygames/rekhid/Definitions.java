package com.huskygames.rekhid;


import com.huskygames.rekhid.slugger.util.DoublePair;
import com.huskygames.rekhid.slugger.util.IntPair;

import java.awt.*;
import java.io.IOException;
import java.io.InputStream;
import java.text.DecimalFormat;

public class Definitions {
    public static final int TARGET_FPS = 60;
    public static final float TARGET_TIME = 1000f / TARGET_FPS;

    public static final String NAME = "GET REKHI'D";

    public static final Rekhid.GameState DEFAULT_STATE = Rekhid.GameState.CONTROLLER_MENU;

    public static final int DEFAULT_WIDTH = 1280;
    public static final int DEFAULT_HEIGHT = 720;

    public static final IntPair DEFAULT_WORLD_SIZE = new IntPair(3840, 2160);

    public static final boolean DRAW_CENTRES = false;
    public static final Color CENTRE_COLOR = new Color(0, 255, 0, 100);

    public static final double MAX_VELOCITY = 10;

    public static final int DEFAULT_PLAYER_HEIGHT = 100;

    public static final DoublePair GRAVITY = new DoublePair(0, 0.09);

    public static final boolean DRAW_HITBOXES = false;

    public static final boolean DRAW_PADDING = true;

    public static final Color HITBOX_COLOR = new Color(0, 100, 255, 100);
    public static final Color HURTBOX_COLOR = new Color(245, 0, 0, 100);
    public static final Color PADDING_COLOR = new Color(45, 140, 0, 30);

    public static final boolean NOISY_RENDER = false;
    public static final boolean NOISY_COLLIDER = false;

    public static final Font DISPLAY_FONT;
    public static final Font FPS_FONT;
    public static final Color SCORE_BACKGROUND = new Color(10, 255, 50, 140);
    public static final IntPair SCORE_BACKGROUND_SIZE = new IntPair(150, 150);
    public static final double DEADZONE = 0.30;
    public static final int VIEWPORT_PADDING = 100;
    private static final String DAMAGE_PATTERN = "0.00";
    public static final DecimalFormat DAMAGE_FORMATTER = new DecimalFormat(DAMAGE_PATTERN);

    static {
        Font temp = null;
        try {
            InputStream res = Definitions.class.getClassLoader().getResourceAsStream("weebfont.ttf");
            temp = Font.createFont(Font.TRUETYPE_FONT, res);
            temp = temp.deriveFont(30f);
        } catch (FontFormatException | IOException e) {
            e.printStackTrace();
        }
        DISPLAY_FONT = temp;
        FPS_FONT = temp.deriveFont(12f);
    }
}
