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
  EncryptionManagerForAdminApi,
  AgentApi,
  InteractiveStatisticsApi,
  OrganizationApi,
  PluginApi,
  PromptApi,
  PromptTaskApi,
} from 'freechat-sdk';

interface FreeChatApiProps {
  server?: string;
}

interface FreeChatApiContextValue {
  serverUrl: string | undefined;
  configuration: Configuration | undefined;
  aiServiceApi: AIServiceApi | undefined;
  accountApi: AccountApi | undefined;
  accountManagerForAdminApi: AccountManagerForAdminApi | undefined;
  appConfigForAdminApi: AppConfigForAdminApi | undefined;
  appMetaForAdminApi: AppMetaForAdminApi | undefined;
  characterApi: CharacterApi | undefined;
  encryptionManagerForAdminApi: EncryptionManagerForAdminApi | undefined;
  agentApi: AgentApi | undefined;
  interactiveStatisticsApi: InteractiveStatisticsApi | undefined;
  organizationApi: OrganizationApi | undefined;
  pluginApi: PluginApi | undefined;
  promptApi: PromptApi | undefined;
  promptTaskApi: PromptTaskApi | undefined;
}

const undefinedContext: FreeChatApiContextValue = {
  serverUrl: undefined,
  configuration: undefined,
  aiServiceApi: undefined,
  accountApi: undefined,
  accountManagerForAdminApi: undefined,
  appConfigForAdminApi: undefined,
  appMetaForAdminApi: undefined,
  characterApi: undefined,
  encryptionManagerForAdminApi: undefined,
  agentApi: undefined,
  interactiveStatisticsApi: undefined,
  organizationApi: undefined,
  pluginApi: undefined,
  promptApi: undefined,
  promptTaskApi: undefined,
}

const FreeChatApiContext = createContext<FreeChatApiContextValue>(undefinedContext);

const FreeChatApiProvider: React.FC<React.PropsWithChildren<FreeChatApiProps>> = ({server, children }) => {
  const parameters = {
    baseServer: server ? new ServerConfiguration(server, {}) : servers[0]
  }

  const configuration = createConfiguration(parameters);

  const aiServiceApi = new AIServiceApi(configuration);
  const accountApi = new AccountApi(configuration);
  const accountManagerForAdminApi = new AccountManagerForAdminApi(configuration);
  const appConfigForAdminApi = new AppConfigForAdminApi(configuration);
  const appMetaForAdminApi = new AppMetaForAdminApi(configuration);
  const characterApi = new CharacterApi(configuration);
  const encryptionManagerForAdminApi = new EncryptionManagerForAdminApi(configuration);
  const agentApi = new AgentApi(configuration);
  const interactiveStatisticsApi = new InteractiveStatisticsApi(configuration);
  const organizationApi = new OrganizationApi(configuration);
  const pluginApi = new PluginApi(configuration);
  const promptApi = new PromptApi(configuration);
  const promptTaskApi = new PromptTaskApi(configuration);

  return (
    <FreeChatApiContext.Provider value={{
      serverUrl: server,
      configuration,
      aiServiceApi,
      accountApi,
      accountManagerForAdminApi,
      appConfigForAdminApi,
      appMetaForAdminApi,
      characterApi,
      encryptionManagerForAdminApi,
      agentApi: agentApi,
      interactiveStatisticsApi,
      organizationApi,
      pluginApi,
      promptApi,
      promptTaskApi,
    }}>
      {children}
    </FreeChatApiContext.Provider>
  );
};

export default FreeChatApiProvider;
export const useFreeChatApiContext = () => useContext(FreeChatApiContext);
