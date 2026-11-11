const express = require('express');
const router = express.Router();
const service = require('../services/productService');

router.get('/', async (req, res) => res.json(await service.getAll()));
router.get('/:id', async (req, res) => res.json(await service.getById(req.params.id)));
router.post('/', async (req, res) => res.json(await service.create(req.body)));
router.put('/:id', async (req, res) => res.json(await service.update(req.params.id, req.body)));
router.delete('/:id', async (req, res) => res.json(await service.delete(req.params.id)));

module.exports = router;
