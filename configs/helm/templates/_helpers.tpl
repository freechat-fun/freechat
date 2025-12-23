{{/*
Expand the name of the chart.
*/}}
{{- define "helpers.name" -}}
{{- default .Chart.Name .Values.nameOverride | trunc 63 | trimSuffix "-" -}}
{{- end -}}

{{/*
Create a default fully qualified app name.
We truncate at 63 chars because some Kubernetes name fields are limited to this (by the DNS naming spec).
If release name contains chart name it will be used as a full name.
*/}}
{{- define "helpers.fullname" -}}
{{- if .Values.fullnameOverride -}}
{{- .Values.fullnameOverride | trunc 63 | trimSuffix "-" -}}
{{- else -}}
{{- $name := default .Chart.Name .Values.nameOverride -}}
{{- if contains $name .Release.Name -}}
{{- .Release.Name | trunc 63 | trimSuffix "-" -}}
{{- else -}}
{{- printf "%s-%s" .Release.Name $name | trunc 63 | trimSuffix "-" -}}
{{- end -}}
{{- end -}}
{{- end -}}

{{/*
Allow the release namespace to be overridden for multi-namespace deployments in combined charts.
*/}}
{{- define "helpers.namespace" -}}
{{- default .Release.Namespace .Values.namespaceOverride | trunc 63 | trimSuffix "-" -}}
{{- end -}}

{{/*
Create chart name and version as used by the chart label.
*/}}
{{- define "helpers.chart" -}}
{{- printf "%s-%s" .Chart.Name .Chart.Version | replace "+" "_" | trunc 63 | trimSuffix "-" -}}
{{- end -}}

{{/*
Common labels
*/}}
{{- define "helpers.labels" -}}
helm.sh/chart: {{ include "helpers.chart" . }}
{{ include "helpers.selectorLabels" . }}
{{- if .Chart.AppVersion }}
app.kubernetes.io/version: {{ .Chart.AppVersion | quote }}
{{- end }}
app.kubernetes.io/managed-by: {{ .Release.Service }}
{{- end -}}

{{/*
Selector labels
*/}}
{{- define "helpers.selectorLabels" -}}
app.kubernetes.io/name: {{ include "helpers.name" . }}
app.kubernetes.io/instance: {{ .Release.Name }}
{{- end -}}

{{/*
Create the name of the service account to use
*/}}
{{- define "helpers.serviceAccountName" -}}
{{- if .Values.serviceAccount.create -}}
{{- default (include "helpers.fullname" .) .Values.serviceAccount.name -}}
{{- else -}}
{{- default "default" .Values.serviceAccount.name -}}
{{- end -}}
{{- end -}}

{{/*
Extract "host port" from URL
*/}}
{{- define "helpers.extractHostPort" -}}
{{- $u := urlParse . -}}

{{- /* handle opaque URLs like http:a.com:80 */ -}}
{{- if and (empty $u.host) $u.opaque -}}
{{- $u = urlParse $u.opaque -}}
{{- end -}}

{{- $host := $u.host -}}
{{- $port := "" -}}

{{- if contains ":" $host -}}
{{- $parts := splitList ":" $host -}}
{{- $host = index $parts 0 -}}
{{- $port = index $parts 1 -}}
{{- else -}}
{{- if eq $u.scheme "https" -}}
{{- $port = "443" -}}
{{- else -}}
{{- $port = "80" -}}
{{- end -}}
{{- end -}}

{{- printf "%s %s" $host $port -}}
{{- end -}}

{{/*
Resolve storageClassName
*/}}
{{- define "helpers.storageClass" -}}
{{- $storageClass := "-" -}}

{{- if .Values.backend.persistence -}}
{{- if .Values.backend.persistence.storageClass -}}
{{- $storageClass = .Values.backend.persistence.storageClass -}}
{{- end -}}
{{- end -}}

{{- if .Values.global -}}
{{- if .Values.global.defaultStorageClass -}}
{{- $storageClass = .Values.global.defaultStorageClass -}}
{{- end -}}
{{- end -}}

{{- if $storageClass -}}
{{- if eq "-" $storageClass -}}
{{- printf "storageClassName: \"\"" -}}
{{- else -}}
{{- printf "storageClassName: %s" $storageClass -}}
{{- end -}}
{{- end -}}
{{- end -}}

{{/*
Render values that may contain templates
*/}}
{{- define "helpers.tplvalues.render" -}}
{{- if typeIs "string" .value -}}
{{- tpl .value .context -}}
{{- else -}}
{{- tpl (.value | toYaml) .context -}}
{{- end -}}
{{- end -}}

{{/*
Merge a list of values after rendering templates.
Usage:
{{ include "helpers.tplvalues.merge" (dict "values" (list .Values.a .Values.b) "context" $) }}
*/}}
{{- define "helpers.tplvalues.merge" -}}
{{- $dst := dict -}}
{{- range .values -}}
{{- $dst = include "helpers.tplvalues.render" (dict "value" . "context" $.context "scope" $.scope) | fromYaml | merge $dst -}}
{{- end -}}
{{- $dst | toYaml -}}
{{- end -}}

{{/*
Labels used on immutable fields such as deploy.spec.selector.matchLabels or svc.spec.selector
*/}}
{{- define "helpers.matchLabels" -}}
{{- if and (hasKey . "customLabels") (hasKey . "context") -}}
{{- merge
    (pick
      (include "helpers.tplvalues.render" (dict "value" .customLabels "context" .context) | fromYaml)
      "app.kubernetes.io/name"
      "app.kubernetes.io/instance")
    (dict
      "app.kubernetes.io/name" (include "helpers.name" .context)
      "app.kubernetes.io/instance" .context.Release.Name)
  | toYaml -}}
{{- else -}}
{{ include "helpers.selectorLabels" }}
{{- end -}}
{{- end -}}
