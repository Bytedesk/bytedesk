---
sidebar_label: AI
sidebar_position: 7
---

# AI

:::tip

- 操作系统：Ubuntu 20.04 LTS
- 服务器最低配置2核4G内存，推荐配置4核8G内存

:::

## 初始化步骤

- [github下载](https://github.com/Bytedesk/bytedesk-ai)

```bash
# pip config set global.index-url https://pypi.tuna.tsinghua.edu.cn/simple
# poetry source add --priority=default mirrors https://pypi.tuna.tsinghua.edu.cn/simple/
sudo apt update
# # ubuntu install pyenv
sudo apt install git -y
# curl https://pyenv.run | bash
curl -L https://gitee.com/xinghuipeng/pyenv-installer/raw/master/bin/pyenv-installer | bash
# 编辑 ~/.bashrc
vi  ~/.bashrc
# 添加下面内容到 .bashrc
export PYENV_ROOT="$HOME/.pyenv"
[[ -d $PYENV_ROOT/bin ]] && export PATH="$PYENV_ROOT/bin:$PATH"
eval "$(pyenv init -)"
eval "$(pyenv virtualenv-init -)"
# 安装依赖
sudo apt-get install libbz2-dev libncurses5 libncurses5-dev libncursesw5
sudo apt-get install libffi-dev libreadline-dev openssl libssl-dev
sudo apt-get install libsqlite3-dev liblzma-dev lzma
sudo apt-get install ffmpeg libsm6 libxext6 libmagic1
# 另外打开一个终端
wget https://mirrors.huaweicloud.com/python/3.11.4/Python-3.11.4.tar.xz  -P ~/.pyenv/cache
pyenv install 3.11.4
pyenv versions
pyenv global 3.11.4
python -m venv .venv
source .venv/bin/activate
# 
pip install poetry
poetry config virtualenvs.prefer-active-python true
rm .env
cp .env.dev .env
# 修改 .env 文件中的数据库配置
mkdir files
# which python
# poetry run which python
# source .venv/bin/activate
poetry install --no-root
# 前台启动，仅用于测试，Ctrl+C 停止
# http://127.0.0.1:9008/
# python main.py
# 后台运行
# nohup python main.py > output.log 2>&1 &
# chmod +x start.sh
./start.sh
# 停止
# chmod +x stop.sh
./stop.sh
```
