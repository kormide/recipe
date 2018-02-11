package ca.derekcormier.recipe;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class AbstractOven {
    private List<CakeValueSerializer> serializers = new ArrayList<>();

    public List<CakeValueSerializer> getSerializers() {
        return serializers;
    }

    public void addCakeValueSerializer(CakeValueSerializer serializer) {
        Objects.requireNonNull(serializer);
        this.serializers.add(serializer);
    }
}
