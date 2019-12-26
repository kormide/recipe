#!/usr/bin/env node

const { ArgumentParser } = require("argparse");
const { exec } = require("child_process");

const { FILEPATH } = require("./vars");

const REQUIRED_ARGS = [
    {name: "flavour", help: "type of generation to perform; valid options: java-ingredient, java-hook, js-ingredient, js-hook, ts-ingredient, ts-hook"},
    {name: "cookbook", help: "path to the yaml cookbook definition file"},
    {name: "targetDir", help: "directory to output generated files"}
];

const OPTIONAL_ARGS = [
    {name: "javaPackage", metavar: "package", help: "java package for generated classes"},
    {name: "ingredientPostfix", metavar: "postfix", help: "string to append to ingredient names"}
];

// parse required and optional arguments
const parser = new ArgumentParser({addHelp: true});
for (let arg of REQUIRED_ARGS) {
    parser.addArgument(arg.name, {help: arg.help});    
}
for (let arg of OPTIONAL_ARGS) {
    parser.addArgument("--" + arg.name, {help: arg.help, metavar: arg.metavar});    
}
const options = parser.parseArgs();

// form the execution string
let execStr = `java -jar ${FILEPATH} `;
for (let arg of REQUIRED_ARGS) {
    execStr += options[arg.name] + " ";
}
for (let arg of OPTIONAL_ARGS) {
    if (options[arg.name] != null) {
        execStr += `--${arg.name} ${options[arg.name]} `;
    }
}

// run generation
console.log(execStr);
exec(execStr, (err, stdout) => {
    if (err) {
        throw err;
    }
    console.log(stdout);
});