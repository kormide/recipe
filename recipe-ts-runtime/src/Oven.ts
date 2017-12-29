import { Cake } from "./Cake";
import { Recipe } from "./Recipe";

export type Dispatcher = (payload: string) => string;

export class Oven {
    private readonly dispatchers: {[key: string]: Dispatcher} = {};

    public bake(recipe: Recipe): Cake {
        let cake = new Cake();
        for (const segment of recipe.segment()) {
            const payload = `{"recipe":${JSON.stringify(segment.recipe)},"cake":${JSON.stringify(cake)}}`;
            if (!(segment.domain! in this.dispatchers)) {
                throw new Error(`cannot dispatch ingredient; no dispatcher registered for domain '${segment.domain}'`);
            }
            cake = Cake.fromJson(this.dispatchers[segment.domain!](payload));
        }
        return cake;
    }

    public addDispatcher(domain: string, dispatcher: Dispatcher) {
        if (domain in this.dispatchers) {
            throw new Error(`oven already has a dispatcher for domain '${domain}'`);
        }

        this.dispatchers[domain] = dispatcher;
    }
}