import { expect, use } from "chai";
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
            const dispatcher = spy((payload: string) => "{}");
            oven.addDispatcher("A", dispatcher);

            oven.bake(Recipe.prepare());

            expect(dispatcher).callCount(0);
        });

        it("should not call dispatcher for empty nested recipes", () => {
            const dispatcher = spy((payload: string) => "{}");
            oven.addDispatcher("A", dispatcher);

            oven.bake(Recipe.prepare(Recipe.prepare()));

            expect(dispatcher).callCount(0);
        });

        it("should throw when there is no dispatcher for an ingredient", () => {
            expect(() => oven.bake(Recipe.prepare(new TestIngredientA1()))).to.throw();
        });

        it("should throw when adding two dispatchers for the same ingredient", () => {
            oven.addDispatcher("A", payload => "{}");
            expect(() => oven.addDispatcher("A", payload => "{}")).to.throw();
        });

        it("should call the dispatcher for a single ingredient", () => {
            const dispatcher = spy((payload: string) => "{}");
            oven.addDispatcher("A", dispatcher);

            oven.bake(Recipe.prepare(new TestIngredientA1()));

            expect(dispatcher).callCount(1);
        });

        it("should call the dispatcher for different domains", () => {
            const dispatcherA = spy((payload: string) => "{}");
            const dispatcherB = spy((payload: string) => "{}");
            const dispatcherC = spy((payload: string) => "{}");

            oven.addDispatcher("A", dispatcherA);
            oven.addDispatcher("B", dispatcherB);
            oven.addDispatcher("C", dispatcherC);

            oven.bake(Recipe.prepare(
                new TestIngredientA1(),
                new TestIngredientB1(),
                new TestIngredientC1()
            ));

            expect(dispatcherA).callCount(1);
            expect(dispatcherB).callCount(1);
            expect(dispatcherC).callCount(1);
        });

        it("should call the dispatcher for different domains in nested recipe", () => {
            const dispatcherA = spy((payload: string) => "{}");
            const dispatcherB = spy((payload: string) => "{}");
            const dispatcherC = spy((payload: string) => "{}");

            oven.addDispatcher("A", dispatcherA);
            oven.addDispatcher("B", dispatcherB);
            oven.addDispatcher("C", dispatcherC);

            oven.bake(Recipe.prepare(
                new TestIngredientA1(),
                Recipe.prepare(
                    new TestIngredientB1(),
                    new TestIngredientC1()
                )
            ));

            expect(dispatcherA).callCount(1);
            expect(dispatcherB).callCount(1);
            expect(dispatcherC).callCount(1);
        });

        it("should call the dispatcher for the same interleaved domains", () => {
            const dispatcherA = spy((payload: string) => "{}");
            const dispatcherB = spy((payload: string) => "{}");

            oven.addDispatcher("A", dispatcherA);
            oven.addDispatcher("B", dispatcherB);

            oven.bake(Recipe.prepare(
                new TestIngredientA1(),
                new TestIngredientB1(),
                new TestIngredientA1()
            ));

            expect(dispatcherA).callCount(2);
            expect(dispatcherB).callCount(1);
        });

        it("should call the dispatcher for a context ingredient", () => {
            const dispatcher = spy((payload: string) => "{}");
            oven.addDispatcher("A", dispatcher);

            oven.bake(Recipe.context(new TestKeyedIngredientA()));

            expect(dispatcher).callCount(1);
        });

        it("should call the dispatcher for a context ingredient and child with different domain", () => {
            const dispatcherA = spy((payload: string) => "{}");
            const dispatcherB = spy((payload: string) => "{}");

            oven.addDispatcher("A", dispatcherA);
            oven.addDispatcher("B", dispatcherB);

            oven.bake(Recipe.context(new TestKeyedIngredientA(),
                new TestIngredientB1()
            ));

            expect(dispatcherA).callCount(1);
            expect(dispatcherB).callCount(1);
        });

        it("should propagate the cake to subsequent dispatches", () => {
            const dispatcherA = spy((payload: string) => `{"foo":"bar"}`);
            const dispatcherB = spy((payload: string) => "{}");

            oven.addDispatcher("A", dispatcherA);
            oven.addDispatcher("B", dispatcherB);

            const recipe = Recipe.prepare(
                new TestIngredientA1(),
                new TestIngredientB1()
            );
            const segments = recipe.segment();

            oven.bake(recipe);

            expect(dispatcherA.getCall(0).calledWith(`{"recipe":${JSON.stringify(segments[0].recipe)},"cake":{}}`)).to.equal(true);
            expect(dispatcherB.getCall(0).calledWith(`{"recipe":${JSON.stringify(segments[1].recipe)},"cake":{"foo":"bar"}}`)).to.equal(true);
        });
    });
});