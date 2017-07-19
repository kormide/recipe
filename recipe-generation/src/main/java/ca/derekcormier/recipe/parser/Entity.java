package ca.derekcormier.recipe.parser;

import java.util.ArrayList;
import java.util.List;

public class Entity {
    private String name;
    private List<Property> properties = new ArrayList<>();

    public Entity(String name) {
        this.name = name;
    }

    public void addProperty(Property property) {
        this.properties.add(property);
    }

    public String getName() {
        return name;
    }

    public List<Property> getProperties() {
        return properties;
    }
}
