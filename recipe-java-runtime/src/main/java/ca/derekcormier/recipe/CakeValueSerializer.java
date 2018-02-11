package ca.derekcormier.recipe;

public abstract class CakeValueSerializer<T,R> {
    private Class<T> serializeType;
    public CakeValueSerializer(Class<T> serializeType) {
        this.serializeType = serializeType;
    }

    public Class<T> getSerializeType() {
        return serializeType;
    }

    public abstract R serialize(T value);
    public abstract T deserialize(Class<? extends T> clazz, R value);
}
