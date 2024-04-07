<!--
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-01-29 16:23:55
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-04-07 10:35:28
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  仅支持企业内部员工自用，严禁私自用于销售、二次销售或者部署SaaS方式销售 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
-->
# online customer service - 在线客服

```bash
# https://blog.csdn.net/jgwmjz/article/details/132378798
<!-- https://mvnrepository.com/artifact/com.github.wvengen/proguard-maven-plugin -->
<plugin>
    <groupId>com.github.wvengen</groupId>
    <artifactId>proguard-maven-plugin</artifactId>
    <version>${proguard.maven.plugin.version}</version>
    <executions>
        <!--以下配置说明执行mvn的package命令时候，会执行proguard-->
        <execution>
            <phase>package</phase>
            <goals>
                <goal>proguard</goal>
            </goals>
        </execution>
    </executions>
    <configuration>
        <!--输入jar-->
        <injar>${project.build.finalName}.jar</injar>
        <!--输出jar-->
        <outjar>${project.build.finalName}.jar</outjar>
        <!--是否混淆-->
        <obfuscate>true</obfuscate>
        <!-- 指定该模块是否是项目的一部分 -->
        <attach>true</attach>
        <!--配置文件-->
        <proguardInclude>${project.basedir}/proguard.cfg</proguardInclude>
        <!--额外的依赖-->
        <!-- <libs>
            <lib>${java.home}/lib/rt.jar</lib>
        </libs> -->
        <!--对输入jar继续过滤-->
        <inLibsFilter>!META-INF/**</inLibsFilter>
        <!--输出路径配置-->
        <outputDirectory>${project.basedir}/target</outputDirectory>
        <!--混淆的一些细节选项-->
        <!--
        <options>
            <option></option>
        </options>
        -->
    </configuration>
</plugin>
```

```bash
# https://blog.csdn.net/xiao_jiu_xian/article/details/131050127
# https://waylau.com/use-proguard-maven-plugin-to-obfuscate-the-spring-boot-program/
<proguard.version>7.4.2</proguard.version>
<proguard.maven.plugin.version>2.6.1</proguard.maven.plugin.version>
# 
<plugin>
    <groupId>com.github.wvengen</groupId>
    <artifactId>proguard-maven-plugin</artifactId>
    <version>${proguard.maven.plugin.version}</version>
    <executions>
        <execution>
            <phase>package</phase>
            <goals>
                <goal>proguard</goal>
            </goals>
        </execution>
    </executions>

    <configuration>
        <proguardVersion>${proguard.version}</proguardVersion>
        <injar>${project.build.finalName}.jar</injar>
        <outjar>${project.build.finalName}.jar</outjar>
        <obfuscate>true</obfuscate>
        <options>
            <option>-dontshrink</option>
            <option>-dontoptimize</option>

            <!-- 此选项将用新的类名替换反射方法调用中的所有字符串。例如，调用Class.forName('className') -->
            <option>-adaptclassstrings</option>

            <!-- 此选项将保存所有原始注解等。否则，将从文件中删除所有注解。 -->
            <option>-keepattributes
                Exceptions,
                InnerClasses,
                Signature,
                Deprecated,
                SourceFile,
                LineNumberTable,
                *Annotation*,
                EnclosingMethod
            </option>

            <!-- 此选项将保存接口中的所有原始名称（不混淆） -->
            <option>-keepnames interface **</option>

            <!-- 此选项将将所有原始方法参数 -->
            <option>-keepparameternames</option>

            <!-- 此选项将保存所有原始类文件（不混淆），一般是混淆领域或者服务包中的文件。 -->
            <option>-keep
                class com.waylau.proguard.ProguardMavenPluginSpringBootExampleApplication {
                    public static
                    void main(java.lang.String[]);
                }
            </option>

            <!-- 此选项忽略警告，例如重复的类定义和命名不正确的文件中的类 -->
            <option>-ignorewarnings</option>

            <!-- 此选项将保存服务包中的所有原始类文件（不进行混淆） -->
            <!-- <option>-keep class com.waylau.proguard.service { *; }</option> -->
            
            <!-- 此选项将保存所有软件包中的所有原始接口文件（不进行混淆） -->
            <option>-keep interface * extends * { *; }</option>
            
            <!-- 此选项将保存所有包中所有类中的所有原始定义的注解 -->
            <option>-keep class com.fasterxml.jackson.** { *; }</option>
            <option>-keep class org.json.JSONObject.** {**
                put(java.lang.String,java.util.Map);}</option>
            <option>-keepclassmembers class * {
                @org.springframework.context.annotation.Bean *;
                @org.springframework.beans.factory.annotation.Autowired *;
                @org.springframework.beans.factory.annotation.Value *;
                }

            </option>

            <option>-dontwarn com.fasterxml.jackson.databind.**</option>
            <option>-dontwarn com.fasterxml.jackson.**</option>

        </options>
        <injarNotExistsSkip>true</injarNotExistsSkip>
        <libs>
            <!--在此添加需要的类库 -->
            <!--<lib>${java.home}/lib/rt.jar</lib> -->
        </libs>

    </configuration>
    <dependencies>
        <dependency>
            <groupId>com.guardsquare</groupId>
            <artifactId>proguard-base</artifactId>
            <version>${proguard.version}</version>
        </dependency>
    </dependencies>
</plugin>
```
