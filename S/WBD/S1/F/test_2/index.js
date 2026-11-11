/*
const express = require('express');
const app = express();

app.use(express.json()); // It adds a middleware that parses incoming HTTP requests that have a Content-Type: application/json header, and automatically converts the JSON payload in the request body into a JavaScript object, attaching it to req.body.

app.use(express.urlencoded({ extended: true })); // Add this line!, if the data is also sent from form
// when Express see “Content-Type: application/x-www-form-urlencoded”(content type of form), please decode(username=Sok%20Piseth&city=Phnom%20Penh&age=25)and give me a nice JavaScript object in req.body.”


app.get('/', (req, res) => {
    res.send('សួស្តី! នេះជា Home Page');
});

app.get('/about', (req, res) => {
    res.send('នេះជា About Page');
});

app.get('/register', (req, res) => {
    res.send(`
        <form method='POST' action='submit'>
            <input name='username' placeholder='Username'/><br><br>
            <input name='email' placeholder='Email'/><br><br>
            <button type='submit'>Register</button>
        </form>`);
});

app.post('/submit', (req, res) => {
    const data = req.body;
    res.send(`ទទួលបានទិន្នន័យ: ${JSON.stringify(data)}`); 
      // JSON.stringify(data) => converts a JavaScript object → JSON text string.
      // 
      // res.json(user);   <=>      res.setHeader('Content-Type', 'application/json')
                  res.send(JSON.stringify(user))

});

app.listen(3000, () => {
    console.log('Server កំពុងដំណើរការនៅ port 3000');
});
*/

/*
const fs = require('fs');
// នឯក រ 'example.txt' 
fs.readFile('example.txt', 'utf8', (err, data) => {
    if (err) {
        console.error(' នប កង រ នឯក រ:', err);
        return;
    }
    console.log('ខឹម រឯក រ:', data);
});
*/


/*
const fs = require('fs');

try {
    const data = fs.readFileSync('example.txt', 'utf8');
    console.log('ខ្លឹមសារឯកសារ:', data);
} catch (err) {
    console.error('មានបញ្ហាក្នុងការអានឯកសារ:', err);
}
console.log('កម្មវិធីបញ្ចប់។');
*/


/*
const fs = require('fs'); 
 
const content = 'េនះ ខឹម រថីស ប់ឯក រ'; 
 
fs.writeFile('example.txt', content, (err) => { 
    if (err) { 
        console.error(' នប កង រសរេសរឯក រ:', err); 
        return; 
    } 
    console.log('ឯក រតវ នសរេសរេ យេ គជ័យ'); 
});
*/


/*
const fs = require('fs');
const content = 'នេះជាខ្លឹមសារថ្មីសម្រាប់ឯកសារ';
fs.writeFile('example.txt', content, (err) => {
    if (err) {
        console.error('មានបញ្ហាក្នុងការសរសេរឯកសារ:', err);
        return;
    }
    console.log('ឯកសារត្រូវបានសរសេរដោយជោគជ័យ');
});
*/


/*
const fs = require('fs');
const content = 'នេះជាខ្លឹមសារថ្មីសម្រាប់ឯកសារ';
try {
    fs.writeFileSync('output.txt', content);
    console.log('ឯកសារត្រូវបានសរសេរដោយជោគជ័យ');
} catch (err) {
    console.error('មានបញ្ហាក្នុងការសរសេរឯកសារ:', err);
}
*/


/*
const fs = require('fs');

fs.open('example.txt', 'r', (err, fd) => {
    if (err) {
        console.error('មានបញ្ហាក្នុងការបើកឯកសារ:', err);
        return;
    }
    console.log('ឯកសារត្រូវបានបើកដោយជោគជ័យ លេខសម្គាល់ឯកសារ:', fd);
});
*/

/*
const fs = require('fs');
fs.unlink('example.txt', (err) => {
    if (err) {
        console.error(' នប កង រលុបឯក រ:', err);
        return;
    }
    console.log('ឯក រតវ នលុបេ យេ គជ័យ');
});
*/

/*
const fs = require('fs');
fs.open('output.txt', 'w', (err, fd) => {
    if (err) {
        console.error(' នប កង រេបកឯក រ:', err);
        return;
    }
    const content = 'េនះ ទិនន័យស ប់សរេសរ';
    fs.write(fd, content, (err, written, string) => {
        if (err) {
            console.error(' នប កង រសរេសរ:', err);
            return;
        }
        console.log(`សរេសរេ យេ គជ័យ ${written} ៃប`);
        fs.close(fd, (err) => {
            if (err) console.error(' នប កង របិទឯក រ:', err);
        });
    });
}); 
*/

/*
const fs = require('fs');
const path = require('path');

const filename = 'output.txt';
const filePath = path.join(__dirname, filename);

const dataToWrite = '---HEADER---Data to be written---FOOTER---';

const buffer = Buffer.from(dataToWrite, 'utf8');
const bufferOffset = 12; // Start reading from the 13th byte (index 12) of the buffer ("D" of "Data...")
const length = 18;       // Write 18 bytes from the buffer (covers "Data to be written")
const filePosition = 0;  // Start writing at the beginning of the file (0)

fs.open(filePath, 'w', (err, fd) => {
    if (err) {
        console.error('Error opening file:', err);
        return;
    }

    fs.write(fd, buffer, bufferOffset, length, filePosition, (err, writtenBytes, buffer) => {
        if (err) {
            console.error('Error writing to file:', err);
        } else {
            console.log(`Successfully wrote ${writtenBytes} bytes to ${filename}.`);
            console.log(`Content written: "${buffer.toString('utf8', bufferOffset, bufferOffset + writtenBytes)}"`);
        }
        fs.close(fd, (err) => {
            if (err) {
                console.error('Error closing file:', err);
            } else {
                console.log('File closed successfully.');
            }
        });
    });
});
*/

/*
const fs = require('fs');
const additionalContent = '\nThis is the additional content to be appended.';
fs.appendFile('output.txt', additionalContent, (err) => {
    if (err) {
        console.error('Error appending file:', err);
        return;
    }
    console.log('Data appended successfully');
});
*/

/*
const fs = require('fs').promises; // ប្រើកំណែ Promise របស់ fs

async function checkFileExistence(filePath) {
    try {
        // fs.constants.F_OK គ្រាន់តែពិនិត្យមើលថាឯកសារមានឬអត់
        await fs.access(filePath, fs.constants.F_OK);
        console.log(`✅ ឯកសារ "${filePath}" មាន។`);
        return true;
    } catch (error) {
        // ប្រសិនបើមានកំហុស វាមានន័យថាឯកសារមិនមាន ឬមិនអាចចូលប្រើបាន។
        if (error.code === 'ENOENT') {
            console.log(`❌ ឯកសារ "${filePath}" មិនមានទេ។`);
        } else {
            console.log(`⚠️ កំហុសក្នុងការចូលប្រើឯកសារ៖ ${error.message}`);
        }
        return false;
    }
}

// ឧទាហរណ៍នៃការប្រើប្រាស់
checkFileExistence('./output.txt');
checkFileExistence('./example.txt');
*/

/*
const fs = require('fs');   const { F_OK } = fs.constants;
function checkFileExistenceCallback(filePath) {
    fs.access(filePath, F_OK, (error) => {
        if (error) {
            if (error.code === 'ENOENT') {
                console.log(`❌ Document: "${filePath}" does not exist.`);
            } else {
                console.log(`⚠️ Error accessing file: ${error.message}`);
            }
            return;
        }
        console.log(`✅ Document: "${filePath}" exists.`);
    });
    console.log("👉 This code runs before the file existence check completes!");
}
checkFileExistenceCallback('./output.txt');
checkFileExistenceCallback('./example.txt');
*/

/*
const fs = require('fs');

const filePath = './config.json';

if (fs.existsSync(filePath)) {
    console.log(`✅ ឯកសារ "${filePath}" មាន។`);
    // អាចបន្តប្រតិបត្តិការដូចជា fs.readFileSync(filePath)
} else {
    console.log(`❌ ឯកសារ "${filePath}" មិនមានទេ។`);
}
*/

/*
const fs = require('fs').promises;
const { R_OK } = fs.constants;

async function checkReadAccess(filePath) {
    try {
        // ពិនិត្យវត្តមាន និងសិទ្ធិអាន
        await fs.access(filePath, R_OK); // fs.constants.R_OK
        console.log(`✅ ${filePath} មានសិទ្ធិអាន។`);
    } catch (error) {
        // កំហុសអាចជា 'ENOENT' (មិនមាន) ឬ 'EACCES' (មិនមានសិទ្ធិ)
        console.log(`❌ ${filePath} មិនមានសិទ្ធិអាន ឬមិនមានឯកសារ។`);
    }
}
*/

/*
const fs = require('fs').promises;
const { W_OK } = fs.constants;

async function checkWriteAccess(filePath) {
    try {
        await fs.access(filePath, W_OK);
        console.log(`✅ ${filePath} មានសិទ្ធិសរសេរ។`);
    } catch (error) {
        console.log(`❌ ${filePath} មិនមានសិទ្ធិសរសេរ ឬមិនមានឯកសារ។`);
    }
}
checkWriteAccess('./data/some_folder');
*/

/*
const fs = require('fs'); 
 
fs.mkdir('newFolder', (err) => { 
    if (err) { 
        console.error(' នប កង របេងតថត:', err); 
        return; 
    } 
    console.log('ថតតវ នបេងតេ យេ គជ័យ'); 
}); 
*/

/*
const fs = require('fs');
fs.mkdirSync('./config/deep/path', {recursive: true});
*/

/*
const fs = require('fs');
fs.rmdir('newFolder', (err) => {
    if (err) {
        console.error(' នប កង រលុបថត:', err);
        return;
    }
    console.log('ថតតវ នលុបេ យេ គជ័យ');
});
*/


/*
const express = require("express");
const app = express();

let count = 0;

app.use(express.urlencoded({ extended: true }));

app.get("/", (req, res) => {
    res.send(`
        <html>
        <head>
            <title>Node Counter</title>
            <style>
                body {
                    font-family: Arial;
                    text-align: center;
                    margin-top: 50px;
                }
                .btn-row {
                    display: flex;
                    justify-content: center;
                    gap: 10px;
                }
                button {
                    font-size: 18px;
                    padding: 10px 20px;
                }
                h2 {
                    margin-bottom: 20px;
                }
            </style>
        </head>
        <body>
            <h2>Number: ${count}</h2>

            <div class="btn-row">
                <form method="POST" action="/add">
                    <button>+</button>
                </form>

                <form method="POST" action="/reset">
                    <button>Reset</button>
                </form>

                <form method="POST" action="/minus">
                    <button>-</button>
                </form>
            </div>
        </body>
        </html>
    `);
});

app.post("/add", (req, res) => {
    count++;
    res.redirect("/");
});

app.post("/minus", (req, res) => {
    count--;
    res.redirect("/");
});

app.post("/reset", (req, res) => {
    count = 0;
    res.redirect("/");
});

app.listen(3000, () => {
    console.log("Server running at http://localhost:3000");
});

*/


/*
function sayHello(name) {
    console.log('សួសី ' + name);
    debugger; // ចំណុចឈប់ (breakpoint) 
    console.log('សូម គមន៍មក ន់ Node.js');
}
sayHello('ភំេពញ');
*/

/*
const EventEmitter = require('events');
// បេងតវត EventEmitter 
const myEmitter = new EventEmitter();
// ប់អនុគមន៍េ នឹងពឹតិ រណ៍ 'greet' 
myEmitter.on('greet', (name) => {
    console.log(`Hello ${name}!`);
});
// ប នពឹតិ រណ៍ 'greet' 
myEmitter.emit('greet', 'Phnom Penh');
*/


/* 
const EventEmitter = require('events'); 
const myEmitter = new EventEmitter(); 
 
myEmitter.once('greet', (name) => { 
    console.log(`Hello ${name}! It show data`); 
}); 
 
myEmitter.emit('greet', 'Phnom Penh'); // ប ញ រ 
myEmitter.emit('greet', 'Siem Reap'); // មិនប ញអីេទ
*/

/*
const express = require('express');
const app = express();
const port = 3000;
app.get('/', (req, res) => {
    res.send('សួសីពី ExpressJS!');
});
app.listen(port, () => {
    console.log(`កមវធីកំពុងដំេណរ រេ http://localhost:${port}`);
});
*/


/*
const express = require('express'); 
const app = express(); 
const port = 3000; 
 
// Dynamic route មួយ parameter 
app.get('/user/:id/:name', (req, res) => { 
    const userId = req.params.id; 
    const name = req.params.name || "Guest"; 
    res.send(`អកេប ស់េលខសល់: ${userId}, Name: ${name}`); 
}); 
 
app.listen(port, () => { 
    console.log(`Server កំពុងដំេណរ រេ http://localhost:${port}`); 
});
*/

/*
const express = require('express'); 
const app = express(); 
const port = 3000; 
 
// Dynamic route មួយ parameter 
app.get('/user/:id/:name', (req, res) => { 
    const userId = req.params.id; 
    const name = req.params.name || "Guest"; 
    res.send(`អកេប ស់េលខសល់: ${userId}, Name: ${name}`); 
}); 
 
app.listen(port, () => { 
    console.log(`Server កំពុងដំេណរ រេ http://localhost:${port}`); 
});
*/


/*
const express = require('express'); 
const app = express(); 
const port = 3000; 
 
// Dynamic route មួយ parameter 
app.get('/user/:id{/:name}', (req, res) => { 
    const userId = req.params.id; 
    const name = req.params.name || "Guest"; 
    res.send(`អកេប ស់េលខសល់: ${userId}, Name: ${name}`); 
}); 
 
app.listen(port, () => { 
    console.log(`Server កំពុងដំេណរ រេ http://localhost:${port}`); 
});
*/



/*
const express = require('express'); 
const app = express(); 
const port = 3000; 
 
app.get('/search', (req, res) => { 
    const query = req.query.q || ' នសំណួ រ'; 
    res.send(`លទផលែសងរក: ${query}`); 
}); 
 
app.listen(port, () => { 
    console.log(`Server កំពុងដំេណរ រេ http://localhost:${port}`); 
}); 

*/



/*
const express = require('express');
const app = express();
const port = 3000;

// Middleware ស ប់ពិនិតសំេណ 
const logRequest = (req, res, next) => {
    console.log(`សំេណេ ន់ ${req.method} ${req.url} េ ${new
        Date().toLocaleTimeString()}`);
    next(); // បនេ route handler 
};

app.use(logRequest);

app.get('/', (req, res) => {
    res.send('សួសីពីទំព័រេដម');
});

app.listen(port, () => {
    console.log(`Server កំពុងដំេណរ រេ http://localhost:${port}`);
});
*/


/*
const express = require('express');
const app = express();
const port = 3000;
app.get('/', (req, res) => {
    res.send('សូមស្វាគមន៍មកកាន់ទំព័រដើម');
});
app.get('/about', (req, res) => {
    res.send('នេះជាទំព័រអំពី');
});
app.post('/submit', (req, res) => {
    res.send('ទទួលបានទិន្នន័យរួចរាល់');
});
// app.use((req, res) => {
//     res.status(404).send('សុំទោស! រកមិនឃើញទំព័រដែលអ្នកចង់រកទេ (404 Not Found)');
// });

// The other way: 
app.all('*', (req, res) => {
    res.status(404).send(`<h1 style="color: red;">រកមិនឃើញទំព័រ!</h1>
        <p>សុំទោស រាល់ការស្នើសុំទៅកាន់ <strong>${req.url}</strong> 
        មិនត្រូវបានអនុញ្ញាត ឬមិនមានក្នុងប្រព័ន្ធឡើយ។</p>
        <a href="/">ត្រឡប់ទៅទំព័រដើមវិញ</a>
    `);
});



app.listen(port, () => {
    console.log(`Server កំពុងដំណើរការនៅ http://localhost:${port}`);
});
*/




/*
const express = require('express'); 
const app = express(); 
const port = 3000; 
 
// បេមឯក រ static ពីថត 'public' 
app.use(express.static('public'));
 
app.listen(port, () => { 
    console.log(`Server កំពុងដំេណរ រេ http://localhost:${port}`); 
});
*/


/*
const path = require('path');
const express = require('express'); 
const app = express(); 
const port = 3000; 
 
// បេមឯក រ static ពីថត 'public' 
app.use(express.static(path.join(__dirname, 'public')));
 
app.listen(port, () => { 
    console.log(`Server កំពុងដំេណរ រេ http://localhost:${port}`); 
});
*/


/*
const express = require('express');
const app = express(); const port = 3000;
app.use(express.urlencoded({ extended: true }));
app.use(express.json());
app.get('/', (req, res) => {
    res.send(`
        <form method="POST" action="/submit">
            <input type="text" name="username" placeholder="បញ្ចូលឈ្មោះ">
            <button type="submit">បញ្ជូន</button>
        </form>
    `);
});

app.post('/submit', (req, res) => {
    const username = req.body.username;
    res.send(`សួស្តី ${username}! ទិន្នន័យត្រូវបានបញ្ជូនជោគជ័យ`);
});

app.listen(port, () => {
    console.log(`Server កំពុងដំណើរការនៅ http://localhost:${port}`);
});
*/

/*
const path = require('path');
const express = require('express');
const app = express(); const port = 3000;
app.use(express.urlencoded({ extended: true }));
app.use(express.json());

app.use(express.static(path.join(__dirname, 'public')));

app.get('/', (req, res) => {
    res.sendFile( path.join(__dirname, 'public', 'login-form.html'));
});

app.post('/submit', (req, res) => {
    const username = req.body.username;
    res.send(`សួស្តី ${username}! ទិន្នន័យត្រូវបានបញ្ជូនជោគជ័យ`);
});

app.listen(port, () => {
    console.log(`Server កំពុងដំណើរការនៅ http://localhost:${port}`);
});
*/

/*
const path = require('path');
const express = require('express');
const app = express(); const port = 3000;
app.use(express.urlencoded({ extended: true }));
app.use(express.json());

app.use(express.static(path.join(__dirname, 'public')));

app.get('/', (req, res) => {
    res.sendFile( path.join(__dirname, 'public', 'login-form.html'));
});

app.post('/submit', (req, res) => {
    const username = JSON.stringify(req.body); // convert JS obj => JSON
    // JSON.oarse();
    res.send(`សួស្តី ${username}! ទិន្នន័យត្រូវបានបញ្ជូនជោគជ័យ`);
});

app.listen(port, () => {
    console.log(`Server កំពុងដំណើរការនៅ http://localhost:${port}`);
});
*/



const express = require('express');
const app = express();
app.get('/', (req, res) => {
  res.send('Welcome to Home!');
});
app.use((req, res, next) => {
  const err = new Error('រកមិនឃើញ URL ដែលអ្នកចង់បានឡើយ!');
  err.status = 404;
  // បញ្ជូន Error នេះទៅកាន់ Error Handling Middleware
  next(err); 
});
app.use((err, req, res, next) => {
  console.log("Error caught in Middleware!");
  res.status(err.status || 500).json({
    message: err.message,
    status: err.status
  });
});
app.listen(3000);
