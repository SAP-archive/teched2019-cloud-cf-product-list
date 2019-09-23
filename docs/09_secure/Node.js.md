## Step 4.1: Prerequisite
Make sure that you've cloned the Product List sample application (Node.js) as part of this [Exercise](/docs/02_clone/README.md).

## Step 4.2: Adding required security libraries

In order to enable offline JWT validation of the XS Advanced container security API the `sap-xssec` module needs to be added to the `dependencies` section of the `package.json` file.
The module `xsenv` module is needed to retrieve the configuration of the default services (which are read from environment variable `VCAP_SERVICES` or if not set, from the default configuration file).

```json
"dependencies": {
    "@sap/xsenv": "^1.2.9",
    "@sap/xssec": "^2.1.15",
    ...
}
```

## Step 4.3: Usage of the Security API in the application

If you use [express](https://www.npmjs.com/package/express) and [passport](https://www.npmjs.com/package/passport), you can easily plug a ready-made authentication strategy:

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
        // configure servlet to check against scope "$XSAPPNAME.read"
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
> You can find the above code in the `index.js` file.

## Step 4.4: Build the Project
* Build the project on the console with the following commands:
    ```shell
    D:
    cd D:\Files\Session\SEC366\teched2019-cloud-cf-product-list-teched2019\nodejs
    npm config set @sap:registry https://npm.sap.com
    npm install
    ```
* Finally, make sure that the folder `D:\Files\Session\SEC364\teched2019-cloud-cf-product-list-teched2019\samples\Node.js\node_modules\@sap` contains these three modules: `node-jwt`, `xsenv` and `xssec`. 



