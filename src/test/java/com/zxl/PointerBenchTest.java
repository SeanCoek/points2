package com.zxl;

import com.zxl.analyzer.Points2Analyzer;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.Map;
import java.util.Set;

import static com.zxl.analyzer.Points2Analyzer.KEY_METHOD;

public class PointerBenchTest {

    Points2Analyzer analyzer = null;
    Map<String, Map> allPoints2Map = null;

    @Before
    public void beforeTest() {
        analyzer = new Points2Analyzer();
    }

    @Test
    public void testAssignment() {
        allPoints2Map = analyzer.process("D:\\IdeaProjects\\points2\\benchmark", null, null);
        Map methodPoints2Map = (Map) allPoints2Map.get("assignment.BasicAssignment").get(KEY_METHOD);
        Map<String, Set> var2varMethod = (Map<String, Set>) methodPoints2Map.get("var2var()");
        Set a1 = var2varMethod.get("a1");
        Set a2 = var2varMethod.get("a2");
        Assert.assertTrue(a1.containsAll(a2));
        Assert.assertEquals(a1.size(), 2);
        Assert.assertEquals(a2.size(), 1);
    }
}
