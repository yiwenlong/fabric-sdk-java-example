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

import com.github.yiwenlong.fabric.SimpleFabricUser;
import org.hyperledger.fabric.sdk.*;
import org.hyperledger.fabric.sdk.exception.InvalidArgumentException;
import org.hyperledger.fabric.sdk.exception.TransactionException;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

public class SingleOrgNetwork {

    private static final String BaseSampleDir = "network";

    public static class Org1 {

        private static final String Org1MspId = "Org1MSP";
        private static final String CryptoDir = BaseSampleDir + "/Org1/crypto-config/peerOrganizations/org1.example.fnodocker.icu";
        static final String tlsCaCert = CryptoDir + "/tlsca/tlsca.org1.example.fnodocker.icu-cert.pem";

        public static class Peer0 {
            private static final String grpcUrl = "grpcs://peer0.org1.example.fnodocker.icu:7051";
            private static final String name = "peer0";

            public static Peer get(HFClient fabClient, String tlsClientCert, String tlsClientKey) throws InvalidArgumentException {
                Properties p = createProperties(tlsCaCert, tlsClientKey, tlsClientCert);
                return fabClient.newPeer(name, grpcUrl, p);
            }
        }

        public static class Peer1 {
            private static final String grpcUrl = "grpcs://peer1.org1.example.fnodocker.icu:8051";
            private static final String name = "peer1";

            public static Peer get(HFClient fabClient, String tlsClientCert, String tlsClientKey) throws InvalidArgumentException {
                Properties p = createProperties(tlsCaCert, tlsClientKey, tlsClientCert);
                return fabClient.newPeer(name, grpcUrl, p);
            }
        }

        public static class Admin {
            static final String name = "admin";
            static final String adminCryptoDir = CryptoDir + "/users/Admin@org1.example.fnodocker.icu";
            public static final String keyFile = adminCryptoDir + "/msp/keystore/priv_sk";
            static final String certFile = adminCryptoDir + "/msp/signcerts/Admin@org1.example.fnodocker.icu-cert.pem";

            static final String tlsClientCert = adminCryptoDir + "/tls/client.crt";
            static final String tlsClientKey = adminCryptoDir + "/tls/client.key";

            public static User get() {
                return getUser(name, Org1MspId, certFile, keyFile);
            }
        }

        public static class User1 {
            static final String name = "user1";
            static final String user1CryptoDir = CryptoDir + "/users/User1@org1.example.fnodocker.icu";
            static final String keyFile = user1CryptoDir + "/msp/keystore/priv_sk";
            static final String certFile = user1CryptoDir + "/msp/signcerts/User1@org1.example.fnodocker.icu-cert.pem";

            static final String tlsClientCert = user1CryptoDir + "/tls/client.crt";
            static final String tlsClientKey = user1CryptoDir + "/tls/client.key";

            public static User get() {
                return getUser(name, Org1MspId, certFile, keyFile);
            }
        }

        public static Peer getPeer0Admin(HFClient client) throws InvalidArgumentException {
            return Peer0.get(client, Admin.tlsClientCert, Admin.tlsClientKey);
        }

        public static Peer getPeer1Admin(HFClient client) throws InvalidArgumentException {
            return Peer1.get(client, Admin.tlsClientCert, Admin.tlsClientKey);
        }

        public static Peer getPeer0User1(HFClient client) throws InvalidArgumentException {
            return Peer0.get(client, User1.tlsClientCert, User1.tlsClientKey);
        }

        public static Peer getPeer1User1(HFClient client) throws InvalidArgumentException {
            return Peer1.get(client, User1.tlsClientCert, User1.tlsClientKey);
        }

        public static Collection<Peer> getPeersAdmin(HFClient client) throws InvalidArgumentException {
            Collection<Peer> peers = new ArrayList<>();
            peers.add(getPeer0Admin(client));
            peers.add(getPeer1Admin(client));
            return peers;
        }
    }

    public static class Orderers {

        private static final String CryptoDir = BaseSampleDir + "/Orderer/crypto-config/ordererOrganizations/example.fnodocker.icu";
        private static final String MspId = "OrdererMSP";
        static final String tlsCaCert = CryptoDir + "/tlsca/tlsca.example.fnodocker.icu-cert.pem";

        public static class Orderer0 {

            static final String name = "orderer0";
            static final String grpcUrl = "grpcs://orderer0.example.fnodocker.icu:7050";

            public static Orderer get(HFClient fabClient, String tlsClientCert, String tlsClientKey) throws InvalidArgumentException {
                Properties p = createProperties(tlsCaCert, tlsClientKey, tlsClientCert);
                return fabClient.newOrderer(name, grpcUrl, p);
            }
        }

        public static class Orderer1 {

            static final String name = "orderer1";
            static final String grpcUrl = "grpcs://orderer1.example.fnodocker.icu:8050";

            public static Orderer get(HFClient fabClient, String tlsClientCert, String tlsClientKey) throws InvalidArgumentException {
                Properties p = createProperties(tlsCaCert, tlsClientKey, tlsClientCert);
                return fabClient.newOrderer(name, grpcUrl, p);
            }
        }

        public static class Orderer2 {

            static final String name = "orderer2";
            static final String grpcUrl = "grpcs://orderer2.example.fnodocker.icu:9050";

            public static Orderer get(HFClient fabClient, String tlsClientCert, String tlsClientKey) throws InvalidArgumentException {
                Properties p = createProperties(tlsCaCert, tlsClientKey, tlsClientCert);
                return fabClient.newOrderer(name, grpcUrl, p);
            }
        }

        public static class Admin {
            static final String name = "admin";
            static final String adminCryptoDir = CryptoDir + "/users/Admin@example.fnodocker.icu";

            static final String tlsClientCert = adminCryptoDir + "/tls/client.crt";
            static final String tlsClientKey = adminCryptoDir + "/tls/client.key";

            static final String certFile = adminCryptoDir + "/msp/signcerts/Admin@example.fnodocker.icu-cert.pem";
            static final String keyFile = adminCryptoDir + "/msp/keystore/priv_sk";

            public static User get() {
                return getUser(name, MspId, certFile, keyFile);
            }
        }

        public static Orderer getOrderer0(HFClient client) throws InvalidArgumentException {
            return Orderer0.get(client, Admin.tlsClientCert, Admin.tlsClientKey);
        }

        public static Orderer getOrderer1(HFClient client) throws InvalidArgumentException {
            return Orderer1.get(client, Admin.tlsClientCert, Admin.tlsClientKey);
        }

        public static Orderer getOrderer2(HFClient client) throws InvalidArgumentException {
            return Orderer2.get(client, Admin.tlsClientCert, Admin.tlsClientKey);
        }
    }

    public static class MyChannel {

        static final String configTx = BaseSampleDir + "/channel-mychannel/mychannel.tx";
        public static final String name = "mychannel";

        public static ChannelConfiguration getChannelConfiguration() throws IOException, InvalidArgumentException {
            return new ChannelConfiguration(new File(configTx));
        }

        public static Channel create(HFClient client) throws IOException, InvalidArgumentException, TransactionException {
            ChannelConfiguration config = getChannelConfiguration();
            Orderer orderer = Orderers.getOrderer1(client);
            User org1Admin = Org1.Admin.get();
            byte[] signature = client.getChannelConfigurationSignature(config, org1Admin);
            return client.newChannel(name, orderer, config, signature);
        }
    }

    public static class TPS {
        public static final String chaincodePackage = BaseSampleDir + "/chaincode-tps/tps.tar.gz";
    }

    private static User getUser(String userName, String mspId, String certFile, String keyFile) {
        return SimpleFabricUser.createInstance(userName, mspId, certFile, keyFile);
    }

    public static Properties createProperties(String serverTlsCert, String clientTlsKey, String clientTlsCert) {
        Properties properties = new Properties();
        properties.setProperty("sslProvider", "openSSL");
        properties.setProperty("negotiationType", "TLS");
        properties.put("pemFile", serverTlsCert);
        properties.put("clientKeyFile", clientTlsKey);
        properties.put("clientCertFile",clientTlsCert);

        properties.put("grpc.NettyChannelBuilderOption.keepAliveTime", new Object[] {5L, TimeUnit.MINUTES});
        properties.put("grpc.NettyChannelBuilderOption.keepAliveTimeout", new Object[] {8L, TimeUnit.SECONDS});
        properties.put("grpc.NettyChannelBuilderOption.keepAliveWithoutCalls", new Object[] {true});
        return properties;
    }
}
