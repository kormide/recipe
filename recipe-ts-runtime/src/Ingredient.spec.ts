import { expect, use } from "chai";
import { describe, it } from "mocha";
import { fake, replace, restore } from "sinon";
import * as sinnonChai from "sinon-chai";

import { Ingredient } from "./Ingredient";
import { KeyedIngredient } from "./KeyedIngredient";

use(sinnonChai);

export class EmptyIngredient extends Ingredient {
    constructor() {
        super("EmptyIngredient", "A");
    }
}

class IngredientWithRequired extends Ingredient {
    constructor() {
        super("IngredientWithRequired", "A");
        this.setRequired("required", "foo");
    }
}

class IngredientWithOptional extends Ingredient {
    constructor() {
        super("IngredientWithOptional", "A");
        this.setOptional("optional", false, true);
    }
}

class IngredientWithOptionalSetTwice extends Ingredient {
    constructor() {
        super("IngredientWithOptionalSetTwice", "A");
        this.setOptional("optional", false, true);
        this.setOptional("optional", false, false);
    }
}

class IngredientWithRepeatableOptional extends Ingredient {
    constructor() {
        super("IngredientWithRepeatableOptional", "A");
        this.setOptional("optional", true, true);
        this.setOptional("optional", true, false);
    }
}

class IngredientWithCompoundOptional extends Ingredient {
    constructor() {
        super("IngredientWithCompoundOptional", "A");
        this.setCompoundOptional("optional", false, "a", 1, "b", "foo");
    }
}

class IngredientWithRepeatableCompoundOptional extends Ingredient {
    constructor() {
        super("IngredientWithRepeatableCompoundOptional", "A");
        this.setCompoundOptional("optional", true, "a", 1, "b", "foo");
        this.setCompoundOptional("optional", true, "a", -1, "b", "bar");
    }
}

export class EmptyKeyedIngredient extends KeyedIngredient {
    constructor() {
        super("EmptyKeyedIngredient", "A");
    }
}

export function expectJsonEquals(expected: string, actual: any) {
    const expectedObj = JSON.parse(expected);

    expect(expectedObj).to.deep.equal(JSON.parse(JSON.stringify(actual)));
}

describe("Ingredient", () => {
    describe("toJSON", () => {
        it("should serialize an empty ingredient", () => {
            expectJsonEquals(`{"EmptyIngredient":{}}`, new EmptyIngredient());
        });

        it("should serialize an ingredient with a required param", () => {
            expectJsonEquals(`{"IngredientWithRequired":{"required":"foo"}}`, new IngredientWithRequired());
        });

        it("should serialize an ingredient with an optional param", () => {
            expectJsonEquals(`{"IngredientWithOptional":{"optional":true}}`, new IngredientWithOptional());
        });

        it("should serialize only the latest value of an optional param", () => {
            expectJsonEquals(`{"IngredientWithOptionalSetTwice":{"optional":false}}`, new IngredientWithOptionalSetTwice());
        });

        it("should serialize multiple values for an ingredient with a repeatable optional param", () => {
            expectJsonEquals(`{"IngredientWithRepeatableOptional":{"optional":[true,false]}}`, new IngredientWithRepeatableOptional());
        });

        it("should serialize an ingredient with a compound optional param", () => {
            expectJsonEquals(`{"IngredientWithCompoundOptional":{"optional":{"a":1,"b":"foo"}}}`, new IngredientWithCompoundOptional());
        });

        it("should serialize an ingredient with a repeatable compound optional param", () => {
            expectJsonEquals(`{"IngredientWithRepeatableCompoundOptional":{"optional":[{"a":1,"b":"foo"},{"a":-1,"b":"bar"}]}}`, new IngredientWithRepeatableCompoundOptional());
        });

        it("should serialize a keyed ingredient without a key", () => {
            expectJsonEquals(`{"EmptyKeyedIngredient":{}}`, new EmptyKeyedIngredient());
        });

        it("should serialize a keyed ingredient with a key", () => {
            expectJsonEquals(`{"EmptyKeyedIngredient":{"key":"foo"}}`, new EmptyKeyedIngredient().keyed("foo"));
        });
    });

    describe("fromJSON", () => {
        const TYPES = {EmptyIngredient, IngredientWithRequired, IngredientWithOptional, IngredientWithCompoundOptional};

        it("should defer to a known ingredient subclass to perform deserialization", () => {
            const fromJSON = fake.returns(new EmptyIngredient());
            replace(EmptyIngredient, "fromJSON", fromJSON);

            Ingredient.fromJSON({EmptyIngredient: {}}, TYPES);

            expect(fromJSON.args[0][0]).to.deep.equal({EmptyIngredient: {}});
            restore();
        });

        it("should throw when attempting to deserialize an unknown ingredient", () =>  {
            expect(() => Ingredient.fromJSON({UnknownIngredient: {}}, TYPES)).to.throw();
        });
    });
});
