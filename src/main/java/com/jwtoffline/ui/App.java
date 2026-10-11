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
    private HomeView home;
    private DecodeView decode;
    private EncodeView encode;
    private VerifyView verify;
    private WorkspaceView workspace;
    private Screen currentScreen;

    @Override
    public void start(Stage stage) {
        AppContext context = new AppContext(this);
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

        VBox side = new VBox(6, brand);
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
        Label version = new Label("v0.1.0");
        version.getStyleClass().add("tagline");
        side.getChildren().addAll(spacer, version);
        return side;
    }

    @Override
    public void show(Screen screen) {
        if (currentScreen == Screen.DECODE && screen != Screen.DECODE) {
            decode.rememberCurrent();
        }
        Node view = switch (screen) {
            case DECODE -> decode;
            case ENCODE -> encode;
            case VERIFY -> verify;
            case WORKSPACE -> {
                workspace.refresh();
                yield workspace;
            }
            case HOME -> {
                home.refresh();
                yield home;
            }
        };
        root.setCenter(view);
        currentScreen = screen;
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
