package ca.derekcormier.recipe.generator.filter;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import liqp.filters.Filter;

public class JavaIdentifierFilterTest {
    @Test
    public void testApply_sanitizesValueIfJavaKeyword() {
        Filter filter = new JavaIdentifierFilter();

        assertEquals("_class", filter.apply("class"));
    }

    @Test
    public void testApply_doesNotSanitizeNonJavaKeywords() {
        Filter filter = new JavaIdentifierFilter();

        assertEquals("foobar", filter.apply("foobar"));
    }
}
