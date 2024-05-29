const providers = [{
  label: 'OpenAI',
  provider: 'open_ai',
},{
  label: 'DashScope',
  provider: 'dash_scope',
}];

const defaultModels = {
  'open_ai': '[open_ai]gpt-4o',
  'dash_scope': '[dash_scope]qwen-max',
}

export { providers, defaultModels };