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

import com.github.yiwenlong.fabric.FabricService;
import com.github.yiwenlong.fabric.Organization;
import com.github.yiwenlong.fabric.network.NetworkOrganizationConfig;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import org.hyperledger.fabric.sdk.Orderer;
import org.hyperledger.fabric.sdk.Peer;
import org.hyperledger.fabric.sdk.User;
import org.hyperledger.fabric.sdk.exception.InvalidArgumentException;
import org.hyperledger.fabric.sdk.exception.ProposalException;
import org.hyperledger.fabric.sdk.exception.TransactionException;

import java.io.IOException;

public class ChannelTestCase extends TestCase {

    private final FabricService service = FabricService.service();

    private Organization org1, ordererOrg;
    private User org1Admin;

    private final String channelName = "";

    public ChannelTestCase(String name) {
        super(name);
        this.org1 = new Organization(NetworkOrganizationConfig.Org1);
        this.ordererOrg = new Organization(NetworkOrganizationConfig.Orderer);
        org1Admin = org1.user("Admin");
    }

    public void createChannel() throws IOException, InvalidArgumentException, TransactionException {
        String txFile = "";
        Orderer orderer = service.buildOrderer(ordererOrg, "orderer0", org1Admin);
        service.createChannel(channelName, txFile, orderer, org1Admin);
    }

    public void joinChannelPeer0() throws InvalidArgumentException, ProposalException {
        Orderer orderer = service.buildOrderer(ordererOrg, "orderer0", org1Admin);
        Peer peer0 = service.buildPeer(org1, "peer0", org1Admin);
        service.joinChannel(channelName, orderer, peer0, org1Admin);
    }

    public void joinChannelPeer1() throws InvalidArgumentException, ProposalException {
        Orderer orderer = service.buildOrderer(ordererOrg, "orderer0", org1Admin);
        Peer peer0 = service.buildPeer(org1, "peer1", org1Admin);
        service.joinChannel(channelName, orderer, peer0, org1Admin);
    }

    public static Test suite() {
        TestSuite suite = new TestSuite();
        suite.addTest(new ChannelTestCase("createChannel"));
        suite.addTest(new ChannelTestCase("joinChannelPeer0"));
        suite.addTest(new ChannelTestCase("joinChannelPeer1"));
        return suite;
    }
}
