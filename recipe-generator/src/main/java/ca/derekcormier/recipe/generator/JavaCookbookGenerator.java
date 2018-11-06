package ca.derekcormier.recipe.generator;

import ca.derekcormier.recipe.cookbook.Cookbook;
import ca.derekcormier.recipe.generator.filter.JavaParamFilter;
import ca.derekcormier.recipe.generator.filter.JavaTypeFilter;
import ca.derekcormier.recipe.generator.filter.JavaValueFilter;
import liqp.filters.Filter;

public abstract class JavaCookbookGenerator extends CookbookGenerator {
    public JavaCookbookGenerator(Cookbook cookbook) {
        super(cookbook);

        Filter javaTypeFilter = new JavaTypeFilter(cookbook);
        Filter.registerFilter(javaTypeFilter);
        Filter.registerFilter(new JavaParamFilter(cookbook, javaTypeFilter));
        Filter.registerFilter(new JavaValueFilter(cookbook));
    }
}
