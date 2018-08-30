const restify = require('restify');
const server = restify.createServer({name: 'Product List Sample'});
const { repository } = require('./lib/repository');

server.use(restify.plugins.queryParser());

server.get('/productsByParam', (req, res, next) => {
	console.log(req.query.name);
	if (repository.has(req.query.name))
		res.send([repository.get(req.query.name)]);
	else
		res.send([]);
  return next();
});

server.listen(8080, () => {
	console.log('%s listening at %s', server.name, server.url);
})
