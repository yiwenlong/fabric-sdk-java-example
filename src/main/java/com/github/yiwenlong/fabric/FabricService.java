package com.github.yiwenlong.fabric;

import org.hyperledger.fabric.sdk.*;
import org.hyperledger.fabric.sdk.exception.InvalidArgumentException;
import org.hyperledger.fabric.sdk.exception.ProposalException;
import org.hyperledger.fabric.sdk.exception.TransactionException;
import org.hyperledger.fabric.sdk.security.CryptoSuite;

import java.io.File;
import java.io.IOException;
import java.security.Security;
import java.util.Arrays;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class FabricService {

    static {
        Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
    }

    private final HFClient client;

    private static final FabricService service = new FabricService();

    private FabricService() {
        client = HFClient.createNewInstance();
        try {
            client.setCryptoSuite(CryptoSuite.Factory.getCryptoSuite());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static FabricService service() {
        return service;
    }

    public Peer buildPeer(Organization organization, String peer, User user) throws InvalidArgumentException {
        client.setUserContext(user);
        return organization.peer(client, peer);
    }

    public Orderer buildOrderer(Organization organization, String orderer, User user) throws InvalidArgumentException {
        client.setUserContext(user);
        return organization.orderer(client, orderer);
    }

    public void installChaincode(String packageFile, Peer... peers) throws IOException, InvalidArgumentException, ProposalException {
        LifecycleInstallChaincodeRequest request = client.newLifecycleInstallChaincodeRequest();
        request.setLifecycleChaincodePackage(LifecycleChaincodePackage.fromFile(new File(packageFile)));
        Collection<LifecycleInstallChaincodeProposalResponse> responses =
                client.sendLifecycleInstallChaincodeRequest(request, Arrays.asList(peers));
        for (LifecycleInstallChaincodeProposalResponse response: responses) {
            System.out.println("status: " + response.getStatus().name());
            System.out.println("txid: " + response.getTransactionID());
            System.out.println("message" + response.getMessage());
            System.out.println("Package Id: " + response.getPackageId());
        }
    }

    public Channel setUpChannel(String channelName, Orderer orderer, Peer ... peers) throws InvalidArgumentException, TransactionException {
        Channel mychannel = client.newChannel(channelName);
        mychannel.addOrderer(orderer);
        Arrays.stream(peers).iterator().forEachRemaining( peer -> {
            try {
                mychannel.addPeer(peer);
            } catch (InvalidArgumentException e) {
                e.printStackTrace();
            }
        });
        return mychannel.initialize();
    }

    public void invokeTps(String channelName, ChaincodeProposal proposal, Orderer orderer, Peer ... peers)
            throws InvalidArgumentException, ProposalException, ExecutionException,
            InterruptedException, TransactionException {
        TransactionProposalRequest proposalRequest = client.newTransactionProposalRequest();
        proposalRequest.setArgs(proposal.args).setFcn(proposal.funcName);
        proposalRequest.setChaincodeName(proposal.chaincodeName);

        Channel mychannel = null;
        try {
            mychannel = setUpChannel(channelName, orderer, peers);
            Collection<ProposalResponse> rs = mychannel.sendTransactionProposal(proposalRequest, Arrays.asList(peers));
            rs.forEach(response -> System.out.printf("%s proposal result: %s\n", response.getPeer().toString(), response.isVerified()));

            CompletableFuture<BlockEvent.TransactionEvent> txFuture = mychannel.sendTransaction(rs);
            BlockEvent.TransactionEvent txEvent = txFuture.get();
            assert txEvent.isValid();
        } finally {
            assert mychannel != null;
            mychannel.shutdown(true);
        }
    }

    public void queryChaincode(String channelName, ChaincodeProposal proposal, Orderer orderer, Peer ... peers)
            throws TransactionException, InvalidArgumentException, ProposalException {
        TransactionProposalRequest proposalRequest = client.newTransactionProposalRequest();
        proposalRequest.setArgs(proposal.args).setFcn(proposal.funcName);
        proposalRequest.setChaincodeName(proposal.chaincodeName);

        Channel mychannel = null;
        try {
            mychannel = setUpChannel(channelName, orderer, peers);
            Collection<ProposalResponse> rs = mychannel.sendTransactionProposal(proposalRequest, Arrays.asList(peers));
            rs.forEach(response -> {
                try {
                    System.out.printf("%s proposal result: %s\n",
                            response.getPeer().toString(),
                            new String(response.getChaincodeActionResponsePayload()));
                } catch (InvalidArgumentException e) {
                    e.printStackTrace();
                }
            });
        } finally {
            assert mychannel != null;
            mychannel.shutdown(true);
        }
    }

    public void joinChannel(String channelName, Orderer orderer, Peer peer, User user) throws InvalidArgumentException, ProposalException {
        Channel channel = client.newChannel(channelName);
        channel.addOrderer(orderer);
        try {
            client.setUserContext(user);
            channel.joinPeer(peer);
        } finally {
            channel.shutdown(true);
        }
    }

    public void queryJoinedChannel(Peer peer, User user) throws InvalidArgumentException, ProposalException {
        client.setUserContext(user);
        System.out.println(peer.toString());
        client.queryChannels(peer).forEach(channel ->
                System.out.printf("\t%s\n", channel)
        );
    }

    public void createChannel(String channelName, String txfile, Orderer orderer, User admin)
            throws IOException, InvalidArgumentException, TransactionException {
        Channel channel = null;
        try {
            ChannelConfiguration config = new ChannelConfiguration(new File(txfile));
            byte[] signature = client.getChannelConfigurationSignature(config, admin);
            channel = client.newChannel(channelName, orderer, config, signature);
        } finally {
            assert channel != null;
            channel.shutdown(true);
        }
    }

    public void updateChannel(String channelName, String configTx, Orderer orderer, Peer peer, User admin) throws TransactionException, InvalidArgumentException, IOException {
        Channel channel = null;
        try {
            channel = setUpChannel(channelName, orderer, peer);
            UpdateChannelConfiguration config = new UpdateChannelConfiguration(new File(configTx));
            byte[] signature = client.getUpdateChannelConfigurationSignature(config, admin);
            channel.updateChannelConfiguration(config, signature);
        } finally {
            assert channel != null;
            channel.shutdown(true);
        }
    }

    public void approveChaincodeDefinition(String channelName, ChaincodeDefinition definition, Orderer orderer, Peer ... peers)
            throws TransactionException, InvalidArgumentException, ProposalException {
        Channel channel = setUpChannel(channelName, orderer, peers);

        LifecycleApproveChaincodeDefinitionForMyOrgRequest request = client.newLifecycleApproveChaincodeDefinitionForMyOrgRequest();
        request.setChaincodeName(definition.chaincodeName);
        request.setInitRequired(definition.init);
        request.setSequence(definition.sequence);
        request.setChaincodeVersion(definition.version);
        request.setPackageId(definition.packageId);

        try {
            Collection<LifecycleApproveChaincodeDefinitionForMyOrgProposalResponse> response =
                    channel.sendLifecycleApproveChaincodeDefinitionForMyOrgProposal(request, Arrays.asList(peers));
            CompletableFuture<BlockEvent.TransactionEvent> txFuture = channel.sendTransaction(response);
            BlockEvent.TransactionEvent txEvent;
            try {
                txEvent = txFuture.get();
                assert txEvent.isValid();
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        } finally {
            channel.shutdown(true);
        }
    }

    public void commitChaincodeDefinition(String channelName, ChaincodeDefinition definition, Orderer orderer, Peer ... peers)
            throws InvalidArgumentException, TransactionException, ExecutionException,
            InterruptedException, ProposalException {

        LifecycleCommitChaincodeDefinitionRequest request = client.newLifecycleCommitChaincodeDefinitionRequest();
        request.setChaincodeName(definition.chaincodeName);
        request.setSequence(definition.sequence);
        request.setChaincodeVersion(definition.version);
        request.setInitRequired(definition.init);


        Channel mychannel = null;
        try {
            mychannel = setUpChannel(channelName, orderer, peers);
            Collection<LifecycleCommitChaincodeDefinitionProposalResponse> rs =
                    mychannel.sendLifecycleCommitChaincodeDefinitionProposal(request, Arrays.asList(peers));
            rs.forEach(response -> {
                System.out.printf("%s proposal result:\n \t%s\n", response.getPeer().toString(), response.getMessage());
                assert response.isVerified();
            });

            CompletableFuture<BlockEvent.TransactionEvent> txFuture = mychannel.sendTransaction(rs);
            BlockEvent.TransactionEvent txEvent = txFuture.get();
            assert txEvent.isValid();
        } finally {
            assert mychannel != null;
            mychannel.shutdown(true);
        }
    }

    public void queryChaincodeApprove(String channelName, ChaincodeDefinition definition, Orderer orderer, Peer ... peers)
            throws TransactionException, InvalidArgumentException, ProposalException {
        Channel channel = setUpChannel(channelName, orderer, peers);

        LifecycleCheckCommitReadinessRequest request = client.newLifecycleSimulateCommitChaincodeDefinitionRequest();
        request.setChaincodeName(definition.chaincodeName);
        request.setInitRequired(definition.init);
        request.setSequence(definition.sequence);
        request.setChaincodeVersion(definition.version);
        try {
            Collection<LifecycleCheckCommitReadinessProposalResponse> responses =
                    channel.sendLifecycleCheckCommitReadinessRequest(request, Arrays.asList(peers));
            responses.forEach(response -> {
                try {
                    Map<String, Boolean> approvalsMap = response.getApprovalsMap();
                    approvalsMap.forEach((o, isProval) -> System.out.printf("Org: %s, %s\n", o, isProval));
                } catch (ProposalException e) {
                    e.printStackTrace();
                }
            });
        } finally {
            channel.shutdown(true);
        }
    }

    public void queryDefinedChaincode(String channel, Orderer orderer, Peer ... peers)
            throws TransactionException, InvalidArgumentException, ProposalException {
        Channel mychannel = setUpChannel(channel, orderer, peers);
        LifecycleQueryChaincodeDefinitionsRequest request = client.newLifecycleQueryChaincodeDefinitionsRequest();
        try {
            Collection<LifecycleQueryChaincodeDefinitionsProposalResponse> reponses =
                    mychannel.lifecycleQueryChaincodeDefinitions(request, Arrays.asList(peers));
            reponses.forEach(response -> {
                try {
                    response.getLifecycleQueryChaincodeDefinitionsResult().iterator().forEachRemaining(result ->
                            System.out.printf("name: %s, version: %s, sequence: %d, escc: %s\n",
                                    result.getName(), result.getVersion(), result.getSequence(), result.getEndorsementPlugin())
                    );
                } catch (ProposalException e) {
                    e.printStackTrace();
                }
            });
        } finally {
            mychannel.shutdown(true);
        }
    }

    public void queryInstalledChaincode(Peer ... peers) throws ProposalException, InvalidArgumentException {
        LifecycleQueryInstalledChaincodesRequest queryRequest = client.newLifecycleQueryInstalledChaincodesRequest();
        Collection<LifecycleQueryInstalledChaincodesProposalResponse> responses =
                client.sendLifecycleQueryInstalledChaincodes(queryRequest, Arrays.asList(peers));
        responses.forEach( response -> {
            try {
                Collection<LifecycleQueryInstalledChaincodesProposalResponse.LifecycleQueryInstalledChaincodesResult> ress =
                        response.getLifecycleQueryInstalledChaincodesResult();
                ress.forEach(res ->
                        System.out.printf("%s:\n\tlabel: %s, packageId: %s\n",response.getPeer().toString(), res.getLabel(), res.getPackageId())
                );
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }
}
