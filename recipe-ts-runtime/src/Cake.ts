export class Cake {
    public static readonly SEPARATOR = ".";
    private readonly entries = new Map<string, any>();
    private readonly prefixStack: string[] = [];

    public static key(...subKeys: string[]): string {
        if (subKeys.length === 0) {
            throw new Error("cannot form cake key; no keys supplied");
        }

        subKeys.forEach(Cake.validateKey);
        return subKeys.join(Cake.SEPARATOR);
    }

    private static validateKey(key: string) {
        if (key === "") {
            throw new Error("keys cannot be empty");
        }

        if (key.includes(Cake.SEPARATOR)) {
            throw new Error("keys cannot contain the namespace separator: " + Cake.SEPARATOR);
        }
    }

    public publish(key: string, value: any) {
        this.getSubKeysAndValidateFullKey(key);
        const newKey = this.getPrefixWithSeparator(this.prefixStack) + key;
        this.entries.set(newKey, value);
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

    public get<T>(...key: string[]): T {
        if (key.length === 0) {
            throw new Error("cannot get value for empty key");
        }

        key.forEach(Cake.validateKey);

        const fullKey = key.join(Cake.SEPARATOR);

        // search within current namespace
        let searchKey = this.getPrefixWithSeparator(this.prefixStack) + fullKey;
        if (this.entries.has(searchKey)) {
            return <T>this.entries.get(searchKey);
        }

        // search within each ancestor namespace up to the root
        const namespaces = this.prefixStack.slice();
        while (namespaces.length !== 0) {
            namespaces.pop();
            searchKey = this.getPrefixWithSeparator(namespaces) + fullKey;
            if (this.entries.has(searchKey)) {
                return <T>this.entries.get(searchKey);
            }
        }

        // search within any other namespace (if unambiguous)
        const candidates: string[] = [];
        for (const k of this.entries.keys()) {
            if (k.endsWith(fullKey)) {
                candidates.push(k);
            }
        }

        if (candidates.length === 1) {
            return <T>this.entries.get(candidates[0]);
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
        for (const e of this.entries) {
            if (e[1] === value) {
                matchingKeys.push(e[0]);
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
}