import { expect, use } from "chai";
import { describe, it } from "mocha";
import { spy } from "sinon";
import * as sinnonChai from "sinon-chai";

import { Oven } from "./Oven";
import { Recipe } from "./Recipe";
import { Ingredient } from "./Ingredient";
import { KeyedIngredient } from "./KeyedIngredient";

use(sinnonChai);

class TestIngredientA1 extends Ingredient {
    constructor() {
        super("TestIngredientA1", "A");
    }
}

class TestIngredientB1 extends Ingredient {
    constructor() {
        super("TestIngredientB1", "B");
    }
}

class TestIngredientC1 extends Ingredient {
    constructor() {
        super("TestIngredientC1", "C");
    }
}

class TestKeyedIngredientA extends KeyedIngredient {
    constructor() {
        super("TestKeyedIngredientA", "A");
    }
}

describe("Oven", () => {
    let oven: Oven;

    beforeEach(() => {
        oven = new Oven();
    });

    afterEach(() => {
        (<any>oven) = null;
    });

    describe("bake", () => {
        it("should not call dispatcher for empty recipe", () => {
            const dispatcher = spy((payload: string) => Promise.resolve("{}"));
            oven.addDispatcher("A", dispatcher);

            oven.bake(Recipe.prepare());

            expect(dispatcher).callCount(0);
        });

        it("should not call dispatcher for empty nested recipes", () => {
            const dispatcher = spy((payload: string) => Promise.resolve("{}"));
            oven.addDispatcher("A", dispatcher);

            oven.bake(Recipe.prepare(Recipe.prepare()));

            expect(dispatcher).callCount(0);
        });

        it("should throw when there is no dispatcher for an ingredient", () => {
            expect(() => oven.bake(Recipe.prepare(new TestIngredientA1()))).to.throw();
        });

        it("should throw when adding two dispatchers for the same ingredient", () => {
            oven.addDispatcher("A", payload => Promise.resolve("{}"));
            expect(() => oven.addDispatcher("A", payload => Promise.resolve("{}"))).to.throw();
        });

        it("should call the dispatcher for a single ingredient", () => {
            const dispatcher = spy((payload: string) => Promise.resolve("{}"));
            oven.addDispatcher("A", dispatcher);

            return oven.bake(Recipe.prepare(new TestIngredientA1())).then(() => {
                expect(dispatcher).callCount(1);
            });
        });

        it("should call the dispatcher for different domains in order", () => {
            const dispatcherA = spy((payload: string) => Promise.resolve("{}"));
            const dispatcherB = spy((payload: string) => Promise.resolve("{}"));
            const dispatcherC = spy((payload: string) => Promise.resolve("{}"));

            oven.addDispatcher("A", dispatcherA);
            oven.addDispatcher("B", dispatcherB);
            oven.addDispatcher("C", dispatcherC);

            return oven.bake(Recipe.prepare(
                new TestIngredientA1(),
                new TestIngredientB1(),
                new TestIngredientC1()
            )).then(() => {
                expect(dispatcherA).callCount(1);
                expect(dispatcherB).callCount(1);
                expect(dispatcherC).callCount(1);
                expect(dispatcherA).calledBefore(dispatcherB as any);
                expect(dispatcherB).calledBefore(dispatcherC as any);
            });
        });

        it("should call the dispatcher for different domains in nested recipe", () => {
            const dispatcherA = spy((payload: string) => Promise.resolve("{}"));
            const dispatcherB = spy((payload: string) => Promise.resolve("{}"));
            const dispatcherC = spy((payload: string) => Promise.resolve("{}"));

            oven.addDispatcher("A", dispatcherA);
            oven.addDispatcher("B", dispatcherB);
            oven.addDispatcher("C", dispatcherC);

            return oven.bake(Recipe.prepare(
                new TestIngredientA1(),
                Recipe.prepare(
                    new TestIngredientB1(),
                    new TestIngredientC1()
                )
            )).then(() => {
                expect(dispatcherA).callCount(1);
                expect(dispatcherB).callCount(1);
                expect(dispatcherC).callCount(1);
                expect(dispatcherA).calledBefore(dispatcherB as any);
                expect(dispatcherB).calledBefore(dispatcherC as any);
            });
        });

        it("should call the dispatcher for the same interleaved domains", () => {
            const dispatcherA = spy((payload: string) => Promise.resolve("{}"));
            const dispatcherB = spy((payload: string) => Promise.resolve("{}"));

            oven.addDispatcher("A", dispatcherA);
            oven.addDispatcher("B", dispatcherB);

            return oven.bake(Recipe.prepare(
                new TestIngredientA1(),
                new TestIngredientB1(),
                new TestIngredientA1()
            )).then(() => {
                expect(dispatcherA).callCount(2);
                expect(dispatcherB).callCount(1);
                expect(dispatcherA).calledBefore(dispatcherB as any);
                expect(dispatcherB).calledBefore(dispatcherA as any);
            });
        });

        it("should call the dispatcher for a context ingredient", () => {
            const dispatcher = spy((payload: string) => Promise.resolve("{}"));
            oven.addDispatcher("A", dispatcher);

            return oven.bake(Recipe.context(new TestKeyedIngredientA())).then(() => {
                expect(dispatcher).callCount(1);
            });
        });

        it("should call the dispatcher for a context ingredient and child with different domain", () => {
            const dispatcherA = spy((payload: string) => Promise.resolve("{}"));
            const dispatcherB = spy((payload: string) => Promise.resolve("{}"));

            oven.addDispatcher("A", dispatcherA);
            oven.addDispatcher("B", dispatcherB);

            return oven.bake(Recipe.context(new TestKeyedIngredientA(),
                new TestIngredientB1()
            )).then(() => {
                expect(dispatcherA).callCount(1);
                expect(dispatcherB).callCount(1);
                expect(dispatcherA).calledBefore(dispatcherB as any);
            });
        });

        it("should propagate the cake to subsequent dispatches", () => {
            const dispatcherA = spy((payload: string) => Promise.resolve(`{"foo":"bar"}`));
            const dispatcherB = spy((payload: string) => Promise.resolve(`{"foo":"bar","moo":"cow"}`));

            oven.addDispatcher("A", dispatcherA);
            oven.addDispatcher("B", dispatcherB);

            const recipe = Recipe.prepare(
                new TestIngredientA1(),
                new TestIngredientB1()
            );
            const segments = recipe.segment();

            return oven.bake(recipe).then(cake => {
                expect(dispatcherA.getCall(0).calledWith(`{"recipe":${JSON.stringify(segments[0].recipe)},"cake":{}}`)).to.equal(true);
                expect(dispatcherB.getCall(0).calledWith(`{"recipe":${JSON.stringify(segments[1].recipe)},"cake":{"foo":"bar"}}`)).to.equal(true);

                expect(cake.get("foo")).to.equal("bar");
                expect(cake.get("moo")).to.equal("cow");
            });
        });
    });
});