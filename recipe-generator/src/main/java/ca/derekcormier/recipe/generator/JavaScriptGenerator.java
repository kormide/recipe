package ca.derekcormier.recipe.generator;

import ca.derekcormier.recipe.cookbook.Cookbook;
import ca.derekcormier.recipe.generator.filter.JsParamFilter;
import ca.derekcormier.recipe.generator.filter.JsValueFilter;
import ca.derekcormier.recipe.generator.filter.TsIdentifierFilter;
import java.util.Arrays;
import java.util.List;
import liqp.filters.Filter;

public abstract class JavaScriptGenerator extends Generator {
  public JavaScriptGenerator(Cookbook cookbook) {
    super(cookbook);
  }

  @Override
  protected List<Filter> getTemplateFilters() {
    Filter identifierFilter = new TsIdentifierFilter();

    return Arrays.asList(
        identifierFilter,
        new JsParamFilter(getCookbook(), identifierFilter),
        new JsValueFilter(getCookbook()));
  }
}
