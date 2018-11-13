package ca.derekcormier.recipe.generator;

import ca.derekcormier.recipe.cookbook.Cookbook;
import ca.derekcormier.recipe.generator.filter.JsValueFilter;
import ca.derekcormier.recipe.generator.filter.TsIdentifierFilter;
import ca.derekcormier.recipe.generator.filter.TsParamFilter;
import ca.derekcormier.recipe.generator.filter.TsTypeFilter;
import liqp.filters.Filter;

public abstract class TypeScriptCookbookGenerator extends CookbookGenerator {
    public TypeScriptCookbookGenerator(Cookbook cookbook) {
        super(cookbook);

        Filter tsTypeFilter = new TsTypeFilter(cookbook);
        Filter tsIdentifierFilter = new TsIdentifierFilter();
        Filter.registerFilter(tsTypeFilter);
        Filter.registerFilter(new TsParamFilter(cookbook, tsTypeFilter, tsIdentifierFilter));
        Filter.registerFilter(new JsValueFilter(cookbook));
        Filter.registerFilter(tsIdentifierFilter);
    }
}
