package visitor;

import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.ConstructorDeclaration;
import com.github.javaparser.ast.body.Parameter;
import com.github.javaparser.ast.type.ReferenceType;
import com.github.javaparser.javadoc.Javadoc;
import com.github.javaparser.javadoc.JavadocBlockTag;
import com.github.javaparser.resolution.types.ResolvedReferenceType;
import com.google.common.base.Joiner;
//import javafx.util.Pair;
import model.TempMethod;
import util.Pair;

import java.util.*;


public class ConstructorVisitor {
    public TempMethod parseConstructor(ConstructorDeclaration c) {
        try {
            TempMethod tempMethod = new TempMethod();
            String belongClassName = getBelongClass(c);
            tempMethod.setBelongClass(belongClassName);
            String fullDeclaration = getFullDeclaration(c);
            String description = getDescription(c);
            tempMethod.setDescription(description);
            String methodName = getQualifiedName(c);
            tempMethod.setMethodName(methodName);
            String shortName = getShortName(c);
            tempMethod.setName(shortName);
            tempMethod.setParamsTag(getParamTag(c));
            tempMethod.setReturnValueDescription(getReturnValueDescription(c));
            tempMethod.setThrowsTag(getThrowTag(c));
            tempMethod.setParameter(getParameter(c));
            tempMethod.setParameterTypeList(getParameterType(c));
            tempMethod.setThrowException(getException(c));
            String returnType = getConstructorType(c);
            tempMethod.setReturnValueType(returnType);
//            System.out.println("=========================");
//            System.out.println("Method name: " + methodName);
//            System.out.println("Short name: " + shortName);
//            System.out.println("Full declaration: " + fullDeclaration);
//            System.out.println("Belong class name: " + belongClassName);
//            System.out.println("Description: " + description);
//            System.out.println("Return Type: " + returnType);
//            System.out.println("=========================");
            return tempMethod;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private static String getDescription(ConstructorDeclaration c) {
        String description = "";
        Optional<Javadoc> javadocOptional = c.getJavadoc();
        if (javadocOptional.isPresent()) {
            Javadoc javadoc = javadocOptional.get();
            description = javadoc.getDescription().toText();
            return description;
        }
        return null;
    }

    private static String getConstructorType(ConstructorDeclaration c) {
        try {
            ClassOrInterfaceDeclaration parentClass = (ClassOrInterfaceDeclaration) getAncestorNodeClassOrInterFaceDeclaration(c, 0);
            if (parentClass != null) {
                String belongClassName;
                belongClassName = parentClass.resolve().getQualifiedName();
                return belongClassName;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return null;
    }

    public static String getMethodName(String belongClassName, String full_declaration) {
        String result = "";
        if (belongClassName.equals("")) {
            return "";
        }
        int end = full_declaration.lastIndexOf(")");
        int start = full_declaration.indexOf("(");
        String parameter = full_declaration.substring(start + 1, end + 1);
        String left = full_declaration.replace(parameter, "").replace("(", "");
        String shortName = left.split(" ")[left.split(" ").length - 1];
        String[] paramList = parameter.split(",(?=(((?!\\>).)*\\<)|[^\\<\\>]*$)");
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

    private static String getShortName(ConstructorDeclaration c) {
        String name = "";
        name = c.getName().asString();
        String full_declaration = c.getDeclarationAsString();
        int end = full_declaration.lastIndexOf(")");
        int start = full_declaration.indexOf("(");
        String right = full_declaration.substring(start, end + 1);
        name += right;
        return name;
    }

    private static String getQualifiedName(ConstructorDeclaration c) {
        //在某些情况下resolve会失败，暂不清楚什么情况下会触发bug。补救的getMethodName函数还有优化空间。
        String methodName = "";
        try {
            methodName = c.resolve().getQualifiedSignature();
            return methodName;
        } catch (Exception e) {
            ClassOrInterfaceDeclaration parentClass = (ClassOrInterfaceDeclaration) getAncestorNodeClassOrInterFaceDeclaration(c, 0);
            //String belongClassName = parentClass.resolve().getQualifiedName();
            String belongClassName = parentClass.getFullyQualifiedName().get();
            String full_declaration = c.getDeclarationAsString();
            methodName = getMethodName(belongClassName, full_declaration);
            return methodName;
        }
    }

    private static String getFullDeclaration(ConstructorDeclaration c) {
        String full_declaration = "";
        full_declaration = c.getDeclarationAsString();
        return full_declaration;
    }

    private static String getBelongClass(ConstructorDeclaration c) {
        String belongClassName = "";
        try {
            ClassOrInterfaceDeclaration parentClass = (ClassOrInterfaceDeclaration) getAncestorNodeClassOrInterFaceDeclaration(c, 0);
            if (parentClass != null) {
                belongClassName = parentClass.getFullyQualifiedName().get();
//                System.out.println("className: " + belongClassName);
            }
            return belongClassName;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private static String getReturnValueDescription(ConstructorDeclaration c) {
        String ReturnValueDescription = "";
        Optional<Javadoc> javadocOptional = c.getJavadoc();
        if (javadocOptional.isPresent()) {
            Javadoc javadoc = javadocOptional.get();
            List<JavadocBlockTag> javadocBlockTags = javadoc.getBlockTags();
            for (JavadocBlockTag javadocBlockTag : javadocBlockTags) {
                String tagName = javadocBlockTag.getTagName();
                if (tagName.equals("return")) {
                    ReturnValueDescription = javadocBlockTag.getContent().toText();
                }
            }
        }
        return ReturnValueDescription;
    }

    private static List<Pair<String, String>> getParamTag(ConstructorDeclaration c) {
        List<Pair<String, String>> paramsTag = new ArrayList<>();
        Optional<Javadoc> javadocOptional = c.getJavadoc();
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

    private static List<Pair<String, String>> getThrowTag(ConstructorDeclaration c) {
        List<Pair<String, String>> throwTag = new ArrayList<>();
        Optional<Javadoc> javadocOptional = c.getJavadoc();
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

    private static List<String> getParameter(ConstructorDeclaration c) {
        List<Parameter> parameterList = c.getParameters();
        List<String> pList = new LinkedList<>();
        for (Parameter p : parameterList) {
            pList.add(p.toString());
        }
        return pList;
    }

    private static List<String> getParameterType(ConstructorDeclaration c) {
        List<String> parameterTypeList = new ArrayList<>();
        String methodName = getQualifiedName(c);
//      修复方法名为me.cmoz.diver.JavaServer.registeredProcName) (OtpNode, HBaseClient, @Named ("erlang.registered_proc_name"))的情况
        int me_end = methodName.lastIndexOf(")");
        int me_start = methodName.indexOf("(");

        String parameterString = methodName.substring(me_start + 1, me_end);
//        修复参数map<T,T>,T的情况
        String[] parameterListString = parameterString.split(",(?=(((?!\\>).)*\\<)|[^\\<\\>]*$)");
        for (int i = 0; i < parameterListString.length; i++) {
            parameterListString[i] = parameterListString[i].trim();
        }
        parameterTypeList = Arrays.asList(parameterListString);
        return parameterTypeList;
    }

    private static List<String> getException(ConstructorDeclaration c) {
        List<String> eList = new LinkedList<>();
        List<ReferenceType> thrownExceptions = c.getThrownExceptions();
        for (ReferenceType t : thrownExceptions) {
            ResolvedReferenceType rrt = t.resolve().asReferenceType();
            String exception = t.resolve().asReferenceType().getQualifiedName();
            eList.add(exception);
        }
        return eList;
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
//            System.out.println("No parentNode, recursionCount: " + recursionCount);
            return null;
        }
    }
}
