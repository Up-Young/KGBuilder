package visitor;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ParseResult;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.EnumDeclaration;
import com.github.javaparser.ast.type.ClassOrInterfaceType;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import com.github.javaparser.javadoc.Javadoc;
import com.github.javaparser.symbolsolver.JavaSymbolSolver;
import com.github.javaparser.symbolsolver.model.resolution.TypeSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.CombinedTypeSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.JavaParserTypeSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.ReflectionTypeSolver;
import model.TempClass;

import java.io.File;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.Queue;

import static util.Tools.ImportPath;

public class ClassVisitor extends VoidVisitorAdapter<Object> {
    public static TempClass parseClass(ClassOrInterfaceDeclaration c) {
        try {
            TempClass tempClass = new TempClass();
            String description = getDescription(c);
            tempClass.setDescription(description);
            Queue<String> inheritInfo = getInheritInfo(c);
            tempClass.setInherit(inheritInfo);
            boolean type = getType(c);
            tempClass.setType(type);
            String name = getName(c);
            tempClass.setName(name);
//            System.out.println("===================");
//            System.out.println(name);
//            System.out.println(type);
//            System.out.println(inheritInfo);
//            System.out.println(description);
//            System.out.println("===================");
            return tempClass;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    public static TempClass parseClass(EnumDeclaration c) {
        try {
            TempClass tempClass = new TempClass();
            String description = getDescription(c);
            tempClass.setDescription(description);
            Queue<String> inheritInfo = new LinkedList<>();
            tempClass.setInherit(inheritInfo);
            boolean type = false;
            tempClass.setType(type);
            String name = getName(c);
            tempClass.setName(name);
//            System.out.println("===================");
//            System.out.println(name);
//            System.out.println(type);
//            System.out.println(inheritInfo);
//            System.out.println(description);
//            System.out.println("===================");
            return tempClass;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String getDescription(ClassOrInterfaceDeclaration c) {
        String description = "";
        Optional<Javadoc> javadocOptional = c.getJavadoc();
        if (javadocOptional.isPresent()) {
            Javadoc javadoc = javadocOptional.get();
            description = javadoc.getDescription().toText();
        }
        return description;
    }
    public static String getDescription(EnumDeclaration c) {
        String description = "";
        Optional<Javadoc> javadocOptional = c.getJavadoc();
        if (javadocOptional.isPresent()) {
            Javadoc javadoc = javadocOptional.get();
            description = javadoc.getDescription().toText();
        }
        return description;
    }

    public static Queue<String> getInheritInfo(ClassOrInterfaceDeclaration c) {
        Queue<String> inherit = new LinkedList<>();
        List<ClassOrInterfaceType> extendedTypeList = c.getExtendedTypes();
        List<ClassOrInterfaceType> implementedTypeList = c.getImplementedTypes();

        for (ClassOrInterfaceType extendedType : extendedTypeList) {
            try {
                String extendName = extendedType.resolve().getQualifiedName();
                inherit.add(extendName);
            } catch (Throwable e) {
                String extendName = extendedType.getNameAsString();
                inherit.add(extendName);
            }
        }

        for (ClassOrInterfaceType implementedType : implementedTypeList) {
            try {
                String implementedName = implementedType.resolve().getQualifiedName();
                inherit.add(implementedName);
            } catch (Exception e) {
                String implementedName = implementedType.getNameAsString();
                inherit.add(implementedName);
            }
        }
        return inherit;
    }

    public static boolean getType(ClassOrInterfaceDeclaration c) {
        return c.isInterface();
    }

    public static String getName(ClassOrInterfaceDeclaration c) {
        String classOrInterfaceName = "";
        Optional<String> classOrInterfaceNameOptional = c.getFullyQualifiedName();
        if (classOrInterfaceNameOptional.isPresent()) classOrInterfaceName = c.getFullyQualifiedName().get();
        return classOrInterfaceName;
    }
    public static String getName(EnumDeclaration c) {
        String classOrInterfaceName = "";
        Optional<String> classOrInterfaceNameOptional = c.getFullyQualifiedName();
        if (classOrInterfaceNameOptional.isPresent()) classOrInterfaceName = c.getFullyQualifiedName().get();
        return classOrInterfaceName;
    }

    public static void main(String[] args) {
        String FilePath = "D:\\Program\\APIComments\\javasrc\\src\\java\\awt\\Button.java";
        try {
            JavaParser javaParser = new JavaParser();
            TypeSolver reflectionTypeSolver = new ReflectionTypeSolver(false);
            TypeSolver javaParserTypeSolver_1 = new JavaParserTypeSolver(new File(ImportPath));
            reflectionTypeSolver.setParent(javaParserTypeSolver_1);
            CombinedTypeSolver combinedSolver = new CombinedTypeSolver();
            combinedSolver.add(reflectionTypeSolver);
            JavaSymbolSolver symbolSolver = new JavaSymbolSolver(combinedSolver);
            javaParser.getParserConfiguration().setSymbolResolver(symbolSolver);
            //使用CombinedTypeSolver替换ReflectionTypeSolver后可以解析出更多来自param和throws的type。
            ParseResult<CompilationUnit> result = javaParser.parse(new File(FilePath));
            CompilationUnit cu = result.getResult().get();
            //new ClassVisitor().test(cu);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

