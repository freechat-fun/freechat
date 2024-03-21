# coding: utf-8

"""
    FreeChat OpenAPI Definition

    https://github.com/freechat-fun/freechat

    The version of the OpenAPI document: 0.6.0
    Generated by OpenAPI Generator (https://openapi-generator.tech)

    Do not edit the class manually.
"""  # noqa: E501


import unittest

from freechat_sdk.models.prompt_create_dto import PromptCreateDTO

class TestPromptCreateDTO(unittest.TestCase):
    """PromptCreateDTO unit test stubs"""

    def setUp(self):
        pass

    def tearDown(self):
        pass

    def make_instance(self, include_optional) -> PromptCreateDTO:
        """Test PromptCreateDTO
            include_option is a boolean, when False only required
            params are included, when True both required and
            optional params are included """
        # uncomment below to create an instance of `PromptCreateDTO`
        """
        model = PromptCreateDTO()
        if include_optional:
            return PromptCreateDTO(
                parent_uid = '',
                visibility = '',
                name = '',
                description = '',
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
                format = '',
                lang = '',
                example = '',
                inputs = '',
                ext = '',
                draft = '',
                tags = [
                    ''
                    ],
                ai_models = [
                    ''
                    ]
            )
        else:
            return PromptCreateDTO(
                name = '',
        )
        """

    def testPromptCreateDTO(self):
        """Test PromptCreateDTO"""
        # inst_req_only = self.make_instance(include_optional=False)
        # inst_req_and_optional = self.make_instance(include_optional=True)

if __name__ == '__main__':
    unittest.main()
