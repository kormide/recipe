package ca.derekcormier.recipe.parser;

import java.util.ArrayList;
import java.util.List;

public class Enum {
    private String name;
    private List<String> values = new ArrayList<>();

    public Enum(String name) {
        this.name = name;
    }

    public void addValue(String value) {
        values.add(value);
    }

    public String getName() {
        return name;
    }

    public List<String> getValues() {
        return values;
    }
}
