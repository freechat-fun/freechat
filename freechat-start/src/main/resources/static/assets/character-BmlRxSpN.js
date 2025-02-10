const e="角色档案",t="问候语",a="操作",o="风格",r="知识",s="相册",n="来源",c="状态",i="文件",m="网址",l={Profile:e,"Chat Style":"聊天风格","Chat Example":"聊天示例","Default Scene":"默认场景","The default scene will be set as the default conversation background information when creating a new chat.":"在创建新聊天时，“默认场景”将被设置为默认的对话背景信息。",Greeting:t,"Character information":"角色信息","Character backends: (maximum of 3 backends allowed)":"角色后端：(最多允许 3 个后端配置)","Character documents: (A maximum of 10 documents are allowed, each document is less than 3M)":"角色文档：(最多允许 10 篇文档，每篇文档小于 3M)","Character's information, significantly influences chat feedback":"角色的信息，显著影响聊天反馈","Do you really want to delete this character?":"你真的想删除这个角色吗？","Creation Time":"创建时间","Message Window":"消息窗口","Long Term Memory Window":"长期记忆窗口","Proactive Chat Waiting Time":"主动对话时延","Enable TTS":"启用 TTS","Builtin Voice":"内置语音","Custom Voice":"自定义语音","Enable Album Tool":"启用相册工具","Moderation Model":"审核模型","Quota Limit":"配额限制","Quota limit":"配额限制","Preset Memory":"预设记忆","Preset memory":"预设记忆","Adjust preset memory.":"调整预设记忆。","After reaching the quota limit, users need to use their own API-Key to continue chatting.":"达到配额限制后，用户需要使用自己的 API-Key 才能继续聊天。","Enable characters to read replies.":"允许角色读出回复。","MP3 format, about 5 seconds, size less than 1M.":"MP3 格式，约 5 秒，尺寸小于 1M。","If the user wants to see photos during a chat, select one from the album to display.":"如果在聊天时，用户想看照片，从相册中选择一张展示。","As Default":"作为默认",Actions:a,"Do you really want to delete this character backend?":"你真的想删除这个角色后端吗？","Do you really want to delete this document?":"你真的想删除这篇文档吗？","Character backend parameters":"角色后端参数","Set the maximum number of historical messages sent to the model.":"设置发送给模型的历史消息最大数量","Set the maximum number of long term memory rounds (a round includes a user message and a character reply) sent to the model, 0 to disable. Recalled long-term memory messages will be placed at the top of the historical message window.":"设置发送到模型的长期记忆的最大轮次数量（一个轮次包括一条用户消息和一条角色回复），0 表示禁用。召回的长期记忆消息将放置在历史消息窗口的顶部。","Set the number of minutes to wait before evoking a proactive message, 0 to disable.":"设置在发出主动消息之前等待的分钟数，0 表示禁用。","Adjust prompt words.":"调整提示词。","Set up the moderation model.":"设置审核模型","Set character metadata":"设置角色元信息","Set character profile":"设置角色资料","Affects character performance":"影响着角色的表现",Style:o,Knowledge:r,Album:s,"Set character backend":"设置角色后端","The character can connect to different backends":"角色可以连接到不同的后端","History messages":"历史消息","Long term memory":"长期记忆","Prompt Template":"提示词模版","Prompt template":"提示词模版","Model parameters":"模型参数","Select tools":"选择工具","Moderation model":"审核模型","Set another backend":"设置另一个后端","Do you really want to delete this picture?":"你真的想删除这张图片吗？","Start Time":"开始时间","End Time":"结束时间",Source:n,Status:c,"Add Document":"添加文档",FILE:i,URL:m,"Supported file formats include txt, doc, docx, pdf, ppt, pptx, xls, xlsx, etc. The maximum size for a single file is 3MB.":"支持 txt，doc，docx，pdf，ppt，pptx，xls，xlsx 等文件格式，单文件尺寸不超过 3M。","Only publicly accessible URLs are supported.":"仅支持可公开访问的 URL。","Invalid URL":"无效的 URL","File too large!":"文件过大！","Splitter Settings":"切分设置","Max Segment Size":"最大分段尺寸","The maximum size of a segment in tokens.":"以 tokens 计算的最大分段尺寸。","Max Overlap Size":"最大重叠尺寸","The maximum size of the overlap between segments in tokens.":"以 tokens 计算的段落之间重叠的最大尺寸。","Recommended Characters":"推荐角色","Your Characters":"你的角色"};export{a as Actions,s as Album,i as FILE,t as Greeting,r as Knowledge,e as Profile,n as Source,c as Status,o as Style,m as URL,l as default};
