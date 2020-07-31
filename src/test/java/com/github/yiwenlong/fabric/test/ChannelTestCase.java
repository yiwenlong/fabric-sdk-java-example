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
package com.github.yiwenlong.fabric.test;

import com.github.yiwenlong.fabric.network.SingleOrgNetwork.MyChannel;
import com.github.yiwenlong.fabric.network.SingleOrgNetwork.Orderers;
import com.github.yiwenlong.fabric.network.SingleOrgNetwork.Org1;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import org.hyperledger.fabric.sdk.*;
import org.hyperledger.fabric.sdk.exception.InvalidArgumentException;
import org.hyperledger.fabric.sdk.exception.ProposalException;
import org.hyperledger.fabric.sdk.exception.TransactionException;
import org.hyperledger.fabric.sdk.security.CryptoSuite;
import org.junit.Assert;

import java.io.File;
import java.io.IOException;
import java.security.Security;

public class ChannelTestCase extends TestCase {

    static {
        Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
    }

    private HFClient client;

    public ChannelTestCase(String name) {
        super(name);
        client = HFClient.createNewInstance();
        try {
            client.setCryptoSuite(CryptoSuite.Factory.getCryptoSuite());
            client.setUserContext(Org1.admin());
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail();
        }
    }

    public void createChannel() throws IOException, InvalidArgumentException {
        try {
            ChannelConfiguration config = new ChannelConfiguration(new File(MyChannel.configTx));
            Orderer orderer0 = Orderers.Orderer0.get(client);
            User admin = Org1.admin();
            byte[] signature = client.getChannelConfigurationSignature(config, admin);
            Channel channel = client.newChannel(MyChannel.name, orderer0, config, signature);
            Assert.assertEquals(channel.getName(), MyChannel.name);
        } catch (TransactionException ex) {
            ex.printStackTrace();
            Assert.fail();
        }
    }

    public void joinChannelPeer0() throws InvalidArgumentException {
        Channel channel = client.newChannel(MyChannel.name);
        channel.addOrderer(Orderers.Orderer0.get(client));
        try {
            channel.joinPeer(Org1.Peer0.get(client));
        } catch (ProposalException ex) {
            Assert.fail();
        }
    }

    public void joinChannelPeer1() throws InvalidArgumentException {
        Channel channel = client.newChannel(MyChannel.name);
        channel.addOrderer(Orderers.Orderer0.get(client));
        try {
            channel.joinPeer(Org1.Peer1.get(client));
        } catch (ProposalException ex) {
            Assert.fail();
        }
    }

    public void viewChannelInformation() throws InvalidArgumentException {
        Channel channel = client.newChannel(MyChannel.name);
        channel.addPeer(Org1.Peer1.get(client));
        channel.addOrderer(Orderers.Orderer0.get(client));
        try {
            BlockchainInfo blockchainInfo = channel.initialize().queryBlockchainInfo();
            Assert.assertNotNull(blockchainInfo);
        } catch (TransactionException | ProposalException ex) {
            ex.printStackTrace();
            Assert.fail();
        }
    }

    public static Test suite() {
        TestSuite suite = new TestSuite();
        suite.addTest(new ChannelTestCase("createChannel"));
        suite.addTest(new ChannelTestCase("joinChannelPeer0"));
        suite.addTest(new ChannelTestCase("joinChannelPeer1"));
        suite.addTest(new ChannelTestCase("viewChannelInformation"));
        return suite;
    }
}
