/*

const express = require('express');
const app = express();
const port = 3000;
// Route for Default/Main Page
app.get('/', (req, res) => {
    res.send('Welcome back to Default/Main Page');
});
// Route for Page "about" 
app.get('/about', (req, res) => {
    res.send('It is About');
});

// Route for POST request 
app.post('/submit', (req, res) => {
    res.send('Received Data');
});

app.listen(port, () => {
    console.log(`Server is running http://localhost:${port}`);
});

*/




const express = require('express');
const app = express();
const port = 3000;

// Middleware to parse JSON and form data
app.use(express.json());
app.use(express.urlencoded({ extended: true }));

// In-memory storage for registered users
let users = [];

// Simple navigation HTML
const navigation = `
<div style="background:#f0f0f0; padding:15px; margin-bottom:20px; border-radius:5px;">
    <h3 style="margin-top:0;">Navigation Menu</h3>
    <div style="display:flex; gap:10px; flex-wrap:wrap;">
        <a href="/" style="background:#007bff; color:white; padding:8px 15px; text-decoration:none; border-radius:4px;">🏠 Home</a>
        <a href="/about" style="background:#28a745; color:white; padding:8px 15px; text-decoration:none; border-radius:4px;">ℹ️ About</a>
        <a href="/register" style="background:#ffc107; color:black; padding:8px 15px; text-decoration:none; border-radius:4px;">📝 Register</a>
        <a href="/users" style="background:#17a2b8; color:white; padding:8px 15px; text-decoration:none; border-radius:4px;">👥 Users API</a>
        <button onclick="testPost()" style="background:#6f42c1; color:white; padding:8px 15px; border:none; border-radius:4px; cursor:pointer;">📤 Test POST</button>
    </div>
</div>
<script>
function testPost() {
    fetch('/submit', {
        method: 'POST',
        headers: {'Content-Type': 'application/json'},
        body: JSON.stringify({test: 'data', timestamp: new Date().toISOString()})
    })
    .then(r => r.text())
    .then(data => alert('POST Response: ' + data))
    .catch(e => alert('Error: ' + e));
}
</script>
`;

// Route for Default/Main Page - Updated with navigation
app.get('/', (req, res) => {
    res.send(`
        ${navigation}
        <div style="max-width:800px; margin:0 auto;">
            <h1>Welcome back to Default/Main Page</h1>
            <p>This is the main page with navigation buttons at the top.</p>
            <p>Use the buttons above to navigate to different sections:</p>
            <ul>
                <li><strong>Home</strong> - Current page</li>
                <li><strong>About</strong> - About page</li>
                <li><strong>Register</strong> - User registration form</li>
                <li><strong>Users API</strong> - View all registered users (JSON)</li>
                <li><strong>Test POST</strong> - Test the POST /submit endpoint</li>
            </ul>
            <div style="margin-top:20px; padding:15px; background:#e8f4f8; border-radius:5px;">
                <h3>📊 Quick Stats:</h3>
                <p>Total Registered Users: <strong>${users.length}</strong></p>
            </div>
        </div>
    `);
});

// Route for Page "about" - Updated with navigation
app.get('/about', (req, res) => {
    res.send(`
        ${navigation}
        <div style="max-width:800px; margin:0 auto;">
            <h1>About Page</h1>
            <p><strong>It is About</strong> this application!</p>
            <div style="background:#f8f9fa; padding:20px; border-radius:5px; margin-top:20px;">
                <h3>This application includes:</h3>
                <ul>
                    <li>User Registration System</li>
                    <li>GET and POST endpoints</li>
                    <li>In-memory user storage</li>
                    <li>JSON API endpoints</li>
                    <li>Navigation menu</li>
                </ul>
            </div>
        </div>
    `);
});

// Route for POST request - Updated to show data received
app.post('/submit', (req, res) => {
    const htmlResponse = `
        ${navigation}
        <div style="max-width:800px; margin:0 auto;">
            <h1 style="color:green;">✅ Received Data Successfully!</h1>
            <div style="background:#f0f8ff; padding:15px; border-radius:5px; margin:20px 0;">
                <h3>Data Received:</h3>
                <pre style="background:white; padding:10px; border-radius:3px;">${JSON.stringify(req.body, null, 2)}</pre>
            </div>
            <p><strong>Timestamp:</strong> ${new Date().toLocaleString()}</p>
            <p>Use the navigation buttons above to go to other pages.</p>
        </div>
    `;
    res.send(htmlResponse);
});

// Route to display user registration form - Already has good navigation
app.get('/register', (req, res) => {
    const formHTML = `
    <!DOCTYPE html>
    <html>
    <head>
        <title>User Registration</title>
        <style>
            body { font-family: Arial, sans-serif; margin: 40px; }
            .container { max-width: 800px; margin: 0 auto; }
            .form-group { margin-bottom: 15px; }
            label { display: block; margin-bottom: 5px; }
            input { width: 100%; padding: 8px; box-sizing: border-box; }
            button { background: #007bff; color: white; padding: 10px 15px; border: none; cursor: pointer; }
            button:hover { background: #0056b3; }
            .error { color: red; margin-top: 10px; }
            .success { color: green; margin-top: 10px; }
            .user-list { margin-top: 20px; border-top: 1px solid #ddd; padding-top: 20px; }
        </style>
    </head>
    <body>
        <div class="container">
            ${navigation}
            <h2>User Registration</h2>
            <form action="/register" method="POST">
                <div class="form-group">
                    <label for="username">Username:</label>
                    <input type="text" id="username" name="username" required>
                </div>
                <div class="form-group">
                    <label for="email">Email:</label>
                    <input type="email" id="email" name="email" required>
                </div>
                <div class="form-group">
                    <label for="password">Password:</label>
                    <input type="password" id="password" name="password" required>
                </div>
                <button type="submit">Register</button>
            </form>
            
            <div class="user-list">
                <h3>Registered Users (${users.length} total):</h3>
                <ul id="userList">
                    ${users.map(user => `<li>${user.username} (${user.email}) - ID: ${user.id}</li>`).join('')}
                </ul>
                <p><a href="/users">View all users API endpoint</a></p>
            </div>
        </div>
    </body>
    </html>
    `;
    res.send(formHTML);
});

// Registration POST route - Keep as is (it already has navigation buttons)
app.post('/register', (req, res) => {
    const { username, email, password } = req.body;
    
    // Basic validation
    if (!username || !email || !password) {
        return res.status(400).send(`
            ${navigation}
            <div style="color:red; padding:20px; max-width:800px; margin:0 auto;">
                <h2>❌ Error: All fields are required!</h2>
                <p>Please go back and fill all fields.</p>
                <button onclick="location.href='/register'" style="background:#dc3545; color:white; padding:10px 15px; border:none; border-radius:4px; cursor:pointer;">
                    Go Back to Registration
                </button>
            </div>
        `);
    }
    
    // Check if user already exists
    const existingUser = users.find(user => user.email === email);
    if (existingUser) {
        return res.status(400).send(`
            ${navigation}
            <div style="color:red; padding:20px; max-width:800px; margin:0 auto;">
                <h2>❌ Error: User with this email already exists!</h2>
                <p>Email "${email}" is already registered.</p>
                <button onclick="location.href='/register'" style="background:#dc3545; color:white; padding:10px 15px; border:none; border-radius:4px; cursor:pointer;">
                    Try Again
                </button>
            </div>
        `);
    }
    
    // Create new user object
    const newUser = {
        id: users.length + 1,
        username,
        email,
        password: password,
        registeredAt: new Date().toISOString()
    };
    
    // Add user to array
    users.push(newUser);
    
    // Send success response
    res.send(`
        <!DOCTYPE html>
        <html>
        <head>
            <title>Registration Successful</title>
            <style>
                body { font-family: Arial, sans-serif; margin: 40px; }
                .container { max-width: 400px; margin: 0 auto; text-align: center; }
                .success { color: green; font-size: 18px; margin: 20px 0; }
                button { background: #007bff; color: white; padding: 10px 15px; border: none; cursor: pointer; margin: 5px; }
                button:hover { background: #0056b3; }
            </style>
        </head>
        <body>
            ${navigation}
            <div class="container">
                <h2>✅ Registration Successful!</h2>
                <div class="success">User "${username}" has been registered successfully!</div>
                <p><strong>Email:</strong> ${email}</p>
                <p><strong>User ID:</strong> ${newUser.id}</p>
                <div>
                    <button onclick="location.href='/'">🏠 Go to Home</button>
                    <button onclick="location.href='/register'">📝 Register Another User</button>
                    <button onclick="location.href='/users'">👥 View All Users</button>
                </div>
            </div>
        </body>
        </html>
    `);
});

// API endpoint to get all registered users (JSON)
app.get('/users', (req, res) => {
    // Return users without passwords for security
    const usersWithoutPasswords = users.map(user => {
        const { password, ...userWithoutPassword } = user;
        return userWithoutPassword;
    });
    
    // Check if client wants HTML or JSON
    if (req.accepts('html')) {
        res.send(`
            ${navigation}
            <div style="max-width:800px; margin:0 auto;">
                <h1>👥 Registered Users (${users.length} total)</h1>
                <div style="background:#f8f9fa; padding:20px; border-radius:5px;">
                    <h3>User List:</h3>
                    ${usersWithoutPasswords.map(user => `
                        <div style="padding:10px; margin:5px 0; background:white; border-radius:4px;">
                            <strong>${user.username}</strong> (${user.email})
                            <br><small>ID: ${user.id} | Registered: ${new Date(user.registeredAt).toLocaleString()}</small>
                        </div>
                    `).join('')}
                </div>
                <p style="margin-top:20px;"><strong>JSON API:</strong> This endpoint also returns JSON. Use a tool like curl or Postman to see the raw JSON response.</p>
            </div>
        `);
    } else {
        res.json({
            count: users.length,
            users: usersWithoutPasswords
        });
    }
});

// API endpoint to check if a specific user is registered
app.get('/user/:id', (req, res) => {
    const userId = parseInt(req.params.id);
    const user = users.find(u => u.id === userId);
    
    if (!user) {
        if (req.accepts('html')) {
            return res.status(404).send(`
                ${navigation}
                <div style="color:red; padding:20px; max-width:800px; margin:0 auto;">
                    <h2>❌ User Not Found</h2>
                    <p>User with ID ${userId} was not found.</p>
                </div>
            `);
        }
        return res.status(404).json({ error: 'User not found' });
    }
    
    // Remove password from response
    const { password, ...userWithoutPassword } = user;
    
    if (req.accepts('html')) {
        res.send(`
            ${navigation}
            <div style="max-width:800px; margin:0 auto;">
                <h1>👤 User Details</h1>
                <div style="background:#f8f9fa; padding:20px; border-radius:5px;">
                    <h3>${userWithoutPassword.username}</h3>
                    <p><strong>Email:</strong> ${userWithoutPassword.email}</p>
                    <p><strong>User ID:</strong> ${userWithoutPassword.id}</p>
                    <p><strong>Registered:</strong> ${new Date(userWithoutPassword.registeredAt).toLocaleString()}</p>
                </div>
            </div>
        `);
    } else {
        res.json(userWithoutPassword);
    }
});

// Other endpoints remain the same...
app.get('/check-user/:email', (req, res) => {
    const email = req.params.email;
    const user = users.find(u => u.email === email);
    
    if (!user) {
        return res.json({ registered: false, message: 'User not found' });
    }
    
    // Remove password from response
    const { password, ...userWithoutPassword } = user;
    res.json({ registered: true, user: userWithoutPassword });
});

const checkUserRegistered = (req, res, next) => {
    const userId = req.headers['user-id'] || req.query.userId;
    
    if (!userId) {
        return res.status(401).json({ error: 'User ID required' });
    }
    
    const user = users.find(u => u.id === parseInt(userId));
    
    if (!user) {
        return res.status(403).json({ error: 'User not registered' });
    }
    
    req.user = user;
    next();
};

app.get('/profile', checkUserRegistered, (req, res) => {
    const { password, ...userWithoutPassword } = req.user;
    res.json({
        message: 'Welcome to your profile',
        user: userWithoutPassword
    });
});

app.listen(port, () => {
    console.log(`Server is running http://localhost:${port}`);
    console.log(`Access the registration form at: http://localhost:${port}/register`);
    console.log(`View all users API at: http://localhost:${port}/users`);
    console.log('\n🔗 Navigation Links:');
    console.log('├── Home: http://localhost:3000/');
    console.log('├── About: http://localhost:3000/about');
    console.log('├── Register: http://localhost:3000/register');
    console.log('├── Users API: http://localhost:3000/users');
    console.log('└── Test POST: Click "Test POST" button in navigation');
});
