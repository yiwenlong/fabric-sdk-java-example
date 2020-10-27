package com.github.yiwenlong.fabric.test;

import com.github.yiwenlong.fabric.ChaincodeInstaller;
import com.github.yiwenlong.fabric.ChaincodeInstantiate;
import com.github.yiwenlong.fabric.FabricService;
import com.github.yiwenlong.fabric.Organization;
import com.github.yiwenlong.fabric.network.NetworkOrganizationConfig;
import org.hyperledger.fabric.sdk.Orderer;
import org.hyperledger.fabric.sdk.Peer;
import org.hyperledger.fabric.sdk.User;
import org.hyperledger.fabric.sdk.exception.ChaincodeEndorsementPolicyParseException;
import org.hyperledger.fabric.sdk.exception.InvalidArgumentException;
import org.hyperledger.fabric.sdk.exception.ProposalException;
import org.hyperledger.fabric.sdk.exception.TransactionException;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

public class ChaincodeLifecycleTest {

    private final FabricService service = FabricService.service();

    private Organization org1, ordererOrg;
    private User org1Admin, ordererAdmin;

    @Before
    public void init() throws FileNotFoundException {
        org1 = new Organization(NetworkOrganizationConfig.Org1).init();
        ordererOrg = new Organization(NetworkOrganizationConfig.Orderer).init();
        org1Admin = org1.user("Admin");
        ordererAdmin = ordererOrg.user("Admin");
    }

    @Test
    public void viewPeerInstalledChaincodesTest() throws InvalidArgumentException, ProposalException {
        System.out.println("Show installed chaincode for peer0:");
        Peer peer0 = service.buildPeer(org1, "peer0", org1Admin);
        service.queryInstalledChaincodes(peer0, org1Admin).forEach(chaincodeInfo -> {
            System.out.printf("\tName: %s, Version: %s, isInitialized: %s\n",
                    chaincodeInfo.getName(),
                    chaincodeInfo.getVersion(),
                    chaincodeInfo.isInitialized());
        });

        System.out.println("\nShow installed chaincode for peer1:");
        Peer peer1 = service.buildPeer(org1, "peer1", org1Admin);
        service.queryInstalledChaincodes(peer1, org1Admin).forEach(chaincodeInfo -> {
            System.out.printf("\tName: %s, Version: %s, isInitialized: %s\n",
                    chaincodeInfo.getName(),
                    chaincodeInfo.getVersion(),
                    chaincodeInfo.isInitialized());
        });
    }

    @Test
    public void viewPeerInstantiatedChaincodesTest() throws InvalidArgumentException, ProposalException, TransactionException {
        final String mychannel = "mychannel";
        System.out.println("Show instantiated chaincode for mychannel of peer0:");
        Peer peer0 = service.buildPeer(org1, "peer0", org1Admin);
        service.queryInstantiatedChaincodes(peer0, mychannel, org1Admin).forEach(chaincodeInfo ->
                System.out.printf("\tName: %s, Version: %s, isInitialized: %s\n",
                    chaincodeInfo.getName(),
                    chaincodeInfo.getVersion(),
                    chaincodeInfo.isInitialized()
                )
        );

        System.out.println("\nShow instantiated chaincode for mychannel of peer1:");
        Peer peer1 = service.buildPeer(org1, "peer1", org1Admin);
        service.queryInstantiatedChaincodes(peer1, mychannel, org1Admin).forEach(chaincodeInfo ->
                System.out.printf("\tName: %s, Version: %s, isInitialized: %s\n",
                    chaincodeInfo.getName(),
                    chaincodeInfo.getVersion(),
                    chaincodeInfo.isInitialized()
                )
        );
    }

    @Test
    public void installChaincodeTest() throws InvalidArgumentException, ProposalException {
        ChaincodeInstaller ccDefine = new ChaincodeInstaller()
                .chaincodeName("test_cc")
                .version("1.3")
                .path("test")
                .sourceLocation(new File("chaincodes/test"));
        Peer peer0 = service.buildPeer(org1, "peer0", org1Admin);
        service.installChaincode(ccDefine, org1Admin, peer0).forEach(
                proposalResponse -> System.out.println(
                        "Response status: " +
                        proposalResponse.getResponse().getStatus())
        );
        System.out.println("Show installed chaincode for peer0:");
        service.queryInstalledChaincodes(peer0, org1Admin).forEach(chaincodeInfo -> System.out.printf("\tName: %s, Version: %s\n",
                chaincodeInfo.getName(),
                chaincodeInfo.getVersion()));
    }

    @Test
    public void instantiateChaincodeTest() throws
            InvalidArgumentException,
            TransactionException,
            ProposalException,
            ChaincodeEndorsementPolicyParseException,
            IOException {
        final String channel = "mychannel";
        ChaincodeInstantiate instantiate = new ChaincodeInstantiate()
                .addArg("a-init-argument")
                .chaincodeName("test_cc")
                .version("1.3")
                .yamlPolicy(new File("chaincodes/endorsementpolicy.yaml"));
        Peer peer0 = service.buildPeer(org1, "peer0", org1Admin);
        Orderer orderer = service.buildOrderer(ordererOrg, "orderer", ordererAdmin);
        service.instantiateChaincode(instantiate, channel, org1Admin, orderer, peer0).forEach(
                proposalResponse -> {
                    System.out.println("Response status: " + proposalResponse.getResponse().getStatus());
                    System.out.println("Response message: " + proposalResponse.getResponse().getMessage());
                }
        );
    }
}
