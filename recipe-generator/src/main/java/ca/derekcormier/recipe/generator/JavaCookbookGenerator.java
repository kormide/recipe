package ca.derekcormier.recipe.generator;

import ca.derekcormier.recipe.cookbook.Cookbook;
import ca.derekcormier.recipe.generator.filter.JavaGetterFilter;
import ca.derekcormier.recipe.generator.filter.JavaIdentifierFilter;
import ca.derekcormier.recipe.generator.filter.JavaParamFilter;
import ca.derekcormier.recipe.generator.filter.JavaTypeFilter;
import ca.derekcormier.recipe.generator.filter.JavaValueFilter;
import liqp.filters.Filter;

public abstract class JavaCookbookGenerator extends CookbookGenerator {
    public JavaCookbookGenerator(Cookbook cookbook) {
        super(cookbook);

        Filter javaTypeFilter = new JavaTypeFilter(cookbook);
        Filter javaIdentifierFilter = new JavaIdentifierFilter();
        Filter.registerFilter(javaTypeFilter);
        Filter.registerFilter(javaIdentifierFilter);
        Filter.registerFilter(new JavaParamFilter(cookbook, javaTypeFilter, javaIdentifierFilter));
        Filter.registerFilter(new JavaValueFilter(cookbook));
        Filter.registerFilter(new JavaGetterFilter());
    }
}
