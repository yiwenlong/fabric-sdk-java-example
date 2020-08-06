package com.github.yiwenlong.fabric.test;

import com.github.yiwenlong.fabric.network.SingleOrgNetwork;
import org.hyperledger.fabric.protos.common.Configtx;
import org.hyperledger.fabric.sdk.Channel;
import org.hyperledger.fabric.sdk.HFClient;
import org.hyperledger.fabric.sdk.Orderer;
import org.hyperledger.fabric.sdk.User;
import org.hyperledger.fabric.sdk.exception.InvalidArgumentException;
import org.hyperledger.fabric.sdk.exception.TransactionException;
import org.hyperledger.fabric.sdk.security.CryptoSuite;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.security.Security;

public class SystemChannelTest {

    static {
        Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
    }

    private HFClient fabricClient = HFClient.createNewInstance();
    private Orderer orderer0;
    private User admin;
    private Channel sysChannel;

    @Before
    public void init() throws Exception {
        admin = SingleOrgNetwork.Orderers.admin();
        fabricClient.setCryptoSuite(CryptoSuite.Factory.getCryptoSuite());
        fabricClient.setUserContext(admin);

        orderer0 = SingleOrgNetwork.Orderers.Orderer0.get(fabricClient);
        sysChannel = fabricClient.newChannel("sys-channel-nodocker");
        sysChannel.addOrderer(orderer0);
        sysChannel.initialize();
    }

    @Test
    public void testViewSystemChannelInformation() throws TransactionException, InvalidArgumentException, IOException {
        byte[] configuration = sysChannel.getChannelConfigurationBytes();
        Configtx.Config config = Configtx.Config.parseFrom(configuration);
        Assert.assertArrayEquals(configuration, config.toByteArray());
    }
}
