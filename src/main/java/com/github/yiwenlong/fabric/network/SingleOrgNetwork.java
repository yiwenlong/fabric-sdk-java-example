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
import org.hyperledger.fabric.sdk.HFClient;
import org.hyperledger.fabric.sdk.Orderer;
import org.hyperledger.fabric.sdk.Peer;
import org.hyperledger.fabric.sdk.User;
import org.hyperledger.fabric.sdk.exception.InvalidArgumentException;

import java.io.File;
import java.io.FileNotFoundException;

public class SingleOrgNetwork {

    private static final String BaseSampleDir = "network";

    public static class Org1 {

        private static final String MspId = "Org1MSP";
        private static final String Domain = "org1.example.fnodocker.icu";
        private static final String CryptoDir = BaseSampleDir + "/Org1/crypto-config/peerOrganizations/org1.example.fnodocker.icu";

        private static Organization organization;

        static {
            try {
                organization = new Organization(MspId, Domain, new File(CryptoDir)).init();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }

        public static class Peer0 {
            private static final String grpcUrl = "grpcs://peer0.org1.example.fnodocker.icu:7051";
            private static final String name = "peer0";

            public static Peer get(HFClient client) throws InvalidArgumentException {
                return organization.getPeer(client, name, grpcUrl);
            }
        }

        public static class Peer1 {
            private static final String grpcUrl = "grpcs://peer1.org1.example.fnodocker.icu:8051";
            private static final String name = "peer1";

            public static Peer get(HFClient client) throws InvalidArgumentException {
                return organization.getPeer(client, name, grpcUrl);
            }
        }

        public static User admin() {
            return organization.getUser("Admin");
        }

        public static User user1() {
            return organization.getUser("User1");
        }
    }

    public static class Orderers {

        private static final String CryptoDir = BaseSampleDir + "/Orderer/crypto-config/ordererOrganizations/example.fnodocker.icu";
        private static final String MspId = "OrdererMSP";
        private static final String Domain = "example.fnodocker.icu";

        private static Organization organization;

        static {
            try {
                organization = new Organization(MspId, Domain, new File(CryptoDir)).init();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }

        public static class Orderer0 {
            public static final String name = "orderer0";
            public static final String grpcUrl = "grpcs://orderer0.example.fnodocker.icu:7050";

            public static Orderer get(HFClient client) throws InvalidArgumentException {
                return organization.getOrderer(client, name, grpcUrl);
            }
        }

        public static User admin() {
            return organization.getUser("Admin");
        }
    }

    public static class MyChannel {
        public static final String configTx = BaseSampleDir + "/channel-mychannel/mychannel.tx";
        public static final String name = "mychannel";
    }

    public static class TPS {
        public static final String chaincodePackage = BaseSampleDir + "/chaincode-tps/tps.tar.gz";
    }
}
