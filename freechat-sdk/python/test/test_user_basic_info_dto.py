# coding: utf-8

"""
    FreeChat OpenAPI Definition

    https://github.com/freechat-fun/freechat

    The version of the OpenAPI document: 0.2.0
    Generated by OpenAPI Generator (https://openapi-generator.tech)

    Do not edit the class manually.
"""  # noqa: E501


import unittest
import datetime

from freechat-sdk.models.user_basic_info_dto import UserBasicInfoDTO

class TestUserBasicInfoDTO(unittest.TestCase):
    """UserBasicInfoDTO unit test stubs"""

    def setUp(self):
        pass

    def tearDown(self):
        pass

    def make_instance(self, include_optional) -> UserBasicInfoDTO:
        """Test UserBasicInfoDTO
            include_option is a boolean, when False only required
            params are included, when True both required and
            optional params are included """
        # uncomment below to create an instance of `UserBasicInfoDTO`
        """
        model = UserBasicInfoDTO()
        if include_optional:
            return UserBasicInfoDTO(
                request_id = '',
                username = '',
                nickname = '',
                picture = ''
            )
        else:
            return UserBasicInfoDTO(
        )
        """

    def testUserBasicInfoDTO(self):
        """Test UserBasicInfoDTO"""
        # inst_req_only = self.make_instance(include_optional=False)
        # inst_req_and_optional = self.make_instance(include_optional=True)

if __name__ == '__main__':
    unittest.main()
