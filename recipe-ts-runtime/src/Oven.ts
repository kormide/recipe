import { Cake } from "./Cake";
import { Recipe } from "./Recipe";

export class Oven {
    public bake(recipe: Recipe): Cake {
        return new Cake();
    }
}