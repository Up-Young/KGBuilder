package util;

import com.alibaba.fastjson.JSON;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Collection;

public class Tools {
    public static final Integer PACKAGE_ENTITY = 1;
    public static final Integer CLASS_ENTITY = 2;
    public static final Integer INTERFACE_ENTITY = 3;
    public static final Integer METHOD_ENTITY = 4;
    public static final Integer FIELD_ENTITY = 5;
    public static final Integer VARIABLE_ENTITY = 6;

    public static final Integer BELONGTO = 1;
    public static final Integer EXTEND = 2;
    public static final Integer IMPLEMENT = 3;
    public static final Integer Has_Parameter = 4;
    public static final Integer Method_Call = 13;
    public static final Integer EXCEPTION_THROW = 6;
    public static final Integer Field_In_Method = 15;
    public static final Integer RELATION_CATEGORY_METHOD_IMPLEMENT_CODE_CALL_CLASS = 14;
    public static final Integer Field_In_Class = 14;

    public static String ImportPath = "srcCode";
    public static String OutputPath = "parseResult";

    public static void writeModelListToJson(String filename, Collection<?> entityModelList) {
        try {
            if (!(entityModelList.size() > 0)) {
                return;
            }
            System.out.println(filename);

            Files.write(Paths.get(filename), JSON.toJSONString(entityModelList).getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void writeToJson(String FilePath, Collection<?> entityModelList, String filename){
        System.out.println("-------start write--------");
        writeModelListToJson(FilePath+"\\"+filename, entityModelList);
        System.out.println("-------finish write--------");
    }
}
