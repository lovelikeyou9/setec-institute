const Cart = require("../models/Cart");

exports.addToCart = async(req,res)=>{

    try{

        const cart = new Cart(req.body);

        await cart.save();

        res.json({
            message:"Added To Cart"
        });

    }catch(err){

        res.status(500).json({
            error:err.message
        });

    }

};