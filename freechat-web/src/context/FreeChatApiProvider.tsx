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
  FlowApi,
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
  configuration: Configuration;
  aiServiceApi: AIServiceApi;
  accountApi: AccountApi;
  accountManagerForAdminApi: AccountManagerForAdminApi;
  appConfigForAdminApi: AppConfigForAdminApi;
  appMetaForAdminApi: AppMetaForAdminApi;
  characterApi: CharacterApi;
  encryptionManagerForAdminApi: EncryptionManagerForAdminApi;
  flowApi: FlowApi;
  interactiveStatisticsApi: InteractiveStatisticsApi;
  organizationApi: OrganizationApi;
  pluginApi: PluginApi;
  promptApi: PromptApi;
  promptTaskApi: PromptTaskApi;
}

const FreeChatApiContext = createContext<FreeChatApiContextValue | null>(null);

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
  const flowApi = new FlowApi(configuration);
  const interactiveStatisticsApi = new InteractiveStatisticsApi(configuration);
  const organizationApi = new OrganizationApi(configuration);
  const pluginApi = new PluginApi(configuration);
  const promptApi = new PromptApi(configuration);
  const promptTaskApi = new PromptTaskApi(configuration);

  return (
    <FreeChatApiContext.Provider value={{
      configuration,
      aiServiceApi,
      accountApi,
      accountManagerForAdminApi,
      appConfigForAdminApi,
      appMetaForAdminApi,
      characterApi,
      encryptionManagerForAdminApi,
      flowApi,
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
