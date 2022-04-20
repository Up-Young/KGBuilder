package visitor;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Modifier;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.body.VariableDeclarator;
import com.github.javaparser.ast.comments.Comment;
import com.github.javaparser.ast.type.ClassOrInterfaceType;
import com.github.javaparser.javadoc.Javadoc;
import com.github.javaparser.resolution.UnsolvedSymbolException;
import com.github.javaparser.resolution.declarations.ResolvedValueDeclaration;
import com.github.javaparser.resolution.types.ResolvedPrimitiveType;
import com.google.common.base.Strings;
import model.ClassModel;
import model.EntityModel;
import model.FieldModel;
import model.RelationModel;

import java.util.*;

import static util.Tools.*;

public class ClassAndPackageVisitor {
    public Set<EntityModel> entityModelSet = new HashSet<>();
    public Set<ClassModel> classModelSet = new HashSet<>();
    public static Set<String> recordName = new HashSet<>();
    public List<RelationModel> relationModelList = new ArrayList<>();
    public List<FieldModel> fieldModelArrayList = new ArrayList<>();
    public List<RelationModel> fieldRelationModelList = new ArrayList<>();
    private static Integer fieldId = 1;

    private void addEntityModelList(String qualifiedName, Integer type, String code, String comment) {
        recordName.add(qualifiedName);
        EntityModel entityModel = new EntityModel();
        qualifiedName = qualifiedName.replace(" ", "");
        entityModel.setQualified_name(qualifiedName);
        entityModel.setCode(code);
        entityModel.setType(type);
        entityModel.setComment(comment);
        this.entityModelSet.add(entityModel);
    }

    private void addClassModel(String classOrInterfaceName, String name, Integer type, String code, String class_comment, List<Comment> AllComment) {
        recordName.add(classOrInterfaceName);

        ClassModel classyModel = new ClassModel();

//        classOrInterfaceName = classOrInterfaceName.replace(" ", "");
        classyModel.setQualified_name(classOrInterfaceName);
        classyModel.setName(name);
        classyModel.setDescription(code);
        classyModel.setType(type);
        classyModel.setComment(class_comment);
        classModelSet.add(classyModel);
    }

    private void addRelationModelList(String startName, String endName, Integer relationType) {
        startName = startName.replace("\n", "");
        if ((!startName.equals("")) && (!endName.equals(""))) {
            RelationModel relationModel = new RelationModel();
            startName = startName.replace(", ", ",");
            endName = endName.replace(", ", ",");
            relationModel.setStart_name(startName);
            relationModel.setRelation_type(relationType);
            relationModel.setEnd_name(endName);
            relationModelList.add(relationModel);
        }
    }

    private void addFieldRelationModelList(String startName, String endName, Integer relationType) {
        startName = startName.replace("\n", "");
        if ((!startName.equals("")) && (!endName.equals(""))) {
            RelationModel relationModel = new RelationModel();
            startName = startName.replace(", ", ",");
            endName = endName.replace(", ", ",");
            relationModel.setStart_name(startName);
            relationModel.setRelation_type(relationType);
            relationModel.setEnd_name(endName);
            fieldRelationModelList.add(relationModel);
        }
    }

    public void parseClassInterface(CompilationUnit cu, String packageName) {
        List<ClassOrInterfaceDeclaration> classOrInterfaceDeclarationList = cu.findAll(ClassOrInterfaceDeclaration.class);
        try {
            for (ClassOrInterfaceDeclaration classOrInterfaceDeclaration : classOrInterfaceDeclarationList) {
                String class_comment = "";
                String classOrInterfaceName = "";
                String description = "";
                String name = classOrInterfaceDeclaration.getName().asString();
                Optional<String> classOrInterfaceNameOptional = classOrInterfaceDeclaration.getFullyQualifiedName();
                if (classOrInterfaceNameOptional.isPresent())
                    classOrInterfaceName = classOrInterfaceDeclaration.getFullyQualifiedName().get();
                if (classOrInterfaceName.equals("java.lang.Object")) {
                    System.out.println(classOrInterfaceName);
                }
                boolean isInterface = classOrInterfaceDeclaration.isInterface();
                boolean containsInheritdoc = false;
                Optional<Comment> commentOptional = classOrInterfaceDeclaration.getComment();
                List<Comment> commentList = classOrInterfaceDeclaration.getAllContainedComments();
                if (commentOptional.isPresent()) {
                    class_comment = commentOptional.get().getContent();
                }
                System.out.println(Strings.repeat("=", classOrInterfaceName.length()));
                if (isInterface) {
                    System.out.println("interface " + classOrInterfaceName);
                    addClassModel(classOrInterfaceName, name, INTERFACE_ENTITY, description, class_comment, commentList);
                } else {
                    System.out.println("Class " + classOrInterfaceName);
                    addClassModel(classOrInterfaceName, name, CLASS_ENTITY, description, class_comment, commentList);
                }
                if (!packageName.equals("")) addRelationModelList(classOrInterfaceName, packageName, BELONGTO);
                List<ClassOrInterfaceType> extendedTypeList = classOrInterfaceDeclaration.getExtendedTypes();
                for (ClassOrInterfaceType extendedType : extendedTypeList) {
                    String extendName = "";
                    try {
                        extendName = extendedType.resolve().getQualifiedName();
                    } catch (UnsolvedSymbolException e) {
                        System.err.println("UnsolvedSymbolException");
                        extendName = extendedType.toString();
//                        e.printStackTrace();
                    }
                    System.out.println("extend " + extendName);
                    addRelationModelList(classOrInterfaceName, extendName, EXTEND);
                }
                List<ClassOrInterfaceType> implementedTypeList = classOrInterfaceDeclaration.getImplementedTypes();
                for (ClassOrInterfaceType implementedType : implementedTypeList) {
                    String interfaceName ="";
                    try {
                        interfaceName = implementedType.resolve().getQualifiedName();
                        //                    修复UnsolvedSymbolException异常
                    } catch (UnsolvedSymbolException e) {
//                        e.printStackTrace();
                        System.err.println("UnsolvedSymbolException");
                        interfaceName=implementedType.toString();
                    }
                    System.out.println("implemented " + interfaceName);
                    addRelationModelList(classOrInterfaceName, interfaceName, IMPLEMENT);

                }
                Optional<Javadoc> javadocOptional = classOrInterfaceDeclaration.getJavadoc();
                if (javadocOptional.isPresent()) {
                    Javadoc javadoc = javadocOptional.get();
                    description = javadoc.getDescription().toText();
                    if (description.contains("{@inheritDoc}")) containsInheritdoc = true;
                }
                // add field
                List<FieldDeclaration> fieldDeclarationList = classOrInterfaceDeclaration.getFields();
                for (FieldDeclaration fieldDeclaration : fieldDeclarationList) {
                    System.out.println("fieldDeclaration: " + fieldDeclaration.getModifiers());
                    List<Modifier> modifierList = fieldDeclaration.getModifiers();
                    StringBuilder declaration = new StringBuilder();
                    String comment = "";

                    for (Modifier m : modifierList) {
                        declaration.append(m.toString());
                        System.out.println("modifierList: " + m.toString());
                    }
                    List<VariableDeclarator> variables = fieldDeclaration.getVariables();
                    for (VariableDeclarator v : variables) {
                        ResolvedValueDeclaration d = v.resolve();
//                        System.out.println("fieldName: " + d.getName());
//                        System.out.println("fieldAll: " + v.toString());
                        String valTypeName = "";
                        try {
                            valTypeName = d.getType().asReferenceType().getQualifiedName();
                        } catch (Exception e1) {
                            try {
                                valTypeName = ((ResolvedPrimitiveType) d.getType()).name().toLowerCase();
                            } catch (Exception e2) {
                                valTypeName = v.getTypeAsString();
                            }
                        }
                        declaration.append(valTypeName);
                        declaration.append(" ");
                        declaration.append(v.toString());
                        System.out.println("declaration: " + declaration);
                        comment = fieldDeclaration.toString().replace(declaration.toString() + ";", "");
                        FieldModel fieldModel = new FieldModel();
                        fieldModel.setId(fieldId);
                        fieldModel.setField_type(valTypeName);
                        fieldModel.setField_name(d.getName());
                        fieldModel.setFull_declaration(declaration.toString());
                        fieldModel.setComment(comment);
                        if (fieldId == 282) {
                            System.out.println(fieldId);
                        }
                        addFieldRelationModelList(classOrInterfaceName, fieldId.toString(), Field_In_Class);
                        fieldModelArrayList.add(fieldModel);
                        fieldId++;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

//    private static String resolveInheritdoc(ClassOrInterfaceDeclaration classOrInterfaceDeclaration, Integer recursion){
//        Optional<Javadoc> javadocOptional = classOrInterfaceDeclaration.getJavadoc();
//        String description = "";
//        if(javadocOptional.isPresent()){
//            Javadoc javadoc = javadocOptional.get();
//            description = javadoc.getDescription().toText();
//            if(description.contains("{@inheritDoc}")){
//                List<ClassOrInterfaceType> implementedTypeList = classOrInterfaceDeclaration.getImplementedTypes();
//                for (ClassOrInterfaceType implementedType : implementedTypeList) {
//                    try {
//                        String interfaceName = implementedType.resolve().getQualifiedName();
//                        Optional<Javadoc> interfaceNameOptional = implementedType.resolve().getJavadoc();
//                    }catch (Exception e){
//                        e.printStackTrace();
//                    }
//                }
//            }
//        }
//    }

    public String parsePackage(CompilationUnit cu) {
        String packageName = "";
        try {
            if (cu.getPackageDeclaration().isPresent()) {
                packageName = cu.getPackageDeclaration().get().getName().asString();
                addEntityModelList(packageName, PACKAGE_ENTITY, "", "");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return packageName;
    }


    public void cleanAll() {
        System.out.println("start clean");
        entityModelSet.clear();
        classModelSet.clear();
        relationModelList.clear();
        recordName.clear();
        fieldModelArrayList.clear();
        fieldRelationModelList.clear();
        System.out.println("clean finish");
    }
}