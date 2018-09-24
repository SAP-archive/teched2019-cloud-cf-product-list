## Step 1: Adding required security libraries

**This step is mandatory only if you work on the master branch. For the advanced branch you can go through it to understand what is happening (no need to change anything)**

To secure the application we have to add Spring Security to the classpath. By configuring Spring Security in the application, Spring Boot automatically secures all HTTP endpoints with BASIC authentication. Since we want to use OAuth 2.0 together with [Java Web Tokens (JWT)](https://tools.ietf.org/html/rfc7519) instead, we need to add the Spring OAUTH and Spring JWT dependencies as well.

To enable offline JWT validation the SAP XS Security Libraries need to be added as well. The latest version can be downloaded from the [Service Marketplace](https://launchpad.support.sap.com/#/softwarecenter/template/products/%20_APP=00200682500000001943&_EVENT=DISPHIER&HEADER=Y&FUNCTIONBAR=N&EVENT=TREE&NE=NAVIGATE&ENR=73555000100200004333&V=MAINT&TA=ACTUAL&PAGE=SEARCH/XS%20JAVA%201). At the time of writing the latest version is `XS_JAVAP_2-70001362.ZIP`. 
For Teched participants we have stored a copy on `\\students.fair.sap.corp\Studentshare\SEC366\XS_JAVAP_2-70001362.ZIP`.

**Note:** Be aware to adapt the version number in your `pom.xml` in case you are using a newer version of the SAP XS Security Libraries.

* Unzip `XS_JAVAP_2-70001362.ZIP` to `D:\Files\Session\sec366\libs`
* Install SAP XS Security Libraries to your local maven repo by executing:

```shell
cd D:\Files\Session\sec366\libs
D:
mvn clean install
```
* The following dependencies are already added to the advanced `pom.xml` file:

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
    <version>0.28.6</version>
</dependency>
<dependency>
    <groupId>com.sap.xs2.security</groupId>
    <artifactId>java-container-security</artifactId>
    <version>0.28.6</version>
</dependency>
<dependency>
    <groupId>com.unboundid.components</groupId>
    <artifactId>json</artifactId>
    <version>1.0.0</version>
</dependency>

```
## Step 2: Import the sample project into Eclipse
1. Open the Windows Start menu and enter ```Eclipse...``` in the input field. Under ```Programs``` you will see ```Eclipse Oxygen -...```. Click on this entry to open Eclipse.
2. If you are prompted for the workspace during startup, select ```<student directory>\workspace```.
3. Now import the target state of the sample project as Maven project into your Eclipse workspace: In the Eclipse menu, chose ```File```> ```Import...```.
4. In the ```Import``` wizard, select ```Maven``` > ```Existing Maven Projects``` and click ```Next```.
5. In the next step of the ```Import Maven Projects``` popup, click ```Browse```, navigate into the ```cloud-cf-product-list-sample-advanced\spring``` project in your student directory folder, then click ```Finish```.
6. The project is now imported in Eclipse. You should see the project in the Project Explorer.
7. Build the project in Eclipse (`Context Menu -> Run As -> Maven install`) -> Result: BUILD SUCCESS

## Step 3: Configuration of the Spring Security framework

**This step is mandatory only if you work on the master branch. For the advanced branch you can go through it to understand what is happening (no need to change anything)**

* In the advanced branch, a new class `com.sap.cp.cf.demoapps.ConfigSecurity.java` was created including the following scope checks and offline token validations.

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

Now all endpoints are blocked except the health endpoint. You can verify that by:
* running `Maven Install`
* right clicking on `product-list` and then `Run As -> Spring Boot App`
* clicking on the following link http://localhost:8080/health
