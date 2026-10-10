package com.jwtoffline.ui;

import com.jwtoffline.core.ClaimInspector;
import com.jwtoffline.core.DecodedJwt;
import com.jwtoffline.core.JwtException;
import java.util.Map;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextInputDialog;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

/** Decode screen: token in, header / payload / time claims out. Decodes as you type. */
class DecodeView extends VBox {

    private final AppContext ctx;
    private final TextArea tokenInput = Ui.codeArea("Paste a JWT here (a leading \"Bearer \" is fine)", 4, true);
    private final TextArea headerOut = Ui.codeArea("Header", 8, false);
    private final TextArea payloadOut = Ui.codeArea("Payload", 8, false);
    private final Label status = Ui.status();
    private final Label algLabel = new Label();
    private final GridPane times = new GridPane();
    private DecodedJwt current;

    DecodeView(AppContext ctx) {
        this.ctx = ctx;
        setSpacing(12);
        setPadding(new Insets(28));

        tokenInput.textProperty().addListener((obs, old, text) -> decode(text));

        Button copyHeader = new Button("Copy header");
        copyHeader.setOnAction(e -> Ui.copy(headerOut.getText()));
        Button copyPayload = new Button("Copy payload");
        copyPayload.setOnAction(e -> Ui.copy(payloadOut.getText()));
        Button save = new Button("Save to workspace");
        save.setOnAction(e -> saveToWorkspace());
        Button verify = new Button("Verify signature");
        verify.getStyleClass().add("primary");
        verify.setOnAction(e -> ctx.navigator().openInVerifier(tokenInput.getText()));
        Button clear = new Button("Clear");
        clear.setOnAction(e -> tokenInput.clear());
        HBox actions = new HBox(8, verify, save, copyHeader, copyPayload, clear);

        VBox headerBox = new VBox(6, Ui.sectionLabel("Header"), headerOut, algLabel);
        VBox payloadBox = new VBox(6, Ui.sectionLabel("Payload"), payloadOut);
        HBox.setHgrow(headerBox, Priority.ALWAYS);
        HBox.setHgrow(payloadBox, Priority.ALWAYS);
        headerBox.setPrefWidth(380);
        payloadBox.setPrefWidth(480);
        HBox panes = new HBox(14, headerBox, payloadBox);
        VBox.setVgrow(panes, Priority.ALWAYS);
        VBox.setVgrow(headerOut, Priority.ALWAYS);
        VBox.setVgrow(payloadOut, Priority.ALWAYS);

        times.setHgap(18);
        times.setVgap(4);
        times.getStyleClass().add("times");
        algLabel.getStyleClass().add("caption");

        getChildren().addAll(Ui.title("Decode"), Ui.caption("Everything is decoded on this computer. The token is never sent anywhere."),
                tokenInput, status, panes, Ui.sectionLabel("Time claims"), times, actions);
        showEmpty();
    }

    void load(String token) {
        tokenInput.setText(token);
    }

    private void decode(String text) {
        if (text == null || text.isBlank()) {
            showEmpty();
            return;
        }
        try {
            current = ctx.decoder.decode(text);
        } catch (JwtException ex) {
            current = null;
            headerOut.clear();
            payloadOut.clear();
            times.getChildren().clear();
            algLabel.setText("");
            Ui.setStatus(status, ex.getMessage(), "bad");
            return;
        }
        headerOut.setText(current.prettyHeader());
        payloadOut.setText(current.prettyPayload());
        algLabel.setText("Algorithm: " + current.algorithmName() + "   Signature: "
                + (current.signatureSegment().isEmpty() ? "none" : current.signatureSegment().length() + " chars"));
        ClaimInspector.Status s = ctx.inspector.status(current.payload());
        String kind = s == ClaimInspector.Status.VALID ? "ok" : s == ClaimInspector.Status.NO_EXPIRY ? "warn" : "bad";
        Ui.setStatus(status, ctx.inspector.describe(current.payload()), kind);
        fillTimes(ctx.inspector.readableTimes(current.payload()));
        ctx.history.add(current.rawToken());
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
        switch (claim) {
            case "exp": return "Expires";
            case "iat": return "Issued at";
            default: return "Not before";
        }
    }

    private void showEmpty() {
        current = null;
        headerOut.clear();
        payloadOut.clear();
        algLabel.setText("");
        times.getChildren().clear();
        Ui.hideStatus(status);
    }

    private void saveToWorkspace() {
        if (current == null) {
            Ui.setStatus(status, "Decode a valid token before saving it", "warn");
            return;
        }
        TextInputDialog dialog = new TextInputDialog(String.valueOf(current.payload().getOrDefault("sub", "")));
        dialog.setTitle("Save token");
        dialog.setHeaderText("Give this token a label");
        dialog.setContentText("Label:");
        dialog.showAndWait().ifPresent(label -> {
            ctx.repository.save(label, current.rawToken());
            Ui.setStatus(status, "Saved to workspace as \"" + (label.isBlank() ? "Untitled token" : label.trim()) + "\"", "ok");
        });
    }
}
