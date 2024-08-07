# Helm chart for images based on codecentric/single-page-application-server

## Overview

This Helm chart can deploy application container images which are based on [codecentric/single-page-application-server](https://hub.docker.com/r/codecentric/single-page-application-server). It provides a scalable and secure way to serve your SPA with customizable configuration options.

## Installation

You can install the chart using the [Docker Hub repository](https://hub.docker.com/r/codecentric/single-page-application-server-chart).

### Using the version Tag

To install the chart using the version from Docker Hub. Replace `{MAJOR}.{MINOR}.{PATCH}` with the desired version number of this chart or image.

```sh
helm install my-release oci://docker.io/codecentric/single-page-application-server-chart \
  --version {MAJOR}.{MINOR}.{PATCH} \
  --set pod.container.image.repository=my-docker-repository/my-app-image \
  --set pod.container.image.tag=latest
```

For example, to install version `1.2.3`, you would use:

```sh
helm install my-release oci://docker.io/codecentric/single-page-application-server-chart \
  --version 1.2.3 \
  --set pod.container.image.repository=my-docker-repository/my-app-image \
  --set pod.container.image.tag=latest
```

## Values

The following table lists the configurable parameters of the Helm chart and their default values.

| Parameter                                              | Description                                                             | Default                          |
|--------------------------------------------------------| ----------------------------------------------------------------------- | -------------------------------- |
| `nameOverride`                                         | Explicit name of the application (not the application instance)         | `""`                             |
| `fullnameOverride`                                     | Explicit fully qualified name of the application (not the application instance) | `""`                             |
| `config.default.base_href`                             | Base href for the SPA                                                   | `"/"`                            |
| `config.default.spa_config.endpoints`                  | SPA endpoints configuration                                             | `{}`                             |
| `config.default.http.enabled`                          | Enable HTTP                                                             | `true`                           |
| `config.default.http.port`                             | HTTP port                                                               | `80`                             |
| `config.default.https.enabled`                         | Enable HTTPS                                                            | `false`                          |
| `config.default.https.port`                            | HTTPS port                                                              | `443`                            |
| `config.default.https.ssl_certificate`                 | Path to SSL certificate                                                 | `/var/run/secrets/tls/tls.crt`   |
| `config.default.https.ssl_certificate_key`             | Path to SSL certificate key                                             | `/var/run/secrets/tls/tls.key`   |
| `labels`                                               | Labels added to all resource types                                      | `{"app.kubernetes.io/component": "frontend"}` |
| `pod.labels`                                           | Labels for the pod                                                      | `{}`                             |
| `pod.annotations`                                      | Annotations for the pod                                                 | `{}`                             |
| `pod.replicaCount`                                     | Number of replicas                                                      | `2`                              |
| `pod.securityContext.runAsNonRoot`                     | Run containers as non-root                                              | `true`                           |
| `pod.volumes`                                          | Additional volumes for the pod                                          | `[]`                             |
| `pod.chartVolumes.configOut.medium`                    | Medium for config output volume                                         | `"Memory"`                       |
| `pod.chartVolumes.configOut.sizeLimit`                 | Size limit for config output volume                                     | `"256Mi"`                        |
| `pod.chartVolumes.tmp.medium`                          | Medium for tmp volume                                                   | `"Memory"`                       |
| `pod.chartVolumes.tmp.sizeLimit`                       | Size limit for tmp volume                                               | `"2Gi"`                          |
| `pod.container.image.repository`                       | Container image repository                                              | `""`                             |
| `pod.container.image.pullPolicy`                       | Container image pull policy                                             | `Always`                         |
| `pod.container.image.tag`                              | Container image tag                                                     | `""`                             |
| `pod.container.image.pullSecrets`                      | Image pull secrets                                                      | `[]`                             |
| `pod.container.securityContext.capabilities.drop`      | Capabilities to drop                                                    | `["ALL"]`                        |
| `pod.container.securityContext.readOnlyRootFilesystem` | Use read-only root filesystem                                          | `true`                           |
| `pod.container.securityContext.runAsNonRoot`           | Run container as non-root                                              | `true`                           |
| `pod.container.livenessProbe.httpGet.path`             | Path for liveness probe                                                | `/health/liveness`               |
| `pod.container.livenessProbe.httpGet.port`             | Port for liveness probe                                                | `http`                           |
| `pod.container.readinessProbe.httpGet.path`            | Path for readiness probe                                               | `/health/readiness`              |
| `pod.container.readinessProbe.httpGet.port`            | Port for readiness probe                                               | `http`                           |
| `pod.container.volumeMounts`                           | Volume mounts for the container                                         | `[]`                             |
| `pod.container.resources.limits.cpu`                   | CPU limit for the container                                             | `2`                              |
| `pod.container.resources.limits.memory`                | Memory limit for the container                                          | `4Gi`                            |
| `pod.container.resources.requests.cpu`                 | CPU request for the container                                           | `100m`                           |
| `pod.container.resources.requests.memory`              | Memory request for the container                                       | `512Mi`                          |
| `pod.nodeSelector`                                     | Node selector for the pod                                               | `{}`                             |
| `pod.tolerations`                                      | Tolerations for the pod                                                 | `[]`                             |
| `pod.affinity`                                         | Affinity for the pod                                                    | `{}`                             |
| `pod.disruptionBudget.enabled`                         | Enable Pod Disruption Budget                                            | `true`                           |
| `pod.disruptionBudget.maxUnavailable`                  | Maximum unavailable pods                                                | `1`                              |
| `pod.disruptionBudget.minAvailable`                    | Minimum available pods                                                  | `0`                              |
| `pod.autoscaling.enabled`                              | Enable autoscaling                                                      | `false`                          |
| `pod.autoscaling.minReplicas`                          | Minimum replicas for autoscaling                                        | `1`                              |
| `pod.autoscaling.maxReplicas`                          | Maximum replicas for autoscaling                                        | `4`                              |
| `pod.autoscaling.targetCPUUtilizationPercentage`       | Target CPU utilization percentage for autoscaling                      | `50`                             |
| `pod.autoscaling.customMetrics`                        | Custom metrics for autoscaling                                          | `[]`                             |
| `service.type`                                         | Type of the service                                                     | `ClusterIP`                      |
| `service.httpPort`                                     | HTTP port for the service                                               | `80`                             |
| `service.httpNodePort`                                 | HTTP NodePort for the service                                           | `""`                             |
| `service.httpsPort`                                    | HTTPS port for the service                                              | `443`                            |
| `service.httpsNodePort`                                | HTTPS NodePort for the service                                          | `""`                             |
| `service.annotations`                                  | Annotations for the service                                             | `{}`                             |
| `networkPolicy.enabled`                                | Enable NetworkPolicy                                                    | `true`                           |
| `networkPolicy.policyTypes`                            | Types of network policies                                               | `["Egress"]`                     |
| `networkPolicy.egress`                                 | Egress rules for the NetworkPolicy                                      | `[]`                             |
| `networkPolicy.ingress`                                | Ingress rules for the NetworkPolicy                                     | `[]`                             |
| `http.hosts`                                           | HTTP hosts configuration                                                | `{}`                             |
| `ingress.enabled`                                      | Enable Ingress                                                          | `false`                          |
| `ingress.className`                                    | Ingress class name                                                      | `""`                             |
| `ingress.targetServicePortName`                        | Target service port name for Ingress                                    | `http`                           |
| `ingress.annotations`                                  | Annotations for Ingress                                                 | `{}`                             |
| `openshift.route.enabled`                              | Enable OpenShift Route                                                  | `false`                          |
| `openshift.route.annotations`                          | Annotations for OpenShift Route                                         | `{}`                             |
| `openshift.route.tls.termination`                      | TLS termination for OpenShift Route                                     | `reencrypt`                      |
| `openshift.route.tls.insecureEdgeTerminationPolicy`    | Insecure edge termination policy for OpenShift Route                   | `Redirect`                       |

## Usage

### Runtime Configuration

The application is configured using runtime configurations specified in `config`. Default values can be overridden by specifying custom values.

### Labels and Annotations

Labels and annotations can be added to various resource types such as pods and services. These can be specified under the `labels` and `annotations` sections.

### Pod Configuration

The pod configuration allows setting various parameters such as the number of replicas, security context, resource requests, and limits. Additional volumes and volume mounts can also be specified.

### Service Configuration

Service configuration includes setting the type of service (e.g., `ClusterIP`), ports, and annotations.

### Network Policies

Network policies can be configured to control the ingress and egress traffic to the application.

### Ingress and OpenShift Route

Ingress and OpenShift Route configurations are provided to expose the application externally.

### Autoscaling

Autoscaling can be enabled to automatically adjust the number of replicas based on CPU utilization or custom metrics.

## License

MIT
