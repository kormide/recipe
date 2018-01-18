import { expect } from "chai";
import {
    EmptyIngredient, IngredientWithOptional, IngredientWithDefaultRequired, IngredientWithRequired, TestEnum,
    IngredientWithRepeatableOptional, IngredientWithRepeatableVarargOptional, IngredientWithRequiredAndOptional,
    AllParamsIngredient, IngredientWithCompoundOptional, IngredientWithRepeatableCompoundOptional,
    IngredientWithCompoundOptionalWithOneParam, IngredientWithDefaultRequiredNoInitializers,
    IngredientWithStringDefaultContainingQuotes, IngredientWithNullStringDefault, IngredientWithConstant,
    KeyedIngredientWithDefaultKey, KeyedIngredientWithDefaultKeyParamIsDefaulted
} from "../target/ingredients";
import { PostfixIngredientFoo } from "../target/ingredients/postfix";

describe("generation", () => {
    it("should generate an empty ingredient", () => {
        new EmptyIngredient();
    });

    it("should generate an ingredient with a required param", () => {
        new IngredientWithRequired("foo");
    });

    it("should generate an ingredient with required params with defaults", () => {
        new IngredientWithDefaultRequired("foo");
        new IngredientWithDefaultRequired(false);
        new IngredientWithDefaultRequired(TestEnum.B);
    });

    it("should generate an ingredient with a null string default param", () => {
        new IngredientWithNullStringDefault();
    });

    it("should generate an ingredient with a required param with a default but no initializers", () => {
        new IngredientWithDefaultRequiredNoInitializers();
    });

    it("should generate an ingredient with an optional param", () => {
        new IngredientWithOptional()
            .withOptional(true);
    });

    it("should generate an ingredient with a repeatable optional param", () => {
        new IngredientWithRepeatableOptional()
            .withOptional(true)
            .withOptional(false);
    });

    it("should generate an ingredient with a repeatable optional vararg param", () => {
        new IngredientWithRepeatableVarargOptional()
            .withOptional(1, 2, 3)
            .withOptional(4, 5, 6);
    });

    it("should generate ingredient optionals that return the same class", () => {
        expect(new IngredientWithOptional().withOptional(true) instanceof IngredientWithOptional).to.equal(true);
    });

    it("should generate ingredient optionals that return a new instance of the same class", () => {
        const ingredient = new IngredientWithOptional();
        const withValue = ingredient.withOptional(true);
        expect(ingredient === withValue).to.equal(false);
    });

    it("should generate an ingredient with optional and required params", () => {
        new IngredientWithRequiredAndOptional("foo")
            .withOptional(false);
    });

    it("should generate an ingredient with string params", () => {
        new AllParamsIngredient().withStringArg("foo");
    });

    it("should generate an ingredient with int params", () => {
        new AllParamsIngredient().withIntArg(5);
    });

    it("should generate an ingredient with float params", () => {
        new AllParamsIngredient().withFloatArg(123.4);
    });

    it("should generate an ingredient with flag params", () => {
        new AllParamsIngredient().withFlagArg();
    });

    it("should generate an ingredient with enum params", () => {
        new AllParamsIngredient().withEnumArg(TestEnum.C);
    });

    it("should generate an ingredient with primitive array params", () => {
        new AllParamsIngredient().withStringArrayArg(["a", "b", "c"]);
    });

    it("should generate an ingredient with enum array params", () => {
        new AllParamsIngredient().withEnumArrayArg([TestEnum.A, TestEnum.B]);
    });

    it("should generate an ingredient with vararg params", () => {
        new AllParamsIngredient().withVarargArg("a", "b", "c");
    });

    it("should generate an ingredient with vararg array params", () => {
        new AllParamsIngredient().withVarargArrayArg([1, 2], [3]);
    });

    it("should generate an ingredient with a compound optional param", () => {
        new IngredientWithCompoundOptional()
            .withCompoundOptional(-5, true);
    });

    it("should generate an ingredient with a repeatable compound optional param", () => {
        new IngredientWithRepeatableCompoundOptional()
            .withCompoundOptional(1, false)
            .withCompoundOptional(0.0043, true);
    });

    it("should generate and ingredient with a string default containing quotes", () => {
        new IngredientWithStringDefaultContainingQuotes();
    });

    it("should generate ingredients with the correct domain", () => {
        const ingredient = new AllParamsIngredient();
        expect(ingredient.getDomain()).to.equal("TestDomain");
    });

    it("should generate an ingredient with a postfix", () => {
        new PostfixIngredientFoo();
    });

    it("should not include an ingredient postfix in the type", () => {
        const ingredient = new PostfixIngredientFoo();
        expect(ingredient.getIngredientType()).to.equal("PostfixIngredient");
    });

    it("should generate key constants", () => {
        expect(IngredientWithConstant.FOO).to.equal("bar");
    });

    it("should allow string params to take null", () => {
        new IngredientWithRequired(null);
        new AllParamsIngredient().withStringArg(null);
    });

    it("should allow string array params to take null", () => {
        new AllParamsIngredient().withStringArrayArg([null, "foo", null]);
    });
});

describe("serialization", () => {
    it("should serialize an empty ingredient", () => {
        expectJsonEquals(`{"EmptyIngredient":{}}`, new EmptyIngredient());
    });

    it("should serialize an ingredient with a required param", () => {
        expectJsonEquals(`{"IngredientWithRequired":{"required":"foo"}}`, new IngredientWithRequired("foo"));
    });

    it("should serialize an ingredient with required params with defaults", () => {
         expectJsonEquals(`{"IngredientWithDefaultRequired":{"param1":"foobar","param2":false,"param3":"A"}}`, new IngredientWithDefaultRequired(false));
    });

    it("should serialize an ingredient with a required param with a default but no initializers", () => {
        expectJsonEquals(`{"IngredientWithDefaultRequiredNoInitializers":{"required":5}}`, new IngredientWithDefaultRequiredNoInitializers());
    });

    it("should serialize an ingredient with a null string default param", () => {
        expectJsonEquals(`{"IngredientWithNullStringDefault":{"required":null}}`, new IngredientWithNullStringDefault());
    });

    it("should serialize an ingredient with an optional param", () => {
        expectJsonEquals(`{"IngredientWithOptional":{"optional":true}}`, new IngredientWithOptional().withOptional(true));
    });

    it("should serialize an ingredient with a repeatable optional param", () => {
        expectJsonEquals(`{"IngredientWithRepeatableOptional":{"optional":[true, false]}}`, new IngredientWithRepeatableOptional().withOptional(true).withOptional(false));
    });

    it("should serialize an ingredient with a repeatable vararg optional param", () => {
        expectJsonEquals(`{"IngredientWithRepeatableVarargOptional":{"optional":[[1,2],[5]]}}`, new IngredientWithRepeatableVarargOptional().withOptional(1, 2).withOptional(5));
    });

    it("should serialize an ingredient with a compound optional param", () => {
        expectJsonEquals(`{"IngredientWithCompoundOptional":{"compoundOptional":{"param1":-1,"param2":false}}}`, new IngredientWithCompoundOptional().withCompoundOptional(-1, false));
    });

    it("should serialize an ingredient with a repeatable compound optional param", () => {
        expectJsonEquals(`{"IngredientWithRepeatableCompoundOptional":{"compoundOptional":[{"param1":-1,"param2":false},{"param1":5,"param2":true}]}}`, new IngredientWithRepeatableCompoundOptional().withCompoundOptional(-1, false).withCompoundOptional(5, true));
    });

    it("should serialize an ingredient with a compound optional with one param", () => {
        expectJsonEquals(`{"IngredientWithCompoundOptionalWithOneParam":{"compoundOptional":{"param1":5}}}`, new IngredientWithCompoundOptionalWithOneParam().withCompoundOptional(5));
    });

    it("should serialize an ingredient with required and optional params", () => {
        expectJsonEquals(`{"IngredientWithRequiredAndOptional":{"required":"foo","optional":true}}`, new IngredientWithRequiredAndOptional("foo").withOptional(true));
    });

    it("should serialize all ingredient parameter types", () => {
        expectJsonEquals(
            `{"AllParamsIngredient":{"booleanArg":true,"enumArg":"B","flagArg":true,"stringArg":"foobar","intArg":-10,"floatArg":1.123,"enumArrayArg":["A","B"],"varargArg":["foo","bar"],"varargArrayArg":[[1,2],[3,4]]}}`,
            new AllParamsIngredient()
                .withBooleanArg(true)
                .withEnumArg(TestEnum.B)
                .withFlagArg()
                .withStringArg("foobar")
                .withIntArg(-10)
                .withFloatArg(1.123)
                .withEnumArrayArg([TestEnum.A, TestEnum.B])
                .withVarargArg("foo", "bar")
                .withVarargArrayArg([1, 2], [3, 4])
        );
    });

    it("should serialize an ingredient with a string default containing quotes", () => {
        expectJsonEquals(`{"IngredientWithStringDefaultContainingQuotes":{"required":"\\"foo"}}`, new IngredientWithStringDefaultContainingQuotes());
    });

    it("should serialize an ingredient with a null string value", () => {
        expectJsonEquals(`{"AllParamsIngredient":{"stringArg":null}}`, new AllParamsIngredient().withStringArg(null));
    });

    it("should serialize an ingredient with an array of string values with nulls", () => {
        expectJsonEquals(`{"AllParamsIngredient":{"stringArrayArg":["foo", null, "bar"]}}`, new AllParamsIngredient().withStringArrayArg(["foo", null, "bar"]));
    });

    it("should serialize a keyed ingredient with a default key", () => {
        expectJsonEquals(`{"KeyedIngredientWithDefaultKey":{"key":"foo","required":"foo"}}`, new KeyedIngredientWithDefaultKey("foo"));
    });

    it("should serialize a keyed ingredient with a default key which is a defaulted param", () => {
        expectJsonEquals(`{"KeyedIngredientWithDefaultKeyParamIsDefaulted":{"key":"foo","required":"foo"}}`, new KeyedIngredientWithDefaultKeyParamIsDefaulted());
    });
});

export function expectJsonEquals(expected: string, actual: any) {
    const expectedObj = JSON.parse(expected);
    expect(JSON.parse(JSON.stringify(actual))).to.deep.equal(expectedObj);
}