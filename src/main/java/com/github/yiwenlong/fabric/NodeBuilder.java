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
import org.hyperledger.fabric.sdk.exception.InvalidArgumentException;

import java.util.Properties;

import static com.github.yiwenlong.fabric.utils.PropertiesHelper.createTlsAccessProperties;

public class NodeBuilder {

    private String name, grpcUrl, tlsCaFile;


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

    public Orderer buildOrderer(HFClient client) throws InvalidArgumentException {
        Properties prop = createTlsAccessProperties(tlsCaFile);
        return client.newOrderer(name, grpcUrl, prop);
    }

    public Peer buildPeer(HFClient client) throws InvalidArgumentException {
        Properties prop = createTlsAccessProperties(tlsCaFile);
        return client.newPeer(name, grpcUrl, prop);
    }
}
