/*  1.1
add = (a, b) => a + b;
subtract = (a, b) => a - b;
multiply = (a, b) => a * b;
function divide(a, b) {
    if (b === 0) {
        throw new Error("Division by zero is not allowed.");
    }
    return a / b;
}
module.exports = { add, subtract, multiply, divide };
*/

const add = (a, b) => a + b;
const subtract = (a, b) => a - b;
module.exports = { add, subtract };