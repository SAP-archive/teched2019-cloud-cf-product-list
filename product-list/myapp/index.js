const express = require('express');
const { getProducts, getProductsByName} = require('./lib/repository');
const app = express();
const port = process.env.port || 8080;
// secure the direct call to the application

app.get('/products', getProducts);

// Serve static files
app.use('/', express.static('static/'));

app.listen(port, () => {
	console.log('%s listening at %s', app.name, port);
})
