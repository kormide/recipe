package ca.derekcormier.recipe.generator.filter;

import java.util.Map;

import ca.derekcormier.recipe.cookbook.Cookbook;
import ca.derekcormier.recipe.cookbook.CookbookUtils;
import ca.derekcormier.recipe.cookbook.type.ParamType;
import liqp.filters.Filter;

public class TsParamFilter extends RecipeFilter {
    private final Filter typeFiler;
    private final Filter identifierFilter;

    public TsParamFilter(Cookbook cookbook, Filter typeFilter, Filter identifierFilter) {
        super("tsparam", cookbook);
        this.typeFiler = typeFilter;
        this.identifierFilter = identifierFilter;
    }

    @Override
    public Object apply(Object value, Object... params) {
        Map<String,String> param = (Map<String,String>)value;
        String name = param.get("name");
        ParamType type = CookbookUtils.parseType(param.get("type"), getCookbook());

        return (type.isVararg() ? "..." : "") + identifierFilter.apply(name) + ": " + typeFiler.apply(param.get("type"));
    }
}
