# coding: utf-8

"""
    FreeChat OpenAPI Definition

    https://github.com/freechat-fun/freechat

    The version of the OpenAPI document: 0.1.0
    Generated by OpenAPI Generator (https://openapi-generator.tech)

    Do not edit the class manually.
"""  # noqa: E501


import unittest
import datetime

from freechat-sdk.models.prompt_update_dto import PromptUpdateDTO

class TestPromptUpdateDTO(unittest.TestCase):
    """PromptUpdateDTO unit test stubs"""

    def setUp(self):
        pass

    def tearDown(self):
        pass

    def make_instance(self, include_optional) -> PromptUpdateDTO:
        """Test PromptUpdateDTO
            include_option is a boolean, when False only required
            params are included, when True both required and
            optional params are included """
        # uncomment below to create an instance of `PromptUpdateDTO`
        """
        model = PromptUpdateDTO()
        if include_optional:
            return PromptUpdateDTO(
                parent_id = '',
                visibility = '',
                name = '',
                description = '',
                template = '',
                chat_template = freechat-sdk.models.chat_prompt_content_dto.ChatPromptContentDTO(
                    system = '', 
                    messages_to_send = freechat-sdk.models.chat_message_dto.ChatMessageDTO(
                        role = '', 
                        name = '', 
                        content = '', 
                        tool_call = freechat-sdk.models.chat_tool_call_dto.ChatToolCallDTO(
                            name = '', 
                            arguments = '', ), 
                        gmt_create = datetime.datetime.strptime('2013-10-20 19:20:30.00', '%Y-%m-%d %H:%M:%S.%f'), ), 
                    messages = [
                        freechat-sdk.models.chat_message_dto.ChatMessageDTO(
                            role = '', 
                            name = '', 
                            content = '', 
                            gmt_create = datetime.datetime.strptime('2013-10-20 19:20:30.00', '%Y-%m-%d %H:%M:%S.%f'), )
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
            return PromptUpdateDTO(
                name = '',
        )
        """

    def testPromptUpdateDTO(self):
        """Test PromptUpdateDTO"""
        # inst_req_only = self.make_instance(include_optional=False)
        # inst_req_and_optional = self.make_instance(include_optional=True)

if __name__ == '__main__':
    unittest.main()
