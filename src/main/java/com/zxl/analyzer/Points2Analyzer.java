package com.zxl.analyzer;

import com.zxl.utils.Utils;
import spoon.reflect.CtModel;
import spoon.reflect.code.*;
import spoon.reflect.declaration.*;
import spoon.reflect.visitor.filter.TypeFilter;

import java.util.*;

public class Points2Analyzer {

        public static String KEY_STATIC = "static";
        public static String KEY_FIELD = "field";
        public static String KEY_METHOD = "method";
        public static String KEY_RETURN = "return";
        public static String KEY_OBJECT_NAME = "name";
        public static String KEY_OBJECT_TYPE = "type";

        public static void main(String[] args) {

            Points2Analyzer analyzer = new Points2Analyzer();
            analyzer.process("D:\\Code\\Java\\PointerBench\\src", "D:\\points2\\result\\result.json");

        }

        private void process(String targetPath, String resultPath) {
//            String targetPath = "D:\\annotationTest\\src\\main\\java\\com\\zxl\\points2";
            CtModel model = Utils.getModel(targetPath);
            Map<String, Map> allPoints2Map = new HashMap<>();
            initAllPoints2Map(model, allPoints2Map);
            analyseMethod(model, allPoints2Map);
            analyseConstructorCall(model, allPoints2Map);
            Utils.save(allPoints2Map, resultPath);
//            System.out.println(allPoints2Map.get(KEY_STATIC).size());
        }

        /***
         * Method Name: initAllPoints2Map
         * @param model
         * @param allPoints2Map
         * Description: initial a key-value map to represent the result of points-to analysis.
         * the result map would be like:
         * {    Class1:{
         *          "field":{
         *              "fieldA":[],
         *              "fieldB":[], ...
         *          },
         *          "method":{
         *              "methodX":{
         *                  "param1":[],
         *                  "var1":[], ...
         *              }
         *          }
         *          }
         *     Class2:{} ...
         * }
         */
        private void initAllPoints2Map(CtModel model, Map<String, Map> allPoints2Map) {
                List<CtClass> allClass = model.getElements(new TypeFilter<>(CtClass.class));
                for(CtClass c : allClass) {
                    Map<String, Map<String, ?>> classMap = new HashMap<>();
                    Map<String, Map> methodMap = new HashMap<>();
                    Map<String, Set> fieldMap = new HashMap<>();

                    List<CtField> allFields = c.getElements(new TypeFilter<>(CtField.class));
                    for(CtField field : allFields) {
                        Set locationSites = new HashSet();
                        if(field.isStatic()) {
                            fieldMap.put(KEY_STATIC + ":" + field.getSimpleName(), locationSites);
                        } else {
                            fieldMap.put(field.getSimpleName(), locationSites);
                        }
                    }

                    List<CtMethod> allMethods = c.getElements(new TypeFilter<>(CtMethod.class));
                    for(CtMethod method : allMethods) {
                        Map<String, Set> paramMap = new HashMap<>();
                        List<CtParameter> params = method.getElements(new TypeFilter<>(CtParameter.class));
                        for(CtParameter p : params) {
                            Set locationSites = new HashSet();
                            paramMap.put(p.getSimpleName(), locationSites);
                        }
                        paramMap.put(KEY_RETURN, new HashSet());
                        methodMap.put(method.getSignature(), paramMap);
                    }
                    classMap.put(KEY_FIELD, fieldMap);
                    classMap.put(KEY_METHOD, methodMap);
                    allPoints2Map.put(c.getQualifiedName(), classMap);
                }
            }

        private void analyseMethod(CtModel model, Map<String, Map> allPoints2Map) {
            List<CtClass> allClass = model.getElements(new TypeFilter<>(CtClass.class));
            for(CtClass c : allClass) {
                List<CtMethod> allMethod = c.getElements(new TypeFilter<>(CtMethod.class));
                for(CtMethod m : allMethod) {
                    Map<String, Set> methodMap = (Map<String, Set>) ((Map)allPoints2Map.get(c.getQualifiedName()).get(KEY_METHOD)).get(m.getSignature());
                    List<CtStatement> statements = m.getBody().getStatements();


                    for(CtStatement s : statements) {
                        // 1. Local Variable
                        if(s instanceof CtLocalVariable) {
                            Set<String> locationSites = methodMap.get(((CtLocalVariable) s).getSimpleName());
                            if(locationSites == null) {
                                locationSites = new HashSet<>();
                                methodMap.put(((CtLocalVariable) s).getSimpleName(), locationSites);
                            }
                            // assigned in declaration
                            if(((CtLocalVariable) s).getAssignment() instanceof CtConstructorCall) {
                                locationSites.add(c.getQualifiedName() + ":" + s.getPosition().getLine());
                            }
                        }
                        // 2. Other assignment
                        if(s instanceof CtAssignment) {
                            CtExpression assigned = ((CtAssignment) s).getAssigned();
                            CtExpression assigment = ((CtAssignment) s).getAssignment();
                            // field write
                            if(assigned instanceof CtFieldWrite) {
                                Set leftLocationsSites = Utils.getFieldSites(allPoints2Map, (CtFieldAccess) assigned);

                                if(assigment instanceof CtConstructorCall) {
                                    // right hand is a "new()" operation
                                    // example: x.f = new object()
                                    leftLocationsSites.add(c.getQualifiedName() + ":" + assigment.getPosition().getLine());

                                } else if(assigment instanceof CtFieldRead){
                                    // right hand is a field read operation
                                    // example: x.f = y.f
                                    Set assignmentSites = Utils.getFieldSites(allPoints2Map, (CtFieldAccess) assigment);
                                    leftLocationsSites.addAll(assignmentSites);
                                }
                            }
                        }
                    }

                    // collects all the location sites of return expression.
                    List<CtReturn> returnList = m.getElements(new TypeFilter<>(CtReturn.class));
                    Set returnSites = methodMap.get(KEY_RETURN);
                    for(CtReturn r : returnList){
                        CtExpression returnExp = r.getReturnedExpression();
                        if(returnExp instanceof CtFieldRead) {
                            Set fieldSites = Utils.getFieldSites(allPoints2Map, (CtFieldAccess) returnExp);
                            returnSites.addAll(fieldSites);
                        } else if(returnExp instanceof CtVariableRead) {
                            Set<String> varSites = methodMap.get(((CtVariableRead) returnExp).getVariable().getSimpleName());
                            returnSites.addAll(varSites);
                        } else if(returnExp instanceof CtConstructorCall) {
                            returnSites.add(c.getQualifiedName() + ":" + returnExp.getPosition().getLine());
                        }
                    }
                }
            }
        }

        private void analyseConstructorCall(CtModel model, Map<String, Map> allPoints2Map) {
            List<CtClass> allClass = model.getElements(new TypeFilter<>(CtClass.class));
            for(CtClass c : allClass) {
                List<CtConstructorCall> ctConstructorCalls = c.getElements(new TypeFilter<>(CtConstructorCall.class));

                for(CtConstructorCall call : ctConstructorCalls) {
                    // 1. ConstructorCall in field declaration
                    if(call.getParent() instanceof CtField) {
                        CtField parent = (CtField) call.getParent();
                        Map<String, Set> fieldMap = (Map<String, Set>) allPoints2Map.get(c.getQualifiedName()).get(KEY_FIELD);
                        Set locationSites = fieldMap.get(parent.isStatic() ? KEY_STATIC + ":" + parent.getSimpleName() : parent.getSimpleName());
                        locationSites.add(c.getQualifiedName() + ":" + call.getPosition().getLine());
                    }
                    // 2. ConstructorCall in method

                    // 3. ConstructorCall in AnonymousBlock
                }
            }
        }

}
