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

import com.github.yiwenlong.fabric.network.SingleOrgNetwork;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import org.hyperledger.fabric.sdk.BlockchainInfo;
import org.hyperledger.fabric.sdk.Channel;
import org.hyperledger.fabric.sdk.HFClient;
import org.hyperledger.fabric.sdk.exception.InvalidArgumentException;
import org.hyperledger.fabric.sdk.exception.ProposalException;
import org.hyperledger.fabric.sdk.exception.TransactionException;
import org.hyperledger.fabric.sdk.security.CryptoSuite;
import org.junit.Assert;

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
            client.setUserContext(SingleOrgNetwork.Org1.Admin.get());
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail();
        }
    }

    public void createChannel() throws IOException, InvalidArgumentException {
        try {
            Channel channel = SingleOrgNetwork.MyChannel.create(client);
            Assert.assertEquals(channel.getName(), SingleOrgNetwork.MyChannel.name);
        } catch (TransactionException ex) {
            ex.printStackTrace();
            Assert.fail();
        }
    }

    public void joinChannelPeer0() throws InvalidArgumentException {
        Channel channel = client.newChannel(SingleOrgNetwork.MyChannel.name);
        channel.addOrderer(SingleOrgNetwork.Orderers.getOrderer0(client));
        try {
            channel.joinPeer(SingleOrgNetwork.Org1.getPeer0(client));
        } catch (ProposalException ex) {
            Assert.fail();
        }
    }

    public void joinChannelPeer1() throws InvalidArgumentException {
        Channel channel = client.newChannel(SingleOrgNetwork.MyChannel.name);
        channel.addOrderer(SingleOrgNetwork.Orderers.getOrderer0(client));
        try {
            channel.joinPeer(SingleOrgNetwork.Org1.getPeer1(client));
        } catch (ProposalException ex) {
            Assert.fail();
        }
    }

    public void viewChannelInformation() throws InvalidArgumentException {
        Channel channel = client.newChannel(SingleOrgNetwork.MyChannel.name);
        channel.addPeer(SingleOrgNetwork.Org1.getPeer0(client));
        channel.addOrderer(SingleOrgNetwork.Orderers.getOrderer0(client));
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
