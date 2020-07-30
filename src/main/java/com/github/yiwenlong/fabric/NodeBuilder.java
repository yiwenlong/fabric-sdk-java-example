package com.github.yiwenlong.fabric;

import org.hyperledger.fabric.sdk.HFClient;
import org.hyperledger.fabric.sdk.Orderer;
import org.hyperledger.fabric.sdk.Peer;
import org.hyperledger.fabric.sdk.exception.InvalidArgumentException;

import java.util.Properties;

import static com.github.yiwenlong.fabric.utils.PropertiesHelper.createTlsAccessProperties;

public class NodeBuilder {

    private final HFClient client;
    private String name, grpcUrl, tlsCaFile;

    public NodeBuilder(HFClient fclient) {
        this.client = fclient;
    }

    public NodeBuilder name(String name) {
        this.name = name;
        return this;
    }

    public NodeBuilder grpcUrl(String grpcUrl) {
        this.grpcUrl = grpcUrl;
        return this;
    }

    public NodeBuilder tlsCaFile(String tlsCaFile) {
        this.tlsCaFile = tlsCaFile;
        return this;
    }

    public Orderer buildOrderer() throws InvalidArgumentException {
        Properties prop = createTlsAccessProperties(tlsCaFile);
        return client.newOrderer(name, grpcUrl, prop);
    }

    public Peer buildPeer() throws InvalidArgumentException {
        Properties prop = createTlsAccessProperties(tlsCaFile);
        return client.newPeer(name, grpcUrl, prop);
    }
}
