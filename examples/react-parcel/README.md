# React example with SpaConfig.js

This is a small React example app using Parcel, which includes a `SpaConfig.js` to configure the application.

## How To

1. Place `SpaConfig.js` in `/public`
2. Declare `spaConfig` in the `window` object via the TypeScript definition file `/src/@types/SpaConfig.d.ts`
3. Add `<script type="application/javascript" src="SpaConfig.js"></script>` to `index.html` before referencing the main script.
4. Add the package.json configuration from the below snippet.
5. Add .parcelrc configuration from the below snippet.

### `package.json` snippet
```json
{
  "devDependencies": {
    "parcel": "^2.8.2",
    "parcel-reporter-static-files-copy": "^1.4.0",
    "parcel-resolver-ignore": "^2.1.3"
  },
  "scripts": {
    "start": "parcel public/index.html",
    "build": "parcel build public/index.html"
  },
  "parcelIgnore": [
    "SpaConfig\\.js"
  ],
  "staticFiles": {
    "staticPath": "public/SpaConfig.js"
  }
}
```

### `.parcelrc` snippet
```json
{
  "extends": "@parcel/config-default",
  "resolvers": ["parcel-resolver-ignore", "..."],
  "reporters": ["...", "parcel-reporter-static-files-copy"]
}
```

## Build Dockerfile

```bash
$ docker build -t react-example .
```

## Run Image

```bash
$ docker run -it --rm -p 8080:80 -v "$(pwd)/runtime-config.yaml:/config/config.yaml" react-example
```

In the browser on `http://localhost:8080` should be a page visible containing the text `Prod Title`.
