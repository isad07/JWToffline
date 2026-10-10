package com.jwtoffline.ui;

import com.jwtoffline.core.JwtAlgorithm;
import com.jwtoffline.core.JwtException;
import java.time.Instant;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

/** Encode screen: header + payload JSON, an algorithm and a key, out comes a signed token. */
class EncodeView extends VBox {

    private final AppContext ctx;
    private final TextArea headerIn = Ui.codeArea("{ \"kid\": \"optional\" }", 5, true);
    private final TextArea payloadIn = Ui.codeArea("{ \"sub\": \"1234\" }", 9, true);
    private final ComboBox<JwtAlgorithm> algorithm = new ComboBox<>();
    private final TextArea keyIn = Ui.codeArea("Secret for HS*, or PKCS#8 PEM private key for RS*", 3, true);
    private final TextArea output = Ui.codeArea("Your signed token appears here", 4, false);
    private final Label status = Ui.status();

    EncodeView(AppContext ctx) {
        this.ctx = ctx;
        setSpacing(12);
        setPadding(new Insets(28));

        long now = Instant.now().getEpochSecond();
        headerIn.setText("{\n  \"typ\": \"JWT\"\n}");
        payloadIn.setText("{\n  \"sub\": \"user-123\",\n  \"name\": \"Test User\",\n  \"role\": \"tester\",\n  \"iat\": " + now
                + ",\n  \"exp\": " + (now + 3600) + "\n}");

        algorithm.getItems().setAll(JwtAlgorithm.values());
        algorithm.setValue(JwtAlgorithm.HS256);
        algorithm.valueProperty().addListener((o, a, b) -> Ui.hideStatus(status));

        Button generate = new Button("Generate token");
        generate.getStyleClass().add("primary");
        generate.setOnAction(e -> generate());
        Button copy = new Button("Copy token");
        copy.setOnAction(e -> Ui.copy(output.getText()));
        Button openDecoder = new Button("Open in decoder");
        openDecoder.setOnAction(e -> {
            if (!output.getText().isBlank()) {
                ctx.navigator().openInDecoder(output.getText());
            }
        });

        VBox left = new VBox(6, Ui.sectionLabel("Header (alg is set for you)"), headerIn,
                Ui.sectionLabel("Payload"), payloadIn);
        VBox right = new VBox(6, Ui.sectionLabel("Algorithm"), algorithm,
                Ui.sectionLabel("Signing key"), keyIn, new HBox(8, generate, copy, openDecoder));
        HBox.setHgrow(left, Priority.ALWAYS);
        HBox.setHgrow(right, Priority.ALWAYS);
        left.setPrefWidth(460);
        right.setPrefWidth(400);
        VBox.setVgrow(payloadIn, Priority.ALWAYS);
        HBox body = new HBox(14, left, right);
        VBox.setVgrow(body, Priority.ALWAYS);

        getChildren().addAll(Ui.title("Encode"), Ui.caption("Build a test token. Signing happens locally, your key never leaves this window."),
                body, status, Ui.sectionLabel("Signed token"), output);
    }

    private void generate() {
        try {
            String token = ctx.encoder.encode(headerIn.getText(), payloadIn.getText(), algorithm.getValue(), keyIn.getText());
            output.setText(token);
            ctx.history.add(token);
            Ui.setStatus(status, "Token signed with " + algorithm.getValue().name(), "ok");
        } catch (JwtException ex) {
            output.clear();
            Ui.setStatus(status, ex.getMessage(), "bad");
        }
    }
}
