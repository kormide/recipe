import { expect, use } from "chai";
import { describe } from "mocha";
import { Cake, BackendOven } from "recipe-ts-runtime";
import { fake, SinonSpy } from "sinon";
import * as sinnonChai from "sinon-chai";

import { AbstractEmptyIngredientHook, EmptyIngredientData, AbstractAllParamsIngredientHook, AllParamsIngredientData, IngredientWithRequiredData, IngredientWithRequiredAndOptionalData, IngredientWithRepeatableOptionalData, IngredientWithRepeatableVarargOptionalData, IngredientWithCompoundOptionalData, IngredientWithRepeatableCompoundOptionalData, TestEnum, IngredientWithTypeScriptKeywordsData, AbstractIngredientWithConstantHook, AbstractIngredientWithRequiredHook, AbstractIngredientWithDefaultRequiredNoInitializersHook, AbstractIngredientWithNullStringDefaultHook, AbstractIngredientWithRequiredAndOptionalHook, AbstractIngredientWithOptionalHook, AbstractIngredientWithRepeatableOptionalHook, AbstractIngredientWithRepeatableVarargOptionalHook, AbstractIngredientWithCompoundOptionalHook, AbstractIngredientWithRepeatableCompoundOptionalHook, AbstractIngredientWithStringDefaultContainingQuotesHook, AbstractIngredientWithTypeScriptKeywordsHook } from "../target/hooks";
import { AbstractPostfixIngredientHook, PostfixIngredientData } from "../target/hooks/postfix";

use(sinnonChai);

describe("generation", () => {
    it("should generate a hook for an empty ingredient", () => {
        class EmptyIngredientHook extends AbstractEmptyIngredientHook {
            public bake(data: EmptyIngredientData, cake: Cake) {}
        }
    });

    it("should generate a hook for an ingrdient with multiple parameters", () => {
        class AllParamsIngredientHook extends AbstractAllParamsIngredientHook {
            public bake(data: AllParamsIngredientData, cake: Cake) {}
        }
    });

    it("should be able to register a generated hook in a backend oven", () => {
        class EmptyIngredientHook extends AbstractEmptyIngredientHook {
            public bake(data: EmptyIngredientData, cake: Cake) {}
        }
        const oven = new BackendOven();
        oven.registerHook(new EmptyIngredientHook());
    });

    it("should generate a data interface for an empty ingredient", () => {
        const data: EmptyIngredientData = {};
    });

    it("should generate a data interface for an ingredient with required params", () => {
        const data: IngredientWithRequiredData = {required: "foo"};
    });

    it("should generate a data interface for an ingredient with required and optional params", () => {
        let data: IngredientWithRequiredAndOptionalData;
        data = {required: "foo"};
        data = {required: "foo", optional: true};
    });

    it("should generate a data interface for an ingredient with repeatable optional params", () => {
        const data: IngredientWithRepeatableOptionalData = {optional: [true, false]};
    });

    it("should generate a data interface for an ingredient with repeatable vararg optional params", () => {
        const data: IngredientWithRepeatableVarargOptionalData = {optional: [[1], [2, 3]]};
    });

    it("should generate a data interface for an ingredient with compound optional params", () => {
        const data: IngredientWithCompoundOptionalData = {compoundOptional: {param1: 5, param2: false}};
    });

    it("should generate a data interface for an ingredient with repeatable compound optional params", () => {
        const data: IngredientWithRepeatableCompoundOptionalData = {
            compoundOptional: [{param1: 5, param2: false}, {param1: 0, param2: true}]
        };
    });

    it("should generate a data interface for an ingredient with a float param", () => {
        const data: AllParamsIngredientData = {floatArg: 2.0};
    });

    it("should generate a data interface for an ingredient with a string param", () => {
        const data: AllParamsIngredientData = {stringArg: "foo"};
    });

    it("should generate a data interface for an ingredient with a boolean param", () => {
        const data: AllParamsIngredientData = {booleanArg: true};
    });

    it("should generate a data interface for an ingredient with a flag param", () => {
        const data: AllParamsIngredientData = {flagArg: false};
    });

    it("should generate a data interface for an ingredient with an enum param", () => {
        const data: AllParamsIngredientData = {enumArg: TestEnum.B};
    });

    it("should generate a data interface for an ingredient with a primitive array param", () => {
        const data: AllParamsIngredientData = {stringArrayArg: ["foo", "bar"]};
    });

    it("should generate a data interface for an ingredient with an enum array param", () => {
        const data: AllParamsIngredientData = {enumArrayArg: [TestEnum.A, TestEnum.C]};
    });

    it("should generate a data interface for an ingredient with a vararg param", () => {
        const data: AllParamsIngredientData = {varargArg: ["foo", "bar"]};
    });

    it("should generate a data interface for an ingredient with a vararg array param", () => {
        const data: AllParamsIngredientData = {varargArrayArg: [[1, 2], [3]]};
    });

    it("should generate a data interface for an ingredient with typescript keyworkds", () => {
        const data: IngredientWithTypeScriptKeywordsData = {
            any: true,
            const: 5,
            number: false,
            delete: {
                enum: 0,
                false: "foobar"
            }
        };
    });

    it("should not add an ingredient postfix to a hook class names", () => {
        AbstractPostfixIngredientHook;
        let foo: PostfixIngredientData;
    });

    it("should generate key constants in hook classes", () => {
        expect(AbstractIngredientWithConstantHook.FOO).to.equal("bar");
    });

    it("should deserialize an empty ingredient", () => {
        class EmptyIngredientHook extends AbstractEmptyIngredientHook {
            public bake = fake();
        }
        const backendOven = new BackendOven();
        const hook = new EmptyIngredientHook();
        backendOven.registerHook(hook);

        backendOven.bake(`{"recipe":{"Recipe":{"ingredients":[{"EmptyIngredient":{}}]}},"cake":{}}`);
        expect(hook.bake).callCount(1);
    });

    it("should deserialize an ingredient with a required param", () => {
        class IngredientWithRequiredHook extends AbstractIngredientWithRequiredHook {
            public bake = fake();
        }
        const backendOven = new BackendOven();
        const hook = new IngredientWithRequiredHook();
        backendOven.registerHook(hook);

        backendOven.bake(`{"recipe":{"Recipe":{"ingredients":[{"IngredientWithRequired":{"required":"foo"}}]}},"cake":{}}`);
        expect(hook.bake.args[0][0]).to.deep.equal({required: "foo"});
    });

    it("should deserialize an ingedient with a default required param and no initializers", () => {
        class IngredientWithDefaultRequiredNoInitializersHook extends AbstractIngredientWithDefaultRequiredNoInitializersHook {
            public bake = fake();
        }
        const backendOven = new BackendOven();
        const hook = new IngredientWithDefaultRequiredNoInitializersHook();
        backendOven.registerHook(hook);

        backendOven.bake(`{"recipe":{"Recipe":{"ingredients":[{"IngredientWithDefaultRequiredNoInitializers":{"required":5}}]}},"cake":{}}`);
        expect(hook.bake.args[0][0]).to.deep.equal({required: 5});
    });

    it("should deserialize an ingedient with a null default string", () => {
        class IngredientWithNullStringDefaultHook extends AbstractIngredientWithNullStringDefaultHook {
            public bake = fake();
        }
        const backendOven = new BackendOven();
        const hook = new IngredientWithNullStringDefaultHook();
        backendOven.registerHook(hook);

        backendOven.bake(`{"recipe":{"Recipe":{"ingredients":[{"IngredientWithNullStringDefault":{"required":null}}]}},"cake":{}}`);
        expect(hook.bake.args[0][0]).to.deep.equal({required: null});
    });

    it("should deserialize an ingedient a reqiured and optional param", () => {
        class IngredientWithRequiredAndOptionalHook extends AbstractIngredientWithRequiredAndOptionalHook {
            public bake = fake();
        }
        const backendOven = new BackendOven();
        const hook = new IngredientWithRequiredAndOptionalHook();
        backendOven.registerHook(hook);

        backendOven.bake(`{"recipe":{"Recipe":{"ingredients":[{"IngredientWithRequiredAndOptional":{"required":"foobar","optional":true}}]}},"cake":{}}`);
        expect(hook.bake.args[0][0]).to.deep.equal({required: "foobar", optional: true});
    });

    it("should deserialize an ingedient with an optional param present", () => {
        class IngredientWithOptionalHook extends AbstractIngredientWithOptionalHook {
            public bake = fake();
        }
        const backendOven = new BackendOven();
        const hook = new IngredientWithOptionalHook();
        backendOven.registerHook(hook);

        backendOven.bake(`{"recipe":{"Recipe":{"ingredients":[{"IngredientWithOptional":{"optional":true}}]}},"cake":{}}`);
        expect(hook.bake.args[0][0]).to.deep.equal({optional: true});
    });

    it("should deserialize an ingedient with an optional param missing", () => {
        class IngredientWithOptionalHook extends AbstractIngredientWithOptionalHook {
            public bake = fake();
        }
        const backendOven = new BackendOven();
        const hook = new IngredientWithOptionalHook();
        backendOven.registerHook(hook);

        backendOven.bake(`{"recipe":{"Recipe":{"ingredients":[{"IngredientWithOptional":{}}]}},"cake":{}}`);
        expect(hook.bake.args[0][0]).to.deep.equal({});
    });

    it("should deserialize an ingedient with a single value present for a repeatable optional param", () => {
        class IngredientWithRepeatableOptionalHook extends AbstractIngredientWithRepeatableOptionalHook {
            public bake = fake();
        }
        const backendOven = new BackendOven();
        const hook = new IngredientWithRepeatableOptionalHook();
        backendOven.registerHook(hook);

        backendOven.bake(`{"recipe":{"Recipe":{"ingredients":[{"IngredientWithRepeatableOptional":{"optional":[true]}}]}},"cake":{}}`);
        expect(hook.bake.args[0][0]).to.deep.equal({optional: [true]});
    });

    it("should deserialize an ingedient with several values present for a repeatable optional param", () => {
        class IngredientWithRepeatableOptionalHook extends AbstractIngredientWithRepeatableOptionalHook {
            public bake = fake();
        }
        const backendOven = new BackendOven();
        const hook = new IngredientWithRepeatableOptionalHook();
        backendOven.registerHook(hook);

        backendOven.bake(`{"recipe":{"Recipe":{"ingredients":[{"IngredientWithRepeatableOptional":{"optional":[true,false,true]}}]}},"cake":{}}`);
        expect(hook.bake.args[0][0]).to.deep.equal({optional: [true, false, true]});
    });

    it("should deserialize an ingedient with no value present for a repeatable optional param", () => {
        class IngredientWithRepeatableOptionalHook extends AbstractIngredientWithRepeatableOptionalHook {
            public bake = fake();
        }
        const backendOven = new BackendOven();
        const hook = new IngredientWithRepeatableOptionalHook();
        backendOven.registerHook(hook);

        backendOven.bake(`{"recipe":{"Recipe":{"ingredients":[{"IngredientWithRepeatableOptional":{}}]}},"cake":{}}`);
        expect(hook.bake.args[0][0]).to.deep.equal({});
    });

    it("should deserialize an ingedient with a repeatable vararg optional param", () => {
        class IngredientWithRepeatableVarargOptionalHook extends AbstractIngredientWithRepeatableVarargOptionalHook {
            public bake = fake();
        }
        const backendOven = new BackendOven();
        const hook = new IngredientWithRepeatableVarargOptionalHook();
        backendOven.registerHook(hook);

        backendOven.bake(`{"recipe":{"Recipe":{"ingredients":[{"IngredientWithRepeatableVarargOptional":{"optional":[[1,2],[3,4]]}}]}},"cake":{}}`);
        expect(hook.bake.args[0][0]).to.deep.equal({optional: [[1,2], [3,4]]});
    });

    it("should deserialize an ingedient with the value present for a compound optional param", () => {
        class IngredientWithCompoundOptionalHook extends AbstractIngredientWithCompoundOptionalHook {
            public bake = fake();
        }
        const backendOven = new BackendOven();
        const hook = new IngredientWithCompoundOptionalHook();
        backendOven.registerHook(hook);

        backendOven.bake(`{"recipe":{"Recipe":{"ingredients":[{"IngredientWithCompoundOptional":{"compoundOptional":{"param1":5,"param2":false}}}]}},"cake":{}}`);
        expect(hook.bake.args[0][0]).to.deep.equal({compoundOptional: {param1: 5, param2: false}});
    });

    it("should deserialize an ingedient with the value missing for a compound optional param", () => {
        class IngredientWithCompoundOptionalHook extends AbstractIngredientWithCompoundOptionalHook {
            public bake = fake();
        }
        const backendOven = new BackendOven();
        const hook = new IngredientWithCompoundOptionalHook();
        backendOven.registerHook(hook);

        backendOven.bake(`{"recipe":{"Recipe":{"ingredients":[{"IngredientWithCompoundOptional":{}}]}},"cake":{}}`);
        expect(hook.bake.args[0][0]).to.deep.equal({});
    });

    it("should deserialize an ingedient with a single value present for a repeatable compound optional param", () => {
        class IngredientWithRepeatableCompoundOptionalHook extends AbstractIngredientWithRepeatableCompoundOptionalHook {
            public bake = fake();
        }
        const backendOven = new BackendOven();
        const hook = new IngredientWithRepeatableCompoundOptionalHook();
        backendOven.registerHook(hook);

        backendOven.bake(`{"recipe":{"Recipe":{"ingredients":[{"IngredientWithRepeatableCompoundOptional":{"compoundOptional":[{"param1":5,"param2":false}]}}]}},"cake":{}}`);
        expect(hook.bake.args[0][0]).to.deep.equal({compoundOptional: [{param1: 5, param2: false}]});
    });

    it("should deserialize an ingedient with several values present for a repeatable compound optional param", () => {
        class IngredientWithRepeatableCompoundOptionalHook extends AbstractIngredientWithRepeatableCompoundOptionalHook {
            public bake = fake();
        }
        const backendOven = new BackendOven();
        const hook = new IngredientWithRepeatableCompoundOptionalHook();
        backendOven.registerHook(hook);

        backendOven.bake(`{"recipe":{"Recipe":{"ingredients":[{"IngredientWithRepeatableCompoundOptional":{"compoundOptional":[{"param1":5,"param2":false},{"param1":-1,"param2":true}]}}]}},"cake":{}}`);
        expect(hook.bake.args[0][0]).to.deep.equal({compoundOptional: [{param1: 5, param2: false}, {param1: -1, param2: true}]});
    });

    it("should deserialize an ingedient with no value present for a repeatable compound optional param", () => {
        class IngredientWithRepeatableCompoundOptionalHook extends AbstractIngredientWithRepeatableCompoundOptionalHook {
            public bake = fake();
        }
        const backendOven = new BackendOven();
        const hook = new IngredientWithRepeatableCompoundOptionalHook();
        backendOven.registerHook(hook);

        backendOven.bake(`{"recipe":{"Recipe":{"ingredients":[{"IngredientWithRepeatableCompoundOptional":{}}]}},"cake":{}}`);
        expect(hook.bake.args[0][0]).to.deep.equal({});
    });

    it("should deserialize an ingedient with a string param", () => {
        class AllParamsIngredientHook extends AbstractAllParamsIngredientHook {
            public bake = fake();
        }
        const backendOven = new BackendOven();
        const hook = new AllParamsIngredientHook();
        backendOven.registerHook(hook);

        backendOven.bake(`{"recipe":{"Recipe":{"ingredients":[{"AllParamsIngredient":{"stringArg":"foobar"}}]}},"cake":{}}`);
        expect(hook.bake.args[0][0]).to.deep.equal({stringArg: "foobar"});
    });

    it("should deserialize an ingedient with a string param set to null", () => {
        class AllParamsIngredientHook extends AbstractAllParamsIngredientHook {
            public bake = fake();
        }
        const backendOven = new BackendOven();
        const hook = new AllParamsIngredientHook();
        backendOven.registerHook(hook);

        backendOven.bake(`{"recipe":{"Recipe":{"ingredients":[{"AllParamsIngredient":{"stringArg":null}}]}},"cake":{}}`);
        expect(hook.bake.args[0][0]).to.deep.equal({stringArg: null});
    });

    it("should deserialize an ingedient with an int param", () => {
        class AllParamsIngredientHook extends AbstractAllParamsIngredientHook {
            public bake = fake();
        }
        const backendOven = new BackendOven();
        const hook = new AllParamsIngredientHook();
        backendOven.registerHook(hook);

        backendOven.bake(`{"recipe":{"Recipe":{"ingredients":[{"AllParamsIngredient":{"intArg":100}}]}},"cake":{}}`);
        expect(hook.bake.args[0][0]).to.deep.equal({intArg: 100});
    });

    it("should deserialize an ingedient with a float param", () => {
        class AllParamsIngredientHook extends AbstractAllParamsIngredientHook {
            public bake = fake();
        }
        const backendOven = new BackendOven();
        const hook = new AllParamsIngredientHook();
        backendOven.registerHook(hook);

        backendOven.bake(`{"recipe":{"Recipe":{"ingredients":[{"AllParamsIngredient":{"floatArg":-1.543}}]}},"cake":{}}`);
        expect(hook.bake.args[0][0]).to.deep.equal({floatArg: -1.543});
    });

    it("should deserialize an ingedient with a boolean param", () => {
        class AllParamsIngredientHook extends AbstractAllParamsIngredientHook {
            public bake = fake();
        }
        const backendOven = new BackendOven();
        const hook = new AllParamsIngredientHook();
        backendOven.registerHook(hook);

        backendOven.bake(`{"recipe":{"Recipe":{"ingredients":[{"AllParamsIngredient":{"booleanArg":true}}]}},"cake":{}}`);
        expect(hook.bake.args[0][0]).to.deep.equal({booleanArg: true});
    });

    it("should deserialize an ingedient with a flag param", () => {
        class AllParamsIngredientHook extends AbstractAllParamsIngredientHook {
            public bake = fake();
        }
        const backendOven = new BackendOven();
        const hook = new AllParamsIngredientHook();
        backendOven.registerHook(hook);

        backendOven.bake(`{"recipe":{"Recipe":{"ingredients":[{"AllParamsIngredient":{"flagArg":false}}]}},"cake":{}}`);
        expect(hook.bake.args[0][0]).to.deep.equal({flagArg: false});
    });

    it("should deserialize an ingedient with an enum param", () => {
        class AllParamsIngredientHook extends AbstractAllParamsIngredientHook {
            public bake = fake();
        }
        const backendOven = new BackendOven();
        const hook = new AllParamsIngredientHook();
        backendOven.registerHook(hook);

        backendOven.bake(`{"recipe":{"Recipe":{"ingredients":[{"AllParamsIngredient":{"enumArg":"B"}}]}},"cake":{}}`);
        expect(hook.bake.args[0][0]).to.deep.equal({enumArg: TestEnum.B});
    });

    it("should deserialize an ingedient with a primitive array param", () => {
        class AllParamsIngredientHook extends AbstractAllParamsIngredientHook {
            public bake = fake();
        }
        const backendOven = new BackendOven();
        const hook = new AllParamsIngredientHook();
        backendOven.registerHook(hook);

        backendOven.bake(`{"recipe":{"Recipe":{"ingredients":[{"AllParamsIngredient":{"stringArrayArg":["foo","bar"]}}]}},"cake":{}}`);
        expect(hook.bake.args[0][0]).to.deep.equal({stringArrayArg: ["foo", "bar"]});
    });

    it("should deserialize an ingedient with an enum array param", () => {
        class AllParamsIngredientHook extends AbstractAllParamsIngredientHook {
            public bake = fake();
        }
        const backendOven = new BackendOven();
        const hook = new AllParamsIngredientHook();
        backendOven.registerHook(hook);

        backendOven.bake(`{"recipe":{"Recipe":{"ingredients":[{"AllParamsIngredient":{"enumArrayArg":["B","C"]}}]}},"cake":{}}`);
        expect(hook.bake.args[0][0]).to.deep.equal({enumArrayArg: [TestEnum.B, TestEnum.C]});
    });

    it("should deserialize an ingedient with a vararg param", () => {
        class AllParamsIngredientHook extends AbstractAllParamsIngredientHook {
            public bake = fake();
        }
        const backendOven = new BackendOven();
        const hook = new AllParamsIngredientHook();
        backendOven.registerHook(hook);

        backendOven.bake(`{"recipe":{"Recipe":{"ingredients":[{"AllParamsIngredient":{"varargArg":["foo","bar"]}}]}},"cake":{}}`);
        expect(hook.bake.args[0][0]).to.deep.equal({varargArg: ["foo", "bar"]});
    });

    it("should deserialize an ingedient with a vararg array param", () => {
        class AllParamsIngredientHook extends AbstractAllParamsIngredientHook {
            public bake = fake();
        }
        const backendOven = new BackendOven();
        const hook = new AllParamsIngredientHook();
        backendOven.registerHook(hook);

        backendOven.bake(`{"recipe":{"Recipe":{"ingredients":[{"AllParamsIngredient":{"varargArrayArg":[[1,2],[3,4]]}}]}},"cake":{}}`);
        expect(hook.bake.args[0][0]).to.deep.equal({varargArrayArg: [[1, 2], [3, 4]]});
    });

    it("should deserialize an ingedient with a string default param with a value containing quotes", () => {
        class IngredientWithStringDefaultContainingQuotesHook extends AbstractIngredientWithStringDefaultContainingQuotesHook {
            public bake = fake();
        }
        const backendOven = new BackendOven();
        const hook = new IngredientWithStringDefaultContainingQuotesHook();
        backendOven.registerHook(hook);

        backendOven.bake(`{"recipe":{"Recipe":{"ingredients":[{"IngredientWithStringDefaultContainingQuotes":{"required":"\\"foo"}}]}},"cake":{}}`);
        expect(hook.bake.args[0][0]).to.deep.equal({required: "\"foo"});
    });

    it("should deserialize an ingedient with typescript keywords", () => {
        class IngredientWithTypeScriptKeywordsHook extends AbstractIngredientWithTypeScriptKeywordsHook {
            public bake = fake();
        }
        const backendOven = new BackendOven();
        const hook = new IngredientWithTypeScriptKeywordsHook();
        backendOven.registerHook(hook);

        backendOven.bake(`{"recipe":{"Recipe":{"ingredients":[{"IngredientWithTypeScriptKeywords":{"any":true,"const":5,"number":false,"delete":{"enum":0,"false":null}}}]}},"cake":{}}`);
        expect(hook.bake.args[0][0]).to.deep.equal({
            any: true,
            const: 5,
            number: false,
            delete: {
                enum: 0,
                false: null
            }
        });
    });

    it("should deserialize a cake", () => {
        class EmptyIngredientHook extends AbstractEmptyIngredientHook {
            public bake = fake();
        }
        const backendOven = new BackendOven();
        const hook = new EmptyIngredientHook();
        backendOven.registerHook(hook);

        backendOven.bake(`{"recipe":{"Recipe":{"ingredients":[{"EmptyIngredient":{}}]}},"cake":{"someKey":"someValue"}}`);
        const cake: Cake = (hook.bake as SinonSpy).args[0][1];

        expect(cake.get("someKey")).to.equal("someValue");
    });
});