/* eslint-disable react-refresh/only-export-components */
import { createContext, useContext } from 'react';
import {
  servers,
  createConfiguration,
  Configuration,
  ServerConfiguration,
  AIServiceApi,
  AccountApi,
  AccountManagerForAdminApi,
  AppConfigForAdminApi,
  AppMetaForAdminApi,
  CharacterApi,
  ChatApi,
  ConnectorApi,
  EncryptionManagerForAdminApi,
  AgentApi,
  InteractiveStatisticsApi,
  OrganizationApi,
  PluginApi,
  PromptApi,
  PromptTaskApi,
  RagApi,
  SkillApi,
  TTSServiceApi,
} from 'freechat-sdk';

type FreeChatApiProps = {
  server?: string;
};

type FreeChatApiContextValue = {
  serverUrl: string | undefined;
  configuration: Configuration | undefined;
  aiServiceApi: AIServiceApi | undefined;
  accountApi: AccountApi | undefined;
  accountManagerForAdminApi: AccountManagerForAdminApi | undefined;
  appConfigForAdminApi: AppConfigForAdminApi | undefined;
  appMetaForAdminApi: AppMetaForAdminApi | undefined;
  characterApi: CharacterApi | undefined;
  chatApi: ChatApi | undefined;
  connectorApi: ConnectorApi | undefined;
  encryptionManagerForAdminApi: EncryptionManagerForAdminApi | undefined;
  agentApi: AgentApi | undefined;
  interactiveStatisticsApi: InteractiveStatisticsApi | undefined;
  organizationApi: OrganizationApi | undefined;
  pluginApi: PluginApi | undefined;
  promptApi: PromptApi | undefined;
  promptTaskApi: PromptTaskApi | undefined;
  ragApi: RagApi | undefined;
  skillApi: SkillApi | undefined;
  ttsServiceApi: TTSServiceApi | undefined;
};

const undefinedContext: FreeChatApiContextValue = {
  serverUrl: undefined,
  configuration: undefined,
  aiServiceApi: undefined,
  accountApi: undefined,
  accountManagerForAdminApi: undefined,
  appConfigForAdminApi: undefined,
  appMetaForAdminApi: undefined,
  characterApi: undefined,
  chatApi: undefined,
  connectorApi: undefined,
  encryptionManagerForAdminApi: undefined,
  agentApi: undefined,
  interactiveStatisticsApi: undefined,
  organizationApi: undefined,
  pluginApi: undefined,
  promptApi: undefined,
  promptTaskApi: undefined,
  ragApi: undefined,
  skillApi: undefined,
  ttsServiceApi: undefined,
};

const FreeChatApiContext =
  createContext<FreeChatApiContextValue>(undefinedContext);

const FreeChatApiProvider: React.FC<
  React.PropsWithChildren<FreeChatApiProps>
> = ({ server, children }) => {
  const parameters = {
    baseServer: server ? new ServerConfiguration(server, {}) : servers[0],
  };

  const configuration = createConfiguration(parameters);

  const aiServiceApi = new AIServiceApi(configuration);
  const accountApi = new AccountApi(configuration);
  const accountManagerForAdminApi = new AccountManagerForAdminApi(
    configuration
  );
  const appConfigForAdminApi = new AppConfigForAdminApi(configuration);
  const appMetaForAdminApi = new AppMetaForAdminApi(configuration);
  const characterApi = new CharacterApi(configuration);
  const chatApi = new ChatApi(configuration);
  const connectorApi = new ConnectorApi(configuration);
  const encryptionManagerForAdminApi = new EncryptionManagerForAdminApi(
    configuration
  );
  const agentApi = new AgentApi(configuration);
  const interactiveStatisticsApi = new InteractiveStatisticsApi(configuration);
  const organizationApi = new OrganizationApi(configuration);
  const pluginApi = new PluginApi(configuration);
  const promptApi = new PromptApi(configuration);
  const promptTaskApi = new PromptTaskApi(configuration);
  const ragApi = new RagApi(configuration);
  const skillApi = new SkillApi(configuration);
  const ttsServiceApi = new TTSServiceApi(configuration);

  return (
    <FreeChatApiContext.Provider
      value={{
        serverUrl: server,
        configuration,
        aiServiceApi,
        accountApi,
        accountManagerForAdminApi,
        appConfigForAdminApi,
        appMetaForAdminApi,
        characterApi,
        chatApi,
        connectorApi,
        encryptionManagerForAdminApi,
        agentApi,
        interactiveStatisticsApi,
        organizationApi,
        pluginApi,
        promptApi,
        promptTaskApi,
        ragApi,
        skillApi,
        ttsServiceApi,
      }}
    >
      {children}
    </FreeChatApiContext.Provider>
  );
};

export default FreeChatApiProvider;
export const useFreeChatApiContext = () => useContext(FreeChatApiContext);
