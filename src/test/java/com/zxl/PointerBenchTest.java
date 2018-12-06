package com.zxl;

import com.zxl.analyzer.Points2Analyzer;
import com.zxl.utils.Utils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.util.Map;
import java.util.Set;

import static com.zxl.analyzer.Points2Analyzer.KEY_FIELD;
import static com.zxl.analyzer.Points2Analyzer.KEY_METHOD;
import static com.zxl.analyzer.Points2Analyzer.KEY_RETURN;

public class PointerBenchTest {

    Points2Analyzer analyzer = null;
    Map<String, Map> allPoints2Map = null;

    @Before
    public void beforeTest() {
        analyzer = new Points2Analyzer();
        allPoints2Map = analyzer.process(Utils.getCurrentPath()+ File.separator + "benchmark", null, null);
    }

//    @Test
//    public void testVar2VarAssignment() {
//        Map methodPoints2Map = (Map) allPoints2Map.get("assignment.BasicAssignment").get(KEY_METHOD);
//        Map<String, Set> var2varMethod = (Map<String, Set>) methodPoints2Map.get("var2var()");
//        Set a1 = var2varMethod.get("a1");
//        Set a2 = var2varMethod.get("a2");
//        Assert.assertTrue(a1.containsAll(a2));
//
//    }
//
//    @Test
//    public void testVar2FieldAssignment() {
//        Map methodMap = (Map) allPoints2Map.get("assignment.BasicAssignment").get(KEY_METHOD);
//        Map<String, Set> fieldMap = (Map<String, Set>) allPoints2Map.get("basic.ObjectB").get(KEY_FIELD);
//        Map<String, Set> var2fieldMethod = (Map<String, Set>) methodMap.get("var2field()");
//        Set a = var2fieldMethod.get("a");
//        Set b = var2fieldMethod.get("b");
//        Set f3 = fieldMap.get("f3");
//
//        Assert.assertTrue(a.containsAll(f3));
//
//    }
//
//    @Test
//    public void testVar2CallAssignment() {
//        Map methodMap = (Map) allPoints2Map.get("assignment.BasicAssignment").get(KEY_METHOD);
//        Map<String, Set> var2callMethod = (Map<String, Set>) methodMap.get("var2call()");
//        Map<String, Set> getAMethod = (Map<String, Set>) ((Map)allPoints2Map.get("basic.ObjectB").get(KEY_METHOD)).get("getA()");
//        Set a = var2callMethod.get("a");
//        Set b = var2callMethod.get("b");
//        Set callReturn = getAMethod.get(KEY_RETURN);
//
//        Assert.assertTrue(a.containsAll(callReturn));
//
//    }
//
//    @Test
//    public void testField2VarAssignment() {
//        Map fieldSites = (Map) allPoints2Map.get("basic.ObjectB").get(KEY_FIELD);
//        Set f3 = (Set) fieldSites.get("f3");
//        Map methodMap = (Map) allPoints2Map.get("assignment.BasicAssignment").get(KEY_METHOD);
//        Map<String, Set> field2varMethod = (Map) methodMap.get("field2var()");
//        Set a = field2varMethod.get("a");
//
//        Assert.assertTrue(f3.containsAll(a));
//    }
//
//    @Test
//    public void testField2FieldAssignment() {
//        // object-sensitive has not been implemented yet
//        // so we could not test field-to-field assignment now.
//        Map fieldSties1 = (Map) allPoints2Map.get("basic.ObjectA").get(KEY_FIELD);
//        Set objc1 = (Set) fieldSties1.get("objc");
//
//        Map fieldSties2 = (Map) allPoints2Map.get("basic.ObjectB").get(KEY_FIELD);
//        Set objc2 = (Set) fieldSties1.get("objc");
//
//        Assert.assertTrue(objc1.containsAll(objc2));
//    }
//
//    @Test
//    public void testField2CallAssignment() {
//        Map fieldSites = (Map) allPoints2Map.get("basic.ObjectB").get(KEY_FIELD);
//        Set f3 = (Set) fieldSites.get("f3");
//        Map methodMap = (Map) allPoints2Map.get("basic.ObjectB").get(KEY_METHOD);
//        Map<String, Set> getAMethod = (Map<String, Set>) methodMap.get("getA()");
//        Set callReturn  = getAMethod.get(KEY_RETURN);
//
//        Assert.assertTrue(f3.containsAll(callReturn));
//    }
//
//    @Test
//    public void testVar2Param() {
//        Map methodMap = (Map) allPoints2Map.get("assignment.BasicAssignment").get(KEY_METHOD);
//        Map<String, Set> var2paramMethod = (Map<String, Set>) methodMap.get("var2param()");
//        Set a = var2paramMethod.get("a");
//
//        Map methodMap2 = (Map) allPoints2Map.get("basic.ObjectB").get(KEY_METHOD);
//        Map<String, Set> setAMethod = (Map<String, Set>) methodMap2.get("setA(basic.ObjectA)");
//        Set objAParam = setAMethod.get("objA");
//
//        Assert.assertTrue(objAParam.containsAll(a));
//    }
//
//    @Test
//    public void testField2Param() {
//        Map fieldSites = (Map) allPoints2Map.get("basic.ObjectB").get(KEY_FIELD);
//        Set f3 = (Set) fieldSites.get("f3");
//
//        Map methodMap = (Map) allPoints2Map.get("basic.ObjectB").get(KEY_METHOD);
//        Map<String, Set> setAMethod = (Map<String, Set>) methodMap.get("setA(basic.ObjectA)");
//        Set objAParam = setAMethod.get("objA");
//
//        Assert.assertTrue(objAParam.containsAll(f3));
//    }
//
//    @Test
//    public void testCall2Param() {
//        Map methodMap = (Map) allPoints2Map.get("basic.ObjectB").get(KEY_METHOD);
//        Map<String, Set> setAMethod = (Map<String, Set>) methodMap.get("setA(basic.ObjectA)");
//        Map<String, Set> getAMethod = (Map<String, Set>) methodMap.get("getA()");
//        Set getAReturn = getAMethod.get(KEY_RETURN);
//        Set objAParam = setAMethod.get("objA");
//
//        Assert.assertTrue(objAParam.containsAll(getAReturn));
//    }

    @Test
    public void test3PartyLib() {

    }

    @Test
    public void testAssignInVarInitial() {
        Map methodMap = (Map) allPoints2Map.get("assignment.BasicAssignment2").get(KEY_METHOD);
        Map<String, Set> varInitialMethod = (Map<String, Set>) methodMap.get("assignInLocalInitial(basic.ObjectA,basic.ObjectA[])");

        Map fieldSites = (Map) allPoints2Map.get("basic.ObjectB").get(KEY_FIELD);
        Set fieldF3 = (Set) fieldSites.get("f3");
        Set fieldArray = (Set) fieldSites.get("a_arr");

        Set a1 = varInitialMethod.get("a1");
        Set a2 = varInitialMethod.get("a2");
        Set a3 = varInitialMethod.get("a3");
        Set a4 = varInitialMethod.get("a4");
        Set a5 = varInitialMethod.get("a5");
        Set a6 = varInitialMethod.get("a6");
        Set a7 = varInitialMethod.get("a7");
        Set a8 = varInitialMethod.get("a8");
        Set a9 = varInitialMethod.get("a9");
        Set a10 = varInitialMethod.get("a10");
        Set paramA = varInitialMethod.get("paramA");
        Set paramArr = varInitialMethod.get("paramArr");

        Map objBMethods = (Map) allPoints2Map.get("basic.ObjectB").get(KEY_METHOD);
        Map<String, Set> getAMethod = (Map<String, Set>) objBMethods.get("getA()");
        Map<String, Set> getArrayMethod = (Map<String, Set>) objBMethods.get("getArray()");
        Set getAReturn = getAMethod.get(KEY_RETURN);
        Set getArrayReturn = getArrayMethod.get(KEY_RETURN);

        // assignment : "new" operation
        Assert.assertTrue(a1.contains("assignment.BasicAssignment2:10"));
        Assert.assertEquals(a1.size(), 1);
        Assert.assertTrue(a2.contains("assignment.BasicAssignment2:11"));
        Assert.assertEquals(a2.size(), 1);

        // assignment : local var
        Assert.assertTrue(a3.containsAll(a1));
        Assert.assertTrue(a4.containsAll(a2));

        // assignment : field
        Assert.assertTrue(a5.contains("assignment.BasicAssignment2:19"));
        Assert.assertTrue(a5.containsAll(fieldF3));
        Assert.assertTrue(a6.contains("assignment.BasicAssignment2:20"));
        Assert.assertTrue(a6.containsAll(fieldArray));

        // assignment : parameter
        Assert.assertTrue(a7.containsAll(paramA));
        Assert.assertTrue(a8.containsAll(paramArr));

        // assignment : method invocation
        Assert.assertTrue(a9.containsAll(getAReturn));
        Assert.assertTrue(a10.containsAll(getArrayReturn));

    }

    @Test
    public void testCommonAssignment() {
        Map methodMap = (Map) allPoints2Map.get("assignment.BasicAssignment2").get(KEY_METHOD);
        Map<String, Set> commonAssignMethod = (Map<String, Set>) methodMap.get("commonAssignment(basic.ObjectA,basic.ObjectA,basic.ObjectA[],basic.ObjectA[])");

        Map fieldSites = (Map) allPoints2Map.get("basic.ObjectB").get(KEY_FIELD);
        Set fieldF3 = (Set) fieldSites.get("f3");
        Set fieldArray = (Set) fieldSites.get("a_arr");

        Set locA = commonAssignMethod.get("locA");
        Set locArr = commonAssignMethod.get("locArr");
        Set paramA = commonAssignMethod.get("paramA");
        Set paramArr = commonAssignMethod.get("paramArr");

        // assignment : "new" operation
        Assert.assertTrue(locA.contains("assignment.BasicAssignment2:38"));
        Assert.assertTrue(locArr.contains("assignment.BasicAssignment2:39"));
        Assert.assertTrue(paramA.contains("assignment.BasicAssignment2:40"));
        Assert.assertTrue(paramArr.contains("assignment.BasicAssignment2:41"));
        Assert.assertTrue(fieldF3.contains("assignment.BasicAssignment2:42"));
        Assert.assertTrue(fieldArray.contains("assignment.BasicAssignment2:43"));

        // assignment : local var
        Set locA2 = commonAssignMethod.get("locA2");
        Set locArr2 = commonAssignMethod.get("locArr2");
        Assert.assertTrue(locA.containsAll(locA2));
        Assert.assertTrue(locArr.containsAll(locArr2));
        Assert.assertTrue((paramA.containsAll(locA2)));
        Assert.assertTrue(paramArr.containsAll(locArr2));
        Assert.assertTrue(fieldF3.containsAll(locA2));
        Assert.assertTrue(fieldArray.containsAll(locArr2));

        // assignment : field
        Assert.assertTrue(locA.containsAll(fieldF3));
        Assert.assertTrue(locArr.containsAll(fieldArray));
        Assert.assertTrue(paramA.containsAll(fieldF3));
        Assert.assertTrue(paramArr.containsAll(fieldArray));

        // assignment : parameter
        Set paramA2 = commonAssignMethod.get("paramA2");
        Set paramArr2 = commonAssignMethod.get("paramArr2");
        Assert.assertTrue(locA.containsAll(paramA2));
        Assert.assertTrue(locArr.containsAll(paramArr2));
        Assert.assertTrue(paramA.containsAll(paramA2));
        Assert.assertTrue(paramArr.containsAll(paramArr2));
        Assert.assertTrue(fieldF3.containsAll(paramA2));
        Assert.assertTrue(fieldArray.containsAll(paramArr2));

        // assignment : method invocation
        Map objBMethods = (Map) allPoints2Map.get("basic.ObjectB").get(KEY_METHOD);
        Map<String, Set> getAMethod = (Map<String, Set>) objBMethods.get("getA()");
        Map<String, Set> getArrayMethod = (Map<String, Set>) objBMethods.get("getArray()");
        Set getAReturn = getAMethod.get(KEY_RETURN);
        Set getArrayReturn = getArrayMethod.get(KEY_RETURN);
        Assert.assertTrue(locA.containsAll(getAReturn));
        Assert.assertTrue(locArr.containsAll(getArrayReturn));
        Assert.assertTrue(paramA.containsAll(getAReturn));
        Assert.assertTrue(paramArr.containsAll(getArrayReturn));
        Assert.assertTrue(fieldF3.containsAll(getAReturn));
        Assert.assertTrue(fieldArray.containsAll(getArrayReturn));

    }

    @Test
    public void testArgumentPass() {
        Map methodMap = (Map) allPoints2Map.get("assignment.BasicAssignment2").get(KEY_METHOD);
        Map<String, Set> methodInvocationMethod = (Map<String, Set>) methodMap.get("methodInvocation(basic.ObjectA,basic.ObjectA[])");
        Map objBMethods = (Map) allPoints2Map.get("basic.ObjectB").get(KEY_METHOD);
        Map<String, Set> setAMethod = (Map<String, Set>) objBMethods.get("setA(basic.ObjectA)");
        Map<String, Set> setArrayMethod = (Map<String, Set>) objBMethods.get("setArray(basic.ObjectA[])");
        Set setAParam = setAMethod.get("objA");
        Set setArrayParam = setArrayMethod.get("array");

        // argument : local var
        Set locA = methodInvocationMethod.get("locA");
        Set locArr = methodInvocationMethod.get("locArr");
        Assert.assertTrue(setAParam.containsAll(locA));
        Assert.assertTrue(setArrayParam.containsAll(locArr));

        // argument : parameter
        Set paramA = methodInvocationMethod.get("paramA");
        Set paramArr = methodInvocationMethod.get("paramArr");
        Assert.assertTrue(setAParam.containsAll(paramA));
        Assert.assertTrue(setArrayParam.containsAll(paramArr));

        // argument : field
        Map fieldSites = (Map) allPoints2Map.get("basic.ObjectB").get(KEY_FIELD);
        Set fieldF3 = (Set) fieldSites.get("f3");
        Set fieldArray = (Set) fieldSites.get("a_arr");
        Assert.assertTrue(setAParam.containsAll(fieldF3));
        Assert.assertTrue(setArrayParam.containsAll(fieldArray));

        // argument : method invocation
        Map<String, Set> getAMethod = (Map<String, Set>) objBMethods.get("getA()");
        Map<String, Set> getArrayMethod = (Map<String, Set>) objBMethods.get("getArray()");
        Set getAReturn = getAMethod.get(KEY_RETURN);
        Set getArrayReturn = getArrayMethod.get(KEY_RETURN);
        Assert.assertTrue(setAParam.containsAll(getAReturn));
        Assert.assertTrue(setArrayParam.containsAll(getArrayReturn));
    }
}