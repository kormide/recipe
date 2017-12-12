package ca.derekcormier.recipe;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class Ingredient extends BaseIngredient {
    public Ingredient(String type) {
        this(type, "");
    }

    public Ingredient(String type, String domain) {
        super(type, domain);
    }

    protected void setRequired(String name, Object value) {
        setProperty(name, value);
    }

    protected void setOptional(String name, boolean repeatable, Object value) {
        if (!repeatable) {
            setProperty(name, value);
        } else {
            List values = (List)getProperties().getOrDefault(name, new ArrayList());
            values.add(value);
            setProperty(name, values);
        }
    }

    protected void setCompoundOptional(String name, boolean repeatable, Object...keyValuePairs) {
        Map map = (Map)getProperties().getOrDefault(name, new HashMap());

        if (keyValuePairs.length % 2 != 0) {
            throw new IllegalArgumentException("must have an even number of key-value pairs for compound optional");
        }

        for (int i = 0; i < keyValuePairs.length; i += 2) {
            if (!(keyValuePairs[i] instanceof  String)) {
                throw new IllegalArgumentException("key in compound optional is not a string");
            }

            if (!repeatable) {
                map.put(keyValuePairs[i], keyValuePairs[i+1]);
            } else {
                List values = (List)map.getOrDefault(keyValuePairs[i], new ArrayList<>());
                values.add(keyValuePairs[i+1]);
                map.put(keyValuePairs[i], values);
            }
        }

        setProperty(name, map);
    }
}