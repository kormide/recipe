package ca.derekcormier.recipe.parser;

public class Property {
    private String name;
    private boolean required;
    private String type;

    public Property(String name, String type, boolean required) {
        this.name = name;
        this.type = type;
        this.required = required;
    }

    public String getName() {
        return name;
    }

    public boolean isRequired() {
        return required;
    }

    public String getType() {
        return type;
    }
}
