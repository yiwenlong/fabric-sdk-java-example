package com.github.yiwenlong.fabric;

import org.hyperledger.fabric.protos.peer.FabricProposalResponse;
import org.hyperledger.fabric.sdk.*;
import org.hyperledger.fabric.sdk.exception.InvalidArgumentException;
import org.hyperledger.fabric.sdk.exception.ProposalException;
import org.hyperledger.fabric.sdk.request.InstallProposalRequest;
import org.hyperledger.fabric.sdk.request.TransactionRequest;
import org.hyperledger.fabric.sdk.response.ProposalResponse;

import java.io.File;
import java.util.Arrays;
import java.util.Collection;
import java.util.stream.Collectors;

public class ChaincodeInstaller {
    private String chaincodeName;
    private String version;
    private File sourceLocation;
    private String path;
    private final TransactionRequest.Type language = TransactionRequest.Type.GO_LANG;

    public ChaincodeInstaller chaincodeName(String name) {
        this.chaincodeName = name;
        return this;
    }

    public ChaincodeInstaller sourceLocation(File sourceLocation) {
        this.sourceLocation = sourceLocation;
        return this;
    }

    public ChaincodeInstaller version(String version) {
        this.version = version;
        return this;
    }

    public ChaincodeInstaller path(String path) {
        this.path = path;
        return this;
    }

    private InstallProposalRequest request(HFClient client) throws InvalidArgumentException {
        InstallProposalRequest request = client.newInstallProposalRequest();
        request.setChaincodeName(chaincodeName)
                .setChaincodeVersion(version)
                .setChaincodePath(path)
                .setChaincodeLanguage(language);
        request.setChaincodeSourceLocation(sourceLocation);
        return request;
    }

    public Collection<FabricProposalResponse.ProposalResponse> install(HFClient client, User user, Peer ... peers) throws
            InvalidArgumentException,
            ProposalException {
        client.setUserContext(user);
        return client.sendInstallProposal(request(client), Arrays.asList(peers))
                .stream()
                .map(ProposalResponse::getProposalResponse)
                .collect(Collectors.toList());
    }
}
