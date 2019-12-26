import { Dispatcher, Oven } from "./Oven";
import { BackendOven } from "./BackendOven";

export class DirectDispatchOven extends Oven {
    constructor(readonly backendOven: BackendOven) {
        super();
        super._setDefaultDispatcher(payload => Promise.resolve(backendOven.bake(payload)));
    }

    public addDispatcher(domain: string, dispatcher: Dispatcher) {
        throw new Error("cannot add dispatchers to a DirectDispatchOven");
    }

    public setDefaultDispatcher(dispatcher?: Dispatcher) {
        throw new Error("cannot overwrite default dispatcher in DirectDispatchOven");
    }
}