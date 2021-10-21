package net.eltown.quadplots.components.math;

import cn.nukkit.Player;

public enum Direction {

    NORTH,
    EAST,
    SOUTH,
    WEST,
    ALL;

    public static Direction getPlayerDirection(final Player player) {
        switch (player.getDirection()) {
            case NORTH:
                return NORTH;
            case EAST:
                return EAST;
            case SOUTH:
                return SOUTH;
            case WEST:
                return WEST;
            default:
                return NORTH;
        }
    }

}
