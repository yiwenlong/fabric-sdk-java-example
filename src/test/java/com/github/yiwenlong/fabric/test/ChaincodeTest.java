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
import org.hyperledger.fabric.sdk.*;
import org.hyperledger.fabric.sdk.exception.InvalidArgumentException;
import org.hyperledger.fabric.sdk.exception.ProposalException;
import org.hyperledger.fabric.sdk.security.CryptoSuite;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.security.Security;
import java.util.Base64;
import java.util.Collection;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class ChaincodeTest {

    static {
        Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
    }

    private HFClient client;
    private Channel mychannel;
    private Peer peer0;

    private User user1 = Org1.Admin.get();

    @Before
    public void initClient() throws Exception {
        client = HFClient.createNewInstance();
        client.setCryptoSuite(CryptoSuite.Factory.getCryptoSuite());
        client.setUserContext(Org1.Admin.get());

        mychannel = client.newChannel(MyChannel.name);
        peer0 = Org1.getPeer0Admin(client);
        mychannel.addPeer(peer0);
        mychannel.addOrderer(Orderers.getOrderer0(client));
        BlockchainInfo blockchainInfo = mychannel.initialize().queryBlockchainInfo();

        System.out.println("Height: " + blockchainInfo.getHeight());
        System.out.println("Current block hash: " + Base64.getEncoder().encodeToString(blockchainInfo.getCurrentBlockHash()));
        System.out.println("Previous block hash: " + Base64.getEncoder().encodeToString(blockchainInfo.getPreviousBlockHash()));
    }

    @Test
    public void testInvoke() throws InvalidArgumentException, ProposalException, ExecutionException, InterruptedException {
        TransactionProposalRequest proposalRequest = (TransactionProposalRequest) TransactionProposalRequest.newInstance(user1)
                .setArgs("whoami2", "yiwenlong2")
                .setFcn("put");
        proposalRequest.setChaincodeName("tps");
        Collection<ProposalResponse>  responses = mychannel.sendTransactionProposal(proposalRequest);
        CompletableFuture<BlockEvent.TransactionEvent> transactionEventCompletableFuture =  mychannel.sendTransaction(responses);
        BlockEvent.TransactionEvent transactionEvent = transactionEventCompletableFuture.get();
        Assert.assertTrue(transactionEvent.isValid());
    }

    @Test
    public void testQueryChaincode() throws ProposalException, InvalidArgumentException {
        QueryByChaincodeRequest request = (QueryByChaincodeRequest) QueryByChaincodeRequest.newInstance(user1)
                .setFcn("get")
                .setArgs("whoami2");
        request.setChaincodeName("tps");
        Collection<ProposalResponse>  responses = mychannel.queryByChaincode(request);
        responses.forEach( response -> {
            try {
                System.out.println(new String(response.getChaincodeActionResponsePayload()));
            } catch (InvalidArgumentException e) {
                e.printStackTrace();
            }
        });
    }
}
