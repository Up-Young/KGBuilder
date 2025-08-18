# APISrcParser

## 1. Project Overview

APISrcParser is a Java-based command-line tool designed to parse Java source code. It utilizes the `javaparser` library to analyze Java files, extracting detailed information about classes, methods, fields, and the relationships between them. The extracted data is then serialized into structured JSON files, making it suitable for further analysis, documentation generation, or integration with other tools.

The tool can handle both single and multiple Java projects, providing a flexible way to process codebases of varying sizes and structures.

## 2. Features

- **Source Code Parsing:** Parses `.java` files to extract Abstract Syntax Tree (AST) information.
- **API Information Extraction:** Identifies and extracts data for:
    - Packages
    - Classes and Interfaces
    - Methods
    - Fields
- **Relationship Analysis:** Determines relationships between different code elements, such as:
    - Class/interface and package relationships
    - Field and class relationships
- **JSON Output:** Exports all extracted information into a set of well-structured JSON files.
- **Flexible Project Handling:** Supports parsing a single project or a directory containing multiple projects.
- **Command-Line Interface:** Easy to use via a simple command-line interface.

## 3. Requirements

To build and run APISrcParser, you will need the following:

- **Java Development Kit (JDK):** Version 1.8 or higher.
- **Apache Maven:** To manage dependencies and build the project.

## 4. How to Build

You can build the project from the source to generate a runnable JAR file. This file will contain all the necessary dependencies.

1.  Clone the repository or download the source code.
2.  Navigate to the root directory of the project in your terminal.
3.  Run the following Maven command:

    ```bash
    mvn package
    ```

4.  Upon successful completion, the runnable JAR file, `APISrcParser-1.0-SNAPSHOT.jar`, will be created in the `target` directory.

## 5. How to Run

You can run the tool from the command line using `java -jar`.

### Command-Line Options

| Option             | Short | Description                                                                                             |
| ------------------ | ----- | ------------------------------------------------------------------------------------------------------- |
| `--input-dir`      | `-i`  | **Required.** Specifies the input directory containing the Java source code to be parsed.                 |
| `--output-dir`     | `-o`  | Specifies the directory where the output JSON files will be saved. Defaults to `./output`.                |
| `--multi-project`  | `-mp` | Treats each subdirectory within the input directory as a separate project, generating separate outputs. |
| `--version`        | `-v`  | Prints the version of the tool.                                                                         |
| `--help`           | `-h`  | Prints the help message with all command-line options.                                                  |

### Examples

#### Parsing a Single Project

To parse a single project located in `srcCode/applet`, and save the results to the default `output` directory:

```bash
java -jar target/APISrcParser-1.0-SNAPSHOT.jar -i srcCode/applet
```

To specify a custom output directory, for example `my-output`:

```bash
java -jar target/APISrcParser-1.0-SNAPSHOT.jar -i srcCode/applet -o my-output
```

#### Parsing Multiple Projects

If you have a directory (`srcCode`) containing multiple project subdirectories (`applet`, `another-project`), you can parse them all at once using the `-mp` flag.

```bash
java -jar target/APISrcParser-1.0-SNAPSHOT.jar -i srcCode -mp
```

This will create a corresponding subdirectory for each project within the output directory (`output/applet`, `output/another-project`).

## 6. Input Structure

-   **Single Project:** The input directory should contain the Java source files (`.java`). The tool will recursively find all Java files within this directory.
-   **Multiple Projects:** The input directory should contain one or more subdirectories. Each subdirectory is treated as an independent project.

## 7. Output Structure

The tool generates a series of JSON files in the specified output directory. If parsing multiple projects, a subdirectory is created for each project.

The output files include:

-   `Packages.json`: Information about all packages.
-   `ClassOrInterfaces.json`: Details about each class and interface.
-   `MethodAll.json`: Details about all methods.
-   `FieldsInClass.json`: Details about fields within classes.
-   `ClassOrInterfaceAndPackageRelations.json`: Describes which package each class/interface belongs to.
-   `FieldsAndClassRelations.json`: Describes which class each field belongs to.

This structured output allows for easy consumption by other programs for further analysis or visualization.
