import { expect, use } from "chai";
import { describe, it } from "mocha";
import { spy } from "sinon";
import * as sinnonChai from "sinon-chai";

import { Cake } from "./Cake";
import { DirectDispatchOven } from "./DirectDispatchOven";
import { Recipe } from "./Recipe";
import { Ingredient } from "./Ingredient";
import { BackendOven } from "./BackendOven";
import { BaseIngredientHook } from "./BaseIngredientHook";

use(sinnonChai);

class TestIngredient extends Ingredient {
    constructor() {
        super("TestIngredient", "TestDomain");
    }
}

class TestIngredientData extends Ingredient {
    constructor() {
        super("TestIngredient", "TestDomain");
    }

    public static fromJSON(json: any) {
        return new TestIngredientData();
    }
}

class TestIngredientHook extends BaseIngredientHook<TestIngredientData> {
    constructor() {
        super("TestIngredient", TestIngredientData);
    }

    public bake(ingredient: TestIngredientData, cake: Cake) {
        return cake;
    }
}

describe("DirectDispatchOven", () => {
    it("should dispatch directly to the backend oven for baking", () => {
        const backendOven = new BackendOven();
        backendOven.registerHook(new TestIngredientHook());
        spy(backendOven, "bake");
        const oven = new DirectDispatchOven(backendOven);

        return oven.bake(Recipe.prepare(new TestIngredient())).then(() => {
            expect(backendOven.bake).callCount(1);
        });
    });

    it("should disallow setting a default dispatcher", () => {
        const oven = new DirectDispatchOven(new BackendOven());

        expect(() => oven.setDefaultDispatcher(payload => Promise.resolve("{}"))).to.throw();
    });

    it("should disallow adding a domain dispatcher", () => {
        const oven = new DirectDispatchOven(new BackendOven());

        expect(() => oven.addDispatcher("FooDomain", payload => Promise.resolve("{}"))).to.throw();
    });
});