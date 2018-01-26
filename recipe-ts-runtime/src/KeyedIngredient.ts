import { Ingredient } from "./Ingredient";

export class KeyedIngredient extends Ingredient {
    private key: string | null = null;

    public constructor(name: string, domain: string) {
        super(name, domain);
    }

    protected setKey(key: string | null) {
        this.key = key;
    }

    public getKey(): string | null {
        return this.key;
    }

    public keyed(key: string | null): KeyedIngredient {
        this.key = key;
        return this;
    }

    public toJSON() {
        const jsonObj = super.toJSON();
        if (this.key) {
            jsonObj[this.getIngredientType()].key = this.key;
        }

        return jsonObj;
    }
}