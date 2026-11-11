/*  1.1
const { add, subtract } = require('./math');
console.log(add(5, 3));
console.log(subtract(5, 3));
*/

const { writeToFile, readFromFile } = require('./util');

writeToFile('test.txt', 'Hello Node.js!');
console.log(readFromFile('test.txt'));


const data = readFromFile('test.txt');
console.log(data.toString());