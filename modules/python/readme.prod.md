<!--
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-01-05 17:32:28
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-09-11 07:15:40
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  仅支持企业内部员工自用，严禁私自用于销售、二次销售或者部署SaaS方式销售 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  技术/商务联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
-->
# 微语ai

## 初始化步骤

```bash
# pip config set global.index-url https://pypi.tuna.tsinghua.edu.cn/simple
# poetry source add --priority=default mirrors https://pypi.tuna.tsinghua.edu.cn/simple/
# 远程连接redis
# http://114.55.33.206:8001/redis-stack/browser
# https://vegastack.com/tutorials/how-to-install-python-3-11-on-ubuntu-20-04/
sudo apt update
# # ubuntu install pyenv
sudo apt install git -y
# curl https://pyenv.run | bash
curl -L https://gitee.com/xinghuipeng/pyenv-installer/raw/master/bin/pyenv-installer | bash
# 编辑 ~/.bashrc
vi  ~/.bashrc
# 添加下面内容到 
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
cp .env.prod .env
# 修改.env 文件中redis相关配置
mkdir files
# which python
# poetry run which python
# source .venv/bin/activate
poetry install --no-root
# 前台启动，仅用于测试，Ctrl+C 停止
# http://121.199.165.116:9007/
# python main.py
# 后台运行
# nohup python main.py > output.log 2>&1 &
# chmod +x start.sh
./start.sh
# 停止
# chmod +x stop.sh
./stop.sh
```

## 其他

```bash
# 设置代理
export http_proxy=http://127.0.0.1:10818
export https_proxy=http://127.0.0.1:10818
# https://python.langchain.com/docs/integrations/vectorstores/redis#deployment-options
# 1.进入redis的容器：docker exec -it redis-stack bash
# 2.运行命令：redis-cli
# rvl index listall --password C8aJEVCCvSA1VFi8
# rvl index info --password C8aJEVCCvSA1VFi8 -i users
# rvl stats --password C8aJEVCCvSA1VFi8 -i users
# 
# embedings
# bge-large-zh-v1.5
# bge-small-zh-v1.5
# m3e-base
```

## 参考

- [danswer](https://github.com/danswer-ai/danswer)
- [opengpts](https://github.com/langchain-ai/opengpts)
