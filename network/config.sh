#!/bin/bash
#
# Copyright 2020 Yiwenlong(wlong.yi#gmail.com)
#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
#    http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.
#

#
# config.sh generate auto boot & stop script for supervisor.
#

DIR=$(cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd)
SCRIPT_DIR=$(cd "$DIR/../scripts" && pwd)
BINARIES_DIR=$(cd "$DIR/../binaries/" && pwd)

COMMAND_PEER="$BINARIES_DIR/bin/peer"
COMMAND_ORDERER="$BINARIES_DIR/bin/orderer"
COMMAND_TPS="$BINARIES_DIR/chaincode/tps"

if [ ! -d "$BINARIES_DIR" ]; then
  echo "Start download fabric & chaincode binaries."
  exit 0
  if ! "$SCRIPT_DIR/download-binaries.sh"; then
    echo "Download fabric & chaincode binaries failed."
    exit 1
  else
    echo "Download fabirc & chaincode binaries success."
  fi
fi

if [ ! -f "$COMMAND_PEER" ]; then
  echo "Binary file not found: $COMMAND_PEER"
  exit 1
fi

if [ ! -f "$COMMAND_ORDERER" ]; then
  echo "Binary file not found: $COMMAND_ORDERER"
  exit 1
fi

if [ ! -f "$COMMAND_TPS" ]; then
  echo "Binary file not found: $COMMAND_TPS"
  exit 1
fi

PROC_PREFIX="Fabric-SDK-example"

# For peer nodes
for node in "peer0" "peer1"; do
  node_home="$DIR/Org1/$node"
  # copy command binary to node home directory.
  cp "$COMMAND_PEER" "$node_home/"
  # generate peer boot & stop script.
  node_process_name="$PROC_PREFIX-Org1-$node"
  cd "$node_home" || exit
  if ! "$SCRIPT_DIR/conf-supervisor-script.sh" -n "$node_process_name" -h "$node_home" -c "peer node start"; then
    echo "Failed config supervisor script for node: $node"
    exit $?
  fi
  echo "Success config node [$node] for boot."
done

# For orderer nodes
for node in "orderer0" "orderer1" "orderer2"; do
  node_home="$DIR/Orderer/$node"
  # copy command binary to node home directory.
  cp "$COMMAND_ORDERER" "$node_home/"
  # generate peer boot & stop script.
  node_process_name="$PROC_PREFIX-Orderer-$node"
  cd "$node_home" || exit
  if ! "$SCRIPT_DIR/conf-supervisor-script.sh" -n "$node_process_name" -h "$node_home" -c "orderer"; then
    echo "Failed config supervisor script for node: $node"
    exit $?
  fi
  echo "Success config node [$node] for boot."
done

# For chaincode service
cc_home="$DIR/chaincode-tps"
cp "$COMMAND_TPS" "$cc_home/"
node_process_name="$PROC_PREFIX-Chaincode-tps"
cd "$cc_home" || exit
if ! "$SCRIPT_DIR/conf-supervisor-script.sh" -n "$node_process_name" -h "$cc_home" -c "tps tps:818934162895283ba4fa6d04149aa40179768279d963d0c81f6b9feff20e4421 localhost:9999"; then
    echo "Failed config supervisor script for node: $node"
    exit $?
  fi
echo "Success config chaincode [tps] for boot."
