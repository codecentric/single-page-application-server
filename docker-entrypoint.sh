#!/bin/sh -e

test ! -z "${APP_ROOT}" || ( echo 'APP_ROOT is not defined!' && false )
test ! -z "${CONFIG_DIR}" || ( echo 'CONFIG_DIR is not defined!' && false )

${CONFIG_DIR}/bootstrap.sh

find "${APP_ROOT}" -maxdepth 1 -iname 'spa_config.js' -exec rm {} \;
find "${APP_ROOT}" -maxdepth 1 -iname 'spa_config.*.js' -exec rm {} \;
find "${APP_ROOT}" -maxdepth 1 -iname 'spaConfig.js' -exec rm {} \;
find "${APP_ROOT}" -maxdepth 1 -iname 'spaConfig.*.js' -exec rm {} \;

find "${CONFIG_DIR}/.out" -name "spa_config.*.js" -exec cp {} ${APP_ROOT}/ \;
find "${CONFIG_DIR}/.out" -name "index.*.html" -exec cp {} ${APP_ROOT}/ \;

# Delete existing index in root directory so that each listening server can serve its custom index as default document
rm -f "${APP_ROOT}/index.html"

exec nginx -g "daemon off;"
