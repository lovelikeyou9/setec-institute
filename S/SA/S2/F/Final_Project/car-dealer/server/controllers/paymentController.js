const Payment = require("../models/Payment");

exports.makePayment = async(req,res)=>{

    try{

        const payment =
        new Payment(req.body);

        await payment.save();

        res.json({
            message:"Payment Success"
        });

    }catch(err){

        res.status(500).json({
            error:err.message
        });

    }

};