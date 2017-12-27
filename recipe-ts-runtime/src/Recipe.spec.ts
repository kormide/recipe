import { expect } from "chai";

import { Recipe } from "./Recipe";
import { Ingredient } from "./Ingredient";
import { KeyedIngredient } from "./KeyedIngredient";
import { EmptyIngredient, EmptyKeyedIngredient, expectJsonEquals } from "./Ingredient.spec";

class TestIngredientA1 extends Ingredient {
    constructor() {
        super("TestIngredientA1", "A");
    }
}

class TestIngredientA2 extends Ingredient {
    constructor() {
        super("TestIngredientA2", "A");
    }
}

class TestIngredientA3 extends Ingredient {
    constructor() {
        super("TestIngredientA3", "A");
    }
}

class TestIngredientB1 extends Ingredient {
    constructor() {
        super("TestIngredientB1", "B");
    }
}

class TestKeyedIngredientA extends KeyedIngredient {
    constructor() {
        super("TestKeyedIngredientA", "A");
    }
}

describe("Recipe", () => {
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
            expect(segments[0].recipe.getIngredients()[0].getType()).to.equal("TestIngredientA1");
            expect(segments[0].recipe.getIngredients()[1].getType()).to.equal("TestIngredientA2");
        });

        it("should create two segments for two ingredients in different domains", () => {
            const recipe = Recipe.prepare(new TestIngredientA1(), new TestIngredientB1());

            const segments = recipe.segment();

            expect(segments.length).to.equal(2);
            expect(segments[0].domain).to.equal("A");
            expect(segments[0].recipe.getIngredients().length).to.equal(1);
            expect(segments[1].domain).to.equal("B");
            expect(segments[1].recipe.getIngredients().length).to.equal(1);
            expect(segments[0].recipe.getIngredients()[0].getType()).to.equal("TestIngredientA1");
            expect(segments[1].recipe.getIngredients()[0].getType()).to.equal("TestIngredientB1");
        });

        it("should preserve nested recipe structure", () => {
            const recipe = Recipe.prepare(
                new TestIngredientA1(),
                new TestIngredientA2(),
                Recipe.prepare(
                    new TestIngredientA3()
                )
            );

            const segments = recipe.segment();

            expect(segments.length).to.equal(1);
            expect(segments[0].domain).to.equal("A");
            expect(segments[0].recipe.getIngredients().length).to.equal(3);
            expect(segments[0].recipe.getIngredients()[0].getType()).to.equal("TestIngredientA1");
            expect(segments[0].recipe.getIngredients()[1].getType()).to.equal("TestIngredientA2");
            expect(segments[0].recipe.getIngredients()[2].getType()).to.equal("Recipe");
            expect((<Recipe>segments[0].recipe.getIngredients()[2]).getIngredients()[0].getType()).to.equal("TestIngredientA3");
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
            expect(segments[0].recipe.getIngredients()[0].getType()).to.equal("TestIngredientA1");
            expect(segments[0].recipe.getIngredients()[1].getType()).to.equal("TestIngredientA2");
            expect(segments[0].recipe.getIngredients()[2].getType()).to.equal("Recipe");
            expect((<Recipe>segments[0].recipe.getIngredients()[2]).getContext()).to.equal("context");
            expect((<Recipe>segments[0].recipe.getIngredients()[2]).getIngredients()[0].getType()).to.equal("TestIngredientA3");
        });

        it("should segment nested recipes with ingredients in different domains", () => {
            const recipe = Recipe.prepare(
                new TestIngredientA1(),
                new TestIngredientA2(),
                Recipe.prepare(
                    new TestIngredientB1()
                )
            );

            const segments = recipe.segment();

            expect(segments.length).to.equal(2);
            expect(segments[0].domain).to.equal("A");
            expect(segments[0].recipe.getIngredients().length).to.equal(2);
            expect(segments[0].recipe.getIngredients()[0].getType()).to.equal("TestIngredientA1");
            expect(segments[0].recipe.getIngredients()[1].getType()).to.equal("TestIngredientA2");
            expect(segments[1].domain).to.equal("B");
            expect(segments[1].recipe.getIngredients().length).to.equal(1);
            expect(segments[1].recipe.getIngredients()[0].getType()).to.equal("Recipe");
            expect((<Recipe>segments[1].recipe.getIngredients()[0]).getIngredients().length).to.equal(1);
            expect((<Recipe>segments[1].recipe.getIngredients()[0]).getIngredients()[0].getType()).to.equal("TestIngredientB1");
        });

        it("should preserve recipe contexts for nested recipes with different domains", () => {
            const recipe = Recipe.context("foo",
                new TestIngredientA1(),
                Recipe.prepare(
                    Recipe.context("bar",
                        new TestIngredientB1()
                    )
                )
            );

            const segments = recipe.segment();

            expect(segments.length).to.equal(2);
            expect(segments[0].domain).to.equal("A");
            expect(segments[0].recipe.getContext()).to.equal("foo");
            expect(segments[0].recipe.getIngredients().length).to.equal(1);
            expect(segments[0].recipe.getIngredients()[0].getType()).to.equal("TestIngredientA1");
            expect(segments[1].recipe.getContext()).to.equal("foo");
            expect(segments[1].recipe.getIngredients().length).to.equal(1);
            expect(segments[1].domain).to.equal("B");
            expect(segments[1].recipe.getIngredients()[0].getType()).to.equal("Recipe");
            expect((<Recipe>segments[1].recipe.getIngredients()[0]).getContext()).to.equal(null);
            expect((<Recipe>segments[1].recipe.getIngredients()[0]).getIngredients().length).to.equal(1);
            expect((<Recipe>segments[1].recipe.getIngredients()[0]).getIngredients()[0].getType()).to.equal("Recipe");
            expect((<Recipe>(<Recipe>segments[1].recipe.getIngredients()[0]).getIngredients()[0]).getContext()).to.equal("bar");
            expect((<Recipe>(<Recipe>segments[1].recipe.getIngredients()[0]).getIngredients()[0]).getIngredients().length).to.equal(1);
            expect((<Recipe>(<Recipe>segments[1].recipe.getIngredients()[0]).getIngredients()[0]).getIngredients()[0].getType()).to.equal("TestIngredientB1");
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
            expect(segments[0].recipe.getIngredients().length).to.equal(0);
            expect(segments[0].recipe.getContextIngredient()).not.to.equal(null);
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

        it("should serialize a recipe with an ingredient context", () => {
            expectJsonEquals(`{"Recipe":{"contextIngredient":${JSON.stringify(new EmptyKeyedIngredient())},"ingredients":[]}}`, Recipe.context(new EmptyKeyedIngredient()));
        });
    });
});