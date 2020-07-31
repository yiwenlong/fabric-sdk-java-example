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

public class Organization {

    private final String mspId;
    private final String domain;
    private final File cryptoConfigDir;

    private String tlsCaCertFilePath;
    private Map<String, User> name2Users = new HashMap<>();

    public Organization(String mspId, String domain, File cryptoConfigDir) {
        this.mspId = mspId;
        this.domain = domain;
        this.cryptoConfigDir = cryptoConfigDir;
    }

    public Organization init() throws FileNotFoundException {
        File tlsCaCertFile = new File(cryptoConfigDir, String.format("tlsca/tlsca.%s-cert.pem", domain));
        if (!tlsCaCertFile.exists()) {
            throw new FileNotFoundException(tlsCaCertFile.getAbsolutePath());
        }
        this.tlsCaCertFilePath = tlsCaCertFile.getPath();
        initUsers();
        return this;
    }

    private void initUsers() throws FileNotFoundException {
        File usersDir = new File(cryptoConfigDir, "users");
        if (!usersDir.exists() || !usersDir.isDirectory()) {
            throw new FileNotFoundException(usersDir.getAbsolutePath());
        }
        for (File f: Objects.requireNonNull(usersDir.listFiles())) {
            if (!f.getName().contains(domain)) {
                continue;
            }
            String name = f.getName().split("@" + domain)[0];
            File keyFile = new File(f, "/msp/keystore/priv_sk");
            if (!keyFile.exists()) {
                throw new FileNotFoundException(keyFile.getAbsolutePath());
            }
            File certFile = new File(f, String.format("/msp/signcerts/%s@%s-cert.pem", name, domain));
            if (!certFile.exists()) {
                throw new FileNotFoundException(certFile.getAbsolutePath());
            }
            User user = SimpleFabricUser.createInstance(name, mspId, certFile.getAbsolutePath(), keyFile.getAbsolutePath());
            if (user != null) {
                name2Users.put(name, user);
            }
        }
    }

    public User getUser(String name) {
        return name2Users.get(name);
    }

    public Peer getPeer(HFClient client, String name, String grpcUrl) throws InvalidArgumentException {
        return new NodeBuilder(client)
                .grpcUrl(grpcUrl)
                .name(name)
                .tlsCaFile(tlsCaCertFilePath)
                .buildPeer();
    }

    public Orderer getOrderer(HFClient client, String name, String grpcUrl) throws InvalidArgumentException {
        return new NodeBuilder(client)
                .grpcUrl(grpcUrl)
                .name(name)
                .tlsCaFile(tlsCaCertFilePath)
                .buildOrderer();
    }
}
