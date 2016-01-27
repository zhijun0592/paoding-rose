第一支程序



# 前件：运行环境 #
  * 已经下载rose开发包，如果没有，请先把Rose[下载](Rose_Download.md)下来
  * 确认Java运行环境没问题：[开发和运行环境](Rose_Environment.md)
  * 有一个Tomcat、Resin或其他web容器：[开发和运行环境](Rose_Environment.md)
  * 有一个Java编辑器，如Eclipse等(此文假设您使用的是Eclipse)

# 后件：成果 #
> 按照这个文档说明的步骤开发后，最后能够把Rose运行起来，并通过 http://127.0.0.1/hello/world 访问，呈现以下的效果：<p>
<img src='http://paoding-rose.googlecode.com/svn/other/imgs/demo/app1/hello.png' /></li></ul>

<h1>创建动态web项目 #
```
Eclipse
 --> New
 --> Other...
 --> Web --> Dynamic Web Project
 --> Project Name: HelloRose
 --> Target Runtime: 
     如果Eclipse已经有Server环境(Tomcat等)，选一个；
     如果没有的话，选择右边的New按钮，创建Server环境
 --〉Next
 --> Context Root: 清空代表所要运行的web程序在根目录下
 --> Finish
 --> 确认项目使用的Java Compiler compliance level为1.6(项目属性->Java Compiler选项卡)
 --> 确认项目使用的字符集是UTF-8 （项目属性->Resource选项卡)
```

# WEB-INF #
  * 在web.xml下配置一个Filter,以及 **可选的** log4j功能

```
<context-param>
        <param-name>log4jConfigLocation</param-name>
        <param-value>/WEB-INF/log4j.properties</param-value>
</context-param>

<listener>
        <listener-class>org.springframework.web.util.Log4jConfigListener</listener-class>
</listener>

<filter>
        <filter-name>roseFilter</filter-name>
        <filter-class>net.paoding.rose.RoseFilter</filter-class>
</filter>

<filter-mapping>
        <filter-name>roseFilter</filter-name>
        <url-pattern>/*</url-pattern>
        <dispatcher>REQUEST</dispatcher>
        <dispatcher>FORWARD</dispatcher>
        <dispatcher>INCLUDE</dispatcher>
</filter-mapping>
```

  * 创建WEB-INF/log4j.properties

```
log4j.rootLogger=INFO, stdout

log4j.appender.stdout=org.apache.log4j.ConsoleAppender
log4j.appender.stdout.layout=org.apache.log4j.PatternLayout
log4j.appender.stdout.layout.ConversionPattern=%d %p [%c] - %m%n

log4j.logger.org.springframework=DEBUG
log4j.logger.net.paoding=DEBUG
# set roseInfo INFO to close /rose-inf
log4j.logger.net.paoding.rose.web.controllers.roseInfo=DEBUG
```

请顺便确认WEB-INF/lib含有log4j的jar，如果没有那就下载一个(1.0 的rose依赖包已经含有)。

  * 创建WEB-INF/toolbox.xml文件

```
<?xml version="1.0"?>
<toolbox>
	<tool>
		<key>date</key>
		<scope>application</scope>
		<class>org.apache.velocity.tools.generic.DateTool</class>
		<parameter name="format" value="yyyy-MM-dd" />
	</tool>
</toolbox>
```

# 拷贝jar包 #
  * 把下载包中的 `dist/paoding-rose*.jar` 拷贝到 WebContent/WEB-INF/lib下
  * 把下载包中的 lib/ 的所有jar拷贝到 WebContent/WEB-INF/lib下
  * 刷新 Eclipse 中该项目，使 build path 生效

# 设计视图模板 #
  * 在WebContent下创建views子目录
  * 在views目录下创建hello-world.vm文件

```
<html>
	<head></head>
	<body>#msg("hello"), it's $date.format("yy-MM-dd HH:mm:ss", $now)</body>
</html>
```

# 国际化文件 #
  * 在src下创建messages.xml 【[xml格式参考](http://www.ibm.com/developerworks/java/library/j-tiger02254.html)】
```
<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE properties SYSTEM "http://java.sun.com/dtd/properties.dtd">
<properties>
	<entry key="hello">您好</entry>
</properties>
```

  * 也可以选择properties文件格式的messages.properties，此时需要修改messages.properties的字符集从ISO-8859-1调整为UTF-8   (选中文件--> 右键-->Resource-->Text encoding-->Other: UTF-8)
```
hello = 您好
```

# 编写控制器 #

创建com.xiaonei.rose.gettingStarted.**controllers**.HelloController
```
package com.xiaonei.rose.gettingStarted.controllers;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;

import net.paoding.rose.web.Invocation;

public class HelloController {

    public String world(Invocation inv) {
        inv.addModel("now", new Date());
        return "hello-world";
    }
}

```

# 做一个标识 #

目前来说这个步骤是可选的，但是如果您是通过打jar来部署的，则这个步骤不可避免。

在src下建立创建META-INF/rose.properties文件，写上rose=controllers, applicationContext, dao, messages

这个标识指示rose要识别你开发的jar包中的web代码、jar根目录下的`applicationContext*.xml、messages*.xml或messages*.properties`，以及jar包中的Jade DAO对象（Jade在本示例没有说明，他是rose提供的DAO编写规范，你只要写DAO接口就可以使用他访问数据库的技术，这个技术一样在人人网、糯米网大力使用 详见：[Jade DAO规范](Jade_DAO_Spec.md)）


# 启动 #
```
Eclipse
 --> 选中项目
 --> 右键
 --> Run As --> Run On Server
 --> 启动日志，最后看到打印的module日志(mappingPath, url, controllers等信息)
 --> OK
```

# 验证 #
访问 http://127.0.0.1/hello/world 或 http://127.0.0.1:8080/hello/world<br>
<p>
<img src='http://paoding-rose.googlecode.com/svn/other/imgs/demo/app1/hello.png' />

<h1>接下来</h1>

接下来你可以看一个更丰富的、比较完整的程序 <a href='Rose_Guide_Application2.md'>第二支程序</a>