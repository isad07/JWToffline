package com.jwtoffline.core;

/** Outcome of a signature check, with a message the UI can show as is. */
public final class VerificationResult {

    private final boolean valid;
    private final String message;

    private VerificationResult(boolean valid, String message) {
        this.valid = valid;
        this.message = message;
    }

    public static VerificationResult valid(JwtAlgorithm alg) {
        return new VerificationResult(true, "Signature verified (" + alg.name() + ")");
    }

    public static VerificationResult invalid(String message) {
        return new VerificationResult(false, message);
    }

    public boolean isValid() { return valid; }
    public String message() { return message; }

    @Override
    public String toString() {
        return (valid ? "VALID: " : "INVALID: ") + message;
    }
}
