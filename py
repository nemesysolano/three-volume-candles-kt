#!/bin/sh
SCRIPT_DIR="$( cd "$( dirname "$0" )" && pwd )"
SCRIPT_NAME=$1
export PYTHONPATH="${SCRIPT_DIR}/src/main/python"
shift
python "${SCRIPT_DIR}/src/main/python/${SCRIPT_NAME}" $*
