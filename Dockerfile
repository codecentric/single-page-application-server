ARG NGINX_TAG
FROM ghcr.io/hairyhenderson/gomplate:stable as gomplate
FROM nginxinc/nginx-unprivileged:${NGINX_TAG} as single-page-app-server
COPY --from=gomplate /gomplate /usr/local/bin/gomplate

ENV APP_ROOT="/app"
ENV CONFIG_DIR="/config"
ENV CONFIG_FILES=""

WORKDIR ${APP_ROOT}

COPY ./docker-entrypoint.sh /docker-entrypoint.sh
COPY ./config/ "${CONFIG_DIR}/"

USER root
RUN chown -R 101:101 /docker-entrypoint.sh "${CONFIG_DIR}" "${APP_ROOT}"
RUN rm -r /etc/nginx/conf.d/ && ln -s "${CONFIG_DIR}/.out/conf.d" /etc/nginx/conf.d
# Enable binding privileged ports with non root user
RUN apk --no-cache add libcap && \
    setcap cap_net_bind_service=+ep /usr/sbin/nginx && \
    apk --no-cache del libcap
USER nginx

ENTRYPOINT ["/docker-entrypoint.sh"]
