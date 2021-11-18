package model;

import javafx.util.Pair;

import java.util.List;

public class TempMethod {
    private String description;
    private String methodName;
    private String name;
    // params name, params description
    private List<Pair<String, String>> paramsTag;
    private List<Pair<String, String>> throwsTag;
    private List<Pair<String, String>> throwsCodeDirective;
    private List<String> parameter;
    private List<String> parameterTypeList;
    private List<String> throwException;
    private String returnValueDescription;
    private String belongClass;
    private String returnValueType;
    private List<Pair<String, String>> returnCodeDirective;
    private List<String> modifierList;

    public TempMethod() {

    }

    public List<String> getModifierList() {
        return modifierList;
    }

    public void setModifierList(List<String> modifierList) {
        this.modifierList = modifierList;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getMethodName() {
        return methodName;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<String> getParameter() {
        return parameter;
    }

    public void setParameter(List<String> parameter) {
        this.parameter = parameter;
    }

    public List<String> getParameterTypeList() {
        return parameterTypeList;
    }

    public void setParameterTypeList(List<String> parameterTypeList) {
        this.parameterTypeList = parameterTypeList;
    }

    public List<String> getThrowException() {
        return throwException;
    }

    public void setThrowException(List<String> throwException) {
        this.throwException = throwException;
    }

    public void setParamsTag(List<Pair<String, String>> paramsTag) {
        this.paramsTag = paramsTag;
    }

    public List<Pair<String, String>> getParamsTag() {
        return paramsTag;
    }

    public void setThrowsTag(List<Pair<String, String>> throwsTag) {
        this.throwsTag = throwsTag;
    }

    public List<Pair<String, String>> getThrowsTag() {
        return throwsTag;
    }

    public void setReturnValueDescription(String returnValueDescription) {
        this.returnValueDescription = returnValueDescription;
    }

    public String getReturnValueDescription() {
        return returnValueDescription;
    }

    public void setBelongClass(String belongClass) {
        this.belongClass = belongClass;
    }

    public String getBelongClass() {
        return belongClass;
    }

    public void setReturnValueType(String returnValueType) {
        this.returnValueType = returnValueType;
    }

    public String getReturnValueType() {
        return returnValueType;
    }

    public void setThrowsCodeDirective(List<Pair<String, String>> throwsCodeDirective) {
        this.throwsCodeDirective = throwsCodeDirective;
    }

    public List<Pair<String, String>> getThrowsCodeDirective() {
        return throwsCodeDirective;
    }

    public void setReturnCodeDirective(List<Pair<String, String>> returnCodeDirective) {
        this.returnCodeDirective = returnCodeDirective;
    }

    public List<Pair<String, String>> getReturnCodeDirective() {
        return returnCodeDirective;
    }
}
