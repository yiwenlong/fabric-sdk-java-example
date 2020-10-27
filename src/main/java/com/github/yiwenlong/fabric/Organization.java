//
// Copyright 2020 Yiwenlong(wlong.yi#gmail.com)
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//    http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.
//
package com.github.yiwenlong.fabric;

import org.hyperledger.fabric.sdk.HFClient;
import org.hyperledger.fabric.sdk.Orderer;
import org.hyperledger.fabric.sdk.Peer;
import org.hyperledger.fabric.sdk.User;
import org.hyperledger.fabric.sdk.exception.InvalidArgumentException;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Stream;

public class Organization {

    private final Config config;
    private String tlsCaCertFilePath;
    private final Map<String, User> name2Users = new HashMap<>();
    private final Map<String, String> nodeUrls;

    public Organization(Config config) {
        this.config = config;
        this.nodeUrls = config.nodeUrls();
    }

    public String mspId() {
        return config.mspId();
    }

    public Organization init() throws FileNotFoundException {
        File tlsCaCertFile = new File(config.cryptoDirectory(), String.format("tlsca/tlsca.%s-cert.pem", config.domain()));
        if (!tlsCaCertFile.exists()) {
            throw new FileNotFoundException(tlsCaCertFile.getAbsolutePath());
        }
        this.tlsCaCertFilePath = tlsCaCertFile.getPath();
        initUsers();
        return this;
    }

    private void initUsers() throws FileNotFoundException {
        File usersDir = new File(config.cryptoDirectory(), "users");
        if (!usersDir.exists() || !usersDir.isDirectory()) {
            throw new FileNotFoundException(usersDir.getAbsolutePath());
        }
        Stream.of(Objects.requireNonNull(usersDir.listFiles())).forEach(file -> {
            if (!file.getName().contains(config.domain())) {
                return;
            }
            String name = file.getName().split("@" + config.domain())[0];
            File keyStore = new File(file, "/msp/keystore");
            if (!keyStore.exists() || !keyStore.isDirectory()) {
                return;
            }
            File keyFile = Objects.requireNonNull(keyStore.listFiles())[0];
            File certFile = new File(file, String.format("/msp/signcerts/%s@%s-cert.pem", name, config.domain()));
            if (!certFile.exists()) {
                return;
            }
            User user = SimpleFabricUser.createInstance(name, config.mspId(), certFile.getAbsolutePath(), keyFile.getAbsolutePath());
            if (user != null) {
                name2Users.put(name, user);
            }
        });
    }

    public User user(String name) {
        return name2Users.get(name);
    }

    public Peer peer(HFClient client, String name) throws InvalidArgumentException {
        String grpcurl = nodeUrls.get(name);
        return new NodeBuilder()
                .grpcUrl(grpcurl)
                .name(name)
                .tlsCaFile(tlsCaCertFilePath)
                .buildPeer(client);
    }

    public Orderer orderer(HFClient client, String name) throws InvalidArgumentException {
        String grpcurl = nodeUrls.get(name);
        return new NodeBuilder()
                .grpcUrl(grpcurl)
                .name(name)
                .tlsCaFile(tlsCaCertFilePath)
                .buildOrderer(client);
    }

    public interface Config {
        String mspId();
        String domain();
        String cryptoDirectory();
        Map<String, String> nodeUrls();
    }
}
