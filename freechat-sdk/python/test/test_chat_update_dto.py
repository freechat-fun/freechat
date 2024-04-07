# coding: utf-8

"""
    FreeChat OpenAPI Definition

    https://github.com/freechat-fun/freechat

    The version of the OpenAPI document: 0.7.0
    Generated by OpenAPI Generator (https://openapi-generator.tech)

    Do not edit the class manually.
"""  # noqa: E501


import unittest

from freechat_sdk.models.chat_update_dto import ChatUpdateDTO

class TestChatUpdateDTO(unittest.TestCase):
    """ChatUpdateDTO unit test stubs"""

    def setUp(self):
        pass

    def tearDown(self):
        pass

    def make_instance(self, include_optional) -> ChatUpdateDTO:
        """Test ChatUpdateDTO
            include_option is a boolean, when False only required
            params are included, when True both required and
            optional params are included """
        # uncomment below to create an instance of `ChatUpdateDTO`
        """
        model = ChatUpdateDTO()
        if include_optional:
            return ChatUpdateDTO(
                user_nickname = '',
                user_profile = '',
                character_nickname = '',
                about = '',
                character_id = 56,
                backend_id = '',
                ext = '',
                gmt_read = datetime.datetime.strptime('2013-10-20 19:20:30.00', '%Y-%m-%d %H:%M:%S.%f')
            )
        else:
            return ChatUpdateDTO(
                character_id = 56,
        )
        """

    def testChatUpdateDTO(self):
        """Test ChatUpdateDTO"""
        # inst_req_only = self.make_instance(include_optional=False)
        # inst_req_and_optional = self.make_instance(include_optional=True)

if __name__ == '__main__':
    unittest.main()
