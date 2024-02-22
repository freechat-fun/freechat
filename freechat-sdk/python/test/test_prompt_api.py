# coding: utf-8

"""
    FreeChat OpenAPI Definition

    https://github.com/freechat-fun/freechat

    The version of the OpenAPI document: 0.4.0
    Generated by OpenAPI Generator (https://openapi-generator.tech)

    Do not edit the class manually.
"""  # noqa: E501


import unittest

from freechat_sdk.api.prompt_api import PromptApi


class TestPromptApi(unittest.TestCase):
    """PromptApi unit test stubs"""

    def setUp(self) -> None:
        self.api = PromptApi()

    def tearDown(self) -> None:
        pass

    def test_apply_prompt_ref(self) -> None:
        """Test case for apply_prompt_ref

        Apply Parameters to Prompt Record
        """
        pass

    def test_apply_prompt_template(self) -> None:
        """Test case for apply_prompt_template

        Apply Parameters to Prompt Template
        """
        pass

    def test_batch_search_prompt_details(self) -> None:
        """Test case for batch_search_prompt_details

        Batch Search Prompt Details
        """
        pass

    def test_batch_search_prompt_summary(self) -> None:
        """Test case for batch_search_prompt_summary

        Batch Search Prompt Summaries
        """
        pass

    def test_clone_prompt(self) -> None:
        """Test case for clone_prompt

        Clone Prompt
        """
        pass

    def test_clone_prompts(self) -> None:
        """Test case for clone_prompts

        Batch Clone Prompts
        """
        pass

    def test_count_prompts(self) -> None:
        """Test case for count_prompts

        Calculate Number of Prompts
        """
        pass

    def test_create_prompt(self) -> None:
        """Test case for create_prompt

        Create Prompt
        """
        pass

    def test_create_prompts(self) -> None:
        """Test case for create_prompts

        Batch Create Prompts
        """
        pass

    def test_delete_prompt(self) -> None:
        """Test case for delete_prompt

        Delete Prompt
        """
        pass

    def test_delete_prompt_by_name(self) -> None:
        """Test case for delete_prompt_by_name

        Delete Prompt by Name
        """
        pass

    def test_delete_prompts(self) -> None:
        """Test case for delete_prompts

        Batch Delete Prompts
        """
        pass

    def test_exists_prompt_name(self) -> None:
        """Test case for exists_prompt_name

        Check If Prompt Name Exists
        """
        pass

    def test_get_prompt_details(self) -> None:
        """Test case for get_prompt_details

        Get Prompt Details
        """
        pass

    def test_get_prompt_summary(self) -> None:
        """Test case for get_prompt_summary

        Get Prompt Summary
        """
        pass

    def test_list_prompt_versions_by_name(self) -> None:
        """Test case for list_prompt_versions_by_name

        List Versions by Prompt Name
        """
        pass

    def test_new_prompt_name(self) -> None:
        """Test case for new_prompt_name

        Create New Prompt Name
        """
        pass

    def test_publish_prompt(self) -> None:
        """Test case for publish_prompt

        Publish Prompt
        """
        pass

    def test_search_prompt_details(self) -> None:
        """Test case for search_prompt_details

        Search Prompt Details
        """
        pass

    def test_search_prompt_summary(self) -> None:
        """Test case for search_prompt_summary

        Search Prompt Summary
        """
        pass

    def test_send_prompt(self) -> None:
        """Test case for send_prompt

        Send Prompt
        """
        pass

    def test_stream_send_prompt(self) -> None:
        """Test case for stream_send_prompt

        Send Prompt by Streaming Back
        """
        pass

    def test_update_prompt(self) -> None:
        """Test case for update_prompt

        Update Prompt
        """
        pass


if __name__ == '__main__':
    unittest.main()
