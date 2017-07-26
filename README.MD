#pureJ
纯简的java服务应用。

###框架组合
核心模块core：maven + SpringBoot + Mybatis

工具化模块go：nodeJS + jQuery + bootstrap

###项目结构
biz ： 业务服务配置

core ： 框架核心组件，可以jar的形态被引用

demo-app ： 实例应用，默认为为空项目，只需maven依赖core即可

doc ： 框架设计说明文档

go ： 工具化模块工程，nodeJS + html + jQuery + css

###程序入口
Core：Application.java

go：app.js