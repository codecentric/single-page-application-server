ARG DND_VERSION=19.03

FROM docker:${DND_VERSION}
RUN apk --no-cache add jq
COPY ./contains-all-layers-of /contains-all-layers-of
COPY ./action /action
ENTRYPOINT ["/action"]
