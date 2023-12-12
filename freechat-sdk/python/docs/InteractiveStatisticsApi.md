# freechat-sdk.InteractiveStatisticsApi

All URIs are relative to *http://127.0.0.1:8080*

Method | HTTP request | Description
------------- | ------------- | -------------
[**add_statistic**](InteractiveStatisticsApi.md#add_statistic) | **POST** /api/v1/stats/{infoType}/{infoId}/{statsType}/{delta} | Add Statistics
[**get_score**](InteractiveStatisticsApi.md#get_score) | **GET** /api/v1/score/{infoType}/{infoId} | Get Score for Resource
[**get_statistic**](InteractiveStatisticsApi.md#get_statistic) | **GET** /api/v1/stats/{infoType}/{infoId}/{statsType} | Get Statistics
[**get_statistics**](InteractiveStatisticsApi.md#get_statistics) | **GET** /api/v1/stats/{infoType}/{infoId} | Get All Statistics
[**increase_statistic**](InteractiveStatisticsApi.md#increase_statistic) | **POST** /api/v1/stats/{infoType}/{infoId}/{statsType} | Increase Statistics
[**list_characters_by_statistic**](InteractiveStatisticsApi.md#list_characters_by_statistic) | **GET** /api/v1/stats/characters/by/{statsType}/{pageSize} | List Characters by Statistics
[**list_characters_by_statistic1**](InteractiveStatisticsApi.md#list_characters_by_statistic1) | **GET** /api/v1/stats/characters/by/{statsType}/{pageSize}/{pageNum} | List Characters by Statistics
[**list_characters_by_statistic2**](InteractiveStatisticsApi.md#list_characters_by_statistic2) | **GET** /api/v1/stats/characters/by/{statsType} | List Characters by Statistics
[**list_flows_by_statistic**](InteractiveStatisticsApi.md#list_flows_by_statistic) | **GET** /api/v1/stats/flows/by/{statsType}/{pageSize}/{pageNum} | List Flows by Statistics
[**list_flows_by_statistic1**](InteractiveStatisticsApi.md#list_flows_by_statistic1) | **GET** /api/v1/stats/flows/by/{statsType} | List Flows by Statistics
[**list_flows_by_statistic2**](InteractiveStatisticsApi.md#list_flows_by_statistic2) | **GET** /api/v1/stats/flows/by/{statsType}/{pageSize} | List Flows by Statistics
[**list_plugins_by_statistic**](InteractiveStatisticsApi.md#list_plugins_by_statistic) | **GET** /api/v1/stats/plugins/by/{statsType}/{pageSize}/{pageNum} | List Plugins by Statistics
[**list_plugins_by_statistic1**](InteractiveStatisticsApi.md#list_plugins_by_statistic1) | **GET** /api/v1/stats/plugins/by/{statsType}/{pageSize} | List Plugins by Statistics
[**list_plugins_by_statistic2**](InteractiveStatisticsApi.md#list_plugins_by_statistic2) | **GET** /api/v1/stats/plugins/by/{statsType} | List Plugins by Statistics
[**list_prompts_by_statistic**](InteractiveStatisticsApi.md#list_prompts_by_statistic) | **GET** /api/v1/stats/prompts/by/{statsType}/{pageSize} | List Prompts by Statistics
[**list_prompts_by_statistic1**](InteractiveStatisticsApi.md#list_prompts_by_statistic1) | **GET** /api/v1/stats/prompts/by/{statsType}/{pageSize}/{pageNum} | List Prompts by Statistics
[**list_prompts_by_statistic2**](InteractiveStatisticsApi.md#list_prompts_by_statistic2) | **GET** /api/v1/stats/prompts/by/{statsType} | List Prompts by Statistics


# **add_statistic**
> int add_statistic(info_type, info_id, stats_type, delta)

Add Statistics

Add the statistics of the corresponding metrics of the corresponding resources. The increment can be negative. Return the latest statistics.

### Example

* Bearer Authentication (bearerAuth):
```python
import time
import os
import freechat-sdk
from freechat-sdk.rest import ApiException
from pprint import pprint

# Defining the host is optional and defaults to http://127.0.0.1:8080
# See configuration.py for a list of all supported configuration parameters.
configuration = freechat-sdk.Configuration(
    host = "http://127.0.0.1:8080"
)

# The client must configure the authentication and authorization parameters
# in accordance with the API server security policy.
# Examples for each auth method are provided below, use the example that
# satisfies your auth use case.

# Configure Bearer authorization: bearerAuth
configuration = freechat-sdk.Configuration(
    access_token = os.environ["BEARER_TOKEN"]
)

# Enter a context with an instance of the API client
with freechat-sdk.ApiClient(configuration) as api_client:
    # Create an instance of the API class
    api_instance = freechat-sdk.InteractiveStatisticsApi(api_client)
    info_type = 'info_type_example' # str | Resource type: prompt | flow | plugin
    info_id = 'info_id_example' # str | Unique resource identifier
    stats_type = 'stats_type_example' # str | Statistics type: view_count | refer_count | recommend_count | score
    delta = 56 # int | Delta in statistical value

    try:
        # Add Statistics
        api_response = api_instance.add_statistic(info_type, info_id, stats_type, delta)
        print("The response of InteractiveStatisticsApi->add_statistic:\n")
        pprint(api_response)
    except Exception as e:
        print("Exception when calling InteractiveStatisticsApi->add_statistic: %s\n" % e)
```



### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **info_type** | **str**| Resource type: prompt | flow | plugin | 
 **info_id** | **str**| Unique resource identifier | 
 **stats_type** | **str**| Statistics type: view_count | refer_count | recommend_count | score | 
 **delta** | **int**| Delta in statistical value | 

### Return type

**int**

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

# **get_score**
> int get_score(info_type, info_id)

Get Score for Resource

Get the current user's score for the corresponding resource.

### Example

* Bearer Authentication (bearerAuth):
```python
import time
import os
import freechat-sdk
from freechat-sdk.rest import ApiException
from pprint import pprint

# Defining the host is optional and defaults to http://127.0.0.1:8080
# See configuration.py for a list of all supported configuration parameters.
configuration = freechat-sdk.Configuration(
    host = "http://127.0.0.1:8080"
)

# The client must configure the authentication and authorization parameters
# in accordance with the API server security policy.
# Examples for each auth method are provided below, use the example that
# satisfies your auth use case.

# Configure Bearer authorization: bearerAuth
configuration = freechat-sdk.Configuration(
    access_token = os.environ["BEARER_TOKEN"]
)

# Enter a context with an instance of the API client
with freechat-sdk.ApiClient(configuration) as api_client:
    # Create an instance of the API class
    api_instance = freechat-sdk.InteractiveStatisticsApi(api_client)
    info_type = 'info_type_example' # str | Resource type: prompt | flow | plugin
    info_id = 'info_id_example' # str | Unique resource identifier

    try:
        # Get Score for Resource
        api_response = api_instance.get_score(info_type, info_id)
        print("The response of InteractiveStatisticsApi->get_score:\n")
        pprint(api_response)
    except Exception as e:
        print("Exception when calling InteractiveStatisticsApi->get_score: %s\n" % e)
```



### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **info_type** | **str**| Resource type: prompt | flow | plugin | 
 **info_id** | **str**| Unique resource identifier | 

### Return type

**int**

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

# **get_statistic**
> int get_statistic(info_type, info_id, stats_type)

Get Statistics

Get the statistics of the corresponding metrics of the corresponding resources.

### Example

* Bearer Authentication (bearerAuth):
```python
import time
import os
import freechat-sdk
from freechat-sdk.rest import ApiException
from pprint import pprint

# Defining the host is optional and defaults to http://127.0.0.1:8080
# See configuration.py for a list of all supported configuration parameters.
configuration = freechat-sdk.Configuration(
    host = "http://127.0.0.1:8080"
)

# The client must configure the authentication and authorization parameters
# in accordance with the API server security policy.
# Examples for each auth method are provided below, use the example that
# satisfies your auth use case.

# Configure Bearer authorization: bearerAuth
configuration = freechat-sdk.Configuration(
    access_token = os.environ["BEARER_TOKEN"]
)

# Enter a context with an instance of the API client
with freechat-sdk.ApiClient(configuration) as api_client:
    # Create an instance of the API class
    api_instance = freechat-sdk.InteractiveStatisticsApi(api_client)
    info_type = 'info_type_example' # str | Resource type: prompt | flow | plugin
    info_id = 'info_id_example' # str | Unique resource identifier
    stats_type = 'stats_type_example' # str | Statistics type: view_count | refer_count | recommend_count | score

    try:
        # Get Statistics
        api_response = api_instance.get_statistic(info_type, info_id, stats_type)
        print("The response of InteractiveStatisticsApi->get_statistic:\n")
        pprint(api_response)
    except Exception as e:
        print("Exception when calling InteractiveStatisticsApi->get_statistic: %s\n" % e)
```



### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **info_type** | **str**| Resource type: prompt | flow | plugin | 
 **info_id** | **str**| Unique resource identifier | 
 **stats_type** | **str**| Statistics type: view_count | refer_count | recommend_count | score | 

### Return type

**int**

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

# **get_statistics**
> InteractiveStatsDTO get_statistics(info_type, info_id)

Get All Statistics

Get all statistics of the corresponding resources.

### Example

* Bearer Authentication (bearerAuth):
```python
import time
import os
import freechat-sdk
from freechat-sdk.models.interactive_stats_dto import InteractiveStatsDTO
from freechat-sdk.rest import ApiException
from pprint import pprint

# Defining the host is optional and defaults to http://127.0.0.1:8080
# See configuration.py for a list of all supported configuration parameters.
configuration = freechat-sdk.Configuration(
    host = "http://127.0.0.1:8080"
)

# The client must configure the authentication and authorization parameters
# in accordance with the API server security policy.
# Examples for each auth method are provided below, use the example that
# satisfies your auth use case.

# Configure Bearer authorization: bearerAuth
configuration = freechat-sdk.Configuration(
    access_token = os.environ["BEARER_TOKEN"]
)

# Enter a context with an instance of the API client
with freechat-sdk.ApiClient(configuration) as api_client:
    # Create an instance of the API class
    api_instance = freechat-sdk.InteractiveStatisticsApi(api_client)
    info_type = 'info_type_example' # str | Resource type: prompt | flow | plugin
    info_id = 'info_id_example' # str | Unique resource identifier

    try:
        # Get All Statistics
        api_response = api_instance.get_statistics(info_type, info_id)
        print("The response of InteractiveStatisticsApi->get_statistics:\n")
        pprint(api_response)
    except Exception as e:
        print("Exception when calling InteractiveStatisticsApi->get_statistics: %s\n" % e)
```



### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **info_type** | **str**| Resource type: prompt | flow | plugin | 
 **info_id** | **str**| Unique resource identifier | 

### Return type

[**InteractiveStatsDTO**](InteractiveStatsDTO.md)

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

# **increase_statistic**
> int increase_statistic(info_type, info_id, stats_type)

Increase Statistics

Increase the statistics of the corresponding metrics of the corresponding resources by one. Return the latest statistics.

### Example

* Bearer Authentication (bearerAuth):
```python
import time
import os
import freechat-sdk
from freechat-sdk.rest import ApiException
from pprint import pprint

# Defining the host is optional and defaults to http://127.0.0.1:8080
# See configuration.py for a list of all supported configuration parameters.
configuration = freechat-sdk.Configuration(
    host = "http://127.0.0.1:8080"
)

# The client must configure the authentication and authorization parameters
# in accordance with the API server security policy.
# Examples for each auth method are provided below, use the example that
# satisfies your auth use case.

# Configure Bearer authorization: bearerAuth
configuration = freechat-sdk.Configuration(
    access_token = os.environ["BEARER_TOKEN"]
)

# Enter a context with an instance of the API client
with freechat-sdk.ApiClient(configuration) as api_client:
    # Create an instance of the API class
    api_instance = freechat-sdk.InteractiveStatisticsApi(api_client)
    info_type = 'info_type_example' # str | Resource type: prompt | flow | plugin
    info_id = 'info_id_example' # str | Unique resource identifier
    stats_type = 'stats_type_example' # str | Statistics type: view_count | refer_count | recommend_count | score

    try:
        # Increase Statistics
        api_response = api_instance.increase_statistic(info_type, info_id, stats_type)
        print("The response of InteractiveStatisticsApi->increase_statistic:\n")
        pprint(api_response)
    except Exception as e:
        print("Exception when calling InteractiveStatisticsApi->increase_statistic: %s\n" % e)
```



### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **info_type** | **str**| Resource type: prompt | flow | plugin | 
 **info_id** | **str**| Unique resource identifier | 
 **stats_type** | **str**| Statistics type: view_count | refer_count | recommend_count | score | 

### Return type

**int**

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

# **list_characters_by_statistic**
> List[CharacterSummaryStatsDTO] list_characters_by_statistic(stats_type, page_size, asc=asc)

List Characters by Statistics

List characters based on statistics, including interactive statistical data.

### Example

* Bearer Authentication (bearerAuth):
```python
import time
import os
import freechat-sdk
from freechat-sdk.models.character_summary_stats_dto import CharacterSummaryStatsDTO
from freechat-sdk.rest import ApiException
from pprint import pprint

# Defining the host is optional and defaults to http://127.0.0.1:8080
# See configuration.py for a list of all supported configuration parameters.
configuration = freechat-sdk.Configuration(
    host = "http://127.0.0.1:8080"
)

# The client must configure the authentication and authorization parameters
# in accordance with the API server security policy.
# Examples for each auth method are provided below, use the example that
# satisfies your auth use case.

# Configure Bearer authorization: bearerAuth
configuration = freechat-sdk.Configuration(
    access_token = os.environ["BEARER_TOKEN"]
)

# Enter a context with an instance of the API client
with freechat-sdk.ApiClient(configuration) as api_client:
    # Create an instance of the API class
    api_instance = freechat-sdk.InteractiveStatisticsApi(api_client)
    stats_type = 'stats_type_example' # str | Statistics type: view_count | refer_count | recommend_count | score
    page_size = 56 # int | Maximum quantity
    asc = 'asc_example' # str | Default is descending order, set asc=1 for ascending order (optional)

    try:
        # List Characters by Statistics
        api_response = api_instance.list_characters_by_statistic(stats_type, page_size, asc=asc)
        print("The response of InteractiveStatisticsApi->list_characters_by_statistic:\n")
        pprint(api_response)
    except Exception as e:
        print("Exception when calling InteractiveStatisticsApi->list_characters_by_statistic: %s\n" % e)
```



### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **stats_type** | **str**| Statistics type: view_count | refer_count | recommend_count | score | 
 **page_size** | **int**| Maximum quantity | 
 **asc** | **str**| Default is descending order, set asc&#x3D;1 for ascending order | [optional] 

### Return type

[**List[CharacterSummaryStatsDTO]**](CharacterSummaryStatsDTO.md)

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

# **list_characters_by_statistic1**
> List[CharacterSummaryStatsDTO] list_characters_by_statistic1(stats_type, page_size, page_num, asc=asc)

List Characters by Statistics

List characters based on statistics, including interactive statistical data.

### Example

* Bearer Authentication (bearerAuth):
```python
import time
import os
import freechat-sdk
from freechat-sdk.models.character_summary_stats_dto import CharacterSummaryStatsDTO
from freechat-sdk.rest import ApiException
from pprint import pprint

# Defining the host is optional and defaults to http://127.0.0.1:8080
# See configuration.py for a list of all supported configuration parameters.
configuration = freechat-sdk.Configuration(
    host = "http://127.0.0.1:8080"
)

# The client must configure the authentication and authorization parameters
# in accordance with the API server security policy.
# Examples for each auth method are provided below, use the example that
# satisfies your auth use case.

# Configure Bearer authorization: bearerAuth
configuration = freechat-sdk.Configuration(
    access_token = os.environ["BEARER_TOKEN"]
)

# Enter a context with an instance of the API client
with freechat-sdk.ApiClient(configuration) as api_client:
    # Create an instance of the API class
    api_instance = freechat-sdk.InteractiveStatisticsApi(api_client)
    stats_type = 'stats_type_example' # str | Statistics type: view_count | refer_count | recommend_count | score
    page_size = 56 # int | Maximum quantity
    page_num = 56 # int | Current page number
    asc = 'asc_example' # str | Default is descending order, set asc=1 for ascending order (optional)

    try:
        # List Characters by Statistics
        api_response = api_instance.list_characters_by_statistic1(stats_type, page_size, page_num, asc=asc)
        print("The response of InteractiveStatisticsApi->list_characters_by_statistic1:\n")
        pprint(api_response)
    except Exception as e:
        print("Exception when calling InteractiveStatisticsApi->list_characters_by_statistic1: %s\n" % e)
```



### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **stats_type** | **str**| Statistics type: view_count | refer_count | recommend_count | score | 
 **page_size** | **int**| Maximum quantity | 
 **page_num** | **int**| Current page number | 
 **asc** | **str**| Default is descending order, set asc&#x3D;1 for ascending order | [optional] 

### Return type

[**List[CharacterSummaryStatsDTO]**](CharacterSummaryStatsDTO.md)

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

# **list_characters_by_statistic2**
> List[CharacterSummaryStatsDTO] list_characters_by_statistic2(stats_type, asc=asc)

List Characters by Statistics

List characters based on statistics, including interactive statistical data.

### Example

* Bearer Authentication (bearerAuth):
```python
import time
import os
import freechat-sdk
from freechat-sdk.models.character_summary_stats_dto import CharacterSummaryStatsDTO
from freechat-sdk.rest import ApiException
from pprint import pprint

# Defining the host is optional and defaults to http://127.0.0.1:8080
# See configuration.py for a list of all supported configuration parameters.
configuration = freechat-sdk.Configuration(
    host = "http://127.0.0.1:8080"
)

# The client must configure the authentication and authorization parameters
# in accordance with the API server security policy.
# Examples for each auth method are provided below, use the example that
# satisfies your auth use case.

# Configure Bearer authorization: bearerAuth
configuration = freechat-sdk.Configuration(
    access_token = os.environ["BEARER_TOKEN"]
)

# Enter a context with an instance of the API client
with freechat-sdk.ApiClient(configuration) as api_client:
    # Create an instance of the API class
    api_instance = freechat-sdk.InteractiveStatisticsApi(api_client)
    stats_type = 'stats_type_example' # str | Statistics type: view_count | refer_count | recommend_count | score
    asc = 'asc_example' # str | Default is descending order, set asc=1 for ascending order (optional)

    try:
        # List Characters by Statistics
        api_response = api_instance.list_characters_by_statistic2(stats_type, asc=asc)
        print("The response of InteractiveStatisticsApi->list_characters_by_statistic2:\n")
        pprint(api_response)
    except Exception as e:
        print("Exception when calling InteractiveStatisticsApi->list_characters_by_statistic2: %s\n" % e)
```



### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **stats_type** | **str**| Statistics type: view_count | refer_count | recommend_count | score | 
 **asc** | **str**| Default is descending order, set asc&#x3D;1 for ascending order | [optional] 

### Return type

[**List[CharacterSummaryStatsDTO]**](CharacterSummaryStatsDTO.md)

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

# **list_flows_by_statistic**
> List[FlowSummaryStatsDTO] list_flows_by_statistic(stats_type, page_size, page_num, asc=asc)

List Flows by Statistics

List flows based on statistics, including interactive statistical data.

### Example

* Bearer Authentication (bearerAuth):
```python
import time
import os
import freechat-sdk
from freechat-sdk.models.flow_summary_stats_dto import FlowSummaryStatsDTO
from freechat-sdk.rest import ApiException
from pprint import pprint

# Defining the host is optional and defaults to http://127.0.0.1:8080
# See configuration.py for a list of all supported configuration parameters.
configuration = freechat-sdk.Configuration(
    host = "http://127.0.0.1:8080"
)

# The client must configure the authentication and authorization parameters
# in accordance with the API server security policy.
# Examples for each auth method are provided below, use the example that
# satisfies your auth use case.

# Configure Bearer authorization: bearerAuth
configuration = freechat-sdk.Configuration(
    access_token = os.environ["BEARER_TOKEN"]
)

# Enter a context with an instance of the API client
with freechat-sdk.ApiClient(configuration) as api_client:
    # Create an instance of the API class
    api_instance = freechat-sdk.InteractiveStatisticsApi(api_client)
    stats_type = 'stats_type_example' # str | Statistics type: view_count | refer_count | recommend_count | score
    page_size = 56 # int | Maximum quantity
    page_num = 56 # int | Current page number
    asc = 'asc_example' # str | Default is descending order, set asc=1 for ascending order (optional)

    try:
        # List Flows by Statistics
        api_response = api_instance.list_flows_by_statistic(stats_type, page_size, page_num, asc=asc)
        print("The response of InteractiveStatisticsApi->list_flows_by_statistic:\n")
        pprint(api_response)
    except Exception as e:
        print("Exception when calling InteractiveStatisticsApi->list_flows_by_statistic: %s\n" % e)
```



### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **stats_type** | **str**| Statistics type: view_count | refer_count | recommend_count | score | 
 **page_size** | **int**| Maximum quantity | 
 **page_num** | **int**| Current page number | 
 **asc** | **str**| Default is descending order, set asc&#x3D;1 for ascending order | [optional] 

### Return type

[**List[FlowSummaryStatsDTO]**](FlowSummaryStatsDTO.md)

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

# **list_flows_by_statistic1**
> List[FlowSummaryStatsDTO] list_flows_by_statistic1(stats_type, asc=asc)

List Flows by Statistics

List flows based on statistics, including interactive statistical data.

### Example

* Bearer Authentication (bearerAuth):
```python
import time
import os
import freechat-sdk
from freechat-sdk.models.flow_summary_stats_dto import FlowSummaryStatsDTO
from freechat-sdk.rest import ApiException
from pprint import pprint

# Defining the host is optional and defaults to http://127.0.0.1:8080
# See configuration.py for a list of all supported configuration parameters.
configuration = freechat-sdk.Configuration(
    host = "http://127.0.0.1:8080"
)

# The client must configure the authentication and authorization parameters
# in accordance with the API server security policy.
# Examples for each auth method are provided below, use the example that
# satisfies your auth use case.

# Configure Bearer authorization: bearerAuth
configuration = freechat-sdk.Configuration(
    access_token = os.environ["BEARER_TOKEN"]
)

# Enter a context with an instance of the API client
with freechat-sdk.ApiClient(configuration) as api_client:
    # Create an instance of the API class
    api_instance = freechat-sdk.InteractiveStatisticsApi(api_client)
    stats_type = 'stats_type_example' # str | Statistics type: view_count | refer_count | recommend_count | score
    asc = 'asc_example' # str | Default is descending order, set asc=1 for ascending order (optional)

    try:
        # List Flows by Statistics
        api_response = api_instance.list_flows_by_statistic1(stats_type, asc=asc)
        print("The response of InteractiveStatisticsApi->list_flows_by_statistic1:\n")
        pprint(api_response)
    except Exception as e:
        print("Exception when calling InteractiveStatisticsApi->list_flows_by_statistic1: %s\n" % e)
```



### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **stats_type** | **str**| Statistics type: view_count | refer_count | recommend_count | score | 
 **asc** | **str**| Default is descending order, set asc&#x3D;1 for ascending order | [optional] 

### Return type

[**List[FlowSummaryStatsDTO]**](FlowSummaryStatsDTO.md)

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

# **list_flows_by_statistic2**
> List[FlowSummaryStatsDTO] list_flows_by_statistic2(stats_type, page_size, asc=asc)

List Flows by Statistics

List flows based on statistics, including interactive statistical data.

### Example

* Bearer Authentication (bearerAuth):
```python
import time
import os
import freechat-sdk
from freechat-sdk.models.flow_summary_stats_dto import FlowSummaryStatsDTO
from freechat-sdk.rest import ApiException
from pprint import pprint

# Defining the host is optional and defaults to http://127.0.0.1:8080
# See configuration.py for a list of all supported configuration parameters.
configuration = freechat-sdk.Configuration(
    host = "http://127.0.0.1:8080"
)

# The client must configure the authentication and authorization parameters
# in accordance with the API server security policy.
# Examples for each auth method are provided below, use the example that
# satisfies your auth use case.

# Configure Bearer authorization: bearerAuth
configuration = freechat-sdk.Configuration(
    access_token = os.environ["BEARER_TOKEN"]
)

# Enter a context with an instance of the API client
with freechat-sdk.ApiClient(configuration) as api_client:
    # Create an instance of the API class
    api_instance = freechat-sdk.InteractiveStatisticsApi(api_client)
    stats_type = 'stats_type_example' # str | Statistics type: view_count | refer_count | recommend_count | score
    page_size = 56 # int | Maximum quantity
    asc = 'asc_example' # str | Default is descending order, set asc=1 for ascending order (optional)

    try:
        # List Flows by Statistics
        api_response = api_instance.list_flows_by_statistic2(stats_type, page_size, asc=asc)
        print("The response of InteractiveStatisticsApi->list_flows_by_statistic2:\n")
        pprint(api_response)
    except Exception as e:
        print("Exception when calling InteractiveStatisticsApi->list_flows_by_statistic2: %s\n" % e)
```



### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **stats_type** | **str**| Statistics type: view_count | refer_count | recommend_count | score | 
 **page_size** | **int**| Maximum quantity | 
 **asc** | **str**| Default is descending order, set asc&#x3D;1 for ascending order | [optional] 

### Return type

[**List[FlowSummaryStatsDTO]**](FlowSummaryStatsDTO.md)

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

# **list_plugins_by_statistic**
> List[PluginSummaryStatsDTO] list_plugins_by_statistic(stats_type, page_size, page_num, asc=asc)

List Plugins by Statistics

List plugins based on statistics, including interactive statistical data.

### Example

* Bearer Authentication (bearerAuth):
```python
import time
import os
import freechat-sdk
from freechat-sdk.models.plugin_summary_stats_dto import PluginSummaryStatsDTO
from freechat-sdk.rest import ApiException
from pprint import pprint

# Defining the host is optional and defaults to http://127.0.0.1:8080
# See configuration.py for a list of all supported configuration parameters.
configuration = freechat-sdk.Configuration(
    host = "http://127.0.0.1:8080"
)

# The client must configure the authentication and authorization parameters
# in accordance with the API server security policy.
# Examples for each auth method are provided below, use the example that
# satisfies your auth use case.

# Configure Bearer authorization: bearerAuth
configuration = freechat-sdk.Configuration(
    access_token = os.environ["BEARER_TOKEN"]
)

# Enter a context with an instance of the API client
with freechat-sdk.ApiClient(configuration) as api_client:
    # Create an instance of the API class
    api_instance = freechat-sdk.InteractiveStatisticsApi(api_client)
    stats_type = 'stats_type_example' # str | Statistics type: view_count | refer_count | recommend_count | score
    page_size = 56 # int | Maximum quantity
    page_num = 56 # int | Current page number
    asc = 'asc_example' # str | Default is descending order, set asc=1 for ascending order (optional)

    try:
        # List Plugins by Statistics
        api_response = api_instance.list_plugins_by_statistic(stats_type, page_size, page_num, asc=asc)
        print("The response of InteractiveStatisticsApi->list_plugins_by_statistic:\n")
        pprint(api_response)
    except Exception as e:
        print("Exception when calling InteractiveStatisticsApi->list_plugins_by_statistic: %s\n" % e)
```



### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **stats_type** | **str**| Statistics type: view_count | refer_count | recommend_count | score | 
 **page_size** | **int**| Maximum quantity | 
 **page_num** | **int**| Current page number | 
 **asc** | **str**| Default is descending order, set asc&#x3D;1 for ascending order | [optional] 

### Return type

[**List[PluginSummaryStatsDTO]**](PluginSummaryStatsDTO.md)

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

# **list_plugins_by_statistic1**
> List[PluginSummaryStatsDTO] list_plugins_by_statistic1(stats_type, page_size, asc=asc)

List Plugins by Statistics

List plugins based on statistics, including interactive statistical data.

### Example

* Bearer Authentication (bearerAuth):
```python
import time
import os
import freechat-sdk
from freechat-sdk.models.plugin_summary_stats_dto import PluginSummaryStatsDTO
from freechat-sdk.rest import ApiException
from pprint import pprint

# Defining the host is optional and defaults to http://127.0.0.1:8080
# See configuration.py for a list of all supported configuration parameters.
configuration = freechat-sdk.Configuration(
    host = "http://127.0.0.1:8080"
)

# The client must configure the authentication and authorization parameters
# in accordance with the API server security policy.
# Examples for each auth method are provided below, use the example that
# satisfies your auth use case.

# Configure Bearer authorization: bearerAuth
configuration = freechat-sdk.Configuration(
    access_token = os.environ["BEARER_TOKEN"]
)

# Enter a context with an instance of the API client
with freechat-sdk.ApiClient(configuration) as api_client:
    # Create an instance of the API class
    api_instance = freechat-sdk.InteractiveStatisticsApi(api_client)
    stats_type = 'stats_type_example' # str | Statistics type: view_count | refer_count | recommend_count | score
    page_size = 56 # int | Maximum quantity
    asc = 'asc_example' # str | Default is descending order, set asc=1 for ascending order (optional)

    try:
        # List Plugins by Statistics
        api_response = api_instance.list_plugins_by_statistic1(stats_type, page_size, asc=asc)
        print("The response of InteractiveStatisticsApi->list_plugins_by_statistic1:\n")
        pprint(api_response)
    except Exception as e:
        print("Exception when calling InteractiveStatisticsApi->list_plugins_by_statistic1: %s\n" % e)
```



### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **stats_type** | **str**| Statistics type: view_count | refer_count | recommend_count | score | 
 **page_size** | **int**| Maximum quantity | 
 **asc** | **str**| Default is descending order, set asc&#x3D;1 for ascending order | [optional] 

### Return type

[**List[PluginSummaryStatsDTO]**](PluginSummaryStatsDTO.md)

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

# **list_plugins_by_statistic2**
> List[PluginSummaryStatsDTO] list_plugins_by_statistic2(stats_type, asc=asc)

List Plugins by Statistics

List plugins based on statistics, including interactive statistical data.

### Example

* Bearer Authentication (bearerAuth):
```python
import time
import os
import freechat-sdk
from freechat-sdk.models.plugin_summary_stats_dto import PluginSummaryStatsDTO
from freechat-sdk.rest import ApiException
from pprint import pprint

# Defining the host is optional and defaults to http://127.0.0.1:8080
# See configuration.py for a list of all supported configuration parameters.
configuration = freechat-sdk.Configuration(
    host = "http://127.0.0.1:8080"
)

# The client must configure the authentication and authorization parameters
# in accordance with the API server security policy.
# Examples for each auth method are provided below, use the example that
# satisfies your auth use case.

# Configure Bearer authorization: bearerAuth
configuration = freechat-sdk.Configuration(
    access_token = os.environ["BEARER_TOKEN"]
)

# Enter a context with an instance of the API client
with freechat-sdk.ApiClient(configuration) as api_client:
    # Create an instance of the API class
    api_instance = freechat-sdk.InteractiveStatisticsApi(api_client)
    stats_type = 'stats_type_example' # str | Statistics type: view_count | refer_count | recommend_count | score
    asc = 'asc_example' # str | Default is descending order, set asc=1 for ascending order (optional)

    try:
        # List Plugins by Statistics
        api_response = api_instance.list_plugins_by_statistic2(stats_type, asc=asc)
        print("The response of InteractiveStatisticsApi->list_plugins_by_statistic2:\n")
        pprint(api_response)
    except Exception as e:
        print("Exception when calling InteractiveStatisticsApi->list_plugins_by_statistic2: %s\n" % e)
```



### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **stats_type** | **str**| Statistics type: view_count | refer_count | recommend_count | score | 
 **asc** | **str**| Default is descending order, set asc&#x3D;1 for ascending order | [optional] 

### Return type

[**List[PluginSummaryStatsDTO]**](PluginSummaryStatsDTO.md)

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

# **list_prompts_by_statistic**
> List[PromptSummaryStatsDTO] list_prompts_by_statistic(stats_type, page_size, asc=asc)

List Prompts by Statistics

List prompts based on statistics, including interactive statistical data.

### Example

* Bearer Authentication (bearerAuth):
```python
import time
import os
import freechat-sdk
from freechat-sdk.models.prompt_summary_stats_dto import PromptSummaryStatsDTO
from freechat-sdk.rest import ApiException
from pprint import pprint

# Defining the host is optional and defaults to http://127.0.0.1:8080
# See configuration.py for a list of all supported configuration parameters.
configuration = freechat-sdk.Configuration(
    host = "http://127.0.0.1:8080"
)

# The client must configure the authentication and authorization parameters
# in accordance with the API server security policy.
# Examples for each auth method are provided below, use the example that
# satisfies your auth use case.

# Configure Bearer authorization: bearerAuth
configuration = freechat-sdk.Configuration(
    access_token = os.environ["BEARER_TOKEN"]
)

# Enter a context with an instance of the API client
with freechat-sdk.ApiClient(configuration) as api_client:
    # Create an instance of the API class
    api_instance = freechat-sdk.InteractiveStatisticsApi(api_client)
    stats_type = 'stats_type_example' # str | Statistics type: view_count | refer_count | recommend_count | score
    page_size = 56 # int | Maximum quantity
    asc = 'asc_example' # str | Default is descending order, set asc=1 for ascending order (optional)

    try:
        # List Prompts by Statistics
        api_response = api_instance.list_prompts_by_statistic(stats_type, page_size, asc=asc)
        print("The response of InteractiveStatisticsApi->list_prompts_by_statistic:\n")
        pprint(api_response)
    except Exception as e:
        print("Exception when calling InteractiveStatisticsApi->list_prompts_by_statistic: %s\n" % e)
```



### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **stats_type** | **str**| Statistics type: view_count | refer_count | recommend_count | score | 
 **page_size** | **int**| Maximum quantity | 
 **asc** | **str**| Default is descending order, set asc&#x3D;1 for ascending order | [optional] 

### Return type

[**List[PromptSummaryStatsDTO]**](PromptSummaryStatsDTO.md)

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

# **list_prompts_by_statistic1**
> List[PromptSummaryStatsDTO] list_prompts_by_statistic1(stats_type, page_size, page_num, asc=asc)

List Prompts by Statistics

List prompts based on statistics, including interactive statistical data.

### Example

* Bearer Authentication (bearerAuth):
```python
import time
import os
import freechat-sdk
from freechat-sdk.models.prompt_summary_stats_dto import PromptSummaryStatsDTO
from freechat-sdk.rest import ApiException
from pprint import pprint

# Defining the host is optional and defaults to http://127.0.0.1:8080
# See configuration.py for a list of all supported configuration parameters.
configuration = freechat-sdk.Configuration(
    host = "http://127.0.0.1:8080"
)

# The client must configure the authentication and authorization parameters
# in accordance with the API server security policy.
# Examples for each auth method are provided below, use the example that
# satisfies your auth use case.

# Configure Bearer authorization: bearerAuth
configuration = freechat-sdk.Configuration(
    access_token = os.environ["BEARER_TOKEN"]
)

# Enter a context with an instance of the API client
with freechat-sdk.ApiClient(configuration) as api_client:
    # Create an instance of the API class
    api_instance = freechat-sdk.InteractiveStatisticsApi(api_client)
    stats_type = 'stats_type_example' # str | Statistics type: view_count | refer_count | recommend_count | score
    page_size = 56 # int | Maximum quantity
    page_num = 56 # int | Current page number
    asc = 'asc_example' # str | Default is descending order, set asc=1 for ascending order (optional)

    try:
        # List Prompts by Statistics
        api_response = api_instance.list_prompts_by_statistic1(stats_type, page_size, page_num, asc=asc)
        print("The response of InteractiveStatisticsApi->list_prompts_by_statistic1:\n")
        pprint(api_response)
    except Exception as e:
        print("Exception when calling InteractiveStatisticsApi->list_prompts_by_statistic1: %s\n" % e)
```



### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **stats_type** | **str**| Statistics type: view_count | refer_count | recommend_count | score | 
 **page_size** | **int**| Maximum quantity | 
 **page_num** | **int**| Current page number | 
 **asc** | **str**| Default is descending order, set asc&#x3D;1 for ascending order | [optional] 

### Return type

[**List[PromptSummaryStatsDTO]**](PromptSummaryStatsDTO.md)

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

# **list_prompts_by_statistic2**
> List[PromptSummaryStatsDTO] list_prompts_by_statistic2(stats_type, asc=asc)

List Prompts by Statistics

List prompts based on statistics, including interactive statistical data.

### Example

* Bearer Authentication (bearerAuth):
```python
import time
import os
import freechat-sdk
from freechat-sdk.models.prompt_summary_stats_dto import PromptSummaryStatsDTO
from freechat-sdk.rest import ApiException
from pprint import pprint

# Defining the host is optional and defaults to http://127.0.0.1:8080
# See configuration.py for a list of all supported configuration parameters.
configuration = freechat-sdk.Configuration(
    host = "http://127.0.0.1:8080"
)

# The client must configure the authentication and authorization parameters
# in accordance with the API server security policy.
# Examples for each auth method are provided below, use the example that
# satisfies your auth use case.

# Configure Bearer authorization: bearerAuth
configuration = freechat-sdk.Configuration(
    access_token = os.environ["BEARER_TOKEN"]
)

# Enter a context with an instance of the API client
with freechat-sdk.ApiClient(configuration) as api_client:
    # Create an instance of the API class
    api_instance = freechat-sdk.InteractiveStatisticsApi(api_client)
    stats_type = 'stats_type_example' # str | Statistics type: view_count | refer_count | recommend_count | score
    asc = 'asc_example' # str | Default is descending order, set asc=1 for ascending order (optional)

    try:
        # List Prompts by Statistics
        api_response = api_instance.list_prompts_by_statistic2(stats_type, asc=asc)
        print("The response of InteractiveStatisticsApi->list_prompts_by_statistic2:\n")
        pprint(api_response)
    except Exception as e:
        print("Exception when calling InteractiveStatisticsApi->list_prompts_by_statistic2: %s\n" % e)
```



### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **stats_type** | **str**| Statistics type: view_count | refer_count | recommend_count | score | 
 **asc** | **str**| Default is descending order, set asc&#x3D;1 for ascending order | [optional] 

### Return type

[**List[PromptSummaryStatsDTO]**](PromptSummaryStatsDTO.md)

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

