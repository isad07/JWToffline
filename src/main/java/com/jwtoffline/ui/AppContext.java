package com.jwtoffline.ui;

import com.jwtoffline.core.ClaimInspector;
import com.jwtoffline.core.JwtDecoder;
import com.jwtoffline.core.JwtEncoder;
import com.jwtoffline.core.SignatureVerifier;
import com.jwtoffline.workspace.InMemoryTokenRepository;
import com.jwtoffline.workspace.TokenHistory;
import com.jwtoffline.workspace.TokenRepository;

public final class AppContext {

    public final JwtDecoder decoder = new JwtDecoder();
    public final JwtEncoder encoder = new JwtEncoder();
    public final SignatureVerifier verifier = new SignatureVerifier();
    public final ClaimInspector inspector = new ClaimInspector();
    public final TokenRepository repository = InMemoryTokenRepository.withSampleData();
    public final TokenHistory history = new TokenHistory();

    private final Navigator navigator;

    public AppContext(Navigator navigator) {
        this.navigator = navigator;
    }

    public Navigator navigator() {
        return navigator;
    }

    /** Lets one screen jump to another, optionally handing over a token. */
    public interface Navigator {
        void show(Screen screen);

        void openInDecoder(String token);

        void openInVerifier(String token);
    }
}
