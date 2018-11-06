package ca.derekcormier.recipe.generator.filter;

import java.util.Map;

import ca.derekcormier.recipe.cookbook.Cookbook;
import liqp.filters.Filter;

public class JavaParamFilter extends RecipeFilter {
    private final Filter typeFilter;

    public JavaParamFilter(Cookbook cookbook, Filter typeFilter) {
        super("javaparam", cookbook);
        this.typeFilter = typeFilter;
    }

    @Override
    public Object apply(Object value, Object... params) {
        Map<String,Object> param = (Map<String,Object>)value;
        String name = (String)param.get("name");

        boolean varargAsArray = params.length > 0 && super.asBoolean(params[0]);
        return typeFilter.apply(param.get("type"), varargAsArray)  + " " + name;
    }
}
