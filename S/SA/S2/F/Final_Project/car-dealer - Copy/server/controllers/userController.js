const User = require("../models/User");

/* REGISTER */

exports.register = async(req,res)=>{

    try{

        const user = new User(req.body);

        await user.save();

        res.json({
            message:"Register Success"
        });

    }catch(err){

        res.status(500).json({
            error:err.message
        });

    }

};

/* LOGIN */

exports.login = async(req,res)=>{

    const {email,password} = req.body;

    try{

        const user = await User.findOne({
            email,
            password
        });

        if(user){

            res.json({
                message:"Login Success",
                user
            });

        }else{

            res.status(401).json({
                message:
                "Invalid Email or Password"
            });

        }

    }catch(err){

        res.status(500).json({
            error:err.message
        });

    }

};
