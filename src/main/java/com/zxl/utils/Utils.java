package com.zxl.utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import spoon.Launcher;
import spoon.reflect.CtModel;
import spoon.reflect.code.CtFieldAccess;
import spoon.reflect.code.CtFieldWrite;
import spoon.reflect.reference.CtFieldReference;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.Map;
import java.util.Set;

import static com.zxl.analyzer.Points2Analyzer.KEY_FIELD;
import static com.zxl.analyzer.Points2Analyzer.KEY_STATIC;

public class Utils {
    public static CtModel getModel(String targetPath) {
        Launcher launcher = new Launcher();
        launcher.addInputResource(targetPath);
//        launcher.addInputResource("D:\\annotationTest\\src\\main\\java\\com\\zxl\\points2");
//        launcher.addInputResource("D:\\Code\\Java\\PointerBench\\src");

        launcher.getEnvironment().setAutoImports(true);
        launcher.getEnvironment().setNoClasspath(true);
        launcher.buildModel();

        return launcher.getModel();
    }

    public static void save(Map<String, Map> allClassPoints2, String resultPath) {
        try {
            File file = new File(resultPath);
            if(file.exists()) {
                file.delete();
            }
            file.createNewFile();
            Writer writer = new OutputStreamWriter(new FileOutputStream(file), "UTF-8");
            Gson gson = new GsonBuilder()
                    .setPrettyPrinting()
                    .create();
            writer.write(gson.toJson(allClassPoints2));
            writer.flush();
            writer.close();
            System.out.println("\n\nRESULT:");
            System.out.println(gson.toJson(allClassPoints2));
            System.out.println("store in file \"result.json\"");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static Set getFieldSites(Map<String, Map> allPoints2Map, CtFieldAccess fieldAccess) {
        CtFieldReference fieldVarRef = fieldAccess.getVariable();
        String targetClassName = fieldVarRef.getDeclaringType().getQualifiedName();
        String targetFieldName = fieldVarRef.isStatic()? KEY_STATIC + ":" + fieldVarRef.getSimpleName() : fieldVarRef.getSimpleName();

        Set targetLocationSites = ((Map<String, Set>)((Map)(allPoints2Map.get(targetClassName)).get(KEY_FIELD))).get(targetFieldName);
        return targetLocationSites;
    }
}
