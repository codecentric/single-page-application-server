FROM node:18-alpine as build

WORKDIR /src

COPY . .

RUN yarn install --frozen-lockfile && yarn run build

FROM codecentric/single-page-application-server:1-nginx-stable-alpine

# Usually we would mount this file at runtime, but we will add it to the image for demo purposes.
COPY ./runtime-config.yaml /config/config.yaml

COPY --from=build /src/dist/ /app/
