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
import org.hyperledger.fabric.sdk.*;
import org.hyperledger.fabric.sdk.exception.InvalidArgumentException;
import org.hyperledger.fabric.sdk.exception.ProposalException;
import org.hyperledger.fabric.sdk.security.CryptoSuite;
import org.junit.Assert;

import java.io.File;
import java.io.IOException;
import java.security.Security;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class ChaincodeLifecycleTestCase extends TestCase {

    static {
        Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
    }

    private HFClient client;
    private Channel mychannel;
    private Peer peer0;

    public ChaincodeLifecycleTestCase(String name) {
        super(name);
        client = HFClient.createNewInstance();
        try {
            client.setCryptoSuite(CryptoSuite.Factory.getCryptoSuite());
            client.setUserContext(SingleOrgNetwork.Org1.Admin.get());
            mychannel = client.newChannel(SingleOrgNetwork.MyChannel.name);
            peer0 = SingleOrgNetwork.Org1.getPeer0Admin(client);
            mychannel.addPeer(peer0);
            mychannel.addOrderer(SingleOrgNetwork.Orderers.getOrderer0(client));
            BlockchainInfo blockchainInfo = mychannel.initialize().queryBlockchainInfo();

            System.out.println("Height: " + blockchainInfo.getHeight());
            System.out.println("Current block hash: " + Base64.getEncoder().encodeToString(blockchainInfo.getCurrentBlockHash()));
            System.out.println("Previous block hash: " + Base64.getEncoder().encodeToString(blockchainInfo.getPreviousBlockHash()));
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail();
        }
    }

    public void installChaincode() throws IOException, InvalidArgumentException {
        LifecycleInstallChaincodeRequest request = client.newLifecycleInstallChaincodeRequest();
        request.setLifecycleChaincodePackage(LifecycleChaincodePackage.fromFile(new File(SingleOrgNetwork.TPS.chaincodePackage)));
        Collection<LifecycleInstallChaincodeProposalResponse> responses;
        try {
            responses = client.sendLifecycleInstallChaincodeRequest(request, SingleOrgNetwork.Org1.getPeersAdmin(client));
            for (LifecycleInstallChaincodeProposalResponse response: responses) {
                System.out.println("status: " + response.getStatus().name());
                System.out.println("txid: " + response.getTransactionID());
                System.out.println("message" + response.getMessage());
                System.out.println("Package Id: " + response.getPackageId());
            }
        } catch (ProposalException e) {
            e.printStackTrace();
            Assert.fail();
        }
    }

    public void queryInstalledChaincode() throws InvalidArgumentException {
        LifecycleQueryInstalledChaincodesRequest queryRequest = client.newLifecycleQueryInstalledChaincodesRequest();
        Collection<LifecycleQueryInstalledChaincodesProposalResponse> responses;
        try {
            responses = client.sendLifecycleQueryInstalledChaincodes(queryRequest, SingleOrgNetwork.Org1.getPeersAdmin(client));
            responses.forEach( response -> {
                try {
                    Collection<LifecycleQueryInstalledChaincodesProposalResponse.LifecycleQueryInstalledChaincodesResult> ress =
                            response.getLifecycleQueryInstalledChaincodesResult();
                    ress.forEach( res -> {
                        System.out.println(res.getLabel());
                        System.out.println(res.getPackageId());
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                    Assert.fail();
                }
            });
        } catch (ProposalException e) {
            e.printStackTrace();
            Assert.fail();
        }
    }

    public void approveChaincode() throws InvalidArgumentException {
        LifecycleApproveChaincodeDefinitionForMyOrgRequest request = client.newLifecycleApproveChaincodeDefinitionForMyOrgRequest();
        request.setChaincodeName("tps");
        request.setInitRequired(false);
        request.setSequence(1);
        request.setChaincodeVersion("1");
        request.setPackageId("tps:818934162895283ba4fa6d04149aa40179768279d963d0c81f6b9feff20e4421");
        Collection<Peer> peers = new ArrayList<>();
        peers.add(peer0);
        Collection<LifecycleApproveChaincodeDefinitionForMyOrgProposalResponse>  response;
        try {
            response = mychannel.sendLifecycleApproveChaincodeDefinitionForMyOrgProposal(request, peers);
            CompletableFuture<BlockEvent.TransactionEvent> txFuture = mychannel.sendTransaction(response);
            BlockEvent.TransactionEvent txEvent;
            try {
                txEvent = txFuture.get();
                Assert.assertTrue(txEvent.isValid());
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
                Assert.fail();
            }
        } catch (ProposalException e) {
            e.printStackTrace();
            Assert.fail();
        }
    }

    public void queryApproveChaincode() throws InvalidArgumentException {
        Collection<Peer> peers = new ArrayList<>();
        peers.add(peer0);
        LifecycleCheckCommitReadinessRequest request = client.newLifecycleSimulateCommitChaincodeDefinitionRequest();
        request.setChaincodeName("tps");
        request.setInitRequired(false);
        request.setSequence(1);
        request.setChaincodeVersion("1");
        Collection<LifecycleCheckCommitReadinessProposalResponse> responses;
        try {
            responses = mychannel.sendLifecycleCheckCommitReadinessRequest(request, peers);
            responses.forEach(response -> {
                try {
                    Map<String, Boolean> approvalsMap = response.getApprovalsMap();
                    approvalsMap.forEach((org, isProval) -> System.out.printf("Org: %s, %s\n", org, isProval));
                } catch (ProposalException e) {
                    e.printStackTrace();
                    Assert.fail();
                }
            });
        } catch (ProposalException e) {
            e.printStackTrace();
            Assert.fail();
        }
    }

    public void commitChaincode() throws InvalidArgumentException {
        LifecycleCommitChaincodeDefinitionRequest request = client.newLifecycleCommitChaincodeDefinitionRequest();
        request.setChaincodeName("tps");
        request.setSequence(1);
        request.setChaincodeVersion("1");
        request.setInitRequired(false);
        Collection<Peer> peers = new ArrayList<>();
        peers.add(peer0);
        Collection<LifecycleCommitChaincodeDefinitionProposalResponse> responses;
        try {
            responses = mychannel.sendLifecycleCommitChaincodeDefinitionProposal(request, peers);
            responses.forEach(response -> System.out.println(response.getMessage()));
            CompletableFuture<BlockEvent.TransactionEvent> txFuture = mychannel.sendTransaction(responses);
            BlockEvent.TransactionEvent txEvent = txFuture.get();
            Assert.assertTrue(txEvent.isValid());
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail();
        }
    }

    public void queryCommittedChaincode() {
        LifecycleQueryChaincodeDefinitionsRequest request = client.newLifecycleQueryChaincodeDefinitionsRequest();
        Collection<Peer> peers = new ArrayList<>();
        peers.add(peer0);
        Collection<LifecycleQueryChaincodeDefinitionsProposalResponse> reponses;
        try {
            reponses = mychannel.lifecycleQueryChaincodeDefinitions(request, peers);
            reponses.forEach(response -> {
                try {
                    response.getLifecycleQueryChaincodeDefinitionsResult().iterator().forEachRemaining(result -> System.out.println(result.getName()));
                } catch (ProposalException e) {
                    e.printStackTrace();
                    Assert.fail();
                }
            });
        } catch (InvalidArgumentException | ProposalException e) {
            e.printStackTrace();
            Assert.fail();
        }
    }

    public static Test suite() {
        TestSuite suite = new TestSuite();
        suite.addTest(new ChaincodeLifecycleTestCase("installChaincode"));
        suite.addTest(new ChaincodeLifecycleTestCase("queryInstalledChaincode"));
        suite.addTest(new ChaincodeLifecycleTestCase("approveChaincode"));
        suite.addTest(new ChaincodeLifecycleTestCase("queryApproveChaincode"));
        suite.addTest(new ChaincodeLifecycleTestCase("commitChaincode"));
        suite.addTest(new ChaincodeLifecycleTestCase("queryCommittedChaincode"));
        return suite;
    }
}
