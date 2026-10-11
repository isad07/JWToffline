package com.jwtoffline.ui;

import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.layout.VBox;

final class Ui {

    enum StatusKind {
        OK("status-ok"), BAD("status-bad"), WARN("status-warn");

        private final String styleClass;

        StatusKind(String styleClass) {
            this.styleClass = styleClass;
        }
    }

    private Ui() {
    }

    static void setupScreen(VBox screen, double spacing) {
        screen.setSpacing(spacing);
        screen.setPadding(new Insets(28));
    }

    static Button button(String text, Runnable action, String... styles) {
        Button button = new Button(text);
        button.getStyleClass().addAll(styles);
        button.setOnAction(e -> action.run());
        return button;
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

    static void setStatus(Label label, String text, StatusKind kind) {
        label.setText(text);
        label.getStyleClass().removeAll("status-ok", "status-bad", "status-warn");
        label.getStyleClass().add(kind.styleClass);
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
