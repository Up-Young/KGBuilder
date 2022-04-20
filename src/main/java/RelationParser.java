import com.github.javaparser.JavaParser;
import com.github.javaparser.ParseResult;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.symbolsolver.JavaSymbolSolver;
import com.github.javaparser.symbolsolver.model.resolution.TypeSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.CombinedTypeSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.JavaParserTypeSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.ReflectionTypeSolver;
import util.GetJavaFiles;
import visitor.ClassAndPackageVisitor;

import java.io.File;
import java.util.List;
import java.util.concurrent.*;

import static util.Tools.*;

public class RelationParser {
    public static void main(String[] args) {
        parseRelation();
    }

    public static void parseRelation(){
        ClassAndPackageVisitor visitor = new ClassAndPackageVisitor();
        JavaParser javaParser = new JavaParser();
        TypeSolver reflectionTypeSolver = new ReflectionTypeSolver(false);
        TypeSolver javaParserTypeSolver_1 = new JavaParserTypeSolver(new File(ImportPath));
        reflectionTypeSolver.setParent(javaParserTypeSolver_1);
        CombinedTypeSolver combinedSolver = new CombinedTypeSolver();
        combinedSolver.add(reflectionTypeSolver);
        JavaSymbolSolver symbolSolver = new JavaSymbolSolver(combinedSolver);
        javaParser.getParserConfiguration().setSymbolResolver(symbolSolver);
        File projectDir = new File(ImportPath);
        List<String> pathList = GetJavaFiles.listClasses(projectDir);
        for (String path : pathList) {
            ExecutorService executor = Executors.newSingleThreadExecutor();
            FutureTask<Boolean> future =
                    new FutureTask<Boolean>(new Callable<Boolean>() {//使用Callable接口作为构造参数
                        @Override
                        public Boolean call() {
                            try {
                                ParseResult<CompilationUnit> parseResult = javaParser.parse(new File(path));
                                if (parseResult.getResult().isPresent()) {
                                    CompilationUnit cu = parseResult.getResult().get();
                                    String packageName = visitor.parsePackage(cu);
                                    visitor.parseClassInterface(cu, packageName);
                                    System.out.println("\r\n");
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
//
                executor.shutdown();
            }
        }

        //写入json文件
        System.out.println("-------start write--------");
        writeToJson(OutputPath, visitor.relationModelList, "ClassOrInterfaceAndPackageRelations.json");
        visitor.relationModelList.clear();
        writeToJson(OutputPath, visitor.entityModelSet, "Packages.json");
        visitor.entityModelSet.clear();
        writeToJson(OutputPath, visitor.classModelSet, "ClassOrInterfaces.json");
        visitor.classModelSet.clear();
        writeToJson(OutputPath, visitor.fieldModelArrayList, "FieldsInClass.json");
        visitor.fieldModelArrayList.clear();
        writeToJson(OutputPath, visitor.fieldRelationModelList, "FieldsAndClassRelations.json");
        visitor.fieldRelationModelList.clear();
        visitor.cleanAll();
        System.out.println("------finish-----------");
    }
}
