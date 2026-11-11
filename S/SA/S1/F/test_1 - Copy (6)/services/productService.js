const Product = require('../models/Product');

exports.getAll = async () => await Product.find().populate('category');
exports.getById = async (id) => await Product.findById(id).populate('category');
exports.create = async (data) => {
    const product = new Product(data);
    return await product.save();
};
exports.update = async (id, data) => await Product.findByIdAndUpdate(id, data, { new: true });
exports.delete = async (id) => await Product.findByIdAndDelete(id);
