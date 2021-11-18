package visitor;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ParseResult;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Modifier;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.Parameter;
import com.github.javaparser.ast.stmt.IfStmt;
import com.github.javaparser.ast.stmt.ReturnStmt;
import com.github.javaparser.ast.stmt.ThrowStmt;
import com.github.javaparser.ast.type.ClassOrInterfaceType;
import com.github.javaparser.ast.type.ReferenceType;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import com.github.javaparser.javadoc.Javadoc;
import com.github.javaparser.javadoc.JavadocBlockTag;
import com.github.javaparser.symbolsolver.JavaSymbolSolver;
import com.github.javaparser.symbolsolver.model.resolution.TypeSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.CombinedTypeSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.JavaParserTypeSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.ReflectionTypeSolver;
import com.google.common.base.Joiner;
import javafx.util.Pair;
import model.TempMethod;

import java.io.File;
import java.util.*;

import static util.Tools.ImportPath;

public class MethodVisitor extends VoidVisitorAdapter<Object> {
    public TempMethod parseMethod(MethodDeclaration n) {
        try {
            TempMethod tempMethod = new TempMethod();
            tempMethod.setModifierList(getAccessModifier(n));
            String belongClassName = getBelongClass(n);
            tempMethod.setBelongClass(belongClassName);
            String fullDeclaration = getFullDeclaration(n);
            String description = getDescription(n);
            if (description == null) {
                description = "";
            }
            tempMethod.setDescription(description);
            String methodName = getQualifiedName(n);
            tempMethod.setMethodName(methodName);
            String shortName = getShortName(n);
            tempMethod.setName(shortName);
            tempMethod.setParamsTag(getParamTag(n));
            tempMethod.setReturnValueDescription(getReturnValueDescription(n));
            tempMethod.setThrowsTag(getThrowTag(n));
            tempMethod.setParameter(getParameter(n));
            tempMethod.setParameterTypeList(getParameterType(n));
            tempMethod.setThrowException(getException(n));
            String returnType = getType(n);
            tempMethod.setReturnValueType(returnType);
            tempMethod.setThrowsCodeDirective(getThrowsCodeDirective(n));
            tempMethod.setReturnCodeDirective(getReturnCodeDirective(n));
            System.out.println("=========================");
            System.out.println("Method name: " + methodName);
            System.out.println("Short name: " + shortName);
            System.out.println("Full declaration: " + fullDeclaration);
            System.out.println("Belong class name: " + belongClassName);
            System.out.println("Description: " + description);
            System.out.println("Return Type: " + returnType);
            System.out.println("=========================");
            return tempMethod;

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String getDescription(MethodDeclaration m) {
        String description = "";
        Optional<Javadoc> javadocOptional = m.getJavadoc();
        if (javadocOptional.isPresent()) {
            Javadoc javadoc = javadocOptional.get();
            description = javadoc.getDescription().toText();
            return description;
        }
        return description;
    }

    public static List<String> getAccessModifier(MethodDeclaration m) {
        NodeList<Modifier> nodeList = m.getModifiers();
        List<String> list = new ArrayList<String>();
        for (Modifier node : nodeList) {
            list.add(node.toString().trim());
        }
        return list;
    }

    public static String getMethodName(String belongClassName, String full_declaration) {
        String result = "";
        if (belongClassName.equals("")) {
            return "";
        }
        int end = full_declaration.indexOf(")");
        int start = full_declaration.indexOf("(");
        String parameter = full_declaration.substring(start + 1, end + 1);
        String left = full_declaration.replace(parameter, "").replace("(", "");
        String shortName = left.split(" ")[left.split(" ").length - 1];
        String[] paramList = parameter.split(",");
        for (int i = 0; i < paramList.length; i++) {
            if (paramList[i].trim().split(" ").length > 2) {
                paramList[i] = paramList[i].trim().split(" ")[1];
            } else {
                paramList[i] = paramList[i].trim().split(" ")[0];
            }

        }
        parameter = Joiner.on(", ").join(paramList);
        result = belongClassName + "." + shortName + "(" + parameter + ")";
        return result;
    }

    private static String getShortName(MethodDeclaration m) {
        String name = "";
        name = m.getName().asString();
        String full_declaration = m.getDeclarationAsString();
        int end = full_declaration.indexOf(")");
        int start = full_declaration.indexOf("(");
        String right = full_declaration.substring(start, end + 1);
        name += right;
        return name;
    }

    private static String getQualifiedName(MethodDeclaration m) {
        //在某些情况下resolve会失败，暂不清楚什么情况下会触发bug。补救的getMethodName函数还有优化空间。
        String methodName = "";
        try {
            methodName = m.resolve().getQualifiedSignature();
            return methodName;
        } catch (Exception e) {
            ClassOrInterfaceDeclaration parentClass = (ClassOrInterfaceDeclaration) getAncestorNodeClassOrInterFaceDeclaration(m, 0);
            //String belongClassName = parentClass.resolve().getQualifiedName();
            String belongClassName = parentClass.getFullyQualifiedName().get();
            String full_declaration = m.getDeclarationAsString();
            methodName = getMethodName(belongClassName, full_declaration);
            return methodName;
        }
    }

    private static String getType(MethodDeclaration m) {
        //问题很大 看看有没有更好的方法获取Type
        String typeReturn = "";
        try {
            if ("T".equals(m.getType().asString())) {
                typeReturn = "T";
            } else {
                if (m.getType().resolve().isPrimitive()) {
                    typeReturn = m.getType().resolve().asPrimitive().getBoxTypeQName();
                } else if (m.getType().resolve().isArray()) {
                    typeReturn = m.getType().resolve().describe();
                } else if (m.getType().isVoidType()) {
                    typeReturn = "void";
                } else {
                    typeReturn = m.getType().resolve().asReferenceType().getQualifiedName();
                }
            }
            return typeReturn;
        } catch (Exception e) {
            typeReturn = m.getType().asString();
            return typeReturn;
        }
    }


    private static String getFullDeclaration(MethodDeclaration m) {
        String full_declaration = "";
        full_declaration = m.getDeclarationAsString();
        return full_declaration;
    }

    private static String getBelongClass(MethodDeclaration m) {
        String belongClassName = "";
        try {
            ClassOrInterfaceDeclaration parentClass = (ClassOrInterfaceDeclaration) getAncestorNodeClassOrInterFaceDeclaration(m, 0);
            if (parentClass != null) {
                belongClassName = parentClass.getFullyQualifiedName().get();
                System.out.println("className: " + belongClassName);
            }
            if (belongClassName != null) {
                return belongClassName;
            } else {
                return "";
            }
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    private static String getReturnValueDescription(MethodDeclaration m) {
        String ReturnValueDescription = "";
        Optional<Javadoc> javadocOptional = m.getJavadoc();
        if (javadocOptional.isPresent()) {
            Javadoc javadoc = javadocOptional.get();
            List<JavadocBlockTag> javadocBlockTags = javadoc.getBlockTags();
            for (JavadocBlockTag javadocBlockTag : javadocBlockTags) {
                String tagName = javadocBlockTag.getTagName();
                if (tagName.equals("return")) {
                    ReturnValueDescription = javadocBlockTag.getContent().toText();
//                            ReturnValueDescription = re;
//                            System.out.println("TagName: " + javadocBlockTag.getTagName());
//                            System.out.println("Content: " + javadocBlockTag.getContent().toText());
                }
            }
        }
        return ReturnValueDescription;
    }

    private static List<Pair<String, String>> getParamTag(MethodDeclaration m) {
        List<Pair<String, String>> paramsTag = new ArrayList<>();
        Optional<Javadoc> javadocOptional = m.getJavadoc();
        if (javadocOptional.isPresent()) {
            Javadoc javadoc = javadocOptional.get();
            List<JavadocBlockTag> javadocBlockTags = javadoc.getBlockTags();
            for (JavadocBlockTag javadocBlockTag : javadocBlockTags) {
                String tagName = javadocBlockTag.getTagName();
                if (tagName.equals("param")) {
                    Pair<String, String> re = new Pair<String, String>(javadocBlockTag.getName().get(), javadocBlockTag.getContent().toText());
                    paramsTag.add(re);
                }
            }
        }
        return paramsTag;
    }

    private static List<Pair<String, String>> getThrowTag(MethodDeclaration m) {
        List<Pair<String, String>> throwTag = new ArrayList<>();
        Optional<Javadoc> javadocOptional = m.getJavadoc();
        if (javadocOptional.isPresent()) {
            Javadoc javadoc = javadocOptional.get();
            List<JavadocBlockTag> javadocBlockTags = javadoc.getBlockTags();
            for (JavadocBlockTag javadocBlockTag : javadocBlockTags) {
                String tagName = javadocBlockTag.getTagName();
                if (tagName.equals("throws")) {
                    Pair<String, String> re = new Pair<String, String>(javadocBlockTag.getName().get(), javadocBlockTag.getContent().toText());
                    throwTag.add(re);
                }
            }
        }
        return throwTag;
    }

    private static List<String> getParameter(MethodDeclaration m) {
        List<Parameter> parameterList = m.getParameters();
        List<String> pList = new LinkedList<>();
        for (Parameter p : parameterList) {
            pList.add(p.toString());
        }
        return pList;
    }

    private static List<String> getParameterType(MethodDeclaration m) {
        List<String> parameterTypeList = new ArrayList<>();
        String methodName = getQualifiedName(m);
        int me_end = methodName.indexOf(")");
        int me_start = methodName.indexOf("(");
        String parameterString = methodName.substring(me_start + 1, me_end);
        String[] parameterListString = parameterString.split(",");
        parameterTypeList = Arrays.asList(parameterListString);
        return parameterTypeList;
    }

    private static List<String> getException(MethodDeclaration m) {
        List<String> eList = new LinkedList<>();
        List<ReferenceType> thrownExceptions = m.getThrownExceptions();
        for (ReferenceType t : thrownExceptions) {
            String exception = t.resolve().asReferenceType().getQualifiedName();
            eList.add(exception);
        }
        return eList;
    }

    private static List<Pair<String, String>> getThrowsCodeDirective(MethodDeclaration m) {
        List<Pair<String, String>> throwsCodeDirective = new ArrayList<>();
        List<IfStmt> nodeList = m.findAll(IfStmt.class);
        for (IfStmt ifStmt : nodeList) {
            try {
                String ExceptionType = "";
                String conditionString = "";
                Node condition = ifStmt.getCondition();
                Node throwStmt = ifStmt.getThenStmt();
                if (!throwStmt.findAll(ThrowStmt.class).isEmpty()) {
                    List<ThrowStmt> throwsParsed = throwStmt.findAll(ThrowStmt.class);
                    conditionString = condition.toString();
                    ExceptionType = throwsParsed.get(0).getChildNodes().get(0).findAll(ClassOrInterfaceType.class).get(0).toString();
                    Pair<String, String> cd = new Pair<String, String>(ExceptionType, conditionString);
                    throwsCodeDirective.add(cd);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return throwsCodeDirective;
    }

    private static List<Pair<String, String>> getReturnCodeDirective(MethodDeclaration m) {
        List<Pair<String, String>> returnCodeDirective = new ArrayList<>();
        List<IfStmt> nodeList = m.findAll(IfStmt.class);
        String returnType = m.getTypeAsString();
        for (IfStmt ifStmt : nodeList) {
            try {
                Node condition = ifStmt.getCondition();
                Node returnStmt = ifStmt.getThenStmt();
                if (!returnStmt.findAll(ReturnStmt.class).isEmpty()) {
                    //获取parameter名称列表
                    List<String> parameters = new ArrayList<>();
                    for (Parameter node : m.getParameters()) {
                        parameters.add(node.getName().toString());
                    }
                    //获取condition名称列表
                    //List<Node> conditionChild = condition.getChildNodes();
                    //List<String> conditionChildString = new ArrayList<>();
                    //for (Node child: conditionChild){
                    //    conditionChildString.add(child.toString());
                    //}
                    for (String parameter : parameters) {
                        if (condition.toString().contains(parameter)) {
                            Pair<String, String> pcd = new Pair<String, String>(returnType, condition.toString());
                            returnCodeDirective.add(pcd);
                            //添加到tmpMethod中
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return returnCodeDirective;
    }

    public static ClassOrInterfaceDeclaration getAncestorNodeClassOrInterFaceDeclaration(Node methodDeclaration, Integer recursionCount) {
        Optional<Node> parent = methodDeclaration.getParentNode();
        if (parent.isPresent()) {
            if (parent.get() instanceof ClassOrInterfaceDeclaration)
                return (ClassOrInterfaceDeclaration) methodDeclaration.getParentNode().get();
            else {
                if (recursionCount > 5) return null;
                return getAncestorNodeClassOrInterFaceDeclaration(methodDeclaration.getParentNode().get(), recursionCount);
            }
        } else {
            System.out.println("No parentNode, recursionCount: " + recursionCount);
            return null;
        }
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
            List<MethodDeclaration> methodDeclarationList = cu.findAll(MethodDeclaration.class);
            for (MethodDeclaration methodDeclaration : methodDeclarationList) {
                new MethodVisitor().parseMethod(methodDeclaration);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
