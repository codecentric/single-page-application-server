{{/*
Expand the name of the Release.
We truncate at 63 chars because some Kubernetes name fields are limited to this (by the DNS naming spec).
*/}}
{{- define "single-page-application-server.name" -}}
{{- .Values.nameOverride | default .Release.Name | trunc 63 | trimSuffix "-" }}
{{- end }}

{{/*
Fully qualified application name.
We truncate at 63 chars because some Kubernetes name fields are limited to this (by the DNS naming spec).
*/}}
{{- define "single-page-application-server.fullname" -}}
{{- .Values.fullnameOverride | default .Release.Name | trunc 63 | trimSuffix "-" }}
{{- end }}

{{/*
Create chart name and version as used by the chart label.
*/}}
{{- define "single-page-application-server.chart" -}}
{{- printf "%s-%s" .Chart.Name .Chart.Version | replace "+" "_" | trunc 63 | trimSuffix "-" }}
{{- end }}

{{/*
Common labels
*/}}
{{- define "single-page-application-server.labels" -}}
helm.sh/chart: {{ include "single-page-application-server.chart" . }}
{{ include "single-page-application-server.selectorLabels" . }}
app.kubernetes.io/version: {{ ( "" | or (index .Values.labels "app.kubernetes.io/version")) | default (.Values.pod.container.image.tag | required "image.tag is required!") | quote }}
app.kubernetes.io/managed-by: {{ .Release.Service }}
{{ toYaml .Values.labels }}
{{- end }}

{{/*
Selector labels
*/}}
{{- define "single-page-application-server.selectorLabels" -}}
app.kubernetes.io/name: {{ include "single-page-application-server.name" . | quote }}
{{- if (eq (include "single-page-application-server.name" . ) .Release.Name) }}
app.kubernetes.io/instance: "default"
{{- else }}
app.kubernetes.io/instance: {{ .Release.Name | quote }}
{{- end}}
{{- end }}
