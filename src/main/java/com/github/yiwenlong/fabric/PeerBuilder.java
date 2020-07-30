package com.github.yiwenlong.fabric;

import org.hyperledger.fabric.sdk.HFClient;
import org.hyperledger.fabric.sdk.Peer;
import org.hyperledger.fabric.sdk.exception.InvalidArgumentException;

import java.util.Properties;

import static com.github.yiwenlong.fabric.utils.PropertiesHelper.createTlsAccessProperties;

public class PeerBuilder {

    private final HFClient client;
    private String name, grpcUrl, tlsCaFile;

    public PeerBuilder(HFClient fclient) {
        this.client = fclient;
    }

    public PeerBuilder name(String name) {
        this.name = name;
        return this;
    }

    public PeerBuilder grpcUrl(String grpcUrl) {
        this.grpcUrl = grpcUrl;
        return this;
    }

    public PeerBuilder tlsCaFile(String tlsCaFile) {
        this.tlsCaFile = tlsCaFile;
        return this;
    }

    public Peer build() throws InvalidArgumentException {
        Properties prop = createTlsAccessProperties(tlsCaFile);
        return client.newPeer(name, grpcUrl, prop);
    }

}
