# KGBuilder
## 项目结构
结构说明
```
project_root                           整个代码项目的根目录
│   README.md                          对于整个项目的介绍
│   .gitignore                         对于某些文件和目录让Git忽略管理
│   requirements.txt                   声明整个项目依赖的Python库
│   definitions.py                     定义一个ROOT_DIR的常量作为项目根目录。
│ 
│   main.py                            建图功能的主入口
│                      
└───data                               存放java源码解析后的json文件
│
└───output                             存放构建完成的.graph文件和.dc文件
│
└───component                          建图程序的核心代码目录
│   │                   
│   └───api_importer_component.py      建图程序的核心组件，功能是从JSON文件中将API信息导入到图中。
│   └───ConstantCodeEntity.py          定义了一些API类型、API关系的常量
│   └───html_extracter.py              工具类，用于处理API文档中的html标签
│   └───path_util.py                   工具类，路径工具类，里面的路径都是相对与项目根目录的路径
│   └───spacy_fixer.py                 spacy相关的工具类
│   └───spacy_model.py                 spacy相关的工具类
│   └───sentenceHandler.py             spacy相关的工具类
│

```

## 建图程序运行

1. 安装依赖
```
  >>> pip install -r requirements.txt
```

2. 安装spacy语言模型，由于网络问题，spacy官方的模型经常下载不下来，故直接将模型文件上传到项目中
```
  >>> pip install data/en_core_web_sm-3.0.0.tar.gz
```

3. 将解析好的java源码数据放入data目录下后运行 
```
  >>> python main.py
```