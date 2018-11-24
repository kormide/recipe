import { expect, use } from "chai";
import { spy, SinonSpy } from "sinon";
import * as sinnonChai from "sinon-chai";

import { Cake } from "./Cake";
import { Recipe } from "./Recipe";
import { Payload } from "./Payload";

use(sinnonChai);

describe("Payload", () => {
    describe("fromJSON", () => {
        let cakeFromJson: (json: any) => Cake;
        let recipeFromJson: (json: any, ingredientTypes: {[key: string]: Function}) => Recipe;

        beforeEach(() => {
            cakeFromJson = Cake.fromJSON;
            recipeFromJson = Recipe.fromJSON;
            Cake.fromJSON = () => new Cake();
            Recipe.fromJSON = () => Recipe.prepare();
        });

        afterEach(() => {
            Cake.fromJSON = cakeFromJson;
            Recipe.fromJSON = recipeFromJson;
        });

        it("should set the recipe and cake to result of Recipe.fromJSON and Cake.fromJSON", () => {
            spy(Cake, "fromJSON");
            spy(Recipe, "fromJSON");

            Payload.fromJSON(JSON.parse(`{"recipe":{"Recipe":{"ingredients":[]}},"cake":{"foo":"bar"}}`), {});

            expect(Cake.fromJSON).callCount(1);
            expect((Cake.fromJSON as SinonSpy).getCall(0).args[0]).to.deep.equal({foo: "bar"});

            expect(Recipe.fromJSON).callCount(1);
            expect((Recipe.fromJSON as SinonSpy).getCall(0).args[0]).to.deep.equal({Recipe: {ingredients: []}});
        });
    });
});