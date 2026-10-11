package com.jwtoffline.ui;

import com.jwtoffline.core.VerificationResult;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

class VerifyView extends VBox {

    private final AppContext ctx;
    private final TextArea tokenIn = Ui.codeArea("Paste the token to check", 5, true);
    private final TextArea keyIn = Ui.codeArea("HMAC secret, or PEM public key (PUBLIC KEY / RSA PUBLIC KEY)", 6, true);
    private final Label result = Ui.status();

    VerifyView(AppContext ctx) {
        this.ctx = ctx;
        Ui.setupScreen(this, 12);

        Button verify = Ui.button("Verify signature", this::verify, "primary");
        Button clear = Ui.button("Clear", () -> {
            tokenIn.clear();
            keyIn.clear();
            Ui.hideStatus(result);
        });
        tokenIn.textProperty().addListener((o, a, b) -> Ui.hideStatus(result));
        keyIn.textProperty().addListener((o, a, b) -> Ui.hideStatus(result));

        getChildren().addAll(Ui.title("Verify"),
                Ui.sectionLabel("Token"), tokenIn,
                Ui.sectionLabel("Secret or public key"), keyIn,
                new HBox(8, verify, clear), result);
    }

    void load(String token) {
        tokenIn.setText(token == null ? "" : token.trim());
    }

    private void verify() {
        VerificationResult r = ctx.verifier.verify(tokenIn.getText(), keyIn.getText());
        Ui.setStatus(result, (r.isValid() ? "✔  " : "✖  ") + r.message(), r.isValid() ? Ui.StatusKind.OK : Ui.StatusKind.BAD);
        if (r.isValid()) {
            ctx.history.add(tokenIn.getText());
        }
    }
}
