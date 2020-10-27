package com.github.yiwenlong.fabric.utils;

import java.util.Properties;
import java.util.concurrent.TimeUnit;

public class PropertiesHelper {

    public static Properties createTlsAccessProperties(String tlscaFilePath) {
        Properties properties = new Properties();
        properties.setProperty("sslProvider", "openSSL");
        properties.setProperty("negotiationType", "TLS");
        properties.put("pemFile", tlscaFilePath);

        properties.put("grpc.NettyChannelBuilderOption.keepAliveTime", new Object[] {5L, TimeUnit.MINUTES});
        properties.put("grpc.NettyChannelBuilderOption.keepAliveTimeout", new Object[] {8L, TimeUnit.SECONDS});
        properties.put("grpc.NettyChannelBuilderOption.keepAliveWithoutCalls", new Object[] {true});
        return properties;
    }
}
