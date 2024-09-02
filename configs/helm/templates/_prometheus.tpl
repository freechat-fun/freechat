{{/*
Return the prometheus scrape configuration for kubernetes objects.
Usage:
{{ include "prometheus.extraScrapeConfig" (dict "name" "mysql" "component" "metrics" "port" "metrics" "context" $) }}
*/}}
{{- define "prometheus.extraScrapeConfig" -}}
- job_name: {{ .name }}
  kubernetes_sd_configs:
    - role: endpoints
      namespaces:
        names:
          - {{ .context.Release.Namespace }}
  metrics_path: /metrics
  relabel_configs:
    - source_labels:
        - job
      target_label: __tmp_{{ regexReplaceAllLiteral "-" .name "_" }}_job_name
    {{- if .component }}
    - action: keep
      source_labels:
        - __meta_kubernetes_service_label_app_kubernetes_io_component
        - __meta_kubernetes_service_labelpresent_app_kubernetes_io_component
      regex: ({{ .component }});true
    {{- end }}
    - action: keep
      source_labels:
        - __meta_kubernetes_service_label_app_kubernetes_io_instance
        - __meta_kubernetes_service_labelpresent_app_kubernetes_io_instance
      regex: ({{ .context.Release.Name }});true
    - action: keep
      source_labels:
        - __meta_kubernetes_service_label_app_kubernetes_io_name
        - __meta_kubernetes_service_labelpresent_app_kubernetes_io_name
      regex: ({{ .name }});true
    - action: keep
      source_labels:
        - __meta_kubernetes_endpoint_port_name
      regex: ({{ .port }})
    - source_labels:
        - __meta_kubernetes_endpoint_address_target_kind
        - __meta_kubernetes_endpoint_address_target_name
      separator: ;
      regex: Node;(.*)
      replacement: ${1}
      target_label: node
    - source_labels:
        - __meta_kubernetes_endpoint_address_target_kind
        - __meta_kubernetes_endpoint_address_target_name
      separator: ;
      regex: Pod;(.*)
      replacement: ${1}
      target_label: pod
    - source_labels:
        - __meta_kubernetes_namespace
      target_label: namespace
    - source_labels:
        - __meta_kubernetes_namespace
      action: replace
      target_label: k8s_namespace
    - source_labels:
        - __meta_kubernetes_service_name
      target_label: service
    - source_labels:
        - __meta_kubernetes_pod_name
      target_label: pod
    - source_labels:
        - __meta_kubernetes_pod_container_name
      target_label: container
    - action: labelmap
      regex: __meta_kubernetes_service_label_(.+)
    - action: drop
      source_labels:
        - __meta_kubernetes_pod_phase
      regex: (Failed|Succeeded)
    - source_labels:
        - __meta_kubernetes_service_name
      target_label: job
    - target_label: endpoint
      replacement: metrics
    - target_label: cluster
      replacement: linuxea
    - source_labels:
        - __address__
      target_label: __tmp_hash
      modulus: 1
      action: hashmod
    - source_labels:
        - __tmp_hash
      regex: 0
      action: keep
{{- end }}