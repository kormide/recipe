# Recipe ![](sketchy_enjoy.svg?sanitize=true)


Recipe is a cross-language framework that uses the metaphor of baking to setup data fixtures for integration testing. It suppports testing within a service as well as across microservices. 

##### Table of Contents
1. [TL;DR](#tldr)<br>
1. [Why?](#why)<br>
1. [Installation](#installation)<br>
   1. [Recipe generator](#generator)<br>
   1. [Run-time library](#runtime)<br>
1. [Getting started](#getting-started)<br>
   1. [Concepts](#concepts)<br>
   1. [Setup and cookbook]()<br>
   1. [Generating hooks]()<br>
   1. [Generating ingredients]()<br>
   1. [Adding dispatchers]()<br>
   1. [Publishing to the cake]()<br>
   1. [Keyed ingredients and namespacing]()<br>
   1. [Setup for within-service ITs]()<br>
1. [Cookbook spec]()<br>
1. [Language Support]()<br>
1. [Miscellaneous topics]()<br>
    1. [Recipe segmentation]()<br>
1. [FAQ]()<br>

<a name="tldr"/>

## TL;DR

Recipe generates ingredients from a cookbook. Ingredients are combined into recipes that describe what data you need for a test. Here's how it looks in Java:  

```java
/* setup data for test */
 
Recipe testFixture = Recipe.prepare(
    new Customer("Jim"),
    new Product("Cat Slippers", "#01234", 14.99)
        .withRegionAvailability(Region.CANADA, Region.TAIWAN),
    new ShoppingCart("Jim")
        .withItem("Cat Slippers")    
);
 

Cake cake = oven.bake(testFixture);

String userId = cake.get("Jim");
 
/* test logic here */
 ``` 
Ready to start baking recipes? Grab your apron and head over to [Getting Started](#getting-started).

<a name="why"/>

## Why?

Setting up data for integration tests is not easy.

Each integration test requires a carefully planned set of data to be prepared before running the test logic. In a microservice architecture, this data may be persisted across several data stores. But how do you set up the data in the first place?

#### Snapshot approach

Some developers maintain a set of database snapshots and load them up for each test or family of tests. Although it's usually faster than re-creating data at run-time, there are a few downsides:
* *Lack of precision*: snapshots require a lot of work to create so they are often reused for several tests even when they contain unnecessary data; this can introduce of false positives
* *Lack of clarity*: depending on the snapshot format (sql, xml, etc.), it can be difficult to determine at a glance exactly what data is being set up for a test, making the test harder to debug or modify
* *Migration overhead*: databases and snapshots need to be migrated whenever the data schema changes
* *Hard to extend*: if two tests require similar but slightly different setups, it is tempting to duplicate the original snapshot and modify it slightly, even though not all data is required for the test; over time this can lead to convoluted fixtures

#### Dynamic approach

Other developers set up data using a series of API calls, or, if testing within-service, calls to the controller, service, or data access layers of an application. This is slower but avoids migration issues since all setup is done at run-time and (ideally) undergoes  validation, preventing impossible data states. However, there are still issues:
* *Convoluted setup*: a long series of api/service calls at the beginning of each test is hard to read, and similar to the snapshot approach it can be difficult to see the full picture of what is being set up 
* *Language differences*: two services written in different languages may want to set up the same data on a third service, but that setup may look different depending on how the services implement their interface to the third service 

What's missing is a simple way to *declare*, *combine*, and *maintain* data fixtures such that each test sets up only the data it actually needs and in a language agnostic way. As architectures become less monolithic and more distrubuted with different languages and stacks, it is important to find a language-independent way of configuring data state for cross-service testing.

Recipe is designed to address all of these issues.

<a name="installation"/>

## Installation

<a name="generator"/>

### Recipe Generator
The recipe generator is written as a Java jar but can be invoked for different build environments for convenience. Regardless of the environment, the generator will generate ingredients and hooks in any of the supported languages. If there isn't a wrapper for the build system you use, invoke the jar directly to perform generation. 

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

The run-time libraries contain the core classes (Recipe, Cake, Oven, etc.) and the superclases required for the generated ingredients and hooks.

#### Java
```xml
<dependency>
    <groupId>ca.derekcormier.recipe</groupId>
    <artifactId>recipe-java-runtime</artifactId>
    <version>0.3.1</version>
</dependency>
```
#### TypeScript (node)
```json
/* package.json */

devDependencies: {
  "recipe-ts-runtime": "0.3.1",
}

```

<a name="getting-started"/>

## Getting started

<a name="concepts"/>

### Concepts
