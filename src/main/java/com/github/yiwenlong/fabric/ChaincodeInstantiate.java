package com.github.yiwenlong.fabric;

import org.hyperledger.fabric.protos.peer.FabricProposalResponse;
import org.hyperledger.fabric.sdk.*;
import org.hyperledger.fabric.sdk.exception.ChaincodeEndorsementPolicyParseException;
import org.hyperledger.fabric.sdk.exception.InvalidArgumentException;
import org.hyperledger.fabric.sdk.exception.ProposalException;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class ChaincodeInstantiate {

    private File yamlPolicy;
    private final List<String> argsList = new ArrayList<>();
    private String chaincodeName;
    private String version;

    public ChaincodeInstantiate addArg(String arg) {
        argsList.add(arg);
        return this;
    }

    public ChaincodeInstantiate yamlPolicy(File yamlPolicy) {
        this.yamlPolicy = yamlPolicy;
        return this;
    }

    public ChaincodeInstantiate chaincodeName(String chaincodeName) {
        this.chaincodeName = chaincodeName;
        return this;
    }

    public ChaincodeInstantiate version(String version) {
        this.version = version;
        return this;
    }

    protected String[] args() {
        return argsList.toArray(new String[]{});
    }

    protected InstantiateProposalRequest request(HFClient client) throws
            IOException,
            ChaincodeEndorsementPolicyParseException {
        ChaincodeEndorsementPolicy policy = new ChaincodeEndorsementPolicy();
        policy.fromYamlFile(yamlPolicy);
        InstantiateProposalRequest request = (InstantiateProposalRequest) client.newInstantiationProposalRequest()
                .setArgs(args())
                .setFcn("init")
                .setChaincodeVersion(version)
                .setChaincodeName(chaincodeName);
        request.setChaincodeEndorsementPolicy(policy);
        return request;
    }

    public Collection<FabricProposalResponse.ProposalResponse> instantiate(HFClient client, Channel channel, User user, Peer... peers) throws
            IOException,
            ChaincodeEndorsementPolicyParseException,
            ProposalException,
            InvalidArgumentException {
        client.setUserContext(user);
        return channel.sendInstantiationProposal(request(client), Arrays.asList(peers))
                .stream()
                .map(ProposalResponse::getProposalResponse)
                .collect(Collectors.toList());
    }
}