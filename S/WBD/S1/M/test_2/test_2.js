/*  1
// Object Literal
const person = {
    name: 'Alice',
    age: 30,
    greet: function(){
        console.log(`Hello, my name is ${this.name}`);
    }
};

// Accessing properties
console.log(person.name);   // Alice
console.log(person['age']); // 30
*/


/*  2
class Person {
    constructor(name, age){
        this.name = name;
        this.age = age;
    }
    greet() {
        console.log(`Hello, my name is ${this.name}
            and I am ${this.age} years old.`);
    }
}

const person = new Person('John', 30);  // instance of Person class
person.greet();
*/

/*  3
class Person {
    name = '';
    age = 0;
    constructor(name, age){
        this.name = name;
        this.age = age;
    }
    greet() {
        console.log(`Hello, my name is ${this.name}
            and I am ${this.age} years old.`);
    }
}

const person = new Person('John', 30);  // instance of Person class
person.greet();
*/

/*  4
const a = 90;
const b = 10;
const sum = a + b;
console.log(sum);

class Person {
    name = '';
    age = 0;
    constructor(name, age){
        this.name = name;
        this.age = age;
    }
    greet() {
        console.log(`Hello, my name is ${this.name}
            and I am ${this.age} years old.`);
    }
}

const person = new Person('John', 30);  // instance of Person class
person.greet();
*/

/*  5
class Person {
    #fullName = '';
    
    constructor(name){
        this.#fullName = name;
    }
    getFullName(){
        console.log(this.#fullName);
    }
}

const person = new Person('John Doe');
person.getFullName();
*/

// Ecapsulation Example in JavaScript
class Calculator {
    #value1 = 0;
    #value2 = 0;
    get value1() {
        return this.#value1;
    }
    set value1(val) {
        this.#value1 = val;
    }
    get value2() {
        return this.#value2;
    }
    set value2(val) {
        this.#value2 = val;
    }   
    add() {
        return this.#value1 + this.#value2;
    }
    subtract() {
        return this.#value1 - this.#value2;
    }
    multiply() {
        return this.#value1 * this.#value2;
    }
}
const calc = new Calculator();
calc.value1 = 10;
calc.value2 = 5;

console.log("Value 1: " + calc.value1); // Output: Value 1: 10
console.log("Value 2: " + calc.value2); // Output: Value 2: 5   

console.log("Addition: " + calc.add());         // Output: Addition: 15
console.log("Subtraction: " + calc.subtract()); // Output: Subtraction: 5
console.log("Multiplication: " + calc.multiply()); // Output: Multiplication: 50