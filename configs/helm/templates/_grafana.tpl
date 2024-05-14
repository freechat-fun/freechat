{{- define "prometheus.ini" -}}
apiVersion: 1
datasources:
- name: Prometheus
  type: prometheus
  access: proxy
  orgId: 1
  url: http://{{ include "helpers.fullname" . }}-prometheus-server
  version: 1
  editable: true
  isDefault: true
- name: Alertmanager
  uid: alertmanager
  type: alertmanager
  access: proxy
  orgId: 1
  url: http://{{ include "helpers.fullname" . }}-prometheus-alertmanager
  version: 1
  editable: true
{{ end }}
