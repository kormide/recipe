package ca.derekcormier.recipe.generator;

import java.util.Arrays;
import java.util.List;

import ca.derekcormier.recipe.cookbook.Cookbook;
import ca.derekcormier.recipe.generator.filter.JavaGetterFilter;
import ca.derekcormier.recipe.generator.filter.JavaIdentifierFilter;
import ca.derekcormier.recipe.generator.filter.JavaParamFilter;
import ca.derekcormier.recipe.generator.filter.JavaTypeFilter;
import ca.derekcormier.recipe.generator.filter.JavaValueFilter;
import liqp.filters.Filter;

public abstract class JavaGenerator extends Generator {
    public JavaGenerator(Cookbook cookbook) {
        super(cookbook);
    }

    @Override
    protected List<Filter> getTemplateFilters() {
        Filter javaTypeFilter = new JavaTypeFilter(getCookbook());
        Filter javaIdentifierFilter = new JavaIdentifierFilter();

        return Arrays.asList(
            javaTypeFilter,
            javaIdentifierFilter,
            new JavaParamFilter(getCookbook(), javaTypeFilter, javaIdentifierFilter),
            new JavaValueFilter(getCookbook()),
            new JavaGetterFilter()
        );
    }
}
