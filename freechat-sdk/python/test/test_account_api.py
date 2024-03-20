# coding: utf-8

"""
    FreeChat OpenAPI Definition

    https://github.com/freechat-fun/freechat

    The version of the OpenAPI document: 0.5.0
    Generated by OpenAPI Generator (https://openapi-generator.tech)

    Do not edit the class manually.
"""  # noqa: E501


import unittest

from freechat_sdk.api.account_api import AccountApi


class TestAccountApi(unittest.TestCase):
    """AccountApi unit test stubs"""

    def setUp(self) -> None:
        self.api = AccountApi()

    def tearDown(self) -> None:
        pass

    def test_create_token(self) -> None:
        """Test case for create_token

        Create API Token
        """
        pass

    def test_create_token1(self) -> None:
        """Test case for create_token1

        Create API Token
        """
        pass

    def test_delete_token(self) -> None:
        """Test case for delete_token

        Delete API Token
        """
        pass

    def test_delete_token_by_id(self) -> None:
        """Test case for delete_token_by_id

        Delete API Token by Id
        """
        pass

    def test_disable_token(self) -> None:
        """Test case for disable_token

        Disable API Token
        """
        pass

    def test_disable_token_by_id(self) -> None:
        """Test case for disable_token_by_id

        Disable API Token by Id
        """
        pass

    def test_get_token_by_id(self) -> None:
        """Test case for get_token_by_id

        Get API Token by Id
        """
        pass

    def test_get_user_basic(self) -> None:
        """Test case for get_user_basic

        Get User Basic Information
        """
        pass

    def test_get_user_details(self) -> None:
        """Test case for get_user_details

        Get User Details
        """
        pass

    def test_list_tokens(self) -> None:
        """Test case for list_tokens

        List API Tokens
        """
        pass

    def test_update_user_info(self) -> None:
        """Test case for update_user_info

        Update User Details
        """
        pass

    def test_upload_user_picture(self) -> None:
        """Test case for upload_user_picture

        Upload User Picture
        """
        pass


if __name__ == '__main__':
    unittest.main()
