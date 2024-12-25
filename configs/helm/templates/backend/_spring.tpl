{{- define "application-online.yml" -}}
{{- if .Values.app.icpCode }}
app:
  icpCode: {{ .Values.app.icpCode | quote }}
  homeUrl: {{ printf "https://%s" (index .Values.backend.ingress.hosts 0).host | default "http://localhost:8080" }}
{{- end }}
auth:
  aes:
    key: {{ .Values.backend.auth.aes.key | quote }}
spring:
  datasource:
    {{- with .Values.mysql }}
    url: {{ default "jdbc:mysql://mysql:3306/freechat?useUnicode=true&characterEncoding=utf-8" .url }}
    username: {{ default "root" .auth.username }}
    password: {{ default .auth.rootPassword .auth.password }}
    {{- end }}
  {{- if .Values.backend.auth.oauth2 }}
  security:
    oauth2:
      client:
        registration:
          {{- if and .Values.backend.auth.oauth2.aliyun .Values.backend.auth.oauth2.aliyun.enabled }}
          {{- with .Values.backend.auth.oauth2.aliyun }}
          aliyun:
            authorizationGrantType: {{ default "authorization_code" .authorizationGrantType }}
            clientAuthenticationMethod: {{ default "client_secret_basic" .clientAuthenticationMethod }}
            clientName: {{ default "Aliyun" .clientName }}
            scope: {{ default "openid" .scope }}
            clientId: {{ .clientId | quote }}
            clientSecret: {{ .clientSecret | quote }}
          {{- end }}
          {{- end }}
          {{- if and .Values.backend.auth.oauth2.google .Values.backend.auth.oauth2.google.enabled }}
          {{- with .Values.backend.auth.oauth2.google }}
          google:
            authorizationGrantType: {{ default "authorization_code" .authorizationGrantType }}
            clientAuthenticationMethod: {{ default "client_secret_basic" .clientAuthenticationMethod }}
            clientName: {{ default "Google" .clientName }}
            scope: {{ default "openid,profile,email" .scope }}
            clientId: {{ .clientId | quote }}
            clientSecret: {{ .clientSecret | quote }}
          {{- end }}
          {{- end }}
          {{- if and .Values.backend.auth.oauth2.github .Values.backend.auth.oauth2.github.enabled }}
          {{- with .Values.backend.auth.oauth2.github }}
          github:
            authorizationGrantType: {{ default "authorization_code" .authorizationGrantType }}
            clientAuthenticationMethod: {{ default "client_secret_basic" .clientAuthenticationMethod }}
            clientName: {{ default "GitHub" .clientName }}
            scope: {{ default "read:user" .scope }}
            clientId: {{ .clientId | quote }}
            clientSecret: {{ .clientSecret | quote }}
          {{- end }}
          {{- end }}
  {{- end }}
  {{- if .Values.spring }}
  {{- toYaml .Values.spring | nindent 2 }}
  {{- end }}
redis:
  datasource:
    url: {{ default "redis://redis:6379" .Values.redis.url }}
    {{- $redisPassword := "" }}
    {{- if .Values.global.redis.password }}
    {{- $redisPassword = .Values.global.redis.password }}
    {{- else if .Values.redis.auth.password }}
    {{- $redisPassword = .Values.redis.auth.password }}
    {{- end }}
    password: {{ $redisPassword }}
embedding:
  milvus:
    {{- with .Values.milvus }}
    url: {{ default "http://milvus:19530" .url }}
    {{- if .milvus.auth.enabled }}
    {{- if .milvus.auth.token }}
    token: {{ .milvus.auth.token }}
    {{- else }}
    username: root
    password: {{ default "Milvus" .milvus.auth.rootPassword }}
    {{- end }}
    {{- end }}
    {{- end }}
{{ end }}
