# APISrcParser

## 1. 项目概述

APISrcParser 是一个基于 Java 的命令行工具，用于解析 Java 源代码。它利用 `javaparser` 库来分析 Java 文件，提取关于类、方法、字段及其之间关系的详细信息。提取的数据随后被序列化为结构化的 JSON 文件，使其适用于进一步的分析、文档生成或与其他工具集成。

该工具可以处理单个和多个 Java 项目，为处理不同规模和结构的代码库提供了一种灵活的方式。

## 2. 功能特性

- **源代码解析:** 解析 `.java` 文件以提取抽象语法树 (AST) 信息。
- **API 信息提取:** 识别并提取以下数据：
    - 包
    - 类和接口
    - 方法
    - 字段
- **关系分析:** 确定不同代码元素之间的关系，例如：
    - 类/接口与包的关系
    - 字段与类的关系
- **JSON 输出:** 将所有提取的信息导出为一组结构良好的 JSON 文件。
- **灵活的项目处理:** 支持解析单个项目或包含多个项目的目录。
- **命令行界面:** 通过简单的命令行界面易于使用。

## 3. 环境要求

要构建和运行 APISrcParser，您将需要以下环境：

- **Java 开发工具包 (JDK):** 1.8 或更高版本。
- **Apache Maven:** 用于管理依赖项和构建项目。

## 4. 如何构建

您可以从源代码构建项目以生成可运行的 JAR 文件。该文件将包含所有必需的依赖项。

1.  克隆仓库或下载源代码。
2.  在您的终端中，导航到项目的根目录。
3.  运行以下 Maven 命令：

    ```bash
    mvn package
    ```

4.  成功完成后，可运行的 JAR 文件 `APISrcParser-1.0-SNAPSHOT.jar` 将在 `target` 目录中创建。

## 5. 如何运行

您可以使用 `java -jar` 从命令行运行该工具。

### 命令行选项

| 选项             | 简称 | 描述                                                                                             |
| ------------------ | ----- | ------------------------------------------------------------------------------------------------------- |
| `--input-dir`      | `-i`  | **必需。** 指定包含要解析的 Java 源代码的输入目录。                 |
| `--output-dir`     | `-o`  | 指定将保存输出 JSON 文件的目录。默认为 `./output`。                |
| `--multi-project`  | `-mp` | 将输入目录中的每个子目录视为一个独立的项目，并为每个项目生成单独的输出。 |
| `--version`        | `-v`  | 打印工具的版本号。                                                                         |
| `--help`           | `-h`  | 打印包含所有命令行选项的帮助消息。                                                  |

### 示例

#### 解析单个项目

要解析位于 `srcCode/applet` 的单个项目，并将结果保存到默认的 `output` 目录：

```bash
java -jar target/APISrcParser-1.0-SNAPSHOT.jar -i srcCode/applet
```

要指定自定义输出目录，例如 `my-output`：

```bash
java -jar target/APISrcParser-1.0-SNAPSHOT.jar -i srcCode/applet -o my-output
```

#### 解析多个项目

如果您有一个目录 (`srcCode`) 包含多个项目子目录 (`applet`, `another-project`)，您可以使用 `-mp` 标志一次性解析所有项目。

```bash
java -jar target/APISrcParser-1.0-SNAPSHOT.jar -i srcCode -mp
```

这将在输出目录中为每个项目创建一个对应的子目录 (`output/applet`, `output/another-project`)。

## 6. 输入结构

-   **单个项目:** 输入目录应包含 Java 源文件 (`.java`)。该工具将递归地查找此目录中的所有 Java 文件。
-   **多个项目:** 输入目录应包含一个或多个子目录。每个子目录都被视为一个独立的项目。

## 7. 输出结构

该工具在指定的输出目录中生成一系列 JSON 文件。如果解析多个项目，则会为每个项目创建一个子目录。

输出文件包括：

-   `Packages.json`: 所有包的信息。
-   `ClassOrInterfaces.json`: 每个类和接口的详细信息。
-   `MethodAll.json`: 所有方法的详细信息。
-   `FieldsInClass.json`: 类中字段的详细信息。
-   `ClassOrInterfaceAndPackageRelations.json`: 描述每个类/接口所属的包。
-   `FieldsAndClassRelations.json`: 描述每个字段所属的类。

这种结构化的输出允许其他程序轻松使用以进行进一步的分析或可视化。
