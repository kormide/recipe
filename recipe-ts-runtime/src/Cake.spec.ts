import { expect } from "chai";

import { Cake } from "./Cake";

describe("Cake", () => {
    let cake: Cake;

    beforeEach(() => {
        cake = new Cake();
    });

    describe("get", () => {
        it("should throw on missing key", () => {
            expect(() => cake.get("foo")).to.throw();
        });

        it("should throw on empty string key", () => {
            expect(() => cake.get("")).to.throw();
        });

        it("should get the value for a key", () => {
            cake.publish("key", "value");
            expect(cake.get("key")).to.equal("value");
        });

        it("should get the value when a subkey contains the namespace separator", () => {
            cake.inNamespace("foo", () => {
                cake.publish("bar", 1);
            });
            expect(cake.get("foo" + Cake.SEPARATOR + "bar")).to.equal(1);
        });

        it("should find a partial key from the root", () => {
            cake.inNamespace("a", () => {
                cake.publish("key", "value");
            });
            expect(cake.get("key")).to.equal("value");
        });

        it("should find a partial key from an unrelated namespace", () => {
            cake.inNamespace("a", () => {
                cake.publish("key", "foo");
            });
            cake.inNamespace("b", () => {
                expect(cake.get("key")).to.equal("foo");
            });
        });

        it("should throw on a ambiguous partial keys", () => {
            cake.inNamespace("a", () => {
                cake.publish("key", "foo");
            });
            cake.inNamespace("b", () => {
                cake.publish("key", "foo");
            });

            expect(() => cake.get("key")).to.throw();
        });
    });

    describe("publish", () => {
        it("should publish a key", () => {
            cake.publish("foo", "bar");
            expect(cake.get("foo")).to.equal("bar");
        });

        it("should publish a namespaced key", () => {
            cake.publish("a" + Cake.SEPARATOR + "b", "value");
            expect(cake.get("a", "b")).to.equal("value");
        });

        it("should publish multiple keys", () => {
            cake.publish("k1", "v1");
            cake.publish("k2", "v2");
            expect(cake.get("k1")).to.equal("v1");
            expect(cake.get("k2")).to.equal("v2");
        });

        it("should throw on empty string subkey", () => {
            expect(() => cake.publish("a" + Cake.SEPARATOR + "", "value")).to.throw();
        });
    });

    describe("key", () => {
        it("should throw on no inputs", () => {
            expect(() => Cake.key()).to.throw();
        });

        it("should throw on empty string subkey", () => {
            expect(() => Cake.key("")).to.throw();
        });

        it("should throw when a subkey contains the separator character", () => {
            expect(() => Cake.key("a", "b" + Cake.SEPARATOR)).to.throw();
        });

        it("should not produce a key with a separator for only one subkey", () => {
            expect(Cake.key("a")).to.equal("a");
        });

        it("should return subkeys separated by separator", () => {
            expect(Cake.key("a", "b")).to.equal(`a${Cake.SEPARATOR}b`);
        });
    });

    describe("inNamespace", () => {
        it("should publish keys in the namespace", () => {
            cake.inNamespace("a", () => {
                cake.publish("key", "value");
            });

            expect(cake.get("a", "key")).to.equal("value");
        });

        it("should unwrap the namespace on throw", () => {
            try {
                cake.inNamespace("a", () => {
                    expect(cake.getNamespace()).to.equal("a");
                    throw new Error();
                });
            }
            catch (e) {}
            expect(cake.getNamespace()).to.equal("");
        });

        it("should unwrap only the latest namespace on throw", () => {
            cake.inNamespace("a", () => {
                try {
                    cake.inNamespace("b", () => {
                        expect(cake.getNamespace()).to.equal("b");
                        throw new Error();
                    });
                }
                catch (e) {}
                expect(cake.getNamespace()).to.equal("a");
            });

            expect(cake.getNamespace()).to.equal("");
        });

        it("should get key in current namespace", () => {
            cake.inNamespace("a", () => {
                cake.publish("key", "value");
                expect(cake.get("key")).to.equal("value");
            });
        });

        it("should prefer keys in the current namespace when resolving", () => {
            cake.publish("key", "foo");
            cake.inNamespace("a", () => {
                cake.publish("key", "bar");
                expect(cake.get("key")).to.equal("bar");
            });
            expect(cake.get("key")).to.equal("foo");
        });

        it("should find keys in parent namespaces", () => {
            cake.inNamespace("a", () => {
                cake.publish("key", "foo");
                cake.inNamespace("b", () => {
                    expect(cake.get("key")).to.equal("foo");
                });
            });
        });
    });

    describe("getPublishedKeyForValue", () => {
        it("should throw on a non-existent value", () => {
            expect(() => cake.getPublishedKeyForValue("foo", false)).to.throw();
        });

        it("should get the value for a key", () => {
            cake.publish("key", "value");
            expect(cake.getPublishedKeyForValue("value", false)).to.equal("key");
        });

        it("should throw on ambiguous keys", () => {
            cake.publish("foo", "value");
            cake.publish("bar", "value");
            expect(() => cake.getPublishedKeyForValue("value", false)).to.throw();
        });

        it("should get the non-fully-qualified key for a value when specified", () => {
            cake.inNamespace("a", () => {
                cake.publish("key", "value");
            });

            expect(cake.getPublishedKeyForValue("value", false)).to.equal("key");
        });

        it("should get the fully-qualified key for a value when specified", () => {
            cake.inNamespace("a", () => {
                cake.publish("key", "value");
            });

            expect(cake.getPublishedKeyForValue("value", true)).to.equal(Cake.key("a", "key"));
        });
    });

    describe("getContext", () => {
        it("should throw when called in the root namespace", () => {
            expect(() => cake.getContext()).to.throw();
        });

        it("should throw when there is no value for the namespace key", () => {
            cake.inNamespace("foo", () => {
                expect(() => cake.getContext()).to.throw();
            });
        });

        it("should get the value for the namespace key", () => {
            cake.publish("foo", "bar");
            cake.inNamespace("foo", () => {
                expect(cake.getContext()).to.equal("bar");
            });
        });

        it("should get the value for the namespace key two levels deep", () => {
            cake.publish("foo", "bar");
            cake.inNamespace("foo", () => {
                cake.publish("json", "bearded");
                cake.inNamespace("json", () => {
                    expect(cake.getContext()).to.equal("bearded");
                });
            });
        });
    });

    describe("hasContext", () => {
        it("should return false at the root namespace", () => {
            expect(cake.hasContext()).to.equal(false);
        });

        it("should return false when there is no value for the namespace key", () => {
            cake.inNamespace("foo", () => {
                expect(cake.hasContext()).to.equal(false);
            });
        });

        it("should return true when there is a value for the namespace key", () => {
            cake.publish("foo", "bar");
            cake.inNamespace("foo", () => {
                expect(cake.hasContext()).to.equal(true);
            });
        });
    });

    describe("getOrGetContext", () => {
        it("should get the context on empty input", () => {
            cake.publish("foo", "bar");
            cake.inNamespace("foo", () => {
                expect(cake.getOrGetContext()).to.equal("bar");
            });
        });

        it("should get the context on null input", () => {
            cake.publish("foo", "bar");
            cake.inNamespace("foo", () => {
                expect(cake.getOrGetContext(null)).to.equal("bar");
            });
        });

        it("should prioritize the get over the getContext", () => {
            cake.publish("foo", "bar");
            cake.inNamespace("foo", () => {
                cake.publish("moo", "cow");
                expect(cake.getOrGetContext("moo")).to.equal("cow");
            });

        });

        it("should throw on non-existent key and no context", () => {
            expect(() => cake.getOrGetContext("foo")).to.throw();
        });

        it("should search up the namespace hierarchy for the key", () => {
            cake.publish("a", "foo");
            cake.inNamespace("a", () => {
                cake.publish("key", "value"); // a.key
                cake.inNamespace("b", () => {
                    expect(cake.getOrGetContext("key")).to.equal("value");
                });
            });
        });
    });
});