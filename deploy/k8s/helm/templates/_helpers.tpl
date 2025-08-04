{{/*
Expand the name of the chart.
*/}}
{{- define "bytedesk.name" -}}
{{- default .Chart.Name .Values.nameOverride | trunc 63 | trimSuffix "-" }}
{{- end }}

{{/*
Create a default fully qualified app name.
We truncate at 63 chars because some Kubernetes name fields are limited to this (by the DNS naming spec).
If release name contains chart name it will be used as a full name.
*/}}
{{- define "bytedesk.fullname" -}}
{{- if .Values.fullnameOverride }}
{{- .Values.fullnameOverride | trunc 63 | trimSuffix "-" }}
{{- else }}
{{- $name := default .Chart.Name .Values.nameOverride }}
{{- if contains $name .Release.Name }}
{{- .Release.Name | trunc 63 | trimSuffix "-" }}
{{- else }}
{{- printf "%s-%s" .Release.Name $name | trunc 63 | trimSuffix "-" }}
{{- end }}
{{- end }}
{{- end }}

{{/*
Create chart name and version as used by the chart label.
*/}}
{{- define "bytedesk.chart" -}}
{{- printf "%s-%s" .Chart.Name .Chart.Version | replace "+" "_" | trunc 63 | trimSuffix "-" }}
{{- end }}

{{/*
Common labels
*/}}
{{- define "bytedesk.labels" -}}
helm.sh/chart: {{ include "bytedesk.chart" . }}
{{ include "bytedesk.selectorLabels" . }}
{{- if .Chart.AppVersion }}
app.kubernetes.io/version: {{ .Chart.AppVersion | quote }}
{{- end }}
app.kubernetes.io/managed-by: {{ .Release.Service }}
{{- end }}

{{/*
Selector labels
*/}}
{{- define "bytedesk.selectorLabels" -}}
app.kubernetes.io/name: {{ include "bytedesk.name" . }}
app.kubernetes.io/instance: {{ .Release.Name }}
{{- end }}

{{/*
Create the name of the service account to use
*/}}
{{- define "bytedesk.serviceAccountName" -}}
{{- if .Values.serviceAccount.create }}
{{- default (include "bytedesk.fullname" .) .Values.serviceAccount.name }}
{{- else }}
{{- default "default" .Values.serviceAccount.name }}
{{- end }}
{{- end }}

{{/*
Create image name
*/}}
{{- define "bytedesk.image" -}}
{{- if .Values.global.imageRegistry }}
{{- printf "%s/%s:%s" .Values.global.imageRegistry .Values.bytedesk.image.repository .Values.bytedesk.image.tag }}
{{- else }}
{{- printf "%s:%s" .Values.bytedesk.image.repository .Values.bytedesk.image.tag }}
{{- end }}
{{- end }}

{{/*
Create MySQL image name
*/}}
{{- define "mysql.image" -}}
{{- printf "%s:%s" .Values.mysql.image.repository .Values.mysql.image.tag }}
{{- end }}

{{/*
Create Redis image name
*/}}
{{- define "redis.image" -}}
{{- printf "%s:%s" .Values.redis.image.repository .Values.redis.image.tag }}
{{- end }}

{{/*
Create Elasticsearch image name
*/}}
{{- define "elasticsearch.image" -}}
{{- printf "%s:%s" .Values.elasticsearch.image.repository .Values.elasticsearch.image.tag }}
{{- end }}

{{/*
Create Artemis image name
*/}}
{{- define "artemis.image" -}}
{{- printf "%s:%s" .Values.artemis.image.repository .Values.artemis.image.tag }}
{{- end }}

{{/*
Create MinIO image name
*/}}
{{- define "minio.image" -}}
{{- printf "%s:%s" .Values.minio.image.repository .Values.minio.image.tag }}
{{- end }}

{{/*
Create Zipkin image name
*/}}
{{- define "zipkin.image" -}}
{{- printf "%s:%s" .Values.zipkin.image.repository .Values.zipkin.image.tag }}
{{- end }} 