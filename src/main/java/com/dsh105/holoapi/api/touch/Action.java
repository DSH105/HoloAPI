package com.dsh105.holoapi.api.touch;

public enum Action {

    LEFT_CLICK,
    RIGHT_CLICK,
    UNKNOWN;

    public static Action getFromId(int id) {
        switch (id) {
            case 0:
                return RIGHT_CLICK;
            case 1:
                return LEFT_CLICK;
            default:
                return UNKNOWN;
        }
    }
}
