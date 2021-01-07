package com.github.yiwenlong.fabric.proto.test;

import com.github.yiwenlong.fabric.utils.FileUtils;
import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;
import org.hyperledger.fabric.protos.common.Common;
import org.hyperledger.fabric.protos.common.Configtx;
import org.hyperledger.fabric.protos.common.MspPrincipal;
import org.hyperledger.fabric.protos.common.Policies;
import org.hyperledger.fabric.protos.msp.MspConfig;
import org.hyperledger.fabric.protos.orderer.Configuration;
import org.junit.Test;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public class ConfigBlockTest {

    @Test
    public void testDecodeOrdererGenesisBlock() throws IOException {
        byte[] blockBytes = FileUtils.toByteArray("/Users/yiwenlong/Code/fabric-samples-nodocker/samples/network-single-org/Orderer/genesis.block");
        Common.Block block = Common.Block.parseFrom(blockBytes);
        List<ByteString> dataList = block.getData().getDataList();
        for (ByteString bytes : dataList) {
            Common.Envelope envelope = Common.Envelope.parseFrom(bytes);
            Common.Payload payload = Common.Payload.parseFrom(envelope.getPayload());
//            printPayloadHeader(payload.getHeader());

            Configtx.ConfigEnvelope configEnvelope = Configtx.ConfigEnvelope.parseFrom(payload.getData());
//            printConfig(configEnvelope.getConfig());
//            printConfigGroup(configEnvelope.getConfig().getChannelGroup().getGroupsMap().get("Orderer").getGroupsMap().get("Orderer"));
//            printConfigValue(configEnvelope.getConfig().getChannelGroup().getGroupsMap().get("Orderer").getGroupsMap().get("Orderer").getValuesMap().get("MSP"));
//            Configtx.ConfigValue mspConfigValue = configEnvelope.getConfig().getChannelGroup().getGroupsMap().get("Orderer").getGroupsMap().get("Orderer").getValuesMap().get("MSP");
//            MspConfig.FabricMSPConfig mspConfig = MspConfig.FabricMSPConfig.parseFrom(mspConfigValue.getValue());
//            printFabricMSPConfig(mspConfig);
//            Configtx.ConfigPolicy configPolicy = configEnvelope.getConfig().getChannelGroup().getGroupsMap().get("Orderer").getGroupsMap().get("Orderer").getPoliciesMap().get("Readers");
//            Configtx.ConfigPolicy configPolicy = configEnvelope.getConfig().getChannelGroup().getGroupsMap().get("Orderer").getGroupsMap().get("Orderer").getPoliciesMap().get("Writers");
//            Configtx.ConfigPolicy configPolicy = configEnvelope.getConfig().getChannelGroup().getGroupsMap().get("Orderer").getGroupsMap().get("Orderer").getPoliciesMap().get("Admins");
//            printConfigPolicy(configPolicy);

//            configEnvelope.getConfig().getChannelGroup().getGroupsMap().get("Orderer").getValuesMap()
//                    .forEach((key, value) -> {
//                        System.out.println("Key of value: " + key);
//                        printConfigValue(value);});
            Configtx.ConfigValue configValue = configEnvelope.getConfig().getChannelGroup().getGroupsMap().get("Orderer").getValuesMap().get("ConsensusType");
            System.out.println(configValue.getValue().toStringUtf8());
//            Configuration.ConsensusType consensusType = Configuration.ConsensusType.parseFrom(configValue.getValue());
//            printConsensus(consensusType);

        }
    }

    protected void printConsensus(Configuration.ConsensusType consensusType) {
        System.out.println(consensusType.getType());
        System.out.println(consensusType.getUnknownFields());
    }

    protected void printConfigPolicy(Configtx.ConfigPolicy policy) throws InvalidProtocolBufferException {
        System.out.println(policy);
        Policies.SignaturePolicyEnvelope sPolicy = Policies.SignaturePolicyEnvelope.parseFrom(policy.getPolicy().getValue());
        System.out.println(sPolicy);
        System.out.println(sPolicy.getIdentitiesList().get(0).getPrincipalClassification());

        MspPrincipal.MSPRole mspRole = MspPrincipal.MSPRole.parseFrom(sPolicy.getIdentitiesList().get(0).getPrincipal());
        System.out.println(mspRole.getMspIdentifier());
        System.out.println(mspRole.getRole());
    }

    protected void printFabricMSPConfig(MspConfig.FabricMSPConfig fabricMSPConfig) throws InvalidProtocolBufferException {
//        System.out.println(fabricMSPConfig);
//        System.out.println(fabricMSPConfig.getRootCertsCount());
        MspConfig.FabricMSPConfig mspConfig = MspConfig.FabricMSPConfig.parseFrom(fabricMSPConfig.getRootCerts(0));
        System.out.println(mspConfig.getRootCerts(0).toStringUtf8());
    }

    protected void printConfigValue(Configtx.ConfigValue value) {
        System.out.printf("ModPolicy: %s\n", value.getModPolicy());
        System.out.printf("Version: %s\n", value.getVersion());
        System.out.printf("Value: %s\n", value.getValue().toStringUtf8());
    }

    protected void printConfigGroup(Configtx.ConfigGroup config) {
        Map<String, Configtx.ConfigGroup> groupsMap = config.getGroupsMap();
        System.out.println("ConfigGroups: ");
        groupsMap.forEach((key, group) -> System.out.printf("Key: %s, \tModPolicy: %s\n", key, group.getModPolicy()));
        Map<String, Configtx.ConfigValue> valueMap = config.getValuesMap();
        System.out.println("ConfigValues: ");
        valueMap.forEach((key, group) -> System.out.printf("Key: %s, \tModPolicy: %s\n", key, group.getModPolicy()));
        Map<String, Configtx.ConfigPolicy> policyMap = config.getPoliciesMap();
        System.out.println("ConfigPolicies: ");
        policyMap.forEach((key, group) -> System.out.printf("Key: %s, \tModPolicy: %s\n", key, group.getModPolicy()));
    }

    protected void printPayloadHeader(Common.Header header) throws InvalidProtocolBufferException {
        Common.ChannelHeader channelHeader = Common.ChannelHeader.parseFrom(header.getChannelHeader());
        Common.SignatureHeader signatureHeader = Common.SignatureHeader.parseFrom(header.getSignatureHeader());
        System.out.println(channelHeader);
        System.out.println(signatureHeader);
    }

    @Test
    public void testDecodeChannelTxFile() throws IOException {
        byte[] configTxBytes = FileUtils.toByteArray("network/channel-mychannel/mychannel.tx");
        Common.Envelope envelope = Common.Envelope.parseFrom(configTxBytes);
        Common.Payload payload = Common.Payload.parseFrom(envelope.getPayload());
        Configtx.ConfigUpdateEnvelope configUpdateEnvelope = Configtx.ConfigUpdateEnvelope.parseFrom(payload.getData());
        Configtx.ConfigUpdate configUpdate = Configtx.ConfigUpdate.parseFrom(configUpdateEnvelope.getConfigUpdate());
        System.out.println(configUpdate.toString());
    }
}
