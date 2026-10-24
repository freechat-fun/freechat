

# ConnectorWriteDTO

Private remote MCP configuration. Saves validate syntax only; no network requests are made.

## Properties

| Name | Type | Description | Notes |
|------------ | ------------- | ------------- | -------------|
|**name** | **String** | Printable display name, 1–64 characters |  [optional] |
|**transport** | [**TransportEnum**](#TransportEnum) | Remote HTTP or SSE only |  [optional] |
|**url** | **String** | HTTP(S) base URL, at most 2048 characters; no credentials, query, or fragment |  [optional] |
|**timeout** | **Integer** | Timeout in milliseconds, 1000–120000 |  [optional] |
|**headers** | **Map&lt;String, String&gt;** | New header values, HTTPS only. At most 16 headers and 16 KiB total. Values are never returned. |  [optional] |
|**keepHeaderNames** | **List&lt;String&gt;** | Stored header names to retain on update; cannot overlap new headers or move to another origin |  [optional] |
|**expectedRevision** | **String** | Current revision required on update; omit on create |  [optional] |
|**active** | **Boolean** |  |  [optional] |



## Enum: TransportEnum

| Name | Value |
|---- | -----|
| HTTP | &quot;http&quot; |
| SSE | &quot;sse&quot; |
| UNKNOWN_DEFAULT_OPEN_API | &quot;unknown_default_open_api&quot; |



