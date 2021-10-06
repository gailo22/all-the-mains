package com.gailo22.cryptofundamentals;

import static org.junit.Assert.assertEquals;

import org.bouncycastle.cert.X509CertificateHolder;
import org.junit.Test;

public class CertificateTest {
    
    @Test
    public void testGetPublicKeyFromCertificate() throws Exception {
        X509CertificateHolder certificate = CertificateHelpers.loadCertificate(CryptofundamentalsCertificate);

        assertEquals("CN=www.cryptofundamentals.com", certificate.getSubject().toString());
    }

    private static String CryptofundamentalsCertificate = String.join(System.getProperty("line.separator"),
        "-----BEGIN CERTIFICATE-----",
        "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",
        "-----END CERTIFICATE-----"
    );
}
