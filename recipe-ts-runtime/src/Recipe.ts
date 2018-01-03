import { Ingredient } from "./Ingredient";
import { KeyedIngredient } from "./KeyedIngredient";

export interface Segment {
    domain: string | null;
    recipe: Recipe;
}

export class Recipe extends Ingredient {
    private readonly ingredients: Ingredient[];
    private context: string | null = null;
    private contextIngredient: KeyedIngredient | null = null;

    public static prepare(...ingredients: Ingredient[]): Recipe {
        return new Recipe(...ingredients);
    }

    public static context(context: string | KeyedIngredient, ...ingredients: Ingredient[]): Recipe {
        const recipe = new Recipe(...ingredients);
        recipe.context = (typeof context === "string") ? context : null;
        recipe.contextIngredient = (context instanceof KeyedIngredient) ? context : null;
        return recipe;
    }

    public getIngredients(): Ingredient[] {
        return this.ingredients;
    }

    public getContext(): string | null {
        return this.context;
    }

    public getContextIngredient(): KeyedIngredient | null {
        return this.contextIngredient;
    }

    public segment(): Segment[] {
        const segments: Segment[] = [];
        const recipeStack: Recipe[] = [];
        recipeStack.push(this);
        this._segment(new Recipe(), recipeStack, null, segments);
        return segments;
    }

    private _segment(currRecipe: Recipe | null, recipeStack: Recipe[], currDomain: string | null, segments: Segment[]) {
        const contextIngredient = recipeStack[0].contextIngredient;
        if (contextIngredient !== null) {
            if (contextIngredient.getDomain() !== currDomain) {
                //copy recipe structure
                let outerRecipe: Recipe | null = null;
                let recipe: Recipe | null = null;
                for (const r of recipeStack) {
                    if (recipe === null) {
                        outerRecipe = new Recipe();
                        recipe = outerRecipe;
                    }
                    else {
                        outerRecipe = Recipe.prepare(outerRecipe!);
                    }
                    outerRecipe.context = r.context;
                    outerRecipe.contextIngredient = r.contextIngredient;
                }

                const segmented: Segment = {
                    domain: contextIngredient.getDomain(),
                    recipe: outerRecipe!
                };
                segments.push(segmented);

                currRecipe = recipe;
                currDomain = contextIngredient.getDomain();
            }

            currRecipe!.contextIngredient = contextIngredient;
        }
        for (const ingredient of recipeStack[0].ingredients) {
            if (ingredient instanceof Recipe) {
                let recipe = new Recipe();
                recipe.contextIngredient = ingredient.contextIngredient;
                recipe.context = ingredient.context;
                recipeStack.unshift(ingredient);
                this._segment(recipe, recipeStack, currDomain, segments);

                if (recipe.ingredients.length !== 0) {
                    currRecipe!.ingredients.push(recipe);
                }
            }
            else {
                if (ingredient.getDomain() !== currDomain) {
                    //copy recipe structure
                    let outerRecipe: Recipe | null = null;
                    let recipe: Recipe | null = null;
                    for (const r of recipeStack) {
                        if (recipe === null) {
                            outerRecipe = new Recipe();
                            recipe = outerRecipe;
                        }
                        else {
                            outerRecipe = Recipe.prepare(outerRecipe!);
                        }
                        outerRecipe.context = r.context;
                        outerRecipe.contextIngredient = r.contextIngredient;
                    }

                    const segmented: Segment = {
                        domain: ingredient.getDomain(),
                        recipe: outerRecipe!
                    };
                    segments.push(segmented);

                    currRecipe = recipe;
                    currDomain = ingredient.getDomain();
                }

                currRecipe!.ingredients.push(ingredient);
            }
        }
        recipeStack.unshift();
    }

    public toJSON() {
        const jsonObj = super.toJSON();
        jsonObj[this.getIngredientType()].ingredients = this.ingredients;

        if (this.context) {
            jsonObj[this.getIngredientType()].context = this.context;
        }
        else if (this.contextIngredient) {
            jsonObj[this.getIngredientType()].contextIngredient = this.contextIngredient.toJSON();
        }

        return jsonObj;
    }

    private constructor(...ingredients: Ingredient[]) {
        super("Recipe");
        this.ingredients = ingredients;
    }
}