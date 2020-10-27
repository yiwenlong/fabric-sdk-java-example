package com.github.yiwenlong.fabric;

import org.hyperledger.fabric.sdk.HFClient;
import org.hyperledger.fabric.sdk.TransactionProposalRequest;

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

    public TransactionProposalRequest toProposalRequest(HFClient client) {
        return (TransactionProposalRequest) client.newTransactionProposalRequest()
                .setChaincodeName(chaincodeName)
                .setFcn(funcName)
                .setArgs(args);
    }
}
