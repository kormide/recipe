package ca.derekcormier.recipe.generator.filter;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import ca.derekcormier.recipe.cookbook.Cookbook;
import liqp.filters.Filter;

public class JsParamFilterTest {
    @Test
    public void testApply_outputsParamName() {
        Cookbook cookbook = new Cookbook("", new ArrayList<>(), new ArrayList<>());
        Filter filter = new JsParamFilter(cookbook);

        assertEquals("foo", filter.apply(param("foo", "string")));
    }

    @Test
    public void testApply_outputsEllipsesWhenParamTypeIsVararg() {
        Cookbook cookbook = new Cookbook("", new ArrayList<>(), new ArrayList<>());
        Filter filter = new JsParamFilter(cookbook);

        assertEquals("...foo", filter.apply(param("foo", "int...")));
    }

    private Map param(String name, String type) {
        Map param = new HashMap();
        param.put("name", name);
        param.put("type", type);
        return param;
    }
}
