package main

import (
	"fmt"

	"github.com/hyperledger/fabric/core/chaincode/shim"
	"github.com/hyperledger/fabric/protos/peer"
)

type Manager struct {
}

func (m *Manager) Init(stub shim.ChaincodeStubInterface) peer.Response {
	_, args := stub.GetFunctionAndParameters()
	fmt.Printf("Init args: %v", args)
	if len(args) != 1 {
		return shim.Error(fmt.Sprintf("Error args: expect 1, got: %v", args))
	}
	return shim.Success([]byte(args[0]))
}

func (m *Manager) Invoke(stub shim.ChaincodeStubInterface) peer.Response {
	_func, args := stub.GetFunctionAndParameters()
	fmt.Printf("Invoke function: %s, args: %v", _func, args)
	if _func == "get" {
		return handleGet(args, stub)
	} else if _func == "put" {
		return handlePut(args, stub)
	}
	return shim.Error("Error function name: " + _func)
}

func handleGet(args []string, stub shim.ChaincodeStubInterface) peer.Response {
	if len(args) != 1 {
		return shim.Error("Error args length. expect 1")
	}
	value, err := stub.GetState(args[0])
	if err != nil {
		return shim.Error(err.Error())
	}
	return shim.Success(value)
}

func handlePut(args []string, stub shim.ChaincodeStubInterface) peer.Response {
	if len(args) != 2 {
		return shim.Error("Error args length. expect 2")
	}
	if err := stub.PutState(args[0], []byte(args[1])); err != nil {
		return shim.Error(err.Error())
	}
	return shim.Success([]byte("Success"))
}

func main() {
	manage := &Manager{}
	err := shim.Start(manage)
	if err != nil {
		fmt.Printf("Error starting Test chaincode: %s", err)
	}
}
