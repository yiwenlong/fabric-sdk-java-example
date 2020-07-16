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
read -r -p "Are You Sure Clean Fabric Network Running Data? [Y/n] " input
case $input in
  [yY][eE][sS]|[yY])
  for node in "peer0" "peer1"; do
    if [ -d "Org1/$node/data" ]; then
      rm -fr "Org1/$node/data"
    fi
    if [ -f "Org1/$node/FABRIC-NODOCKER-Org1-$node.log" ]; then
      rm -f "Org1/$node/FABRIC-NODOCKER-Org1-$node.log"
    fi
  done

  for node in "orderer0" "orderer1" "orderer2"; do
    if [ -d "Orderer/$node/etcdraft" ]; then
      rm -fr "Orderer/$node/etcdraft"
    fi
    if [ -d "Orderer/$node/file-ledger" ]; then
      rm -fr "Orderer/$node/file-ledger"
    fi
    if [ -f "Orderer/$node/FABRIC-NODOCKER-Orderer-$node.log" ]; then
      rm -f "Orderer/$node/FABRIC-NODOCKER-Orderer-$node.log"
    fi
  done
  echo "Clean Done!"
esac