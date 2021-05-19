# mySpring 



## 实现Spring的基本思路-V1



### 1. 配置阶段

| 配置web.xml          | DispatcherServlet                                            |
| -------------------- | ------------------------------------------------------------ |
| **设定init-param**   | **contextConfigLocation=classpath:application.xml**          |
| **设定url-parttern** | **/***                                                       |
| **配置Annotation**   | **@controller, @Service, @Autowired, @RequestParamMapping...** |



### 2. 初始化阶段

| 调用init()方法                   | 加载配置文件                                                 |
| -------------------------------- | ------------------------------------------------------------ |
| **IoC容器初始化**                | **Map<String, Object>**                                      |
| **扫描相关的类**                 | **Scan-package="com.zhiyuanzag"**                            |
| **创建实例并保存至容器中 [IoC]** | **通过反射机制将实例化放到Ioc容器中**                        |
| **进行DI操作 [DI]**              | **扫描IoC容器的实例, 给没有复制的属性自动赋值**              |
| **初始化HandlerMapping [MVC]**   | **将一个URL和一个Method进行一对一的关联映射Map<String, Method>** |



### 3. 运行阶段

| **调用post() / doGet()方法**    | **web容器调用doPost()/doGet()方法, 获得request/response对象** |
| ------------------------------- | ------------------------------------------------------------ |
| **匹配HandlerMapping**          | **从request对象中获取到对象输入的url, 找到其中对应的Method** |
| **反射调用method.invoke()方法** | **利用反射, 执行方法并返回结果**                             |
| **response.getWrite().write()** | **将返回结果输出到浏览器**                                   |

