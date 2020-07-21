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

import com.github.yiwenlong.fabric.network.SingleOrgNetwork.*;
import org.hyperledger.fabric.sdk.BlockchainInfo;
import org.hyperledger.fabric.sdk.Channel;
import org.hyperledger.fabric.sdk.HFClient;
import org.hyperledger.fabric.sdk.exception.CryptoException;
import org.hyperledger.fabric.sdk.exception.InvalidArgumentException;
import org.hyperledger.fabric.sdk.security.CryptoSuite;
import org.junit.Before;
import org.junit.Test;

import java.lang.reflect.InvocationTargetException;
import java.security.Security;
import java.util.Base64;

public class ChannelTest {

    static {
        Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
    }

    private HFClient client;

    @Before
    public void initClient() throws IllegalAccessException, InvocationTargetException, InvalidArgumentException, InstantiationException, NoSuchMethodException, CryptoException, ClassNotFoundException {
        client = HFClient.createNewInstance();
        client.setCryptoSuite(CryptoSuite.Factory.getCryptoSuite());
        client.setUserContext(Org1.Admin.get());
    }

    @Test
    public void testCreateChannel() throws Exception {
        Channel channel = MyChannel.create(client);
//        channel.joinPeer(Org1.getPeer0Admin(client));
//        channel.joinPeer(Org1.getPeer1Admin(client));
        System.out.println(channel.getName());
    }

    @Test
    public void testJoinChannelPeer0() throws Exception {
        Channel channel = client.newChannel(MyChannel.name);
        channel.addOrderer(Orderers.getOrderer0(client));
        channel.joinPeer(Org1.getPeer0Admin(client));
    }

    @Test
    public void testJoinChannelPeer1() throws Exception {
        Channel channel = client.newChannel(MyChannel.name);
        channel.addOrderer(Orderers.getOrderer0(client));
        channel.joinPeer(Org1.getPeer1Admin(client));
    }

    @Test
    public void testViewChannel() throws Exception {
        Channel channel = client.newChannel(MyChannel.name);
        channel.addPeer(Org1.getPeer0Admin(client));
        channel.addOrderer(Orderers.getOrderer0(client));
        BlockchainInfo blockchainInfo = channel.initialize().queryBlockchainInfo();

        System.out.println("Height: " + blockchainInfo.getHeight());
        System.out.println("Current block hash: " + Base64.getEncoder().encodeToString(blockchainInfo.getCurrentBlockHash()));
        System.out.println("Previous block hash: " + Base64.getEncoder().encodeToString(blockchainInfo.getPreviousBlockHash()));
    }
}
