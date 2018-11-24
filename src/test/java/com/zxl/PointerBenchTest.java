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
        Assert.assertEquals(a1.size(), 2);
        Assert.assertEquals(a2.size(), 1);
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
        Assert.assertEquals(a.size(), 2);
        Assert.assertEquals(b.size(), 1);
        Assert.assertEquals(f3.size(), 1);
    }
}
