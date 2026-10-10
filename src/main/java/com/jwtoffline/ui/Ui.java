package com.jwtoffline.ui;

import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;

/** Small helpers so every screen builds controls the same way. */
final class Ui {

    private Ui() {
    }

    static Label title(String text) {
        Label l = new Label(text);
        l.getStyleClass().add("screen-title");
        return l;
    }

    static Label caption(String text) {
        Label l = new Label(text);
        l.getStyleClass().add("caption");
        l.setWrapText(true);
        return l;
    }

    static Label sectionLabel(String text) {
        Label l = new Label(text);
        l.getStyleClass().add("section-label");
        return l;
    }

    static TextArea codeArea(String prompt, int rows, boolean editable) {
        TextArea area = new TextArea();
        area.setPromptText(prompt);
        area.setPrefRowCount(rows);
        area.setWrapText(true);
        area.setEditable(editable);
        area.getStyleClass().add("code-area");
        return area;
    }

    static Label status() {
        Label l = new Label();
        l.getStyleClass().add("status");
        l.setWrapText(true);
        l.setVisible(false);
        l.setManaged(false);
        return l;
    }

    static void setStatus(Label label, String text, String kind) {
        label.setText(text);
        label.getStyleClass().removeAll("status-ok", "status-bad", "status-warn");
        label.getStyleClass().add("status-" + kind);
        label.setVisible(true);
        label.setManaged(true);
    }

    static void hideStatus(Label label) {
        label.setVisible(false);
        label.setManaged(false);
    }

    static void copy(String text) {
        ClipboardContent content = new ClipboardContent();
        content.putString(text);
        Clipboard.getSystemClipboard().setContent(content);
    }
}
