# CloudTest 

该仓库用于存放Spring Cloud课程实验代码，包含多个实验模块，涉及Eureka服务注册中心、Nacos服务发现、Spring Cloud Gateway网关等功能。

## 📦项目结构 

```plaintext
CloudTest
├── .gitignore
├── LICENSE
├── README.md
├── Test1
│   ├── pom.xml
│   ├── Service_Provider_11000
│   ├── Service_Consumer_16000
│   │   └── src
│   ├── Eureka_Server_18000
│   │   ├── pom.xml
│   │   └── src
│   ├── Eureka_Server_18001
│   │   ├── pom.xml
│   │   └── src
│   ├── Eureka_Server_18002
│   │   ├── pom.xml
│   │   └── src
│   ├── Service_Provider_11001
│   ├── Nacos_Provider_12000
│   │   ├── pom.xml
│   │   └── src
│   ├── Nacos_Provider_12001
│   │   ├── pom.xml
│   │   └── src
│   └── Nacos_Consumer_17000
│       ├── pom.xml
│       └── src
├── Test2
│   ├── pom.xml
│   ├── Service_Provider_11000
│   ├── Service_Consumer_16000
│   │   └── src
│   ├── Eureka_Server_18000
│   │   ├── pom.xml
│   │   └── src
│   ├── Eureka_Server_18001
│   │   ├── pom.xml
│   │   └── src
│   ├── Eureka_Server_18002
│   │   ├── pom.xml
│   │   └── src
│   ├── Service_Provider_11001
│   ├── Nacos_Provider_12000
│   │   ├── pom.xml
│   │   └── src
│   ├── Nacos_Provider_12001
│   │   ├── pom.xml
│   │   └── src
│   └── Nacos_Consumer_17000
│       ├── pom.xml
│       └── src
├── Test3
│   ├── pom.xml
│   ├── Service_Provider_11000
│   ├── Service_Consumer_16000
│   │   └── src
│   ├── Eureka_Server_18000
│   │   ├── pom.xml
│   │   └── src
│   ├── Eureka_Server_18001
│   │   ├── pom.xml
│   │   └── src
│   ├── Eureka_Server_18002
│   │   ├── pom.xml
│   │   └── src
│   ├── Service_Provider_11001
│   ├── Nacos_Provider_12000
│   │   ├── pom.xml
│   │   └── src
│   ├── Nacos_Provider_12001
│   │   ├── pom.xml
│   │   └── src
│   └── Nacos_Consumer_17000
│       ├── pom.xml
│       └── src
└── Test4
│   ├── pom.xml
│   ├── Service_Provider_11000
│   ├── Service_Consumer_16000
│   │   └── src
│   ├── Eureka_Server_18000
│   │   ├── pom.xml
│   │   └── src
│   ├── Eureka_Server_18001
│   │   ├── pom.xml
│   │   └── src
│   ├── Eureka_Server_18002
│   │   ├── pom.xml
│   │   └── src
│   ├── Service_Provider_11001
│   ├── Nacos_Provider_12000
│   │   ├── pom.xml
│   │   └── src
│   ├── Nacos_Provider_12001
│   │   ├── pom.xml
│   │   └── src
│   ├── Nacos_Consumer_17000
│   │   ├── pom.xml
│   │   └── src
│   └── Gateway_19000
│       ├── pom.xml
│       └── src
└── Test5
│   ├── pom.xml
│   ├── Service_Provider_11000
│   ├── Service_Consumer_16000
│   │   └── src
│   ├── Config_Server_20000
│   │   ├── pom.xml
│   │   └── src
│   ├── Config_Server_20001
│   │   ├── pom.xml
│   │   └── src
│   ├── Eureka_Server_18000
│   │   ├── pom.xml
│   │   └── src
│   ├── Eureka_Server_18001
│   │   ├── pom.xml
│   │   └── src
│   ├── Eureka_Server_18002
│   │   ├── pom.xml
│   │   └── src
│   ├── Service_Provider_11001
│   ├── Nacos_Provider_12000
│   │   ├── pom.xml
│   │   └── src
│   ├── Nacos_Provider_12001
│   │   ├── pom.xml
│   │   └── src
│   ├── Nacos_Consumer_17000
│   │   ├── pom.xml
│   │   └── src
│   └── Gateway_19000
│       ├── pom.xml
│       └── src
```

## 🔍模块说明
### 子模块 
#### Eureka服务注册中心 
- `Eureka_Server_18000`、`Eureka_Server_18001`、`Eureka_Server_18002`：多个Eureka服务注册中心实例，端口分别为18000、18001、18002。它们负责服务的注册与发现，为整个微服务架构提供基础支持。
    - `pom.xml`：模块的Maven配置文件，引入了`spring-cloud-starter-netflix-eureka-server`依赖。
    - `src`：源代码目录。

#### Nacos服务发现 
- `Nacos_Provider_12000`、`Nacos_Provider_12001`：Nacos服务提供者模块。它们将自己的服务信息注册到Nacos服务注册中心，供消费者调用。
    - `pom.xml`：引入了`spring-boot-starter-web`和`spring-cloud-starter-alibaba-nacos-discovery`依赖。
    - `src`：源代码目录。
- `Nacos_Consumer_17000`：Nacos服务消费者模块，从Nacos服务注册中心获取服务提供者的信息，并调用其服务。
    - `pom.xml`：引入了相关依赖。
    - `src`：源代码目录。

#### 服务提供者和消费者 
- `Service_Provider_11000`、`Service_Provider_11001`：服务提供者模块，提供具体的业务服务。
    - `src`：源代码目录。
- `Service_Consumer_16000`：服务消费者模块，配置了带有负载均衡的`RestTemplate`，实现对服务提供者的调用，并通过负载均衡策略选择合适的服务实例。
    - `src`：源代码目录。

#### Spring Cloud Gateway网关 
- `Gateway_19000`：Spring Cloud Gateway网关模块，作为整个微服务架构的入口，负责路由转发、请求过滤等功能，保护后端服务的安全。
    - `pom.xml`：引入了`spring-cloud-starter-gateway`、`spring-cloud-starter-netflix-eureka-client`等依赖。
    - `src`：源代码目录。

## 📋 依赖管理 
父项目的`pom.xml`中使用`dependencyManagement`标签统一管理依赖版本，子模块继承后只需引入依赖的`artifactId`即可。主要依赖版本如下：
- `spring-cloud-dependencies`：2024.0.0
- `spring-cloud-alibaba-dependencies`：2022.0.0.0
- `lombok`：1.18.36
- `spring-boot-dependencies`：3.4.3

这样可以确保各个模块使用的依赖版本一致，避免版本冲突问题。

## 🚀 运行步骤 
1. 确保本地已经安装了Java 21和Maven。
2. 克隆本仓库到本地：
```bash
git clone https://github.com/Echo-Nie/CloudTest.git
```
3. 进入项目根目录，执行Maven命令进行项目构建：
```bash
mvn clean install
```
4. 依次启动各个模块的服务，例如启动Eureka服务注册中心：
```bash
java -jar Test1/Eureka_Server_18000/target/Eureka_Server_18000-1.0-SNAPSHOT.jar
```
5. 访问各个服务的端点，验证服务是否正常运行。

## ⚠️ 注意事项 
- 请确保Eureka服务注册中心和Nacos服务发现组件已经正确启动，否则服务无法正常注册和发现。
- 部分配置文件中的`hostname`和`service-url`可能需要根据实际情况进行调整，以适应不同的运行环境。
