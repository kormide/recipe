package ca.derekcormier.recipe.generator.filter;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class TsIdentifierFilterTest {
    @Test
    public void testApply_sanitizesValueIfTypeScriptKeyword() {
        TsIdentifierFilter filter = new TsIdentifierFilter();

        assertEquals("_number", filter.apply("number"));
    }

    @Test
    public void testApply_doesNotSanitizeNonTypeScriptKeywords() {
        TsIdentifierFilter filter = new TsIdentifierFilter();

        assertEquals("foobar", filter.apply("foobar"));
    }
}
