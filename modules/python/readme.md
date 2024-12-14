<!--
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-08-29 09:30:25
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-08-31 14:51:46
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  仅支持企业内部员工自用，严禁私自用于销售、二次销售或者部署SaaS方式销售 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  技术/商务联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
-->
# readme

```bash
# 设置国内镜像
# pip config set global.index-url https://pypi.tuna.tsinghua.edu.cn/simple
# poetry source add --priority=default mirrors https://pypi.tuna.tsinghua.edu.cn/simple/
brew install libmagic
# 设置代理
export http_proxy=http://127.0.0.1:10818
export https_proxy=http://127.0.0.1:10818
# 清理缓存
pip cache purge
# 生成虚拟环境
python -m venv .venv
# 激活虚拟环境
source .venv/bin/activate
# deactivate
which python
# install poetry
pip install poetry
poetry config virtualenvs.prefer-active-python true
# python path
poetry run which python
# poetry add fastapi, uvicorn
poetry install --no-root
# run
python main.py
# nohup python main.py > output.log 2>&1 &
# 排除embeddings文件夹
sh cicd/scripts/release.sh
# 首次-上传所有
sh cicd/scripts/release-all.sh
```

```bash
```
