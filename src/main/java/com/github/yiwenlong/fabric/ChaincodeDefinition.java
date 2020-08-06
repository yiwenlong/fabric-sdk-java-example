package com.github.yiwenlong.fabric;

public class ChaincodeDefinition {
    String chaincodeName;
    String packageId;
    String version;
    int sequence;
    boolean init;

    public ChaincodeDefinition chaincodeName(String name) {
        this.chaincodeName = name;
        return this;
    }

    public ChaincodeDefinition packageId(String packageId) {
        this.packageId = packageId;
        return this;
    }

    public ChaincodeDefinition version(String version) {
        this.version = version;
        return this;
    }

    public ChaincodeDefinition sequence(int sequence) {
        this.sequence = sequence;
        return this;
    }

    public ChaincodeDefinition init(boolean init) {
        this.init = init;
        return this;
    }
}
