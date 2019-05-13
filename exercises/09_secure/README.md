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
* Step 3: Adding required security libraries
* Step 4: Configuration of the Spring Security framework
* Step 5: Adding the XS Advanced Application Router
* Step 6: Configuration of trust

:warning: If not yet done, please [clone](https://github.com/SAP/cloud-cf-product-list-sample/tree/master/exercises/11_clonebranch) the advanced version of the application.

### Step 1: Definition of the Application Security Descriptor

**This step is mandatory only if you work on the master branch. For the advanced branch you can go through it to understand what is happening (no need to change anything)**

An Application Security Descriptor defines the details of the authentication methods and authorization types to use for accessing the Product List application. The Product List application uses this information to perform scope checks. With scopes a fine-grained user authorization can be build up. The container security library integrated in Spring, Node.js and Java Web applications allows to check scopes for each HTTP method on all HTTP endpoints. Scopes are carried by [JSON Web Tokens (JWTs)](https://tools.ietf.org/html/rfc7519) which in turn are issued by the [XS UAA Service](https://help.sap.com/viewer/4505d0bdaf4948449b7f7379d24d0f0d/1.0.12/en-US/17acf1ac0cf84487a3199c51b28feafd.html).

* Create the file `xs-security.json` in the root directory of this project.
* Paste the following JSON content

```json
{
	"xsappname": "product-list",
	"tenant-mode": "dedicated",
	"scopes":
	[
		{
			"name": "$XSAPPNAME.read",
			"description": "With this scope, all endpoints of the product-list app can be read."
		}
	],

	"role-templates":
	[
		{
			"name": "UserRoleTemplate",
			"description": "Role to call the product-list service",
			"scope-references":
			[
				"$XSAPPNAME.read"
			]
		}
	]
}
```

### Step 2: Creation and configuration of the XS UAA service

**Mandatory for both master and advanced branch**

To grant users access to the Product List application, an instance of the XSUAA service for this application must be created; the XSUAA service instance acts as an OAuth 2.0 client for the bound application.
* You need to tell the CF CLI which Cloud Foundry you will use. To do this you have to set the API endpoint to the Cloud Controller of the Cloud Foundry region where you created your Cloud Foundry trial. Open a command prompt, navigate to the folder ```cloud-cf-product-list-sample-advanced``` in the student directory and use the command  ```cf api CLOUD_FOUNDRY_API_ENDPOINT```.
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
cd D:\Files\Session\SEC366\cloud-cf-product-list-sample-advanced
cf create-service xsuaa application xsuaa -c xs-security.json
```
* (**only for master branch**) Add the XS UAA service instance under services to the `manifest.yml`:
```
applications:
  ...
  services:
    - xsuaa
```

### Step 3: Configure your Product List Service
In the advanced branch, three different implementation options are provided. For this exercise, choose one of the implementations.
 * Option 1: [Use the Spring Boot implementation of the Product List Sample](Spring.md)
 * Option 2: [Use the Java implementation (not using Spring) of the Product List Sample](Java.md)
 * Option 3: [Use the Node.js implementation of the Product List Sample](Node.js.md)
 
### Step 4: Adding the XS Advanced Application Router

**This step is mandatory for both the master and the advanced branch.**

The [XS Advanced Application Router](https://github.com/SAP/cloud-cf-product-list-sample/blob/advanced/approuter/README.md) is used to provide a single entry point to a business application that consists of several different apps (microservices). It dispatches requests to backend microservices and acts as a reverse proxy. The rules that determine which request should be forwarded to which _destinations_ are called _routes_. The application router can be configured to authenticate the users and propagate the user information. Finally, the application router can serve static content.

**Note** that the application router does not hide the backend microservices in any way. They are still directly accessible bypassing the application router. So, the backend microservices _must_ protect all their endpoints by validating the JWT token and implementing proper scope checks.

* Only for master branch: Download and install the application router in `approuter`
  * Create a new folder: `mkdir approuter`
  * Change directory to: `cd approuter`
  * Create a new file to specify the version of the application router: `notepad package.json`

```json
{
    "name": "approuter",
    "dependencies": {
        "@sap/approuter": "^5.6.4"
    },
    "scripts": {
        "start": "node node_modules/@sap/approuter/approuter.js"
    }
}
```

*  Run `npm install`
:bulb: For the advanced branch only this command has to be executed from `approuter`

```shell
    approuter$ npm config set @sap:registry https://npm.sap.com
    approuter$ npm install @sap/approuter
```

* Only for master branch: Configure the application router by defining the destinations and routes: `vi src/main/approuter/xs-app.json`

```json
{
  "routes": [{
    "source": "^/",
    "target": "/",
    "destination": "products-destination"
  }]
}
```

* Only for master branch: Add the application router to the `manifest.yml`

```yml---
applications:
# Application
- name: product-list
  instances: 1
  memory: ((MEMORY_PRODUCT_LIST_APP))
  routes:
    - route: product-list-((YOUR_BIRTH_DATE)).((LANDSCAPE_APPS_DOMAIN))
  path: ((PATH_PRODUCT_LIST_APP))
  buildpacks:
    - ((BUILDPACK_PRODUCT_LIST_APP))
  services:
    - xsuaa

# Application Router
- name: approuter
  routes:
    - route: approuter-((YOUR_BIRTH_DATE)).((LANDSCAPE_APPS_DOMAIN))
  path: approuter
  buildpacks:
    - nodejs_buildpack
  memory: 128M
  services:
    - xsuaa
  env:
    destinations: >
      [
        {"name":"products-destination",
         "url":"https://product-list-((YOUR_BIRTH_DATE)).((LANDSCAPE_APPS_DOMAIN))",
         "forwardAuthToken": true}
      ]
```
* The advanced branch uses variable replacement to simplify editing the manifest. Adapt the variables in file ```vars.yml``` within directory ```cloud-cf-product-list-sample-advanced``` to your example and landscape by using an editor of your choice.
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

* [Push the product list application together with a approuter](https://github.com/SAP/cloud-cf-product-list-sample/tree/master/exercises/04_push) to your cloud foundry space: `cf push --vars-file vars.yml`

### Step 5: Role Assignment

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

In order to enable access, the end-users should be assigned the required authorizations. The authorizations of the application is registered with the authorization services, xsuaa, using the security.json. You can view these authorizations for the application in the SAP Cloud Cockpit.
- Navigate to the `Subaccount --> Space --> Applications --> approuter` [this is the front end application]
- Expand the `Security` group and navigate to `Roles` UI
<br><br>
![Authorizations](/img/security_cockpit_1.png?raw=true)
<br><br>
- The UI lists the roles defined by the application
<br><br>
![Authorizations](/img/security_cockpit_2.png?raw=true)
<br><br>
- In order to provide access to the end-users, the above role has to be assigned to the end-user. Roles can't be directly assigned to the end-users, you will have to create RoleCollection and add the required Roles to the RoleCollection.
- Navigate to the `Subaccount --> Security --> RoleCollections` [expand the security group to see this entry]
<br><br>
![Authorizations](/img/security_cockpit_3.png?raw=true)
<br><br>
- Click on button **New Role Collection**
<br><br>
![Authorizations](/img/security_cockpit_4.png?raw=true)
<br><br>
- Enter the name and description for the RoleCollection and click on button **Save**
<br><br>
![Authorizations](/img/security_cockpit_5.png?raw=true)
<br><br>
- Navigate into the RoleCollection and click on button **Add Role**
<br><br>
![Authorizations](/img/security_cockpit_6.png?raw=true)
<br><br>
- In the pop-up dialog, choose the sample application, the role template and the role. Click on button **Save**
<br><br>
![Authorizations](/img/security_cockpit_7.png?raw=true)
<br><br>
- As a next step, assign this RoleCollection to the user. Navigate to the `Subaccount --> Trust Configuration` [expand the security group to see this entry]
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
- In the pop-up dialog, choose the Role Collection you have defined recently and click on button **Add Assignment**
<br><br>
![Authorizations](/img/security_cockpit_10.png?raw=true)
<br><br>
- Now, the user should be able to access the application
- Launch the application on the browser and login with your credentials. You should be able to see the product list
### (optional) Step 6: Role Assignment using a SAML Identity Provider
Besides SAP ID Service, the Cloud Foundry environment also supports custom SAML Identity Providers. Once an SAML2 Identity Provider has been added, role collections can be assigned to the corresponding users.
- Configure an SAML IdP as described in [SAML Configuration](./SAML.md)

**Note for Teched participants:**: Contact the instructors for the configuration in the shared Identity Provider

- Assign a role collection to an IdP user in the same way as for an SAP ID Service user

