
const http = require('http');

const server = http.createServer((req, res) => {
    if (req.method === 'GET' && req.url === '/') {
        res.statusCode = 200;
        res.setHeader('Content-Type', 'text/plain');
        res.end('This is the Home Page');
    } else if (req.method === 'GET' && req.url === '/about') {
        res.statusCode = 200;
        res.setHeader('Content-Type', 'text/html');
        res.end(`
                <form method="POST" action="/submit">
                    <input type="test" name="data"/>
                    <button type="submit">Submit</button>
                </form>`);
    } else if (req.method === 'POST' && req.url === '/submit') {
        let body = '';
        req.on('data', (chunk) => {
            body += chunk; // បមូលទិនន័យពី POST request 
        });
        req.on('end', () => {
            res.statusCode = 200;
            res.setHeader('Content-Type', 'text/plain');
            res.end(`Receiving Data: ${body}`);
        });
    } else {
        res.statusCode = 404;
        res.setHeader('Content-Type', 'text/plain');
        res.end('404: ទំព័រមិនតវ នរកេឃញ');
    }
});

server.listen(3000, () => {
    console.log('Server is running on port 3000');
});
