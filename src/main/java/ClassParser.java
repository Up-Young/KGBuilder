import com.github.javaparser.JavaParser;
import com.github.javaparser.ParseResult;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.symbolsolver.JavaSymbolSolver;
import com.github.javaparser.symbolsolver.model.resolution.TypeSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.CombinedTypeSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.JavaParserTypeSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.ReflectionTypeSolver;
import model.TempClass;
import util.GetJavaFiles;
import visitor.ClassVisitor;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

import static util.Tools.*;

public class ClassParser {
    private static List<TempClass> classModelSet = new ArrayList<>();
    public static ClassVisitor classVisitor = new ClassVisitor();

    public static void main(String[] args) {
        parseClass();
    }

    public static void parseClass() {
//        使用默认的java8来解析
        JavaParser javaParser = new JavaParser();
        TypeSolver reflectionTypeSolver = new ReflectionTypeSolver(false);
        TypeSolver javaParserTypeSolver_1 = new JavaParserTypeSolver(new File(ImportPath));
        reflectionTypeSolver.setParent(javaParserTypeSolver_1);
        CombinedTypeSolver combinedSolver = new CombinedTypeSolver();
        combinedSolver.add(reflectionTypeSolver);
        JavaSymbolSolver symbolSolver = new JavaSymbolSolver(combinedSolver);
        javaParser.getParserConfiguration().setSymbolResolver(symbolSolver);
        //使用CombinedTypeSolver替换ReflectionTypeSolver后可以解析出更多来自param和throws的type。
        //设置文件根目录
        File projectDir = new File(ImportPath);
        //获取java文件list
        List<String> pathList = GetJavaFiles.listClasses(projectDir);
        for (String path : pathList) {
            ExecutorService executor = Executors.newSingleThreadExecutor();
            FutureTask<Boolean> future =
                    new FutureTask<Boolean>(new Callable<Boolean>() {//使用Callable接口作为构造参数
                        @Override
                        public Boolean call() {
                            try {
                                ParseResult<CompilationUnit> result = javaParser.parse(new File(path));
                                CompilationUnit cu = result.getResult().get();
                                // 从解析结果中获取类/接口类
                                List<ClassOrInterfaceDeclaration> classOrInterfaceDeclarationList = cu.findAll(ClassOrInterfaceDeclaration.class);
                                for (ClassOrInterfaceDeclaration classOrInterfaceDeclaration : classOrInterfaceDeclarationList) {
                                    TempClass tempClass = ClassVisitor.parseClass(classOrInterfaceDeclaration);
                                    classModelSet.add(tempClass);
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            return true;
                        }
                    });
            executor.execute(future);
            try {
                Boolean result = future.get(15000, TimeUnit.MILLISECONDS); //取得结果，同时设置超时执行时间为15秒。同样可以用future.get()，不设置执行超时时间取得结果
            } catch (InterruptedException | ExecutionException | TimeoutException e) {
                future.cancel(true);
            } finally {
                executor.shutdown();
            }
        }
        writeToJson(OutputPath, classModelSet, "ClassAll.json");
    }
}
