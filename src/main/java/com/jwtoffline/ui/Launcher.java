package com.jwtoffline.ui;

/** Plain entry point so the app also starts from a fat jar without JavaFX module flags. */
public final class Launcher {
    private Launcher() {
    }

    public static void main(String[] args) {
        App.main(args);
    }
}
