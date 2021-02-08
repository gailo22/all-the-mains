package com.gailo22.cryptofundamentals;

import java.io.StringReader;

import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.openssl.PEMParser;

public class CertificateHelpers {

	public static X509CertificateHolder loadCertificate(String pem) throws Exception {
		var reader = new StringReader(pem);
		var parser = new PEMParser(reader);
		var certificateHolder = (X509CertificateHolder)parser.readObject();
		return certificateHolder;
	}

}