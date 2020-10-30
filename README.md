# Single Page Application Server
![Update Docker Images](https://github.com/PSanetra/single-page-application-server/workflows/Update%20Docker%20Images/badge.svg?event=schedule)

This image can be used as a base image for single page applications. It is itself based on Nginx.

## Tags

The following tags will be updated automatically with the latest nginx base image on a weekly basis:

* `latest`, `latest-nginx-mainline-alpine`, `1-nginx-mainline-alpine`
* `latest-nginx-stable-alpine`, `1-nginx-stable-alpine`

There will also be tags for specific versions.

## General Features

* Any non-existing routes return the root `index.html`
  * Does NOT apply to resource routes with the following filename extensions: `js | css | ico | pdf | flv | jpg | jpeg | png | gif | swf`
* Configure application dynamically at container startup
* Configure base element
* Support environment specific configuration depending on the requested host (port and domain)
* Hashed resources are cached indefinitely by the browser without revalidating
  * Resource name needs to look like `my-script.3f8a240b190e37d1.js`
    * Hash needs to consist of at least 16 characters
  * Applies to resources with the following filename extensions: `js | css | ico | pdf | flv | jpg | jpeg | png | gif | swf`
* HTTP 2 is enabled by default for HTTPS connections

## Security Features

* Restrictive Content Security Policy by default
  * Server API endpoints can be whitelisted automatically
* Referrer is disabled by default
* Content Type Sniffing is disabled by default
* HTTPS is enforced via HSTS by default if enabled
* HTTPS uses [recommended OWASP protocols and cipher suites](https://cheatsheetseries.owasp.org/cheatsheets/TLS_Cipher_String_Cheat_Sheet.html)
* Container runs as non-root user
  * Nevertheless the server can still bind to port 80 and 443

## Configuration

### App Directory

Copy your SPA resources to `/app/`. All resources in this directory will be served by the Nginx server.

### YAML Configuration

The container is configured using YAML files.

You can mount your custom configuration file at `/config/config.yaml`. Every specified option in this file will override the default configuration.

It is also possible to merge multiple configuration files by specifying the `CONFIG_FILES` environment variable like `CONFIG_FILES="file:///config/config1.yaml|file:///config/config2.yaml"`. The configuration options, specified in the first configuration file in that variable, will have priority over the options in later declared files.

The following configuration shows the default values for every available option:
```yaml
# Do not edit or replace this file!
# Instead create a second config file, which overrides these defaults.
default:
  # Specifies to which host names this configuration should apply.
  server_names:
    # "_" matches any hostname
    - "_"
  # The href attribute for the base element in the index.html
  base_href: "/"
  # All options in this map will be available inside the SPA via `window.spaConfig`.
  # To enable this feature, you also need to include spa_config.js in your index.html.
  # An existing spa_config.js will be overridden at container startup.
  spa_config:
    # A map of endpoints to which the SPA will communicate.
    # These endpoints will automatically be whitelisted in the connect-src CSP directive if .hardening.whitelist_connect_sources is enabled.
    endpoints: {}
  access_log:
    # Enables access logging
    enabled: false
  http:
    enabled: true
    port: 80
    # Enables redirect to HTTPS if HTTPS is enabled.
    https_redirect: true
    # Use different https_redirect_port if application is behind a NAT.
    # 0 = use https.port
    https_redirect_port: 0
    # HTTP 2 over plain text is disabled by default as Nginx supports HTTP 2 over plain text only via prior knowledge.
    # Enabling HTTP 2 for plain text connections will prevent clients to connect without prior knowledge.
    # https://trac.nginx.org/nginx/ticket/816
    http2_enabled: false
  https:
    enabled: false
    port: 443
    # Enforces HTTPS permanently
    hsts_enabled: true
    ssl_certificate: /etc/ssl/default.crt
    ssl_certificate_key: /etc/ssl/default.key
    # Configures supported TLS protocols and cipher suites with recommended value
    # https://cheatsheetseries.owasp.org/cheatsheets/TLS_Cipher_String_Cheat_Sheet.html
    owasp_cipher_string: A
    http2_enabled: true
  hardening:
    # Disables referrer to prevent information leakage
    referrer_policy: "no-referrer"
    # Prevents browsers from guessing the content type
    x_content_type_options: "nosniff"
    # Will whitelist the endpoints listed in .spa_config.endpoints automatically in the connect-srv CSP directive if enabled.
    whitelist_spa_config_endpoints_as_connect_sources: true
    # Map of CSP directives, which will be added to all HTTP responses for HTML and JavaScript documents
    content_security_policy:
      base-uri: "'self'"
      block-all-mixed-content: true
      default-src: "'self'"
      form-action: "'self'"
      frame-ancestors: "'self'"
      frame-src: "'self'"
      object-src: "'none'"
      script-src: "'self'"
      style-src: "'self'"
```

Aside to the `default` configuration block, you can also define other blocks, which might define configuration options for special hosts. The non-default blocks will inherit the configured options of the default-block if they are not explicitly redeclared.

Example:
```yaml
default:
  spa_config:
    appTitle: "My Default Application"
    endpoints:
      globalApi: "https://api.example.com"
special_host:
  server_names:
    - "special.example.com"
  spa_config:
    appTitle: "My Special Application"
```

With this configuration the application would have the app title "My Special Application", when it is accessed via the host `special.example.com`, while the endpoints would stay the same in every instance of the application.

## Development

Configuration files are dynamically generated via [gomplate templates](https://docs.gomplate.ca/).

Tests are written in Java using [Testcontainers](https://www.testcontainers.org/).

## License

MIT
