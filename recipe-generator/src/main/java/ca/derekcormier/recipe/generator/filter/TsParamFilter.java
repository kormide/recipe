package ca.derekcormier.recipe.generator.filter;

import java.util.Map;

import ca.derekcormier.recipe.cookbook.Cookbook;
import ca.derekcormier.recipe.cookbook.CookbookUtils;
import ca.derekcormier.recipe.cookbook.type.ParamType;
import liqp.filters.Filter;

public class TsParamFilter extends RecipeFilter {
    private final Filter typeFiler;

    public TsParamFilter(Cookbook cookbook, Filter typeFilter) {
        super("tsparam", cookbook);
        this.typeFiler = typeFilter;
    }

    @Override
    public Object apply(Object value, Object... params) {
        Map<String,String> param = (Map<String,String>)value;
        String name = param.get("name");
        ParamType type = CookbookUtils.parseType(param.get("type"), getCookbook());

        return (type.isVararg() ? "..." : "") + name + ": " + typeFiler.apply(param.get("type"));
    }
}
