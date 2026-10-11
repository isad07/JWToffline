package com.jwtoffline.core;

/** Outcome of a signature check, with a message the UI can show as is. */
public record VerificationResult(boolean isValid, String message) {

    public static VerificationResult valid(JwtAlgorithm alg) {
        return new VerificationResult(true, "Signature verified (" + alg.name() + ")");
    }

    public static VerificationResult invalid(String message) {
        return new VerificationResult(false, message);
    }

    @Override
    public String toString() {
        return (isValid ? "VALID: " : "INVALID: ") + message;
    }
}
