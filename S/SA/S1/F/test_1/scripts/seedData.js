const mongoose = require('mongoose');
const bcrypt = require('bcryptjs');
require('dotenv').config();

// Import all models
const User = require('./models/User');
const Category = require('./models/Category');
const Product = require('./models/Product');
const ProductLine = require('./models/ProductLine');
const Office = require('./models/Office');
const Employee = require('./models/Employee');
const Settings = require('./models/Settings');

const connectDB = require('./config/db');

async function seedData() {
    try {
        await connectDB();
        
        // Clear existing data (optional)
        // await mongoose.connection.dropDatabase();
        
        // 1. Create default settings
        const settings = [
            {
                key: 'store_name',
                value: 'ComputerStore',
                type: 'string',
                category: 'general',
                label: 'Store Name',
                description: 'The name of your online store'
            },
            {
                key: 'currency',
                value: 'USD',
                type: 'string',
                category: 'general',
                label: 'Currency',
                description: 'Default currency for prices'
            },
            {
                key: 'tax_rate',
                value: 8.0,
                type: 'number',
                category: 'tax',
                label: 'Tax Rate',
                description: 'Sales tax rate in percentage'
            },
            {
                key: 'free_shipping_threshold',
                value: 100,
                type: 'number',
                category: 'shipping',
                label: 'Free Shipping Threshold',
                description: 'Minimum order amount for free shipping'
            },
            {
                key: 'shipping_cost',
                value: 9.99,
                type: 'number',
                category: 'shipping',
                label: 'Standard Shipping Cost',
                description: 'Standard shipping cost for orders below threshold'
            }
        ];
        
        await Settings.insertMany(settings);
        console.log('✓ Settings created');
        
        // 2. Create admin user
        const hashedPassword = await bcrypt.hash('admin123', 10);
        const adminUser = await User.create({
            name: 'Admin User',
            email: 'admin@computerstore.com',
            password: hashedPassword,
            role: 'admin',
            emailVerified: true
        });
        console.log('✓ Admin user created');
        
        // 3. Create office
        const office = await Office.create({
            officeCode: 'HQ001',
            name: 'Headquarters',
            address: {
                street: '123 Tech Street',
                city: 'San Francisco',
                state: 'CA',
                zipCode: '94107',
                country: 'USA'
            },
            phone: '+1 (555) 123-4567',
            email: 'hq@computerstore.com'
        });
        console.log('✓ Office created');
        
        // 4. Create employee
        const employeeUser = await User.create({
            name: 'John Manager',
            email: 'manager@computerstore.com',
            password: await bcrypt.hash('employee123', 10),
            role: 'employee',
            emailVerified: true
        });
        
        const employee = await Employee.create({
            user: employeeUser._id,
            employeeId: 'EMP001',
            department: 'management',
            position: 'Store Manager',
            office: office._id,
            salary: 60000
        });
        
        // Update office with manager
        office.manager = employee._id;
        await office.save();
        console.log('✓ Employee created');
        
        // 5. Create product lines
        const gamingLine = await ProductLine.create({
            productLine: 'GAMING',
            name: 'Gaming Series',
            description: 'High-performance gaming computers and accessories',
            category: '695b8805166fe4363a41eafa', // Gaming PCs category ID
            manufacturer: 'Alienware',
            warrantyPeriod: 24
        });
        
        const productivityLine = await ProductLine.create({
            productLine: 'PRODUCTIVITY',
            name: 'Productivity Series',
            description: 'Computers and accessories for work and productivity',
            category: '695b8805166fe4363a41eafb', // Laptops category ID
            manufacturer: 'Apple',
            warrantyPeriod: 12
        });
        console.log('✓ Product lines created');
        
        // 6. Update existing products with product lines and SKUs
        const products = await Product.find();
        
        for (const product of products) {
            // Assign product line based on category
            if (product.name.includes('Alienware') || product.name.includes('ROG')) {
                product.productLine = gamingLine._id;
                product.sku = `GAM-${Date.now().toString().slice(-6)}`;
            } else if (product.name.includes('MacBook')) {
                product.productLine = productivityLine._id;
                product.sku = `PROD-${Date.now().toString().slice(-6)}`;
            } else {
                product.sku = `GEN-${Date.now().toString().slice(-6)}`;
            }
            
            // Add more images
            product.images = [
                product.image,
                'https://images.unsplash.com/photo-1499951360447-b19be8fe80f5?w=600&fit=crop',
                'https://images.unsplash.com/photo-1517077304055-6e89abbf09b0?w=600&fit=crop'
            ];
            product.mainImage = product.image;
            
            // Add specifications
            product.specifications = new Map([
                ['Processor', 'Intel Core i9'],
                ['RAM', '32GB DDR5'],
                ['Storage', '1TB NVMe SSD'],
                ['Graphics', 'NVIDIA RTX 4090'],
                ['Display', '27" 4K UHD']
            ]);
            
            // Add features
            product.features = [
                'High-performance components',
                'RGB lighting',
                'Advanced cooling system',
                'Upgradable design',
                'Premium build quality'
            ];
            
            // Add dimensions
            product.dimensions = {
                length: 50,
                width: 25,
                height: 45,
                weight: 15
            };
            
            // Add warranty
            product.warranty = {
                period: 24,
                type: 'Standard Warranty'
            };
            
            await product.save();
        }
        console.log('✓ Products updated with complete data');
        
        console.log('\n✅ Database seeding completed successfully!');
        console.log('\nDefault credentials:');
        console.log('Admin: admin@computerstore.com / admin123');
        console.log('Manager: manager@computerstore.com / employee123');
        
        process.exit(0);
        
    } catch (error) {
        console.error('❌ Seeding error:', error);
        process.exit(1);
    }
}

seedData();
