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
package com.github.yiwenlong.fabric.network;

import com.github.yiwenlong.fabric.Organization;

import java.util.HashMap;
import java.util.Map;

public enum NetworkOrganizationConfig implements Organization.Config {

    Orderer() {

        @Override
        public String mspId() {
            return "OrdererMSP";
        }

        @Override
        public String domain() {
            return "example.com";
        }

        @Override
        public String cryptoDirectory() {
            return ordererCryptoPeerPath();
        }

        @Override
        public Map<String, String> nodeUrls() {
            Map<String, String> nodes = new HashMap<>();
            nodes.put("orderer", nodeUrl("orderer", 7050));
            nodes.put("orderer1", nodeUrl("orderer1", 8050));
            nodes.put("orderer2", nodeUrl("orderer2", 9050));
            return nodes;
        }
    },

    Org1() {
        @Override
        public String mspId() {
            return "Org1MSP";
        }

        @Override
        public String domain() {
            return "org1.example.com";
        }

        @Override
        public String cryptoDirectory() {
            return peerCryptoPeerPath();
        }

        @Override
        public Map<String, String> nodeUrls() {
            Map<String, String> nodes = new HashMap<>();
            nodes.put("peer0", nodeUrl("peer0", 7051));
            nodes.put("peer1", nodeUrl("peer1", 8051));
            return nodes;
        }
    };

    protected String peerCryptoPeerPath() {
        return String.format("crypto-config/peerOrganizations/%s/", domain());
    }

    protected String ordererCryptoPeerPath() {
        return String.format("crypto-config/ordererOrganizations/%s/", domain());
    }

    protected String nodeUrl(String name, int port) {
        return String.format("grpcs://%s.%s:%d", name, domain(), port);
    }

}
