package com.github.yiwenlong.fabric;

import org.hyperledger.fabric.sdk.HFClient;
import org.hyperledger.fabric.sdk.Peer;
import org.hyperledger.fabric.sdk.exception.InvalidArgumentException;

import java.util.Properties;
import java.util.concurrent.TimeUnit;

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
        Properties prop = createProperties(tlsCaFile);
        return client.newPeer(name, grpcUrl, prop);
    }

    public static Properties createProperties(String serverTlsCert) {
        Properties properties = new Properties();
        properties.setProperty("sslProvider", "openSSL");
        properties.setProperty("negotiationType", "TLS");
        properties.put("pemFile", serverTlsCert);

        properties.put("grpc.NettyChannelBuilderOption.keepAliveTime", new Object[] {5L, TimeUnit.MINUTES});
        properties.put("grpc.NettyChannelBuilderOption.keepAliveTimeout", new Object[] {8L, TimeUnit.SECONDS});
        properties.put("grpc.NettyChannelBuilderOption.keepAliveWithoutCalls", new Object[] {true});
        return properties;
    }
}
