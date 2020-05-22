FROM alpine:3
COPY ./action /action
RUN apk --no-cache add curl jq
ENTRYPOINT ["/action"]
