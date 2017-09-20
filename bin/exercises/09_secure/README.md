# Exercise 09: Security

## Estimated time

:clock4: xx minutes

## Objective

//TODO In this exercise you'll learn how you can...

# Exercise description

//TODO Overview of the exercise goal and steps

:bulb: [Security Overview in Cloud Curriculum](https://github.wdf.sap.corp/cc-java-dev/cc-coursematerial/tree/master/Security)
:bulb: [Blog explaining the whole security setup](https://blogs.sap.com/2017/07/18/step-7-with-sap-s4hana-cloud-sdk-secure-your-application-on-sap-cloud-platform-cloudfoundry/)

## Steps Overview
* Securing the product-list application
* Configure the OAuth 2.o Client Credentials Grant (service to service communication)
* Configure the OAuth 2.o Authorization Code Grant (human to service communication)

## Securing the product-list application
* Eclipse: clone the product-list from master branch (Standard Solution without any advanced add-ons)
* Eclipse: build the app with Maven install
 * All unit tests are passing
* Eclipse: run app as Spring Boot App
 * Browser:localhost:8080 show that the product-list is loading
* Eclipse:pom.xml add

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

```
* Eclipse: update maven project
* Eclipse: Run As: Spring Boot App
* Browser:localhost8080: window popping up that authentication is required
* Eclipse: Run As: Maven install
 * BUILD FAILURE as unit test are failing due to missing authentication: Status expected:<200> but was:<401>
* Eclipse: ControllerTest.java // Disable need to authenticate for tests
 * Add `@AutoConfigureMockMvc(secure = false)` to `ControllerTests` class.
* Eclipse: Run As: Maven install
 * Result: BUILD SUCCESS
* Disable all security checks again to have a runnig version
* Eclipse:File:New:Class:com.sap.cp.cf.demoapps.ConfigSecurity.java

```java
package com.sap.cp.cf.demoapps;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;

@Configuration
@EnableWebSecurity
@EnableResourceServer
public class ConfigSecurity extends ResourceServerConfigurerAdapter {

	// configure Spring Security, demand authentication and specific scopes
	@Override
	public void configure(final HttpSecurity http) throws Exception {

		// @formatter:off
		http.authorizeRequests()
            .anyRequest().permitAll();
		// @formatter:on
	}
}
```
* Eclipse: Run As: Spring Boot App
* Browser:localhost8080
 * App works as expected

## Deny request to all endpoints except the health enptoint
* Replace

```java
            .anyRequest().permitAll();
```
* with

```java
            .antMatchers(GET, "/health").permitAll()
            .anyRequest().denyAll(); // deny anything not configured above
````
* Add import `import static org.springframework.http.HttpMethod.GET;`


## Adding OAuth scopes to allow read access to the product-list resource
* Explain what OAuth, Tokens and Scopes are
* Explain the XSUAA and how it supports OAuth
* Eclipse:src/main/security: Create xs-security.json file

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
	]
}
```

* Create the XSUAA Service
    * show marketpalce: `product-list$ cf m`
    * Terminal: cf create-service xsuaa application xsuaa -c ./src/main/security/xs-security.json
    * Add the xsuaa service to the manifest.yml under services: `- xsuaa``

### Installation of SAP XS Security Libraries for offline token validation
* The newest version of the SAP XS Security Libraries can be downloaded from the [Service Marketplace](https://launchpad.support.sap.com/#/softwarecenter/template/products/%20_APP=00200682500000001943&_EVENT=DISPHIER&HEADER=Y&FUNCTIONBAR=N&EVENT=TREE&NE=NAVIGATE&ENR=73555000100200004333&V=MAINT&TA=ACTUAL&PAGE=SEARCH/XS%20JAVA%201)
* At the time of writing the latest package is XS_JAVA_4-70001362.ZIP.
* This version of the SAP XS Security Libraries is also stored in product-list/libs
* Unzip product-list/libs/XS_JAVA_4-70001362.ZI
* Install XS Security Libs to your local maven repo using:

```shell
cd libs
mvn clean install
```

* Add the following dependencies to the pom.xml with

```xml
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

* Eclipse:Maven:Update Project...

### Add Scope check and offline token validation to the Security Configuration
* Eclipse:ConfigSecurity.java
* Replace the content of `ConfigSecurity` class with

```java
	@Value("${vcap.services.xsuaa.credentials.xsappname:product-list}")
	private String xsAppName;

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
```

* Elcipse:Run As: Maven install
* Push the application: cf push
* Now the application is OAuth ready

## OAuth 2.o Client Credentials Grant
* Explain the Client Credentials Flow
* [RFC 6749](https://tools.ietf.org/html/rfc6749#section-4.4): The client can request an access token using only its client credentials ... when the client is requesting access to the protected resources ... of another resource owner that have been previously arranged with the authorization server.
* Translated: service to service communication
### Setup the product-list app
* For the client credentials flow to run we need to configure two things:
* First - we allow other applications to access the product-list app
 * Eclipse: manifest.yml
   * Add to `env`: `SAP_JWT_TRUST_ACL: "[{\"clientid\":\"*\",\"identityzone\":\"*\"}]"`
 * In order to avoid pushing the whole application again we could also directly add the environment variable
   * `cf set-env product-list SAP_JWT_TRUST_ACL "[{\"clientid\":\"*\",\"identityzone\":\"*\"}]"`
 * Comment: in production the asterisks must be replaced by real ids
* Second - we need to grant scopes to another application

 * In the xs-security.json scopes can be granted as authorities to apps by specifying the tenant and application name e.g:

```json
{
	"xsappname": "product-list",
	"tenant-mode": "shared",
	"scopes":
	[
		{
			"name": "$XSAPPNAME.read",
			"description": With this scope, all endpoints of the product-list app can be read.",
			"grant-as-authority-to-apps":
			[
				"$XSAPPNAME(application,<tenant-id>,<appName>)"
			]
		}
	]
}
```

 * Eclipse: Update xs-security.json with the tenant-id of the trail account and test-client as appName
 * cf update-service xsuaa -c ./src/main/security/xs-security.json

### Setup the test-client
* The calling app needs to accept granted authorities by stating this in their xs-security.json file e.g:
```json
{
	"xsappname": "test-client",
	"tenant-mode": "shared",
	"authorities":
	[
		"$ACCEPT_GRANTED_AUTHORITIES"
	]
}
```

* Create the test-client by creating a new xsuaa instance: `cf create-service xsuaa application calling-app -c ./src/main/security/test-client-xs-security.json`
* Generate service key: `cf create-service-key test-client test-client-key`
* Show generated service key: cf service-key test-client test-client-key

### Test the Client Credentials Grant
* Postman: Get the JWT in order to access the product-list application
 * Postman: Add the following code und Tests to capture the JWT
```javascript
tests["Status code is 200"] = responseCode.code === 200;

var jsonData = JSON.parse(responseBody);
postman.setGlobalVariable("accessToken", "Bearer " + jsonData.access_token);
```
 * Postman: In the Authorization tab select Basic Auth and enter: Username: \<clientid> Password: \<clientsecret> - Press Update Request
 * Postman: Body: Select x-www-form-urlencoded
  * Key: client_id Value: \<clientsecret>
  * Key: garnt_type Value: client_credentials
 * Use Base64 to encode \<clientid\>:\<clientsecret\> in the authorization header but not in the body
```curl
curl -X POST \
  https://d046826trial.authentication.us20.hana.ondemand.com/oauth/token \
  -H 'authorization: Basic <clientid>:<clientsecret>' \
  -H 'content-type: application/x-www-form-urlencoded' \
  -d 'client_id=<clientid>&grant_type=client_credentials'
```
* Postman: Show the JWT and the granted scopes
 * Result no additional scopes assigned
* Create and bind a xsuaa service with `cf cs xsuaa application xsuaa-products -c ./src/main/security/xs-security.json`
* OR update the service with `cf update-service xsuaa-products -c ./src/main/security/xs-security.json` (no restage necessary)
* Postman: Show the JWT again and show the additional granted scopes

* Postman: Call the product-list application with the clientid and clientsecret from the displayed service key
 * Postman: Under Headers add Key: Authorization Value: {{accessToken}} - this does the whole magic
```curl
curl -X GET \
  https://products-d046826.cfapps.us20.hana.ondemand.com/products \
  -H 'authorization: Bearer <JWT>'
```

## OAuth 2.o Authorization Code Grant
* Add role-templates to xs-security.json
```json
{
	"xsappname": "product-list",
	"tenant-mode": "shared",
	"scopes":
	[
		{
			"name": "$XSAPPNAME.read",
			"description": "With this scope, all endpoints of the product-list app can be read.",
			"grant-as-authority-to-apps":
			[
				"$XSAPPNAME(application,604685de-3081-485f-bcdb-a05d8ad6a139,calling-app)"
			]
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

* cf update-service xsuaa -c ./src/main/security/xs-security.json

* Authenticate a user by using the approuter
* [Approuter documentation](https://github.infra.hana.ondemand.com/TechEd2017/product-list/blob/advanced/src/main/approuter/README.md) and [Approuter in cloud curriculum](https://github.wdf.sap.corp/cc-java-dev/cc-coursematerial/blob/master/Security/Exercise_22_DeployApplicationRouter.md)
* Specify the approuter application and dependencies
    * Eclipse:File:New:Foler: `product-list/src/main/approuter`
    * Eclipse:src/main/approuter:File:New:File: `package.json`

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
* Download and install approuter in `product-list/src/main/approuter`

```shell
    approuter$ npm config set @sap:registry https://npm.sap.com
    approuter$ npm install @sap/approuter
```
* Configure the approuter
    * Define the destinations and routes: Eclipse:src/main/approuter:File:New:File: `xs-app.json`

```json
{
  "routes": [{
    "source": "^/",
    "target": "/",
    "destination": "products-destination"
  }]
}
```

* Add the approuter to the `manifest.yml`

```yml
---
applications:
# Application
- name: product-list
  instances: 1
  memory: 896M
  host: product-list-d000000
  path: target/product-list.jar
  buildpack: https://github.com/cloudfoundry/java-buildpack.git#v4.3
  env:
    SAP_JWT_TRUST_ACL: "[{\"clientid\":\"*\",\"identityzone\":\"*\"}]"
  services:
    - postgres
    - logs
    - xsuaa
# Application Router
- name: approuter
  host: approuter-d000000
  path: src/main/approuter
  buildpack: nodejs_buildpack
  memory: 128M
  env:
    TENANT_HOST_PATTERN: "^(.*)-approuter-d000000.cfapps.us20.hana.ondemand.com"
    destinations: >
      [
        {"name":"products-destination",
         "url":"https://product-list-d000000.cfapps.us20.hana.ondemand.com",
         "forwardAuthToken": true}
      ]
  services:
    - xsuaa
...
```

*Terminal:Ddeploy the approuter: `product-list$ cf push approuter`

* A new route has to be configured for the approuter according to the following schema: `<tenant-domain>-<approuter-host-name>.cfapps.<cf-domain>` e.g. `d046826trial-approuter-d000000.cfapps.us20.hana.ondemand.com`
* The tenant domain can either be found in the cockpit or in the environment variables:
    * `cf env approuter` : `System-Provided:{"VCAP_SERVICES": {"xsuaa": [{"credentials": {"identityzone": "d046826trial" ...`
    * `cf map-route approuter cfapps.us20.hana.ondemand.com -n d046826trial-approuter-d000000`
* When calling the `https://d046826trial-approuter-d000000.cfapps.us20.hana.ondemand.com` the user has to authenticate himself with his trial account credentials.

## Trust configuration
TODO Padma: Show how to configure trust to the product-list by using the UserRoleTemplate, [like it is done in this example](https://blogs.sap.com/2017/07/18/step-7-with-sap-s4hana-cloud-sdk-secure-your-application-on-sap-cloud-platform-cloudfoundry/).
