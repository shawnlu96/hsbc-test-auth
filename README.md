# Java 权限系统

## 简介
本项目使用Java原生HttpServer构建了一个简单的权限服务，其中包含了用户和角色的创建、删除，角色的分配、登录获取token以及根据token信息查询用户信息等。

项目主体的文件结构包括：

| 文件（夹）      | 作用                    |
|------------|-----------------------|
| exceptions | 自定义异常类                |
| handlers   | 用于处理请求的Handler        |
| models     | 一些模型类和Domain的存放       |
| services   | 服务层，对于逻辑进行整合          |
| storage    | 内存中存储的结构              |
| utils      | 一些自定义工具类，包括Json序列化和加密 |

## 开发环境 & 引用类库

基于 Maven 3.8.1 和 JDK 11 编写。

类库：
- Junit: 用于测试用例编写


## 接口文档

见 [**Wiki页面**](https://github.com/shawnlu96/hsbc-test-auth/wiki/) 

## 测试

 ```
 mvn compile test
```
## 配置
端口号默认为8080，可以在 [`config.properties`](src/main/resources/config.properties)中更改。



## 运行
 ```
 mvn compile exec:java
```

## 实现思路

- **框架搭建**

首先编写了一个抽象类 [BaseHttpHandler](src/main/java/com/sunstriker/handlers/BaseHttpHandler.java) 来实现一些基本的数据处理工作，并且在其中编写了一些模板方法，比如对正常请求和异常请求的响应，以便快速编写后面的需求。
在代码中定义了主要3层结构：
   1. Handler：可以看作类似Controller层，负责检查输入的参数、拦截异常、格式化输出的数据。
   2. Service：负责查询、以及聚合下面Domain的逻辑。测试类也是通过调用Service的方法来进行判断。
   3. Domain：数据主体类，充血模型，不止负责数据传递，其本身也有相关持久层（模拟在内存中）的操作方法。

对于请求的返回，考虑到不可以引用三方类库，我自己实现了一个简陋的Json序列化工具类，可以满足我在本项目中的需求，但是对于包含复杂对象数组的序列化没有做处理。

 由于是第一次使用原生JDK编写WebAPI，有些地方可能会有设计不当和考虑不周，恳请指正。
- **数据结构设计**

由于数据都在内存中，所以可以设计的相对灵活。主要有三类数据需要存储，`User`（用户），`Role` （角色），`UserRole`（用户与角色的多对多关系）。

其中 `User` 和 `Role` 都是使用`ConcurrentHashMap`来存储，方便快速通过Key去查询。

`UserRole` 则使用了一个HashSet来存储，这样不仅实现天然的添加时幂等，还可以最快o(1)的时间复杂度来判断某个 `User` 是否有 `Role` 的角色。

以上的数据结构都保存在单例的 `Storage` 对象中，通过双重校验锁保证安全初始化。

- **Token** **&** **加密** 

Token的设计使用了JWT的思路，Payload 部分存储用户名和过期时间，加上Sign部分签名验证。加密签名算法方面我使用的是HmacSHA256，一是Java原生支持方便，二是具有较高的安全性。

由于需求中有 `Invalidate` 接口的存在，所以要有主动废弃旧Token的能力，这就必然需要在 服务器上存储一些状态信息。我这里的实现方式是每个 `User` 分配随机生成的 Secret ，这个 Secret 在调用 `Invalidate` 
接口时就会重新生成随机字符串，从而使过去的token签名失效，引导用户重新登录获取新token。

用户密码的保存我选择使用SHA256+加盐的方式存储，一定程度上可以防止撞库攻击。

- **其他**

对于并发数据一致性的问题做了一些处理。对于`ConcurrentHashMap`的调用全部采取原子性操作，如使用`putIfAbsent`来代替`containsKey`判断。对于其他无法保证原子性的跨段操作，如删除`Role`时需要删除对应的
`UserRole`记录时，对存放`UserRole`的`HashSet`加锁，防止因没有隔离性导致的脏数据的读取。
