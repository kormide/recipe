package ca.derekcormier.recipe.generator.filter;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import liqp.filters.Filter;

public class TsIdentifierFilter extends Filter {
    public static final Set<String> TYPESCRIPT_KEYWORDS = new HashSet<>();

    static {
        Collections.addAll(TYPESCRIPT_KEYWORDS, "any", "as", "boolean", "break", "case", "catch", "class", "const",
            "constructor", "continue", "debugger", "declare", "default", "delete", "do", "else", "enum", "export", "extends", "false",
            "finally", "for", "from", "function", "get", "if", "implements", "import", "in", "instanceof", "interface", "let", "module",
            "new", "null", "number", "of", "package", "private", "protected", "public", "require", "return", "set", "static", "string",
            "super", "switch", "symbol", "this", "throw", "true", "try", "type", "typeof", "undefined", "var", "void", "while", "with", "yield");
    };

    public TsIdentifierFilter() {
        super("tsidentifier");
    }

    @Override
    public Object apply(Object value, Object... params) {
        return TYPESCRIPT_KEYWORDS.contains(value) ? "_" + value : value;
    }
}
