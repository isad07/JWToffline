package com.jwtoffline.ui;

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
