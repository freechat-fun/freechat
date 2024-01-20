# coding: utf-8

"""
    FreeChat OpenAPI Definition

    https://github.com/freechat-fun/freechat

    The version of the OpenAPI document: 0.2.11
    Generated by OpenAPI Generator (https://openapi-generator.tech)

    Do not edit the class manually.
"""  # noqa: E501


import unittest
import datetime

from freechat_sdk.models.chat_create_dto import ChatCreateDTO

class TestChatCreateDTO(unittest.TestCase):
    """ChatCreateDTO unit test stubs"""

    def setUp(self):
        pass

    def tearDown(self):
        pass

    def make_instance(self, include_optional) -> ChatCreateDTO:
        """Test ChatCreateDTO
            include_option is a boolean, when False only required
            params are included, when True both required and
            optional params are included """
        # uncomment below to create an instance of `ChatCreateDTO`
        """
        model = ChatCreateDTO()
        if include_optional:
            return ChatCreateDTO(
                user_nickname = '',
                user_profile = '',
                character_nickname = '',
                backend_id = '',
                ext = ''
            )
        else:
            return ChatCreateDTO(
                backend_id = '',
        )
        """

    def testChatCreateDTO(self):
        """Test ChatCreateDTO"""
        # inst_req_only = self.make_instance(include_optional=False)
        # inst_req_and_optional = self.make_instance(include_optional=True)

if __name__ == '__main__':
    unittest.main()
