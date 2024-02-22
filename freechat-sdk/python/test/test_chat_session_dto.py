# coding: utf-8

"""
    FreeChat OpenAPI Definition

    https://github.com/freechat-fun/freechat

    The version of the OpenAPI document: 0.4.0
    Generated by OpenAPI Generator (https://openapi-generator.tech)

    Do not edit the class manually.
"""  # noqa: E501


import unittest

from freechat_sdk.models.chat_session_dto import ChatSessionDTO

class TestChatSessionDTO(unittest.TestCase):
    """ChatSessionDTO unit test stubs"""

    def setUp(self):
        pass

    def tearDown(self):
        pass

    def make_instance(self, include_optional) -> ChatSessionDTO:
        """Test ChatSessionDTO
            include_option is a boolean, when False only required
            params are included, when True both required and
            optional params are included """
        # uncomment below to create an instance of `ChatSessionDTO`
        """
        model = ChatSessionDTO()
        if include_optional:
            return ChatSessionDTO(
                context = freechat_sdk.models.chat_context_dto.ChatContextDTO(
                    request_id = '', 
                    chat_id = '', 
                    gmt_create = datetime.datetime.strptime('2013-10-20 19:20:30.00', '%Y-%m-%d %H:%M:%S.%f'), 
                    gmt_modified = datetime.datetime.strptime('2013-10-20 19:20:30.00', '%Y-%m-%d %H:%M:%S.%f'), 
                    user_nickname = '', 
                    user_profile = '', 
                    character_nickname = '', 
                    about = '', 
                    backend_id = '', 
                    ext = '', ),
                character = freechat_sdk.models.character_summary_dto.CharacterSummaryDTO(
                    request_id = '', 
                    character_id = '', 
                    gmt_create = datetime.datetime.strptime('2013-10-20 19:20:30.00', '%Y-%m-%d %H:%M:%S.%f'), 
                    gmt_modified = datetime.datetime.strptime('2013-10-20 19:20:30.00', '%Y-%m-%d %H:%M:%S.%f'), 
                    visibility = '', 
                    version = 56, 
                    name = '', 
                    description = '', 
                    nickname = '', 
                    avatar = '', 
                    picture = '', 
                    gender = '', 
                    lang = '', 
                    username = '', 
                    tags = [
                        ''
                        ], )
            )
        else:
            return ChatSessionDTO(
        )
        """

    def testChatSessionDTO(self):
        """Test ChatSessionDTO"""
        # inst_req_only = self.make_instance(include_optional=False)
        # inst_req_and_optional = self.make_instance(include_optional=True)

if __name__ == '__main__':
    unittest.main()
