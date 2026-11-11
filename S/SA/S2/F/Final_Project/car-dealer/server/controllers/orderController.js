const Order = require("../models/Order");

exports.createOrder = async(req,res)=>{

    try{

        const order = new Order(req.body);

        await order.save();

        res.json({
            message:"Order Created"
        });

    }catch(err){

        res.status(500).json({
            error:err.message
        });

    }

};