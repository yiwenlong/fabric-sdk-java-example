# fabric-sdk-java-example
A simple example to use and manage fabric network using fabric-sdk-java.

## Requirment

### supervisor，maven

You should install supervisor and maven on your own operation system.

## Bring up the Fabric network processes.

### Download Fabric & Chaincode binaries

```shell
./scripts/download-binaries.sh
```

### Config network boot & stop scripts

```shell
./network/config.sh
```

### Bring up the Fabric network

```shell
./networl/start.sh
```

## Run the Fabric test cases

### Channel test cases

#### Create channel

```shell
mvn test -Dtest=ChannelTest#testCreateChannel
```

#### Join channel

```shell
mvn test -Dtest=ChannelTest#testJoinChannelPeer0
mvn test -Dtest=ChannelTest#testJoinChannelPeer1
```

#### View channel information

```shell
mvn test -Dtest=ChannelTest#testViewChannel
```

### Chaincode Lifecycle test cases

#### Install Chaincode

```shell
mvn test -Dtest=ChaincodeLifecycleTest#testInstallChaincode
```

#### View chaincode install information

```shell
mvn test -Dtest=ChaincodeLifecycleTest#testQueryInstalledChaincode
```

#### Approve chaincode defination

```shell
mvn test -Dtest=ChaincodeLifecycleTest#testApproveChaincode
```

#### View approve information

```shell
mvn test -Dtest=ChaincodeLifecycleTest#testQueyApproveChaincode
```

#### Commit chaincode defination

```shell
mvn test -Dtest=ChaincodeLifecycleTest#testCommitChaincode
```

#### View chaincode defination

```shell
mvn test -Dtest=ChaincodeLifecycleTest#testChaincodeDefinition
```

### Chaincode test cases

#### Invoke chaincode

```shell
mvn test -Dtest=ChaincodeTest#testInvoke
```

#### Query chaincode

```shell
mvn test -Dtest=ChaincodeTest#testQueryChaincode
```

