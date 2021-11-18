import com.github.javaparser.JavaParser;
import com.github.javaparser.ParseResult;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ConstructorDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.symbolsolver.JavaSymbolSolver;
import com.github.javaparser.symbolsolver.model.resolution.TypeSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.CombinedTypeSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.JavaParserTypeSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.ReflectionTypeSolver;
import model.TempMethod;
import util.GetJavaFiles;
import visitor.ConstructorVisitor;
import visitor.MethodVisitor;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

import static util.Tools.*;


public class MethodParser {
    private static List<TempMethod> methodModelSet = new ArrayList<>();
    public static MethodVisitor methodVisitor = new MethodVisitor();
    public static ConstructorVisitor constructorVisitor = new ConstructorVisitor();

    public static void main(String[] args) {
        parseMethod();
    }

    public static void parseMethod(){
        JavaParser javaParser = new JavaParser();
        TypeSolver reflectionTypeSolver = new ReflectionTypeSolver(false);
        TypeSolver javaParserTypeSolver_1 = new JavaParserTypeSolver(new File(ImportPath));
        reflectionTypeSolver.setParent(javaParserTypeSolver_1);
        CombinedTypeSolver combinedSolver = new CombinedTypeSolver();
        combinedSolver.add(reflectionTypeSolver);
        JavaSymbolSolver symbolSolver = new JavaSymbolSolver(combinedSolver);
        javaParser.getParserConfiguration().setSymbolResolver(symbolSolver);
        //使用CombinedTypeSolver替换ReflectionTypeSolver后可以解析出更多来自param和throws的type。
        File projectDir = new File(ImportPath);
        List<String> pathList = GetJavaFiles.listClasses(projectDir);
        for (String path : pathList) {
            ExecutorService executor = Executors.newSingleThreadExecutor();
            FutureTask<Boolean> future =
                    new FutureTask<Boolean>(new Callable<Boolean>() {//使用Callable接口作为构造参数
                        @Override
                        public Boolean call() {
                            //在对文件进行遍历的层次进行计时 如果这个文件跑了超过15s还没跑完 说明可能进入死循环了
                            try {
                                ParseResult<CompilationUnit> result = javaParser.parse(new File(path));
                                CompilationUnit cu = result.getResult().get();
                                List<MethodDeclaration> methodDeclarationList = cu.findAll(MethodDeclaration.class);
                                for (MethodDeclaration methodDeclaration : methodDeclarationList) {
                                    TempMethod tempMethod = methodVisitor.parseMethod(methodDeclaration);
                                    methodModelSet.add(tempMethod);
                                }
                                List<ConstructorDeclaration> constructorDeclarationList = cu.findAll(ConstructorDeclaration.class);
                                for (ConstructorDeclaration constructorDeclaration : constructorDeclarationList) {
                                    TempMethod tempMethod = constructorVisitor.parseConstructor(constructorDeclaration);
                                    methodModelSet.add(tempMethod);
                                }
                                return true;
                            } catch (Exception e) {
                                e.printStackTrace();
                                return false;
                            }
                        }
                    });
            executor.execute(future);
            try {
                Boolean result = future.get(15000, TimeUnit.MILLISECONDS); //取得结果，同时设置超时执行时间为15秒。
            } catch (InterruptedException | ExecutionException | TimeoutException e) {
                future.cancel(true);
            } finally {
                executor.shutdown();
            }
        }
        writeToJson(OutputPath, methodModelSet, "MethodAll.json");
    }
}
