# freechat_sdk.PromptApi

All URIs are relative to *http://127.0.0.1:8080*

Method | HTTP request | Description
------------- | ------------- | -------------
[**apply_prompt_ref**](PromptApi.md#apply_prompt_ref) | **POST** /api/v1/prompt/apply/ref | Apply Parameters to Prompt Record
[**apply_prompt_template**](PromptApi.md#apply_prompt_template) | **POST** /api/v1/prompt/apply/template | Apply Parameters to Prompt Template
[**batch_search_prompt_details**](PromptApi.md#batch_search_prompt_details) | **POST** /api/v1/prompt/batch/details/search | Batch Search Prompt Details
[**batch_search_prompt_summary**](PromptApi.md#batch_search_prompt_summary) | **POST** /api/v1/prompt/batch/search | Batch Search Prompt Summaries
[**clone_prompt**](PromptApi.md#clone_prompt) | **POST** /api/v1/prompt/clone/{promptId} | Clone Prompt
[**clone_prompts**](PromptApi.md#clone_prompts) | **POST** /api/v1/prompt/batch/clone | Batch Clone Prompts
[**count_prompts**](PromptApi.md#count_prompts) | **POST** /api/v1/prompt/count | Calculate Number of Prompts
[**create_prompt**](PromptApi.md#create_prompt) | **POST** /api/v1/prompt | Create Prompt
[**create_prompts**](PromptApi.md#create_prompts) | **POST** /api/v1/prompt/batch | Batch Create Prompts
[**delete_prompt**](PromptApi.md#delete_prompt) | **DELETE** /api/v1/prompt/{promptId} | Delete Prompt
[**delete_prompt_by_name**](PromptApi.md#delete_prompt_by_name) | **DELETE** /api/v1/prompt/name/{name} | Delete Prompt by Name
[**delete_prompts**](PromptApi.md#delete_prompts) | **DELETE** /api/v1/prompt/batch | Batch Delete Prompts
[**exists_name**](PromptApi.md#exists_name) | **GET** /api/v1/prompt/exists/name/{name} | Check If Name Exists
[**get_prompt_details**](PromptApi.md#get_prompt_details) | **GET** /api/v1/prompt/details/{promptId} | Get Prompt Details
[**get_prompt_summary**](PromptApi.md#get_prompt_summary) | **GET** /api/v1/prompt/summary/{promptId} | Get Prompt Summary
[**list_prompt_versions_by_name**](PromptApi.md#list_prompt_versions_by_name) | **POST** /api/v1/prompt/versions/{name} | List Versions by Prompt Name
[**publish_prompt**](PromptApi.md#publish_prompt) | **POST** /api/v1/prompt/publish/{promptId}/{visibility} | Publish Prompt
[**search_prompt_details**](PromptApi.md#search_prompt_details) | **POST** /api/v1/prompt/details/search | Search Prompt Details
[**search_prompt_summary**](PromptApi.md#search_prompt_summary) | **POST** /api/v1/prompt/search | Search Prompt Summary
[**send_prompt**](PromptApi.md#send_prompt) | **POST** /api/v1/prompt/send | Send Prompt
[**stream_send_prompt**](PromptApi.md#stream_send_prompt) | **POST** /api/v1/prompt/send/stream | Send Prompt by Streaming Back
[**update_prompt**](PromptApi.md#update_prompt) | **PUT** /api/v1/prompt/{promptId} | Update Prompt


# **apply_prompt_ref**
> str apply_prompt_ref(prompt_ref_dto)

Apply Parameters to Prompt Record

Apply parameters to prompt record.

### Example

* Bearer Authentication (bearerAuth):

```python
import time
import os
import freechat_sdk
from freechat_sdk.models.prompt_ref_dto import PromptRefDTO
from freechat_sdk.rest import ApiException
from pprint import pprint

# Defining the host is optional and defaults to http://127.0.0.1:8080
# See configuration.py for a list of all supported configuration parameters.
configuration = freechat_sdk.Configuration(
    host = "http://127.0.0.1:8080"
)

# The client must configure the authentication and authorization parameters
# in accordance with the API server security policy.
# Examples for each auth method are provided below, use the example that
# satisfies your auth use case.

# Configure Bearer authorization: bearerAuth
configuration = freechat_sdk.Configuration(
    access_token = os.environ["BEARER_TOKEN"]
)

# Enter a context with an instance of the API client
with freechat_sdk.ApiClient(configuration) as api_client:
    # Create an instance of the API class
    api_instance = freechat_sdk.PromptApi(api_client)
    prompt_ref_dto = freechat_sdk.PromptRefDTO() # PromptRefDTO | Prompt record

    try:
        # Apply Parameters to Prompt Record
        api_response = api_instance.apply_prompt_ref(prompt_ref_dto)
        print("The response of PromptApi->apply_prompt_ref:\n")
        pprint(api_response)
    except Exception as e:
        print("Exception when calling PromptApi->apply_prompt_ref: %s\n" % e)
```



### Parameters


Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **prompt_ref_dto** | [**PromptRefDTO**](PromptRefDTO.md)| Prompt record | 

### Return type

**str**

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

 - **Content-Type**: application/json
 - **Accept**: text/plain

### HTTP response details

| Status code | Description | Response headers |
|-------------|-------------|------------------|
**200** | OK |  -  |

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)

# **apply_prompt_template**
> str apply_prompt_template(prompt_template_dto)

Apply Parameters to Prompt Template

Apply parameters to prompt template.

### Example

* Bearer Authentication (bearerAuth):

```python
import time
import os
import freechat_sdk
from freechat_sdk.models.prompt_template_dto import PromptTemplateDTO
from freechat_sdk.rest import ApiException
from pprint import pprint

# Defining the host is optional and defaults to http://127.0.0.1:8080
# See configuration.py for a list of all supported configuration parameters.
configuration = freechat_sdk.Configuration(
    host = "http://127.0.0.1:8080"
)

# The client must configure the authentication and authorization parameters
# in accordance with the API server security policy.
# Examples for each auth method are provided below, use the example that
# satisfies your auth use case.

# Configure Bearer authorization: bearerAuth
configuration = freechat_sdk.Configuration(
    access_token = os.environ["BEARER_TOKEN"]
)

# Enter a context with an instance of the API client
with freechat_sdk.ApiClient(configuration) as api_client:
    # Create an instance of the API class
    api_instance = freechat_sdk.PromptApi(api_client)
    prompt_template_dto = freechat_sdk.PromptTemplateDTO() # PromptTemplateDTO | String type prompt template

    try:
        # Apply Parameters to Prompt Template
        api_response = api_instance.apply_prompt_template(prompt_template_dto)
        print("The response of PromptApi->apply_prompt_template:\n")
        pprint(api_response)
    except Exception as e:
        print("Exception when calling PromptApi->apply_prompt_template: %s\n" % e)
```



### Parameters


Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **prompt_template_dto** | [**PromptTemplateDTO**](PromptTemplateDTO.md)| String type prompt template | 

### Return type

**str**

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

 - **Content-Type**: application/json
 - **Accept**: text/plain

### HTTP response details

| Status code | Description | Response headers |
|-------------|-------------|------------------|
**200** | OK |  -  |

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)

# **batch_search_prompt_details**
> List[List[PromptDetailsDTO]] batch_search_prompt_details(prompt_query_dto)

Batch Search Prompt Details

Batch call shortcut for /api/v1/prompt/details/search.

### Example

* Bearer Authentication (bearerAuth):

```python
import time
import os
import freechat_sdk
from freechat_sdk.models.prompt_details_dto import PromptDetailsDTO
from freechat_sdk.models.prompt_query_dto import PromptQueryDTO
from freechat_sdk.rest import ApiException
from pprint import pprint

# Defining the host is optional and defaults to http://127.0.0.1:8080
# See configuration.py for a list of all supported configuration parameters.
configuration = freechat_sdk.Configuration(
    host = "http://127.0.0.1:8080"
)

# The client must configure the authentication and authorization parameters
# in accordance with the API server security policy.
# Examples for each auth method are provided below, use the example that
# satisfies your auth use case.

# Configure Bearer authorization: bearerAuth
configuration = freechat_sdk.Configuration(
    access_token = os.environ["BEARER_TOKEN"]
)

# Enter a context with an instance of the API client
with freechat_sdk.ApiClient(configuration) as api_client:
    # Create an instance of the API class
    api_instance = freechat_sdk.PromptApi(api_client)
    prompt_query_dto = [{"where":{"visibility":"public","username":"amin","text":"robot"},"orderBy":["version","modifyTime asc"],"pageNum":1,"pageSize":1},{"where":{"visibility":"private","name":"A Test"}},{"where":{"visibility":"private","tags":["test1"]}},{"where":{"visibility":"public","username":"amin","name":"Second Test","text":"robot","tags":["robot"],"aiModels":["123"]},"orderBy":["version","modifyTime asc"],"pageNum":0,"pageSize":1}] # List[PromptQueryDTO] | Query conditions

    try:
        # Batch Search Prompt Details
        api_response = api_instance.batch_search_prompt_details(prompt_query_dto)
        print("The response of PromptApi->batch_search_prompt_details:\n")
        pprint(api_response)
    except Exception as e:
        print("Exception when calling PromptApi->batch_search_prompt_details: %s\n" % e)
```



### Parameters


Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **prompt_query_dto** | [**List[PromptQueryDTO]**](PromptQueryDTO.md)| Query conditions | 

### Return type

**List[List[PromptDetailsDTO]]**

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

 - **Content-Type**: application/json
 - **Accept**: application/json

### HTTP response details

| Status code | Description | Response headers |
|-------------|-------------|------------------|
**200** | OK |  -  |

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)

# **batch_search_prompt_summary**
> List[List[PromptSummaryDTO]] batch_search_prompt_summary(prompt_query_dto)

Batch Search Prompt Summaries

Batch call shortcut for /api/v1/prompt/search.

### Example

* Bearer Authentication (bearerAuth):

```python
import time
import os
import freechat_sdk
from freechat_sdk.models.prompt_query_dto import PromptQueryDTO
from freechat_sdk.models.prompt_summary_dto import PromptSummaryDTO
from freechat_sdk.rest import ApiException
from pprint import pprint

# Defining the host is optional and defaults to http://127.0.0.1:8080
# See configuration.py for a list of all supported configuration parameters.
configuration = freechat_sdk.Configuration(
    host = "http://127.0.0.1:8080"
)

# The client must configure the authentication and authorization parameters
# in accordance with the API server security policy.
# Examples for each auth method are provided below, use the example that
# satisfies your auth use case.

# Configure Bearer authorization: bearerAuth
configuration = freechat_sdk.Configuration(
    access_token = os.environ["BEARER_TOKEN"]
)

# Enter a context with an instance of the API client
with freechat_sdk.ApiClient(configuration) as api_client:
    # Create an instance of the API class
    api_instance = freechat_sdk.PromptApi(api_client)
    prompt_query_dto = [{"where":{"visibility":"public","username":"amin","text":"robot"},"orderBy":["version","modifyTime asc"],"pageNum":1,"pageSize":1},{"where":{"visibility":"private","name":"A Test"}},{"where":{"visibility":"private","tags":["test1"]}},{"where":{"visibility":"public","username":"amin","name":"Second Test","text":"robot","tags":["robot"],"aiModels":["123"]},"orderBy":["version","modifyTime asc"],"pageNum":0,"pageSize":1}] # List[PromptQueryDTO] | Query conditions

    try:
        # Batch Search Prompt Summaries
        api_response = api_instance.batch_search_prompt_summary(prompt_query_dto)
        print("The response of PromptApi->batch_search_prompt_summary:\n")
        pprint(api_response)
    except Exception as e:
        print("Exception when calling PromptApi->batch_search_prompt_summary: %s\n" % e)
```



### Parameters


Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **prompt_query_dto** | [**List[PromptQueryDTO]**](PromptQueryDTO.md)| Query conditions | 

### Return type

**List[List[PromptSummaryDTO]]**

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

 - **Content-Type**: application/json
 - **Accept**: application/json

### HTTP response details

| Status code | Description | Response headers |
|-------------|-------------|------------------|
**200** | OK |  -  |

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)

# **clone_prompt**
> str clone_prompt(prompt_id)

Clone Prompt

Enter the promptId, generate a new record, the content is basically the same as the original prompt, but the following fields are different: - Version number is 1 - Visibility is private - The parent prompt is the source promptId - The creation time is the current moment. - All statistical indicators are zeroed.  Return the new promptId. 

### Example

* Bearer Authentication (bearerAuth):

```python
import time
import os
import freechat_sdk
from freechat_sdk.rest import ApiException
from pprint import pprint

# Defining the host is optional and defaults to http://127.0.0.1:8080
# See configuration.py for a list of all supported configuration parameters.
configuration = freechat_sdk.Configuration(
    host = "http://127.0.0.1:8080"
)

# The client must configure the authentication and authorization parameters
# in accordance with the API server security policy.
# Examples for each auth method are provided below, use the example that
# satisfies your auth use case.

# Configure Bearer authorization: bearerAuth
configuration = freechat_sdk.Configuration(
    access_token = os.environ["BEARER_TOKEN"]
)

# Enter a context with an instance of the API client
with freechat_sdk.ApiClient(configuration) as api_client:
    # Create an instance of the API class
    api_instance = freechat_sdk.PromptApi(api_client)
    prompt_id = 'prompt_id_example' # str | The referenced promptId

    try:
        # Clone Prompt
        api_response = api_instance.clone_prompt(prompt_id)
        print("The response of PromptApi->clone_prompt:\n")
        pprint(api_response)
    except Exception as e:
        print("Exception when calling PromptApi->clone_prompt: %s\n" % e)
```



### Parameters


Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **prompt_id** | **str**| The referenced promptId | 

### Return type

**str**

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: text/plain

### HTTP response details

| Status code | Description | Response headers |
|-------------|-------------|------------------|
**200** | OK |  -  |

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)

# **clone_prompts**
> List[str] clone_prompts(request_body)

Batch Clone Prompts

Batch clone multiple prompts. Ensure transactionality, return the promptId list after success.

### Example

* Bearer Authentication (bearerAuth):

```python
import time
import os
import freechat_sdk
from freechat_sdk.rest import ApiException
from pprint import pprint

# Defining the host is optional and defaults to http://127.0.0.1:8080
# See configuration.py for a list of all supported configuration parameters.
configuration = freechat_sdk.Configuration(
    host = "http://127.0.0.1:8080"
)

# The client must configure the authentication and authorization parameters
# in accordance with the API server security policy.
# Examples for each auth method are provided below, use the example that
# satisfies your auth use case.

# Configure Bearer authorization: bearerAuth
configuration = freechat_sdk.Configuration(
    access_token = os.environ["BEARER_TOKEN"]
)

# Enter a context with an instance of the API client
with freechat_sdk.ApiClient(configuration) as api_client:
    # Create an instance of the API class
    api_instance = freechat_sdk.PromptApi(api_client)
    request_body = ['request_body_example'] # List[str] | List of prompt information to be created

    try:
        # Batch Clone Prompts
        api_response = api_instance.clone_prompts(request_body)
        print("The response of PromptApi->clone_prompts:\n")
        pprint(api_response)
    except Exception as e:
        print("Exception when calling PromptApi->clone_prompts: %s\n" % e)
```



### Parameters


Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **request_body** | [**List[str]**](str.md)| List of prompt information to be created | 

### Return type

**List[str]**

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

 - **Content-Type**: application/json
 - **Accept**: application/json

### HTTP response details

| Status code | Description | Response headers |
|-------------|-------------|------------------|
**200** | OK |  -  |

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)

# **count_prompts**
> int count_prompts(prompt_query_dto)

Calculate Number of Prompts

Calculate the number of prompts according to the specified query conditions.

### Example

* Bearer Authentication (bearerAuth):

```python
import time
import os
import freechat_sdk
from freechat_sdk.models.prompt_query_dto import PromptQueryDTO
from freechat_sdk.rest import ApiException
from pprint import pprint

# Defining the host is optional and defaults to http://127.0.0.1:8080
# See configuration.py for a list of all supported configuration parameters.
configuration = freechat_sdk.Configuration(
    host = "http://127.0.0.1:8080"
)

# The client must configure the authentication and authorization parameters
# in accordance with the API server security policy.
# Examples for each auth method are provided below, use the example that
# satisfies your auth use case.

# Configure Bearer authorization: bearerAuth
configuration = freechat_sdk.Configuration(
    access_token = os.environ["BEARER_TOKEN"]
)

# Enter a context with an instance of the API client
with freechat_sdk.ApiClient(configuration) as api_client:
    # Create an instance of the API class
    api_instance = freechat_sdk.PromptApi(api_client)
    prompt_query_dto = freechat_sdk.PromptQueryDTO() # PromptQueryDTO | Query conditions

    try:
        # Calculate Number of Prompts
        api_response = api_instance.count_prompts(prompt_query_dto)
        print("The response of PromptApi->count_prompts:\n")
        pprint(api_response)
    except Exception as e:
        print("Exception when calling PromptApi->count_prompts: %s\n" % e)
```



### Parameters


Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **prompt_query_dto** | [**PromptQueryDTO**](PromptQueryDTO.md)| Query conditions | 

### Return type

**int**

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

 - **Content-Type**: application/json
 - **Accept**: application/json

### HTTP response details

| Status code | Description | Response headers |
|-------------|-------------|------------------|
**200** | OK |  -  |

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)

# **create_prompt**
> str create_prompt(prompt_create_dto)

Create Prompt

Create a prompt, required fields: - Prompt name - Prompt content - Applicable model  Limitations: - Description: 300 characters - Template: 1000 characters - Example: 2000 characters - Tags: 5 - Parameters: 10 

### Example

* Bearer Authentication (bearerAuth):

```python
import time
import os
import freechat_sdk
from freechat_sdk.models.prompt_create_dto import PromptCreateDTO
from freechat_sdk.rest import ApiException
from pprint import pprint

# Defining the host is optional and defaults to http://127.0.0.1:8080
# See configuration.py for a list of all supported configuration parameters.
configuration = freechat_sdk.Configuration(
    host = "http://127.0.0.1:8080"
)

# The client must configure the authentication and authorization parameters
# in accordance with the API server security policy.
# Examples for each auth method are provided below, use the example that
# satisfies your auth use case.

# Configure Bearer authorization: bearerAuth
configuration = freechat_sdk.Configuration(
    access_token = os.environ["BEARER_TOKEN"]
)

# Enter a context with an instance of the API client
with freechat_sdk.ApiClient(configuration) as api_client:
    # Create an instance of the API class
    api_instance = freechat_sdk.PromptApi(api_client)
    prompt_create_dto = {"name":"A Test Prompt","description":"A prompt description","template":"Hello world. I'm {name}","inputs":"{\"name\": null}","tags":["test","demo"],"aiModels":["123"]} # PromptCreateDTO | Information of the prompt to be created

    try:
        # Create Prompt
        api_response = api_instance.create_prompt(prompt_create_dto)
        print("The response of PromptApi->create_prompt:\n")
        pprint(api_response)
    except Exception as e:
        print("Exception when calling PromptApi->create_prompt: %s\n" % e)
```



### Parameters


Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **prompt_create_dto** | [**PromptCreateDTO**](PromptCreateDTO.md)| Information of the prompt to be created | 

### Return type

**str**

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

 - **Content-Type**: application/json
 - **Accept**: text/plain

### HTTP response details

| Status code | Description | Response headers |
|-------------|-------------|------------------|
**200** | OK |  -  |

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)

# **create_prompts**
> List[str] create_prompts(prompt_create_dto)

Batch Create Prompts

Batch create multiple prompts. Ensure transactionality, return the promptId list after success.

### Example

* Bearer Authentication (bearerAuth):

```python
import time
import os
import freechat_sdk
from freechat_sdk.models.prompt_create_dto import PromptCreateDTO
from freechat_sdk.rest import ApiException
from pprint import pprint

# Defining the host is optional and defaults to http://127.0.0.1:8080
# See configuration.py for a list of all supported configuration parameters.
configuration = freechat_sdk.Configuration(
    host = "http://127.0.0.1:8080"
)

# The client must configure the authentication and authorization parameters
# in accordance with the API server security policy.
# Examples for each auth method are provided below, use the example that
# satisfies your auth use case.

# Configure Bearer authorization: bearerAuth
configuration = freechat_sdk.Configuration(
    access_token = os.environ["BEARER_TOKEN"]
)

# Enter a context with an instance of the API client
with freechat_sdk.ApiClient(configuration) as api_client:
    # Create an instance of the API class
    api_instance = freechat_sdk.PromptApi(api_client)
    prompt_create_dto = [{"name":"First Test Prompt","description":"First prompt description","template":"Hello world. I'm {name}","inputs":"{\"name\": null}","tags":["test1","demo1"],"aiModels":["123"]},{"name":"Second Test Prompt","visibility":"public","description":"Second prompt description","template":"I wanna call you {robot}","inputs":"{\"robot\": null}","tags":["test2","demo2"],"aiModels":["123","456"]}] # List[PromptCreateDTO] | List of prompt information to be created

    try:
        # Batch Create Prompts
        api_response = api_instance.create_prompts(prompt_create_dto)
        print("The response of PromptApi->create_prompts:\n")
        pprint(api_response)
    except Exception as e:
        print("Exception when calling PromptApi->create_prompts: %s\n" % e)
```



### Parameters


Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **prompt_create_dto** | [**List[PromptCreateDTO]**](PromptCreateDTO.md)| List of prompt information to be created | 

### Return type

**List[str]**

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

 - **Content-Type**: application/json
 - **Accept**: application/json

### HTTP response details

| Status code | Description | Response headers |
|-------------|-------------|------------------|
**200** | OK |  -  |

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)

# **delete_prompt**
> bool delete_prompt(prompt_id)

Delete Prompt

Delete prompt. Returns success or failure.

### Example

* Bearer Authentication (bearerAuth):

```python
import time
import os
import freechat_sdk
from freechat_sdk.rest import ApiException
from pprint import pprint

# Defining the host is optional and defaults to http://127.0.0.1:8080
# See configuration.py for a list of all supported configuration parameters.
configuration = freechat_sdk.Configuration(
    host = "http://127.0.0.1:8080"
)

# The client must configure the authentication and authorization parameters
# in accordance with the API server security policy.
# Examples for each auth method are provided below, use the example that
# satisfies your auth use case.

# Configure Bearer authorization: bearerAuth
configuration = freechat_sdk.Configuration(
    access_token = os.environ["BEARER_TOKEN"]
)

# Enter a context with an instance of the API client
with freechat_sdk.ApiClient(configuration) as api_client:
    # Create an instance of the API class
    api_instance = freechat_sdk.PromptApi(api_client)
    prompt_id = 'prompt_id_example' # str | The promptId to be deleted

    try:
        # Delete Prompt
        api_response = api_instance.delete_prompt(prompt_id)
        print("The response of PromptApi->delete_prompt:\n")
        pprint(api_response)
    except Exception as e:
        print("Exception when calling PromptApi->delete_prompt: %s\n" % e)
```



### Parameters


Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **prompt_id** | **str**| The promptId to be deleted | 

### Return type

**bool**

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: application/json

### HTTP response details

| Status code | Description | Response headers |
|-------------|-------------|------------------|
**200** | OK |  -  |

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)

# **delete_prompt_by_name**
> List[str] delete_prompt_by_name(name)

Delete Prompt by Name

Delete prompt by name. return the list of successfully deleted promptIds.

### Example

* Bearer Authentication (bearerAuth):

```python
import time
import os
import freechat_sdk
from freechat_sdk.rest import ApiException
from pprint import pprint

# Defining the host is optional and defaults to http://127.0.0.1:8080
# See configuration.py for a list of all supported configuration parameters.
configuration = freechat_sdk.Configuration(
    host = "http://127.0.0.1:8080"
)

# The client must configure the authentication and authorization parameters
# in accordance with the API server security policy.
# Examples for each auth method are provided below, use the example that
# satisfies your auth use case.

# Configure Bearer authorization: bearerAuth
configuration = freechat_sdk.Configuration(
    access_token = os.environ["BEARER_TOKEN"]
)

# Enter a context with an instance of the API client
with freechat_sdk.ApiClient(configuration) as api_client:
    # Create an instance of the API class
    api_instance = freechat_sdk.PromptApi(api_client)
    name = 'name_example' # str | The prompt name to be deleted

    try:
        # Delete Prompt by Name
        api_response = api_instance.delete_prompt_by_name(name)
        print("The response of PromptApi->delete_prompt_by_name:\n")
        pprint(api_response)
    except Exception as e:
        print("Exception when calling PromptApi->delete_prompt_by_name: %s\n" % e)
```



### Parameters


Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **name** | **str**| The prompt name to be deleted | 

### Return type

**List[str]**

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: application/json

### HTTP response details

| Status code | Description | Response headers |
|-------------|-------------|------------------|
**200** | OK |  -  |

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)

# **delete_prompts**
> List[str] delete_prompts(request_body)

Batch Delete Prompts

Delete multiple prompts. Ensure transactionality, return the list of successfully deleted promptIds.

### Example

* Bearer Authentication (bearerAuth):

```python
import time
import os
import freechat_sdk
from freechat_sdk.rest import ApiException
from pprint import pprint

# Defining the host is optional and defaults to http://127.0.0.1:8080
# See configuration.py for a list of all supported configuration parameters.
configuration = freechat_sdk.Configuration(
    host = "http://127.0.0.1:8080"
)

# The client must configure the authentication and authorization parameters
# in accordance with the API server security policy.
# Examples for each auth method are provided below, use the example that
# satisfies your auth use case.

# Configure Bearer authorization: bearerAuth
configuration = freechat_sdk.Configuration(
    access_token = os.environ["BEARER_TOKEN"]
)

# Enter a context with an instance of the API client
with freechat_sdk.ApiClient(configuration) as api_client:
    # Create an instance of the API class
    api_instance = freechat_sdk.PromptApi(api_client)
    request_body = ['request_body_example'] # List[str] | List of promptIds to be deleted

    try:
        # Batch Delete Prompts
        api_response = api_instance.delete_prompts(request_body)
        print("The response of PromptApi->delete_prompts:\n")
        pprint(api_response)
    except Exception as e:
        print("Exception when calling PromptApi->delete_prompts: %s\n" % e)
```



### Parameters


Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **request_body** | [**List[str]**](str.md)| List of promptIds to be deleted | 

### Return type

**List[str]**

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

 - **Content-Type**: application/json
 - **Accept**: application/json

### HTTP response details

| Status code | Description | Response headers |
|-------------|-------------|------------------|
**200** | OK |  -  |

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)

# **exists_name**
> bool exists_name(name)

Check If Name Exists

Check if the name already exists.

### Example

* Bearer Authentication (bearerAuth):

```python
import time
import os
import freechat_sdk
from freechat_sdk.rest import ApiException
from pprint import pprint

# Defining the host is optional and defaults to http://127.0.0.1:8080
# See configuration.py for a list of all supported configuration parameters.
configuration = freechat_sdk.Configuration(
    host = "http://127.0.0.1:8080"
)

# The client must configure the authentication and authorization parameters
# in accordance with the API server security policy.
# Examples for each auth method are provided below, use the example that
# satisfies your auth use case.

# Configure Bearer authorization: bearerAuth
configuration = freechat_sdk.Configuration(
    access_token = os.environ["BEARER_TOKEN"]
)

# Enter a context with an instance of the API client
with freechat_sdk.ApiClient(configuration) as api_client:
    # Create an instance of the API class
    api_instance = freechat_sdk.PromptApi(api_client)
    name = 'name_example' # str | Name

    try:
        # Check If Name Exists
        api_response = api_instance.exists_name(name)
        print("The response of PromptApi->exists_name:\n")
        pprint(api_response)
    except Exception as e:
        print("Exception when calling PromptApi->exists_name: %s\n" % e)
```



### Parameters


Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **name** | **str**| Name | 

### Return type

**bool**

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: application/json

### HTTP response details

| Status code | Description | Response headers |
|-------------|-------------|------------------|
**200** | OK |  -  |

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)

# **get_prompt_details**
> PromptDetailsDTO get_prompt_details(prompt_id)

Get Prompt Details

Get prompt detailed information.

### Example

* Bearer Authentication (bearerAuth):

```python
import time
import os
import freechat_sdk
from freechat_sdk.models.prompt_details_dto import PromptDetailsDTO
from freechat_sdk.rest import ApiException
from pprint import pprint

# Defining the host is optional and defaults to http://127.0.0.1:8080
# See configuration.py for a list of all supported configuration parameters.
configuration = freechat_sdk.Configuration(
    host = "http://127.0.0.1:8080"
)

# The client must configure the authentication and authorization parameters
# in accordance with the API server security policy.
# Examples for each auth method are provided below, use the example that
# satisfies your auth use case.

# Configure Bearer authorization: bearerAuth
configuration = freechat_sdk.Configuration(
    access_token = os.environ["BEARER_TOKEN"]
)

# Enter a context with an instance of the API client
with freechat_sdk.ApiClient(configuration) as api_client:
    # Create an instance of the API class
    api_instance = freechat_sdk.PromptApi(api_client)
    prompt_id = 'prompt_id_example' # str | PromptId to be obtained

    try:
        # Get Prompt Details
        api_response = api_instance.get_prompt_details(prompt_id)
        print("The response of PromptApi->get_prompt_details:\n")
        pprint(api_response)
    except Exception as e:
        print("Exception when calling PromptApi->get_prompt_details: %s\n" % e)
```



### Parameters


Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **prompt_id** | **str**| PromptId to be obtained | 

### Return type

[**PromptDetailsDTO**](PromptDetailsDTO.md)

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: application/json

### HTTP response details

| Status code | Description | Response headers |
|-------------|-------------|------------------|
**200** | OK |  -  |

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)

# **get_prompt_summary**
> PromptSummaryDTO get_prompt_summary(prompt_id)

Get Prompt Summary

Get prompt summary information.

### Example

* Bearer Authentication (bearerAuth):

```python
import time
import os
import freechat_sdk
from freechat_sdk.models.prompt_summary_dto import PromptSummaryDTO
from freechat_sdk.rest import ApiException
from pprint import pprint

# Defining the host is optional and defaults to http://127.0.0.1:8080
# See configuration.py for a list of all supported configuration parameters.
configuration = freechat_sdk.Configuration(
    host = "http://127.0.0.1:8080"
)

# The client must configure the authentication and authorization parameters
# in accordance with the API server security policy.
# Examples for each auth method are provided below, use the example that
# satisfies your auth use case.

# Configure Bearer authorization: bearerAuth
configuration = freechat_sdk.Configuration(
    access_token = os.environ["BEARER_TOKEN"]
)

# Enter a context with an instance of the API client
with freechat_sdk.ApiClient(configuration) as api_client:
    # Create an instance of the API class
    api_instance = freechat_sdk.PromptApi(api_client)
    prompt_id = 'prompt_id_example' # str | PromptId to be obtained

    try:
        # Get Prompt Summary
        api_response = api_instance.get_prompt_summary(prompt_id)
        print("The response of PromptApi->get_prompt_summary:\n")
        pprint(api_response)
    except Exception as e:
        print("Exception when calling PromptApi->get_prompt_summary: %s\n" % e)
```



### Parameters


Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **prompt_id** | **str**| PromptId to be obtained | 

### Return type

[**PromptSummaryDTO**](PromptSummaryDTO.md)

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: application/json

### HTTP response details

| Status code | Description | Response headers |
|-------------|-------------|------------------|
**200** | OK |  -  |

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)

# **list_prompt_versions_by_name**
> List[PromptItemForNameDTO] list_prompt_versions_by_name(name)

List Versions by Prompt Name

List the versions and corresponding promptIds by prompt name.

### Example

* Bearer Authentication (bearerAuth):

```python
import time
import os
import freechat_sdk
from freechat_sdk.models.prompt_item_for_name_dto import PromptItemForNameDTO
from freechat_sdk.rest import ApiException
from pprint import pprint

# Defining the host is optional and defaults to http://127.0.0.1:8080
# See configuration.py for a list of all supported configuration parameters.
configuration = freechat_sdk.Configuration(
    host = "http://127.0.0.1:8080"
)

# The client must configure the authentication and authorization parameters
# in accordance with the API server security policy.
# Examples for each auth method are provided below, use the example that
# satisfies your auth use case.

# Configure Bearer authorization: bearerAuth
configuration = freechat_sdk.Configuration(
    access_token = os.environ["BEARER_TOKEN"]
)

# Enter a context with an instance of the API client
with freechat_sdk.ApiClient(configuration) as api_client:
    # Create an instance of the API class
    api_instance = freechat_sdk.PromptApi(api_client)
    name = 'name_example' # str | Prompt name

    try:
        # List Versions by Prompt Name
        api_response = api_instance.list_prompt_versions_by_name(name)
        print("The response of PromptApi->list_prompt_versions_by_name:\n")
        pprint(api_response)
    except Exception as e:
        print("Exception when calling PromptApi->list_prompt_versions_by_name: %s\n" % e)
```



### Parameters


Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **name** | **str**| Prompt name | 

### Return type

[**List[PromptItemForNameDTO]**](PromptItemForNameDTO.md)

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: application/json

### HTTP response details

| Status code | Description | Response headers |
|-------------|-------------|------------------|
**200** | OK |  -  |

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)

# **publish_prompt**
> str publish_prompt(prompt_id, visibility)

Publish Prompt

Publish prompt, draft content becomes formal content, version number increases by 1. After successful publication, a new promptId will be generated and returned. You need to specify the visibility for publication.

### Example

* Bearer Authentication (bearerAuth):

```python
import time
import os
import freechat_sdk
from freechat_sdk.rest import ApiException
from pprint import pprint

# Defining the host is optional and defaults to http://127.0.0.1:8080
# See configuration.py for a list of all supported configuration parameters.
configuration = freechat_sdk.Configuration(
    host = "http://127.0.0.1:8080"
)

# The client must configure the authentication and authorization parameters
# in accordance with the API server security policy.
# Examples for each auth method are provided below, use the example that
# satisfies your auth use case.

# Configure Bearer authorization: bearerAuth
configuration = freechat_sdk.Configuration(
    access_token = os.environ["BEARER_TOKEN"]
)

# Enter a context with an instance of the API client
with freechat_sdk.ApiClient(configuration) as api_client:
    # Create an instance of the API class
    api_instance = freechat_sdk.PromptApi(api_client)
    prompt_id = 'prompt_id_example' # str | The promptId to be published
    visibility = 'visibility_example' # str | Visibility: public | private | ...

    try:
        # Publish Prompt
        api_response = api_instance.publish_prompt(prompt_id, visibility)
        print("The response of PromptApi->publish_prompt:\n")
        pprint(api_response)
    except Exception as e:
        print("Exception when calling PromptApi->publish_prompt: %s\n" % e)
```



### Parameters


Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **prompt_id** | **str**| The promptId to be published | 
 **visibility** | **str**| Visibility: public | private | ... | 

### Return type

**str**

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: text/plain

### HTTP response details

| Status code | Description | Response headers |
|-------------|-------------|------------------|
**200** | OK |  -  |

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)

# **search_prompt_details**
> List[PromptDetailsDTO] search_prompt_details(prompt_query_dto)

Search Prompt Details

Same as /api/v1/prompt/search, but returns detailed information of the prompt.

### Example

* Bearer Authentication (bearerAuth):

```python
import time
import os
import freechat_sdk
from freechat_sdk.models.prompt_details_dto import PromptDetailsDTO
from freechat_sdk.models.prompt_query_dto import PromptQueryDTO
from freechat_sdk.rest import ApiException
from pprint import pprint

# Defining the host is optional and defaults to http://127.0.0.1:8080
# See configuration.py for a list of all supported configuration parameters.
configuration = freechat_sdk.Configuration(
    host = "http://127.0.0.1:8080"
)

# The client must configure the authentication and authorization parameters
# in accordance with the API server security policy.
# Examples for each auth method are provided below, use the example that
# satisfies your auth use case.

# Configure Bearer authorization: bearerAuth
configuration = freechat_sdk.Configuration(
    access_token = os.environ["BEARER_TOKEN"]
)

# Enter a context with an instance of the API client
with freechat_sdk.ApiClient(configuration) as api_client:
    # Create an instance of the API class
    api_instance = freechat_sdk.PromptApi(api_client)
    prompt_query_dto = {"where":{"visibility":"public","username":"amin","name":"Second Test","text":"(new)","tags":["demo2"],"aiModels":["123"]},"orderBy":["version","modifyTime asc"],"pageNum":0,"pageSize":1} # PromptQueryDTO | Query conditions

    try:
        # Search Prompt Details
        api_response = api_instance.search_prompt_details(prompt_query_dto)
        print("The response of PromptApi->search_prompt_details:\n")
        pprint(api_response)
    except Exception as e:
        print("Exception when calling PromptApi->search_prompt_details: %s\n" % e)
```



### Parameters


Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **prompt_query_dto** | [**PromptQueryDTO**](PromptQueryDTO.md)| Query conditions | 

### Return type

[**List[PromptDetailsDTO]**](PromptDetailsDTO.md)

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

 - **Content-Type**: application/json
 - **Accept**: application/json

### HTTP response details

| Status code | Description | Response headers |
|-------------|-------------|------------------|
**200** | OK |  -  |

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)

# **search_prompt_summary**
> List[PromptSummaryDTO] search_prompt_summary(prompt_query_dto)

Search Prompt Summary

Search prompts: - Specifiable query fields, and relationship:   - Scope: private, public_org or public. Private can only search this account.   - Username: exact match, only valid when searching public, public_org. If not specified, search all users.   - Tags: exact match (support and, or logic).   - Model type: exact match (support and, or logic).   - Name: left match.   - Type, exact match: string (default) | chat.   - Language, exact match.   - General: name, description, template, example, fuzzy match, one hit is enough; public scope + all user's general search does not guarantee timeliness. - A certain sorting rule can be specified, such as view count, reference count, rating, time, descending or ascending. - The search result is the prompt summary content. - Support pagination. 

### Example

* Bearer Authentication (bearerAuth):

```python
import time
import os
import freechat_sdk
from freechat_sdk.models.prompt_query_dto import PromptQueryDTO
from freechat_sdk.models.prompt_summary_dto import PromptSummaryDTO
from freechat_sdk.rest import ApiException
from pprint import pprint

# Defining the host is optional and defaults to http://127.0.0.1:8080
# See configuration.py for a list of all supported configuration parameters.
configuration = freechat_sdk.Configuration(
    host = "http://127.0.0.1:8080"
)

# The client must configure the authentication and authorization parameters
# in accordance with the API server security policy.
# Examples for each auth method are provided below, use the example that
# satisfies your auth use case.

# Configure Bearer authorization: bearerAuth
configuration = freechat_sdk.Configuration(
    access_token = os.environ["BEARER_TOKEN"]
)

# Enter a context with an instance of the API client
with freechat_sdk.ApiClient(configuration) as api_client:
    # Create an instance of the API class
    api_instance = freechat_sdk.PromptApi(api_client)
    prompt_query_dto = {"where":{"visibility":"public","username":"amin","name":"Second Test","text":"(new)","tags":["demo2"],"aiModels":["123"]},"orderBy":["version","modifyTime asc"],"pageNum":0,"pageSize":1} # PromptQueryDTO | Query conditions

    try:
        # Search Prompt Summary
        api_response = api_instance.search_prompt_summary(prompt_query_dto)
        print("The response of PromptApi->search_prompt_summary:\n")
        pprint(api_response)
    except Exception as e:
        print("Exception when calling PromptApi->search_prompt_summary: %s\n" % e)
```



### Parameters


Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **prompt_query_dto** | [**PromptQueryDTO**](PromptQueryDTO.md)| Query conditions | 

### Return type

[**List[PromptSummaryDTO]**](PromptSummaryDTO.md)

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

 - **Content-Type**: application/json
 - **Accept**: application/json

### HTTP response details

| Status code | Description | Response headers |
|-------------|-------------|------------------|
**200** | OK |  -  |

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)

# **send_prompt**
> LlmResultDTO send_prompt(prompt_ai_param_dto)

Send Prompt

Send the prompt to the AI service. Note that if the embedding model is called, the return is an embedding array, placed in the details field of the result; the original text is in the text field of the result.

### Example

* Bearer Authentication (bearerAuth):

```python
import time
import os
import freechat_sdk
from freechat_sdk.models.llm_result_dto import LlmResultDTO
from freechat_sdk.models.prompt_ai_param_dto import PromptAiParamDTO
from freechat_sdk.rest import ApiException
from pprint import pprint

# Defining the host is optional and defaults to http://127.0.0.1:8080
# See configuration.py for a list of all supported configuration parameters.
configuration = freechat_sdk.Configuration(
    host = "http://127.0.0.1:8080"
)

# The client must configure the authentication and authorization parameters
# in accordance with the API server security policy.
# Examples for each auth method are provided below, use the example that
# satisfies your auth use case.

# Configure Bearer authorization: bearerAuth
configuration = freechat_sdk.Configuration(
    access_token = os.environ["BEARER_TOKEN"]
)

# Enter a context with an instance of the API client
with freechat_sdk.ApiClient(configuration) as api_client:
    # Create an instance of the API class
    api_instance = freechat_sdk.PromptApi(api_client)
    prompt_ai_param_dto = {"prompt":"say 'hello'","params":{"apiKey":"","modelId":"[open_ai]gpt-3.5-turbo"}} # PromptAiParamDTO | Call parameters

    try:
        # Send Prompt
        api_response = api_instance.send_prompt(prompt_ai_param_dto)
        print("The response of PromptApi->send_prompt:\n")
        pprint(api_response)
    except Exception as e:
        print("Exception when calling PromptApi->send_prompt: %s\n" % e)
```



### Parameters


Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **prompt_ai_param_dto** | [**PromptAiParamDTO**](PromptAiParamDTO.md)| Call parameters | 

### Return type

[**LlmResultDTO**](LlmResultDTO.md)

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

 - **Content-Type**: application/json
 - **Accept**: application/json

### HTTP response details

| Status code | Description | Response headers |
|-------------|-------------|------------------|
**200** | OK |  -  |

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)

# **stream_send_prompt**
> SseEmitter stream_send_prompt(prompt_ai_param_dto)

Send Prompt by Streaming Back

Refer to /api/v1/prompt/send, stream back chunks of the response.

### Example

* Bearer Authentication (bearerAuth):

```python
import time
import os
import freechat_sdk
from freechat_sdk.models.prompt_ai_param_dto import PromptAiParamDTO
from freechat_sdk.models.sse_emitter import SseEmitter
from freechat_sdk.rest import ApiException
from pprint import pprint

# Defining the host is optional and defaults to http://127.0.0.1:8080
# See configuration.py for a list of all supported configuration parameters.
configuration = freechat_sdk.Configuration(
    host = "http://127.0.0.1:8080"
)

# The client must configure the authentication and authorization parameters
# in accordance with the API server security policy.
# Examples for each auth method are provided below, use the example that
# satisfies your auth use case.

# Configure Bearer authorization: bearerAuth
configuration = freechat_sdk.Configuration(
    access_token = os.environ["BEARER_TOKEN"]
)

# Enter a context with an instance of the API client
with freechat_sdk.ApiClient(configuration) as api_client:
    # Create an instance of the API class
    api_instance = freechat_sdk.PromptApi(api_client)
    prompt_ai_param_dto = {"prompt":"say 'hello'","params":{"apiKey":"","modelId":"[open_ai]gpt-3.5-turbo"}} # PromptAiParamDTO | Call parameters

    try:
        # Send Prompt by Streaming Back
        api_response = api_instance.stream_send_prompt(prompt_ai_param_dto)
        print("The response of PromptApi->stream_send_prompt:\n")
        pprint(api_response)
    except Exception as e:
        print("Exception when calling PromptApi->stream_send_prompt: %s\n" % e)
```



### Parameters


Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **prompt_ai_param_dto** | [**PromptAiParamDTO**](PromptAiParamDTO.md)| Call parameters | 

### Return type

[**SseEmitter**](SseEmitter.md)

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

 - **Content-Type**: application/json
 - **Accept**: text/event-stream

### HTTP response details

| Status code | Description | Response headers |
|-------------|-------------|------------------|
**200** | OK |  -  |

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)

# **update_prompt**
> bool update_prompt(prompt_id, prompt_update_dto)

Update Prompt

Update prompt, refer to /api/v1/prompt/create, required field: promptId. Returns success or failure.

### Example

* Bearer Authentication (bearerAuth):

```python
import time
import os
import freechat_sdk
from freechat_sdk.models.prompt_update_dto import PromptUpdateDTO
from freechat_sdk.rest import ApiException
from pprint import pprint

# Defining the host is optional and defaults to http://127.0.0.1:8080
# See configuration.py for a list of all supported configuration parameters.
configuration = freechat_sdk.Configuration(
    host = "http://127.0.0.1:8080"
)

# The client must configure the authentication and authorization parameters
# in accordance with the API server security policy.
# Examples for each auth method are provided below, use the example that
# satisfies your auth use case.

# Configure Bearer authorization: bearerAuth
configuration = freechat_sdk.Configuration(
    access_token = os.environ["BEARER_TOKEN"]
)

# Enter a context with an instance of the API client
with freechat_sdk.ApiClient(configuration) as api_client:
    # Create an instance of the API class
    api_instance = freechat_sdk.PromptApi(api_client)
    prompt_id = 'prompt_id_example' # str | The promptId to be updated
    prompt_update_dto = {"version":2,"name":"Second Test Prompt (New)","visibility":"public","description":"Second prompt description (new)","template":"I wanna call you {robot}","inputs":"{\"robot\": null}","tags":["test2","demo2","robot"],"aiModels":["123","456"]} # PromptUpdateDTO | The prompt information to be updated

    try:
        # Update Prompt
        api_response = api_instance.update_prompt(prompt_id, prompt_update_dto)
        print("The response of PromptApi->update_prompt:\n")
        pprint(api_response)
    except Exception as e:
        print("Exception when calling PromptApi->update_prompt: %s\n" % e)
```



### Parameters


Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **prompt_id** | **str**| The promptId to be updated | 
 **prompt_update_dto** | [**PromptUpdateDTO**](PromptUpdateDTO.md)| The prompt information to be updated | 

### Return type

**bool**

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

 - **Content-Type**: application/json
 - **Accept**: application/json

### HTTP response details

| Status code | Description | Response headers |
|-------------|-------------|------------------|
**200** | OK |  -  |

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)

