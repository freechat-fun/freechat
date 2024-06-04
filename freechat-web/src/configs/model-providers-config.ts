const providers = [{
  label: 'OpenAI',
  provider: 'open_ai',
},{
  label: 'Azure OpenAI',
  provider: 'azure_open_ai',
},{
  label: 'DashScope',
  provider: 'dash_scope',
}];

const defaultModels = {
  'open_ai': '[open_ai]gpt-4o',
  'azure_open_ai': '[azure_open_ai]gpt-4o',
  'dash_scope': '[dash_scope]qwen-max',
};

const defaultBaseURLs = {
  'open_ai': 'https://api.openai.com/v1',
  'azure_open_ai': 'https://{resource}.openai.azure.com/',
  'dash_scope': 'https://dashscope.aliyuncs.com/api/',
};

export { providers, defaultModels, defaultBaseURLs };