package ca.derekcormier.recipe.generator;

import ca.derekcormier.recipe.cookbook.Cookbook;
import ca.derekcormier.recipe.generator.filter.JsParamFilter;
import ca.derekcormier.recipe.generator.filter.JsValueFilter;
import ca.derekcormier.recipe.generator.filter.TsIdentifierFilter;
import liqp.filters.Filter;

public abstract class JavaScriptGenerator extends Generator {
    public JavaScriptGenerator(Cookbook cookbook) {
        super(cookbook);
        Filter identifierFilter = new TsIdentifierFilter();
        Filter.registerFilter(new JsParamFilter(cookbook, identifierFilter));
        Filter.registerFilter(new JsValueFilter(cookbook));
        Filter.registerFilter(identifierFilter);
    }
}
