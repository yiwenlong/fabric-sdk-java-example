package com.github.yiwenlong.fabric.ca.test;

import com.github.yiwenlong.fabric.SimpleFabricUser;
import com.github.yiwenlong.fabric.utils.PropertiesHelper;
import org.hyperledger.fabric.sdk.Enrollment;
import org.hyperledger.fabric.sdk.User;
import org.hyperledger.fabric.sdk.security.CryptoSuite;
import org.hyperledger.fabric_ca.sdk.EnrollmentRequest;
import org.hyperledger.fabric_ca.sdk.HFCAClient;
import org.hyperledger.fabric_ca.sdk.RegistrationRequest;
import org.junit.Before;
import org.junit.Test;

public class FabricCaClientTest {

    private HFCAClient client;

    @Before
    public void initClient() throws Exception {
        String tlsCaFile = "/Users/yiwenlong/Code/fabric/fabric-samples/test-network/organizations/fabric-ca/org1/tls-cert.pem";
        String caUrl = "https://localhost:7054";
        this.client = HFCAClient.createNewInstance(caUrl, PropertiesHelper.createTlsAccessProperties(tlsCaFile));
        this.client.setCryptoSuite(CryptoSuite.Factory.getCryptoSuite());
    }

    @Test
    public void testRegisterPeer1() throws Exception {
        RegistrationRequest registrationRequest = new RegistrationRequest("peer1");
        registrationRequest.setType("peer");
        registrationRequest.setSecret("peer1pw");

        Enrollment adminEnrollment = client.enroll("admin", "adminpw");
        User admin = SimpleFabricUser.createInstance("admin", "Org1MSP", adminEnrollment);
        String resp = client.register(registrationRequest, admin);
        System.out.println(resp);
    }

    @Test
    public void testEnrollPeerMsp() throws Exception {
        Enrollment enrollment = client.enroll("peer1", "peer1pw");
        System.out.println(enrollment.getCert());
    }
}
