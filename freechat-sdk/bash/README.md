# FreeChat OpenAPI Definition Bash client

## Overview

This is a Bash client script for accessing FreeChat OpenAPI Definition service.

The script uses cURL underneath for making all REST calls.

## Usage

```shell
# Make sure the script has executable rights
$ chmod u+x freechat

# Print the list of operations available on the service
$ ./freechat -h

# Print the service description
$ ./freechat --about

# Print detailed information about specific operation
$ ./freechat <operationId> -h

# Make GET request
./freechat --host http://<hostname>:<port> --accept xml <operationId> <queryParam1>=<value1> <header_key1>:<header_value2>

# Make GET request using arbitrary curl options (must be passed before <operationId>) to an SSL service using username:password
freechat -k -sS --tlsv1.2 --host https://<hostname> -u <user>:<password> --accept xml <operationId> <queryParam1>=<value1> <header_key1>:<header_value2>

# Make POST request
$ echo '<body_content>' | freechat --host <hostname> --content-type json <operationId> -

# Make POST request with simple JSON content, e.g.:
# {
#   "key1": "value1",
#   "key2": "value2",
#   "key3": 23
# }
$ echo '<body_content>' | freechat --host <hostname> --content-type json <operationId> key1==value1 key2=value2 key3:=23 -

# Make POST request with form data
$ freechat --host <hostname> <operationId> key1:=value1 key2:=value2 key3:=23

# Preview the cURL command without actually executing it
$ freechat --host http://<hostname>:<port> --dry-run <operationid>

```

## Docker image

You can easily create a Docker image containing a preconfigured environment
for using the REST Bash client including working autocompletion and short
welcome message with basic instructions, using the generated Dockerfile:

```shell
docker build -t my-rest-client .
docker run -it my-rest-client
```

By default you will be logged into a Zsh environment which has much more
advanced auto completion, but you can switch to Bash, where basic autocompletion
is also available.

## Shell completion

### Bash

The generated bash-completion script can be either directly loaded to the current Bash session using:

```shell
source freechat.bash-completion
```

Alternatively, the script can be copied to the `/etc/bash-completion.d` (or on OSX with Homebrew to `/usr/local/etc/bash-completion.d`):

```shell
sudo cp freechat.bash-completion /etc/bash-completion.d/freechat
```

#### OS X

On OSX you might need to install bash-completion using Homebrew:

```shell
brew install bash-completion
```

and add the following to the `~/.bashrc`:

```shell
if [ -f $(brew --prefix)/etc/bash_completion ]; then
  . $(brew --prefix)/etc/bash_completion
fi
```

### Zsh

In Zsh, the generated `_freechat` Zsh completion file must be copied to one of the folders under `$FPATH` variable.

## Documentation for API Endpoints

All URIs are relative to **

Class | Method | HTTP request | Description
------------ | ------------- | ------------- | -------------
*AIServiceApi* | [**addAiApiKey**](docs/AIServiceApi.md#addaiapikey) | **POST** /api/v2/ai/apikey | Add Model Provider Credential
*AIServiceApi* | [**deleteAiApiKey**](docs/AIServiceApi.md#deleteaiapikey) | **DELETE** /api/v2/ai/apikey/{id} | Delete Credential of Model Provider
*AIServiceApi* | [**disableAiApiKey**](docs/AIServiceApi.md#disableaiapikey) | **PUT** /api/v2/ai/apikey/disable/{id} | Disable Model Provider Credential
*AIServiceApi* | [**enableAiApiKey**](docs/AIServiceApi.md#enableaiapikey) | **PUT** /api/v2/ai/apikey/enable/{id} | Enable Model Provider Credential
*AIServiceApi* | [**getAiApiKey**](docs/AIServiceApi.md#getaiapikey) | **GET** /api/v2/ai/apikey/{id} | Get credential of Model Provider
*AIServiceApi* | [**listAiApiKeys**](docs/AIServiceApi.md#listaiapikeys) | **GET** /api/v2/ai/apikeys/{provider} | List Credentials of Model Provider
*AccountApi* | [**createToken**](docs/AccountApi.md#createtoken) | **POST** /api/v2/account/token/{duration} | Create API Token
*AccountApi* | [**createToken1**](docs/AccountApi.md#createtoken1) | **POST** /api/v2/account/token | Create API Token
*AccountApi* | [**deleteToken**](docs/AccountApi.md#deletetoken) | **DELETE** /api/v2/account/token/{token} | Delete API Token
*AccountApi* | [**deleteTokenById**](docs/AccountApi.md#deletetokenbyid) | **DELETE** /api/v2/account/token/id/{id} | Delete API Token by Id
*AccountApi* | [**disableToken**](docs/AccountApi.md#disabletoken) | **PUT** /api/v2/account/token/{token} | Disable API Token
*AccountApi* | [**disableTokenById**](docs/AccountApi.md#disabletokenbyid) | **PUT** /api/v2/account/token/id/{id} | Disable API Token by Id
*AccountApi* | [**getTokenById**](docs/AccountApi.md#gettokenbyid) | **GET** /api/v2/account/token/id/{id} | Get API Token by Id
*AccountApi* | [**getUserBasic**](docs/AccountApi.md#getuserbasic) | **GET** /api/v2/account/basic/{username} | Get User Basic Information
*AccountApi* | [**getUserBasic1**](docs/AccountApi.md#getuserbasic1) | **GET** /api/v2/account/basic | Get User Basic Information
*AccountApi* | [**getUserDetails**](docs/AccountApi.md#getuserdetails) | **GET** /api/v2/account/details | Get User Details
*AccountApi* | [**listTokens**](docs/AccountApi.md#listtokens) | **GET** /api/v2/account/tokens | List API Tokens
*AccountApi* | [**updateUserInfo**](docs/AccountApi.md#updateuserinfo) | **PUT** /api/v2/account/details | Update User Details
*AccountApi* | [**uploadUserPicture**](docs/AccountApi.md#uploaduserpicture) | **POST** /api/v2/account/picture | Upload User Picture
*AccountManagerForAdminApi* | [**createTokenForUser**](docs/AccountManagerForAdminApi.md#createtokenforuser) | **POST** /api/v2/admin/token/{username}/{duration} | Create API Token for User.
*AccountManagerForAdminApi* | [**createUser**](docs/AccountManagerForAdminApi.md#createuser) | **POST** /api/v2/admin/user | Create User
*AccountManagerForAdminApi* | [**deleteTokenForUser**](docs/AccountManagerForAdminApi.md#deletetokenforuser) | **DELETE** /api/v2/admin/token/{token} | Delete API Token
*AccountManagerForAdminApi* | [**deleteUser**](docs/AccountManagerForAdminApi.md#deleteuser) | **DELETE** /api/v2/admin/user/{username} | Delete User
*AccountManagerForAdminApi* | [**disableTokenForUser**](docs/AccountManagerForAdminApi.md#disabletokenforuser) | **PUT** /api/v2/admin/token/{token} | Disable API Token
*AccountManagerForAdminApi* | [**getDetailsOfUser**](docs/AccountManagerForAdminApi.md#getdetailsofuser) | **GET** /api/v2/admin/user/{username} | Get User Details
*AccountManagerForAdminApi* | [**getUserByToken**](docs/AccountManagerForAdminApi.md#getuserbytoken) | **GET** /api/v2/admin/tokenBy/{token} | Get User by API Token
*AccountManagerForAdminApi* | [**listAuthoritiesOfUser**](docs/AccountManagerForAdminApi.md#listauthoritiesofuser) | **GET** /api/v2/admin/authority/{username} | List User Permissions
*AccountManagerForAdminApi* | [**listTokensOfUser**](docs/AccountManagerForAdminApi.md#listtokensofuser) | **GET** /api/v2/admin/token/{username} | Get API Token of User
*AccountManagerForAdminApi* | [**listUsers**](docs/AccountManagerForAdminApi.md#listusers) | **GET** /api/v2/admin/users/{pageSize}/{pageNum} | List User Information
*AccountManagerForAdminApi* | [**listUsers1**](docs/AccountManagerForAdminApi.md#listusers1) | **GET** /api/v2/admin/users | List User Information
*AccountManagerForAdminApi* | [**listUsers2**](docs/AccountManagerForAdminApi.md#listusers2) | **GET** /api/v2/admin/users/{pageSize} | List User Information
*AccountManagerForAdminApi* | [**updateAuthoritiesOfUser**](docs/AccountManagerForAdminApi.md#updateauthoritiesofuser) | **PUT** /api/v2/admin/authority/{username} | Update User Permissions
*AccountManagerForAdminApi* | [**updateUser**](docs/AccountManagerForAdminApi.md#updateuser) | **PUT** /api/v2/admin/user | Update User
*AgentApi* | [**batchSearchAgentDetails**](docs/AgentApi.md#batchsearchagentdetails) | **POST** /api/v2/agent/batch/details/search | Batch Search Agent Details
*AgentApi* | [**batchSearchAgentSummary**](docs/AgentApi.md#batchsearchagentsummary) | **POST** /api/v2/agent/batch/search | Batch Search Agent Summaries
*AgentApi* | [**cloneAgent**](docs/AgentApi.md#cloneagent) | **POST** /api/v2/agent/clone/{agentId} | Clone Agent
*AgentApi* | [**cloneAgents**](docs/AgentApi.md#cloneagents) | **POST** /api/v2/agent/batch/clone | Batch Clone Agents
*AgentApi* | [**countAgents**](docs/AgentApi.md#countagents) | **POST** /api/v2/agent/count | Calculate Number of Agents
*AgentApi* | [**createAgent**](docs/AgentApi.md#createagent) | **POST** /api/v2/agent | Create Agent
*AgentApi* | [**createAgents**](docs/AgentApi.md#createagents) | **POST** /api/v2/agent/batch | Batch Create Agents
*AgentApi* | [**deleteAgent**](docs/AgentApi.md#deleteagent) | **DELETE** /api/v2/agent/{agentId} | Delete Agent
*AgentApi* | [**deleteAgents**](docs/AgentApi.md#deleteagents) | **DELETE** /api/v2/agent/batch/delete | Batch Delete Agents
*AgentApi* | [**getAgentDetails**](docs/AgentApi.md#getagentdetails) | **GET** /api/v2/agent/details/{agentId} | Get Agent Details
*AgentApi* | [**getAgentSummary**](docs/AgentApi.md#getagentsummary) | **GET** /api/v2/agent/summary/{agentId} | Get Agent Summary
*AgentApi* | [**listAgentVersionsByName**](docs/AgentApi.md#listagentversionsbyname) | **POST** /api/v2/agent/versions/{name} | List Versions by Agent Name
*AgentApi* | [**publishAgent**](docs/AgentApi.md#publishagent) | **POST** /api/v2/agent/publish/{agentId}/{visibility} | Publish Agent
*AgentApi* | [**searchAgentDetails**](docs/AgentApi.md#searchagentdetails) | **POST** /api/v2/agent/details/search | Search Agent Details
*AgentApi* | [**searchAgentSummary**](docs/AgentApi.md#searchagentsummary) | **POST** /api/v2/agent/search | Search Agent Summary
*AgentApi* | [**updateAgent**](docs/AgentApi.md#updateagent) | **PUT** /api/v2/agent/{agentId} | Update Agent
*AppConfigForAdminApi* | [**getDefaultConfig**](docs/AppConfigForAdminApi.md#getdefaultconfig) | **GET** /api/v2/admin/app/configs/default | Get Default Config
*AppMetaForAdminApi* | [**getAppMeta**](docs/AppMetaForAdminApi.md#getappmeta) | **GET** /api/v2/admin/app/meta | Get Application Information
*CharacterApi* | [**addCharacterBackend**](docs/CharacterApi.md#addcharacterbackend) | **POST** /api/v2/character/backend/{characterUid} | Add Character Backend
*CharacterApi* | [**batchSearchCharacterDetails**](docs/CharacterApi.md#batchsearchcharacterdetails) | **POST** /api/v2/character/batch/details/search | Batch Search Character Details
*CharacterApi* | [**batchSearchCharacterSummary**](docs/CharacterApi.md#batchsearchcharactersummary) | **POST** /api/v2/character/batch/search | Batch Search Character Summaries
*CharacterApi* | [**cloneCharacter**](docs/CharacterApi.md#clonecharacter) | **POST** /api/v2/character/clone/{characterId} | Clone Character
*CharacterApi* | [**countCharacters**](docs/CharacterApi.md#countcharacters) | **POST** /api/v2/character/count | Calculate Number of Characters
*CharacterApi* | [**countPublicCharacters**](docs/CharacterApi.md#countpubliccharacters) | **POST** /api/v2/public/character/count | Calculate Number of Public Characters
*CharacterApi* | [**createCharacter**](docs/CharacterApi.md#createcharacter) | **POST** /api/v2/character | Create Character
*CharacterApi* | [**deleteCharacter**](docs/CharacterApi.md#deletecharacter) | **DELETE** /api/v2/character/{characterId} | Delete Character
*CharacterApi* | [**deleteCharacterByName**](docs/CharacterApi.md#deletecharacterbyname) | **DELETE** /api/v2/character/name/{name} | Delete Character by Name
*CharacterApi* | [**deleteCharacterByUid**](docs/CharacterApi.md#deletecharacterbyuid) | **DELETE** /api/v2/character/uid/{characterUid} | Delete Character by Uid
*CharacterApi* | [**deleteCharacterDocument**](docs/CharacterApi.md#deletecharacterdocument) | **DELETE** /api/v2/character/document/{key} | Delete Character Document
*CharacterApi* | [**deleteCharacterPicture**](docs/CharacterApi.md#deletecharacterpicture) | **DELETE** /api/v2/character/picture/{key} | Delete Character Picture
*CharacterApi* | [**deleteCharacterVideo**](docs/CharacterApi.md#deletecharactervideo) | **DELETE** /api/v2/character/video/{key} | Delete Character Video
*CharacterApi* | [**deleteCharacterVoice**](docs/CharacterApi.md#deletecharactervoice) | **DELETE** /api/v2/character/voice/{characterBackendId}/{key} | Delete Character Voice
*CharacterApi* | [**existsCharacterName**](docs/CharacterApi.md#existscharactername) | **GET** /api/v2/character/exists/name/{name} | Check If Character Name Exists
*CharacterApi* | [**exportCharacter**](docs/CharacterApi.md#exportcharacter) | **GET** /api/v2/character/export/{characterId} | Export Character Configuration
*CharacterApi* | [**getCharacterDetails**](docs/CharacterApi.md#getcharacterdetails) | **GET** /api/v2/character/details/{characterId} | Get Character Details
*CharacterApi* | [**getCharacterLatestIdByName**](docs/CharacterApi.md#getcharacterlatestidbyname) | **POST** /api/v2/character/latest/{name} | Get Latest Character Id by Name
*CharacterApi* | [**getCharacterSummary**](docs/CharacterApi.md#getcharactersummary) | **GET** /api/v2/character/summary/{characterId} | Get Character Summary
*CharacterApi* | [**getDefaultCharacterBackend**](docs/CharacterApi.md#getdefaultcharacterbackend) | **GET** /api/v2/character/backend/default/{characterUid} | Get Default Character Backend
*CharacterApi* | [**importCharacter**](docs/CharacterApi.md#importcharacter) | **POST** /api/v2/character/import | Import Character Configuration
*CharacterApi* | [**listCharacterBackendIds**](docs/CharacterApi.md#listcharacterbackendids) | **GET** /api/v2/character/backend/ids/{characterUid} | List Character Backend ids
*CharacterApi* | [**listCharacterBackends**](docs/CharacterApi.md#listcharacterbackends) | **GET** /api/v2/character/backends/{characterUid} | List Character Backends
*CharacterApi* | [**listCharacterDocuments**](docs/CharacterApi.md#listcharacterdocuments) | **GET** /api/v2/character/documents/{characterUid} | List Character Documents
*CharacterApi* | [**listCharacterPictures**](docs/CharacterApi.md#listcharacterpictures) | **GET** /api/v2/character/pictures/{characterUid} | List Character Pictures
*CharacterApi* | [**listCharacterVersionsByName**](docs/CharacterApi.md#listcharacterversionsbyname) | **POST** /api/v2/character/versions/{name} | List Versions by Character Name
*CharacterApi* | [**listCharacterVideos**](docs/CharacterApi.md#listcharactervideos) | **GET** /api/v2/character/videos/{characterUid} | List Character Videos
*CharacterApi* | [**listCharacterVoices**](docs/CharacterApi.md#listcharactervoices) | **GET** /api/v2/character/voices/{characterBackendId} | List Character Voices
*CharacterApi* | [**newCharacterName**](docs/CharacterApi.md#newcharactername) | **GET** /api/v2/character/create/name/{desired} | Create New Character Name
*CharacterApi* | [**publishCharacter**](docs/CharacterApi.md#publishcharacter) | **POST** /api/v2/character/publish/{characterId}/{visibility} | Publish Character
*CharacterApi* | [**publishCharacter1**](docs/CharacterApi.md#publishcharacter1) | **POST** /api/v2/character/publish/{characterId} | Publish Character
*CharacterApi* | [**removeCharacterBackend**](docs/CharacterApi.md#removecharacterbackend) | **DELETE** /api/v2/character/backend/{characterBackendId} | Remove Character Backend
*CharacterApi* | [**searchCharacterDetails**](docs/CharacterApi.md#searchcharacterdetails) | **POST** /api/v2/character/details/search | Search Character Details
*CharacterApi* | [**searchCharacterSummary**](docs/CharacterApi.md#searchcharactersummary) | **POST** /api/v2/character/search | Search Character Summary
*CharacterApi* | [**searchPublicCharacterSummary**](docs/CharacterApi.md#searchpubliccharactersummary) | **POST** /api/v2/public/character/search | Search Public Character Summary
*CharacterApi* | [**setDefaultCharacterBackend**](docs/CharacterApi.md#setdefaultcharacterbackend) | **PUT** /api/v2/character/backend/default/{characterBackendId} | Set Default Character Backend
*CharacterApi* | [**updateCharacter**](docs/CharacterApi.md#updatecharacter) | **PUT** /api/v2/character/{characterId} | Update Character
*CharacterApi* | [**updateCharacterBackend**](docs/CharacterApi.md#updatecharacterbackend) | **PUT** /api/v2/character/backend/{characterBackendId} | Update Character Backend
*CharacterApi* | [**uploadCharacterAvatar**](docs/CharacterApi.md#uploadcharacteravatar) | **POST** /api/v2/character/avatar/{characterUid} | Upload Character Avatar
*CharacterApi* | [**uploadCharacterDocument**](docs/CharacterApi.md#uploadcharacterdocument) | **POST** /api/v2/character/document/{characterUid} | Upload Character Document
*CharacterApi* | [**uploadCharacterPicture**](docs/CharacterApi.md#uploadcharacterpicture) | **POST** /api/v2/character/picture/{characterUid} | Upload Character Picture
*CharacterApi* | [**uploadCharacterVideo**](docs/CharacterApi.md#uploadcharactervideo) | **POST** /api/v2/character/video/{characterUid} | Upload Character Video
*CharacterApi* | [**uploadCharacterVoice**](docs/CharacterApi.md#uploadcharactervoice) | **POST** /api/v2/character/voice/{characterBackendId} | Upload Character Voice
*ChatApi* | [**clearMemory**](docs/ChatApi.md#clearmemory) | **DELETE** /api/v2/chat/memory/{chatId} | Clear Memory
*ChatApi* | [**deleteChat**](docs/ChatApi.md#deletechat) | **DELETE** /api/v2/chat/{chatId} | Delete Chat Session
*ChatApi* | [**getDefaultChatId**](docs/ChatApi.md#getdefaultchatid) | **GET** /api/v2/chat/{characterUid} | Get Default Chat
*ChatApi* | [**getMemoryUsage**](docs/ChatApi.md#getmemoryusage) | **GET** /api/v2/chat/memory/usage/{chatId} | Get Memory Usage
*ChatApi* | [**listChats**](docs/ChatApi.md#listchats) | **GET** /api/v2/chat | List Chats
*ChatApi* | [**listDebugMessages**](docs/ChatApi.md#listdebugmessages) | **GET** /api/v2/chat/messages/debug/{chatId}/{limit} | List Chat Debug Messages
*ChatApi* | [**listDebugMessages1**](docs/ChatApi.md#listdebugmessages1) | **GET** /api/v2/chat/messages/debug/{chatId}/{limit}/{offset} | List Chat Debug Messages
*ChatApi* | [**listDebugMessages2**](docs/ChatApi.md#listdebugmessages2) | **GET** /api/v2/chat/messages/debug/{chatId} | List Chat Debug Messages
*ChatApi* | [**listMessages**](docs/ChatApi.md#listmessages) | **GET** /api/v2/chat/messages/{chatId}/{limit} | List Chat Messages
*ChatApi* | [**listMessages1**](docs/ChatApi.md#listmessages1) | **GET** /api/v2/chat/messages/{chatId}/{limit}/{offset} | List Chat Messages
*ChatApi* | [**listMessages2**](docs/ChatApi.md#listmessages2) | **GET** /api/v2/chat/messages/{chatId} | List Chat Messages
*ChatApi* | [**rollbackMessages**](docs/ChatApi.md#rollbackmessages) | **POST** /api/v2/chat/messages/rollback/{chatId}/{count} | Rollback Chat Messages
*ChatApi* | [**sendAssistant**](docs/ChatApi.md#sendassistant) | **GET** /api/v2/chat/send/assistant/{chatId}/{assistantUid} | Send Assistant for Chat Message
*ChatApi* | [**sendMessage**](docs/ChatApi.md#sendmessage) | **POST** /api/v2/chat/send/{chatId} | Send Chat Message
*ChatApi* | [**startChat**](docs/ChatApi.md#startchat) | **POST** /api/v2/chat | Start Chat Session
*ChatApi* | [**streamSendAssistant**](docs/ChatApi.md#streamsendassistant) | **GET** /api/v2/chat/send/stream/assistant/{chatId}/{assistantUid} | Send Assistant for Chat Message by Streaming Back
*ChatApi* | [**streamSendMessage**](docs/ChatApi.md#streamsendmessage) | **POST** /api/v2/chat/send/stream/{chatId} | Send Chat Message by Streaming Back
*ChatApi* | [**updateChat**](docs/ChatApi.md#updatechat) | **PUT** /api/v2/chat/{chatId} | Update Chat Session
*EncryptionManagerForAdminApi* | [**encryptText**](docs/EncryptionManagerForAdminApi.md#encrypttext) | **GET** /api/v2/admin/encryption/encrypt/{text} | Encrypt Text
*InteractiveStatisticsApi* | [**addStatistic**](docs/InteractiveStatisticsApi.md#addstatistic) | **POST** /api/v2/stats/{infoType}/{infoId}/{statsType}/{delta} | Add Statistics
*InteractiveStatisticsApi* | [**getScore**](docs/InteractiveStatisticsApi.md#getscore) | **GET** /api/v2/public/score/{infoType}/{infoId} | Get Score for Resource
*InteractiveStatisticsApi* | [**getStatistic**](docs/InteractiveStatisticsApi.md#getstatistic) | **GET** /api/v2/public/stats/{infoType}/{infoId}/{statsType} | Get Statistics
*InteractiveStatisticsApi* | [**getStatistics**](docs/InteractiveStatisticsApi.md#getstatistics) | **GET** /api/v2/public/stats/{infoType}/{infoId} | Get All Statistics
*InteractiveStatisticsApi* | [**increaseStatistic**](docs/InteractiveStatisticsApi.md#increasestatistic) | **POST** /api/v2/stats/{infoType}/{infoId}/{statsType} | Increase Statistics
*InteractiveStatisticsApi* | [**listAgentsByStatistic**](docs/InteractiveStatisticsApi.md#listagentsbystatistic) | **GET** /api/v2/public/stats/agents/by/{statsType}/{pageSize} | List Agents by Statistics
*InteractiveStatisticsApi* | [**listAgentsByStatistic1**](docs/InteractiveStatisticsApi.md#listagentsbystatistic1) | **GET** /api/v2/public/stats/agents/by/{statsType}/{pageSize}/{pageNum} | List Agents by Statistics
*InteractiveStatisticsApi* | [**listAgentsByStatistic2**](docs/InteractiveStatisticsApi.md#listagentsbystatistic2) | **GET** /api/v2/public/stats/agents/by/{statsType} | List Agents by Statistics
*InteractiveStatisticsApi* | [**listCharactersByStatistic**](docs/InteractiveStatisticsApi.md#listcharactersbystatistic) | **GET** /api/v2/public/stats/characters/by/{statsType} | List Characters by Statistics
*InteractiveStatisticsApi* | [**listCharactersByStatistic1**](docs/InteractiveStatisticsApi.md#listcharactersbystatistic1) | **GET** /api/v2/public/stats/characters/by/{statsType}/{pageSize} | List Characters by Statistics
*InteractiveStatisticsApi* | [**listCharactersByStatistic2**](docs/InteractiveStatisticsApi.md#listcharactersbystatistic2) | **GET** /api/v2/public/stats/characters/by/{statsType}/{pageSize}/{pageNum} | List Characters by Statistics
*InteractiveStatisticsApi* | [**listHotTags**](docs/InteractiveStatisticsApi.md#listhottags) | **GET** /api/v2/public/tags/hot/{infoType}/{pageSize} | Hot Tags
*InteractiveStatisticsApi* | [**listPluginsByStatistic**](docs/InteractiveStatisticsApi.md#listpluginsbystatistic) | **GET** /api/v2/public/stats/plugins/by/{statsType}/{pageSize}/{pageNum} | List Plugins by Statistics
*InteractiveStatisticsApi* | [**listPluginsByStatistic1**](docs/InteractiveStatisticsApi.md#listpluginsbystatistic1) | **GET** /api/v2/public/stats/plugins/by/{statsType} | List Plugins by Statistics
*InteractiveStatisticsApi* | [**listPluginsByStatistic2**](docs/InteractiveStatisticsApi.md#listpluginsbystatistic2) | **GET** /api/v2/public/stats/plugins/by/{statsType}/{pageSize} | List Plugins by Statistics
*InteractiveStatisticsApi* | [**listPromptsByStatistic**](docs/InteractiveStatisticsApi.md#listpromptsbystatistic) | **GET** /api/v2/public/stats/prompts/by/{statsType} | List Prompts by Statistics
*InteractiveStatisticsApi* | [**listPromptsByStatistic1**](docs/InteractiveStatisticsApi.md#listpromptsbystatistic1) | **GET** /api/v2/public/stats/prompts/by/{statsType}/{pageSize} | List Prompts by Statistics
*InteractiveStatisticsApi* | [**listPromptsByStatistic2**](docs/InteractiveStatisticsApi.md#listpromptsbystatistic2) | **GET** /api/v2/public/stats/prompts/by/{statsType}/{pageSize}/{pageNum} | List Prompts by Statistics
*OrganizationApi* | [**getOwners**](docs/OrganizationApi.md#getowners) | **GET** /api/v2/org/owners | Get My Superior Relationship
*OrganizationApi* | [**getOwnersDot**](docs/OrganizationApi.md#getownersdot) | **GET** /api/v2/org/owners/dot | Get DOT of Superior Relationship
*OrganizationApi* | [**getSubordinateOwners**](docs/OrganizationApi.md#getsubordinateowners) | **GET** /api/v2/org/manage/{username}/owners | Get Superior Relationship
*OrganizationApi* | [**getSubordinateSubordinates**](docs/OrganizationApi.md#getsubordinatesubordinates) | **GET** /api/v2/org/manage/{username}/subordinates | Get Subordinate Relationship
*OrganizationApi* | [**getSubordinates**](docs/OrganizationApi.md#getsubordinates) | **GET** /api/v2/org/subordinates | Get My Subordinate Relationship
*OrganizationApi* | [**getSubordinatesDot**](docs/OrganizationApi.md#getsubordinatesdot) | **GET** /api/v2/org/subordinates/dot | Get DOT of Subordinate Relationship
*OrganizationApi* | [**listSubordinateAuthorities**](docs/OrganizationApi.md#listsubordinateauthorities) | **GET** /api/v2/org/authority/{username} | List Subordinate Permissions
*OrganizationApi* | [**removeSubordinateSubordinatesTree**](docs/OrganizationApi.md#removesubordinatesubordinatestree) | **DELETE** /api/v2/org/manage/{username}/subordinates | Clear Subordinate Relationship
*OrganizationApi* | [**updateSubordinateAuthorities**](docs/OrganizationApi.md#updatesubordinateauthorities) | **PUT** /api/v2/org/authority/{username} | Update Subordinate Permissions
*OrganizationApi* | [**updateSubordinateOwners**](docs/OrganizationApi.md#updatesubordinateowners) | **PUT** /api/v2/org/manage/{username}/owners | Update Superior Relationship
*OrganizationApi* | [**updateSubordinateSubordinates**](docs/OrganizationApi.md#updatesubordinatesubordinates) | **PUT** /api/v2/org/manage/{username}/subordinates | Update Subordinate Relationship
*PluginApi* | [**batchSearchPluginDetails**](docs/PluginApi.md#batchsearchplugindetails) | **POST** /api/v2/plugin/batch/details/search | Batch Search Plugin Details
*PluginApi* | [**batchSearchPluginSummary**](docs/PluginApi.md#batchsearchpluginsummary) | **POST** /api/v2/plugin/batch/search | Batch Search Plugin Summaries
*PluginApi* | [**countPlugins**](docs/PluginApi.md#countplugins) | **POST** /api/v2/plugin/count | Calculate Number of Plugins
*PluginApi* | [**createPlugin**](docs/PluginApi.md#createplugin) | **POST** /api/v2/plugin | Create Plugin
*PluginApi* | [**createPlugins**](docs/PluginApi.md#createplugins) | **POST** /api/v2/plugin/batch | Batch Create Plugins
*PluginApi* | [**deletePlugin**](docs/PluginApi.md#deleteplugin) | **DELETE** /api/v2/plugin/{pluginId} | Delete Plugin
*PluginApi* | [**deletePlugins**](docs/PluginApi.md#deleteplugins) | **DELETE** /api/v2/plugin/batch | Batch Delete Plugins
*PluginApi* | [**getPluginDetails**](docs/PluginApi.md#getplugindetails) | **GET** /api/v2/plugin/details/{pluginId} | Get Plugin Details
*PluginApi* | [**getPluginSummary**](docs/PluginApi.md#getpluginsummary) | **GET** /api/v2/plugin/summary/{pluginId} | Get Plugin Summary
*PluginApi* | [**refreshPluginInfo**](docs/PluginApi.md#refreshplugininfo) | **PUT** /api/v2/plugin/refresh/{pluginId} | Refresh Plugin Information
*PluginApi* | [**searchPluginDetails**](docs/PluginApi.md#searchplugindetails) | **POST** /api/v2/plugin/details/search | Search Plugin Details
*PluginApi* | [**searchPluginSummary**](docs/PluginApi.md#searchpluginsummary) | **POST** /api/v2/plugin/search | Search Plugin Summary
*PluginApi* | [**updatePlugin**](docs/PluginApi.md#updateplugin) | **PUT** /api/v2/plugin/{pluginId} | Update Plugin
*PromptApi* | [**applyPromptRef**](docs/PromptApi.md#applypromptref) | **POST** /api/v2/prompt/apply/ref | Apply Parameters to Prompt Record
*PromptApi* | [**applyPromptTemplate**](docs/PromptApi.md#applyprompttemplate) | **POST** /api/v2/prompt/apply/template | Apply Parameters to Prompt Template
*PromptApi* | [**batchSearchPromptDetails**](docs/PromptApi.md#batchsearchpromptdetails) | **POST** /api/v2/prompt/batch/details/search | Batch Search Prompt Details
*PromptApi* | [**batchSearchPromptSummary**](docs/PromptApi.md#batchsearchpromptsummary) | **POST** /api/v2/prompt/batch/search | Batch Search Prompt Summaries
*PromptApi* | [**clonePrompt**](docs/PromptApi.md#cloneprompt) | **POST** /api/v2/prompt/clone/{promptId} | Clone Prompt
*PromptApi* | [**clonePrompts**](docs/PromptApi.md#cloneprompts) | **POST** /api/v2/prompt/batch/clone | Batch Clone Prompts
*PromptApi* | [**countPrompts**](docs/PromptApi.md#countprompts) | **POST** /api/v2/prompt/count | Calculate Number of Prompts
*PromptApi* | [**countPublicPrompts**](docs/PromptApi.md#countpublicprompts) | **POST** /api/v2/public/prompt/count | Calculate Number of Public Prompts
*PromptApi* | [**createPrompt**](docs/PromptApi.md#createprompt) | **POST** /api/v2/prompt | Create Prompt
*PromptApi* | [**createPrompts**](docs/PromptApi.md#createprompts) | **POST** /api/v2/prompt/batch | Batch Create Prompts
*PromptApi* | [**deletePrompt**](docs/PromptApi.md#deleteprompt) | **DELETE** /api/v2/prompt/{promptId} | Delete Prompt
*PromptApi* | [**deletePromptByName**](docs/PromptApi.md#deletepromptbyname) | **DELETE** /api/v2/prompt/name/{name} | Delete Prompt by Name
*PromptApi* | [**deletePrompts**](docs/PromptApi.md#deleteprompts) | **DELETE** /api/v2/prompt/batch | Batch Delete Prompts
*PromptApi* | [**existsPromptName**](docs/PromptApi.md#existspromptname) | **GET** /api/v2/prompt/exists/name/{name} | Check If Prompt Name Exists
*PromptApi* | [**getPromptDetails**](docs/PromptApi.md#getpromptdetails) | **GET** /api/v2/prompt/details/{promptId} | Get Prompt Details
*PromptApi* | [**getPromptSummary**](docs/PromptApi.md#getpromptsummary) | **GET** /api/v2/prompt/summary/{promptId} | Get Prompt Summary
*PromptApi* | [**listPromptVersionsByName**](docs/PromptApi.md#listpromptversionsbyname) | **POST** /api/v2/prompt/versions/{name} | List Versions by Prompt Name
*PromptApi* | [**newPromptName**](docs/PromptApi.md#newpromptname) | **GET** /api/v2/prompt/create/name/{desired} | Create New Prompt Name
*PromptApi* | [**publishPrompt**](docs/PromptApi.md#publishprompt) | **POST** /api/v2/prompt/publish/{promptId}/{visibility} | Publish Prompt
*PromptApi* | [**searchPromptDetails**](docs/PromptApi.md#searchpromptdetails) | **POST** /api/v2/prompt/details/search | Search Prompt Details
*PromptApi* | [**searchPromptSummary**](docs/PromptApi.md#searchpromptsummary) | **POST** /api/v2/prompt/search | Search Prompt Summary
*PromptApi* | [**searchPublicPromptSummary**](docs/PromptApi.md#searchpublicpromptsummary) | **POST** /api/v2/public/prompt/search | Search Public Prompt Summary
*PromptApi* | [**sendPrompt**](docs/PromptApi.md#sendprompt) | **POST** /api/v2/prompt/send | Send Prompt
*PromptApi* | [**streamSendPrompt**](docs/PromptApi.md#streamsendprompt) | **POST** /api/v2/prompt/send/stream | Send Prompt by Streaming Back
*PromptApi* | [**updatePrompt**](docs/PromptApi.md#updateprompt) | **PUT** /api/v2/prompt/{promptId} | Update Prompt
*PromptTaskApi* | [**createPromptTask**](docs/PromptTaskApi.md#createprompttask) | **POST** /api/v2/prompt/task | Create Prompt Task
*PromptTaskApi* | [**deletePromptTask**](docs/PromptTaskApi.md#deleteprompttask) | **DELETE** /api/v2/prompt/task/{promptTaskId} | Delete Prompt Task
*PromptTaskApi* | [**getPromptTask**](docs/PromptTaskApi.md#getprompttask) | **GET** /api/v2/prompt/task/{promptTaskId} | Get Prompt Task
*PromptTaskApi* | [**updatePromptTask**](docs/PromptTaskApi.md#updateprompttask) | **PUT** /api/v2/prompt/task/{promptTaskId} | Update Prompt Task
*RagApi* | [**cancelRagTask**](docs/RagApi.md#cancelragtask) | **POST** /api/v2/rag/task/cancel/{taskId} | Cancel RAG Task
*RagApi* | [**createRagTask**](docs/RagApi.md#createragtask) | **POST** /api/v2/rag/task/{characterUid} | Create RAG Task
*RagApi* | [**deleteRagTask**](docs/RagApi.md#deleteragtask) | **DELETE** /api/v2/rag/task/{taskId} | Delete RAG Task
*RagApi* | [**getRagTask**](docs/RagApi.md#getragtask) | **GET** /api/v2/rag/task/{taskId} | Get RAG Task
*RagApi* | [**getRagTaskStatus**](docs/RagApi.md#getragtaskstatus) | **GET** /api/v2/rag/task/status/{taskId} | Get RAG Task Status
*RagApi* | [**listRagTasks**](docs/RagApi.md#listragtasks) | **GET** /api/v2/rag/tasks/{characterUid} | List RAG Tasks
*RagApi* | [**startRagTask**](docs/RagApi.md#startragtask) | **POST** /api/v2/rag/task/start/{taskId} | Start RAG Task
*RagApi* | [**updateRagTask**](docs/RagApi.md#updateragtask) | **PUT** /api/v2/rag/task/{taskId} | Update RAG Task
*TTSServiceApi* | [**listTtsBuiltinSpeakers**](docs/TTSServiceApi.md#listttsbuiltinspeakers) | **GET** /api/v2/public/tts/builtin/speakers | List Builtin Speakers
*TTSServiceApi* | [**playSample**](docs/TTSServiceApi.md#playsample) | **GET** /api/v2/public/tts/play/sample/{speakerType}/{speaker} | Play Sample Audio
*TTSServiceApi* | [**speakMessage**](docs/TTSServiceApi.md#speakmessage) | **GET** /api/v2/tts/speak/{messageId} | Speak Message
*TagManagerForBizAdminApi* | [**createTag**](docs/TagManagerForBizAdminApi.md#createtag) | **POST** /api/v2/biz/admin/tag/{referType}/{referId}/{tag} | Create Tag
*TagManagerForBizAdminApi* | [**deleteTag**](docs/TagManagerForBizAdminApi.md#deletetag) | **DELETE** /api/v2/biz/admin/tag/{referType}/{referId}/{tag} | Delete Tag


## Documentation For Models

 - [AgentCreateDTO](docs/AgentCreateDTO.md)
 - [AgentDetailsDTO](docs/AgentDetailsDTO.md)
 - [AgentItemForNameDTO](docs/AgentItemForNameDTO.md)
 - [AgentQueryDTO](docs/AgentQueryDTO.md)
 - [AgentQueryWhere](docs/AgentQueryWhere.md)
 - [AgentSummaryDTO](docs/AgentSummaryDTO.md)
 - [AgentSummaryStatsDTO](docs/AgentSummaryStatsDTO.md)
 - [AgentUpdateDTO](docs/AgentUpdateDTO.md)
 - [AiApiKeyCreateDTO](docs/AiApiKeyCreateDTO.md)
 - [AiApiKeyInfoDTO](docs/AiApiKeyInfoDTO.md)
 - [AiModelInfoDTO](docs/AiModelInfoDTO.md)
 - [ApiTokenInfoDTO](docs/ApiTokenInfoDTO.md)
 - [AppMetaDTO](docs/AppMetaDTO.md)
 - [CharacterBackendDTO](docs/CharacterBackendDTO.md)
 - [CharacterBackendDetailsDTO](docs/CharacterBackendDetailsDTO.md)
 - [CharacterCreateDTO](docs/CharacterCreateDTO.md)
 - [CharacterDetailsDTO](docs/CharacterDetailsDTO.md)
 - [CharacterItemForNameDTO](docs/CharacterItemForNameDTO.md)
 - [CharacterQueryDTO](docs/CharacterQueryDTO.md)
 - [CharacterQueryWhere](docs/CharacterQueryWhere.md)
 - [CharacterSummaryDTO](docs/CharacterSummaryDTO.md)
 - [CharacterSummaryStatsDTO](docs/CharacterSummaryStatsDTO.md)
 - [CharacterUpdateDTO](docs/CharacterUpdateDTO.md)
 - [ChatContentDTO](docs/ChatContentDTO.md)
 - [ChatContextDTO](docs/ChatContextDTO.md)
 - [ChatCreateDTO](docs/ChatCreateDTO.md)
 - [ChatMessageDTO](docs/ChatMessageDTO.md)
 - [ChatMessageRecordDTO](docs/ChatMessageRecordDTO.md)
 - [ChatPromptContentDTO](docs/ChatPromptContentDTO.md)
 - [ChatSessionDTO](docs/ChatSessionDTO.md)
 - [ChatToolCallDTO](docs/ChatToolCallDTO.md)
 - [ChatUpdateDTO](docs/ChatUpdateDTO.md)
 - [HotTagDTO](docs/HotTagDTO.md)
 - [InteractiveStatsDTO](docs/InteractiveStatsDTO.md)
 - [LlmResultDTO](docs/LlmResultDTO.md)
 - [MemoryUsageDTO](docs/MemoryUsageDTO.md)
 - [PluginCreateDTO](docs/PluginCreateDTO.md)
 - [PluginDetailsDTO](docs/PluginDetailsDTO.md)
 - [PluginQueryDTO](docs/PluginQueryDTO.md)
 - [PluginQueryWhere](docs/PluginQueryWhere.md)
 - [PluginSummaryDTO](docs/PluginSummaryDTO.md)
 - [PluginSummaryStatsDTO](docs/PluginSummaryStatsDTO.md)
 - [PluginUpdateDTO](docs/PluginUpdateDTO.md)
 - [PromptAiParamDTO](docs/PromptAiParamDTO.md)
 - [PromptCreateDTO](docs/PromptCreateDTO.md)
 - [PromptDetailsDTO](docs/PromptDetailsDTO.md)
 - [PromptItemForNameDTO](docs/PromptItemForNameDTO.md)
 - [PromptQueryDTO](docs/PromptQueryDTO.md)
 - [PromptQueryWhere](docs/PromptQueryWhere.md)
 - [PromptRefDTO](docs/PromptRefDTO.md)
 - [PromptSummaryDTO](docs/PromptSummaryDTO.md)
 - [PromptSummaryStatsDTO](docs/PromptSummaryStatsDTO.md)
 - [PromptTaskDTO](docs/PromptTaskDTO.md)
 - [PromptTaskDetailsDTO](docs/PromptTaskDetailsDTO.md)
 - [PromptTemplateDTO](docs/PromptTemplateDTO.md)
 - [PromptUpdateDTO](docs/PromptUpdateDTO.md)
 - [RagTaskDTO](docs/RagTaskDTO.md)
 - [RagTaskDetailsDTO](docs/RagTaskDetailsDTO.md)
 - [SseEmitter](docs/SseEmitter.md)
 - [TokenUsageDTO](docs/TokenUsageDTO.md)
 - [UserBasicInfoDTO](docs/UserBasicInfoDTO.md)
 - [UserDetailsDTO](docs/UserDetailsDTO.md)
 - [UserFullDetailsDTO](docs/UserFullDetailsDTO.md)


## Documentation For Authorization


## bearerAuth


- **Type**: HTTP Bearer Token authentication

