# coding: utf-8

"""
    FreeChat OpenAPI Definition

    https://github.com/freechat-fun/freechat

    The version of the OpenAPI document: 0.4.0
    Generated by OpenAPI Generator (https://openapi-generator.tech)

    Do not edit the class manually.
"""  # noqa: E501


import unittest

from freechat_sdk.models.llm_result_dto import LlmResultDTO

class TestLlmResultDTO(unittest.TestCase):
    """LlmResultDTO unit test stubs"""

    def setUp(self):
        pass

    def tearDown(self):
        pass

    def make_instance(self, include_optional) -> LlmResultDTO:
        """Test LlmResultDTO
            include_option is a boolean, when False only required
            params are included, when True both required and
            optional params are included """
        # uncomment below to create an instance of `LlmResultDTO`
        """
        model = LlmResultDTO()
        if include_optional:
            return LlmResultDTO(
                request_id = '',
                text = '',
                message = freechat_sdk.models.chat_message_dto.ChatMessageDTO(
                    role = '', 
                    name = '', 
                    contents = [
                        freechat_sdk.models.chat_content_dto.ChatContentDTO(
                            type = '', 
                            content = '', )
                        ], 
                    tool_calls = [
                        freechat_sdk.models.chat_tool_call_dto.ChatToolCallDTO(
                            id = '', 
                            name = '', 
                            arguments = '', )
                        ], 
                    context = '', 
                    content_text = '', ),
                finish_reason = '',
                token_usage = freechat_sdk.models.llm_token_usage_dto.LlmTokenUsageDTO(
                    input_token_count = 56, 
                    output_token_count = 56, 
                    total_token_count = 56, )
            )
        else:
            return LlmResultDTO(
        )
        """

    def testLlmResultDTO(self):
        """Test LlmResultDTO"""
        # inst_req_only = self.make_instance(include_optional=False)
        # inst_req_and_optional = self.make_instance(include_optional=True)

if __name__ == '__main__':
    unittest.main()
