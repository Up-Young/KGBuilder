package util;

import com.alibaba.fastjson.JSON;
import com.google.gson.Gson;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

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

    public static String ImportPath = Paths.get("input", "repository", "Java").toString();
    public static String OutputPath = Paths.get("output", "parseResult").toString();

    public static void writeModelListToJson(Path file_path_and_name, List<?> entityModelList) {
        int len = entityModelList.size();
        if ((len <= 0)) {
            return;
        }
        System.out.println(file_path_and_name);
        //oom
//            Files.write(file_path_and_name, JSON.toJSONString(entityModelList).getBytes());

//            oom
//            PrintWriter out = new PrintWriter(new FileWriter(String.valueOf(file_path_and_name)));
//            String json = new Gson().toJson((entityModelList));
//            out.write(json);

//            Files.write(file_path_and_name, json.getBytes());

        try {
            Files.write(file_path_and_name, Collections.singleton((new Gson().toJson((entityModelList)))));

        } catch (IOException e) {
            e.printStackTrace();
        }
//            Gson gson = new Gson();
//            gson.toJson(Collections.singleton((new Gson().toJson((entityModelList), new FileWriter(String.valueOf(file_path_and_name)));
//                        System.out.println(file_util.Tools.writeToJsonpath_and_name);
//            for (Object o : entityModelList) {
//                Files.write(file_path_and_name, JSON.toJSONString(o).getBytes(), StandardOpenOption.APPEND);
//            }

    }


    public static void writeToJson(String FilePath, List<?> entityModelList, String filename) {
//        System.out.println("-------start write--------");
        Path file_path_with_name = Paths.get(FilePath, filename);
//        if(!Files.exists(file_path_with_name)){
//            try {
//                Files.createFile(file_path_with_name);
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }
        writeModelListToJson(file_path_with_name, entityModelList);
//        System.out.println("-------finish write--------");
    }
}
