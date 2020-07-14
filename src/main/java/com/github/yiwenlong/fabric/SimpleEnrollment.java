package com.github.yiwenlong.fabric;

import com.github.yiwenlong.fabric.utils.FileUtils;
import org.hyperledger.fabric.sdk.Enrollment;

import java.io.IOException;
import java.io.Serializable;
import java.security.PrivateKey;

public class SimpleEnrollment implements Enrollment, Serializable {

    private final PrivateKey privKey;
    private final String cert;

    private SimpleEnrollment(String cert, PrivateKey key) {
        this.privKey = key;
        this.cert = cert;
    }

    public PrivateKey getKey() {
        return privKey;
    }

    public String getCert() {
        return cert;
    }

    public static Enrollment createInstance(String certFile, String keyFile) throws IOException {
        return new SimpleEnrollment(FileUtils.readCert(certFile), FileUtils.readPrivateKey(keyFile));
    }
}
