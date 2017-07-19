package ca.derekcormier.recipe.parser;

import java.util.ArrayList;
import java.util.List;

public class Domain {
    private List<Entity> entities = new ArrayList<>();
    private List<Enum> enums = new ArrayList<>();


    public void addEntity(Entity entity) {
        this.entities.add(entity);
    }

    public void addEnum(Enum enumeration) {
        this.enums.add(enumeration);
    }

    public List<Entity> getEntities() {
        return entities;
    }

    public List<Enum> getEnums() {
        return enums;
    }
}
