package com.github.yiwenlong.fabric.test;

import com.github.yiwenlong.fabric.FabricService;
import com.github.yiwenlong.fabric.Organization;
import com.github.yiwenlong.fabric.network.NetworkOrganizationConfig;
import org.hyperledger.fabric.sdk.Peer;
import org.hyperledger.fabric.sdk.User;
import org.hyperledger.fabric.sdk.exception.InvalidArgumentException;
import org.hyperledger.fabric.sdk.exception.ProposalException;
import org.junit.Before;
import org.junit.Test;

import java.io.FileNotFoundException;

public class ChannelTest {

    private final FabricService service = FabricService.service();
    private Organization org1;
    private User org1Admin;

    @Before
    public void init() throws FileNotFoundException {
        org1 = new Organization(NetworkOrganizationConfig.Org1).init();
        org1Admin = org1.user("Admin");
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
