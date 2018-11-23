package com.zxl;

import com.zxl.analyzer.Points2Analyzer;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.Map;
import java.util.Set;

public class PointerBenchTest {

    Points2Analyzer analyzer = null;
    Map<String, Map> allPoints2Map = null;

    @Before
    public void beforeTest() {
        analyzer = new Points2Analyzer();
    }

    @Test
    public void testParameter1() {
//        allPoints2Map = analyzer.process("D:\\Code\\Java\\PointerBench\\src\\basic", null, null);
//        Map<String, Set> points2InMethodTest = (Map<String, Set>) ((Map)allPoints2Map.get("Parameter1").get("method")).get("test(benchmark.objects.A)");
//        Set b = points2InMethodTest.get("b");
//        Set x = points2InMethodTest.get("x");
//        Assert.assertTrue(b.containsAll(x));
    }
}
