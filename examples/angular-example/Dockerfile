FROM node:12-alpine as build

WORKDIR /src

COPY . .

RUN npm ci && npm run build:prod

FROM codecentric/single-page-application-server:1-nginx-stable-alpine

COPY --from=build /src/dist/angular-example/ /app/
