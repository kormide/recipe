package ca.derekcormier.recipe.generator.filter;

import static org.junit.Assert.assertEquals;
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
public class TsParamFilterTest {
    @Mock
    private TsTypeFilter typeFilter;
    @Mock
    private TsIdentifierFilter identifierFilter;

    @Test
    public void testApply_combinesNameAndTypeFromFilters() {
        Cookbook cookbook = new Cookbook(new ArrayList<>(), new ArrayList<>());
        Filter filter = new TsParamFilter(cookbook, typeFilter, identifierFilter);
        when(typeFilter.apply("int[]")).thenReturn("number[]");
        when(identifierFilter.apply("foo")).thenReturn("bar");

        assertEquals("bar: number[]", filter.apply(param("foo", "int[]")));
    }

    @Test
    public void testApply_addsEllipsisToNameWhenVararg() {
        Cookbook cookbook = new Cookbook(new ArrayList<>(), new ArrayList<>());
        Filter filter = new TsParamFilter(cookbook, typeFilter, identifierFilter);
        when(typeFilter.apply("int...")).thenReturn("number[]");
        when(identifierFilter.apply("foo")).thenReturn("foo");

        assertEquals("...foo: number[]", filter.apply(param("foo", "int...")));
    }

    private Map param(String name, String type) {
        Map param = new HashMap();
        param.put("name", name);
        param.put("type", type);
        return param;
    }
}
