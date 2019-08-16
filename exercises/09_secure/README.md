# Securing an Application with OAuth 2.0

## Estimated time

:clock4: 60 minutes

## Objective
In this exercise, you will learn how to secure the Product List application by using a flexible authorization framework - OAuth 2.0. The Authorization Code grant of OAuth 2.0 provides an excellent security mechanism to grant only authorized users access to your application and its data. The SAP XS Advanced Application Router, the SAP XS UAA OAuth authorization service and an application written using Spring Boot, Node.js or Java are outstanding tools to configure roles, assign them to users and, finally, implement role checks in your application.

# Exercise description
Microservices deployed on SAP Cloud Platform are freely accessible via the internet. To restrict access to authorized users only each microservice like the Product List application has to implement appropriate security mechanisms like [OAuth 2.0.](https://tools.ietf.org/html/rfc6749)

## Steps overview
The following steps are required to protect the Product List application with OAuth 2.O on the SAP Cloud Platform:

* Step 1: Definition of the Application Security Descriptor
* Step 2: Creation and configuration of the XS UAA service
* Step 3: Configuration of the Application Router
* Step 4: Secure the Product List application using XS UAA client libraries
* Step 5: Deployment of the Product List Application and Approuter
* Step 6: Cockpit administration task: Assign Role Collection to your User
* Step 7: Access the Application
* [Optional] Step 8: Role Assignment using a SAML Identity Provider
* Step 9: Clean up

### Step 1: Definition of the Application Security Descriptor

An Application Security Descriptor defines the details of the authentication methods and authorization types to use for accessing the Product List application. 
The Product List application uses this information to perform scope checks. With scopes a fine-grained user authorization can be build up. 
The container security library integrated in Spring, Node.js and Java Web applications allows to check scopes for each HTTP method on all HTTP endpoints. 
Scopes are carried by [JSON Web Tokens (JWTs)](https://tools.ietf.org/html/rfc7519) which in turn are issued by the [XS UAA Service](https://help.sap.com/viewer/4505d0bdaf4948449b7f7379d24d0f0d/1.0.12/en-US/17acf1ac0cf84487a3199c51b28feafd.html).

* Find in the `/samples` folder the file `xs-security.json`.

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

### Step 2: Creation and configuration of the XS UAA service

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
* Create the XS UAA service instance: 
    ```
    D:
    cd D:\Files\Session\SEC364\cloud-cf-product-list-sample-teched2019
    cf create-service xsuaa application xsuaa -c xs-security.json
    ```

### Step 3: Configuration of the Application Router

The [Application Router](https://github.com/SAP/cloud-cf-product-list-sample/blob/advanced/approuter/README.md) is used to provide a single entry point to a business application that consists of several different apps (microservices). It dispatches requests to backend microservices and acts as a reverse proxy. The rules that determine which request should be forwarded to which _destinations_ are called _routes_. The application router can be configured to authenticate the users and propagate the user information. Finally, the application router can serve static content.

* You can find all files that are required to install and configure the Application Router in the `/samples/approuter` folder.
  * `.npmrc`  
  With this the node modules are downloaded by the NPM package manager from the https://npm.sap.com SAP external NPM repository (aka registry) into a subdirectory `node_modules/@sap/approuter`. 
  * `package.json`  
  Declares version and package (`node_modules`) of the Application Router, that is a Node.JS application.
  * `xs-app.json`  
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


### Step 4: Secure the Product List application using XS UAA client libraries
**Note** that the application router does not hide the backend microservices in any way. 
They are still directly accessible bypassing the application router. 
So, the backend microservices _must_ protect all their endpoints by validating the JWT token and implementing proper scope checks.

Three different implementation options are provided. For this exercise, choose one of the implementations.
 * Option 1: [Use the Spring Boot implementation of the Product List Sample](Spring.md)
 * Option 2: [Use the Java implementation (not using Spring) of the Product List Sample](Java.md)
 * Option 3: [Use the Node.js implementation of the Product List Sample](Node.js.md)


### Step 5: Deploy Approuter and Application to Cloud Foundry
* We use placeholder to simplify the personalisation of the [Cloud Foundry application descriptor](https://docs.cloudfoundry.org/devguide/deploy-apps/manifest.html), the `manifest.yml`. 
Adapt the variables in file [`vars.yml`](/samples/vars.yaml) by using an editor of your choice.

```yml---
# some data to make the urls unique
YOUR_BIRTH_DATE: 00-00-00

# Choose cfapps.eu10.hana.ondemand.com for the EU10 landscape, cfapps.us10.hana.ondemand.com for US10
LANDSCAPE_APPS_DOMAIN: cfapps.eu10.hana.ondemand.com
#LANDSCAPE_APPS_DOMAIN: cfapps.us10.hana.ondemand.com
# Option 1: To use the Spring boot implementation of the product list sample, uncomment the lines below
PATH_PRODUCT_LIST_APP: spring/target/product-list.jar
MEMORY_PRODUCT_LIST_APP: 896M
BUILDPACK_PRODUCT_LIST_APP: sap_java_buildpack

# Option 2: To use the Java implementation of the product list sample, uncomment the lines below
#PATH_PRODUCT_LIST_APP: java/target/product-list.war
#MEMORY_PRODUCT_LIST_APP: 896M
#BUILDPACK_PRODUCT_LIST_APP: sap_java_buildpack

# Option 3: To use the Node.js implementation of the product list sample, uncomment the lines below
#PATH_PRODUCT_LIST_APP: nodejs
#MEMORY_PRODUCT_LIST_APP: 128M
#BUILDPACK_PRODUCT_LIST_APP: nodejs_buildpack
```

* [Push the product list application together with a approuter](/exercises/04_push/README.md) to your cloud foundry space: 
```
cf push --vars-file vars.yml
```

### Step 6: Cockpit administration task: Assign Role Collection to your User

**This step is mandatory for both master and advanced branch**

Now let us see how to enable access to the application for the business users or end-users.
- Determine the URL of your approuter application by executing `cf apps` in the command prompt. The output lists the URL for the approuter which should have the following format: `approuter-<YOUR_BIRTH_DATE>.<LANDSCAPE_APPS_DOMAIN>`
- Launch the approuter application in the browser by opening the determined URL
- Logon with your user credentials
- If you selected option 1 (Spring) in step 3, you will get the error "Insufficient scope for this resource"
<br><br>
![Authorizations](/img/security_cockpit_0.png?raw=true)
<br><br>
- If you selected option 2 (Java) or option 3 (node.js) in step 3, you will get an empty product list
<br><br>
![Authorizations](/img/security_cockpit_0b.png?raw=true)
<br><br>

In order to enable access, the end-users should be assigned the required authorizations. 
Therefore the Role Collection needs to be assigned to the user.
- Navigate to the `Subaccount --> Trust Configuration` [expand the security group to see this entry]
- Click on the link **SAP ID Service** - the default trust configuration
<br><br>
![Authorizations](/img/security_cockpit_8.png?raw=true)
<br><br>
- Note: The logon URL is https://$identityzone.$uaaDomain. This can be identified from the xsuaa binding credentials (`cf env approuter` and look for `xsuaa.credentials.url`)
- Now, in the `Role Collection Assignment' UI, enter your user id used to logon to the current account and click on button **Show Assignments**
- It lists the current Role Collection assignment to the user and also allows to add new Role Collections to the user
- Click on button **Add Assignment**
<br><br>
![Authorizations](/img/security_cockpit_9.png?raw=true)
<br><br>
- In the pop-up dialog, choose the Role Collection `ProductListViewer` you have defined as part of `xs-security.json` and click on button **Add Assignment**
<br><br>
![Authorizations](/img/security_cockpit_10.png?raw=true)
<br><br>
- Now, the user should be able to access the application.
- Launch the application on the browser and login with your credentials. You should be able to see the product list

### Step 7: Access the Application

After deployment, the AppRouter will trigger authentication automatically when you access one of the following URLs:

* `https://approuter-<ID>.<LANDSCAPE_APPS_DOMAIN>/products/sayHello` - GET request that provides XSUAA user token details, but only if token matches.
* `https://approuter-<ID>.<LANDSCAPE_APPS_DOMAIN>/v1/method` - GET request executes a method secured with Spring Global Method Security.
* `https://approuter-<ID>.<LANDSCAPE_APPS_DOMAIN>/v1/getAdminData` - GET request to read sensitive data via Global Method Security. You will get a `403` (UNAUTHORIZED), in case you do not have `Admin` scope.
* `https://approuter-<ID>.<LANDSCAPE_APPS_DOMAIN>/v2/sayHello` - GET request that logs generic Jwt info, but only if token matches. 

Have a look into the logs with:
```
cf logs product-list --recent
```

> Note: https://spring-security-xsuaa-usage-web-<ID>.<LANDSCAPE_APPS_DOMAIN> points to the url of the AppRouter. Get all app routes with `cf apps`.


### [Optional] Step 8: Role Assignment using a SAML Identity Provider
Besides SAP ID Service, the Cloud Foundry environment also supports custom SAML Identity Providers. Once an SAML2 Identity Provider has been added, role collections can be assigned to the corresponding users.
- Configure an SAML IdP as described in [SAML Configuration](./SAML.md)

**Note for Teched participants:**: Contact the instructors for the configuration in the shared Identity Provider

- Assign a role collection to an IdP user in the same way as for an SAP ID Service user

### Step 9: Clean-Up
Finally delete your application and your service instances using the following commands:
```
cf delete -f product-list
cf delete -f approuter
cf delete-service -f xsuaa
```

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


