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
DIR=$(cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd)

for node in "peer0" "peer1"; do
  if ! "$DIR/Org1/$node/stop.sh"; then
    echo "Node [$node] stop failed!"
  fi
done

for node in "orderer0" "orderer1" "orderer2"; do
  if ! "$DIR/Orderer/$node/stop.sh"; then
    echo "Node [$node] stop failed!"
  fi
done

if ! "$DIR/chaincode-tps/stop.sh"; then
  echo "Chaincode [tps] stop failed!"
fi