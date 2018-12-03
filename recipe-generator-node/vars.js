const path = require("path");

const VERSION = require("./package.json").version;
const OUTPUT_DIR = __dirname + path.sep + "bin";
const FILENAME = `recipe-generator-${VERSION}-jar-with-dependencies.jar`;
const FILEPATH = OUTPUT_DIR + path.sep + FILENAME;
const FILE_URL = `https://search.maven.org/remotecontent?filepath=ca/derekcormier/recipe/recipe-generator/${VERSION}/${FILENAME}`;

module.exports = {VERSION, OUTPUT_DIR, FILENAME, FILEPATH, FILE_URL};