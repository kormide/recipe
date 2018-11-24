import { Cake } from "./Cake";

export abstract class AbstractOven {
    protected createCake(other?: Cake): Cake {
        return new Cake(other);
    }
}