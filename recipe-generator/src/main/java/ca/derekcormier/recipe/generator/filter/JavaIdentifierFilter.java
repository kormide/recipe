package ca.derekcormier.recipe.generator.filter;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import liqp.filters.Filter;

public class JavaIdentifierFilter extends Filter {;
    private static final Set<String> JAVA_KEYWORDS = new HashSet<>();

    static {
        Collections.addAll(JAVA_KEYWORDS, "abstract", "assert", "boolean", "break", "byte", "case", "catch", "char", "class", "const",
            "continue", "default", "do", "double", "else", "enum", "extends", "false", "final", "finally", "float", "for", "goto", "if",
            "implements", "import", "instanceof", "int", "interface", "long", "native", "new", "null", "package", "private", "protected",
            "public", "return", "short", "static", "strictfp", "super", "switch", "synchronized", "this", "throw", "throws", "transient",
            "true", "try", "void", "volatile", "while");
    }

    public JavaIdentifierFilter() {
        super("javaidentifier");
    }

    @Override
    public Object apply(Object value, Object... params) {
        return JAVA_KEYWORDS.contains(value) ? "_" + value : value;
    }
}
