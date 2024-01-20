# coding: utf-8

"""
    FreeChat OpenAPI Definition

    https://github.com/freechat-fun/freechat

    The version of the OpenAPI document: 0.2.11
    Generated by OpenAPI Generator (https://openapi-generator.tech)

    Do not edit the class manually.
"""  # noqa: E501


import unittest

from freechat_sdk.api.ai_service_api import AIServiceApi


class TestAIServiceApi(unittest.TestCase):
    """AIServiceApi unit test stubs"""

    def setUp(self) -> None:
        self.api = AIServiceApi()

    def tearDown(self) -> None:
        pass

    def test_add_ai_api_key(self) -> None:
        """Test case for add_ai_api_key

        Add Model Provider Credential
        """
        pass

    def test_delete_ai_api_key(self) -> None:
        """Test case for delete_ai_api_key

        Delete Credential of Model Provider
        """
        pass

    def test_disable_ai_api_key(self) -> None:
        """Test case for disable_ai_api_key

        Disable Model Provider Credential
        """
        pass

    def test_enable_ai_api_key(self) -> None:
        """Test case for enable_ai_api_key

        Enable Model Provider Credential
        """
        pass

    def test_get_ai_api_key(self) -> None:
        """Test case for get_ai_api_key

        Get credential of Model Provider
        """
        pass

    def test_get_ai_model_info(self) -> None:
        """Test case for get_ai_model_info

        Get Model Information
        """
        pass

    def test_list_ai_api_keys(self) -> None:
        """Test case for list_ai_api_keys

        List Credentials of Model Provider
        """
        pass

    def test_list_ai_model_info(self) -> None:
        """Test case for list_ai_model_info

        List Models
        """
        pass

    def test_list_ai_model_info1(self) -> None:
        """Test case for list_ai_model_info1

        List Models
        """
        pass

    def test_list_ai_model_info2(self) -> None:
        """Test case for list_ai_model_info2

        List Models
        """
        pass


if __name__ == '__main__':
    unittest.main()
