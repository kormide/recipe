import { Recipe } from "./Recipe";
import { Cake } from "./Cake";

export class Payload {
    constructor(private recipe: Recipe, private cake: Cake) {}

    public static fromJSON(json: any, ingredientTypes: {[key: string]: Function}): Payload {
        return new Payload(Recipe.fromJSON(json.recipe, ingredientTypes), Cake.fromJSON(json.cake));
    }

    public getRecipe(): Recipe {
        return this.recipe;
    }

    public getCake(): Cake {
        return this.cake;
    }
}