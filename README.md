# Recipe
Recipe is a cross-language code generation framework for setting up data fixtures for integration testing. It uses the metaphor of baking to support within-service and cross-service integration testing. 

##### Table of Contents
1. [TL;DR](#tldr)<br>
1. [Why?](#why)<br>
1. [Getting started](#getting-started)<br>
   1. [Concepts](#concepts)<br>
   1. [Setup and cookbook]()<br>
   1. [Generating hooks]()<br>
   1. [Generating ingredients]()<br>
   1. [Adding dispatchers]()<br>
   1. [Publishing to the cake]()<br>
   1. [Keyed ingredients and namespacing]()<br>
   1. [Setup for within-service ITs]()<br>
1. [Installation](#installation)<br>
   1. [Recipe generator](#generator)<br>
   1. [Run-time library](#runtime)<br>
1. [Cookbook spec]()<br>
1. [Language Support]()<br>
1. [Miscellaneous topics]()<br>
    1. [Recipe segmentation]()<br>
1. [FAQ]()<br>

<a name="tldr"/>

## TL;DR

You declare your domain concepts in a file (cookbook) which generates building blocks (ingredients) which you then combine to build data fixtures (recipes). Recipes describe all the data you need to be persisted (baked) for a test.

Here's a simple example in Java:  

```java
/* setup a user with an item in their shopping cart  */
 
Cake cake = oven.bake(Recipe.prepare(
    new SimpleShoppingFixture(),
    new Customer("Jim")
    new Product("Cat Slippers", "#01234", 14.99, SimpleShoppingFixture.MAIN_WAREHOUSE)
        .withShippingAvailability(Region.CANADA, Region.TAIWAN),
    new ShoppingCart("Jim")
    new AddToCart("Jim", "Cat Slippers", 2)
        .withExpressShipping(true)
));

String userId = cake.get("Jim");

/* test purchase logic... */
 ``` 
`SimpleShoppingFixture` is an existing recipe that sets up the minimal data required for a shopping scenario. `Customer`, `Product`, and `ShoppingCart`, and `AddToCart` are all generated ingredients, used to set up a more fine-grained scenario for the test.

Ready to start baking? Grab your apron and head over to [Getting Started](#getting-started).

<a name="why"/>

## Why?

Setting up data for integration tests is challenging.

Each integration test requires a carefully planned set of data to be prepared before running the test logic. In a microservice architecture, this data may be persisted across several data stores. But how do you set up the data in the first place?

#### Snapshot approach

Some developers maintain a set of database snapshots and load them up for each test or family of tests. Although this is usually faster than re-creating data at run-time, there are a few downsides:
* *Lack of precision*: snapshots require a lot of work to create so they are often reused for several tests despite containing more data than a particular test needs; the presence of extra data may introduce false positives
* *Lack of clarity*: depending on the snapshot format (sql, xml, etc.), it can be difficult to determine at a glance exactly what data is being set up for a test, making the test harder to debug and modify
* *Migration overhead*: databases and snapshots need to be migrated whenever the data schema changes
* *Difficult to extend*: if two tests require similar but slightly different setups, it is tempting to duplicate the original snapshot and modify it slightly; not being able to break up and re-use common parts of snapshots leads to a lot of duplicated setup

#### Dynamic approach

Other developers set up data using a series of API calls, or, if testing within-service, calls to the controller, service, or data access layers of an application. This is slower but avoids migration issues since all setup is done at run-time and (ideally) undergoes  validation, preventing impossible data states. However, there are still issues:
* *Convoluted setup*: a long series of api/service calls at the beginning of each test is hard to read, and similar to the snapshot approach it can be difficult to see the full picture of what is being set up 
* *Language differences*: two services written in different languages may want to set up the same data on a common third service, but that setup may look different depending on how the services implement their interface to the third service 

What's missing is a simple way to *declare*, *combine*, and *maintain* data fixtures such that each test sets up only the data it actually needs and in a language agnostic way. As architectures become less monolithic and more distrubuted with different languages and stacks, it is important to be to able to easily configure fine-grained data states for cross-service testing.

This is where Recipe comes in.

<a name="getting-started"/>

## Getting started

<a name="concepts"/>

### Concepts

<a name="installation"/>

## Installation

<a name="generator"/>

### Recipe Generator
The recipe generator is an executable jar that generates ingredients and hooks in all supported languages. For convenience, the generator can be invoked via the following build tools/plugins. If your particular build system is not supported, download and invoke the jar directly to perform the generation.

#### Maven plugin

##### Generate ingredients

```xml
<plugin>
    <groupId>ca.derekcormier.recipe</groupId>
    <artifactId>recipe-generator-maven-plugin</artifactId>
    <version>0.3.1</version>
    <executions>
        <execution>
            <id>generate-ingredients</id>
            <phase>generate-test-sources</phase>
            <goals>
                <goal>generate</goal>
            </goals>
            <configuration>
                <domain>MyIngredientDomain</domain>
                <flavour>java-ingredient</flavour>
                <cookbook>${project.basedir}/cookbook.yaml</cookbook>
                <targetDir>${project.build.directory}/generated-test-sources/recipe</targetDir>
                <javaPackage>ingredient.package.name</javaPackage>
            </configuration>
        </execution>
    </executions>
</plugin>
```
##### Generate hooks
```xml
<plugin>
    <groupId>ca.derekcormier.recipe</groupId>
    <artifactId>recipe-generator-maven-plugin</artifactId>
    <version>0.3.1</version>
    <executions>
        <execution>
            <id>generate-hooks</id>
            <phase>generate-test-sources</phase>
            <goals>
                <goal>generate</goal>
            </goals>
            <configuration>
                <domain>MyIngredientDomain</domain>
                <flavour>{{language}}-hook</flavour>
                <cookbook>${project.basedir}/cookbook.yaml</cookbook>
                <targetDir>${project.build.directory}/generated-sources/recipe</targetDir>
                <javaPackage>hook.package.name</javaPackage>
            </configuration>
        </execution>
    </executions>
</plugin>
```

#### npm package
Coming soon...

#### Invoke jar directly

[Download](https://search.maven.org/#search%7Cga%7C1%7Ca%3A%22recipe-generator%22) the latest jar-with-dependencies and run:

```java -jar recipe-generator-x.y.z-jar-with-dependencies.jar```

...to see the usage options.

<a name="runtime"/>

### Run-time library

The run-time libraries contain the core classes (Recipe, Cake, Oven, etc.) and the superclasses required by the generated ingredients and hooks.

#### Java
```xml
<dependency>
    <groupId>ca.derekcormier.recipe</groupId>
    <artifactId>recipe-java-runtime</artifactId>
    <version>0.3.1</version>
</dependency>
```
#### TypeScript
```json
devDependencies: {
  "recipe-ts-runtime": "0.3.1",
}
```
