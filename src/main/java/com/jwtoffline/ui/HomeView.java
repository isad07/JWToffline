package com.jwtoffline.ui;

import java.util.List;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

/** Main menu: quick links to each tool and the recent token history. */
class HomeView extends VBox {

    private final AppContext ctx;
    private final ListView<String> recentList = new ListView<>();

    HomeView(AppContext ctx) {
        this.ctx = ctx;
        setSpacing(14);
        setPadding(new Insets(28));

        HBox cards = new HBox(14,
                card("Decode a token", "Paste a JWT and read its header, payload and expiry.", Screen.DECODE),
                card("Build a token", "Write claims, pick an algorithm and sign locally.", Screen.ENCODE),
                card("Verify a signature", "Check a token with its secret or RSA public key.", Screen.VERIFY),
                card("Workspace", "Saved tokens with labels, ready to reopen.", Screen.WORKSPACE));

        recentList.setPlaceholder(new Label("Tokens you decode or create show up here."));
        recentList.setPrefHeight(220);
        recentList.setOnMouseClicked(e -> {
            String selected = recentList.getSelectionModel().getSelectedItem();
            if (e.getClickCount() == 2 && selected != null) {
                ctx.navigator().openInDecoder(selected);
            }
        });
        VBox.setVgrow(recentList, Priority.ALWAYS);

        getChildren().addAll(
                Ui.title("Welcome to JWTOffline"),
                Ui.caption("Decode, build and verify JSON Web Tokens without a browser or an internet connection."),
                cards,
                Ui.sectionLabel("Recent tokens (double click to open)"),
                recentList);
    }

    private VBox card(String heading, String body, Screen target) {
        Label h = new Label(heading);
        h.getStyleClass().add("card-title");
        Label b = Ui.caption(body);
        Button open = new Button("Open");
        open.getStyleClass().add("primary");
        open.setOnAction(e -> ctx.navigator().show(target));
        VBox card = new VBox(8, h, b, open);
        card.getStyleClass().add("card");
        card.setPadding(new Insets(16));
        card.setPrefWidth(200);
        HBox.setHgrow(card, Priority.ALWAYS);
        return card;
    }

    void refresh() {
        List<String> recent = ctx.history.recent();
        recentList.getItems().setAll(recent);
    }
}
