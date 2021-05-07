# React example with SpaConfig.js

This is a small React example app, which includes a `SpaConfig.js` to configure the application.

## How To

1. Place `SpaConfig.js` in `/public`
2. Declare `spaConfig` in the `window` object via the TypeScript definition file `/src/@types/SpaConfig.d.ts`
3. Add `<script src="%PUBLIC_URL%/SpaConfig.js"></script>` to `index.html`
4. Add `INLINE_RUNTIME_CHUNK=false` to the `.env` file to comply with the default Content Security Policy

## Build Dockerfile

```bash
$ docker build -t react-example .
```

## Run Image

```bash
$ docker run -it --rm -p 8080:80 -v "$(pwd)/runtime-config.yaml:/config/config.yaml" react-example
```
