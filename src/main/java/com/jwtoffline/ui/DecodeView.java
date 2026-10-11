package com.jwtoffline.ui;

import com.jwtoffline.core.ClaimInspector;
import com.jwtoffline.core.DecodedJwt;
import com.jwtoffline.core.JwtException;
import java.util.Map;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextInputDialog;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

/** Decode screen: token in, header / payload / time claims out. Decodes as you type. */
class DecodeView extends VBox {

    private final AppContext ctx;
    private final TextArea tokenInput = Ui.codeArea("Paste a JWT here (a leading \"Bearer \" is fine)", 4, true);
    private final TextArea headerOut = Ui.codeArea("Header", 8, false);
    private final TextArea payloadOut = Ui.codeArea("Payload", 8, false);
    private final Label status = Ui.status();
    private final Label saveStatus = Ui.status();
    private final Label algLabel = new Label();
    private final GridPane times = new GridPane();
    private DecodedJwt current;
    private final Timeline timeRefresh = new Timeline(
            new KeyFrame(Duration.seconds(1), event -> refreshTimeStatus()));

    DecodeView(AppContext ctx) {
        this.ctx = ctx;
        Ui.setupScreen(this, 12);
        timeRefresh.setCycleCount(Animation.INDEFINITE);
        sceneProperty().addListener((obs, old, scene) -> {
            if (scene == null) {
                timeRefresh.stop();
            } else {
                refreshTimeStatus();
                timeRefresh.play();
            }
        });

        tokenInput.textProperty().addListener((obs, old, text) -> decode(text));
        tokenInput.focusedProperty().addListener((obs, old, focused) -> {
            if (!focused) {
                rememberCurrent();
            }
        });

        Button copyHeader = Ui.button("Copy header", () -> Ui.copy(headerOut.getText()));
        Button copyPayload = Ui.button("Copy payload", () -> Ui.copy(payloadOut.getText()));
        Button save = Ui.button("Save to workspace", this::saveToWorkspace);
        Button verify = Ui.button("Verify signature",
                () -> ctx.navigator().openInVerifier(tokenInput.getText()), "primary");
        Button clear = Ui.button("Clear", tokenInput::clear);
        HBox actions = new HBox(8, verify, save, copyHeader, copyPayload, clear);

        GridPane panes = new GridPane();
        panes.setHgap(14);
        panes.setVgap(6);
        for (int column = 0; column < 2; column++) {
            ColumnConstraints constraints = new ColumnConstraints();
            constraints.setPercentWidth(50);
            panes.getColumnConstraints().add(constraints);
        }
        panes.add(Ui.sectionLabel("Header"), 0, 0);
        panes.add(Ui.sectionLabel("Payload"), 1, 0);
        panes.add(headerOut, 0, 1);
        panes.add(payloadOut, 1, 1);
        for (TextArea output : new TextArea[]{headerOut, payloadOut}) {
            output.setMinSize(0, 0);
            output.setPrefHeight(300);
            output.setMaxHeight(300);
            GridPane.setVgrow(output, Priority.ALWAYS);
        }
        panes.setMaxHeight(Region.USE_PREF_SIZE);
        VBox.setVgrow(panes, Priority.ALWAYS);

        times.setHgap(18);
        times.setVgap(4);
        algLabel.getStyleClass().add("caption");

        getChildren().addAll(Ui.title("Decode"),
                tokenInput, status, panes, algLabel, Ui.sectionLabel("Time claims"), times, actions, saveStatus);
        showEmpty();
    }

    void load(String token) {
        tokenInput.setText(token);
        refreshTimeStatus();
        rememberCurrent();
    }

    void rememberCurrent() {
        if (current != null) {
            ctx.history.add(current.rawToken());
        }
    }

    private void decode(String text) {
        Ui.hideStatus(saveStatus);
        if (text == null || text.isBlank()) {
            showEmpty();
            return;
        }
        try {
            current = ctx.decoder.decode(text);
        } catch (JwtException ex) {
            showEmpty();
            Ui.setStatus(status, ex.getMessage(), Ui.StatusKind.BAD);
            return;
        }
        headerOut.setText(current.prettyHeader());
        payloadOut.setText(current.prettyPayload());
        algLabel.setText("Algorithm: " + current.algorithmName() + "   Signature: "
                + (current.signatureSegment().isEmpty() ? "none" : current.signatureSegment().length() + " chars"));
        ClaimInspector.Inspection inspection = ctx.inspector.inspect(current.payload());
        updateTimeStatus(inspection);
        fillTimes(inspection.readableTimes());
    }

    private void refreshTimeStatus() {
        if (current != null) {
            updateTimeStatus(ctx.inspector.inspect(current.payload()));
        }
    }

    private void updateTimeStatus(ClaimInspector.Inspection inspection) {
        Ui.StatusKind kind = switch (inspection.status()) {
            case VALID -> Ui.StatusKind.OK;
            case NO_EXPIRY -> Ui.StatusKind.WARN;
            default -> Ui.StatusKind.BAD;
        };
        Ui.setStatus(status, inspection.message(), kind);
    }

    private void fillTimes(Map<String, String> readable) {
        times.getChildren().clear();
        if (readable.isEmpty()) {
            times.add(Ui.caption("This token has no exp, iat or nbf claims."), 0, 0);
            return;
        }
        int row = 0;
        for (Map.Entry<String, String> e : readable.entrySet()) {
            Label key = new Label(e.getKey());
            key.getStyleClass().add("mono");
            times.add(key, 0, row);
            times.add(new Label(describeClaim(e.getKey())), 1, row);
            times.add(new Label(e.getValue()), 2, row);
            row++;
        }
    }

    private static String describeClaim(String claim) {
        return switch (claim) {
            case "exp" -> "Expires";
            case "iat" -> "Issued at";
            default -> "Not before";
        };
    }

    private void showEmpty() {
        current = null;
        headerOut.clear();
        payloadOut.clear();
        algLabel.setText("");
        times.getChildren().clear();
        Ui.hideStatus(status);
        Ui.hideStatus(saveStatus);
    }

    private void saveToWorkspace() {
        if (current == null) {
            Ui.setStatus(saveStatus, "Decode a valid token before saving it", Ui.StatusKind.WARN);
            return;
        }
        TextInputDialog dialog = new TextInputDialog(String.valueOf(current.payload().getOrDefault("sub", "")));
        dialog.setTitle("Save token");
        dialog.setHeaderText("Give this token a label");
        dialog.setContentText("Label:");
        dialog.showAndWait().ifPresent(label -> {
            ctx.repository.save(label, current.rawToken());
            rememberCurrent();
            Ui.setStatus(saveStatus, "Saved to workspace as \"" + (label.isBlank() ? "Untitled token" : label.trim()) + "\"", Ui.StatusKind.OK);
        });
    }
}
