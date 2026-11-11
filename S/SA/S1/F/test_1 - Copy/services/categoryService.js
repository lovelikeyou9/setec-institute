const Category = require('../models/Category');

exports.getAll = async () => await Category.find();
exports.getById = async (id) => await Category.findById(id);
exports.create = async (data) => {
    const category = new Category({ name: data.name });
    return await category.save();
};
exports.update = async (id, data) => await Category.findByIdAndUpdate(id, data, { new: true });
exports.delete = async (id) => await Category.findByIdAndDelete(id);
