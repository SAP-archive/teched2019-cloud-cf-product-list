const assert = require('assert');
const request = require('request');

let name1 = 'Notebook Basic 15';
request(`http://localhost:8080/productsByParam?name=${name1}`, (error, response, body) => {
	assert.equal(error, null);
	assert.equal(response.statusCode, 200);
	let result = JSON.parse(response.body);
	assert.equal(result[0].name, name1);
});

let name2 = 'Notebook Professional 15';
request(`http://localhost:8080/productsByParam?name=${name2}`, (error, response, body) => {
	assert.equal(error, null);
	assert.equal(response.statusCode, 200);
	let result = JSON.parse(response.body);
	assert.equal(result[0].name, name2);
});

let name3 = 'Ergo Screen';
request(`http://localhost:8080/productsByParam?name=${name3}`, (error, response, body) => {
	assert.equal(error, null);
	assert.equal(response.statusCode, 200);
	let result = JSON.parse(response.body);
	assert.equal(result[0].name, name3);
});

let emptyName = '';
request(`http://localhost:8080/productsByParam?name=${emptyName}`, (error, response, body) => {
	assert.equal(error, null);
	assert.equal(response.statusCode, 200);
	let result = JSON.parse(response.body);
	assert.deepEqual(result, []);
});

let wrongName = 'NotebookBasic';
request(`http://localhost:8080/productsByParam?name=${wrongName}`, (error, response, body) => {
	assert.equal(error, null);
	assert.equal(response.statusCode, 200);
	let result = JSON.parse(response.body);
	assert.deepEqual(result, []);
});