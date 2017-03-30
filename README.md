# IoT\_OceanConnect\_North\_GUI\_APP\_Demo

本示例工程基于华为OceanConnect北向RESTful接口，完成了北向应用大部分的功能，对以下开发者有所帮助：

* 暂时不想进行北向应用开发，但是希望开发南向设备，接入华为物联网平台；
* 正在进行北向应用开发，希望获得一些代码示例来了解接口参数配置和返回值解析。

## 版本更新
North GUI App Demo V1.0

## 开发环境
* 操作系统：Windows7专业版
* JDK 8（JDK 7由于不支持TLS1.1/1.2，HTTPS连接会有问题）

## 文件指引
* application：windows下的可执行程序。
	* cert：HTTPS连接所用证书；（工具默认使用HTTPS连接）
	* Module.json：模块配置文件（可以通过配置该文件，使能北向应用的不同模块）；
	* Demo.jar：可执行程序;
	* ExceptionTrace.txt：异常记录。
* source_code：该工具源码示例。
	* src：源码文件
	* lib：依赖库
	* Module.json：模块配置文件（可以通过配置该文件，使能北向应用的不同模块）；
	* Demo.jar：可执行程序;
	* ExceptionTrace.txt：异常记录。

## 入门指导
参见华为物联网论坛的相关介绍：

http://developer.huawei.com/ict/forum/forum.php?mod=viewthread&tid=834&extra=

## 获取帮助
在开发过程中，您有任何问题均可以至DevCenter中提单跟踪。也可以在华为开发者社区中查找或提问。另外，华为技术支持热线电话：400-822-9999（转二次开发）