## SpringBoot + mica-mqtt 应用演示

注：本项目依赖“new HTTP Client”、lombok插件,所以首先应在idea中安装此插件。

测试可以使用项目中http目录下的相应文件测试，此功能使用是“new HTTP Client”插件。

由于整个项目只能有一个环境变量文件“http-client.env.json”，所以本模块的环境变量文件“http-client.env.json”加了个后缀，做了独立项目使用时，去掉“http-client.env.json-mica-mqtt”去掉“-mica-mqtt”即可。

配置文件中涉及需要加密的地方，可能使用了Jasypt进行加密，生成密钥的方法参见JasyptUtil.java，关于jasypt-spring-boot-starter的用法，可以参考网文。


