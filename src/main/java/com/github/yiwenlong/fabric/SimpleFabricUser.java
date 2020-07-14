package com.github.yiwenlong.fabric;

import org.hyperledger.fabric.sdk.Enrollment;
import org.hyperledger.fabric.sdk.User;

import java.io.IOException;
import java.io.Serializable;
import java.util.Set;

public class SimpleFabricUser implements User, Serializable {

    private final String name;
    private final String mspId;
    private final Enrollment enrollment;
    private SimpleFabricUser(String name, String mspId, Enrollment enrollment) {
        this.enrollment = enrollment;
        this.name = name;
        this.mspId = mspId;
    }

    public String getName() {
        return name;
    }

    public Set<String> getRoles() {
        return null;
    }

    public String getAccount() {
        return null;
    }

    public String getAffiliation() {
        return null;
    }

    public Enrollment getEnrollment() {
        return enrollment;
    }

    public String getMspId() {
        return mspId;
    }

    public static User createInstance(String userName, String orgMspId, String certFile, String keyFile) {
        try {
            Enrollment enrollment = SimpleEnrollment.createInstance(certFile, keyFile);
            return new SimpleFabricUser(userName, orgMspId, enrollment);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
