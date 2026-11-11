const express = require('express');
const app = express();
const port = 3000;
app.get('/', (req, res) => {
    res.send('Hello from ExpressJS!');
});
app.listen(port, () => {
    console.log(`Program is running is http://localhost:${port}`);
});