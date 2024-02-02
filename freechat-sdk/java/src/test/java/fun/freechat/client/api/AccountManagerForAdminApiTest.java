/*
 * FreeChat OpenAPI Definition
 * https://github.com/freechat-fun/freechat
 *
 * The version of the OpenAPI document: 0.2.15
 * 
 *
 * NOTE: This class is auto generated by OpenAPI Generator (https://openapi-generator.tech).
 * https://openapi-generator.tech
 * Do not edit the class manually.
 */


package fun.freechat.client.api;

import fun.freechat.client.ApiException;
import fun.freechat.client.model.ApiTokenInfoDTO;
import java.util.Set;
import fun.freechat.client.model.UserBasicInfoDTO;
import fun.freechat.client.model.UserDetailsDTO;
import fun.freechat.client.model.UserFullDetailsDTO;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * API tests for AccountManagerForAdminApi
 */
@Disabled
public class AccountManagerForAdminApiTest {

    private final AccountManagerForAdminApi api = new AccountManagerForAdminApi();

    /**
     * Create API Token for User.
     *
     * Create an API Token for a specified user, valid for duration seconds.
     *
     * @throws ApiException if the Api call fails
     */
    @Test
    public void createTokenForUserTest() throws ApiException {
        String username = null;
        Long duration = null;
        String response = api.createTokenForUser(username, duration);
        // TODO: test validations
    }

    /**
     * Create User
     *
     * Create user.
     *
     * @throws ApiException if the Api call fails
     */
    @Test
    public void createUserTest() throws ApiException {
        UserFullDetailsDTO userFullDetailsDTO = null;
        Boolean response = api.createUser(userFullDetailsDTO);
        // TODO: test validations
    }

    /**
     * Delete API Token
     *
     * Delete the specified API Token.
     *
     * @throws ApiException if the Api call fails
     */
    @Test
    public void deleteTokenForUserTest() throws ApiException {
        String token = null;
        Boolean response = api.deleteTokenForUser(token);
        // TODO: test validations
    }

    /**
     * Delete User
     *
     * Delete user by username.
     *
     * @throws ApiException if the Api call fails
     */
    @Test
    public void deleteUserTest() throws ApiException {
        String username = null;
        Boolean response = api.deleteUser(username);
        // TODO: test validations
    }

    /**
     * Disable API Token
     *
     * Disable the specified API Token.
     *
     * @throws ApiException if the Api call fails
     */
    @Test
    public void disableTokenForUserTest() throws ApiException {
        String token = null;
        Boolean response = api.disableTokenForUser(token);
        // TODO: test validations
    }

    /**
     * Get User Details
     *
     * Return detailed user information.
     *
     * @throws ApiException if the Api call fails
     */
    @Test
    public void getDetailsOfUserTest() throws ApiException {
        String username = null;
        UserDetailsDTO response = api.getDetailsOfUser(username);
        // TODO: test validations
    }

    /**
     * Get User by API Token
     *
     * Get the detailed user information corresponding to the API Token.
     *
     * @throws ApiException if the Api call fails
     */
    @Test
    public void getUserByTokenTest() throws ApiException {
        String token = null;
        UserDetailsDTO response = api.getUserByToken(token);
        // TODO: test validations
    }

    /**
     * List User Permissions
     *
     * List the user&#39;s permissions.
     *
     * @throws ApiException if the Api call fails
     */
    @Test
    public void listAuthoritiesOfUserTest() throws ApiException {
        String username = null;
        Set<String> response = api.listAuthoritiesOfUser(username);
        // TODO: test validations
    }

    /**
     * Get API Token of User
     *
     * Get the list of API Tokens of the user.
     *
     * @throws ApiException if the Api call fails
     */
    @Test
    public void listTokensOfUserTest() throws ApiException {
        String username = null;
        List<ApiTokenInfoDTO> response = api.listTokensOfUser(username);
        // TODO: test validations
    }

    /**
     * List User Information
     *
     * Return user information by page, return the pageNum page, up to pageSize user information.
     *
     * @throws ApiException if the Api call fails
     */
    @Test
    public void listUsersTest() throws ApiException {
        Long pageSize = null;
        Long pageNum = null;
        List<UserBasicInfoDTO> response = api.listUsers(pageSize, pageNum);
        // TODO: test validations
    }

    /**
     * List User Information
     *
     * Return user information by page, return the pageNum page, up to pageSize user information.
     *
     * @throws ApiException if the Api call fails
     */
    @Test
    public void listUsers1Test() throws ApiException {
        Long pageSize = null;
        List<UserBasicInfoDTO> response = api.listUsers1(pageSize);
        // TODO: test validations
    }

    /**
     * List User Information
     *
     * Return user information by page, return the pageNum page, up to pageSize user information.
     *
     * @throws ApiException if the Api call fails
     */
    @Test
    public void listUsers2Test() throws ApiException {
        List<UserBasicInfoDTO> response = api.listUsers2();
        // TODO: test validations
    }

    /**
     * Update User Permissions
     *
     * Update the user&#39;s permission list.
     *
     * @throws ApiException if the Api call fails
     */
    @Test
    public void updateAuthoritiesOfUserTest() throws ApiException {
        String username = null;
        Set<String> requestBody = null;
        Boolean response = api.updateAuthoritiesOfUser(username, requestBody);
        // TODO: test validations
    }

    /**
     * Update User
     *
     * Update user information.
     *
     * @throws ApiException if the Api call fails
     */
    @Test
    public void updateUserTest() throws ApiException {
        UserFullDetailsDTO userFullDetailsDTO = null;
        Boolean response = api.updateUser(userFullDetailsDTO);
        // TODO: test validations
    }

}
