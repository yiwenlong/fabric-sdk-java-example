package com.github.yiwenlong.fabric.proto.test;

import com.github.yiwenlong.fabric.utils.FileUtils;
import com.google.protobuf.ByteString;
import org.hyperledger.fabric.protos.common.Common;
import org.hyperledger.fabric.protos.common.Configtx;
import org.junit.Test;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public class ConfigBlockTest {

    @Test
    public void testDecodeOrdererGenesisBlock() throws IOException {
        byte[] blockBytes = FileUtils.toByteArray("/Users/yiwenlong/Code/golang/src/github.com/yiwenlong/fabric-samples-nodocker/samples/network-single-org/Orderer/orderer0/genesis.block");
        Common.Block block = Common.Block.parseFrom(blockBytes);
        List<ByteString> dataList = block.getData().getDataList();
        for (ByteString bytes : dataList) {
            Common.Envelope envelope = Common.Envelope.parseFrom(bytes);
            Common.Payload payload = Common.Payload.parseFrom(envelope.getPayload());
            Configtx.ConfigEnvelope configEnvelope = Configtx.ConfigEnvelope.parseFrom(payload.getData());
            Configtx.Config config = configEnvelope.getConfig();
            Map<String, Configtx.ConfigGroup> groupsMap = config.getChannelGroup().getGroupsMap();
            groupsMap.forEach((key, group) -> System.out.printf("Key: %s, \tModPolicy: %s\n", key, group.toString()));
        }
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
