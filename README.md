# APISrcParser
## 项目结构
```
project_root                           整个代码项目的根目录
│   README.md                          对于整个项目的介绍
│   .gitignore                         对于某些文件和目录让Git忽略管理
│   pom.xml                            maven项目的管理文件
│  
│                      
└───srcCode                            存放需要被解析的java源码
│
└───parseResult                        存放解析出来后的json数据文件
│
└───src                                解析程序的核心代码目录
│   │                   
│   └───main
│       └───java
│           └───model                  解析java代码时定义的基础数据结构，如classModel、EntityModel等
│           └───util                   解析java代码时用到的工具类
│           └───visitor                解析java代码的核心组件，传入对应的javaparser解析结果cu，解析出对应的class、method信息
│           └───ClassParser            解析java代码中的class信息的入口
│           └───MethodParser           解析java代码中的method信息的入口
│           └───RelationParser         解析java代码中的class和package以及class和field等entity之间的关系的入口
```

## 解析程序运行
使用maven pom.xml配置完项目依赖后运行ClassParser、MethodParser、RelationParser三个文件即可。没有顺序要求。运行后解析出来的数据会存放在parseResult目录下