{{/*
Expand the name of the chart.
*/}}

{{/*
Selector labels
*/}}
{{- define "deploy.selectorLabels" -}}
app.kubernetes.io/name: batch-admin-bo
{{- end }}
