# Recipe
Recipe is a cross-language, generative framework that uses the metaphor of baking to simplify the declaration of data fixtures for integration testing. It's designed to support within-service and cross-service integration testing. 

##### Table of Contents
1. [TL;DR](#tldr)<br>
1. [Background](#overview)<br>
1. [Getting started]()<br>
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

Create a [cookbook.yaml]() that describes the domain concepts within a service.

Use the cookbook to generate **ingredients**—builder-style objects used to set up domain concepts, and **hooks**—service-side implementations for each of the ingredients. Either can be generated in any of the supported languages.

Set up the data for integration tests by baking **recipes** of the generated ingredients (or other recipes).

##### In Java...
```java
// Setup a user named bob with cat slippers in his shopping cart
Cake cake = oven.bake(Recipe.prepare(
    new User("Bob"),
    new Product("Cat Slipper", "#01234", 14.99)
        .withRegionRestriction(Region.CANADA),
    new ShoppingCart("Bob")
        .withProduct("Cat Slipper")    
));
 
String userId = cake.get("Bob");
 
/* ... test logic here ... */
``` 
##### In TypeScript...
```typescript
const cake = oven.bake(Recipe.prepare(
    new User("Bob"),
    new Product("Cat Slipper", "#01234", 14.99)
        .withRegionRestriction(Region.CANADA),
    new ShoppingCart("Bob")
        .withProduct("Cat Slipper")    
));
 
const userId: string = cake.get("Bob");
 
/* ... test logic here ... */
``` 

The ingredients are baked in order by dispatching them to the owning service's hook.

Baking returns a **cake**, a dictionary that contains information about what was set up during the baking process—usually IDs of created entities, but really any information you need for your tests. Cakes use a flexible [keying and namespacing]() mechanism.

Create **fixture** classes for commonly used sets of ingredients and add additional ingredients for a fine-grained data setups.

```java
Cake cake = oven.bake(Recipe.prepare(
    new SimpleUserAndProductFixture(),
    new PaymentMethod(SimpleUserAndProductFixture.USER, Method.MASTERCARD, "123456"),
    ...
))
``` 

<a name="overview"/>

## Background

Setting up data for integration tests is challenging.

Integration tests typically expect a pre-configured data set to be set up before each test is run. For example, if you run an e-commerce web app, you likely have an end-to-end browser test that ensures a user can add an item to their shopping cart. Before the test is run, the database is bootstrapped with a test user, the merchandise in question, an existing (empty) shopping cart, and so forth. In a microservice architecture, these objects may be persisted across several data stores. But how is the data set up in the first place?

The short answer is that everyone does it differently.

Some developers maintain a set of database snapshots and load them up for each test or family of tests. There are a few problems with this approach:
* *Lack of precision*: the data in these snapshots is often used to service several tests due to the difficulty involved in setting them up in the first place; the presence of unneeded data for a particular test introduces the possibility of false positives
* *Lack of clarity*: depending on the snapshot format, it can be difficult to determine exactly what data is being set up for a test at a glance; it's harder to understand and debug tests when you don't understand the preconditions
* *Migration overhead*: databases and snapshots need to be migrated whenever the data schema changes
* *Hard to extend*: if two tests require similar but slightly different setups, often times an entirely new snapshot is created that duplicates the data in the original snapshot, possibly with unnecessary data. You can see how this can cascade quickly...

Other teams set up data using a series of API calls, or, if testing within a service, through a series of calls to the controller, service, or data access layers of an application. This approach avoids migration issues because all setup is done at run-time and passes through service layer validation, preventing impossible data states. However, there are still issues:
* *Convoluted setup*: a long series of api/service calls at the beginning of each test is hard to read, and similar to the snapshot approach it can be difficult to see the full picture of what is being set up 
* *Language differences*: two services written in different languages may want to set up the same data on a third service, but that setup may look different depending on how the services implement their interface to the third service 

What's missing is a simple way to **declare**, **compose**, and **maintain** data fixtures such that each test sets up _only_ the data it actually needs, preferably in a language agnostic way. As architectures become less monolithic and more distrubuted with different languages and stacks, it is important to find a language-independent way of configuring data state for inter-service testing.

## Getting started

TODO