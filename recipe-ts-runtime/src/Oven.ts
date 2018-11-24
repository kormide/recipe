import { Cake } from "./Cake";
import { Recipe } from "./Recipe";
import { AbstractOven } from "./AbstractOven";
import { Payload } from "./Payload";

export type Dispatcher = (payload: string) => Promise<string>;

export class Oven extends AbstractOven {
    private readonly dispatchers: {[key: string]: Dispatcher} = {};

    public bake(recipe: Recipe): Promise<Cake> {
        let promise: Promise<Cake> = Promise.resolve(this.createCake());

        for (const segment of recipe.segment()) {
            if (!(segment.domain! in this.dispatchers)) {
                throw new Error(`cannot dispatch ingredient; no dispatcher registered for domain '${segment.domain}'`);
            }

            promise = promise.then(cake => {
                const payload = new Payload(segment.recipe, cake);
                return this.dispatchers[segment.domain!](JSON.stringify(payload)).then(jsonCake => Cake.fromJSON(JSON.parse(jsonCake)));
            });
        }

        return promise;
    }

    public addDispatcher(domain: string, dispatcher: Dispatcher) {
        if (domain in this.dispatchers) {
            throw new Error(`oven already has a dispatcher for domain '${domain}'`);
        }

        this.dispatchers[domain] = dispatcher;
    }
}