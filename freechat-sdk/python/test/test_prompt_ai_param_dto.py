# coding: utf-8

"""
    FreeChat OpenAPI Definition

    https://github.com/freechat-fun/freechat

    The version of the OpenAPI document: 0.4.0
    Generated by OpenAPI Generator (https://openapi-generator.tech)

    Do not edit the class manually.
"""  # noqa: E501


import unittest

from freechat_sdk.models.prompt_ai_param_dto import PromptAiParamDTO

class TestPromptAiParamDTO(unittest.TestCase):
    """PromptAiParamDTO unit test stubs"""

    def setUp(self):
        pass

    def tearDown(self):
        pass

    def make_instance(self, include_optional) -> PromptAiParamDTO:
        """Test PromptAiParamDTO
            include_option is a boolean, when False only required
            params are included, when True both required and
            optional params are included """
        # uncomment below to create an instance of `PromptAiParamDTO`
        """
        model = PromptAiParamDTO()
        if include_optional:
            return PromptAiParamDTO(
                prompt = '',
                prompt_template = freechat_sdk.models.prompt_template_dto.PromptTemplateDTO(
                    template = '', 
                    chat_template = freechat_sdk.models.chat_prompt_content_dto.ChatPromptContentDTO(
                        system = '', 
                        message_to_send = freechat_sdk.models.chat_message_dto.ChatMessageDTO(
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
                        messages = [
                            freechat_sdk.models.chat_message_dto.ChatMessageDTO(
                                role = '', 
                                name = '', 
                                context = '', 
                                content_text = '', )
                            ], ), 
                    variables = {
                        'key' : None
                        }, 
                    format = '', ),
                prompt_ref = freechat_sdk.models.prompt_ref_dto.PromptRefDTO(
                    prompt_id = '', 
                    variables = {
                        'key' : None
                        }, 
                    draft = True, ),
                params = {
                    'key' : None
                    }
            )
        else:
            return PromptAiParamDTO(
                params = {
                    'key' : None
                    },
        )
        """

    def testPromptAiParamDTO(self):
        """Test PromptAiParamDTO"""
        # inst_req_only = self.make_instance(include_optional=False)
        # inst_req_and_optional = self.make_instance(include_optional=True)

if __name__ == '__main__':
    unittest.main()
