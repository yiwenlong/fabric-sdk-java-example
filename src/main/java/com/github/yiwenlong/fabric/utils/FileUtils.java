package com.github.yiwenlong.fabric.utils;

import com.sun.xml.internal.messaging.saaj.util.ByteOutputStream;
import org.bouncycastle.asn1.pkcs.PrivateKeyInfo;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.openssl.PEMParser;
import org.bouncycastle.openssl.jcajce.JcaPEMKeyConverter;

import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.security.PrivateKey;

public class FileUtils {

    public static String readCert(String certFile) throws IOException {
        try(InputStream reader = new FileInputStream(certFile)) {
            int len;
            byte[] buffer = new byte[1024];
            StringBuilder sb = new StringBuilder();
            while ((len = reader.read(buffer)) != -1) {
                sb.append(new String(buffer, 0, len));
            }
            return sb.toString();
        }
    }

    public static PrivateKey readPrivateKey(String keyFile) throws IOException {
        try (PEMParser pemParse = new PEMParser(new FileReader(keyFile))) {
            final PrivateKeyInfo pemPair = (PrivateKeyInfo) pemParse.readObject();
            return new JcaPEMKeyConverter()
                    .setProvider(BouncyCastleProvider.PROVIDER_NAME)
                    .getPrivateKey(pemPair);
        }
    }

    public static byte[] toByteArray(String file) throws IOException {
        try(InputStream input = new FileInputStream(file)) {
            int len;
            byte[] buffer = new byte[1024];
            ByteOutputStream baos = new ByteOutputStream();
            while ((len = input.read(buffer)) != -1) {
                baos.write(buffer, 0, len);
            }
            return baos.getBytes();
        }
    }
}
