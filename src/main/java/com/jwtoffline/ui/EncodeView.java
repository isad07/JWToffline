package com.jwtoffline.ui;

import com.jwtoffline.core.JwtAlgorithm;
import com.jwtoffline.core.JwtException;
import java.time.Instant;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

class EncodeView extends VBox {

    private final AppContext ctx;
    private final TextArea headerIn = Ui.codeArea("{ \"kid\": \"optional\" }", 5, true);
    private final TextArea payloadIn = Ui.codeArea("{ \"sub\": \"1234\" }", 9, true);
    private final ComboBox<JwtAlgorithm> algorithm = new ComboBox<>();
    private final TextArea keyIn = Ui.codeArea("Secret for HS*, or PKCS#1 / PKCS#8 PEM private key for RS*", 3, true);
    private final TextArea output = Ui.codeArea("Your signed token appears here", 4, false);
    private final Label status = Ui.status();

    EncodeView(AppContext ctx) {
        this.ctx = ctx;
        Ui.setupScreen(this, 12);

        long now = Instant.now().getEpochSecond();
        headerIn.setText("""
                {
                  "typ": "JWT"
                }""");
        payloadIn.setText("""
                {
                  "sub": "user-123",
                  "name": "Test User",
                  "role": "tester",
                  "iat": %d,
                  "exp": %d
                }""".formatted(now, now + 3600));

        algorithm.getItems().setAll(JwtAlgorithm.values());
        algorithm.setValue(JwtAlgorithm.HS256);
        algorithm.valueProperty().addListener((o, a, b) -> clearResult());
        for (TextArea input : new TextArea[]{headerIn, payloadIn, keyIn}) {
            input.textProperty().addListener((o, a, b) -> clearResult());
        }

        Button generate = Ui.button("Generate token", this::generate, "primary");
        Button copy = Ui.button("Copy token", () -> Ui.copy(output.getText()));
        copy.disableProperty().bind(output.textProperty().isEmpty());
        Button openDecoder = Ui.button("Open in decoder",
                () -> ctx.navigator().openInDecoder(output.getText()));
        openDecoder.disableProperty().bind(output.textProperty().isEmpty());

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

        getChildren().addAll(Ui.title("Encode"),
                body, status, Ui.sectionLabel("Signed token"), output);
    }

    private void clearResult() {
        output.clear();
        Ui.hideStatus(status);
    }

    private void generate() {
        try {
            String token = ctx.encoder.encode(headerIn.getText(), payloadIn.getText(), algorithm.getValue(), keyIn.getText());
            output.setText(token);
            ctx.history.add(token);
            Ui.setStatus(status, "Token signed with " + algorithm.getValue().name(), Ui.StatusKind.OK);
        } catch (JwtException ex) {
            output.clear();
            Ui.setStatus(status, ex.getMessage(), Ui.StatusKind.BAD);
        }
    }
}
