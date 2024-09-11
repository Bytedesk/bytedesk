#!/bin/sh
###
 # @Author: jackning 270580156@qq.com
 # @Date: 2024-08-09 10:35:30
 # @LastEditors: jackning 270580156@qq.com
 # @LastEditTime: 2024-08-28 09:24:27
 # @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 #   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 #  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 #  仅支持企业内部员工自用，严禁私自用于销售、二次销售或者部署SaaS方式销售 
 #  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 #  contact: 270580156@qq.com 
 #  联系：270580156@qq.com
 # Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
### 
RUN_NAME="bytedesk-starter-0.4.1.jar"
export LANG="en_US.UTF-8"
PRG=$0
APPDIRFILE=`dirname "$PRG"`
cd $APPDIRFILE
echo $APPDIRFILE/$RUN_NAME
PID=`ps -ef|grep $RUN_NAME|grep -v grep|awk '{printf $2}'`
echo $PID
if [ ! -n "$PID" ];
then
   echo "bytedesk程序开始启动"
else
   echo "已启动,杀掉进程后重启"
   echo $PID
   ps -ef | grep $RUN_NAME | grep -v grep | awk '{print $2}' | xargs kill
fi
if test -x $RUN_NAME;
then
nohup java -jar ./$RUN_NAME  >/dev/null 2>&1 &
else
sudo chmod +x $RUN_NAME
nohup java -jar ./$RUN_NAME  >/dev/null 2>&1 &
fi
