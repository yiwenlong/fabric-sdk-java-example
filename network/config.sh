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

PROC_PREFIX="Fabric-SDK-example"

# For peer nodes
for node in "peer0" "peer1"; do
  node_home="$DIR/Org1/$node"
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
  echo "$node"
done

# For chaincode service
