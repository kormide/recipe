package ca.derekcormier.recipe.generator.filter;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import ca.derekcormier.recipe.cookbook.Cookbook;
import liqp.filters.Filter;

@RunWith(MockitoJUnitRunner.class)
public class JavaParamFilterTest {
    @Mock
    private JavaTypeFilter javaTypeFilter;

    @Test
    public void testApply_combinesTypeFromTypeFilterWithName() {
        Cookbook cookbook = new Cookbook(new ArrayList<>(), new ArrayList<>());
        Filter filter = new JavaParamFilter(cookbook, javaTypeFilter);

        Map param = new HashMap();
        param.put("name", "foo");
        param.put("type", "int[]");

        when(javaTypeFilter.apply(param.get("type"), false)).thenReturn("type");

        assertEquals("type foo", filter.apply(param));
    }

    @Test
    public void testApply_passesCollapseVarargOptionToTypeFilter() {
        Cookbook cookbook = new Cookbook(new ArrayList<>(), new ArrayList<>());
        Filter filter = new JavaParamFilter(cookbook, javaTypeFilter);

        Map param = new HashMap();
        param.put("name", "foo");
        param.put("type", "int[]");

        filter.apply(param, true);
        verify(javaTypeFilter).apply(param.get("type"), true);
    }
}
