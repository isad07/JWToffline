package com.jwtoffline.ui;

/** Screens reachable from the side navigation. */
public enum Screen {
    HOME("Home"),
    DECODE("Decode"),
    ENCODE("Encode"),
    VERIFY("Verify"),
    WORKSPACE("Workspace");

    private final String title;

    Screen(String title) {
        this.title = title;
    }

    public String title() {
        return title;
    }
}
