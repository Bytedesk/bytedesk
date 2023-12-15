# 说明

```bash
# 安装 python 3.11.4
pyenv install 3.11.4
pyenv global 3.11.4
# 
cd backend
python -m venv venv
pip install -r requirements.txt
# 
pip install permchain python-multipart
# REDIS_URL = "redis://:%s@%s:%s/%s"%(REDIS_PASSWORD,REDIS_HOST,REDIS_PORT,REDIS_DB)
export REDIS_URL=redis://:C8aJEVCCvSA1VFi8@127.0.0.1:6379/3
# 安装 redissearch

# openai
export OPENAI_API_KEY="sk-..."
# langsmith
export LANGCHAIN_TRACING_V2="true"
export LANGCHAIN_API_KEY=...
# 
export YDC_API_KEY=...
export TAVILY_API_KEY=...
# 启动后台
langchain serve --port=8100
# 启动前台
cd frontend
yarn
yarn dev
# 前台地址
# http://localhost:5173/ 
```
