/*  
const express = require('express'); 
const app = express(); 
app.get('/', (req, res) => { 
res.send('សួសីពី Express!'); 
}); 
app.listen(3000, () => { 
    console.log(' ៉សុីនបេមដំេណរ រេ port 3000'); 
});
*/

/*
const { logMessage } = require('./logger'); 
logMessage('កមវធី ប់េផម!'); // លទផល: [INFO] កមវធី ប់េផម!
*/

/* Main
const { add, subtract, multiply, divide } = require('./math');
console.log(add(2, 3)); // Output: 5
console.log(subtract(5, 2)); // Output: 3
console.log(multiply(3, 4)); // Output: 12
console.log(divide(10, 2)); // Output: 5

const express = require('express'); 
const app = express(); 
app.get('/', (req, res) => { 
res.send('សួសីពី Express!'); 
}); 
app.listen(3000, () => { 
console.log(' ៉ សុីនបេមដំេណរ រេ port 3000'); 
}); 
*/

/*
// app.js
const express = require('express');
const { add, subtract, multiply, divide } = require('./math');
const app = express();

app.use(express.urlencoded({ extended: true }));

// Homepage form
app.get('/', (req, res) => {
  res.send(`
    <html>
      <head><title>Math Calculator</title></head>
      <body style="font-family:sans-serif;">
        <h2>Simple Math Calculator</h2>
        <form action="/calculate" method="POST">
          <input type="number" step="any" name="num1" placeholder="Enter number 1" required>
          <input type="number" step="any" name="num2" placeholder="Enter number 2" required>
          <button type="submit">Calculate</button>
        </form>
      </body>
    </html>
  `);
});

// Handle calculation
app.post('/calculate', (req, res) => {
  const num1 = parseFloat(req.body.num1);
  const num2 = parseFloat(req.body.num2);

  // Always compute everything
  const results = {
    add: add(num1, num2),
    subtract: subtract(num1, num2),
    multiply: multiply(num1, num2),
    divide: divide(num1, num2)
  };

  res.send(`
    <html>
      <head><title>Results</title></head>
      <body style="font-family:sans-serif;">
        <h2>Calculation Results</h2>
        <table border="1" cellpadding="8" style="border-collapse:collapse;">
          <tr><th>Operation</th><th>Result</th></tr>
          <tr><td>Add</td><td>${results.add}</td></tr>
          <tr><td>Subtract</td><td>${results.subtract}</td></tr>
          <tr><td>Multiply</td><td>${results.multiply}</td></tr>
          <tr><td>Divide</td><td>${results.divide}</td></tr>
        </table>
        <br><a href="/">Back</a>
      </body>
    </html>
  `);
});

app.listen(3000, () => {
  console.log('✅ Server running on http://localhost:3000');
});
*/

/*
// app.js
const express = require('express');
const { add, subtract, multiply, divide } = require('./math');
const app = express();

app.use(express.urlencoded({ extended: true }));

// Homepage form
app.get('/', (req, res) => {
  res.send(`
    <html>
      <head><title>Math Calculator</title></head>
      <body style="font-family:sans-serif;">
        <h2>Simple Math Calculator</h2>
        <form action="/calculate" method="POST">
          <input type="number" step="any" name="num1" placeholder="Enter number 1" required>
          <input type="number" step="any" name="num2" placeholder="Enter number 2" required>
          <button type="submit">Calculate</button>
        </form>
      </body>
    </html>
  `);
});

// Handle calculation
app.post('/calculate', (req, res) => {
  const num1 = parseFloat(req.body.num1);
  const num2 = parseFloat(req.body.num2);

  // Always compute everything
  const results = {
    add: add(num1, num2),
    subtract: subtract(num1, num2),
    multiply: multiply(num1, num2),
    divide: divide(num1, num2)
  };

  res.send(`
    <html>
      <head><title>Results</title></head>
      <body style="font-family:sans-serif;">
        <h2>Calculation Results</h2>
        <table border="1" cellpadding="8" style="border-collapse:collapse;">
          <tr><th>Operation</th><th>Result</th></tr>
          <tr><td>Add</td><td>${results.add}</td></tr>
          <tr><td>Subtract</td><td>${results.subtract}</td></tr>
          <tr><td>Multiply</td><td>${results.multiply}</td></tr>
          <tr><td>Divide</td><td>${results.divide}</td></tr>
        </table>
        <br><a href="/">Back</a>
      </body>
    </html>
  `);
});

app.listen(3000, () => {
  console.log('✅ Server running on http://localhost:3000');
});
*/

// server.js
const http = require('http');
const querystring = require('querystring');
const { add, subtract, multiply, divide } = require('./math');

const port = 3000;

const server = http.createServer((req, res) => {
    if (req.method === 'GET' && req.url === '/') {
        // Serve input form
        res.writeHead(200, { 'Content-Type': 'text/html; charset=utf-8' });
        res.end(`
      <html>
        <head><title>Math Calculator</title></head>
        <body style="font-family:sans-serif;">
          <h2>Simple Math Calculator</h2>
          <form action="/calculate" method="POST">
            <input type="number" step="any" name="num1" placeholder="Enter number 1" required>
            <input type="number" step="any" name="num2" placeholder="Enter number 2" required>
            <button type="submit">Calculate</button>
          </form>
        </body>
      </html>
    `);
    }
    else if (req.method === 'POST' && req.url === '/calculate') {
        // Collect POST data
        let body = '';
        req.on('data', chunk => body += chunk);
        req.on('end', () => {
            const parsed = querystring.parse(body);
            const num1 = parseFloat(parsed.num1);
            const num2 = parseFloat(parsed.num2);

            // Compute results
            const results = {
                add: add(num1, num2),
                subtract: subtract(num1, num2),
                multiply: multiply(num1, num2),
                divide: num2 === 0 ? 'Division by zero is not allowed.' : divide(num1, num2)
            };

            // Respond with results
            res.writeHead(200, { 'Content-Type': 'text/html; charset=utf-8' });
            res.end(`
        <html>
          <head><title>Results</title></head>
          <body style="font-family:sans-serif;">
            <h2>Calculation Results</h2>
            <table border="1" cellpadding="8" style="border-collapse:collapse;">
              <tr><th>Operation</th><th>Result</th></tr>
              <tr><td>Add</td><td>${results.add}</td></tr>
              <tr><td>Subtract</td><td>${results.subtract}</td></tr>
              <tr><td>Multiply</td><td>${results.multiply}</td></tr>
              <tr><td>Divide</td><td>${results.divide}</td></tr>
            </table>
            <br><a href="/">Back</a>
          </body>
        </html>
      `);
        });
    }
    else {
        // Handle 404
        res.writeHead(404, { 'Content-Type': 'text/plain' });
        res.end('404 Not Found');
    }
});

server.listen(port, () => {
    console.log(`✅ Server running at http://localhost:${port}`);
});
