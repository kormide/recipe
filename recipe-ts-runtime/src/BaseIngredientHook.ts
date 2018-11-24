import { Cake } from "./Cake";

export abstract class BaseIngredientHook<T> {
    public constructor(private ingredientName: string, private snapshotClass: Function) {}

    public getIngredientName(): string {
        return this.ingredientName;
    }

    public getSnapshotClass(): Function {
        return this.snapshotClass;
    }

    public abstract bake(ingredient: T, cake: Cake): void;
}