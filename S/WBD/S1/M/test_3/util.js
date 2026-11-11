/*  1.1
const fs = require('fs');

const writeToFile = (fileName, content) => {
    fs.writeFileSync(fileName, content);
};

const readFromFile = (fileName) => {
    const buffer = fs.readFileSync(fileName);
    return buffer.toString();
};

module.exports = { writeToFile, readFromFile };
*/

const fs = require('fs');

const writeToFile = (fileName, content) => {
    fs.writeFileSync(fileName, content);
};

const readFromFile = (fileName) => {
    const buffer = fs.readFileSync(fileName);
    return buffer;
};

module.exports = { writeToFile, readFromFile };