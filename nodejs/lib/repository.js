const repository = require('./products.json');

function getProducts(req, res) {
	res.setHeader('Content-Type', 'application/json');
	const result = [];
	for (let product in repository) {
		if (repository.hasOwnProperty(product)) {
			result.push(repository[product]);
		}
	}
	res.send(result);
}

function getProductsByName (req, res) {
	console.log(req.query.name);
	res.setHeader('Content-Type', 'application/json');
	if (repository.hasOwnProperty(req.query.name))
		res.send([repository[req.query.name]]);
	else
		res.send([]);
}

module.exports = {
	getProducts,
	getProductsByName
}