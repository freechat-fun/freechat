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

from freechat_sdk.models.user_details_dto import UserDetailsDTO

class TestUserDetailsDTO(unittest.TestCase):
    """UserDetailsDTO unit test stubs"""

    def setUp(self):
        pass

    def tearDown(self):
        pass

    def make_instance(self, include_optional) -> UserDetailsDTO:
        """Test UserDetailsDTO
            include_option is a boolean, when False only required
            params are included, when True both required and
            optional params are included """
        # uncomment below to create an instance of `UserDetailsDTO`
        """
        model = UserDetailsDTO()
        if include_optional:
            return UserDetailsDTO(
                request_id = '',
                username = '',
                nickname = '',
                given_name = '',
                middle_name = '',
                family_name = '',
                preferred_username = '',
                profile = '',
                picture = '',
                website = '',
                email = '',
                gender = '',
                birthdate = datetime.datetime.strptime('2013-10-20 19:20:30.00', '%Y-%m-%d %H:%M:%S.%f'),
                zoneinfo = '',
                locale = '',
                phone_number = '',
                updated_at = datetime.datetime.strptime('2013-10-20 19:20:30.00', '%Y-%m-%d %H:%M:%S.%f'),
                platform = '',
                enabled = True,
                locked = True,
                expires_at = datetime.datetime.strptime('2013-10-20 19:20:30.00', '%Y-%m-%d %H:%M:%S.%f'),
                password_expires_at = datetime.datetime.strptime('2013-10-20 19:20:30.00', '%Y-%m-%d %H:%M:%S.%f'),
                address = ''
            )
        else:
            return UserDetailsDTO(
        )
        """

    def testUserDetailsDTO(self):
        """Test UserDetailsDTO"""
        # inst_req_only = self.make_instance(include_optional=False)
        # inst_req_and_optional = self.make_instance(include_optional=True)

if __name__ == '__main__':
    unittest.main()
