/*  1

// Static Properties and Methods
class Calculator {
    static PI = 3.14;

    static add(a, b) {
        return a + b;
    }

    static subtract(a, b) {
        return a - b;
    }

}

console.log(Calculator.PI); // Output: 3.14
console.log(Calculator.add(5,3)); // Output: 8
console.log(Calculator.subtract(5,3)); // Output: 2

*/

/*  2
class MyClass{
    counter = 0;

    constructor(){
        this.counter++;
    }

}

console.log(new MyClass().counter); // Output: 1
*/

/*  3
class MyClass{
    counter = 0;

    constructor(){
        this.counter++;
    }

}

let instance1 = new MyClass();
console.log(instance1.counter); // Output: 1

let instance2 = new MyClass();
console.log(instance2.counter);  // Output: 1
*/

/*  4
class MyClass{
    static counter = 0;

    constructor(){
        MyClass.counter++;
    }

}

let instance1 = new MyClass();
console.log(MyClass.counter); // Output: 1

let instance2 = new MyClass();
console.log(MyClass.counter);  // Output: 2
*/

/*  5
class MyClass{
    static counter = 0;

    constructor(){
        MyClass.counter++;
    }

}

let instance1 = new MyClass();
console.log(MyClass.counter); // Output: 1

let instance2 = new MyClass();
console.log(MyClass.counter);  // Output: 2
*/

/*  6
const buffer = Buffer.from('hello');
console.log(buffer);
console.log(buffer.toString());
*/

/*  7
const fs = require('fs');
fs.writeFileSync('test.txt', 'Hello from Node.js!');
*/

/*  8
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

/*  9
const math = require('./math');
console.log(math.add(2, 3)); // Output: 5
console.log(math.subtract(5, 2)); // Output: 3
console.log(math.multiply(3, 4)); // Output: 12
console.log(math.divide(10, 2)); // Output: 5
*/

/*  10
const express = require('express');
const app = express();
app.get('/',(req,res)=>{
    res.send('Hello from Express!');
});

app.listen(3000,()=>{
    console.log('Web Server is using port 3000');
});
*/

/*  11
export const logMessage = (message) =>{
    console.log(` [INFO] ${message}`);
};
module.exports = { logMessage };
*/

const { add, subtract, multiply, divide } = require('./math');
console.log(add(2, 3)); // Output: 5
console.log(subtract(5, 2)); // Output: 3
console.log(multiply(3, 4)); // Output: 12
console.log(divide(10, 2)); // Output: 5