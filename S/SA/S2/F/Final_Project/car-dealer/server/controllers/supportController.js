const SupportMessage =
require("../models/SupportMessage");

exports.sendMessage = async(req,res)=>{

    try{

        const support =
        new SupportMessage(req.body);

        await support.save();

        res.json({
            message:
            "Support Message Sent"
        });

    }catch(err){

        res.status(500).json({
            error:err.message
        });

    }

};
