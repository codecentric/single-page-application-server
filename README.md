# Single Page Application Server

![Update Docker Images](https://github.com/codecentric/single-page-application-server/workflows/Update%20Docker%20Images/badge.svg)

This container image provides a base for serving Single Page Applications (SPAs), leveraging Nginx as its web server.

For a fitting Helm Chart see [chart/README.md](https://github.com/codecentric/single-page-application-server/blob/master/chart/README.md).

## Tags

The following tags are updated automatically on a weekly basis with the latest Nginx base image:

* `latest` (alias for `1-nginx-stable-alpine`)
* `1` (alias for `1-nginx-stable-alpine`)
* `latest-nginx-stable-alpine` (alias for `1-nginx-stable-alpine`)
* `1-nginx-stable-alpine`
* `latest-nginx-mainline-alpine` (alias for `1-nginx-mainline-alpine`)
* `1-nginx-mainline-alpine`

Additional tags for specific Nginx versions are also available.

## Examples

Examples for usage with Angular and React are located in the `examples` directory.

## General Features

- **SPA Routes Handling**: Routes not matching static files will serve `index.html`, with exceptions for resources like `.js`, `.css`, etc.
- **Dynamic Configuration**: Configure applications at container startup.
- **Environment-Specific Config**: Customize settings based on port and domain.
- **Resource Caching**: Hashed resources are cached indefinitely. Resources must include a hash of at least 8 characters.
- **HTTP/2**: Enabled by default for HTTPS connections.
- **Helm Chart**: A general [Helm chart](https://github.com/codecentric/single-page-application-server/blob/master/chart/README.md) is available for applications using this image.

## Security Features

- **Content Security Policy**: Restrictive by default, with automatic whitelisting for server API endpoints.
- **Referrer Policy**: Disabled by default to prevent leakage.
- **Content Type Sniffing**: Disabled by default.
- **HTTPS**: Enforced via HSTS if enabled; uses [recommended OWASP protocols and cipher suites.](https://cheatsheetseries.owasp.org/cheatsheets/TLS_Cipher_String_Cheat_Sheet.html)
- **Non-Root User**: The container runs as a non-root user but can bind to ports 80 and 443.
- **Source Maps**: Disabled by default.
- **Read-only root filesystem**: [Supported at container runtime](#read-only-root-filesystem-support)

## Configuration

### App Directory

Place your SPA resources in `/app/`. All files in this directory will be served by Nginx.

### YAML Configuration

Configure the application through YAML files at startup:

1. **Default Configuration**: Add a default configuration file to `/config/default.yaml`. Usually added during `docker build`.
2. **Runtime Configuration**: Mount a runtime configuration file at `/config/config.yaml`. This file will override default settings.

#### Example Configuration

```yaml
default:
  spa_config:
    appTitle: "My Application"
    endpoints:
      api: "https://api.example.com"
```

You can also define host-specific configurations:

```yaml
default:
  spa_config:
    appTitle: "My Default Title"
    endpoints:
      api: "https://api.example.com"
special_host:
  server_names:
    - "special.example.com"
  spa_config:
    appTitle: "My Domain-specific Title"
```

#### Configuration Reference

The following configuration shows the default values of this base image for every available setting:

```yaml
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
  source_maps:
    # Enables source maps
    enabled: false
    # Configures the regex that is used to identify source map resources
    regex: "\\.(js|css)\\.map$"
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
  keepalive:
    server:
      # Sets a timeout in seconds during which a keep-alive client connection will stay open on the server side.
      timeout_seconds: 75
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

## Read-only Root Filesystem Support

For security, use a read-only root filesystem. Ensure the following directories are writable:

* `/config/.out`: Used for file generation.
* `/tmp`: Used by Nginx for cached files and `nginx.pid`.

When using Kubernetes, consider mounting these directories as writable volumes with `emptyDir`.

## Development

* **Configuration Generation**: Uses [gomplate templates](https://docs.gomplate.ca/).
* **Image Tests**: Written in Java using [Testcontainers](https://www.testcontainers.org/).
* **Helm Chart Tests**: Uses the [helm-unittest](https://github.com/helm-unittest/helm-unittest) Helm plugin.

## License

MIT
