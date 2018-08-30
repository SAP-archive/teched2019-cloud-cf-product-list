const repository = new Map();

require('./products.json').forEach((item) => {
	repository.set(item.name, item);
});

module.exports.repository = repository;
