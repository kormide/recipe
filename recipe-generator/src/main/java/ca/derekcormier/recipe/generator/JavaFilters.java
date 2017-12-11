package ca.derekcormier.recipe.generator;

import ca.derekcormier.recipe.cookbook.Cookbook;
import ca.derekcormier.recipe.cookbook.CookbookUtils;
import ca.derekcormier.recipe.cookbook.PrimitiveType;
import liqp.filters.Filter;

public class JavaFilters {
    public static Filter createJavaTypeFilter(Cookbook cookbook) {
        return new Filter("javatype") {
            @Override
            public Object apply(Object value, Object... params) {
                if (CookbookUtils.isPrimitiveType(super.asString(value))) {
                    PrimitiveType type = PrimitiveType.fromAlias(super.asString(value));
                    switch (type) {
                        case BOOLEAN:
                            return "boolean";
                        case INTEGER:
                            return "int";
                        case STRING:
                            return "String";

                        default:
                            throw new RuntimeException("unknown data type '" + super.asString(value) + "'");
                    }
                }
                else if (CookbookUtils.isFlagType(super.asString(value))) {
                    return "boolean";
                }
                else if (CookbookUtils.isKnownType(cookbook, super.asString(value))) {
                    return super.asString(value);
                }

                throw new RuntimeException("unknown data type '" + super.asString(value) + "'");
            }
        };
    }

    public static Filter createJavaValueFilter(Cookbook cookbook) {
        return new Filter("javavalue") {
            @Override
            public Object apply(Object value, Object... params) {
                String strType = super.asString(params[0]);

                if (CookbookUtils.isKnownType(cookbook, strType)) {
                    if (CookbookUtils.isPrimitiveType(strType)) {
                        PrimitiveType type = PrimitiveType.fromAlias(strType);
                        switch (type) {
                            case BOOLEAN:
                                return super.asString(value);
                            case INTEGER:
                                return super.asString(value);
                            case STRING:
                                return "\"" + super.asString(value) + "\"";
                            default:
                                throw new RuntimeException("unknown data type '" + strType + "'");
                        }
                    } else if (CookbookUtils.isEnumType(cookbook, strType)) {
                        if (CookbookUtils.enumHasValue(cookbook, strType, super.asString(value))) {
                            return strType + "." + super.asString(value);
                        }

                        throw new RuntimeException("value '" + super.asString(value) + "' is not a member of enum '" + strType + "'");
                    }
                }

                throw new RuntimeException("unknown data type '" + super.asString(value) + "'");
            }
        };
    }
}
