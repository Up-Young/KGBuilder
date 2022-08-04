
import org.apache.commons.cli.BasicParser;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.Options;
import util.Tools;

import java.io.File;
import java.util.Arrays;
import java.util.Objects;

public class Main {
    public static void main(String[] args) throws Exception {
        Options options = new Options();
        options.addOption("h", "help", false, "Print help");
        options.addOption("v", "version", false, "Print version");
        options.addOption("mp", "multi-project", false, "Parse as multiple projects");
        options.addOption("o", "output-dir", true, "Set output directory");
        options.addOption("i", "input-dir", true, "Set input directory");
        options.addOption("l", "log", false, "Print log");
        options.addOption("ld", "log-dir", false, "Set log directory");
        options.addOption("t", "terminal", false, "Print some INFO in terminal");
        options.addOption("pn", "process-num", true, "Set the num of multiprocess");

        CommandLineParser parser = new BasicParser();
        CommandLine commandLine = parser.parse(options, args);

        boolean multiProject = false;
        boolean log = false;
        int processNum = 1;

        if (commandLine.hasOption("v")) {
            printVersion();
        }
        if (commandLine.hasOption("h")) {
            printHelp();
        }
        if (commandLine.hasOption("mp")) {
            multiProject = true;
        }
        if (commandLine.hasOption("l")) {
            log = true;
        }
        if (commandLine.hasOption("i")) {
            Tools.ImportPath = commandLine.getOptionValue("i");
        }
        if (commandLine.hasOption("o")) {
            Tools.OutputPath = commandLine.getOptionValue("o");
        }
        if (commandLine.hasOption("pn")) {
            processNum = Integer.parseInt(commandLine.getOptionValue("pn"));
        }

        if (multiProject) {
            parseMultipleProjects();
        } else {
            parseSingleProject();
        }

    }

    private static void printHelp() {
        System.out.println("命令行参数说明：\n" +
                "--version (-v): 输出版本号\n" +
                "--help (-h): 输出帮助\n" +
                "--multi-project (-mp): 是否为多个项目（是的话会对各子文件夹分开输出结果，默认不是）\n" +
                "--output-dir xxx (-o): 设置输出目录为xxx（默认值output）\n" +
                "--input-dir xxx (-i): 设置输入目录为xxx（默认值input）\n" +
                "--log (-l): 是否输出日志文件\n" +
                "--log-dir (-ld): 设置日志文件目录（默认值log）\n" +
                "--terminal (-t): 是否在控制台中输出中间信息（默认不输出）\n" +
                "--process-num xxx (-pn): 设置进程数为xxx（默认单进程）");
    }

    private static void printVersion() {
        System.out.println("ver1.0");
    }

    private static void parseSingleProject() {
        ClassParser.parseClass();
        MethodParser.parseMethod();
        RelationParser.parseRelation();
    }

    private static void parseMultipleProjects() {
        String in = Tools.ImportPath;
        String out = Tools.OutputPath;
        File file = new File(in);
        for (File child : Objects.requireNonNull(file.listFiles())) {
            Tools.ImportPath = in + "/" + child.getName();
            Tools.OutputPath = out + "/" + child.getName();
            System.out.println(child);
            File outDir = new File(Tools.OutputPath);
            if (!outDir.exists()) {
                outDir.mkdir();
            }
            parseSingleProject();
        }
    }
}
