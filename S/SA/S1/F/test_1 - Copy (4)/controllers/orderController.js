const Order = require('../models/Order');

exports.createOrder = async (req, res) => {
    try {
        const cart = req.session.cart;

        if (!cart || cart.length === 0) {
            return res.status(400).json({ success: false, error: "Your cart is empty." });
        }

        const { shippingAddress, paymentMethod } = req.body;

        // Calculate Totals
        const subtotal = cart.reduce((sum, item) => sum + (item.price * item.quantity), 0);
        const tax = subtotal * 0.08;
        const total = subtotal + tax;

        // Generate Order Number manually here to ensure it exists before validation
        const date = new Date();
        const year = date.getFullYear().toString().slice(-2);
        const month = (date.getMonth() + 1).toString().padStart(2, '0');
        const day = date.getDate().toString().padStart(2, '0');
        const random = Math.floor(1000 + Math.random() * 9000);
        const generatedOrderNumber = `ORD-${year}${month}${day}-${random}`;

        // Create Order Object
        const orderData = {
            orderNumber: generatedOrderNumber,
            items: cart.map(item => ({
                product: item.productId,
                name: item.name,
                price: item.price,
                quantity: item.quantity,
                subtotal: item.price * item.quantity
            })),
            subtotal,
            tax,
            total,
            status: 'pending',
            shippingAddress,
            paymentMethod,
            paymentStatus: 'pending'
        };

        // ONLY add user ID if the user is actually logged in
        if (req.session.userId) {
            orderData.user = req.session.userId;
        }

        const newOrder = new Order(orderData);
        await newOrder.save();

        // Clear the cart
        req.session.cart = [];

        res.json({
            success: true,
            message: "Order placed!",
            orderNumber: newOrder.orderNumber
        });

    } catch (error) {
        console.error("Order Error:", error);
        res.status(500).json({ success: false, error: error.message });
    }
};
