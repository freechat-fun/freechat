/*
 * FreeChat OpenAPI Definition
 * # FreeChat: Create Some Friends for Yourself with AI  English | [中文版](https://github.com/freechat-fun/freechat/blob/main/README.zh-CN.md)  ## Introduction Welcome! FreeChat aims to build a cloud-native, robust, and quickly commercializable enterprise-level AI virtual character platform.  ## Features - Primarily uses Java and emphasizes **security, robustness, scalability, traceability, and maintainability**. - Boasts **account systems and permission management**, supporting OAuth2 authentication. Introduces the \"organization\" concept and related permission constraint functions. - Extensively employs distributed technologies and caching to support **high concurrency** access. - Provides flexible character customization options, supports direct intervention in prompts, and supports **configuring multiple backends for each character**. - **Offers a comprehensive range of Open APIs**, with more than 180 interfaces and provides java/python/typescript SDKs. These interfaces enable easy construction of systems for end-users. - Supports setting **RAG** (Retrieval Augmented Generation) for characters. - Supports **long-term memory** for characters. - Supports setting **quota limits** for characters.  ## System Snapshots  ## Character Design ```mermaid flowchart TD     A(Character) --> B(Profile)     A --> C(Knowledge/RAG)     A --> D(Album)     A --> E(Backend-1)     A --> F(Backend-n...)     E --> G(Message Window)     E --> H(Long Term Memory Settings)     E --> I(Quota Limit)     E --> J(Chat Prompt Task)     E --> K(Greeting Prompt Task)     E --> L(Moderation Settings)     J --> M(Model & Parameters)     J --> N(API Keys)     J --> O(Prompt Refence)     J --> P(Tool Specifications)     O --> Q(Template)     O --> R(Variables)     O --> S(Version)     O --> T(...)     style K stroke-dasharray: 5, 5     style L stroke-dasharray: 5, 5     style P stroke-dasharray: 5, 5 ```  After setting up a unified persona and knowledge for a character, different backends can be configured. For example, different model may be adopted for different users based on cost considerations.  ## How to Play ### Online Website You can visit [freechat.fun](https://freechat.fun) to experience FreeChat. Share your designed AI character!  ### Running in a Kubernetes Cluster FreeChat is dedicated to the principles of cloud-native design. If you have a Kubernetes cluster, you can deploy FreeChat to your environment by following these steps:  1. Put the Kubernetes configuration file in the `configs/helm/` directory, named `kube-private.conf`. 2. Place the Helm configuration file in the same directory, named `values-private.yaml`. Make sure to reference the default `values.yaml` and customize the variables as needed. 3. Switch to the `scripts/` directory. 4. If needed, run `install-in.sh` to deploy `ingress-nginx` on the Kubernetes cluster. 5. If needed, run `install-cm.sh` to deploy `cert-manager` on the Kubernetes cluster, which automatically issues certificates for domains specified in `ingress.hosts`. 6. Run `install-pvc.sh` to install PersistentVolumeClaim resources.      > By default, FreeChat operates files by accessing the \"local file system.\" You may want to use high-availability distributed storage in the cloud. As a cloud-native-designed system, we recommend interfacing through Kubernetes CSI to avoid individually adapting storage products for each cloud platform. Most cloud service providers offer cloud storage drivers for Kubernetes, with a series of predefined StorageClass resources. Please choose the appropriate configuration according to your actual needs and set it in Helm's `global.storageClass` option.     >      > *In the future, FreeChat may be refactored to use MinIO's APIs directly, as it is now installed in the Kubernetes cluster as a dependency (serving Milvus).*  7. Run `install.sh` script to install FreeChat and its dependencies. 8. FreeChat aims to provide Open API services. If you like the interactive experience of [freechat.fun](https://freechat.fun), run `install-web.sh` to deploy the front-end application. 9. Run `restart.sh` to restart the service. 10. If you modified any Helm configuration files, use `upgrade.sh` to update the corresponding Kubernetes resources. 11. To remove specific resources, run the `uninstall*.sh` script corresponding to the resource you want to uninstall.  As a cloud-native application, the services FreeChat relies on are obtained and deployed to your cluster through the helm repository.  If you prefer cloud services with SLA (Service Level Agreement) guarantees, simply make the relevant settings in `configs/helm/values-private.yaml`: ```yaml bitnami:   mysql:     enabled: false   redis:     enabled: false   milvus:     enabled: false  mysql:   url: <your mysql url>   auth:     rootPassword: <your mysql root password>     username: <your mysql username>     password: <your mysql password for the username>  redis:   url: <your redis url>   auth:     password: <your redis password>   milvus:   url: <your milvus url>   milvus:     auth:       token: <your milvus api-key> ```  With this, FreeChat will not automatically install these services, but rather use the configuration information to connect directly.  ### Running Locally You can also run FreeChat locally. Currently supported on MacOS and Linux (although only tested on MacOS). You need to install the Docker toolset and have a network that can access [Docker Hub](https://hub.docker.com/).  Once ready, enter the `scripts/` directory and run `local-run.sh`, which will download and run the necessary docker containers. After a successful startup, you can access `http://localhost` via a browser to see the locally running freechat.fun. Use `local-run.sh --help` to view the supported options of the script. Good luck!  ### Running in an IDE To run FreeChat in an IDE, you need to start all dependent services first but do not need to run the container for the FreeChat application itself. You can execute the `scripts/local-deps.sh` script to start services like `MySQL`, `Redis`, `Milvus`, etc., locally. Once done, open and debug `freechat-start/src/main/java/fun/freechat/Application.java`。Make sure you have set the following startup parameters: ```shell -Dspring.config.location=classpath:/application.yml,classpath:/application-local.yml \\ -DAPP_HOME=local-data/freechat \\ -Dspring.profiles.active=local ```  ### 使用 SDK #### Java - **Dependency** ```xml <dependency>   <groupId>fun.freechat</groupId>   <artifactId>freechat-sdk</artifactId>   <version>${freechat-sdk.version}</version> </dependency> ```  - **Example** ```java import fun.freechat.client.ApiClient; import fun.freechat.client.ApiException; import fun.freechat.client.Configuration; import fun.freechat.client.api.AccountApi; import fun.freechat.client.auth.ApiKeyAuth; import fun.freechat.client.model.UserDetailsDTO;  public class AccountClientExample {     public static void main(String[] args) {         ApiClient defaultClient = Configuration.getDefaultApiClient();         defaultClient.setBasePath(\"https://freechat.fun\");                  // Configure HTTP bearer authorization: bearerAuth         HttpBearerAuth bearerAuth = (HttpBearerAuth) defaultClient.getAuthentication(\"bearerAuth\");         bearerAuth.setBearerToken(\"FREECHAT_TOKEN\");          AccountApi apiInstance = new AccountApi(defaultClient);         try {             UserDetailsDTO result = apiInstance.getUserDetails();             System.out.println(result);         } catch (ApiException e) {             e.printStackTrace();         }     } } ```  #### Python - **Installation** ```shell pip install freechat-sdk ```  - **Example** ```python import freechat_sdk from freechat_sdk.rest import ApiException from pprint import pprint  # Defining the host is optional and defaults to https://freechat.fun # See configuration.py for a list of all supported configuration parameters. configuration = freechat_sdk.Configuration(     host = \"https://freechat.fun\" )  # Configure Bearer authorization: bearerAuth configuration = freechat_sdk.Configuration(     access_token = os.environ[\"FREECHAT_TOKEN\"] )  # Enter a context with an instance of the API client with freechat_sdk.ApiClient(configuration) as api_client:     # Create an instance of the API class     api_instance = freechat_sdk.AccountApi(api_client)      try:         details = api_instance.get_user_details()         pprint(details)     except ApiException as e:         print(\"Exception when calling AccountClient->get_user_details: %s\\n\" % e) ```  #### TypeScript - **Installation** ```shell npm install freechat-sdk --save ```  - **Example**  Refer to [FreeChatApiContext.tsx](https://github.com/freechat-fun/freechat/blob/main/freechat-web/src/contexts/FreeChatApiProvider.tsx)  ## System Dependencies | | Projects | ---- | ---- | Application Framework | [Spring Boot](https://spring.io/projects/spring-boot/) | LLM Framework | [LangChain4j](https://docs.langchain4j.dev/) | Model Providers | [OpenAI](https://platform.openai.com/), [DashScope](https://dashscope.aliyun.com/) | Database Systems | [MySQL](https://www.mysql.com/), [Redis](https://redis.io/), [Milvus](https://milvus.io/) | OpenAPI Tools | [Springdoc-openapi](https://springdoc.org/), [OpenAPI Generator](https://github.com/OpenAPITools/openapi-generator), [OpenAPI Explorer](https://github.com/Authress-Engineering/openapi-explorer)  ## Collaboration ### Application Integration The FreeChat system is entirely oriented towards Open APIs. The site [freechat.fun](https://freechat.fun) is developed using its TypeScript SDK and hardly depends on private interfaces. You can use these online interfaces to develop your own applications or sites, making them fit your preferences. Currently, FreeChat is completely free with no paid plans (after all, users use their own API Key to call LLM services).  ### Model Integration FreeChat aims to explore AI virtual character technology with anthropomorphic characteristics. So far, it supports model services from OpenAI and DashScope ([HuggingFace](https://huggingface.co/) is also expected to be supported soon). However, we are more interested in supporting models that are under research and can endow AI with more personality traits. If you are researching this area and hope FreeChat supports your model, please contact us. We look forward to AI technology helping people create their own \"soul mates\" in the future.
 *
 * The version of the OpenAPI document: 1.0.2
 * 
 *
 * NOTE: This class is auto generated by OpenAPI Generator (https://openapi-generator.tech).
 * https://openapi-generator.tech
 * Do not edit the class manually.
 */


package fun.freechat.client.model;

import java.util.Objects;
import com.google.gson.TypeAdapter;
import com.google.gson.annotations.JsonAdapter;
import com.google.gson.annotations.SerializedName;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.TypeAdapterFactory;
import com.google.gson.reflect.TypeToken;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import java.io.IOException;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import fun.freechat.client.JSON;

/**
 * Request data for creating new character information
 */
@javax.annotation.Generated(value = "org.openapitools.codegen.languages.JavaClientCodegen", comments = "Generator version: 7.5.0")
public class CharacterCreateDTO {
  public static final String SERIALIZED_NAME_PARENT_UID = "parentUid";
  @SerializedName(SERIALIZED_NAME_PARENT_UID)
  private String parentUid;

  public static final String SERIALIZED_NAME_VISIBILITY = "visibility";
  @SerializedName(SERIALIZED_NAME_VISIBILITY)
  private String visibility;

  public static final String SERIALIZED_NAME_NAME = "name";
  @SerializedName(SERIALIZED_NAME_NAME)
  private String name;

  public static final String SERIALIZED_NAME_DESCRIPTION = "description";
  @SerializedName(SERIALIZED_NAME_DESCRIPTION)
  private String description;

  public static final String SERIALIZED_NAME_NICKNAME = "nickname";
  @SerializedName(SERIALIZED_NAME_NICKNAME)
  private String nickname;

  public static final String SERIALIZED_NAME_AVATAR = "avatar";
  @SerializedName(SERIALIZED_NAME_AVATAR)
  private String avatar;

  public static final String SERIALIZED_NAME_PICTURE = "picture";
  @SerializedName(SERIALIZED_NAME_PICTURE)
  private String picture;

  public static final String SERIALIZED_NAME_GENDER = "gender";
  @SerializedName(SERIALIZED_NAME_GENDER)
  private String gender;

  public static final String SERIALIZED_NAME_PROFILE = "profile";
  @SerializedName(SERIALIZED_NAME_PROFILE)
  private String profile;

  public static final String SERIALIZED_NAME_GREETING = "greeting";
  @SerializedName(SERIALIZED_NAME_GREETING)
  private String greeting;

  public static final String SERIALIZED_NAME_CHAT_STYLE = "chatStyle";
  @SerializedName(SERIALIZED_NAME_CHAT_STYLE)
  private String chatStyle;

  public static final String SERIALIZED_NAME_CHAT_EXAMPLE = "chatExample";
  @SerializedName(SERIALIZED_NAME_CHAT_EXAMPLE)
  private String chatExample;

  public static final String SERIALIZED_NAME_DEFAULT_SCENE = "defaultScene";
  @SerializedName(SERIALIZED_NAME_DEFAULT_SCENE)
  private String defaultScene;

  public static final String SERIALIZED_NAME_LANG = "lang";
  @SerializedName(SERIALIZED_NAME_LANG)
  private String lang;

  public static final String SERIALIZED_NAME_EXT = "ext";
  @SerializedName(SERIALIZED_NAME_EXT)
  private String ext;

  public static final String SERIALIZED_NAME_DRAFT = "draft";
  @SerializedName(SERIALIZED_NAME_DRAFT)
  private String draft;

  public static final String SERIALIZED_NAME_TAGS = "tags";
  @SerializedName(SERIALIZED_NAME_TAGS)
  private List<String> tags = new ArrayList<>();

  public CharacterCreateDTO() {
  }

  public CharacterCreateDTO parentUid(String parentUid) {
    this.parentUid = parentUid;
    return this;
  }

   /**
   * Referenced character
   * @return parentUid
  **/
  @javax.annotation.Nullable
  public String getParentUid() {
    return parentUid;
  }

  public void setParentUid(String parentUid) {
    this.parentUid = parentUid;
  }


  public CharacterCreateDTO visibility(String visibility) {
    this.visibility = visibility;
    return this;
  }

   /**
   * Visibility: private (default), public, public_org, hidden
   * @return visibility
  **/
  @javax.annotation.Nullable
  public String getVisibility() {
    return visibility;
  }

  public void setVisibility(String visibility) {
    this.visibility = visibility;
  }


  public CharacterCreateDTO name(String name) {
    this.name = name;
    return this;
  }

   /**
   * Character name
   * @return name
  **/
  @javax.annotation.Nonnull
  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }


  public CharacterCreateDTO description(String description) {
    this.description = description;
    return this;
  }

   /**
   * Character description
   * @return description
  **/
  @javax.annotation.Nullable
  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }


  public CharacterCreateDTO nickname(String nickname) {
    this.nickname = nickname;
    return this;
  }

   /**
   * Character nickname
   * @return nickname
  **/
  @javax.annotation.Nullable
  public String getNickname() {
    return nickname;
  }

  public void setNickname(String nickname) {
    this.nickname = nickname;
  }


  public CharacterCreateDTO avatar(String avatar) {
    this.avatar = avatar;
    return this;
  }

   /**
   * Character avatar url
   * @return avatar
  **/
  @javax.annotation.Nullable
  public String getAvatar() {
    return avatar;
  }

  public void setAvatar(String avatar) {
    this.avatar = avatar;
  }


  public CharacterCreateDTO picture(String picture) {
    this.picture = picture;
    return this;
  }

   /**
   * Character picture url
   * @return picture
  **/
  @javax.annotation.Nullable
  public String getPicture() {
    return picture;
  }

  public void setPicture(String picture) {
    this.picture = picture;
  }


  public CharacterCreateDTO gender(String gender) {
    this.gender = gender;
    return this;
  }

   /**
   * Character gender: male | female | other
   * @return gender
  **/
  @javax.annotation.Nullable
  public String getGender() {
    return gender;
  }

  public void setGender(String gender) {
    this.gender = gender;
  }


  public CharacterCreateDTO profile(String profile) {
    this.profile = profile;
    return this;
  }

   /**
   * Character profile
   * @return profile
  **/
  @javax.annotation.Nullable
  public String getProfile() {
    return profile;
  }

  public void setProfile(String profile) {
    this.profile = profile;
  }


  public CharacterCreateDTO greeting(String greeting) {
    this.greeting = greeting;
    return this;
  }

   /**
   * Character greeting
   * @return greeting
  **/
  @javax.annotation.Nullable
  public String getGreeting() {
    return greeting;
  }

  public void setGreeting(String greeting) {
    this.greeting = greeting;
  }


  public CharacterCreateDTO chatStyle(String chatStyle) {
    this.chatStyle = chatStyle;
    return this;
  }

   /**
   * Character chat-style
   * @return chatStyle
  **/
  @javax.annotation.Nullable
  public String getChatStyle() {
    return chatStyle;
  }

  public void setChatStyle(String chatStyle) {
    this.chatStyle = chatStyle;
  }


  public CharacterCreateDTO chatExample(String chatExample) {
    this.chatExample = chatExample;
    return this;
  }

   /**
   * Character chat-example
   * @return chatExample
  **/
  @javax.annotation.Nullable
  public String getChatExample() {
    return chatExample;
  }

  public void setChatExample(String chatExample) {
    this.chatExample = chatExample;
  }


  public CharacterCreateDTO defaultScene(String defaultScene) {
    this.defaultScene = defaultScene;
    return this;
  }

   /**
   * Default scene, which will be set as the default conversation background information when creating a new chat
   * @return defaultScene
  **/
  @javax.annotation.Nullable
  public String getDefaultScene() {
    return defaultScene;
  }

  public void setDefaultScene(String defaultScene) {
    this.defaultScene = defaultScene;
  }


  public CharacterCreateDTO lang(String lang) {
    this.lang = lang;
    return this;
  }

   /**
   * Character language: en (default) | zh | ...
   * @return lang
  **/
  @javax.annotation.Nullable
  public String getLang() {
    return lang;
  }

  public void setLang(String lang) {
    this.lang = lang;
  }


  public CharacterCreateDTO ext(String ext) {
    this.ext = ext;
    return this;
  }

   /**
   * Additional information, JSON format
   * @return ext
  **/
  @javax.annotation.Nullable
  public String getExt() {
    return ext;
  }

  public void setExt(String ext) {
    this.ext = ext;
  }


  public CharacterCreateDTO draft(String draft) {
    this.draft = draft;
    return this;
  }

   /**
   * Character draft information
   * @return draft
  **/
  @javax.annotation.Nullable
  public String getDraft() {
    return draft;
  }

  public void setDraft(String draft) {
    this.draft = draft;
  }


  public CharacterCreateDTO tags(List<String> tags) {
    this.tags = tags;
    return this;
  }

  public CharacterCreateDTO addTagsItem(String tagsItem) {
    if (this.tags == null) {
      this.tags = new ArrayList<>();
    }
    this.tags.add(tagsItem);
    return this;
  }

   /**
   * Tag set
   * @return tags
  **/
  @javax.annotation.Nullable
  public List<String> getTags() {
    return tags;
  }

  public void setTags(List<String> tags) {
    this.tags = tags;
  }

  /**
   * A container for additional, undeclared properties.
   * This is a holder for any undeclared properties as specified with
   * the 'additionalProperties' keyword in the OAS document.
   */
  private Map<String, Object> additionalProperties;

  /**
   * Set the additional (undeclared) property with the specified name and value.
   * If the property does not already exist, create it otherwise replace it.
   *
   * @param key name of the property
   * @param value value of the property
   * @return the CharacterCreateDTO instance itself
   */
  public CharacterCreateDTO putAdditionalProperty(String key, Object value) {
    if (this.additionalProperties == null) {
        this.additionalProperties = new HashMap<String, Object>();
    }
    this.additionalProperties.put(key, value);
    return this;
  }

  /**
   * Return the additional (undeclared) property.
   *
   * @return a map of objects
   */
  public Map<String, Object> getAdditionalProperties() {
    return additionalProperties;
  }

  /**
   * Return the additional (undeclared) property with the specified name.
   *
   * @param key name of the property
   * @return an object
   */
  public Object getAdditionalProperty(String key) {
    if (this.additionalProperties == null) {
        return null;
    }
    return this.additionalProperties.get(key);
  }


  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    CharacterCreateDTO characterCreateDTO = (CharacterCreateDTO) o;
    return Objects.equals(this.parentUid, characterCreateDTO.parentUid) &&
        Objects.equals(this.visibility, characterCreateDTO.visibility) &&
        Objects.equals(this.name, characterCreateDTO.name) &&
        Objects.equals(this.description, characterCreateDTO.description) &&
        Objects.equals(this.nickname, characterCreateDTO.nickname) &&
        Objects.equals(this.avatar, characterCreateDTO.avatar) &&
        Objects.equals(this.picture, characterCreateDTO.picture) &&
        Objects.equals(this.gender, characterCreateDTO.gender) &&
        Objects.equals(this.profile, characterCreateDTO.profile) &&
        Objects.equals(this.greeting, characterCreateDTO.greeting) &&
        Objects.equals(this.chatStyle, characterCreateDTO.chatStyle) &&
        Objects.equals(this.chatExample, characterCreateDTO.chatExample) &&
        Objects.equals(this.defaultScene, characterCreateDTO.defaultScene) &&
        Objects.equals(this.lang, characterCreateDTO.lang) &&
        Objects.equals(this.ext, characterCreateDTO.ext) &&
        Objects.equals(this.draft, characterCreateDTO.draft) &&
        Objects.equals(this.tags, characterCreateDTO.tags)&&
        Objects.equals(this.additionalProperties, characterCreateDTO.additionalProperties);
  }

  @Override
  public int hashCode() {
    return Objects.hash(parentUid, visibility, name, description, nickname, avatar, picture, gender, profile, greeting, chatStyle, chatExample, defaultScene, lang, ext, draft, tags, additionalProperties);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class CharacterCreateDTO {\n");
    sb.append("    parentUid: ").append(toIndentedString(parentUid)).append("\n");
    sb.append("    visibility: ").append(toIndentedString(visibility)).append("\n");
    sb.append("    name: ").append(toIndentedString(name)).append("\n");
    sb.append("    description: ").append(toIndentedString(description)).append("\n");
    sb.append("    nickname: ").append(toIndentedString(nickname)).append("\n");
    sb.append("    avatar: ").append(toIndentedString(avatar)).append("\n");
    sb.append("    picture: ").append(toIndentedString(picture)).append("\n");
    sb.append("    gender: ").append(toIndentedString(gender)).append("\n");
    sb.append("    profile: ").append(toIndentedString(profile)).append("\n");
    sb.append("    greeting: ").append(toIndentedString(greeting)).append("\n");
    sb.append("    chatStyle: ").append(toIndentedString(chatStyle)).append("\n");
    sb.append("    chatExample: ").append(toIndentedString(chatExample)).append("\n");
    sb.append("    defaultScene: ").append(toIndentedString(defaultScene)).append("\n");
    sb.append("    lang: ").append(toIndentedString(lang)).append("\n");
    sb.append("    ext: ").append(toIndentedString(ext)).append("\n");
    sb.append("    draft: ").append(toIndentedString(draft)).append("\n");
    sb.append("    tags: ").append(toIndentedString(tags)).append("\n");
    sb.append("    additionalProperties: ").append(toIndentedString(additionalProperties)).append("\n");
    sb.append("}");
    return sb.toString();
  }

  /**
   * Convert the given object to string with each line indented by 4 spaces
   * (except the first line).
   */
  private String toIndentedString(Object o) {
    if (o == null) {
      return "null";
    }
    return o.toString().replace("\n", "\n    ");
  }


  public static HashSet<String> openapiFields;
  public static HashSet<String> openapiRequiredFields;

  static {
    // a set of all properties/fields (JSON key names)
    openapiFields = new HashSet<String>();
    openapiFields.add("parentUid");
    openapiFields.add("visibility");
    openapiFields.add("name");
    openapiFields.add("description");
    openapiFields.add("nickname");
    openapiFields.add("avatar");
    openapiFields.add("picture");
    openapiFields.add("gender");
    openapiFields.add("profile");
    openapiFields.add("greeting");
    openapiFields.add("chatStyle");
    openapiFields.add("chatExample");
    openapiFields.add("defaultScene");
    openapiFields.add("lang");
    openapiFields.add("ext");
    openapiFields.add("draft");
    openapiFields.add("tags");

    // a set of required properties/fields (JSON key names)
    openapiRequiredFields = new HashSet<String>();
    openapiRequiredFields.add("name");
  }

 /**
  * Validates the JSON Element and throws an exception if issues found
  *
  * @param jsonElement JSON Element
  * @throws IOException if the JSON Element is invalid with respect to CharacterCreateDTO
  */
  public static void validateJsonElement(JsonElement jsonElement) throws IOException {
      if (jsonElement == null) {
        if (!CharacterCreateDTO.openapiRequiredFields.isEmpty()) { // has required fields but JSON element is null
          throw new IllegalArgumentException(String.format("The required field(s) %s in CharacterCreateDTO is not found in the empty JSON string", CharacterCreateDTO.openapiRequiredFields.toString()));
        }
      }

      // check to make sure all required properties/fields are present in the JSON string
      for (String requiredField : CharacterCreateDTO.openapiRequiredFields) {
        if (jsonElement.getAsJsonObject().get(requiredField) == null) {
          throw new IllegalArgumentException(String.format("The required field `%s` is not found in the JSON string: %s", requiredField, jsonElement.toString()));
        }
      }
        JsonObject jsonObj = jsonElement.getAsJsonObject();
      if ((jsonObj.get("parentUid") != null && !jsonObj.get("parentUid").isJsonNull()) && !jsonObj.get("parentUid").isJsonPrimitive()) {
        throw new IllegalArgumentException(String.format("Expected the field `parentUid` to be a primitive type in the JSON string but got `%s`", jsonObj.get("parentUid").toString()));
      }
      if ((jsonObj.get("visibility") != null && !jsonObj.get("visibility").isJsonNull()) && !jsonObj.get("visibility").isJsonPrimitive()) {
        throw new IllegalArgumentException(String.format("Expected the field `visibility` to be a primitive type in the JSON string but got `%s`", jsonObj.get("visibility").toString()));
      }
      if (!jsonObj.get("name").isJsonPrimitive()) {
        throw new IllegalArgumentException(String.format("Expected the field `name` to be a primitive type in the JSON string but got `%s`", jsonObj.get("name").toString()));
      }
      if ((jsonObj.get("description") != null && !jsonObj.get("description").isJsonNull()) && !jsonObj.get("description").isJsonPrimitive()) {
        throw new IllegalArgumentException(String.format("Expected the field `description` to be a primitive type in the JSON string but got `%s`", jsonObj.get("description").toString()));
      }
      if ((jsonObj.get("nickname") != null && !jsonObj.get("nickname").isJsonNull()) && !jsonObj.get("nickname").isJsonPrimitive()) {
        throw new IllegalArgumentException(String.format("Expected the field `nickname` to be a primitive type in the JSON string but got `%s`", jsonObj.get("nickname").toString()));
      }
      if ((jsonObj.get("avatar") != null && !jsonObj.get("avatar").isJsonNull()) && !jsonObj.get("avatar").isJsonPrimitive()) {
        throw new IllegalArgumentException(String.format("Expected the field `avatar` to be a primitive type in the JSON string but got `%s`", jsonObj.get("avatar").toString()));
      }
      if ((jsonObj.get("picture") != null && !jsonObj.get("picture").isJsonNull()) && !jsonObj.get("picture").isJsonPrimitive()) {
        throw new IllegalArgumentException(String.format("Expected the field `picture` to be a primitive type in the JSON string but got `%s`", jsonObj.get("picture").toString()));
      }
      if ((jsonObj.get("gender") != null && !jsonObj.get("gender").isJsonNull()) && !jsonObj.get("gender").isJsonPrimitive()) {
        throw new IllegalArgumentException(String.format("Expected the field `gender` to be a primitive type in the JSON string but got `%s`", jsonObj.get("gender").toString()));
      }
      if ((jsonObj.get("profile") != null && !jsonObj.get("profile").isJsonNull()) && !jsonObj.get("profile").isJsonPrimitive()) {
        throw new IllegalArgumentException(String.format("Expected the field `profile` to be a primitive type in the JSON string but got `%s`", jsonObj.get("profile").toString()));
      }
      if ((jsonObj.get("greeting") != null && !jsonObj.get("greeting").isJsonNull()) && !jsonObj.get("greeting").isJsonPrimitive()) {
        throw new IllegalArgumentException(String.format("Expected the field `greeting` to be a primitive type in the JSON string but got `%s`", jsonObj.get("greeting").toString()));
      }
      if ((jsonObj.get("chatStyle") != null && !jsonObj.get("chatStyle").isJsonNull()) && !jsonObj.get("chatStyle").isJsonPrimitive()) {
        throw new IllegalArgumentException(String.format("Expected the field `chatStyle` to be a primitive type in the JSON string but got `%s`", jsonObj.get("chatStyle").toString()));
      }
      if ((jsonObj.get("chatExample") != null && !jsonObj.get("chatExample").isJsonNull()) && !jsonObj.get("chatExample").isJsonPrimitive()) {
        throw new IllegalArgumentException(String.format("Expected the field `chatExample` to be a primitive type in the JSON string but got `%s`", jsonObj.get("chatExample").toString()));
      }
      if ((jsonObj.get("defaultScene") != null && !jsonObj.get("defaultScene").isJsonNull()) && !jsonObj.get("defaultScene").isJsonPrimitive()) {
        throw new IllegalArgumentException(String.format("Expected the field `defaultScene` to be a primitive type in the JSON string but got `%s`", jsonObj.get("defaultScene").toString()));
      }
      if ((jsonObj.get("lang") != null && !jsonObj.get("lang").isJsonNull()) && !jsonObj.get("lang").isJsonPrimitive()) {
        throw new IllegalArgumentException(String.format("Expected the field `lang` to be a primitive type in the JSON string but got `%s`", jsonObj.get("lang").toString()));
      }
      if ((jsonObj.get("ext") != null && !jsonObj.get("ext").isJsonNull()) && !jsonObj.get("ext").isJsonPrimitive()) {
        throw new IllegalArgumentException(String.format("Expected the field `ext` to be a primitive type in the JSON string but got `%s`", jsonObj.get("ext").toString()));
      }
      if ((jsonObj.get("draft") != null && !jsonObj.get("draft").isJsonNull()) && !jsonObj.get("draft").isJsonPrimitive()) {
        throw new IllegalArgumentException(String.format("Expected the field `draft` to be a primitive type in the JSON string but got `%s`", jsonObj.get("draft").toString()));
      }
      // ensure the optional json data is an array if present
      if (jsonObj.get("tags") != null && !jsonObj.get("tags").isJsonNull() && !jsonObj.get("tags").isJsonArray()) {
        throw new IllegalArgumentException(String.format("Expected the field `tags` to be an array in the JSON string but got `%s`", jsonObj.get("tags").toString()));
      }
  }

  public static class CustomTypeAdapterFactory implements TypeAdapterFactory {
    @SuppressWarnings("unchecked")
    @Override
    public <T> TypeAdapter<T> create(Gson gson, TypeToken<T> type) {
       if (!CharacterCreateDTO.class.isAssignableFrom(type.getRawType())) {
         return null; // this class only serializes 'CharacterCreateDTO' and its subtypes
       }
       final TypeAdapter<JsonElement> elementAdapter = gson.getAdapter(JsonElement.class);
       final TypeAdapter<CharacterCreateDTO> thisAdapter
                        = gson.getDelegateAdapter(this, TypeToken.get(CharacterCreateDTO.class));

       return (TypeAdapter<T>) new TypeAdapter<CharacterCreateDTO>() {
           @Override
           public void write(JsonWriter out, CharacterCreateDTO value) throws IOException {
             JsonObject obj = thisAdapter.toJsonTree(value).getAsJsonObject();
             obj.remove("additionalProperties");
             // serialize additional properties
             if (value.getAdditionalProperties() != null) {
               for (Map.Entry<String, Object> entry : value.getAdditionalProperties().entrySet()) {
                 if (entry.getValue() instanceof String)
                   obj.addProperty(entry.getKey(), (String) entry.getValue());
                 else if (entry.getValue() instanceof Number)
                   obj.addProperty(entry.getKey(), (Number) entry.getValue());
                 else if (entry.getValue() instanceof Boolean)
                   obj.addProperty(entry.getKey(), (Boolean) entry.getValue());
                 else if (entry.getValue() instanceof Character)
                   obj.addProperty(entry.getKey(), (Character) entry.getValue());
                 else {
                   JsonElement jsonElement = gson.toJsonTree(entry.getValue());
                   if (jsonElement.isJsonArray()) {
                     obj.add(entry.getKey(), jsonElement.getAsJsonArray());
                   } else {
                     obj.add(entry.getKey(), jsonElement.getAsJsonObject());
                   }
                 }
               }
             }
             elementAdapter.write(out, obj);
           }

           @Override
           public CharacterCreateDTO read(JsonReader in) throws IOException {
             JsonElement jsonElement = elementAdapter.read(in);
             validateJsonElement(jsonElement);
             JsonObject jsonObj = jsonElement.getAsJsonObject();
             // store additional fields in the deserialized instance
             CharacterCreateDTO instance = thisAdapter.fromJsonTree(jsonObj);
             for (Map.Entry<String, JsonElement> entry : jsonObj.entrySet()) {
               if (!openapiFields.contains(entry.getKey())) {
                 if (entry.getValue().isJsonPrimitive()) { // primitive type
                   if (entry.getValue().getAsJsonPrimitive().isString())
                     instance.putAdditionalProperty(entry.getKey(), entry.getValue().getAsString());
                   else if (entry.getValue().getAsJsonPrimitive().isNumber())
                     instance.putAdditionalProperty(entry.getKey(), entry.getValue().getAsNumber());
                   else if (entry.getValue().getAsJsonPrimitive().isBoolean())
                     instance.putAdditionalProperty(entry.getKey(), entry.getValue().getAsBoolean());
                   else
                     throw new IllegalArgumentException(String.format("The field `%s` has unknown primitive type. Value: %s", entry.getKey(), entry.getValue().toString()));
                 } else if (entry.getValue().isJsonArray()) {
                     instance.putAdditionalProperty(entry.getKey(), gson.fromJson(entry.getValue(), List.class));
                 } else { // JSON object
                     instance.putAdditionalProperty(entry.getKey(), gson.fromJson(entry.getValue(), HashMap.class));
                 }
               }
             }
             return instance;
           }

       }.nullSafe();
    }
  }

 /**
  * Create an instance of CharacterCreateDTO given an JSON string
  *
  * @param jsonString JSON string
  * @return An instance of CharacterCreateDTO
  * @throws IOException if the JSON string is invalid with respect to CharacterCreateDTO
  */
  public static CharacterCreateDTO fromJson(String jsonString) throws IOException {
    return JSON.getGson().fromJson(jsonString, CharacterCreateDTO.class);
  }

 /**
  * Convert an instance of CharacterCreateDTO to an JSON string
  *
  * @return JSON string
  */
  public String toJson() {
    return JSON.getGson().toJson(this);
  }
}

