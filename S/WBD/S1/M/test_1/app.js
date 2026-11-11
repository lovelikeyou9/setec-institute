// console.log('Hello, World!');

/*  2
books = [
    { title: "The Great Gatsby", author: "File. Scott Fitzgerald" },
    { title: "1984", author: "George Orwell" },
    { title: "To Kill a Mockingbird", author: "Harper Lee" }
];

books.forEach(book => {
    console.log(`${book.title} by ${book.author}`);
});
*/

/*  3
let books = new Array (
    { title: "The Great Gatsby", author: "File. Scott Fitzgerald" },
    { title: "1984", author: "George Orwell" },
    { title: "To Kill a Mockingbird", author: "Harper Lee" }
);

books.forEach(book => {
    console.log(`${book.title} by ${book.author}`);
});
*/

/*  4
function add(a, b) {
    return a + b;
}
function subtract(a, b) {
    return a - b;
}

console.log("Addition Result: ", add(5, 3));
console.log("Subtraction Result: ", subtract(10, 4));
*/

/*  5
add = (a, b) => (a + b);

function subtract(a, b) {
    return a - b;
}

console.log("Addition Result: ", add(5, 3));
console.log("Subtraction Result: ", subtract(10, 4));
*/

/*  6
const person = {
    name: 'Alice',
    age: 30,
    city: 'New York',
    getFullInfo: function(){
        return `${this.name}, ${this.age} years old, lives in ${this.city}.`;
    }
};

console.log(person.name);   // Output: Alice
console.log(person.age);    // Output: 30
console.log(person.city);   // Output: New York
console.log(person.getFullInfo()); // Output: Alice, 30 years old, lives in New York
*/

/*  7
class Person {
    constructor(name, age){
        this.name = name;
        this.age = age;
    }
    greet(){
        console.log(`Hello, my name is ${this.name} and I am ${this.age} years old.`);
    }
}

const personObject = new Person('John', 30);
personObject.greet();
*/

/*  8
const buffer = Buffer.from('hello');
console.log(buffer);    // <Buffer 68 65 6c 6c 6f>
console.log(buffer.toString());
*/


const fs = require('fs'); 
// fs.writeFileSync('test.txt', 'Hello from Node.js!');
console.log(fs.readFileSync('test.txt', 'utf-8'));