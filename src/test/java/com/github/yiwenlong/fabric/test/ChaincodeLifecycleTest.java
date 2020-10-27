package com.github.yiwenlong.fabric.test;

import com.github.yiwenlong.fabric.ChaincodeDefinition;
import com.github.yiwenlong.fabric.FabricService;
import com.github.yiwenlong.fabric.Organization;
import com.github.yiwenlong.fabric.network.NetworkOrganizationConfig;
import org.hyperledger.fabric.sdk.Peer;
import org.hyperledger.fabric.sdk.User;
import org.hyperledger.fabric.sdk.exception.InvalidArgumentException;
import org.hyperledger.fabric.sdk.exception.ProposalException;
import org.hyperledger.fabric.sdk.exception.TransactionException;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.FileNotFoundException;

public class ChaincodeLifecycleTest {

    private FabricService service = FabricService.service();

    private Organization org1;
    private User org1Admin;

    @Before
    public void init() throws FileNotFoundException {
        org1 = new Organization(NetworkOrganizationConfig.Org1).init();
        org1Admin = org1.user("Admin");
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
    public void installChaincodeTest() throws InvalidArgumentException, ProposalException, FileNotFoundException {
        ChaincodeDefinition ccDefine = new ChaincodeDefinition()
                .chaincodeName("test_cc")
                .version("1.4")
                .sourceFile(new File("chaincodes/certificate.tar.gz"));
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


}
