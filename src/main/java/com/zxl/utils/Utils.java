package com.zxl.utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.zxl.utils.CommonParam.RelationType;
import spoon.Launcher;
import spoon.reflect.CtModel;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.reference.CtFieldReference;
import spoon.reflect.reference.CtVariableReference;

import javax.management.relation.Relation;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static com.zxl.analyzer.Points2Analyzer.KEY_FIELD;
import static com.zxl.analyzer.Points2Analyzer.KEY_METHOD;
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

    public static void save(Map<String, ?> allClassPoints2, String resultPath) {
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

    public static Set getFieldSites(Map<String, Map> allPoints2Map, CtFieldReference fieldVarRef) {
        String targetClassName = fieldVarRef.getDeclaringType().getQualifiedName();
        String targetFieldName = fieldVarRef.isStatic()? KEY_STATIC + ":" + fieldVarRef.getSimpleName() : fieldVarRef.getSimpleName();

        Set targetLocationSites = ((Map<String, Set>)((Map)(allPoints2Map.get(targetClassName)).get(KEY_FIELD))).get(targetFieldName);
        return targetLocationSites;
    }

    public static Set getParamValueInMethod(Map<String, Map> allPoints2Map, CtMethod m, CtVariableReference var) {
        if(var == null) {
            return null;
        }
        String className = m.getDeclaringType().getQualifiedName();
        String methodName = m.getSignature();
        return (Set) ((Map<String, Map>) ((Map) (allPoints2Map.get(className)).get(KEY_METHOD))).get(methodName).get(var.getSimpleName());

    }

    public static void addRelation(Map<String,Set> relationMap, String[] relationNodes) {
        Set left = (Set) relationMap.get(relationNodes[0]);
        if(left == null) {
            left = new HashSet();
            relationMap.put(relationNodes[0], left);
        }
        left.add(relationNodes[1]);
    }

    public static String[] encodeRelationNodeName(RelationType relationType, Object[] relationleft, Object[] relationRight) {
        String[] relationNodes = new String[2];
        switch (relationType) {
            case F2F:
                relationNodes[0] = "class:" + relationleft[0] + ";" + "field:" + relationleft[1] + ";";
                relationNodes[1] = "class:" + relationRight[0] + ";" + "field:" + relationRight[1] + ";";
                break;
            case F2M:
                relationNodes[0] = "class:" + relationleft[0] + ";" + "field:" + relationleft[1] + ";";
                relationNodes[1] = "class:" + relationRight[0] + ";" + "method:" + relationRight[1] + ":" + relationRight[2] + ";";
                break;
            case M2F:
                relationNodes[0] = "class:" + relationleft[0] + ";" + "method:" + relationleft[1] + ":" + relationleft[2] + ";";
                relationNodes[1] = "class:" + relationRight[0] + ";" + "field:" + relationRight[1] + ";";
                break;
            case M2M:
                relationNodes[0] = "class:" + relationleft[0] + ";" + "method:" + relationleft[1] + ":" + relationleft[2] + ";";
                relationNodes[1] = "class:" + relationRight[0] + ";" + "method:" + relationRight[1] + ":" + relationRight[2] + ";";
                break;
        }
        return relationNodes;
    }
}
