# coding: utf-8

# flake8: noqa
"""
    FreeChat OpenAPI Definition

    https://github.com/freechat-fun/freechat

    The version of the OpenAPI document: 0.2.15
    Generated by OpenAPI Generator (https://openapi-generator.tech)

    Do not edit the class manually.
"""  # noqa: E501


# import models into model package
from freechat_sdk.models.agent_create_dto import AgentCreateDTO
from freechat_sdk.models.agent_details_dto import AgentDetailsDTO
from freechat_sdk.models.agent_item_for_name_dto import AgentItemForNameDTO
from freechat_sdk.models.agent_query_dto import AgentQueryDTO
from freechat_sdk.models.agent_query_where import AgentQueryWhere
from freechat_sdk.models.agent_summary_dto import AgentSummaryDTO
from freechat_sdk.models.agent_summary_stats_dto import AgentSummaryStatsDTO
from freechat_sdk.models.agent_update_dto import AgentUpdateDTO
from freechat_sdk.models.ai_api_key_create_dto import AiApiKeyCreateDTO
from freechat_sdk.models.ai_api_key_info_dto import AiApiKeyInfoDTO
from freechat_sdk.models.ai_model_info_dto import AiModelInfoDTO
from freechat_sdk.models.api_token_info_dto import ApiTokenInfoDTO
from freechat_sdk.models.app_config_create_dto import AppConfigCreateDTO
from freechat_sdk.models.app_config_info_dto import AppConfigInfoDTO
from freechat_sdk.models.app_meta_dto import AppMetaDTO
from freechat_sdk.models.character_backend_dto import CharacterBackendDTO
from freechat_sdk.models.character_backend_details_dto import CharacterBackendDetailsDTO
from freechat_sdk.models.character_create_dto import CharacterCreateDTO
from freechat_sdk.models.character_details_dto import CharacterDetailsDTO
from freechat_sdk.models.character_item_for_name_dto import CharacterItemForNameDTO
from freechat_sdk.models.character_query_dto import CharacterQueryDTO
from freechat_sdk.models.character_query_where import CharacterQueryWhere
from freechat_sdk.models.character_summary_dto import CharacterSummaryDTO
from freechat_sdk.models.character_summary_stats_dto import CharacterSummaryStatsDTO
from freechat_sdk.models.character_update_dto import CharacterUpdateDTO
from freechat_sdk.models.chat_content_dto import ChatContentDTO
from freechat_sdk.models.chat_create_dto import ChatCreateDTO
from freechat_sdk.models.chat_message_dto import ChatMessageDTO
from freechat_sdk.models.chat_prompt_content_dto import ChatPromptContentDTO
from freechat_sdk.models.chat_tool_call_dto import ChatToolCallDTO
from freechat_sdk.models.hot_tag_dto import HotTagDTO
from freechat_sdk.models.interactive_stats_dto import InteractiveStatsDTO
from freechat_sdk.models.llm_result_dto import LlmResultDTO
from freechat_sdk.models.llm_token_usage_dto import LlmTokenUsageDTO
from freechat_sdk.models.open_ai_param_dto import OpenAiParamDTO
from freechat_sdk.models.plugin_create_dto import PluginCreateDTO
from freechat_sdk.models.plugin_details_dto import PluginDetailsDTO
from freechat_sdk.models.plugin_query_dto import PluginQueryDTO
from freechat_sdk.models.plugin_query_where import PluginQueryWhere
from freechat_sdk.models.plugin_summary_dto import PluginSummaryDTO
from freechat_sdk.models.plugin_summary_stats_dto import PluginSummaryStatsDTO
from freechat_sdk.models.plugin_update_dto import PluginUpdateDTO
from freechat_sdk.models.prompt_ai_param_dto import PromptAiParamDTO
from freechat_sdk.models.prompt_create_dto import PromptCreateDTO
from freechat_sdk.models.prompt_details_dto import PromptDetailsDTO
from freechat_sdk.models.prompt_item_for_name_dto import PromptItemForNameDTO
from freechat_sdk.models.prompt_query_dto import PromptQueryDTO
from freechat_sdk.models.prompt_query_where import PromptQueryWhere
from freechat_sdk.models.prompt_ref_dto import PromptRefDTO
from freechat_sdk.models.prompt_summary_dto import PromptSummaryDTO
from freechat_sdk.models.prompt_summary_stats_dto import PromptSummaryStatsDTO
from freechat_sdk.models.prompt_task_dto import PromptTaskDTO
from freechat_sdk.models.prompt_task_details_dto import PromptTaskDetailsDTO
from freechat_sdk.models.prompt_template_dto import PromptTemplateDTO
from freechat_sdk.models.prompt_update_dto import PromptUpdateDTO
from freechat_sdk.models.qwen_param_dto import QwenParamDTO
from freechat_sdk.models.sse_emitter import SseEmitter
from freechat_sdk.models.user_basic_info_dto import UserBasicInfoDTO
from freechat_sdk.models.user_details_dto import UserDetailsDTO
from freechat_sdk.models.user_full_details_dto import UserFullDetailsDTO