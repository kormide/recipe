import { AbstractOven } from "./AbstractOven";
import { Payload } from "./Payload";
import { Cake } from "./Cake";
import { BaseIngredientHook } from "./BaseIngredientHook";
import { Ingredient } from "./Ingredient";
import { Recipe } from "./Recipe";

export class BackendOven extends AbstractOven {
    private readonly hooks: {[name: string]: BaseIngredientHook<any>} = {};
    private readonly ingredientTypes: {[name: string]: Function} = {};

    public bake(json: string): string {
        const payload = Payload.fromJSON(JSON.parse(json), this.ingredientTypes);
        const cake = this.createCake(payload.getCake());

        this.bakeIngredient(payload.getRecipe(), cake);

        const cakeToSerialize = new Cake(cake);
        return JSON.stringify(cakeToSerialize);
    }

    public registerHook<T>(hook: BaseIngredientHook<T>) {
        this.hooks[hook.getIngredientName()] = hook;
        this.ingredientTypes[hook.getIngredientName()] = hook.getSnapshotClass();
    }

    private bakeIngredient(ingredient: Ingredient, cake: Cake) {
        if (ingredient instanceof Recipe) {
            const recipe = ingredient as Recipe;

            const bakeRecipeIngredients = () => {
                for (let i of recipe.getIngredients()) {
                    this.bakeIngredient(i, cake);
                }
            };

            if (recipe.getContext() !== null) {
                cake.inNamespace(recipe.getContext()!, bakeRecipeIngredients);
            }
            else {
                bakeRecipeIngredients();
            }
        }
        else {
            const ingredientData = ingredient.toJSON()[ingredient.getIngredientType()];
            this.hooks[ingredient.getIngredientType()].bake(ingredientData, cake);
        }
    }
}