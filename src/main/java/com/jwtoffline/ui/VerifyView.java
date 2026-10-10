package com.jwtoffline.ui;

import com.jwtoffline.core.VerificationResult;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

/** Verify screen: token + secret or public key, shows a clear pass or fail banner. */
class VerifyView extends VBox {

    private final AppContext ctx;
    private final TextArea tokenIn = Ui.codeArea("Paste the token to check", 5, true);
    private final TextArea keyIn = Ui.codeArea("HMAC secret, or RSA public key (-----BEGIN PUBLIC KEY-----)", 6, true);
    private final Label result = Ui.status();

    VerifyView(AppContext ctx) {
        this.ctx = ctx;
        setSpacing(12);
        setPadding(new Insets(28));

        Button verify = new Button("Verify signature");
        verify.getStyleClass().add("primary");
        verify.setOnAction(e -> verify());
        Button clear = new Button("Clear");
        clear.setOnAction(e -> {
            tokenIn.clear();
            keyIn.clear();
            Ui.hideStatus(result);
        });
        tokenIn.textProperty().addListener((o, a, b) -> Ui.hideStatus(result));
        keyIn.textProperty().addListener((o, a, b) -> Ui.hideStatus(result));

        getChildren().addAll(Ui.title("Verify"),
                Ui.caption("Check that a token was signed with the key you expect and has not been edited. "
                        + "Sample tokens in the workspace use the secret \"jwtoffline-demo-secret\"."),
                Ui.sectionLabel("Token"), tokenIn,
                Ui.sectionLabel("Secret or public key"), keyIn,
                new HBox(8, verify, clear), result);
    }

    void load(String token) {
        tokenIn.setText(token == null ? "" : token.trim());
    }

    private void verify() {
        VerificationResult r = ctx.verifier.verify(tokenIn.getText(), keyIn.getText());
        Ui.setStatus(result, (r.isValid() ? "✔  " : "✖  ") + r.message(), r.isValid() ? "ok" : "bad");
        if (r.isValid()) {
            ctx.history.add(tokenIn.getText());
        }
    }
}
