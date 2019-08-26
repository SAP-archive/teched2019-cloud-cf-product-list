# Securing an Application with OAuth 2.0

## Estimated time

:clock4: 60 minutes

## Objective
In this exercise, you will learn how to secure the Product List application by using a flexible authorization framework - OAuth 2.0. The Authorization Code grant of OAuth 2.0 provides an excellent security mechanism to grant only authorized users access to your application and its data. The SAP XS Advanced Application Router, the SAP XSUAA OAuth authorization service and an application written using Spring Boot, Node.js or Java are outstanding tools to configure roles, assign them to users and, finally, implement role checks in your application.

# Exercise description
Microservices deployed on SAP Cloud Platform are freely accessible via the internet. To restrict access to authorized users only each microservice like the Product List application has to implement appropriate security mechanisms like [OAuth 2.0.](https://tools.ietf.org/html/rfc6749)

## Steps overview
The following steps are required to protect the Product List application with OAuth 2.O on the SAP Cloud Platform:

* Step 1: Definition of the Application Security Descriptor
* Step 2: Creation and configuration of the XSUAA service
* Step 3: Configuration of the Application Router
* Step 4: Secure the Product List application using XSUAA client libraries
* Step 5: Deployment of the Product List Application and Approuter
* Step 6: Cockpit administration task: Assign Role Collection to your User
* Step 7: Access the Application
* Step 8: Clean up

### Step 1: Definition of the Application Security Descriptor

An Application Security Descriptor defines the details of the authentication methods and authorization types to use for accessing the Product List application. 
The Product List application uses this information to perform scope checks. With scopes a fine-grained user authorization can be build up. 
The container security library integrated in Spring, Node.js and Java Web applications allows to check scopes for each HTTP method on all HTTP endpoints. 
Scopes are carried by [JSON Web Tokens (JWTs)](https://tools.ietf.org/html/rfc7519) which in turn are issued by the [XSUAA Service](https://help.sap.com/viewer/4505d0bdaf4948449b7f7379d24d0f0d/1.0.12/en-US/17acf1ac0cf84487a3199c51b28feafd.html).

* Find `xs-security.json` in the `/samples` folder: 

```json
{
	"xsappname": "product-list",
	"tenant-mode": "dedicated",
	"scopes": [
		{
			"name": "$XSAPPNAME.read",
			"description": "With this scope, USER can read products."
		}
	],

	"role-templates": [
		{
			"name": "Viewer",
			"description": "Role to get the list of products",
			"scope-references": [
				"$XSAPPNAME.read"
			]
		}
	],
	"role-collections": [
		{
			"name": "ProductListViewer",
			"description": "Product List User",
			"role-template-references": [
				"$XSAPPNAME.Viewer"
			]
		}
	]
}
```

### Step 2: Creation and configuration of the XSUAA service

To grant users access to the Product List application, an instance of the XSUAA service for this application must be created; the XSUAA service instance acts as an OAuth 2.0 client for the bound application.
* You need to tell the CF CLI which Cloud Foundry you will use. To do this you have to set the API endpoint to the Cloud Controller of the Cloud Foundry region where you created your Cloud Foundry trial. Open a command prompt, navigate to the folder ```cloud-cf-product-list-sample-teched2019``` in the student directory and use the command  ```cf api CLOUD_FOUNDRY_API_ENDPOINT```.

  * If you attend TechEd Las Vegas, target the US10 region API endpoint:
  ```
  cf api https://api.cf.us10.hana.ondemand.com
  ```
  * If you attend TechEd Barcelona, target the EU10 region API endpoint:
  ```
  cf api https://api.cf.eu10.hana.ondemand.com
  ```

:bulb: **Note:** You can find the API endpoints for the different regions where Cloud Foundry Environment is available in the [SAP Cloud Platform Documentation](https://help.sap.com/viewer/65de2977205c403bbc107264b8eccf4b/Cloud/en-US/350356d1dc314d3199dca15bd2ab9b0e.html)

* Login with your user account. At the command prompt type:
	```
	cf login
	```

	You will be prompted to fill in the e-mail and password you used when you registered for the SAP Cloud Platform trial account:

	```
	Email> enter your e-mail
	Password> password for your user
	```
* Show the marketplace:  `cf m`
* Create the XSUAA service instance: 
    ```
    D:
    cd D:\Files\Session\SEC364\cloud-cf-product-list-sample-teched2019\samples
    cf create-service xsuaa application xsuaa -c xs-security.json
    ```

### Step 3: Configuration of the Application Router

The Application Router is used to provide a single entry point to a business application that consists of several different apps (microservices). It dispatches requests to backend microservices and acts as a reverse proxy. The rules that determine which request should be forwarded to which _destinations_ are called _routes_. The application router can be configured to authenticate the users and propagate the user information. Finally, the application router can serve static content.

* You can find all files that are required to install and configure the Application Router in the `/samples/approuter` folder.
  * [`.npmrc`](/samples/approuter/.npmrc)  
  With this the node modules are downloaded by the NPM package manager from the https://npm.sap.com SAP external NPM repository (aka registry) into a subdirectory `node_modules/@sap/approuter`. 
  * [`package.json`](/samples/approuter/package.json)
  Declares version and package (`node_modules`) of the Application Router, that is a Node.JS application.
  * [`xs-app.json`](/samples/approuter/xs-app.json)  
  Configures the Application Router by defining the destinations and routes:

    ```json
    {
      "routes": [{
        "source": "^/",
        "target": "/products",
        "destination": "products-destination"
      }]
    }
    ```
**Note** the "products-destination" points to the product-list application. The destination URL is configured in the `manifest.yml`. 

### Step 4: Secure the Product List application using XSUAA client libraries
**Note** that the application router does not hide the backend microservices in any way. 
They are still directly accessible bypassing the application router. 
So, the backend microservices _must_ protect all their endpoints by validating the JWT token and implementing proper scope checks.

Three different implementation options are provided. For this exercise, choose one of the implementations.
 * Option 1: [Use the **Spring Boot** implementation of the Product List Sample](Spring.md)
 * Option 2: [Use the **Java** implementation (not using Spring) of the Product List Sample](Java.md)
 * Option 3: [Use the **Node.js** implementation of the Product List Sample](Node.js.md)


### Step 5: Deploy Approuter and Application to Cloud Foundry
* We use placeholder to simplify the personalisation of the [Cloud Foundry application descriptor](https://docs.cloudfoundry.org/devguide/deploy-apps/manifest.html), the `manifest.yml`.  
Adapt the variables `ID`, `LANDSCAPE_APPS_DOMAIN` and the others variables in the file [`/samples/vars.yml`](/samples/vars.yaml) according to the application chosen (SpringBoot, Java, NodeJs) by using an editor of your choice.

* Push the product-list together with the approuter application to your cloud foundry space:  
    ```
    D:
    cd D:\Files\Session\SEC364\cloud-cf-product-list-sample-teched2019\samples
    cf push --vars-file vars.yml
    ```

**Note** find further details in this [Exercise: Deploy the application to SAP Cloud Platform Cloud Foundry Environment](/exercises/04_push).

### Step 6: Cockpit administration task: Assign Role Collection to your User

Now let us see how to enable access to the application for the business users or end-users.
- Determine the URL of your approuter application by executing `cf apps` in the command prompt. The output lists the URL for the approuter which should have the following format: `approuter-<ID>.<LANDSCAPE_APPS_DOMAIN>`.
- Launch the approuter application in the browser by opening the determined URL, e.g.  `https://approuter-<ID>.cfapps.eu10.hana.ondemand.com/products`.
- Logon with your user credentials.
- If you selected option 1 (Spring Boot) in step 4, you will get an error with HTTP status code `403` ("unauthorized"´, "forbidden") which states that your user is valid and could be successfully authenticated but has no access to the applications `products` endpoint.
<br><br>
![Authorizations](/img/security_cockpit_0.png?raw=true)
<br><br>
- If you selected option 2 (Java) or option 3 (Node.js) in step 4, you will get an empty product list
<br><br>
![Authorizations](/img/security_cockpit_0b.png?raw=true)
<br><br>

In order to enable access, the end-users should be assigned the required authorizations.  
Therefore the Role Collection needs to be assigned to the user.
- In the cockpit, navigate to your trial `Subaccount`. Choose `Security` --> `Trust Configuration`.
- Click on the link **SAP ID Service** - the default trust configuration.
<br><br>
![Authorizations](/img/security_cockpit_8.png?raw=true)
<br><br>
- Now, in the `Role Collection Assignment` UI, enter your user id used to logon to the current account and click on button **Show Assignments**.  
It lists the current Role Collection assignment to the user and also allows to add new Role Collections to the user
- Click on button **Add Assignment**:
<br><br>
![Authorizations](/img/security_cockpit_9.png?raw=true)
<br><br>
- In the pop-up dialog, choose the Role Collection `ProductListViewer` you have defined as part of `xs-security.json` and click on button **Add Assignment**:
<br><br>
![Authorizations](/img/security_cockpit_10.png?raw=true)
<br><br>
- Now, the user should be able to access the application.

Further up-to-date information you can get on sap.help.com:
- [Maintain Role Collections](https://help.sap.com/viewer/65de2977205c403bbc107264b8eccf4b/Cloud/en-US/d5f1612d8230448bb6c02a7d9c8ac0d1.html)
- [Maintain Roles for Applications](https://help.sap.com/viewer/65de2977205c403bbc107264b8eccf4b/Cloud/en-US/7596a0bdab4649ac8a6f6721dc72db19.html).

### Step 7: Access the Application
According to the Role Collection(s) you've assigned to your user you should have read access to the product list endpoints.

You need to logon again to your application so that the authorities are assigned to your user's JWT. You can provoke a logon screen when clearing your cache.
Call again your application endpoints via the approuter Uri using the `Postman` Chrome plugin as explained [here](#testDeployedApp-1). You should now be authorized to create, read and delete advertisements.

- Launch the approuter application in the browser again and login with your credentials. In order to provoke a logon-screen you may need to delete the cache or alternatively start a new private (incognito) browser window. 
You should be able to see the product list.

:bulb: The logon URL is https://$identityzone.$uaaDomain. This can be identified from the xsuaa binding credentials (`cf env approuter` and look for `xsuaa.credentials.url`)

- Test the following endpoints:  
  * `https://product-list-<ID>.<LANDSCAPE_APPS_DOMAIN>/actuator/health` - GET request that is not secured and provides the information whether the product-list app is up and running.
  * `https://product-list-<ID>.<LANDSCAPE_APPS_DOMAIN>/products` - GET request that provides the list of products. It is secured and provides `401` (unauthenticated) in case no JWT access token is provided with `Authorization` header.
  * `https://approuter-<ID>.<LANDSCAPE_APPS_DOMAIN>/products/` - Points to the url of the AppRouter URI. With `/products` path the request is routed to the `index.html` of the product-list app. It should show you three products with details view.
  * `https://approuter-<ID>.<LANDSCAPE_APPS_DOMAIN>/products/products` - GET request that provides list of products (see `https://product-list-<ID>.<LANDSCAPE_APPS_DOMAIN>/products`).
  * `https://approuter-<ID>.<LANDSCAPE_APPS_DOMAIN>/products/productsByParam?name=Notebook Basic 2015` - GET request that provides list of products filtered by name.
  
- You can have a look into the logs with:
    ```
    cf logs product-list --recent
    ```

### Step 8: Clean-Up
Finally delete your application and your service instances using the following commands:
```
cf delete -f product-list
cf delete -f approuter
cf delete-service -f xsuaa
```

### Further Samples 
You can further sample applications here:
- https://github.com/SAP/cloud-security-xsuaa-integration/tree/master/samples
- https://github.com/SAP/cloud-application-security-sample

***
<dl>
  <dd>
  <div class="footer">&copy; 2019 SAP SE</div>
  </dd>
</dl>
<hr>
<a href="/exercises/02_clone/README.md">
  <img src="/img/arrow_left.png" height="80" border="10" align="left" alt="Previous Exercise" title="Previous Exercise: Clone application">
</a>


