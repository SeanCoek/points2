package com.zxl.analyzer;

import com.zxl.utils.Utils;
import jdk.internal.org.objectweb.asm.TypeReference;
import spoon.reflect.CtModel;
import spoon.reflect.code.*;
import spoon.reflect.declaration.*;
import spoon.reflect.reference.CtExecutableReference;
import spoon.reflect.reference.CtFieldReference;
import spoon.reflect.reference.CtParameterReference;
import spoon.reflect.reference.CtVariableReference;
import spoon.reflect.visitor.filter.TypeFilter;

import java.io.File;
import java.util.*;
import com.zxl.utils.CommonParam.RelationType;

public class Points2Analyzer {

        public static String KEY_STATIC = "static";
        public static String KEY_FIELD = "field";
        public static String KEY_METHOD = "method";
        public static String KEY_RETURN = "return";
        public static String KEY_OBJECT_NAME = "name";
        public static String KEY_OBJECT_TYPE = "type";
        public static Map<String, Set> relation = new HashMap<>();

        public static void main(String[] args) {

            Points2Analyzer analyzer = new Points2Analyzer();
//            analyzer.process("D:\\Code\\Java\\PointerBench\\src", null, null);
//            analyzer.process("D:\\Code\\Java\\SpoonTest.java", "D:\\points2\\result\\result.json");
//            analyzer.process("D:\\project\\JInfoFlow-bench\\src\\main\\java", null, null);
            analyzer.process("D:\\Code\\Java\\PointerBench\\src", null, null);
        }

        public Map process(String targetPath, String resultPath, String relationPath) {
            if(resultPath == null) {
                resultPath = Utils.getCurrentPath() + File.separator + "result" + File.separator + "result.json";
            }
            if(relationPath == null) {
                relationPath = Utils.getCurrentPath() + File.separator + "result" + File.separator + "relation.json";
            }
            CtModel model = Utils.getModel(targetPath);
            Map<String, Map> allPoints2Map = new HashMap<>();
            initAllPoints2Map(model, allPoints2Map);
            analyseMethod(model, allPoints2Map);
            analyseConstructorCall(model, allPoints2Map);
            Utils.save(relation, relationPath);
            Utils.resolveRelation(allPoints2Map, relation);
            Utils.save(allPoints2Map, resultPath);
            return allPoints2Map;
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
//                        if(field.isStatic()) {
//                            fieldMap.put(KEY_STATIC + ":" + field.getSimpleName(), locationSites);
//                        } else {
//                            fieldMap.put(field.getSimpleName(), locationSites);
//                        }
                        fieldMap.put(field.getSimpleName(), locationSites);
                    }

                    List<CtMethod> allMethods = c.getElements(new TypeFilter<>(CtMethod.class));
                    for(CtMethod method : allMethods) {
                        Map<String, Set> paramMap = new HashMap<>();
                        List<CtParameter> params = method.getElements(new TypeFilter<>(CtParameter.class));
                        for(CtParameter p : params) {
                            Set locationSites = new HashSet();
//                            locationSites.add(c.getQualifiedName() + ":" + p.getPosition().getLine() + "(" + p.getSimpleName() + ")");
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
                    if(m.isAbstract()) {
                        continue;
                    }
                    Map<String, Set> methodMap = (Map<String, Set>) ((Map)allPoints2Map.get(c.getQualifiedName()).get(KEY_METHOD)).get(m.getSignature());
                    List<CtStatement> statements = m.getBody().getStatements();

                    for(CtStatement s : statements) {
                        /*
                        <<========================================Java BNF=========================================>>
                            <variable declarators> ::= <variable declarator> | <variable declarators> , <variable declarator>
                            <variable declarator> ::= <variable declarator id> | <variable declarator id> = <variable initializer>
                            <variable initializer> ::= <expression> | <array initializer>

                            <assignment> ::= <left hand side> <assignment operator> <assignment expression>
                            <left hand side> ::= <expression name> | <field access> | <array access>
                            <assignment operator> ::= = | *= | /= | %= | += | -= | <<= | >>= | >>>= | &= | ^= | |=
                        <<=========================================================================================>>
                         */

                        // 1. Variable Initial
                        if(s instanceof CtLocalVariable) {
                            Set<String> locationSites = methodMap.get(((CtLocalVariable) s).getSimpleName());
                            if(locationSites == null) {
                                locationSites = new HashSet<>();
                                methodMap.put(((CtLocalVariable) s).getSimpleName(), locationSites);
                            }
                            // assigned in declaration
                            CtExpression defaultExp = ((CtLocalVariable) s).getDefaultExpression();
                            if(defaultExp != null) {
                                if(defaultExp instanceof CtVariableRead) {
                                    CtVariableReference varRef = ((CtVariableRead) defaultExp).getVariable();
                                    if(varRef instanceof CtFieldReference) {
                                        locationSites.addAll(Utils.getFieldSites(allPoints2Map, (CtFieldAccess) (varRef.getParent())));
                                        // adding a method-to-field type of relation
                                        Utils.addRelation(relation,
                                                Utils.encodeRelationNodeName(RelationType.M2F,
                                                        new Object[]{c.getQualifiedName(), m.getSignature(), ((CtLocalVariable) s).getSimpleName()},
                                                        new Object[]{((CtFieldReference) varRef).getDeclaringType().getQualifiedName(), varRef.getSimpleName()}));
                                    } else if(varRef instanceof CtParameterReference) {
                                         locationSites.addAll(Utils.getParamValueInMethod(allPoints2Map, m, varRef));
                                        // adding a method-to-field type of relation
                                        Utils.addRelation(relation,
                                                Utils.encodeRelationNodeName(RelationType.M2M,
                                                        new Object[]{c.getQualifiedName(), m.getSignature(), ((CtLocalVariable) s).getSimpleName()},
                                                        new Object[]{c.getQualifiedName(), m.getSignature(), varRef.getSimpleName()}));
                                    } else{
                                        //TODO array access
                                    }
                                } else if(defaultExp instanceof CtConstructorCall) {
                                    locationSites.add(c.getQualifiedName() + ":" + s.getPosition().getLine());
                                }
                            }
                        }
                        // 2. Assignment
                        if(s instanceof CtAssignment) {
                            CtExpression assigned = ((CtAssignment) s).getAssigned();
                            CtExpression assignment = ((CtAssignment) s).getAssignment();
                            // <left hand side> ::= <field access>
                            Set leftLocationsSites = null;
                            if(assigned instanceof CtFieldWrite) {

                                leftLocationsSites = Utils.getFieldSites(allPoints2Map, (CtFieldAccess) assigned);
                            }
                            else if(assigned instanceof CtVariableWrite) {
                                //TODO left-hand is local variable
                                leftLocationsSites = methodMap.get(((CtVariableWrite) assigned).getVariable().getSimpleName());
                            }

                            Set assignmentSites = null;
                            if(assignment instanceof CtConstructorCall) {
                                // right hand is a "new()" operation
                                // example: x.f = new object()
                                leftLocationsSites.add(c.getQualifiedName() + ":" + assignment.getPosition().getLine());

                            }
                            else if(assignment instanceof CtFieldRead){
                                // right hand is a field read operation
                                // example: x.f = y.f
                                assignmentSites = Utils.getFieldSites(allPoints2Map, (CtFieldAccess) assignment);
                                leftLocationsSites.addAll(assignmentSites);


                                if(assigned instanceof CtFieldWrite) {
                                    Utils.addRelation(relation,
                                            Utils.encodeRelationNodeName(RelationType.F2F,
                                                    new Object[]{((CtFieldWrite)assigned).getVariable().getDeclaringType().getQualifiedName(), ((CtFieldWrite) assigned).getVariable().getSimpleName()},
                                                    new Object[]{((CtFieldRead) assignment).getVariable().getDeclaringType().getQualifiedName(), ((CtFieldRead) assignment).getVariable().getSimpleName()}));
                                }
                                else if(assigned instanceof CtVariableWrite) {
                                    Utils.addRelation(relation,
                                            Utils.encodeRelationNodeName(RelationType.M2F,
                                                    new Object[]{c.getQualifiedName(), m.getSignature(), ((CtVariableWrite) assigned).getVariable().getSimpleName()},
                                                    new Object[]{((CtFieldRead) assignment).getVariable().getDeclaringType().getQualifiedName(), ((CtFieldRead) assignment).getVariable().getSimpleName()}));
                                }
                            }
                            else if(assignment instanceof CtVariableRead) {
                                assignmentSites = methodMap.get(((CtVariableRead) assignment).getVariable().getSimpleName());
                                leftLocationsSites.addAll(assignmentSites);

                                if(assigned instanceof CtFieldWrite) {
                                    Utils.addRelation(relation,
                                            Utils.encodeRelationNodeName(RelationType.F2M,
                                                    new Object[]{((CtFieldWrite)assigned).getVariable().getDeclaringType().getQualifiedName(), ((CtFieldWrite) assigned).getVariable().getSimpleName()},
                                                    new Object[]{c.getQualifiedName(), m.getSignature(), ((CtVariableRead) assignment).getVariable().getSimpleName()}));
                                }
                                else if(assigned instanceof CtVariableWrite) {
                                    Utils.addRelation(relation,
                                            Utils.encodeRelationNodeName(RelationType.M2M,
                                                    new Object[]{c.getQualifiedName(), m.getSignature(), ((CtVariableWrite) assigned).getVariable().getSimpleName()},
                                                    new Object[]{c.getQualifiedName(), m.getSignature(), ((CtVariableRead) assignment).getVariable().getSimpleName()}));
                                }
                            }
                            else if(assignment instanceof CtInvocation) {
                                assignmentSites = Utils.getMethodReturn(allPoints2Map, ((CtInvocation) assignment).getExecutable());
                                leftLocationsSites.addAll(assignmentSites);

                                // adding a field-to-method type of relation
                                if(assigned instanceof CtFieldWrite) {
                                    Utils.addRelation(relation,
                                            Utils.encodeRelationNodeName(RelationType.F2M,
                                                    new Object[]{((CtFieldWrite) assigned).getVariable().getDeclaringType().getQualifiedName(), ((CtFieldWrite) assigned).getVariable().getSimpleName()},
                                                    new Object[]{((CtInvocation) assignment).getExecutable().getDeclaringType().getQualifiedName(), ((CtInvocation) assignment).getExecutable().getSignature(), KEY_RETURN}));
                                }
                                else if(assigned instanceof CtVariableWrite) {
                                    Utils.addRelation(relation,
                                            Utils.encodeRelationNodeName(RelationType.M2M,
                                                    new Object[]{c.getQualifiedName(), m.getSignature(), ((CtVariableWrite) assigned).getVariable().getSimpleName()},
                                                    new Object[]{((CtInvocation) assignment).getExecutable().getDeclaringType().getQualifiedName(), ((CtInvocation) assignment).getExecutable().getSignature(), KEY_RETURN}));
                                }
                            }
                        }
                        // 3. method invocation
                        if(s instanceof CtInvocation) {
                            CtExecutableReference executable = ((CtInvocation) s).getExecutable();
                            // declared parameters
                            List<CtParameter> params = executable.getExecutableDeclaration().getParameters();
                            // actual parameters
                            List<CtExpression> arguments = ((CtInvocation) s).getArguments();
                            if(arguments != null) {
                                for(int i = 0; i < arguments.size(); i++) {
                                    CtExpression exp = arguments.get(i);
                                    CtParameter param = params.get(i);
                                    // 1.argument is a field, add a relation of method-to-field
                                    if(exp instanceof CtFieldRead) {
                                        Utils.addRelation(relation,
                                                Utils.encodeRelationNodeName(RelationType.M2F,
                                                        new Object[]{executable.getDeclaringType().getQualifiedName(), executable.getSignature(), param.getSimpleName()},
                                                        new Object[]{((CtFieldRead) exp).getVariable().getDeclaringType().getQualifiedName(), ((CtFieldRead) exp).getVariable().getSimpleName()}));
                                    }
                                    // 2.argument is a var, add a relation of method-to-method
                                    else if(exp instanceof CtVariableRead) {
                                        Utils.addRelation(relation,
                                                Utils.encodeRelationNodeName(RelationType.M2M,
                                                        new Object[]{executable.getDeclaringType().getQualifiedName(), executable.getSignature(), param.getSimpleName()},
                                                        new Object[]{c.getQualifiedName(), m.getSignature(), ((CtVariableRead) exp).getVariable().getSimpleName()}));
                                    }
                                    // 3. argument is a method call, add a relation of method-to-method
                                    else if (exp instanceof CtInvocation) {
                                        CtExecutableReference executable2 = ((CtInvocation) exp).getExecutable();
                                        Utils.addRelation(relation,
                                                Utils.encodeRelationNodeName(RelationType.M2M,
                                                        new Object[]{executable.getDeclaringType().getQualifiedName(), executable.getSignature(), param.getSimpleName()},
                                                        new Object[]{executable2.getDeclaringType().getQualifiedName(), executable2.getSignature(), KEY_RETURN}));
                                    }
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
                            Utils.addRelation(relation,
                                    Utils.encodeRelationNodeName(RelationType.M2F,
                                            new Object[]{c.getQualifiedName(), m.getSignature(), KEY_RETURN},
                                            new Object[]{((CtFieldRead) returnExp).getTarget().getType().getQualifiedName(), ((CtFieldRead) returnExp).getVariable().getSimpleName()}));
                        } else if(returnExp instanceof CtVariableRead) {
                            Set<String> varSites = methodMap.get(((CtVariableRead) returnExp).getVariable().getSimpleName());
                            returnSites.addAll(varSites);
                            Utils.addRelation(relation,
                                    Utils.encodeRelationNodeName(RelationType.M2M,
                                            new Object[]{c.getQualifiedName(), m.getSignature(), KEY_RETURN},
                                            new Object[]{c.getQualifiedName(), m.getSignature(), ((CtVariableRead) returnExp).getVariable().getSimpleName()}));
                        } else if(returnExp instanceof CtConstructorCall) {
                            returnSites.add(c.getQualifiedName() + ":" + returnExp.getPosition().getLine());
                        } else {
                            //TODO return of invocation
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
//                        Set locationSites = fieldMap.get(parent.isStatic() ? KEY_STATIC + ":" + parent.getSimpleName() : parent.getSimpleName());
                        Set locationSites = fieldMap.get(parent.getSimpleName());
                        locationSites.add(c.getQualifiedName() + ":" + call.getPosition().getLine());
                    }
                    // 2. ConstructorCall in method

                    // 3. ConstructorCall in AnonymousBlock
                }
            }
        }

}
