# coding: utf-8

"""
    FreeChat OpenAPI Definition

    https://github.com/freechat-fun/freechat

    The version of the OpenAPI document: 0.6.0
    Generated by OpenAPI Generator (https://openapi-generator.tech)

    Do not edit the class manually.
"""  # noqa: E501


import unittest

from freechat_sdk.models.chat_message_record_dto import ChatMessageRecordDTO

class TestChatMessageRecordDTO(unittest.TestCase):
    """ChatMessageRecordDTO unit test stubs"""

    def setUp(self):
        pass

    def tearDown(self):
        pass

    def make_instance(self, include_optional) -> ChatMessageRecordDTO:
        """Test ChatMessageRecordDTO
            include_option is a boolean, when False only required
            params are included, when True both required and
            optional params are included """
        # uncomment below to create an instance of `ChatMessageRecordDTO`
        """
        model = ChatMessageRecordDTO()
        if include_optional:
            return ChatMessageRecordDTO(
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
                message_id = 56,
                gmt_create = datetime.datetime.strptime('2013-10-20 19:20:30.00', '%Y-%m-%d %H:%M:%S.%f'),
                ext = ''
            )
        else:
            return ChatMessageRecordDTO(
        )
        """

    def testChatMessageRecordDTO(self):
        """Test ChatMessageRecordDTO"""
        # inst_req_only = self.make_instance(include_optional=False)
        # inst_req_and_optional = self.make_instance(include_optional=True)

if __name__ == '__main__':
    unittest.main()
