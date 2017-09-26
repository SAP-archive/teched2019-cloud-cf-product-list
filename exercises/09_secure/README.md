# Securing an Application with OAuth 2.0

## Estimated time

:clock4: 40 minutes

## Objective
In this exercise, you will learn how to secure the Product List application by using a flexible authorization framework - OAuth 2.0. The Authorization Code grant of OAuth 2.0 provides an excellent security mechanism to grant only authorized users access to your application and its data. The SAP XS Advanced Application Router, the SAP XS UAA OAuth authorization service and Spring Boot are outstanding tools to configure roles, assign them to users and, finally, implement role checks in your application.

# Exercise description
Microservices deployed on SAP Cloud Platform are freely accessible via the internat. To restrict access to authorized users only each microservice like the Product List application has to implement appropriate security mechanisms like [OAuth 2.0.](https://tools.ietf.org/html/rfc6749)

## Steps overview
The following steps are required to protect the Product List application with OAuth 2.O on the SAP Cloud Platform:

* Step 1: Definition of the Application Security Descriptor
* Step 2: Creation and configuration of the XS UAA service
* Step 3: Adding required security libraries
* Step 4: Configuration of the Spring Security framework
* Step 5: Adding the XS Advanced Application Router
* Step 6: Configuration of trust

## Protecting the Product List application
* If not yet done, [clone](https://github.infra.hana.ondemand.com/TechEd2017/product-list/tree/master/exercises/02_clone) the basic version of the application that was developed during the basic hands-on session.
* Build the project in Eclipse (`Context Menu -> Run As -> Maven install`)
* Verify that all unit tests are passing
* Run the project locally as Spring Boot App (`Context Menu -> Run As -> Spring Boot App`)
* Call `localhost:8080` from your browser -> the Product List UI shows up

### Step 1: Definition of the Application Security Descriptor
An Application Security Descriptor defines the details of the authentication methods and authorization types to use for accessing the Product List application. The Product List application uses this information to perform scope checks. With scopes a fine-grained user authorization can be build up. Spring Security allows to check scopes for each HTTP method on all HTTP endpoints. Scopes are carried by [JSON Web Tokens (JWTs)](https://tools.ietf.org/html/rfc7519) which in turn are issued by the [XS UAA Service](https://help.sap.com/viewer/4505d0bdaf4948449b7f7379d24d0f0d/1.0.12/en-US/17acf1ac0cf84487a3199c51b28feafd.html).

* Create the file `xs-security.json` in `src/main/security/`.
* Paste the following JSON content

```json
{
	"xsappname": "product-list",
	"tenant-mode": "shared",
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
To grant users access to the Product List application, an instance of the XS UAA service for this application must be created; the XSUAA service instance acts as an OAuth 2.0 client for the bound application.

* Show the marketplace:  `cf m`
* Create the XS UAA service instance: `cf create-service xsuaa application xsuaa -c ./src/main/security/xs-security.json`
* Add the XS UAA service instance under services to the `manifest.yml`: `- xsuaa`

### Step 3: Adding required security libraries

To secure the application we have to add Spring Security to the classpath. By configuring Spring Security in the application, Spring Boot automatically secures all HTTP endpoints with BASIC authentication. Since we want to use OAuth 2.0 together with [Java Web Tokens (JWT)](https://tools.ietf.org/html/rfc7519) instead, we need to add the Spring OAUTH and Spring JWT dependencies as well.

To enable offline JWT validation the SAP XS Security Libraries need to be added as well. The latest version can be downloaded from the [Service Marketplace](https://launchpad.support.sap.com/#/softwarecenter/template/products/%20_APP=00200682500000001943&_EVENT=DISPHIER&HEADER=Y&FUNCTIONBAR=N&EVENT=TREE&NE=NAVIGATE&ENR=73555000100200004333&V=MAINT&TA=ACTUAL&PAGE=SEARCH/XS%20JAVA%201). At the time of writing the latest version is `XS_JAVA_4-70001362`. This version is also stored in `product-list/libs`.

* Unzip `product-list/libs/XS_JAVA_4-70001362.ZIP`
* Install SAP XS Security Libraries to your local maven repo by executing:

```shell
cd product-list/libs
mvn clean install
```
* Add the following dependencies to the `pom.xml` file: 

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-security</artifactId>
</dependency>
<dependency>
    <groupId>org.springframework.security.oauth</groupId>
    <artifactId>spring-security-oauth2</artifactId>
</dependency>
<dependency>
    <groupId>org.springframework.security</groupId>
    <artifactId>spring-security-jwt</artifactId>
</dependency>
<dependency>
    <groupId>com.sap.xs2.security</groupId>
    <artifactId>security-commons</artifactId>
    <version>0.22.2</version>
</dependency>
<dependency>
    <groupId>com.sap.xs2.security</groupId>
    <artifactId>java-container-security</artifactId>
    <version>0.22.2</version>
</dependency>
<dependency>
    <groupId>com.unboundid.components</groupId>
    <artifactId>json</artifactId>
    <version>1.0.0</version>
</dependency>
<dependency>
    <groupId>com.sap.security.nw.sso.linuxx86_64.opt</groupId>
    <artifactId>sapjwt.linuxx86_64</artifactId>
    <version>1.0.0</version>
</dependency>
<dependency>
    <groupId>com.sap.security.nw.sso.ntamd64.opt</groupId>
    <artifactId>sapjwt.ntamd64</artifactId>
    <version>1.0.0</version>
</dependency>
<dependency>
    <groupId>com.sap.security.nw.sso.linuxppc64.opt</groupId>
    <artifactId>sapjwt.linuxppc64</artifactId>
    <version>1.0.0</version>
</dependency>
<dependency>
    <groupId>com.sap.security.nw.sso.darwinintel64.opt</groupId>
    <artifactId>sapjwt.darwinintel64</artifactId>
    <version>1.0.0</version>
</dependency>
```

* Update the Eclipse project to put the new libraries to the classpath (`Context Menu -> Maven -> Update Project...`)
* Stop the running application
* Build the project in Eclipse (`Context Menu -> Run As -> Maven install`)

The build will fail as some unit tests are failing due to missing authentication: `Status expected:<200> but was:<401>`.

To disable authentication for the unit tests we need to enhance the `ControllerTest` class.

* Add `@AutoConfigureMockMvc(secure = false)` to `ControllerTests` class
* Build the project in Eclipse (`Context Menu -> Run As -> Maven install`) -> Result: BUILD SUCCESS
* Run the project as Spring Boot App (`Context Menu -> Run As -> Spring Boot App`)
* Call `localhost:8080` from your browser -> a window is popping up informing us that authentication is required

All HTTP endpoints are secured and the Product List application is inaccessible. To regain access, we need to configure the Spring Security.

### Step 4: Configuration of the Spring Security framework

* Create a new class `com.sap.cp.cf.demoapps.ConfigSecurity.java`
* Add the following scope checks and offline token validation to the Security Configuration

```java
package com.sap.cp.cf.demoapps;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;

import static org.springframework.http.HttpMethod.GET;

@Configuration
@EnableWebSecurity
@EnableResourceServer
public class ConfigSecurity extends ResourceServerConfigurerAdapter {

  @Value("${vcap.services.xsuaa.credentials.xsappname:product-list}")
	private String xsAppName;

	// configure Spring Security, demand authentication and specific scopes
	@Override
	public void configure(final HttpSecurity http) throws Exception {

		// @formatter:off
    http.sessionManagement()
				.sessionCreationPolicy(SessionCreationPolicy.NEVER).and()
				.authorizeRequests()
				.antMatchers(GET, "/health").permitAll()
				.antMatchers(GET, "/**").access(String.format("#oauth2.hasScope('%s.%s')", xsAppName, "read"))
				.anyRequest().denyAll(); // deny anything not configured above
		// @formatter:on
	}

  // offline token validation
	@Bean
	protected static SAPOfflineTokenServicesCloud offlineTokenServicesBean() {
		return new SAPOfflineTokenServicesCloud();
	}
}
```

Now all endpoints are blocked except the health endpoint.

* Call `localhost:8080` from your browser -> the access to the Product List UI is blocked
* Call `localhost:8080/health` from your browser -> the health status of the Product List application is displayed
* Build the project in Eclipse (`Context Menu -> Run As -> Maven install`) -> Result: BUILD SUCCESS
* Push the application: `cf push`

### Step 5: Adding the XS Advanced Application Router
The [XS Advanced Application Router](https://github.infra.hana.ondemand.com/TechEd2017/product-list/blob/advanced/src/main/approuter/README.md) is used to provide a single entry point to a business application that consists of several different apps (microservices). It dispatches requests to backend microservices and acts as a reverse proxy. The rules that determine which request should be forwarded to which _destinations_ are called _routes_. The application router can be configured to authenticate the users and propagate the user information. Fianlly, the application router can serve static content.

**Note** that the application router does not hide the backend microservices in any way. They are still directly accessible bypassing the application router. So, the backend microservices _must_ protect all their endpoints by validating the JWT token and implementing proper scope checks.

* Download and install the application router in `product-list/src/main/approuter`
  * Create a new folder: `mkdir product-list/src/main/approuter`
  * Change directory to: `cd product-list/src/main/approuter`
  * Create a new file to specify the version of the application router: `vi package.json`

```json
{
    "name": "approuter",
    "dependencies": {
        "@sap/approuter": "^2.9.1"
    },
    "scripts": {
        "start": "node node_modules/@sap/approuter/approuter.js"
    }
}
```

*  Run `npm install`

```shell
    approuter$ npm config set @sap:registry https://npm.sap.com
    approuter$ npm install @sap/approuter
```

* Configure the application router by defining the destinations and routes: `vi src/main/approuter/xs-app.json`

```json
{
  "routes": [{
    "source": "^/",
    "target": "/",
    "destination": "products-destination"
  }]
}
```

* Add the application router to the `manifest.yml`

```yml
---
applications:
# Application
- name: product-list
  instances: 1
  memory: 896M
  host: product-list-YOUR_BIRTH_DATE
  path: target/product-list.jar
  buildpack: https://github.com/cloudfoundry/java-buildpack.git#v4.3
  services:
    - xsuaa
  env:
    SAP_JWT_TRUST_ACL: '[{"clientid" : "*", "identityzone" : "*"}]'
# Application Router
- name: approuter
  host: approuter-YOUR_BIRTH_DATE
  path: src/main/approuter
  buildpack: nodejs_buildpack
  memory: 128M
  env:
    destinations: >
      [
        {"name":"products-destination",
         "url":"https://product-list-YOUR_BIRTH_DATE.cfapps.eu10.hana.ondemand.com",
         "forwardAuthToken": true}
      ]
  services:
    - xsuaa
```

* Deploy the application router: `product-list$ cf push approuter`

### Step 6: Trust configuration
Now let us see how to enable access to the application for the business users or end-users.
- Launch the above application in the browser and logon with your user credentials
- You will get an error, Insufficient scope for this resource
<br><br>
![Authorizations](/img/security_cockpit_0.png?raw=true)
<br><br>
In order to enable access, the end-users should be assigned the required authorizations. The authorizations of the application is registered with the authorization services, xsuaa, using the security.json. You can view these authorizations for the application in the Cockpit.
- Navigate to the `Org --> Space --> Applications --> approuter` [this is the front end application]
- Expand the `Security` group and navigate to `Roles` UI
<br><br>
![Authorizations](/img/security_cockpit_1.png?raw=true)
<br><br>
- The UI lists the roles defined by the application
<br><br>
![Authorizations](/img/security_cockpit_2.png?raw=true)
<br><br>
- In order to provide access to the end-users, the above role has to be assigned to the end-user. Roles can't be directly assigned to the end-users, you will have to create RoleCollection and add the required Roles to the RoleCollection.
- Navigate to the `Subaccount --> RoleCollections` [expand the security group to see this entry]
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
- Before assigning the RoleCollection to the end-user, the user should have logged on to SAP ID Service at least once
- The logon URL is https://<sub-domain>.<uaadomain> . This can be identified from the xsuaa binding credentials
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
