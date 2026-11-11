const Product = require("../models/Product");

/* ADD PRODUCT */

exports.addProduct = async(req,res)=>{

    try{

        const product = new Product(req.body);

        await product.save();

        res.json({
            message:"Product Added"
        });

    }catch(err){

        res.status(500).json({
            error:err.message
        });

    }

};

/* GET PRODUCTS */

exports.getProducts = async(req,res)=>{

    try{

        const products = await Product.find();

        res.json(products);

    }catch(err){

        res.status(500).json({
            error:err.message
        });

    }

};

/* DELETE PRODUCT */

exports.deleteProduct = async(req,res)=>{

    try{

        await Product.findByIdAndDelete(
            req.params.id
        );

        res.json({
            message:"Product Deleted"
        });

    }catch(err){

        res.status(500).json({
            error:err.message
        });

    }

};

/* EDIT PRODUCT */

exports.editProduct = async(req,res)=>{

    try{

        await Product.findByIdAndUpdate(
            req.params.id,
            req.body
        );

        res.json({
            message:"Product Updated"
        });

    }catch(err){

        res.status(500).json({
            error:err.message
        });

    }

};