package com.github.yiwenlong.fabric;

import org.hyperledger.fabric.protos.peer.FabricProposalResponse;
import org.hyperledger.fabric.protos.peer.Query;
import org.hyperledger.fabric.sdk.*;
import org.hyperledger.fabric.sdk.exception.ChaincodeEndorsementPolicyParseException;
import org.hyperledger.fabric.sdk.exception.InvalidArgumentException;
import org.hyperledger.fabric.sdk.exception.ProposalException;
import org.hyperledger.fabric.sdk.exception.TransactionException;
import org.hyperledger.fabric.sdk.security.CryptoSuite;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.Security;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

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

    public Channel setUpChannel(String channelName, Orderer orderer, Peer ... peers) throws InvalidArgumentException, TransactionException {
        Channel mChannel = client.newChannel(channelName);
        if (orderer != null)
            mChannel.addOrderer(orderer);
        Arrays.stream(peers).iterator().forEachRemaining( peer -> {
            try {
                mChannel.addPeer(peer);
            } catch (InvalidArgumentException e) {
                e.printStackTrace();
            }
        });
        return mChannel.initialize();
    }

    public List<Query.ChaincodeInfo> queryInstalledChaincodes(Peer peer, User user) throws InvalidArgumentException, ProposalException {
        client.setUserContext(user);
        return client.queryInstalledChaincodes(peer);
    }

    public List<Query.ChaincodeInfo> queryInstantiatedChaincodes(Peer peer, String channel, User user) throws InvalidArgumentException, ProposalException, TransactionException {
        client.setUserContext(user);
        Channel ch = client.getChannel(channel);
        if (ch == null) {
            ch = client.newChannel(channel)
                    .addPeer(peer)
                    .initialize();
        } else if (!ch.getPeers().contains(peer)) {
            ch.addPeer(peer);
        }
        return ch.queryInstantiatedChaincodes(peer);
    }

    public Collection<FabricProposalResponse.ProposalResponse> installChaincode(ChaincodeInstaller ccInstaller, User user, Peer ... peers) throws
            InvalidArgumentException,
            ProposalException {
        return ccInstaller.install(client, user, peers);
    }

    public Collection<FabricProposalResponse.ProposalResponse> instantiateChaincode(ChaincodeInstantiate instantiate, String channel, User user, Orderer orderer, Peer ... peers) throws
            IOException,
            ChaincodeEndorsementPolicyParseException,
            ProposalException,
            TransactionException,
            InvalidArgumentException {
        Channel mChannel = null;
        try {
            mChannel = setUpChannel(channel, orderer, peers);
            return instantiate.instantiate(client, mChannel, user, peers);
        } finally {
            if (mChannel != null && !mChannel.isShutdown())
                mChannel.shutdown(true);
        }
    }

    public BlockEvent.TransactionEvent invokeChaincode(String channelName, ChaincodeProposal proposal, Orderer orderer, Peer ... peers) throws
            InvalidArgumentException,
            ProposalException,
            ExecutionException,
            InterruptedException,
            TransactionException {

        Channel mChannel = null;
        try {
            mChannel = setUpChannel(channelName, orderer, peers);
            Collection<ProposalResponse> simulationRs = simulationExec(mChannel, proposal, peers);
            if (simulationRs.size() == 0) {
                throw new ProposalException("Simulation res list is empty.");
            }
            return mChannel.sendTransaction(simulationRs).get();
        } finally {
            if (mChannel != null && !mChannel.isShutdown())
                mChannel.shutdown(true);
        }
    }

    public ProposalResponse queryChaincode(String channelName, ChaincodeProposal proposal, Peer ... peers)
            throws TransactionException, InvalidArgumentException, ProposalException {

        Channel mChannel = null;
        try {
            mChannel = setUpChannel(channelName, null, peers);
            Collection<ProposalResponse> simulationRs = simulationExec(mChannel, proposal, peers);
            if (simulationRs.size() == 0) {
                throw new ProposalException("Simulation res list is empty.");
            }
            return simulationRs.iterator().next();
        } finally {
            assert mChannel != null;
            mChannel.shutdown(true);
        }
    }

    protected Collection<ProposalResponse> simulationExec(Channel channel, ChaincodeProposal proposal, Peer ... peers) throws
            InvalidArgumentException,
            ProposalException {
        TransactionProposalRequest request = proposal.toProposalRequest(client);
        Collection<ProposalResponse> rs = channel.sendTransactionProposal(request, Arrays.asList(peers));
        return rs.stream().filter(ProposalResponse::isVerified)
                .collect(Collectors.toList());
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

    public Set<String> queryJoinedChannels(Peer peer, User user) throws InvalidArgumentException, ProposalException {
        client.setUserContext(user);
        return client.queryChannels(peer);
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

}
