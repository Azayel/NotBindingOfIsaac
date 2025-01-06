package Game;

final class Const
{
    // size of the world
    static final int WORLD_WIDTH      = 1920;
    static final int WORLD_HEIGHT     = 1080;

    // size of the displayed part of the world
    static final int WORLDPART_WIDTH  = 1920;
    static final int WORLDPART_HEIGHT = 1080;

    // border: when to scroll
    static final int SCROLL_BOUNDS    =  500;

    static final double SPAWN_INTERVAL = 1;
    static final double SPAWN_GRENADE  = 10.0;
    static final double LIFE_GRENADE   = 15.0;

    static final int TYPE_AVATAR  = 1;
    static final int TYPE_TEXT    = 2;
    static final int TYPE_TREE    = 3;
    static final int TYPE_ZOMBIE  = 4;
    static final int TYPE_SHOT    = 5;
    static final int TYPE_GRENADE = 6;


}