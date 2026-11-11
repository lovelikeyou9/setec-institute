/*
const EventEmitter = require('events');
// បេងតវត EventEmitter 
const myEmitter = new EventEmitter();
// ប់អនុគមន៍េ នឹងពឹតិ រណ៍ 'greet' 
myEmitter.on('greet', (name) => {
    console.log(`Hello 1 ${name}!`);
});

myEmitter.on('greet', (name) => {
    console.log(`Hello 2 ${name}!`);
});

// ប នពឹតិ រណ៍ 'greet' 
myEmitter.emit('greet', 'Phnom Penh');
myEmitter.emit('greet', 'Siem Reap');
*/

/*
const EventEmitter = require('events');
// បេងតវត EventEmitter 
const myEmitter = new EventEmitter();

// ប់អនុគមន៍េ នឹងពឹតិ រណ៍ 'greet' 
const greet = () => {
    console.log('Hello! Nice to meet you.');
};

myEmitter.on('Hello', greet);
myEmitter.emit('Hello');
myEmitter.removeListener('Hello', greet);
myEmitter.emit('Hello');
*/

/*
const EventEmitter = require('events');
const myEmitter = new EventEmitter();
myEmitter.on('update', () => console.log('Processing 1st...'));
myEmitter.on('update', () => console.log('Processing 2st...'));
myEmitter.emit('update');
myEmitter.removeAllListeners('update');
myEmitter.on('login', () => console.log('User logged in'));
myEmitter.on('logout', () => console.log('User logged out'));
myEmitter.on('error', () => console.log('An error occurred'));

myEmitter.removeAllListeners(['login', 'logout']);
myEmitter.emit('update');
*/


/*
const EventEmitter = require('events'); 
const util = require('util'); 
 
// បេងត ក់ ល់ខន 
function MyClass() { 
    EventEmitter.call(this); // េ constructor របស់ EventEmitter 
} 
 
// ទទួលមរតកពី EventEmitter 
util.inherits(MyClass, EventEmitter); 
 
// បេងតវត 
const myObject = new MyClass(); 
 
// ប់ពឹតិ រណ៍ 
myObject.on('event', (data) => { 
    console.log('ពឹតិ រណ៍េកតេឡង:', data); 
}); 

myObject.emit('event', 'ទិនន័យ កលង'); 
*/

/*
const EventEmitter = require('events');
const util = require('util');
function MyClass(){
    EventEmitter.call(this);
}
util.inherits(MyClass, EventEmitter);
const myObject = new MyClass();

myObject.on('event', (data) => {
    console.log('Event Happenning: ', data);
});

myObject.emit('event', 'Data Test');
*/

/*
const EventEmitter = require('events');
class MyClass extends EventEmitter {
    constructor() {
        super(); // េ constructor របស់ EventEmitter 
    }
}
const myObject = new MyClass();
myObject.on('event', (data) => {
    console.log('Event is happening:', data);
});
myObject.emit('event', 'Test Data'); 
*/


 
const EventEmitter = require('events'); 
 
// បេងតម៉ូឌុលែដល return EventEmitter 
function createCounter() { 
    const emitter = new EventEmitter(); 
    let count = 0; 
 
    setInterval(() => { 
        count++; 
        emitter.emit('count', count); // ប នពឹតិ រណ៍ 'count' 
    }, 1000); 
 
    return emitter; 
} 
 
// េបម៉ូឌុល 
const counter = createCounter(); 
counter.on('count', (value) => { 
    console.log(`Current Amount: ${value}`); 
});
