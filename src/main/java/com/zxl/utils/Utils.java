package com.zxl.utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.zxl.utils.CommonParam.RelationType;
import spoon.Launcher;
import spoon.reflect.CtModel;
import spoon.reflect.code.CtFieldAccess;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.reference.CtExecutableReference;
import spoon.reflect.reference.CtFieldReference;
import spoon.reflect.reference.CtVariableReference;

import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static com.zxl.analyzer.Points2Analyzer.*;


public class Utils {
    public static CtModel getModel(String targetPath) {
        Launcher launcher = new Launcher();
        launcher.addInputResource(targetPath);
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
            System.out.println("store in file " + resultPath);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static Set getFieldSites(Map<String, Map> allPoints2Map, CtFieldAccess fieldAccess) {
        CtFieldReference fieldRef = fieldAccess.getVariable();

        String targetClassName = fieldRef.getDeclaringType().getQualifiedName();
        String targetFieldName = fieldRef.getSimpleName();
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

    public static Set getParamValueByName(Map<String, Map> allPoints2Map, String className, String methodName, CtVariableReference var) {
        if(var == null) {
            return null;
        }
        return (Set) ((Map<String, Map>) ((Map) (allPoints2Map.get(className)).get(KEY_METHOD))).get(methodName).get(var.getSimpleName());

    }

    public static Set getMethodReturn(Map<String, Map> allPoints2Map, CtExecutableReference exec) {
        String className = exec.getDeclaringType().getQualifiedName();
        String methodName = exec.getSignature();
        Set result = new HashSet();
        try {
            result =  (Set) ((Map<String, Map>) ((Map) (allPoints2Map.get(className)).get(KEY_METHOD))).get(methodName).get(KEY_RETURN);
        } catch (Exception e) {
            System.out.println("Can't not resolve method ==> " + className + ":" + methodName);
        }
        return result;
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
                relationNodes[0] = relationleft[0] + ":" + "field:" + relationleft[1];
                relationNodes[1] = relationRight[0] + ":" + "field:" + relationRight[1];
                break;
            case F2M:
                relationNodes[0] = relationleft[0] + ":" + "field:" + relationleft[1];
                relationNodes[1] = relationRight[0] + ":" + "method:" + relationRight[1] + ":" + relationRight[2];
                break;
            case M2F:
                relationNodes[0] = relationleft[0] + ":" + "method:" + relationleft[1] + ":" + relationleft[2];
                relationNodes[1] = relationRight[0] + ":" + "field:" + relationRight[1];
                break;
            case M2M:
                relationNodes[0] = relationleft[0] + ":" + "method:" + relationleft[1] + ":" + relationleft[2];
                relationNodes[1] = relationRight[0] + ":" + "method:" + relationRight[1] + ":" + relationRight[2];
                break;
        }
        return relationNodes;
    }

    public static boolean dealRelation(Map<String, Map> allPoint2Map, String relationLeft, String relationRight) {
        String[] relationLeftArray = relationLeft.split(":");
        Set leftSet = null;
        if(relationLeftArray[1].equals(KEY_FIELD)) {
            leftSet = (Set) ((Map)((allPoint2Map.get(relationLeftArray[0])).get(KEY_FIELD))).get(relationLeftArray[2]);
        } else {
            leftSet = (Set) ((Map)((Map)((allPoint2Map.get(relationLeftArray[0])).get(KEY_METHOD))).get(relationLeftArray[2])).get(relationLeftArray[3]);
        }

        String[] relationRightArray = relationRight.split(":");
        Set rightSet = null;
        if(relationRightArray[1].equals(KEY_FIELD)) {
            rightSet = (Set) ((Map)((allPoint2Map.get(relationRightArray[0])).get(KEY_FIELD))).get(relationRightArray[2]);
        } else {
            rightSet = (Set) ((Map)((Map)((allPoint2Map.get(relationRightArray[0])).get(KEY_METHOD))).get(relationRightArray[2])).get(relationRightArray[3]);
        }

        if(leftSet.containsAll(rightSet)) {
            return false;
        } else {
            leftSet.addAll(rightSet);
            return true;
        }
    }

    public static void resolveRelation(Map<String, Map> allPoints2Map, Map<String, Set> relations) {
        boolean changingFlag;
        do{
            changingFlag = false;
            // repeat scanning the relation util the points-to set stop changing.
            for(String relationLeft : relations.keySet()) {
                Set<String> relationRightSet = relations.get(relationLeft);
                for(String relationRight : relationRightSet) {
                    try {
                        if (dealRelation(allPoints2Map, relationLeft, relationRight)) {
                            changingFlag = true;
                        }
                    } catch (Exception e) {
                        // relation node can't be found.
                        e.printStackTrace();
                    }
                }
            }
        } while(changingFlag);
    }

    public static String getCurrentPath(){
        Path path = Paths.get("");
        return path.toAbsolutePath().toString();
    }
}
