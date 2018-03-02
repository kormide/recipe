import { Cake } from "./Cake";
import { Recipe } from "./Recipe";

export type Dispatcher = (payload: string) => Promise<string>;

export class Oven {
    private readonly dispatchers: {[key: string]: Dispatcher} = {};

    public bake(recipe: Recipe): Promise<Cake> {
        let promise: Promise<string> = Promise.resolve(JSON.stringify(new Cake()));

        for (const segment of recipe.segment()) {
            if (!(segment.domain! in this.dispatchers)) {
                throw new Error(`cannot dispatch ingredient; no dispatcher registered for domain '${segment.domain}'`);
            }

            promise = promise.then(jsonCake => {
                const payload = `{"recipe":${JSON.stringify(segment.recipe)},"cake":${jsonCake}}`;
                return this.dispatchers[segment.domain!](payload);
            });
        }

        return promise.then(jsonCake => Cake.fromJson(jsonCake));
    }

    public addDispatcher(domain: string, dispatcher: Dispatcher) {
        if (domain in this.dispatchers) {
            throw new Error(`oven already has a dispatcher for domain '${domain}'`);
        }

        this.dispatchers[domain] = dispatcher;
    }
}