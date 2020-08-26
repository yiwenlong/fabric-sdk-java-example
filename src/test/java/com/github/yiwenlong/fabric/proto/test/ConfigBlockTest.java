package com.github.yiwenlong.fabric.proto.test;

import com.github.yiwenlong.fabric.utils.FileUtils;
import org.hyperledger.fabric.protos.common.Common;
import org.junit.Test;

import java.io.IOException;

public class ConfigBlockTest {

    @Test
    public void testDecodeOrdererGenesisBlock() throws IOException {
        byte[] blockBytes = FileUtils.toByteArray("network/Orderer/orderer0/genesis.block");
        Common.Block block = Common.Block.parseFrom(blockBytes);
        System.out.println(block.toString());
    }
}
