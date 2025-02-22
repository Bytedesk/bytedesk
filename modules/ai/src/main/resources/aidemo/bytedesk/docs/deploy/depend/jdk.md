---
sidebar_label: JDK17
sidebar_position: 7
---

# JDK17

:::tip

- 操作系统：Ubuntu 20.04 LTS
- 服务器最低配置2核4G内存，推荐配置4核8G内存

:::

- [Oracle官网](https://www.oracle.com/java/technologies/downloads/)
- [下载](https://download.oracle.com/java/17/latest/jdk-17_linux-x64_bin.tar.gz)JDK17

### 解压

``` bash
sudo tar -zxvf jdk-17_linux-x64_bin.tar.gz
```

解压后文件夹 jdk-17.0.10

### 先在 /usr 下新建文件夹 java ，然后将文件夹jdk-17.0.10移动到目录/usr/java下

``` bash
mkdir /usr/java
sudo mv jdk-17.0.10 /usr/java/
```

### 现在配置系统环境变量，现在我们在全局配置文件/etc/profile下配置，即为所有用户配置Java环境，使用vi命令编辑/etc/profile文件：

``` bash
sudo vi /etc/profile
```

### 在文件底部加上四条配置信息

``` bash
export JAVA_HOME=/usr/java/jdk-17.0.10
export JRE_HOME=${JAVA_HOME}/jre
export CLASSPATH=.:%{JAVA_HOME}/lib:%{JRE_HOME}/lib
export PATH=${JAVA_HOME}/bin:$PATH
```

### 编辑好后保存退出，执行命令：

``` bash
source /etc/profile
```

### 验证是否安装成功

``` bash
java -version
```

### 安装成功

``` bash
java version "17.0.10" 2024-01-16 LTS
Java(TM) SE Runtime Environment (build 17.0.10+11-LTS-240)
Java HotSpot(TM) 64-Bit Server VM (build 17.0.10+11-LTS-240, mixed mode, sharing)
```

## 其他
<!-- 
由于受到美国对出口软件的限制，我们需要手动[下载jdk加密jar包](http://www.oracle.com/technetwork/java/javase/downloads/jce8-download-2133166.html)，解压之后，需要将local_policy.jar和US_export_policy.jar放到jdk安装文件夹jre/lib/security文件夹，比如：

```bash
apt install unzip
unzip jce_policy-8.zip
cd UnlimitedJCEPolicyJDK8
# 根据自己电脑实际路径配置
# 可通过命令 echo $JAVA_HOME 查看jdk本地路径
# 如：/usr/java/jdk1.8.0_351/jre/lib/security
sudo cp US_export_policy.jar /usr/java/jdk1.8.0_351/jre/lib/security
sudo cp local_policy.jar /usr/java/jdk1.8.0_351/jre/lib/security
``` -->
