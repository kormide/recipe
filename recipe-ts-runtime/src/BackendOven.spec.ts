import { expect, use } from "chai";
import { describe, it } from "mocha";
import { fake, replace, restore, spy, SinonSpy } from "sinon";
import * as sinnonChai from "sinon-chai";

import { BackendOven } from "./BackendOven";
import { BaseIngredientHook } from "./BaseIngredientHook";
import { Cake } from "./Cake";
import { Ingredient } from "./Ingredient";

use(sinnonChai);

interface TestIngredientDataA {}
class TestIngredientASnapshot extends Ingredient {
    constructor() {
        super("TestIngredientA");
    }
    public static fromJSON(json: any): TestIngredientASnapshot {
        return new TestIngredientASnapshot();
    }
}
class TestIngredientHookA extends BaseIngredientHook<TestIngredientDataA> {
    public constructor() {
        super("TestIngredientA", TestIngredientASnapshot);
    }
    public bake(ingredient: TestIngredientDataA, cake: Cake) {}
}

interface TestIngredientDataB {}
class TestIngredientBSnapshot extends Ingredient {
    constructor() {
        super("TestIngredientB");
    }
    public static fromJSON(json: any): TestIngredientBSnapshot {
        return new TestIngredientBSnapshot();
    }
}
class TestIngredientHookB extends BaseIngredientHook<TestIngredientDataB> {
    public constructor() {
        super("TestIngredientB", TestIngredientBSnapshot);
    }
    public bake(ingredient: TestIngredientDataB, cake: Cake) {}
}

interface TestIngredientDataC {}
class TestIngredientCSnapshot extends Ingredient {
    constructor() {
        super("TestIngredientC");
    }
    public static fromJSON(json: any): TestIngredientCSnapshot {
        return new TestIngredientCSnapshot();
    }
}
class TestIngredientHookC extends BaseIngredientHook<TestIngredientDataC> {
    public constructor() {
        super("TestIngredientC", TestIngredientCSnapshot);
    }
    public bake(ingredient: TestIngredientDataC, cake: Cake) {}
}

describe("BackendOven", () => {
    let oven: BackendOven;

    beforeEach(() => {
        oven = new BackendOven();
    });

    describe("bake", () => {
        it("should throw on a missing ingredient hook", () => {
            expect(() => oven.bake(`{"recipe":{"Recipe":{"ingredients":[{"UnknownIngredient":{}}]}}}`)).to.throw();
        });

        it("should bake a single ingredient", () => {
            const hook = new TestIngredientHookA();
            spy(hook, "bake");
            oven.registerHook(hook);

            oven.bake(`{"recipe":{"Recipe":{"ingredients":[{"TestIngredientA":{}}]}}}`);
            expect(hook.bake).callCount(1);
        });

        it("should bake an ingredient in a nested recipe", () => {
            const hook = new TestIngredientHookA();
            spy(hook, "bake");
            oven.registerHook(hook);

            oven.bake(`{"recipe":{"Recipe":{"ingredients":[{"Recipe":{"ingredients":[{"TestIngredientA":{}}]}}]}}}`);
            expect(hook.bake).callCount(1);
        });

        it("should bake a repeated ingredient", () => {
            const hook = new TestIngredientHookA();
            spy(hook, "bake");
            oven.registerHook(hook);

            oven.bake(`{"recipe":{"Recipe":{"ingredients":[{"TestIngredientA":{}},{"TestIngredientA":{}}]}}}`);
            expect(hook.bake).callCount(2);
        });

        it("should make multiple ingredients", () => {
            const hookA = new TestIngredientHookA();
            const hookB = new TestIngredientHookB();
            spy(hookA, "bake");
            spy(hookB, "bake");
            oven.registerHook(hookA);
            oven.registerHook(hookB);

            oven.bake(`{"recipe":{"Recipe":{"ingredients":[{"TestIngredientA":{}},{"TestIngredientB":{}}]}}}`);
            expect(hookA.bake).callCount(1);
            expect(hookB.bake).callCount(1);
            expect(hookA.bake).calledBefore(hookB.bake as any);
        });

        it("should bake an ingredient in a contextful recipe", () => {
            const hook = new TestIngredientHookA();
            spy(hook, "bake");
            oven.registerHook(hook);

            oven.bake(`{"recipe":{"Recipe":{"context":"foo","ingredients":[{"TestIngredientA":{}}]}}}`);
            expect(hook.bake).callCount(1);
        });

        it("should bake all ingredients in a recipe before moving on to the next ingredient", () => {
            const hookA = new TestIngredientHookA();
            const hookB = new TestIngredientHookB();
            const hookC = new TestIngredientHookC();
            spy(hookA, "bake");
            spy(hookB, "bake");
            spy(hookC, "bake");
            oven.registerHook(hookA);
            oven.registerHook(hookB);
            oven.registerHook(hookC);

            oven.bake(`{"recipe":{"Recipe":{"ingredients":[{"Recipe":{"ingredients":[{"TestIngredientA":{}},{"TestIngredientB":{}}]}},{"TestIngredientC":{}}]}}}`);
            expect(hookA.bake).callCount(1);
            expect(hookB.bake).callCount(1);
            expect(hookC.bake).callCount(1);

            expect(hookA.bake).calledBefore(hookB.bake as any);
            expect(hookB.bake).calledBefore(hookC.bake as any);
        });

        it("should pass the ingredient's properties to the bake method", () => {
            const hook = new TestIngredientHookA();
            spy(hook, "bake");
            oven.registerHook(hook);

            // perform the deserialization that a generated ingredient would normally do
            const deserialized = new TestIngredientASnapshot();
            (deserialized as any).properties.set("foo", "bar");
            (deserialized as any).properties.set("moo", 5);
            const fromJSON = fake.returns(deserialized);
            replace(TestIngredientASnapshot, "fromJSON", fromJSON);

            oven.bake(`{"recipe":{"Recipe":{"ingredients":[{"TestIngredientA":{"foo": "bar", "moo": 5}}]}}}`);
            console.log((hook.bake as SinonSpy).args[0][0]);
            expect((hook.bake as SinonSpy).args[0][0]).to.deep.equal({foo: "bar", moo: 5});

            restore();
        });

        it("should propagate the serialized cake to the hook", () => {
            const hook = new TestIngredientHookA();
            spy(hook, "bake");
            oven.registerHook(hook);

            oven.bake(`{"recipe":{"Recipe":{"ingredients":[{"TestIngredientA":{}}]}}, "cake":{"foo":"bar"}}`);
            expect(hook.bake).calledWith();
            const spyCall = (hook.bake as SinonSpy).getCall(0);
            const expectedCake = new Cake();
            expectedCake.publish("foo", "bar");

            expect(spyCall.args[1]).to.deep.equal(expectedCake);
        });

        it("should serialize additions to the cake", () => {
            const hook = new TestIngredientHookA();
            hook.bake = (_, cake: Cake) => {
                cake.publish("json", "bearded");
            };
            oven.registerHook(hook);

            const bakedCake = Cake.fromJSON(JSON.parse(oven.bake(`{"recipe":{"Recipe":{"ingredients":[{"TestIngredientA":{}}]}}, "cake":{"foo":"bar"}}`)));
            expect(bakedCake.get("foo")).to.equal("bar");
            expect(bakedCake.get("json")).to.equal("bearded");
        });

        it("should bake ingredients in the context of their recipe", () => {
            const hook = new TestIngredientHookA();
            hook.bake = (_, cake: Cake) => {
                cake.publish("json", "bearded");
            };
            oven.registerHook(hook);

            const bakedCake = Cake.fromJSON(JSON.parse(oven.bake(`{"recipe":{"Recipe":{"context":"foo","ingredients":[{"TestIngredientA":{}}]}}, "cake":{}}`)));
            expect(bakedCake.get(Cake.key("foo", "json"))).to.equal("bearded");
        });
    });
});