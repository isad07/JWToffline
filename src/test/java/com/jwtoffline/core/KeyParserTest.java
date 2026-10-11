package com.jwtoffline.core;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import org.bouncycastle.asn1.pkcs.PrivateKeyInfo;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.junit.jupiter.api.Test;

class KeyParserTest {

    @Test
    void readsPkcs1KeysAndUsesThemForRsaSigning() throws Exception {
        KeyPair pair = TestKeys.rsaPair();
        byte[] privateDer = PrivateKeyInfo.getInstance(pair.getPrivate().getEncoded())
                .parsePrivateKey().toASN1Primitive().getEncoded();
        byte[] publicDer = SubjectPublicKeyInfo.getInstance(pair.getPublic().getEncoded())
                .parsePublicKey().toASN1Primitive().getEncoded();
        String privatePem = TestKeys.toPem(privateDer, "RSA PRIVATE KEY");
        String publicPem = TestKeys.toPem(publicDer, "RSA PUBLIC KEY");

        assertArrayEquals(pair.getPrivate().getEncoded(), KeyParser.privateKey(privatePem).getEncoded());
        assertArrayEquals(pair.getPublic().getEncoded(), KeyParser.publicKey(publicPem).getEncoded());
        String token = new JwtEncoder().encode("{}", "{}", JwtAlgorithm.RS256, privatePem);
        assertTrue(new SignatureVerifier().verify(token, publicPem).isValid());
    }

    @Test
    void rejectsNonRsaKeys() throws Exception {
        KeyPair pair = KeyPairGenerator.getInstance("EC").generateKeyPair();
        assertThrows(JwtException.class,
                () -> KeyParser.privateKey(TestKeys.toPem(pair.getPrivate().getEncoded(), "PRIVATE KEY")));
        assertThrows(JwtException.class,
                () -> KeyParser.publicKey(TestKeys.toPem(pair.getPublic().getEncoded(), "PUBLIC KEY")));
    }

    @Test
    void reportsMissingAndMalformedKeys() {
        for (String pem : new String[]{null, "", "not a key",
                "-----BEGIN PUBLIC KEY-----\ninvalid\n-----END PUBLIC KEY-----"}) {
            assertThrows(JwtException.class, () -> KeyParser.publicKey(pem));
            assertThrows(JwtException.class, () -> KeyParser.privateKey(pem));
        }
    }

    @Test
    void rejectsPublicKeyWherePrivateKeyIsRequired() throws Exception {
        String publicPem = TestKeys.toPem(TestKeys.rsaPair().getPublic().getEncoded(), "PUBLIC KEY");
        assertThrows(JwtException.class, () -> KeyParser.privateKey(publicPem));
    }
}
