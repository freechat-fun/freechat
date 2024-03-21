/*
 * FreeChat OpenAPI Definition
 * https://github.com/freechat-fun/freechat
 *
 * The version of the OpenAPI document: 0.6.0
 * 
 *
 * NOTE: This class is auto generated by OpenAPI Generator (https://openapi-generator.tech).
 * https://openapi-generator.tech
 * Do not edit the class manually.
 */


package fun.freechat.client.api;

import fun.freechat.client.ApiException;
import fun.freechat.client.model.ApiTokenInfoDTO;
import java.io.File;
import fun.freechat.client.model.UserBasicInfoDTO;
import fun.freechat.client.model.UserDetailsDTO;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * API tests for AccountApi
 */
@Disabled
public class AccountApiTest {

    private final AccountApi api = new AccountApi();

    /**
     * Create API Token
     *
     * Create a timed API Token, valid for {duration} seconds.
     *
     * @throws ApiException if the Api call fails
     */
    @Test
    public void createTokenTest() throws ApiException {
        String response = api.createToken();
        // TODO: test validations
    }

    /**
     * Create API Token
     *
     * Create a timed API Token, valid for {duration} seconds.
     *
     * @throws ApiException if the Api call fails
     */
    @Test
    public void createToken1Test() throws ApiException {
        Long duration = null;
        String response = api.createToken1(duration);
        // TODO: test validations
    }

    /**
     * Delete API Token
     *
     * Delete an API Token.
     *
     * @throws ApiException if the Api call fails
     */
    @Test
    public void deleteTokenTest() throws ApiException {
        String token = null;
        String response = api.deleteToken(token);
        // TODO: test validations
    }

    /**
     * Delete API Token by Id
     *
     * Delete the API token by id.
     *
     * @throws ApiException if the Api call fails
     */
    @Test
    public void deleteTokenByIdTest() throws ApiException {
        Long id = null;
        Boolean response = api.deleteTokenById(id);
        // TODO: test validations
    }

    /**
     * Disable API Token
     *
     * Disable an API Token, the token is not deleted.
     *
     * @throws ApiException if the Api call fails
     */
    @Test
    public void disableTokenTest() throws ApiException {
        String token = null;
        String response = api.disableToken(token);
        // TODO: test validations
    }

    /**
     * Disable API Token by Id
     *
     * Disable the API token by id.
     *
     * @throws ApiException if the Api call fails
     */
    @Test
    public void disableTokenByIdTest() throws ApiException {
        Long id = null;
        Boolean response = api.disableTokenById(id);
        // TODO: test validations
    }

    /**
     * Get API Token by Id
     *
     * Get the API token by id.
     *
     * @throws ApiException if the Api call fails
     */
    @Test
    public void getTokenByIdTest() throws ApiException {
        Long id = null;
        String response = api.getTokenById(id);
        // TODO: test validations
    }

    /**
     * Get User Basic Information
     *
     * Return user basic information, including: username, nickname, avatar link.
     *
     * @throws ApiException if the Api call fails
     */
    @Test
    public void getUserBasicTest() throws ApiException {
        String username = null;
        UserBasicInfoDTO response = api.getUserBasic(username);
        // TODO: test validations
    }

    /**
     * Get User Details
     *
     * Return the detailed user information of the current account, the fields refer to the [standard claims](https://openid.net/specs/openid-connect-core-1_0.html#Claims) of OpenID Connect (OIDC).
     *
     * @throws ApiException if the Api call fails
     */
    @Test
    public void getUserDetailsTest() throws ApiException {
        UserDetailsDTO response = api.getUserDetails();
        // TODO: test validations
    }

    /**
     * List API Tokens
     *
     * List currently valid tokens.
     *
     * @throws ApiException if the Api call fails
     */
    @Test
    public void listTokensTest() throws ApiException {
        List<ApiTokenInfoDTO> response = api.listTokens();
        // TODO: test validations
    }

    /**
     * Update User Details
     *
     * Update the detailed user information of the current account.
     *
     * @throws ApiException if the Api call fails
     */
    @Test
    public void updateUserInfoTest() throws ApiException {
        UserDetailsDTO userDetailsDTO = null;
        Boolean response = api.updateUserInfo(userDetailsDTO);
        // TODO: test validations
    }

    /**
     * Upload User Picture
     *
     * Upload a picture of the user.
     *
     * @throws ApiException if the Api call fails
     */
    @Test
    public void uploadUserPictureTest() throws ApiException {
        File _file = null;
        String response = api.uploadUserPicture(_file);
        // TODO: test validations
    }

}
