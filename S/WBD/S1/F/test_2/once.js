
const EventEmitter = require('events'); 
const myEmitter = new EventEmitter(); 
 
myEmitter.once('greet', (name) => { 
    console.log(`Hello ${name}! It shows only one time`); 
}); 
 
myEmitter.emit('greet', 'Phnom Penh'); // ប ញ រ 
myEmitter.emit('greet', 'Siem Reap'); // មិនប ញអីេទ 
