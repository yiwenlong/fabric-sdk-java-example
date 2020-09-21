package com.github.yiwenlong.fabric.ca.test;

import com.github.yiwenlong.fabric.utils.PropertiesHelper;
import org.hyperledger.fabric.sdk.Enrollment;
import org.hyperledger.fabric.sdk.security.CryptoSuite;
import org.hyperledger.fabric_ca.sdk.EnrollmentRequest;
import org.hyperledger.fabric_ca.sdk.HFCAClient;
import org.junit.Test;

public class FabricCaClientTest {

    @Test
    public void testEnrollPeerMsp() throws Exception {
        String tlsCaFile = "";
        String caUrl = "https://localhost:7054";
        HFCAClient hfcaClient = HFCAClient.createNewInstance(caUrl, PropertiesHelper.createTlsAccessProperties(tlsCaFile));
        hfcaClient.setCryptoSuite(CryptoSuite.Factory.getCryptoSuite());
        EnrollmentRequest request = new EnrollmentRequest();
        request.addHost("fabric-sdk-java-example");
        Enrollment enrollment = hfcaClient.enroll("peer0", "peer0pw", request);
        System.out.println(enrollment.getCert());
    }
}
