export abstract class Ingredient {
    protected readonly properties: Map<string,any> = new Map();
    private readonly ingredientType: string;
    private readonly domain: string | null;

    protected constructor(
        type: string,
        domain?: string
    ) {
        this.ingredientType = type;
        this.domain = domain ? domain : null;
    }

    public getIngredientType(): string {
        return this.ingredientType;
    }

    public getDomain(): string | null {
        return this.domain;
    }

    protected setRequired(name: string, value: any) {
        this.properties.set(name, value);
    }

    protected setOptional(name: string, repeatable: boolean, value: any) {
        if (!repeatable) {
            this.properties.set(name, value);
        }
        else {
            const values = this.properties.has(name) ? <any[]>this.properties.get(name) : [];
            values.push(value);
            this.properties.set(name, values);
        }
    }

    protected setCompoundOptional(name: string, repeatable: boolean, ...keyValuePairs: any[]) {
        if (keyValuePairs.length % 2 !== 0) {
            throw new Error("must have an even number of key-value pairs for compound optional");
        }

        for (let i = 0; i < keyValuePairs.length; i += 2) {
            if (typeof keyValuePairs[i] !== "string") {
                throw new Error("key in compound optional is not a string");
            }
        }

        if (!repeatable) {
            const map = this.properties.has(name) ? this.properties.get(name) : {};
            for (let i = 0; i < keyValuePairs.length; i += 2) {
                map[keyValuePairs[i]] = keyValuePairs[i+1];
            }
            this.properties.set(name, map);
        }
        else {
            const list: any[] = this.properties.has(name) ? <any[]>this.properties.get(name) : [];
            const map : {[key: string]: any} = {};
            for (let i = 0; i < keyValuePairs.length; i += 2) {
                map[keyValuePairs[i]] = keyValuePairs[i+1];
            }
            list.push(map);
            this.properties.set(name, list);
        }
    }

    public toJSON() {
        const jsonObj: any = {};
        jsonObj[this.ingredientType] = this.properties;

        for (const entry of this.properties) {
            jsonObj[this.ingredientType][entry[0]] = entry[1];
        }

        return jsonObj;
    }
}