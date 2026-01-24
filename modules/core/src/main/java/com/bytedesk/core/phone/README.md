# 手机归属地查询

![GitHub](https://img.shields.io/github/license/eeemt/phone-number-geo)

## 简介
根据手机号确定手机号运营商即归属地, 支持包括虚拟运营商的中国大陆手机号查询.

## 数据源

数据源`dat`文件来自[pangongzi/phone](https://github.com/pangongzi/phone)提供的数据库, 会不定时同步更新数据库

当前数据源版本: `202502`
## maven
```xml
<dependency>
    <groupId>me.ihxq.projects</groupId>
    <artifactId>phone-number-geo</artifactId>
    <version>x.x.x-xxxxxx</version>
</dependency>
```
[这里](https://maven-badges.herokuapp.com/maven-central/me.ihxq.projects/phone-number-geo)获取最新版号.  


版本号解释:  
![](./version_explain.png)

## 示例
```java
class Demo1{
    public static void main(String[] args){
        PhoneNumberLookup phoneNumberLookup = new PhoneNumberLookup();
        PhoneNumberInfo found = phoneNumberLookup.lookup("18798896741").orElseThrow(RuntimeException::new);
    }
}
```
```java
class Demo2{
    public static void main(String[] args){
        PhoneNumberLookup phoneNumberLookup = new PhoneNumberLookup();
        String province = phoneNumberLookup.lookup("130898976761")
                        .map(PhoneNumberInfo::getAttribution)
                        .map(Attribution::getProvince)
                        .orElse("未知");
    }
}
```
```java
class Demo3{
    public static void main(String[] args){
        PhoneNumberLookup phoneNumberLookup = new PhoneNumberLookup();
        PhoneNumberInfo found = phoneNumberLookup.lookup("18798896741").orElseThrow(RuntimeException::new);
        found.getNumber(); // 18798896741
        found.getAttribution().getProvince(); // 贵州
        found.getAttribution().getCity(); // 贵阳
        found.getAttribution().getZipCode(); // 550000
        found.getAttribution().getAreaCode(); // 0851
        found.getIsp(); // ISP.CHINA_MOBILE
    }
}
```

## 对比`libphonenumber`
对比[libphonenumber](https://github.com/google/libphonenumber), `libphonenumber`有更多功能, 包括验证号码格式, 格式化, 时区等, 
但基于[xluohome/phonedata](https://github.com/xluohome/phonedata)提供的`dat`数据库能囊括包含虚拟运营商号段的更多号段.  

至于速度, 未做比较, 但本仓库实现已足够快, 选择时建议更多权衡易用性, 功能和数据覆盖范围.

## Benchmark

工程里已内置四种算法, 跑分情况如下:
```
Benchmark                                   Mode  Cnt        Score       Error  Units
BenchmarkRunner.anotherBinarySearchLookup   avgt    5      390.483 ±     3.544  ns/op
BenchmarkRunner.binarySearchLookup          avgt    5      386.357 ±     3.739  ns/op
BenchmarkRunner.prospectBinarySearchLookup  avgt    5      304.622 ±     1.899  ns/op
BenchmarkRunner.sequenceLookup              avgt    5  1555265.227 ± 48814.379  ns/op
```
性能测试源码位于`me.ihxq.projects.pna.benchmark.BenchmarkRunner`, 基于`JMH`

测试样本在每次启动时生成, 供所有算子测试使用, 所以每次测试结果有差异, 结果可用于横向比较, 不适用于纵向比较.

默认使用的是`me.ihxq.projects.pna.algorithm.BinarySearchAlgorithmImpl`, 
可以通过`new PhoneNumberLookup(new AlgorithmYouLike());`使用其他算法;  

也可自行实现算法, 实现`me.ihxq.projects.pna.algorithm.LookupAlgorithm`即可.

## 感谢
- 感谢[xluohome/phonedata](https://github.com/xluohome/phonedata)共享的数据库
- 也参考了@fengjiajie 的java实现[fengjiajie/phone-number-geo](https://github.com/fengjiajie/phone-number-geo)
