/*
const http = require('http');

const website = http.createServer((req, res) => {
  if (req.url === '/' && req.method === 'GET') {
    res.writeHead(200, { 'Content-Type': 'text/plain' });
    res.end('Hello World!');
  } else {
    res.writeHead(404, { 'Content-Type': 'text/plain' });
    res.end('404 Not Found');
  }
});

website.listen(3000, () => {
    console.log('✅ Website is running on PORT: 3000');
});
*/

/*
const http = require('http');

const website = http();

const path = require('path');

website.use(express.static(path.join(__dirname, 'file_website')));

website.listen(3000, () => {
    console.log('✅ Website is running on PORT: 3000');
});
*/

const http = require('http');
const fs = require('fs');
const path = require('path');

const website = http.createServer((req, res) => {
    const filePath = path.join(
        __dirname,
        'file_website',
        req.url === '/' ? 'beginPage.html' : req.url
    );

    fs.readFile(filePath, (err, content) => {
        if (err) {
            res.writeHead(404, { 'Content-Type': 'text/plain' });
            res.end('404 Not Found');
        } else {
            // detect content type automatically
            const ext = path.extname(filePath).toLowerCase();
            const mime = {
                '.html': 'text/html',
                '.css': 'text/css',
                '.js': 'text/javascript',
                '.jpg': 'image/jpeg',
                '.png': 'image/png',
                '.svg': 'image/svg+xml',
            }[ext] || 'text/plain';

            res.writeHead(200, { 'Content-Type': mime });
            res.end(content);
        }
    });
});

website.listen(3000, () => {
    console.log('✅ Website is running on PORT: 3000');
});
