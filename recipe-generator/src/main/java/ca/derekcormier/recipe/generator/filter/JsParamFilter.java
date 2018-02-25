package ca.derekcormier.recipe.generator.filter;

import java.util.Map;

import ca.derekcormier.recipe.cookbook.Cookbook;
import ca.derekcormier.recipe.cookbook.CookbookUtils;
import ca.derekcormier.recipe.cookbook.type.ParamType;

public class JsParamFilter extends RecipeFilter {
    public JsParamFilter(Cookbook cookbook) {
        super("jsparam", cookbook);
    }

    @Override
    public Object apply(Object value, Object... params) {
        Map<String,String> param = (Map<String,String>)value;
        String name = param.get("name");
        ParamType type = CookbookUtils.parseType(param.get("type"), getCookbook());

        return (type.isVararg() ? "..." : "") + name;
    }
}
