package com.github.yiwenlong.fabric;

public class ChaincodeProposal {
    String chaincodeName;
    String funcName;
    String[] args;

    public ChaincodeProposal chaincodeName(String name) {
        this.chaincodeName = name;
        return this;
    }

    public ChaincodeProposal funcName(String func) {
        this.funcName = func;
        return this;
    }

    public ChaincodeProposal args(String ... args) {
        this.args = args;
        return this;
    }
}
