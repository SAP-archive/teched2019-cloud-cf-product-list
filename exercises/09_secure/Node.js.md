## Step 1: Adding required security libraries

**This step is mandatory only if you work on the master branch. For the advanced branch you can go through it to understand what is happening (no need to change anything)**

To enable offline JWT validation of the XS Advanced container security API the module "sap-xssec" needs to be added to the dependencies section of the package.json.
The module xsenv is needed to retrieve the configuration of the default services (which are read from environment variable VCAP_SERVICES or if not set, from the default configuration file).

```json
{
  "name": "cloud-cf-product-list-sample",
  "version": "1.0.0",
  "description": "Product list demo app for Cloudfoundry",
  "main": "index.js",
  "scripts": {
    "start": "node index.js",
    "test": "node test/test-products-by-param.js"
  },
  "author": "Patrick Spiegel",
  "license": "ISC",
  "dependencies": {
    "@sap/xsenv": "^1.2.9",
    "@sap/xssec": "^2.1.15",
    "express": "^4.16.3",
    "passport": "^0.4.0"
  },
  "devDependencies": {
    "request": "^2.88.0"
  }
}
```



## Step 2: Usage of the Security API in the Application
**This step is mandatory only if you work on the master branch. For the advanced branch you can go through it to understand what is happening (no need to change anything)**

*If you use [express](https://www.npmjs.com/package/express) and [passport](https://www.npmjs.com/package/passport), you can easily plug a ready-made authentication strategy.*

```js
const express = require('express');
const passport = require('passport');
const { JWTStrategy } = require('@sap/xssec');
const xsenv = require('@sap/xsenv');
const { getProducts, getProductsByName} = require('./lib/repository');

const app = express();
const port = process.env.port || 8080;

// XSUAA Middleware
passport.use(new JWTStrategy(xsenv.getServices({uaa:{tag:'xsuaa'}}).uaa));

app.use(passport.initialize());
app.use(passport.authenticate('JWT', { session: false }));

app.get('/products', checkReadScope, getProducts);
app.get('/productsByParam', checkReadScope, getProductsByName);

// Scope check
function checkReadScope(req, res, next) {
	if (req.authInfo.checkLocalScope('read')) {
		return next();
	} else {
    	console.log('Missing the expected scope');
    	res.status(403).end('Forbidden');
	}
}

// Serve static files
app.use('/', express.static('static/'));

app.listen(port, () => {
	console.log('%s listening at %s', app.name, port);
})
```

Now all endpoints are blocked.

*  Run `npm install`

:bulb: For the advanced branch only this command has to be executed from `nodejs`

```shell
D:
cd D:\Files\Session\SEC366\cloud-cf-product-list-sample-advanced\nodejs
npm config set @sap:registry https://npm.sap.com
npm install
```
