#!/bin/sh -e

test -n "${APP_ROOT}" || ( echo 'APP_ROOT is not defined!' && false )
test -n "${CONFIG_DIR}" || ( echo 'CONFIG_DIR is not defined!' && false )

${CONFIG_DIR}/bootstrap.sh

exec nginx -g "daemon off;"
