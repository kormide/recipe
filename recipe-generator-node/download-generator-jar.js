const fs = require("fs");
const https = require("https");

const { FILEPATH, FILE_URL, OUTPUT_DIR, VERSION } = require("./vars");

if (!fs.existsSync(OUTPUT_DIR)) {
    fs.mkdirSync(OUTPUT_DIR);
}

console.log(`downloading recipe generator jar ${VERSION} from ${FILE_URL}...`);

const file = fs.createWriteStream(FILEPATH);

https.get(FILE_URL, response => {
    if (response.statusCode !== 200) {
        console.error("download failed with response code " + response.statusCode);
        fs.unlinkSync(FILEPATH);
        process.exit(1);
    }
    response.pipe(file);
    file.on("finish", () => {
        console.log("finished downloading");
        file.close();
    });
}).on("error", err => {
    console.error("download failed");
    fs.unlinkSync(FILEPATH);
    throw err;
});