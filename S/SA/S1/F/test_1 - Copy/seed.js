// seed.js - WINDOWS COMPATIBLE SEED SCRIPT
require('dotenv').config();
const mongoose = require('mongoose');
const bcrypt = require('bcryptjs');

// Connect to MongoDB
const mongoURI = process.env.MONGO_URI || 'mongodb://127.0.0.1:27017/computer_store';
console.log('🔗 Connecting to MongoDB...');

mongoose.connect(mongoURI)
    .then(() => {
        console.log('✅ Connected to MongoDB');
        seedDatabase();
    })
    .catch(err => {
        console.error('❌ MongoDB connection error:', err);
        process.exit(1);
    });

// Import models
const Category = require('./models/Category');
const Product = require('./models/Product');
const User = require('./models/User');

async function seedDatabase() {
    try {
        console.log('🗑️  Clearing existing data...');
        
        // Clear in order to avoid reference errors
        await Product.deleteMany({});
        await Category.deleteMany({});
        await User.deleteMany({});
        
        console.log('✅ Existing data cleared');
        
        // ========== CREATE CATEGORIES ==========
        console.log('📂 Creating categories...');
        const categories = [
            { 
                name: 'Gaming PCs', 
                description: 'High-performance gaming computers',
                createdAt: new Date()
            },
            { 
                name: 'Laptops', 
                description: 'Portable computers and notebooks',
                createdAt: new Date()
            },
            { 
                name: 'Components', 
                description: 'Computer parts and components',
                createdAt: new Date()
            },
            { 
                name: 'Peripherals', 
                description: 'Keyboards, mice, and accessories',
                createdAt: new Date()
            },
            { 
                name: 'Monitors', 
                description: 'Computer displays and screens',
                createdAt: new Date()
            },
            { 
                name: 'Networking', 
                description: 'Routers, switches, and networking gear',
                createdAt: new Date()
            }
        ];
        
        const createdCategories = await Category.insertMany(categories);
        console.log(`✅ Created ${createdCategories.length} categories`);
        
        // Create a map for easy category reference
        const categoryMap = {};
        createdCategories.forEach(cat => {
            categoryMap[cat.name] = cat._id;
        });
        
        // ========== CREATE PRODUCTS ==========
        console.log('📦 Creating products...');
        const products = [
            {
                name: 'Alienware Aurora R15',
                description: 'High-end gaming desktop with RTX 4090 and i9 processor',
                price: 2999.99,
                quantity: 15,
                category: categoryMap['Gaming PCs'],
                image: 'https://images.unsplash.com/photo-1593640408182-31c70c8268f5?w=400&h=300&fit=crop',
                isFeatured: true,
                createdAt: new Date()
            },
            {
                name: 'MacBook Pro 16"',
                description: 'Apple laptop with M3 Pro chip and Liquid Retina XDR display',
                price: 2499.99,
                quantity: 25,
                category: categoryMap['Laptops'],
                image: 'https://images.unsplash.com/photo-1517336714731-489689fd1ca8?w=400&h=300&fit=crop',
                isFeatured: true,
                createdAt: new Date()
            },
            {
                name: 'ASUS ROG Strix G16',
                description: 'Gaming laptop with NVIDIA GeForce RTX 4060',
                price: 1499.99,
                quantity: 30,
                category: categoryMap['Laptops'],
                image: 'https://images.unsplash.com/photo-1603302576837-37561b2e2302?w=400&h=300&fit=crop',
                isFeatured: false,
                createdAt: new Date()
            },
            {
                name: 'Corsair Vengeance RGB RAM',
                description: '32GB DDR5 RGB memory kit for gaming PCs',
                price: 159.99,
                quantity: 100,
                category: categoryMap['Components'],
                image: 'https://images.unsplash.com/photo-1591488320449-011701bb6704?w=400&h=300&fit=crop',
                isFeatured: false,
                createdAt: new Date()
            },
            {
                name: 'Logitech MX Master 3',
                description: 'Wireless mouse with advanced features',
                price: 99.99,
                quantity: 75,
                category: categoryMap['Peripherals'],
                image: 'https://images.unsplash.com/photo-1527814050087-3793815479db?w=400&h=300&fit=crop',
                isFeatured: false,
                createdAt: new Date()
            },
            {
                name: 'Samsung Odyssey G9',
                description: '49" Super Ultrawide gaming monitor',
                price: 1299.99,
                quantity: 20,
                category: categoryMap['Monitors'],
                image: 'https://images.unsplash.com/photo-1593359677879-a4bb92f829d1?w=400&h=300&fit=crop',
                isFeatured: false,
                createdAt: new Date()
            },
            {
                name: 'RTX 4090 Graphics Card',
                description: 'NVIDIA GeForce RTX 4090 24GB',
                price: 1899.99,
                quantity: 12,
                category: categoryMap['Components'],
                image: 'https://images.unsplash.com/photo-1593640408182-31c70c8268f5?w=400&h=300&fit=crop',
                isFeatured: false,
                createdAt: new Date()
            },
            {
                name: 'Mechanical Keyboard',
                description: 'RGB mechanical gaming keyboard with brown switches',
                price: 129.99,
                quantity: 50,
                category: categoryMap['Peripherals'],
                image: 'https://images.unsplash.com/photo-1541140532154-b024d705b90a?w=400&h=300&fit=crop',
                isFeatured: false,
                createdAt: new Date()
            }
        ];
        
        const createdProducts = await Product.insertMany(products);
        console.log(`✅ Created ${createdProducts.length} products`);
        
        // ========== CREATE USERS ==========
        console.log('👤 Creating users...');
        
        // Create admin user
        const adminPassword = await bcrypt.hash('admin123', 10);
        await User.create({
            name: 'Admin User',
            email: 'admin@computerstore.com',
            password: adminPassword,
            address: '123 Tech Street, San Francisco, CA',
            phone: '+1 (555) 123-4567',
            createdAt: new Date()
        });
        
        // Create regular user
        const userPassword = await bcrypt.hash('user123', 10);
        await User.create({
            name: 'John Doe',
            email: 'john@example.com',
            password: userPassword,
            address: '456 User Avenue, New York, NY',
            phone: '+1 (555) 987-6543',
            createdAt: new Date()
        });
        
        console.log('✅ Created 2 users');
        
        // ========== FINAL SUMMARY ==========
        console.log('\n' + '='.repeat(50));
        console.log('🎉 DATABASE SEEDING COMPLETED SUCCESSFULLY!');
        console.log('='.repeat(50));
        console.log('\n📊 SUMMARY:');
        console.log(`• Categories: ${createdCategories.length}`);
        console.log(`• Products: ${createdProducts.length}`);
        console.log(`• Users: 2`);
        
        console.log('\n🔑 LOGIN CREDENTIALS:');
        console.log('Admin: admin@computerstore.com / admin123');
        console.log('User: john@example.com / user123');
        
        console.log('\n🚀 START SERVER:');
        console.log('npm start');
        
        console.log('\n🌐 TEST LINKS:');
        console.log('http://localhost:3000');
        console.log('http://localhost:3000/products');
        console.log('http://localhost:3000/api/products');
        
        // Exit successfully
        setTimeout(() => {
            console.log('\n✅ Seed script completed. You can now start the server.');
            process.exit(0);
        }, 1000);
        
    } catch (error) {
        console.error('\n❌ ERROR SEEDING DATABASE:');
        console.error('Message:', error.message);
        
        if (error.errors) {
            console.error('\nValidation errors:');
            Object.keys(error.errors).forEach(key => {
                console.error(`• ${key}: ${error.errors[key].message}`);
            });
        }
        
        process.exit(1);
    }
}
