const providers = [
  {
    label: 'OpenAI',
    provider: 'open_ai',
    enableApiKey: true,
  },
  {
    label: 'Azure OpenAI',
    provider: 'azure_open_ai',
    enableApiKey: true,
  },
  {
    label: 'DashScope',
    provider: 'dash_scope',
    enableApiKey: true,
  },
  {
    label: 'Ollama',
    provider: 'ollama',
    enableApiKey: false,
  },
];

const defaultModels = {
  open_ai: '[open_ai]gpt-4o',
  azure_open_ai: '[azure_open_ai]gpt-4o',
  dash_scope: '[dash_scope]qwen-max',
  ollama: '[ollama]llama3.2|text2chat',
};

const defaultBaseURLs = {
  open_ai: 'https://api.openai.com/v1',
  azure_open_ai: 'https://{resource}.openai.azure.com/',
  dash_scope: 'https://dashscope.aliyuncs.com/api/',
  ollama: 'https://localhost:11434',
};

export { providers, defaultModels, defaultBaseURLs };
