FROM node:20-alpine as build

WORKDIR /src

COPY . .

RUN npm ci && npm run build:prod

FROM codecentric/single-page-application-server:1-nginx-stable-alpine

COPY ./default-config.yaml /config/default.yaml

# Usually we would mount this file at runtime, but we will add it to the image for demo purposes.
COPY ./runtime-config.yaml /config/config.yaml

COPY --from=build /src/dist/angular-example/browser/ /app/
