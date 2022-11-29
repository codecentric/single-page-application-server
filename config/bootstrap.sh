#!/bin/sh -e

test -n "${APP_ROOT}" || ( echo 'APP_ROOT is not defined!' && false )
test -n "${CONFIG_DIR}" || ( echo 'CONFIG_DIR is not defined!' && false )

if [ -z "${CONFIG_FILES}" ] && [ -f "${CONFIG_DIR}/config.yaml" ]; then
  CONFIG_FILES="file://${CONFIG_DIR}/config.yaml"
fi

if [ -f "${CONFIG_DIR}/default.yaml" ]; then
  if [ -z "${CONFIG_FILES}" ]; then
    CONFIG_FILES="file://${CONFIG_DIR}/default.yaml"
  else
    CONFIG_FILES="${CONFIG_FILES}|file://${CONFIG_DIR}/default.yaml"
  fi
fi

if [ -z "${CONFIG_FILES}" ]; then
  CONFIG_FILES="file://${CONFIG_DIR}/.internal_default.yaml"
else
  CONFIG_FILES="merge:${CONFIG_FILES}|file://${CONFIG_DIR}/.internal_default.yaml"
fi

rm -rf "${CONFIG_DIR}/.out"

cd "${CONFIG_DIR}"

gomplate -c "Servers=${CONFIG_FILES}" -f "${CONFIG_DIR}/main.tmpl" --template "${CONFIG_DIR}/templates"
