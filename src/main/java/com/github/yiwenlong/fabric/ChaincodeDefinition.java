package com.github.yiwenlong.fabric;

import com.sun.tools.javac.jvm.Code;
import org.hyperledger.fabric.sdk.HFClient;
import org.hyperledger.fabric.sdk.InstallProposalRequest;
import org.hyperledger.fabric.sdk.TransactionRequest;
import org.hyperledger.fabric.sdk.exception.InvalidArgumentException;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

public class ChaincodeDefinition {
    String chaincodeName;
    String version;
    File sourceFile;
    String path;
    final TransactionRequest.Type language = TransactionRequest.Type.GO_LANG;

    public ChaincodeDefinition chaincodeName(String name) {
        this.chaincodeName = name;
        return this;
    }

    public ChaincodeDefinition sourceFile(File sourceFile) {
        this.sourceFile = sourceFile;
        return this;
    }

    public ChaincodeDefinition version(String version) {
        this.version = version;
        return this;
    }

    public ChaincodeDefinition path(String path) {
        this.path = path;
        return this;
    }

    public InstallProposalRequest toProposalRequest(HFClient client) throws InvalidArgumentException, FileNotFoundException {
        InstallProposalRequest request = client.newInstallProposalRequest();
        request.setChaincodeName(chaincodeName);
        request.setChaincodeVersion(version);
        request.setChaincodeLanguage(language);
        request.setChaincodeInputStream(new FileInputStream(sourceFile));
        request.setChaincodePath(path);
        return request;
    }
}
