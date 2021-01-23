package com.github.yiwenlong.fabric.test;

import com.github.yiwenlong.fabric.FabricService;
import com.github.yiwenlong.fabric.Organization;
import com.github.yiwenlong.fabric.network.NetworkOrganizationConfig;
import org.hyperledger.fabric.sdk.Orderer;
import org.hyperledger.fabric.sdk.Peer;
import org.hyperledger.fabric.sdk.User;
import org.hyperledger.fabric.sdk.exception.InvalidArgumentException;
import org.hyperledger.fabric.sdk.exception.ProposalException;
import org.hyperledger.fabric.sdk.exception.TransactionException;
import org.junit.Before;
import org.junit.Test;

import java.io.FileNotFoundException;
import java.io.IOException;

public class ChannelTest {

    private final FabricService service = FabricService.service();
    private Organization org1, ordererOrg;
    private User org1Admin, ordererAdmin;

    @Before
    public void init() throws FileNotFoundException {
        org1 = new Organization(NetworkOrganizationConfig.Org1).init();
        org1Admin = org1.user("Admin");
        ordererOrg = new Organization(NetworkOrganizationConfig.Orderer).init();
        ordererAdmin = ordererOrg.user("Admin");
    }

    @Test
    public void createChannel() throws InvalidArgumentException, IOException, TransactionException {
        Orderer orderer0 = service.buildOrderer(ordererOrg, "orderer", ordererAdmin);
        service.createChannel("mychannel", "/Users/yiwenlong/Code/fabric-samples-nodocker/samples/network-single-org/channel-mychannel/mychannel/mychannel.tx", orderer0, org1Admin);
    }

    @Test
    public void viewPeerJoinedChannelsTest() throws InvalidArgumentException, ProposalException {
        System.out.println("Show channels for peer0:");
        Peer peer0 = service.buildPeer(org1, "peer0", org1Admin);
        service.queryJoinedChannels(peer0, org1Admin).forEach(System.out::println);
        System.out.println("Show channels for peer1:");
        Peer peer1 = service.buildPeer(org1, "peer0", org1Admin);
        service.queryJoinedChannels(peer1, org1Admin).forEach(System.out::println);
    }
}
