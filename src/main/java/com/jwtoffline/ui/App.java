package com.jwtoffline.ui;

import java.util.EnumMap;
import java.util.Map;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 * JWTOffline main window: side navigation on the left, the active screen in the middle.
 * Each screen is built once and kept, so text typed on one screen is still there when you come back.
 */
public class App extends Application implements AppContext.Navigator {

    private final BorderPane root = new BorderPane();
    private final Map<Screen, Button> navButtons = new EnumMap<>(Screen.class);
    private AppContext context;
    private HomeView home;
    private DecodeView decode;
    private EncodeView encode;
    private VerifyView verify;
    private WorkspaceView workspace;

    @Override
    public void start(Stage stage) {
        context = new AppContext(this);
        home = new HomeView(context);
        decode = new DecodeView(context);
        encode = new EncodeView(context);
        verify = new VerifyView(context);
        workspace = new WorkspaceView(context);

        root.setLeft(buildSidebar());
        show(Screen.HOME);

        Scene scene = new Scene(root, 1100, 720);
        scene.getStylesheets().add(App.class.getResource("app.css").toExternalForm());
        stage.setTitle("JWTOffline");
        stage.setMinWidth(900);
        stage.setMinHeight(600);
        stage.setScene(scene);
        stage.show();
    }

    private VBox buildSidebar() {
        Label brand = new Label("JWTOffline");
        brand.getStyleClass().add("brand");
        Label tagline = new Label("Nothing leaves this machine");
        tagline.getStyleClass().add("tagline");

        VBox side = new VBox(6, brand, tagline);
        side.getStyleClass().add("sidebar");
        side.setPadding(new Insets(20, 12, 20, 12));
        side.setPrefWidth(200);

        Region gap = new Region();
        gap.setPrefHeight(14);
        side.getChildren().add(gap);

        for (Screen s : Screen.values()) {
            Button b = new Button(s.title());
            b.getStyleClass().add("nav-button");
            b.setMaxWidth(Double.MAX_VALUE);
            b.setOnAction(e -> show(s));
            navButtons.put(s, b);
            side.getChildren().add(b);
        }

        Region spacer = new Region();
        VBox.setVgrow(spacer, Priority.ALWAYS);
        Label offline = new Label("Offline mode  •  v0.2 prototype");
        offline.getStyleClass().add("tagline");
        side.getChildren().addAll(spacer, offline);
        return side;
    }

    @Override
    public void show(Screen screen) {
        Node view;
        switch (screen) {
            case DECODE: view = decode; break;
            case ENCODE: view = encode; break;
            case VERIFY: view = verify; break;
            case WORKSPACE: workspace.refresh(); view = workspace; break;
            default: home.refresh(); view = home;
        }
        root.setCenter(view);
        navButtons.forEach((s, b) -> {
            b.getStyleClass().remove("nav-active");
            if (s == screen) {
                b.getStyleClass().add("nav-active");
            }
        });
    }

    @Override
    public void openInDecoder(String token) {
        decode.load(token);
        show(Screen.DECODE);
    }

    @Override
    public void openInVerifier(String token) {
        verify.load(token);
        show(Screen.VERIFY);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
