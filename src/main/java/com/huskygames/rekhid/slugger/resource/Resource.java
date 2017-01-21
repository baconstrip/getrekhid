package com.huskygames.rekhid.slugger.resource;

public enum Resource {

    // BEGIN RESOURCE LIST

    TEST_BACKGROUND("images/testbackground.jpg", Type.IMAGE),
    DEFAULT_LEVEL_BG("images/level.jpg", Type.IMAGE),
    LEVEL_COMPUTERS_BG("images/LevelComputers.jpg", Type.IMAGE),
    LEVEL_TERMINAL_BG("images/LevelTerminal.png", Type.IMAGE),
    MAIN_MENU_BG("images/menu1.jpg", Type.IMAGE),
    MENU_BG_MUSIC("music/meganddia.ogg", Type.MUSIC),

    KUHLS_HEAD("images/kuhl.jpg", Type.IMAGE),
    //STICK_MAN("spritesheets/sticks.png", Type.SPRITE_SHEET),
    STICK_MAN("spritesheets/sticksinvert.png", Type.SPRITE_SHEET),
    LEOS_HEAD("images/leo.jpg", Type.IMAGE),
    TEST_PROJECTILE("spritesheets/bird.png", Type.SPRITE_SHEET);


    // END RESOURCE LIST

    final Type type;
    final String location;

    Resource(String location, Type type) {
        this.location = location;
        this.type = type;
    }

    public enum Type {
        MUSIC,
        CLIP,
        IMAGE,
        SPRITE_SHEET,
        SPRITE_SHEET_DEFINITION
    }
}
