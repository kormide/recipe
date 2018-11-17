package ca.derekcormier.recipe.generator;

import java.util.Arrays;
import java.util.List;

import ca.derekcormier.recipe.cookbook.Cookbook;
import ca.derekcormier.recipe.generator.filter.JsValueFilter;
import ca.derekcormier.recipe.generator.filter.TsIdentifierFilter;
import ca.derekcormier.recipe.generator.filter.TsParamFilter;
import ca.derekcormier.recipe.generator.filter.TsTypeFilter;
import liqp.filters.Filter;

public abstract class TypeScriptGenerator extends Generator {
    public TypeScriptGenerator(Cookbook cookbook) {
        super(cookbook);
    }

    @Override
    protected List<Filter> getTemplateFilters() {
        Filter tsTypeFilter = new TsTypeFilter(getCookbook());
        Filter tsIdentifierFilter = new TsIdentifierFilter();

        return Arrays.asList(
            tsTypeFilter,
            tsIdentifierFilter,
            new TsParamFilter(getCookbook(), tsTypeFilter, tsIdentifierFilter),
            new JsValueFilter(getCookbook())
        );
    }
}
