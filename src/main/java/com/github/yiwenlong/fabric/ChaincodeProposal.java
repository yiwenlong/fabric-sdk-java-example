package com.github.yiwenlong.fabric;

import org.hyperledger.fabric.sdk.*;
import org.hyperledger.fabric.sdk.exception.InvalidArgumentException;
import org.hyperledger.fabric.sdk.exception.ProposalException;

import java.util.Arrays;
import java.util.Collection;
import java.util.stream.Collectors;

public class ChaincodeProposal {
    String chaincodeName;
    String funcName;
    String version;
    String[] args;

    public ChaincodeProposal chaincodeName(String name) {
        this.chaincodeName = name;
        return this;
    }

    public ChaincodeProposal funcName(String func) {
        this.funcName = func;
        return this;
    }

    public ChaincodeProposal version(String version) {
        this.version = version;
        return this;
    }

    public ChaincodeProposal args(String ... args) {
        this.args = args;
        return this;
    }

    protected TransactionProposalRequest request(HFClient client) {
        ChaincodeID ccid = ChaincodeID.newBuilder().setName(chaincodeName).setVersion(version).build();
        TransactionProposalRequest request =  (TransactionProposalRequest) client.newTransactionProposalRequest()
                                                    .setFcn(funcName)
                                                    .setArgs(args);
        request.setChaincodeID(ccid);
        return request;
    }

    public Collection<ProposalResponse> simulationExec(HFClient client, Channel channel, Peer ... peers) throws
            InvalidArgumentException,
            ProposalException {
        return channel.sendTransactionProposal(request(client), Arrays.asList(peers));
//                .stream()
//                .filter(ProposalResponse::isVerified)
//                .collect(Collectors.toList());
    }
}
