import { Cake } from "./Cake";
import { Recipe } from "./Recipe";

export type Dispatcher = (domain: string | null, payload: string) => string;

export class Oven {
    private readonly dispatchers: Dispatcher[] = [];

    public bake(recipe: Recipe): Cake {
        let cake = new Cake();
        for (const segment of recipe.segment()) {
            const payload = `{"recipe":${JSON.stringify(segment.recipe)},"cake":${JSON.stringify(cake)}}`;
            for (const dispatcher of this.dispatchers) {
                cake = Cake.fromJson(dispatcher(segment.domain, payload));
            }
        }
        return cake;
    }

    public addDispatcher(dispatcher: Dispatcher) {
        this.dispatchers.push(dispatcher);
    }
}