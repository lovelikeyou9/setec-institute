const mongoose = require("mongoose");

const supportMessageSchema =
new mongoose.Schema({

    userId:{
        type:mongoose.Schema.Types.ObjectId,
        ref:"User"
    },

    message:String,

    reply:{
        type:String,
        default:""
    },

    status:{
        type:String,
        default:"Open"
    },

    createdAt:{
        type:Date,
        default:Date.now
    }

});

module.exports = mongoose.model(
    "SupportMessage",
    supportMessageSchema
);