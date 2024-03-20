# coding: utf-8

"""
    FreeChat OpenAPI Definition

    https://github.com/freechat-fun/freechat

    The version of the OpenAPI document: 0.5.0
    Generated by OpenAPI Generator (https://openapi-generator.tech)

    Do not edit the class manually.
"""  # noqa: E501


import unittest

from freechat_sdk.models.prompt_template_dto import PromptTemplateDTO

class TestPromptTemplateDTO(unittest.TestCase):
    """PromptTemplateDTO unit test stubs"""

    def setUp(self):
        pass

    def tearDown(self):
        pass

    def make_instance(self, include_optional) -> PromptTemplateDTO:
        """Test PromptTemplateDTO
            include_option is a boolean, when False only required
            params are included, when True both required and
            optional params are included """
        # uncomment below to create an instance of `PromptTemplateDTO`
        """
        model = PromptTemplateDTO()
        if include_optional:
            return PromptTemplateDTO(
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
                format = ''
            )
        else:
            return PromptTemplateDTO(
        )
        """

    def testPromptTemplateDTO(self):
        """Test PromptTemplateDTO"""
        # inst_req_only = self.make_instance(include_optional=False)
        # inst_req_and_optional = self.make_instance(include_optional=True)

if __name__ == '__main__':
    unittest.main()
