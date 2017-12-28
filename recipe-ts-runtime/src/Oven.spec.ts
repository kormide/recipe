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
            const dispatcher = spy((domain: string | null, payload: string) => "");
            oven.addDispatcher(dispatcher);

            oven.bake(Recipe.prepare());

            expect(dispatcher).callCount(0);
        });

        it("should not call dispatcher for empty nested recipes", () => {
            const dispatcher = spy((domain: string | null, payload: string) => "");
            oven.addDispatcher(dispatcher);

            oven.bake(Recipe.prepare(Recipe.prepare()));

            expect(dispatcher).callCount(0);
        });

        it("should not call the dispatcher for a single ingredient", () => {
            const dispatcher = spy((domain: string | null, payload: string) => "{}");
            oven.addDispatcher(dispatcher);

            oven.bake(Recipe.prepare(new TestIngredientA1()));

            expect(dispatcher).to.have.been.calledWith("A");
        });

        it("should not throw when there is no dispatcher", () => {
            oven.bake(Recipe.prepare(new TestIngredientA1()));
        });

        it("should call the dispatcher for different domains", () => {
            const dispatcher = spy((domain: string | null, payload: string) => "{}");
            oven.addDispatcher(dispatcher);

            oven.bake(Recipe.prepare(
                new TestIngredientA1(),
                new TestIngredientB1(),
                new TestIngredientC1()
            ));

            expect(dispatcher).callCount(3);
            expect(dispatcher.getCall(0).calledWith("A")).to.equal(true);
            expect(dispatcher.getCall(1).calledWith("B")).to.equal(true);
            expect(dispatcher.getCall(2).calledWith("C")).to.equal(true);
        });

        it("should call the dispatcher for different domains in nested recipe", () => {
            const dispatcher = spy((domain: string | null, payload: string) => "{}");
            oven.addDispatcher(dispatcher);

            oven.bake(Recipe.prepare(
                new TestIngredientA1(),
                Recipe.prepare(
                    new TestIngredientB1(),
                    new TestIngredientC1()
                )
            ));

            expect(dispatcher).callCount(3);
            expect(dispatcher.getCall(0).calledWith("A")).to.equal(true);
            expect(dispatcher.getCall(1).calledWith("B")).to.equal(true);
            expect(dispatcher.getCall(2).calledWith("C")).to.equal(true);
        });

        it("should call the dispatcher for the same interleaved domains", () => {
            const dispatcher = spy((domain: string | null, payload: string) => "{}");
            oven.addDispatcher(dispatcher);

            oven.bake(Recipe.prepare(
                new TestIngredientA1(),
                new TestIngredientB1(),
                new TestIngredientA1()
            ));

            expect(dispatcher).callCount(3);
            expect(dispatcher.getCall(0).calledWith("A")).to.equal(true);
            expect(dispatcher.getCall(1).calledWith("B")).to.equal(true);
            expect(dispatcher.getCall(2).calledWith("A")).to.equal(true);
        });

        it("should call the dispatcher for a context ingredient", () => {
            const dispatcher = spy((domain: string | null, payload: string) => "{}");
            oven.addDispatcher(dispatcher);

            oven.bake(Recipe.context(new TestKeyedIngredientA()));

            expect(dispatcher).to.have.been.calledWith("A");
        });

        it("should call the dispatcher for a context ingredient and child with different domain", () => {
            const dispatcher = spy((domain: string | null, payload: string) => "{}");
            oven.addDispatcher(dispatcher);

            oven.bake(Recipe.context(new TestKeyedIngredientA(),
                new TestIngredientB1()
            ));

            expect(dispatcher.getCall(0).calledWith("A")).to.equal(true);
            expect(dispatcher.getCall(1).calledWith("B")).to.equal(true);
        });

        it("should propagate the cake to subsequent dispatches", () => {
            const dispatcher = spy((domain: string | null, payload: string) => `{"foo":"bar"}`);
            oven.addDispatcher(dispatcher);

            const recipe = Recipe.prepare(
                new TestIngredientA1(),
                new TestIngredientB1()
            );
            const segments = recipe.segment();

            oven.bake(recipe);

            expect(dispatcher.getCall(0).calledWith("A", `{"recipe":${JSON.stringify(segments[0].recipe)},"cake":{}}`)).to.equal(true);
            expect(dispatcher.getCall(1).calledWith("B", `{"recipe":${JSON.stringify(segments[1].recipe)},"cake":{"foo":"bar"}}`)).to.equal(true);
        });
    });
});