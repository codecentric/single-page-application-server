FROM node:12-alpine as build

WORKDIR /src

COPY . .

RUN yarn install --frozen-lockfile && yarn run build

FROM codecentric/single-page-application-server:1-nginx-stable-alpine

COPY --from=build /src/build/ /app/
