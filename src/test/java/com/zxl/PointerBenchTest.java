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

    @Test
    public void testVar2VarAssignment() {
        Map methodPoints2Map = (Map) allPoints2Map.get("assignment.BasicAssignment").get(KEY_METHOD);
        Map<String, Set> var2varMethod = (Map<String, Set>) methodPoints2Map.get("var2var()");
        Set a1 = var2varMethod.get("a1");
        Set a2 = var2varMethod.get("a2");
        Assert.assertTrue(a1.containsAll(a2));

    }

    @Test
    public void testVar2FieldAssignment() {
        Map methodMap = (Map) allPoints2Map.get("assignment.BasicAssignment").get(KEY_METHOD);
        Map<String, Set> fieldMap = (Map<String, Set>) allPoints2Map.get("basic.ObjectB").get(KEY_FIELD);
        Map<String, Set> var2fieldMethod = (Map<String, Set>) methodMap.get("var2field()");
        Set a = var2fieldMethod.get("a");
        Set b = var2fieldMethod.get("b");
        Set f3 = fieldMap.get("f3");

        Assert.assertTrue(a.containsAll(f3));

    }

    @Test
    public void testVar2CallAssignment() {
        Map methodMap = (Map) allPoints2Map.get("assignment.BasicAssignment").get(KEY_METHOD);
        Map<String, Set> var2callMethod = (Map<String, Set>) methodMap.get("var2call()");
        Map<String, Set> getAMethod = (Map<String, Set>) ((Map)allPoints2Map.get("basic.ObjectB").get(KEY_METHOD)).get("getA()");
        Set a = var2callMethod.get("a");
        Set b = var2callMethod.get("b");
        Set callReturn = getAMethod.get(KEY_RETURN);

        Assert.assertTrue(a.containsAll(callReturn));

    }

    @Test
    public void testField2VarAssignment() {
        Map fieldSites = (Map) allPoints2Map.get("basic.ObjectB").get(KEY_FIELD);
        Set f3 = (Set) fieldSites.get("f3");
        Map methodMap = (Map) allPoints2Map.get("assignment.BasicAssignment").get(KEY_METHOD);
        Map<String, Set> field2varMethod = (Map) methodMap.get("field2var()");
        Set a = field2varMethod.get("a");

        Assert.assertTrue(f3.containsAll(a));
    }

    @Test
    public void testField2FieldAssignment() {
        // object-sensitive has not been implemented yet
        // so we could not test field-to-field assignment now.
        Map fieldSties1 = (Map) allPoints2Map.get("basic.ObjectA").get(KEY_FIELD);
        Set objc1 = (Set) fieldSties1.get("objc");

        Map fieldSties2 = (Map) allPoints2Map.get("basic.ObjectB").get(KEY_FIELD);
        Set objc2 = (Set) fieldSties1.get("objc");

        Assert.assertTrue(objc1.containsAll(objc2));
    }

    @Test
    public void testField2CallAssignment() {
        Map fieldSites = (Map) allPoints2Map.get("basic.ObjectB").get(KEY_FIELD);
        Set f3 = (Set) fieldSites.get("f3");
        Map methodMap = (Map) allPoints2Map.get("basic.ObjectB").get(KEY_METHOD);
        Map<String, Set> getAMethod = (Map<String, Set>) methodMap.get("getA()");
        Set callReturn  = getAMethod.get(KEY_RETURN);

        Assert.assertTrue(f3.containsAll(callReturn));
    }

    @Test
    public void testVar2Param() {
        Map methodMap = (Map) allPoints2Map.get("assignment.BasicAssignment").get(KEY_METHOD);
        Map<String, Set> var2paramMethod = (Map<String, Set>) methodMap.get("var2param()");
        Set a = var2paramMethod.get("a");

        Map methodMap2 = (Map) allPoints2Map.get("basic.ObjectB").get(KEY_METHOD);
        Map<String, Set> setAMethod = (Map<String, Set>) methodMap2.get("setA(basic.ObjectA)");
        Set objAParam = setAMethod.get("objA");

        Assert.assertTrue(objAParam.containsAll(a));
    }

    @Test
    public void testField2Param() {
        Map fieldSites = (Map) allPoints2Map.get("basic.ObjectB").get(KEY_FIELD);
        Set f3 = (Set) fieldSites.get("f3");

        Map methodMap = (Map) allPoints2Map.get("basic.ObjectB").get(KEY_METHOD);
        Map<String, Set> setAMethod = (Map<String, Set>) methodMap.get("setA(basic.ObjectA)");
        Set objAParam = setAMethod.get("objA");

        Assert.assertTrue(objAParam.containsAll(f3));
    }

    @Test
    public void testCall2Param() {
        Map methodMap = (Map) allPoints2Map.get("basic.ObjectB").get(KEY_METHOD);
        Map<String, Set> setAMethod = (Map<String, Set>) methodMap.get("setA(basic.ObjectA)");
        Map<String, Set> getAMethod = (Map<String, Set>) methodMap.get("getA()");
        Set getAReturn = getAMethod.get(KEY_RETURN);
        Set objAParam = setAMethod.get("objA");

        Assert.assertTrue(objAParam.containsAll(getAReturn));
    }

    @Test
    public void test3PartyLib() {

    }
}