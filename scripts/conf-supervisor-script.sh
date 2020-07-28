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

SCRIPT_DIR=$(cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd)

TMP_BOOT="$SCRIPT_DIR/template/boot.sh"
TMP_STOP="$SCRIPT_DIR/template/stop.sh"

DEFAULT_CONF_SUFFIX="ini"

function usage() {
    echo "Usage: "
    echo "  config-script.sh -n supervisor_process_name -h node_home"
}

while getopts n:h:c: opt
do
  case $opt in
    n) supervisor_conf_file_name=$OPTARG ;;
    h) node_home=$OPTARG ;;
    c) command=$OPTARG ;;
    *) usage; exit 1;;
  esac
done

# If you don't set SUPERVISOR_CONFD_DIR, set a default value.
if [ -z "$SUPERVISOR_CONFD_DIR" ]; then
  arch=$(uname -s|tr '[:upper:]' '[:lower:]')
  if [ "$arch" == "darwin" ]; then
    export SUPERVISOR_CONFD_DIR="/usr/local/etc/supervisor.d"
  elif [ "$arch" == "linux" ]; then
    if hostnamectl | grep "Ubuntu" ; then
      export SUPERVISOR_CONFD_DIR="/etc/supervisor/conf.d"
      DEFAULT_CONF_SUFFIX="conf"
    elif < /etc/system-release grep CentOS ; then
      export SUPERVISOR_CONFD_DIR="/etc/supervisord.d"
    fi
  fi
fi

if [ ! -d "$node_home" ]; then
  echo "Directory not found: $node_home"
  exit 1
fi

boot_script_file=$node_home/boot.sh
sed -e "s/_supervisor_conf_file_name_/${supervisor_conf_file_name}/
        s:_supervisor_conf_dir_:${SUPERVISOR_CONFD_DIR}:
        s/_suffix_/${DEFAULT_CONF_SUFFIX}/
        s/_command_/${command}/" "$TMP_BOOT" > "$boot_script_file"
chmod +x "$boot_script_file"
echo "Node boot script generated: " "$boot_script_file"

stop_script_file=$node_home/stop.sh
sed -e "s/_supervisor_conf_file_name_/${supervisor_conf_file_name}/
        s/_suffix_/${DEFAULT_CONF_SUFFIX}/
        s:_supervisor_conf_dir_:${SUPERVISOR_CONFD_DIR}:" "$TMP_STOP" > "$stop_script_file"
chmod +x "$stop_script_file"
echo "Node stop script generated: " "$stop_script_file"