import { expect } from "chai";
import { describe, it } from "mocha";

import { Recipe } from "./Recipe";
import { Ingredient } from "./Ingredient";
import { KeyedIngredient } from "./KeyedIngredient";
import { EmptyIngredient, EmptyKeyedIngredient, expectJsonEquals } from "./Ingredient.spec";

class TestIngredientA1 extends Ingredient {
    constructor() {
        super("TestIngredientA1", "A");
    }
    public static fromJSON(json: any) {
        return new TestIngredientA1();
    }
}

class TestIngredientA2 extends Ingredient {
    constructor() {
        super("TestIngredientA2", "A");
    }
    public static fromJSON(json: any) {
        return new TestIngredientA2();
    }
}

class TestIngredientA3 extends Ingredient {
    constructor() {
        super("TestIngredientA3", "A");
    }
    public static fromJSON(json: any) {
        return new TestIngredientA3();
    }
}

class TestIngredientB1 extends Ingredient {
    constructor() {
        super("TestIngredientB1", "B");
    }
    public static fromJSON(json: any) {
        return new TestIngredientB1();
    }
}

class TestKeyedIngredientA extends KeyedIngredient {
    constructor() {
        super("TestKeyedIngredientA", "A");
    }
    public static fromJSON(json: any) {
        return new TestKeyedIngredientA();
    }
}

class TestKeyedIngredientB extends KeyedIngredient {
    constructor() {
        super("TestKeyedIngredientB", "B");
    }
    public static fromJSON(json: any) {
        return new TestKeyedIngredientB();
    }
}

describe("Recipe", () => {
    describe("prepare", () => {
        it("should flatten recipes without a context", () => {
            const recipe = Recipe.prepare(
                new TestIngredientA1(),
                Recipe.prepare(
                    new TestIngredientA2()
                )
            );

            expect(recipe.getIngredients().length).to.equal(2);
            expect(recipe.getIngredients()[0].getIngredientType()).to.equal("TestIngredientA1");
            expect(recipe.getIngredients()[1].getIngredientType()).to.equal("TestIngredientA2");
        });

        it("should preserve the structure of contextful recipes", () => {
            const recipe = Recipe.prepare(
                new TestIngredientA1(),
                Recipe.context("key1",
                    new TestIngredientA2()
                )
            );

            expect(recipe.getIngredients().length).to.equal(2);
            expect(recipe.getIngredients()[0].getIngredientType()).to.equal("TestIngredientA1");
            expect(recipe.getIngredients()[1].getIngredientType()).to.equal("Recipe");
            expect((<Recipe>recipe.getIngredients()[1]).getContext()).to.equal("key1");
            expect((<Recipe>recipe.getIngredients()[1]).getIngredients().length).to.equal(1);
            expect((<Recipe>recipe.getIngredients()[1]).getIngredients()[0].getIngredientType()).to.equal("TestIngredientA2");
        });
    });

    describe("context", () => {
        it("should create a recipe with a context", () => {
            const recipe = Recipe.context("key", new TestKeyedIngredientA());

            expect(recipe.getContext()).to.equal("key");
            expect(recipe.getIngredients().length).to.equal(1);
            expect(recipe.getIngredients()[0].getIngredientType()).to.equal("TestKeyedIngredientA");
        });

        it("should flatten an inner recipe with no context", () => {
            const recipe = Recipe.context("key",
                Recipe.prepare(new TestIngredientA1())
            );

            expect(recipe.getContext()).to.equal("key");
            expect(recipe.getIngredients().length).to.equal(1);
            expect(recipe.getIngredients()[0].getIngredientType()).to.equal("TestIngredientA1");
        });

        it("should preserve the structure of an inner contextful recipe", () => {
            const recipe = Recipe.context("key",
                Recipe.context("key2", new TestIngredientA1())
            );

            expect(recipe.getContext()).to.equal("key");
            expect(recipe.getIngredients().length).to.equal(1);
            expect(recipe.getIngredients()[0].getIngredientType()).to.equal("Recipe");
            expect((<Recipe>recipe.getIngredients()[0]).getContext()).to.equal("key2");
            expect((<Recipe>recipe.getIngredients()[0]).getIngredients().length).to.equal(1);
            expect((<Recipe>recipe.getIngredients()[0]).getIngredients()[0].getIngredientType()).to.equal("TestIngredientA1");
        });

        it("should convert a recipe with a context ingredient into a recipe with the ingredient and a sub-recipe with its context", () => {
            const recipe = Recipe.context(new TestKeyedIngredientA().keyed("key"),
                new TestIngredientA1()
            );

            expect(recipe.getContext()).to.equal(null);
            expect(recipe.getIngredients().length).to.equal(2);
            expect(recipe.getIngredients()[0].getIngredientType()).to.equal("TestKeyedIngredientA");
            expect(recipe.getIngredients()[1].getIngredientType()).to.equal("Recipe");
            expect((<Recipe>recipe.getIngredients()[1]).getContext()).to.equal("key");
            expect((<Recipe>recipe.getIngredients()[1]).getIngredients().length).to.equal(1);
            expect((<Recipe>recipe.getIngredients()[1]).getIngredients()[0].getIngredientType()).to.equal("TestIngredientA1");
        });

        it("should flatten a recipe with a context ingredient when the context is null", () => {
            const recipe = Recipe.context(new TestKeyedIngredientA().keyed(null),
                new TestIngredientA1()
            );

            expect(recipe.getContext()).to.equal(null);
            expect(recipe.getIngredients().length).to.equal(2);
            expect(recipe.getIngredients()[0].getIngredientType()).to.equal("TestKeyedIngredientA");
            expect(recipe.getIngredients()[1].getIngredientType()).to.equal("TestIngredientA1");
        });

        it("should flatten an inner context-free recipe of a recipe with a context ingredient", () => {
            const recipe = Recipe.context(new TestKeyedIngredientA().keyed("key"),
                Recipe.prepare(new TestIngredientA1())
            );

            expect(recipe.getContext()).to.equal(null);
            expect(recipe.getIngredients().length).to.equal(2);
            expect(recipe.getIngredients()[0].getIngredientType()).to.equal("TestKeyedIngredientA");
            expect(recipe.getIngredients()[1].getIngredientType()).to.equal("Recipe");
            expect((<Recipe>recipe.getIngredients()[1]).getContext()).to.equal("key");
            expect((<Recipe>recipe.getIngredients()[1]).getIngredients().length).to.equal(1);
            expect((<Recipe>recipe.getIngredients()[1]).getIngredients()[0].getIngredientType()).to.equal("TestIngredientA1");
        });

        it("should preserve the structure of an inner contextful recipe for a recipe with a context ingredient", () => {
            const recipe = Recipe.context(new TestKeyedIngredientA().keyed("key"),
                Recipe.context("key2", new TestIngredientA1())
            );

            expect(recipe.getContext()).to.equal(null);
            expect(recipe.getIngredients().length).to.equal(2);
            expect(recipe.getIngredients()[0].getIngredientType()).to.equal("TestKeyedIngredientA");
            expect(recipe.getIngredients()[1].getIngredientType()).to.equal("Recipe");
            expect((<Recipe>recipe.getIngredients()[1]).getContext()).to.equal("key");
            expect((<Recipe>recipe.getIngredients()[1]).getIngredients().length).to.equal(1);
            expect((<Recipe>recipe.getIngredients()[1]).getIngredients()[0].getIngredientType()).to.equal("Recipe");
            expect((<Recipe>(<Recipe>recipe.getIngredients()[1]).getIngredients()[0]).getContext()).to.equal("key2");
            expect((<Recipe>(<Recipe>recipe.getIngredients()[1]).getIngredients()[0]).getIngredients().length).to.equal(1);
            expect((<Recipe>(<Recipe>recipe.getIngredients()[1]).getIngredients()[0]).getIngredients()[0].getIngredientType()).to.equal("TestIngredientA1");
        });
    });

    describe("segment", () => {
        it("should return no segments for an empty recipe", () => {
            const recipe = Recipe.prepare();
            expect(recipe.segment().length).to.equal(0);
        });

        it("should create one segment for a single ingredient", () => {
            const recipe = Recipe.prepare(new TestIngredientA1());
            const segments = recipe.segment();

            expect(segments.length).to.equal(1);
            expect(segments[0].domain).to.equal("A");
            expect(segments[0].recipe.getIngredients().length).to.equal(1);
        });

        it("should create one segment two ingredients in same domain", () => {
            const recipe = Recipe.prepare(new TestIngredientA1(), new TestIngredientA2());

            const segments = recipe.segment();

            expect(segments.length).to.equal(1);
            expect(segments[0].domain).to.equal("A");
            expect(segments[0].recipe.getIngredients().length).to.equal(2);
            expect(segments[0].recipe.getIngredients()[0].getIngredientType()).to.equal("TestIngredientA1");
            expect(segments[0].recipe.getIngredients()[1].getIngredientType()).to.equal("TestIngredientA2");
        });

        it("should create two segments for two ingredients in different domains", () => {
            const recipe = Recipe.prepare(new TestIngredientA1(), new TestIngredientB1());

            const segments = recipe.segment();

            expect(segments.length).to.equal(2);
            expect(segments[0].domain).to.equal("A");
            expect(segments[0].recipe.getIngredients().length).to.equal(1);
            expect(segments[1].domain).to.equal("B");
            expect(segments[1].recipe.getIngredients().length).to.equal(1);
            expect(segments[0].recipe.getIngredients()[0].getIngredientType()).to.equal("TestIngredientA1");
            expect(segments[1].recipe.getIngredients()[0].getIngredientType()).to.equal("TestIngredientB1");
        });

        it("should preserve recipe contexts", () => {
            const recipe = Recipe.prepare(
                new TestIngredientA1(),
                new TestIngredientA2(),
                Recipe.context("context",
                    new TestIngredientA3()
                )
            );

            const segments = recipe.segment();

            expect(segments.length).to.equal(1);
            expect(segments[0].domain).to.equal("A");
            expect(segments[0].recipe.getIngredients().length).to.equal(3);
            expect(segments[0].recipe.getIngredients()[0].getIngredientType()).to.equal("TestIngredientA1");
            expect(segments[0].recipe.getIngredients()[1].getIngredientType()).to.equal("TestIngredientA2");
            expect(segments[0].recipe.getIngredients()[2].getIngredientType()).to.equal("Recipe");
            expect((<Recipe>segments[0].recipe.getIngredients()[2]).getContext()).to.equal("context");
            expect((<Recipe>segments[0].recipe.getIngredients()[2]).getIngredients()[0].getIngredientType()).to.equal("TestIngredientA3");
        });

        it("should create more than two segments when there are two domains in non-contiguous order", () => {
            const recipe = Recipe.prepare(
                new TestIngredientA1(),
                new TestIngredientB1(),
                new TestIngredientA2()
            );

            const segments = recipe.segment();

            expect(segments.length).to.equal(3);
            expect(segments[0].domain).to.equal("A");
            expect(segments[0].recipe.getIngredients().length).to.equal(1);
            expect(segments[1].domain).to.equal("B");
            expect(segments[1].recipe.getIngredients().length).to.equal(1);
            expect(segments[2].domain).to.equal("A");
            expect(segments[2].recipe.getIngredients().length).to.equal(1);
        });

        it("should create a segment for a recipe with a context ingredient containing no other ingredients", () => {
            const recipe = Recipe.context(new TestKeyedIngredientA());

            const segments = recipe.segment();

            expect(segments.length).to.equal(1);
            expect(segments[0].domain).to.equal("A");
            expect(segments[0].recipe.getIngredients().length).to.equal(1);
            expect(segments[0].recipe.getIngredients()[0].getIngredientType()).to.equal("TestKeyedIngredientA");
        });

        it("should segment a recipe with a context ingredient and a sub-ingredient in a different domain", () => {
            const recipe = Recipe.context(new TestKeyedIngredientA().keyed("key"),
                new TestIngredientB1()
            );
            const segments = recipe.segment();

            expect(segments.length).to.equal(2);
            expect(segments[0].domain).to.equal("A");
            expect(segments[0].recipe.getIngredients().length).to.equal(1);
            expect(segments[0].recipe.getIngredients()[0].getIngredientType()).to.equal("TestKeyedIngredientA");
            expect(segments[1].domain).to.equal("B");
            expect(segments[1].recipe.getIngredients().length).to.equal(1);
            expect(segments[1].recipe.getIngredients()[0].getIngredientType()).to.equal("Recipe");
            expect((<Recipe>segments[1].recipe.getIngredients()[0]).getIngredients().length).to.equal(1);
            expect((<Recipe>segments[1].recipe.getIngredients()[0]).getIngredients()[0].getIngredientType()).to.equal("TestIngredientB1");
        });

        it("should segment a recipe with multiple nested contexts", () => {
            const recipe = Recipe.context("key1",
                new TestIngredientA1(),
                Recipe.context("key2",
                    new TestIngredientA2()
                )
            );
            const segments = recipe.segment();

            expect(segments.length).to.equal(1);
            expect(segments[0].domain).to.equal("A");
            expect(segments[0].recipe.getContext()).to.equal("key1");
            expect(segments[0].recipe.getIngredients().length).to.equal(2);
            expect(segments[0].recipe.getIngredients()[0].getIngredientType()).to.equal("TestIngredientA1");
            expect(segments[0].recipe.getIngredients()[1].getIngredientType()).to.equal("Recipe");
            expect((<Recipe>segments[0].recipe.getIngredients()[1]).getContext()).to.equal("key2");
            expect((<Recipe>segments[0].recipe.getIngredients()[1]).getIngredients().length).to.equal(1);
            expect((<Recipe>segments[0].recipe.getIngredients()[1]).getIngredients()[0].getIngredientType()).to.equal("TestIngredientA2");
        });

        it("should segment a contextful recipe with a nexted context ingredient in a different domain", () => {
            const recipe = Recipe.context("key1",
                new TestIngredientA1(),
                Recipe.context(new TestKeyedIngredientB().keyed("key2"),
                    new TestIngredientA2()
                )
            );
            const segments = recipe.segment();

            expect(segments.length).to.equal(3);
            expect(segments[0].domain).to.equal("A");
            expect(segments[0].recipe.getContext()).to.equal("key1");
            expect(segments[0].recipe.getIngredients().length).to.equal(1);
            expect(segments[0].recipe.getIngredients()[0].getIngredientType()).to.equal("TestIngredientA1");

            expect(segments[1].domain).to.equal("B");
            expect(segments[1].recipe.getContext()).to.equal("key1");
            expect(segments[1].recipe.getIngredients().length).to.equal(1);
            expect(segments[1].recipe.getIngredients()[0].getIngredientType()).to.equal("TestKeyedIngredientB");

            expect(segments[2].domain).to.equal("A");
            expect(segments[2].recipe.getContext()).to.equal("key1");
            expect(segments[2].recipe.getIngredients().length).to.equal(1);
            expect(segments[2].recipe.getIngredients()[0].getIngredientType()).to.equal("Recipe");
            expect((<Recipe>segments[2].recipe.getIngredients()[0]).getContext()).to.equal("key2");
            expect((<Recipe>segments[2].recipe.getIngredients()[0]).getIngredients().length).to.equal(1);
            expect((<Recipe>segments[2].recipe.getIngredients()[0]).getIngredients()[0].getIngredientType()).to.equal("TestIngredientA2");
        });
    });

    describe("toJSON", () => {
        it("should serialize an empty recipe", () => {
            expectJsonEquals(`{"Recipe":{"ingredients":[]}}`, Recipe.prepare());
        });

        it("should serialize a recipe with a single ingredient", () => {
            expectJsonEquals(`{"Recipe":{"ingredients":[${JSON.stringify(new EmptyIngredient())}]}}`, Recipe.prepare(new EmptyIngredient()));
        });

        it("should serialize a recipe with multiple ingredients", () => {
            expectJsonEquals(`{"Recipe":{"ingredients":[${JSON.stringify(new EmptyIngredient())},${JSON.stringify(new EmptyIngredient())}]}}`, Recipe.prepare(new EmptyIngredient(), new EmptyIngredient()));
        });

        it("should serialize a recipe with a context", () => {
            expectJsonEquals(`{"Recipe":{"context":"foo","ingredients":[]}}`, Recipe.context("foo"));
        });

        it("should serialize an empty recipe with an ingredient context", () => {
            expectJsonEquals(`{"Recipe":{"ingredients":[${JSON.stringify(new EmptyKeyedIngredient().keyed("key"))},${JSON.stringify(Recipe.context("key"))}]}}`, Recipe.context(new EmptyKeyedIngredient().keyed("key")));
        });

        it("should serialize a recipe with an ingredient context", () => {
            expectJsonEquals(`{"Recipe":{"ingredients":[${JSON.stringify(new EmptyKeyedIngredient().keyed("key"))},${JSON.stringify(Recipe.context("key", new TestIngredientA1()))}]}}`, Recipe.context(new EmptyKeyedIngredient().keyed("key"), new TestIngredientA1()));
        });
    });

    describe("fromJSON", () => {
        const TYPES = {TestIngredientA1, TestIngredientA2};

        it("should deserialize an empty recipe", () => {
            const json = JSON.parse(`{"Recipe":{"context":null,"ingredients":[]}}`);
            const recipe = Recipe.fromJSON(json, TYPES);

            expect(recipe instanceof Recipe).to.equal(true);
            expect(recipe.getIngredients().length).to.equal(0);
            expect(recipe.getContext()).to.equal(null);
        });

        it("should deserialize a recipe and its ingredients", () => {
            const json = JSON.parse(`{"Recipe":{"context":null,"ingredients":[{"TestIngredientA1":{}},{"TestIngredientA2":{}}]}}`);
            const recipe = Recipe.fromJSON(json, TYPES);

            expect(recipe instanceof Recipe).to.equal(true);
            expect(recipe.getIngredients().length).to.equal(2);
            expect(recipe.getIngredients()[0] instanceof TestIngredientA1).to.equal(true);
            expect(recipe.getIngredients()[1] instanceof TestIngredientA2).to.equal(true);
        });

        it("should deserialize a contextful recipe", () => {
            const json = JSON.parse(`{"Recipe":{"context":"foo","ingredients":[]}}`);
            const recipe = Recipe.fromJSON(json, TYPES);

            expect(recipe instanceof Recipe).to.equal(true);
            expect(recipe.getIngredients().length).to.equal(0);
            expect(recipe.getContext()).to.equal("foo");
        });

        it("should deserialize a recipe with a nested recipe", () => {
            const json = JSON.parse(`{"Recipe":{"context":null,"ingredients":[{"Recipe":{"context":null,"ingredients":[{"TestIngredientA1":{}}]}}]}}`);
            const recipe = Recipe.fromJSON(json, TYPES);

            expect(recipe instanceof Recipe).to.equal(true);
            expect(recipe.getIngredients().length).to.equal(1);
            expect(recipe.getIngredients()[0] instanceof Recipe).to.equal(true);
            expect((recipe.getIngredients()[0] as Recipe).getIngredients().length).to.equal(1);
            expect((recipe.getIngredients()[0] as Recipe).getIngredients()[0] instanceof TestIngredientA1).to.equal(true);
        });

        it("should throw when deserializing a recipe with an unknown ingedient", () => {
            const json = JSON.parse(`{"Recipe":{"context":null,"ingredients":[{"UnknownIngredient":{}}]}}`);
            expect(() => Recipe.fromJSON(json, TYPES)).to.throw();
        });

        it("should deserialize the context into null when it is missing from the json", () => {
            const json = JSON.parse(`{"Recipe":{"ingredients":[{"TestIngredientA2":{}}]}}`);
            const recipe = Recipe.fromJSON(json, TYPES);

            expect(recipe.getContext()).to.equal(null);
        });
    });
});