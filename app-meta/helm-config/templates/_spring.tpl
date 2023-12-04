{{- define "application-private.yml" -}}
auth:
  aes:
    key: {{ .Values.auth.aes.key | quote }}
spring:
  datasource:
    {{- with .Values.mysql }}
    url: {{ default "jdbc:mysql://mysql:3306/freechat?useUnicode=true&characterEncoding=utf-8" .url }}
    username: {{ default "root" .auth.username }}
    password: {{ default .auth.rootPassword .auth.password }}
    {{- end }}
  {{- if .Values.auth.oauth2 }}
  security:
    oauth2:
      client:
        registration:
          {{- if and .Values.auth.oauth2.aliyun .Values.auth.oauth2.aliyun.enabled }}
          {{- with .Values.auth.oauth2.aliyun }}
          aliyun:
            authorizationGrantType: {{ default "authorization_code" .authorizationGrantType }}
            clientAuthenticationMethod: {{ default "client_secret_basic" .clientAuthenticationMethod }}
            clientName: {{ default "Aliyun" .clientName }}
            scope: {{ default "openid" .scope }}
            clientId: {{ .clientId | quote }}
            clientSecret: {{ .clientSecret | quote }}
          {{- end }}
          {{- end }}
          {{- if and .Values.auth.oauth2.google .Values.auth.oauth2.google.enabled }}
          {{- with .Values.auth.oauth2.google }}
          google:
            authorizationGrantType: {{ default "authorization_code" .authorizationGrantType }}
            clientAuthenticationMethod: {{ default "client_secret_basic" .clientAuthenticationMethod }}
            clientName: {{ default "Google" .clientName }}
            scope: {{ default "openid,profile,email" .scope }}
            clientId: {{ .clientId | quote }}
            clientSecret: {{ .clientSecret | quote }}
          {{- end }}
          {{- end }}
          {{- if and .Values.auth.oauth2.google .Values.auth.oauth2.google.enabled }}
          {{- with .Values.auth.oauth2.google }}
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
  mode: cluster
  datasource:
    url: {{ default "redis://redis:6379" .Values.redis.url }}
    {{- $redisPassword := "" }}
    {{- if .Values.global.redis.password }}
    {{- $redisPassword = .Values.global.redis.password }}
    {{- else if .Values.redis.auth.password }}
    {{- $redisPassword = .Values.redis.auth.password }}
    {{- end }}
    password: {{ $redisPassword }}
{{- end }}
