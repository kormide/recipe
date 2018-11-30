const { expect, use } = require("chai");
const { describe, it } = require("mocha");
const { BackendOven } = require("recipe-js-runtime");
const sinnon = require("sinon");
const sinnonChai = require("sinon-chai");

const { AbstractEmptyIngredientHook, AbstractAllParamsIngredientHook, TestEnum, AbstractIngredientWithConstantHook, AbstractIngredientWithRequiredHook, AbstractIngredientWithDefaultRequiredNoInitializersHook, AbstractIngredientWithNullStringDefaultHook, AbstractIngredientWithRequiredAndOptionalHook, AbstractIngredientWithOptionalHook, AbstractIngredientWithRepeatableOptionalHook, AbstractIngredientWithRepeatableVarargOptionalHook, AbstractIngredientWithCompoundOptionalHook, AbstractIngredientWithRepeatableCompoundOptionalHook, AbstractIngredientWithStringDefaultContainingQuotesHook, AbstractIngredientWithJavaScriptKeywordsHook } = require("../target/hooks");
const { AbstractPostfixIngredientHook } = require("../target/hooks/postfix");

use(sinnonChai);

describe("generation", () => {
    it("should generate a hook for an empty ingredient", () => {
        class EmptyIngredientHook extends AbstractEmptyIngredientHook {
            bake(data, cake) {}
        }
    });

    it("should generate a hook for an ingrdient with multiple parameters", () => {
        class AllParamsIngredientHook extends AbstractAllParamsIngredientHook {
            bake(data, cake) {}
        }
    });

    it("should be able to register a generated hook in a backend oven", () => {
        class EmptyIngredientHook extends AbstractEmptyIngredientHook {
            bake(data, cake) {}
        }
        const oven = new BackendOven();
        oven.registerHook(new EmptyIngredientHook());
    });

    it("should not add an ingredient postfix to a hook class names", () => {
        class PostfixIngredientHook extends AbstractPostfixIngredientHook {
            bake(data, cake) {}
        }
    });

    it("should generate key constants in hook classes", () => {
        expect(AbstractIngredientWithConstantHook.FOO).to.equal("bar");
    });

    it("should deserialize an empty ingredient", () => {
        class EmptyIngredientHook extends AbstractEmptyIngredientHook {}
        EmptyIngredientHook.prototype.bake = sinnon.fake();

        const backendOven = new BackendOven();
        const hook = new EmptyIngredientHook();
        backendOven.registerHook(hook);

        backendOven.bake(`{"recipe":{"Recipe":{"ingredients":[{"EmptyIngredient":{}}]}},"cake":{}}`);
        expect(hook.bake).callCount(1);
    });

    it("should deserialize an ingredient with a required param", () => {
        class IngredientWithRequiredHook extends AbstractIngredientWithRequiredHook {}
        IngredientWithRequiredHook.prototype.bake = sinnon.fake();

        const backendOven = new BackendOven();
        const hook = new IngredientWithRequiredHook();
        backendOven.registerHook(hook);

        backendOven.bake(`{"recipe":{"Recipe":{"ingredients":[{"IngredientWithRequired":{"required":"foo"}}]}},"cake":{}}`);
        expect(hook.bake.args[0][0]).to.deep.equal({required: "foo"});
    });

    it("should deserialize an ingedient with a default required param and no initializers", () => {
        class IngredientWithDefaultRequiredNoInitializersHook extends AbstractIngredientWithDefaultRequiredNoInitializersHook {}
        IngredientWithDefaultRequiredNoInitializersHook.prototype.bake = sinnon.fake();

        const backendOven = new BackendOven();
        const hook = new IngredientWithDefaultRequiredNoInitializersHook();
        backendOven.registerHook(hook);

        backendOven.bake(`{"recipe":{"Recipe":{"ingredients":[{"IngredientWithDefaultRequiredNoInitializers":{"required":5}}]}},"cake":{}}`);
        expect(hook.bake.args[0][0]).to.deep.equal({required: 5});
    });

    it("should deserialize an ingedient with a null default string", () => {
        class IngredientWithNullStringDefaultHook extends AbstractIngredientWithNullStringDefaultHook {}
        IngredientWithNullStringDefaultHook.prototype.bake = sinnon.fake();

        const backendOven = new BackendOven();
        const hook = new IngredientWithNullStringDefaultHook();
        backendOven.registerHook(hook);

        backendOven.bake(`{"recipe":{"Recipe":{"ingredients":[{"IngredientWithNullStringDefault":{"required":null}}]}},"cake":{}}`);
        expect(hook.bake.args[0][0]).to.deep.equal({required: null});
    });

    it("should deserialize an ingedient a reqiured and optional param", () => {
        class IngredientWithRequiredAndOptionalHook extends AbstractIngredientWithRequiredAndOptionalHook {}
        IngredientWithRequiredAndOptionalHook.prototype.bake = sinnon.fake();

        const backendOven = new BackendOven();
        const hook = new IngredientWithRequiredAndOptionalHook();
        backendOven.registerHook(hook);

        backendOven.bake(`{"recipe":{"Recipe":{"ingredients":[{"IngredientWithRequiredAndOptional":{"required":"foobar","optional":true}}]}},"cake":{}}`);
        expect(hook.bake.args[0][0]).to.deep.equal({required: "foobar", optional: true});
    });

    it("should deserialize an ingedient with an optional param present", () => {
        class IngredientWithOptionalHook extends AbstractIngredientWithOptionalHook {}
        IngredientWithOptionalHook.prototype.bake = sinnon.fake();

        const backendOven = new BackendOven();
        const hook = new IngredientWithOptionalHook();
        backendOven.registerHook(hook);

        backendOven.bake(`{"recipe":{"Recipe":{"ingredients":[{"IngredientWithOptional":{"optional":true}}]}},"cake":{}}`);
        expect(hook.bake.args[0][0]).to.deep.equal({optional: true});
    });

    it("should deserialize an ingedient with an optional param missing", () => {
        class IngredientWithOptionalHook extends AbstractIngredientWithOptionalHook {}
        IngredientWithOptionalHook.prototype.bake = sinnon.fake();

        const backendOven = new BackendOven();
        const hook = new IngredientWithOptionalHook();
        backendOven.registerHook(hook);

        backendOven.bake(`{"recipe":{"Recipe":{"ingredients":[{"IngredientWithOptional":{}}]}},"cake":{}}`);
        expect(hook.bake.args[0][0]).to.deep.equal({});
    });

    it("should deserialize an ingedient with a single value present for a repeatable optional param", () => {
        class IngredientWithRepeatableOptionalHook extends AbstractIngredientWithRepeatableOptionalHook {}
        IngredientWithRepeatableOptionalHook.prototype.bake = sinnon.fake();

        const backendOven = new BackendOven();
        const hook = new IngredientWithRepeatableOptionalHook();
        backendOven.registerHook(hook);

        backendOven.bake(`{"recipe":{"Recipe":{"ingredients":[{"IngredientWithRepeatableOptional":{"optional":[true]}}]}},"cake":{}}`);
        expect(hook.bake.args[0][0]).to.deep.equal({optional: [true]});
    });

    it("should deserialize an ingedient with several values present for a repeatable optional param", () => {
        class IngredientWithRepeatableOptionalHook extends AbstractIngredientWithRepeatableOptionalHook {}
        IngredientWithRepeatableOptionalHook.prototype.bake = sinnon.fake();

        const backendOven = new BackendOven();
        const hook = new IngredientWithRepeatableOptionalHook();
        backendOven.registerHook(hook);

        backendOven.bake(`{"recipe":{"Recipe":{"ingredients":[{"IngredientWithRepeatableOptional":{"optional":[true,false,true]}}]}},"cake":{}}`);
        expect(hook.bake.args[0][0]).to.deep.equal({optional: [true, false, true]});
    });

    it("should deserialize an ingedient with no value present for a repeatable optional param", () => {
        class IngredientWithRepeatableOptionalHook extends AbstractIngredientWithRepeatableOptionalHook {}
        IngredientWithRepeatableOptionalHook.prototype.bake = sinnon.fake();

        const backendOven = new BackendOven();
        const hook = new IngredientWithRepeatableOptionalHook();
        backendOven.registerHook(hook);

        backendOven.bake(`{"recipe":{"Recipe":{"ingredients":[{"IngredientWithRepeatableOptional":{}}]}},"cake":{}}`);
        expect(hook.bake.args[0][0]).to.deep.equal({});
    });

    it("should deserialize an ingedient with a repeatable vararg optional param", () => {
        class IngredientWithRepeatableVarargOptionalHook extends AbstractIngredientWithRepeatableVarargOptionalHook {}
        IngredientWithRepeatableVarargOptionalHook.prototype.bake = sinnon.fake();

        const backendOven = new BackendOven();
        const hook = new IngredientWithRepeatableVarargOptionalHook();
        backendOven.registerHook(hook);

        backendOven.bake(`{"recipe":{"Recipe":{"ingredients":[{"IngredientWithRepeatableVarargOptional":{"optional":[[1,2],[3,4]]}}]}},"cake":{}}`);
        expect(hook.bake.args[0][0]).to.deep.equal({optional: [[1,2], [3,4]]});
    });

    it("should deserialize an ingedient with the value present for a compound optional param", () => {
        class IngredientWithCompoundOptionalHook extends AbstractIngredientWithCompoundOptionalHook {}
        IngredientWithCompoundOptionalHook.prototype.bake = sinnon.fake();

        const backendOven = new BackendOven();
        const hook = new IngredientWithCompoundOptionalHook();
        backendOven.registerHook(hook);

        backendOven.bake(`{"recipe":{"Recipe":{"ingredients":[{"IngredientWithCompoundOptional":{"compoundOptional":{"param1":5,"param2":false}}}]}},"cake":{}}`);
        expect(hook.bake.args[0][0]).to.deep.equal({compoundOptional: {param1: 5, param2: false}});
    });

    it("should deserialize an ingedient with the value missing for a compound optional param", () => {
        class IngredientWithCompoundOptionalHook extends AbstractIngredientWithCompoundOptionalHook {}
        IngredientWithCompoundOptionalHook.prototype.bake = sinnon.fake();

        const backendOven = new BackendOven();
        const hook = new IngredientWithCompoundOptionalHook();
        backendOven.registerHook(hook);

        backendOven.bake(`{"recipe":{"Recipe":{"ingredients":[{"IngredientWithCompoundOptional":{}}]}},"cake":{}}`);
        expect(hook.bake.args[0][0]).to.deep.equal({});
    });

    it("should deserialize an ingedient with a single value present for a repeatable compound optional param", () => {
        class IngredientWithRepeatableCompoundOptionalHook extends AbstractIngredientWithRepeatableCompoundOptionalHook {}
        IngredientWithRepeatableCompoundOptionalHook.prototype.bake = sinnon.fake();

        const backendOven = new BackendOven();
        const hook = new IngredientWithRepeatableCompoundOptionalHook();
        backendOven.registerHook(hook);

        backendOven.bake(`{"recipe":{"Recipe":{"ingredients":[{"IngredientWithRepeatableCompoundOptional":{"compoundOptional":[{"param1":5,"param2":false}]}}]}},"cake":{}}`);
        expect(hook.bake.args[0][0]).to.deep.equal({compoundOptional: [{param1: 5, param2: false}]});
    });

    it("should deserialize an ingedient with several values present for a repeatable compound optional param", () => {
        class IngredientWithRepeatableCompoundOptionalHook extends AbstractIngredientWithRepeatableCompoundOptionalHook {}
        IngredientWithRepeatableCompoundOptionalHook.prototype.bake = sinnon.fake();

        const backendOven = new BackendOven();
        const hook = new IngredientWithRepeatableCompoundOptionalHook();
        backendOven.registerHook(hook);

        backendOven.bake(`{"recipe":{"Recipe":{"ingredients":[{"IngredientWithRepeatableCompoundOptional":{"compoundOptional":[{"param1":5,"param2":false},{"param1":-1,"param2":true}]}}]}},"cake":{}}`);
        expect(hook.bake.args[0][0]).to.deep.equal({compoundOptional: [{param1: 5, param2: false}, {param1: -1, param2: true}]});
    });

    it("should deserialize an ingedient with no value present for a repeatable compound optional param", () => {
        class IngredientWithRepeatableCompoundOptionalHook extends AbstractIngredientWithRepeatableCompoundOptionalHook {}
        IngredientWithRepeatableCompoundOptionalHook.prototype.bake = sinnon.fake();

        const backendOven = new BackendOven();
        const hook = new IngredientWithRepeatableCompoundOptionalHook();
        backendOven.registerHook(hook);

        backendOven.bake(`{"recipe":{"Recipe":{"ingredients":[{"IngredientWithRepeatableCompoundOptional":{}}]}},"cake":{}}`);
        expect(hook.bake.args[0][0]).to.deep.equal({});
    });

    it("should deserialize an ingedient with a string param", () => {
        class AllParamsIngredientHook extends AbstractAllParamsIngredientHook {}
        AllParamsIngredientHook.prototype.bake = sinnon.fake();

        const backendOven = new BackendOven();
        const hook = new AllParamsIngredientHook();
        backendOven.registerHook(hook);

        backendOven.bake(`{"recipe":{"Recipe":{"ingredients":[{"AllParamsIngredient":{"stringArg":"foobar"}}]}},"cake":{}}`);
        expect(hook.bake.args[0][0]).to.deep.equal({stringArg: "foobar"});
    });

    it("should deserialize an ingedient with a string param set to null", () => {
        class AllParamsIngredientHook extends AbstractAllParamsIngredientHook {}
        AllParamsIngredientHook.prototype.bake = sinnon.fake();

        const backendOven = new BackendOven();
        const hook = new AllParamsIngredientHook();
        backendOven.registerHook(hook);

        backendOven.bake(`{"recipe":{"Recipe":{"ingredients":[{"AllParamsIngredient":{"stringArg":null}}]}},"cake":{}}`);
        expect(hook.bake.args[0][0]).to.deep.equal({stringArg: null});
    });

    it("should deserialize an ingedient with an int param", () => {
        class AllParamsIngredientHook extends AbstractAllParamsIngredientHook {}
        AllParamsIngredientHook.prototype.bake = sinnon.fake();

        const backendOven = new BackendOven();
        const hook = new AllParamsIngredientHook();
        backendOven.registerHook(hook);

        backendOven.bake(`{"recipe":{"Recipe":{"ingredients":[{"AllParamsIngredient":{"intArg":100}}]}},"cake":{}}`);
        expect(hook.bake.args[0][0]).to.deep.equal({intArg: 100});
    });

    it("should deserialize an ingedient with a float param", () => {
        class AllParamsIngredientHook extends AbstractAllParamsIngredientHook {}
        AllParamsIngredientHook.prototype.bake = sinnon.fake();

        const backendOven = new BackendOven();
        const hook = new AllParamsIngredientHook();
        backendOven.registerHook(hook);

        backendOven.bake(`{"recipe":{"Recipe":{"ingredients":[{"AllParamsIngredient":{"floatArg":-1.543}}]}},"cake":{}}`);
        expect(hook.bake.args[0][0]).to.deep.equal({floatArg: -1.543});
    });

    it("should deserialize an ingedient with a boolean param", () => {
        class AllParamsIngredientHook extends AbstractAllParamsIngredientHook {}
        AllParamsIngredientHook.prototype.bake = sinnon.fake();

        const backendOven = new BackendOven();
        const hook = new AllParamsIngredientHook();
        backendOven.registerHook(hook);

        backendOven.bake(`{"recipe":{"Recipe":{"ingredients":[{"AllParamsIngredient":{"booleanArg":true}}]}},"cake":{}}`);
        expect(hook.bake.args[0][0]).to.deep.equal({booleanArg: true});
    });

    it("should deserialize an ingedient with a flag param", () => {
        class AllParamsIngredientHook extends AbstractAllParamsIngredientHook {}
        AllParamsIngredientHook.prototype.bake = sinnon.fake();

        const backendOven = new BackendOven();
        const hook = new AllParamsIngredientHook();
        backendOven.registerHook(hook);

        backendOven.bake(`{"recipe":{"Recipe":{"ingredients":[{"AllParamsIngredient":{"flagArg":false}}]}},"cake":{}}`);
        expect(hook.bake.args[0][0]).to.deep.equal({flagArg: false});
    });

    it("should deserialize an ingedient with an enum param", () => {
        class AllParamsIngredientHook extends AbstractAllParamsIngredientHook {}
        AllParamsIngredientHook.prototype.bake = sinnon.fake();

        const backendOven = new BackendOven();
        const hook = new AllParamsIngredientHook();
        backendOven.registerHook(hook);

        backendOven.bake(`{"recipe":{"Recipe":{"ingredients":[{"AllParamsIngredient":{"enumArg":"B"}}]}},"cake":{}}`);
        expect(hook.bake.args[0][0]).to.deep.equal({enumArg: TestEnum.B});
    });

    it("should deserialize an ingedient with a primitive array param", () => {
        class AllParamsIngredientHook extends AbstractAllParamsIngredientHook {}
        AllParamsIngredientHook.prototype.bake = sinnon.fake();

        const backendOven = new BackendOven();
        const hook = new AllParamsIngredientHook();
        backendOven.registerHook(hook);

        backendOven.bake(`{"recipe":{"Recipe":{"ingredients":[{"AllParamsIngredient":{"stringArrayArg":["foo","bar"]}}]}},"cake":{}}`);
        expect(hook.bake.args[0][0]).to.deep.equal({stringArrayArg: ["foo", "bar"]});
    });

    it("should deserialize an ingedient with an enum array param", () => {
        class AllParamsIngredientHook extends AbstractAllParamsIngredientHook {}
        AllParamsIngredientHook.prototype.bake = sinnon.fake();

        const backendOven = new BackendOven();
        const hook = new AllParamsIngredientHook();
        backendOven.registerHook(hook);

        backendOven.bake(`{"recipe":{"Recipe":{"ingredients":[{"AllParamsIngredient":{"enumArrayArg":["B","C"]}}]}},"cake":{}}`);
        expect(hook.bake.args[0][0]).to.deep.equal({enumArrayArg: [TestEnum.B, TestEnum.C]});
    });

    it("should deserialize an ingedient with a vararg param", () => {
        class AllParamsIngredientHook extends AbstractAllParamsIngredientHook {}
        AllParamsIngredientHook.prototype.bake = sinnon.fake();

        const backendOven = new BackendOven();
        const hook = new AllParamsIngredientHook();
        backendOven.registerHook(hook);

        backendOven.bake(`{"recipe":{"Recipe":{"ingredients":[{"AllParamsIngredient":{"varargArg":["foo","bar"]}}]}},"cake":{}}`);
        expect(hook.bake.args[0][0]).to.deep.equal({varargArg: ["foo", "bar"]});
    });

    it("should deserialize an ingedient with a vararg array param", () => {
        class AllParamsIngredientHook extends AbstractAllParamsIngredientHook {}
        AllParamsIngredientHook.prototype.bake = sinnon.fake();

        const backendOven = new BackendOven();
        const hook = new AllParamsIngredientHook();
        backendOven.registerHook(hook);

        backendOven.bake(`{"recipe":{"Recipe":{"ingredients":[{"AllParamsIngredient":{"varargArrayArg":[[1,2],[3,4]]}}]}},"cake":{}}`);
        expect(hook.bake.args[0][0]).to.deep.equal({varargArrayArg: [[1, 2], [3, 4]]});
    });

    it("should deserialize an ingedient with a string default param with a value containing quotes", () => {
        class IngredientWithStringDefaultContainingQuotesHook extends AbstractIngredientWithStringDefaultContainingQuotesHook {}
        IngredientWithStringDefaultContainingQuotesHook.prototype.bake = sinnon.fake();

        const backendOven = new BackendOven();
        const hook = new IngredientWithStringDefaultContainingQuotesHook();
        backendOven.registerHook(hook);

        backendOven.bake(`{"recipe":{"Recipe":{"ingredients":[{"IngredientWithStringDefaultContainingQuotes":{"required":"\\"foo"}}]}},"cake":{}}`);
        expect(hook.bake.args[0][0]).to.deep.equal({required: "\"foo"});
    });

    it("should deserialize an ingedient with javascript keywords", () => {
        class IngredientWithJavaScriptKeywordsHook extends AbstractIngredientWithJavaScriptKeywordsHook {}
        IngredientWithJavaScriptKeywordsHook.prototype.bake = sinnon.fake();

        const backendOven = new BackendOven();
        const hook = new IngredientWithJavaScriptKeywordsHook();
        backendOven.registerHook(hook);

        backendOven.bake(`{"recipe":{"Recipe":{"ingredients":[{"IngredientWithJavaScriptKeywords":{"null":true,"undefined":5,"try":false,"this":{"instanceof":0,"import":null}}}]}},"cake":{}}`);
        expect(hook.bake.args[0][0]).to.deep.equal({
            null: true,
            undefined: 5,
            try: false,
            this: {
                instanceof: 0,
                import: null
            }
        });
    });

    it("should deserialize a cake", () => {
        class EmptyIngredientHook extends AbstractEmptyIngredientHook {}
        EmptyIngredientHook.prototype.bake = sinnon.fake();

        const backendOven = new BackendOven();
        const hook = new EmptyIngredientHook();
        backendOven.registerHook(hook);

        backendOven.bake(`{"recipe":{"Recipe":{"ingredients":[{"EmptyIngredient":{}}]}},"cake":{"someKey":"someValue"}}`);
        const cake = hook.bake.args[0][1];

        expect(cake.get("someKey")).to.equal("someValue");
    });
});