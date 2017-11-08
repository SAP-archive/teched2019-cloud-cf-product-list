# Install Dependencies

## Estimated time

:clock4: 10 minutes

## Adding required security libraries

To secure the application we have to add Spring Security to the classpath. By configuring Spring Security in the application, Spring Boot automatically secures all HTTP endpoints with BASIC authentication. Since we want to use OAuth 2.0 together with [Java Web Tokens (JWT)](https://tools.ietf.org/html/rfc7519) instead, we need to add the Spring OAUTH and Spring JWT dependencies as well.

To enable offline JWT validation the SAP XS Security Libraries need to be added as well. The libraries are stored in `product-list/libs`. The latest version can be downloaded from the [Service Marketplace](https://launchpad.support.sap.com/#/softwarecenter/template/products/%20_APP=00200682500000001943&_EVENT=DISPHIER&HEADER=Y&FUNCTIONBAR=N&EVENT=TREE&NE=NAVIGATE&ENR=73555000100200004333&V=MAINT&TA=ACTUAL&PAGE=SEARCH/XS%20JAVA%201). At the time of writing the latest version is `XS_JAVA_4-70001362`.

**Note:** Be aware to adapt the version number in your `pom.xml` in case you are using a newer version of the SAP XS Security Libraries.

```
cd product-list/libs
mvn clean install
```

* Add the following deps to the `pom.xml` file:

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

**Note:** Right now, the unit tests will fail. To disable authentication for the unit tests we need to enhance the `ControllerTest` class.

* Add `@AutoConfigureMockMvc(secure = false)` to `ControllerTests` class
* Build the project in Eclipse (`Context Menu -> Run As -> Maven install`) -> Result: BUILD SUCCESS
* Run the project as Spring Boot App (`Context Menu -> Run As -> Spring Boot App`)
* Call `localhost:8080` from your browser -> a window is popping up informing us that authentication is required

All HTTP endpoints are secured and the Product List application is inaccessible. To regain access, we need to configure the Spring Security.
