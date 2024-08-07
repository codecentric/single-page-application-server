# Angular example with spa_config.js

This is a small Angular example app, which includes a `spa_config.js` to configure the application.

## How To

1. Place `spa_config.js` in `/src`
2. Add `"src/spa_config.js"` to `.projects["your-project"].architect.build.options.assets` in `angular.json`
3. Declare `spaConfig` in the `window` object via the TypeScript definition file `/src/@types/spa_config.d.ts`
4. Add `<script src="./spa_config.js"></script>` to `index.html`
5. Add a `/config/default.yaml` configuration file to the image, which allows 'unsafe-inline' styles. (See `default-config.yaml`, [#angular/26152](https://github.com/angular/angular/issues/26152))
6. [Disable inline event handler script for styles.](https://github.com/angular/angular-cli/issues/20864#issuecomment-1165769416):
   * ```json
     {
       "...": "...",
       "configurations": {
         "production": {
           "...": "...",
           "optimization": {
             "scripts": true,
             "styles": {
               "minify": true,
               "inlineCritical": false
             },
             "fonts": true
           }
         },
         "...": "..."
       }
     }
     ```

## Build Dockerfile

```bash
$ docker build -t angular-example .
```

## Run Image

```bash
$ docker run -it --rm -p 8080:80 -v "$(pwd)/runtime-config.yaml:/config/config.yaml" angular-example
```
