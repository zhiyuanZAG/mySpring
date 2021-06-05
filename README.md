# mySpring 



## 一、实现Spring的基本思路-V1



### 1. 配置阶段
| 步骤        | 操作                                          |
| -------------------- | ------------------------------------------------------------ |
| **配置web.xml**      | DispatcherServlet                                            |
| **设定init-param**   | **contextConfigLocation=classpath:application.xml**          |
| **设定url-parttern** | **/***                                                       |
| **配置Annotation**   | **@controller, @Service, @Autowired, @RequestParamMapping...** |



### 2. 初始化阶段
| 步骤        | 操作                                          |
| -------------------- | ------------------------------------------------------------ |
| **调用init()方法**               | 加载配置文件                                                 |
| **IoC容器初始化**                | **Map<String, Object>**                                      |
| **扫描相关的类**                 | **Scan-package="com.zhiyuanzag"**                            |
| **创建实例并保存至容器中 [IoC]** | **通过反射机制将实例化放到Ioc容器中**                        |
| **进行DI操作 [DI]**              | **扫描IoC容器的实例, 给没有复制的属性自动赋值**              |
| **初始化HandlerMapping [MVC]**   | **将一个URL和一个Method进行一对一的关联映射Map<String, Method>** |



### 3. 运行阶段
| 步骤        | 操作                                          |
| -------------------- | ------------------------------------------------------------ |
| **调用post() / doGet()方法**    | **web容器调用doPost()/doGet()方法, 获得request/response对象** |
| **匹配HandlerMapping**          | **从request对象中获取到对象输入的url, 找到其中对应的Method** |
| **反射调用method.invoke()方法** | **利用反射, 执行方法并返回结果**                             |
| **response.getWrite().write()** | **将返回结果输出到浏览器**                                 |





 

##  二、功能类拆分(IoC) [从servlet 到ApplicationContext]-V2  



### 1. 加载配置文件

* 借助BeanDefinitionReader来实现 (功能: 读取并加载配置文件, 可以通过定义各种不同的实现类, 分别实现从properties/ xml等文件形式中读取并加载配置文件)

* 主要步骤:

  a. 加载配置文件

  b. 包扫描

### 2. 解析配置文件, 将所有配置信息封装成BeanDefinition对象

* 一个BeanDefinition对象, 封装的是一个类对象的相关定义信息(如bean是否要延时加载等信息)

### 3. 把所有的配置信息缓存起来

* 通过容器register实现

### 4. 创建"非延时加载"的所有bean

* 循环调用getBean()方法