const mongoose = require("mongoose");

const paymentSchema = new mongoose.Schema({

    orderId:{
        type:mongoose.Schema.Types.ObjectId,
        ref:"Order"
    },

    userId:{
        type:mongoose.Schema.Types.ObjectId,
        ref:"User"
    },

    amount:Number,

    method:String,

    paymentStatus:{
        type:String,
        default:"Paid"
    },

    createdAt:{
        type:Date,
        default:Date.now
    }

});

module.exports = mongoose.model(
    "Payment",
    paymentSchema
);
