

## 关于SDoc
由于Spring Boot能够快速开发、便捷部署等特性，相信有很大一部分Spring Boot的用户会用来构建RESTful API。而我们构建RESTful API的目的通常都是提供接口文档给多个开发人员：IOS开发、Android开发或是Web开发等。往往这部分接口文档都需要开发人员自己维护，本来开发任务繁重，在很忙的时候还要整理这部分接口文档，很是麻烦。

况且随着需求的不断变化接口参数也会不断变化，同时还要更新这部分接口文档所需代价太大，而这文档又是必须的，前台还等着你的文档进行对接呢，对于项目紧而人手不够时，SDoc可能会帮到你。

在做SDoc之前我通常是用swagger，但用着用着发现swagger太大了，依赖又多，平时也不需要这么多功能，索性就自己开发一个简化版，所以SDoc第一版出来了，代码量很小，加上ui部分打包下来才一百多K。

当然第一版功能不是很多，以后会慢慢完善各个功能模块，也会一直维护这个项目，因为平时项目用得着它。


## 快速上手
目前第一版只支持spring boot的项目，项目引用的话也很简单，在pom.xml中加入SDoc的依赖

```xml
<dependency>
    <groupId>com.apcc4m</groupId>
    <artifactId>sdoc</artifactId>
    <version>1.0.0</version>
</dependency>
```

创建SDoc配置类SdocConfig.
```java
@Configuration
@EnableSdoc
public class SdocConfig {

    @Bean
    public SdocInfo createSdocInfo1(){
        SdocInfo sdocInfo = new SdocInfo();
        sdocInfo.setGroupName("文档1");
        sdocInfo.setScannerPath("com.apcc4m.test.controller.admin");
        return sdocInfo;
    }
    
    @Bean
    public SdocInfo createSdocInfo2(){
        SdocInfo sdocInfo = new SdocInfo();
        sdocInfo.setGroupName("文档2");
        sdocInfo.setScannerPath("com.apcc4m.test.controller.weixin");
        return sdocInfo;
    }
}
```

目前只支持包路径扫描，以后会支持路径过滤等功能。

如同swagger，如果需要对接口进行说明，则需要在接口方法上方加入注解

```
@Sdoc(value = "获取用户列表", notes = "后台通过分页参数获取指定页码的用户数据")
@RequestMapping(value = "/list", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
@ResponseBody
public PageList<UserInfo> list(HttpServletRequest req, PageBean pageBean) throws Exception {
    PageList<UserInfo> result = userManageService.findUserList(pageBean);
    return result;
}
```
如果没加说明，则默认是方法名，目前第一版暂不支持对参数的说明，以后的版本会加上，有些人会认为这会侵入原来的代码，不是很喜欢，但这种程度的侵入就当是给方法加注释了，本身SDoc又比较小，对你整体项目部署也不会有太大影响

编译成功后通过项目url+/sdoc/index.html即可访问接口文档，例如：http://localhost:8888/sdoc/index.html

运行如下图所示，每个接口一目了然，当然少不了对接口的测试，只需一点，就能自动判断请求方式和参数，还是相当方便的，

![SDoc Screenshot](https://raw.github.com/apcc4m/sdoc/master/sdoc1.png)


## 问题
关于有些开发者加完以后不能访问接口文档，出现404找不到的情况，请检查代码中是否继承了spring-mvc中WebMvcConfigurerAdapter这个抽象类，因为在spring-boot中Auto-configuration模块在你没有覆盖

这个配置时它会自动用WebMvcAutoConfigurationAdapter继承WebMvcConfigurerAdapter进行默认配置，而该默认配置默认会开启classpath:/META-INF/resources/下的权限访问。所以需要在你自己的配置中重新加入该

路径的配置。

```
    @Override
    public void addResourceHandlers(final ResourceHandlerRegistry registry) {
        super.addResourceHandlers(registry);
        registry.addResourceHandler("/**").addResourceLocations("classpath:/META-INF/resources/");
    }
```