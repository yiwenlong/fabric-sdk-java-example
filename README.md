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

```shell
mvn test -Dtest=ChannelTestCase
```

### Chaincode Lifecycle test cases

```shell
mvn test -Dtest=ChaincodeLifecycleTestCase
```

### Chaincode test cases

```shell
mvn test -Dtest=ChaincodeTestCase
```
