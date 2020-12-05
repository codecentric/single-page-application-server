#!/bin/sh -e

test -n "${APP_ROOT}" || ( echo 'APP_ROOT is not defined!' && false )
test -n "${CONFIG_DIR}" || ( echo 'CONFIG_DIR is not defined!' && false )

if [ -z "${CONFIG_FILES}" ] && [ -f "${CONFIG_DIR}/config.yaml" ]; then
  export CONFIG_FILES="file://${CONFIG_DIR}/config.yaml"
fi

if [ -z "${CONFIG_FILES}" ]; then
  export CONFIG_FILES="file://${CONFIG_DIR}/default_config.yaml"
else
  export CONFIG_FILES="merge:${CONFIG_FILES}|file://${CONFIG_DIR}/default_config.yaml"
fi

rm -rf "${CONFIG_DIR}/.out"

cd "${CONFIG_DIR}"

gomplate -c "Servers=${CONFIG_FILES}" --input-dir "${CONFIG_DIR}/templates" --output-dir "${CONFIG_DIR}/.out/"
