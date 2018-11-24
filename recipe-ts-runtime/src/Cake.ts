export class Cake {
    public static readonly SEPARATOR = ".";
    private entries: {[key: string]: any};
    private readonly prefixStack: string[] = [];

    public static key(...subKeys: string[]): string {
        if (subKeys.length === 0) {
            throw new Error("cannot form cake key; no keys supplied");
        }

        subKeys.forEach(Cake.validateKey);
        return subKeys.join(Cake.SEPARATOR);
    }

    private static validateKey(key: string | null) {
        if (key === null || key === "") {
            throw new Error("keys cannot be empty");
        }

        if (key.includes(Cake.SEPARATOR)) {
            throw new Error("keys cannot contain the namespace separator: " + Cake.SEPARATOR);
        }
    }

    public constructor(other?: Cake) {
        this.entries = other ? {...other.entries} : {};
    }

    public publish(key: string, value: any) {
        this.getSubKeysAndValidateFullKey(key);
        const newKey = this.getPrefixWithSeparator(this.prefixStack) + key;
        this.entries[newKey] = value;
    }

    public inNamespace(key: string, runnable: () => void) {
        const keys = this.getSubKeysAndValidateFullKey(key);
        keys.forEach(k => this.prefixStack.push(k));

        try {
            runnable();
        }
        finally {
            keys.forEach(k => this.prefixStack.pop());
        }
    }

    private getSubKeysAndValidateFullKey(fullKey: string): string[] {
        const keys = fullKey.split(Cake.SEPARATOR);
        if (keys.filter(k => k === "").length > 0) {
            throw new Error("cannot publish value for empty key");
        }

        keys.forEach(Cake.validateKey);
        return keys;
    }

    public get<T>(...key: Array<string|null>): T {
        if (key.length === 0) {
            throw new Error("cannot get value for empty key");
        }
        key = key.reduce((a, v) => a.concat(v ? v.split(Cake.SEPARATOR) : [v]), new Array<string|null>());

        key.forEach(Cake.validateKey);

        const fullKey = key.join(Cake.SEPARATOR);

        // search within current namespace
        let searchKey = this.getPrefixWithSeparator(this.prefixStack) + fullKey;
        if (searchKey in this.entries) {
            return <T>this.entries[searchKey];
        }

        // search within each ancestor namespace up to the root
        const namespaces = this.prefixStack.slice();
        while (namespaces.length !== 0) {
            namespaces.pop();
            searchKey = this.getPrefixWithSeparator(namespaces) + fullKey;
            if (searchKey in this.entries) {
                return <T>this.entries[searchKey];
            }
        }

        // search within any other namespace (if unambiguous)
        const candidates: string[] = [];
        for (const k in this.entries) {
            const subkeys = k.split(Cake.SEPARATOR);
            if (key.length > subkeys.length) {
                continue;
            }
            let match = true;
            for (let i = 0; i < key.length && match; i++) {
                if (key[key.length - 1 - i] !== subkeys[subkeys.length - 1 - i]) {
                    match = false;
                }
            }
            if (match) {
                candidates.push(k);
            }
        }

        if (candidates.length === 1) {
            return <T>this.entries[candidates[0]];
        }
        else if (candidates.length === 0) {
            throw new Error("cake does not contain key '" + fullKey + "'");
        }
        else {
            throw new Error("cannot retrieve ambiguous key '" + fullKey + "'");
        }
    }

    private getPrefixWithSeparator(namespaces: string[]): string {
        return namespaces.join(Cake.SEPARATOR) + (namespaces.length > 0 ? Cake.SEPARATOR : "");
    }

    public getPublishedKeyForValue(value: any, fullyQualified: boolean): string {
        const matchingKeys: string[] = [];
        for (const k in this.entries) {
            if (this.entries[k] === value) {
                matchingKeys.push(k);
            }
        }
        if (matchingKeys.length === 1) {
            if (fullyQualified) {
                return matchingKeys[0];
            }
            const splitKey = matchingKeys[0].split(Cake.SEPARATOR);
            return splitKey[splitKey.length - 1];
        }
        if (matchingKeys.length > 1) {
            throw new Error("multiple keys found for object " + value);
        }
        throw new Error("no key found for object " + value);
    }

    public getNamespace(): string {
        return this.prefixStack.join(Cake.SEPARATOR);
    }

    public hasContext(): boolean {
        try {
            this.getContext();
            return true;
        }
        catch (e) {
            return false;
        }
    }

    public getContext<T>(): T {
        if (this.prefixStack.length === 0) {
            throw new Error("cannot get context in root namespace");
        }
        else {
            let prefix = this.getPrefixWithSeparator(this.prefixStack);
            prefix = prefix.substr(0, prefix.length - 1);

            if (!(prefix in this.entries)) {
                throw new Error(`cake does not contain context value for namespace ${prefix}`);
            }
            return <T>this.entries[prefix];
        }
    }

    public getOrGetContext<T>(...key: Array<string|null>): T {
        try {
            return this.get(...key);
        }
        catch (e) {
            return this.getContext();
        }
    }

    public toJSON() {
        return this.entries;
    }

    public static fromJSON(json: any): Cake {
        const cake = new Cake();
        cake.entries = json || {};
        return cake;
    }
}