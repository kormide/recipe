package ca.derekcormier.recipe.generator.filter;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;

import org.junit.Test;
import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import ca.derekcormier.recipe.cookbook.Cookbook;
import liqp.filters.Filter;

public class TsParamFilterTest {
    @Test
    public void testApply_separatesNameAndType() {
        Cookbook cookbook = new Cookbook("", new ArrayList<>(), new ArrayList<>());
        Filter filter = new TsParamFilter(cookbook, new TsTypeFilter(cookbook));

        assertEquals("foo: number[]", filter.apply(param("foo", "int[]")));
    }

    @Test
    public void testApply_addsEllipsisToNameWhenVararg() {
        Cookbook cookbook = new Cookbook("", new ArrayList<>(), new ArrayList<>());
        Filter filter = new TsParamFilter(cookbook, new TsTypeFilter(cookbook));

        assertEquals("...foo: number[]", filter.apply(param("foo", "int...")));
    }

    @Test
    public void testApply_defersToTypeFilterForType() {
        Cookbook cookbook = new Cookbook("", new ArrayList<>(), new ArrayList<>());
        Filter mockTypeFilter = Mockito.mock(Filter.class);
        Mockito.when(mockTypeFilter.apply(any())).thenReturn("convertedType");
        Filter filter = new TsParamFilter(cookbook, mockTypeFilter);

        assertEquals("foo: convertedType", filter.apply(param("foo", "string")));
    }

    private Map param(String name, String type) {
        Map param = new HashMap();
        param.put("name", name);
        param.put("type", type);
        return param;
    }
}
