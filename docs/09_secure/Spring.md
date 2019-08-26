## Step 4.1: Prerequisite
Make sure that you've imported the Product List sample application (Spring) as part of this [Exercise](/docs/02_clone/README.md).
Within Eclipse IDE you should see the `product-list` project in the Project Explorer View.

* Build the project in Eclipse (`Context Menu -> Run As -> Maven install`) -> Result: **BUILD FAILURE**

> The build fails as the `ControllerTests` JUnit test expects that a bean of `XsuaaServiceConfiguration` class exists and all `GET` endpoints of the Product-List are secured.   

## Step 4.2: Adding required security libraries

To secure the application we have to add **XSUAA Spring Security library** to the classpath. 
This library enhances the [spring-security](https://github.com/spring-projects/spring-security/) project. As of version 5 of spring-security, this includes the OAuth resource-server functionality. 
A Spring boot application needs a security configuration class that enables the resource server and configures authentication with XSUAA as OAuth Authorization Server using JWT tokens.


* Get the current version of the SAP XSUAA Integration Security library from [Maven Central](https://search.maven.org/search?q=com.sap.cloud.security).
* Add the following dependency to your `pom.xml` file:

    ```xml
    <dependency>
        <groupId>com.sap.cloud.security.xsuaa</groupId>
        <artifactId>xsuaa-spring-boot-starter</artifactId>
        <version>1.6.0</version>
    </dependency>
    ```
* After you've added the Maven dependencies, don't forget to update your Maven project (`ALT-F5`)! 
* Note: the following test-dependencies __are already__ added to the `pom.xml` file:

    ```xml
    <dependency>
        <groupId>com.sap.cloud.security.xsuaa</groupId>
        <artifactId>spring-xsuaa-test</artifactId>
        <version>1.6.0</version>
        <scope>test</scope>
    </dependency>
    <dependency>
        <groupId>com.sap.cloud.security.xsuaa</groupId>
        <artifactId>spring-xsuaa-mock</artifactId>
        <version>1.6.0</version>
        <scope>test</scope>
    </dependency>
    ```


## Step 4.3: Configuration of the (XSUAA) Spring Security framework

* Update `com.sap.cp.cf.demoapps.SecurityConfiguration.java` class including the following scope checks and offline token validations.

```java
import com.sap.cloud.security.xsuaa.XsuaaServiceConfiguration;
import com.sap.cloud.security.xsuaa.token.TokenAuthenticationConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.jwt.Jwt;

import static org.springframework.http.HttpMethod.GET;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true, securedEnabled = true, jsr250Enabled = true)
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {

    private final XsuaaServiceConfiguration xsuaaServiceConfiguration;
        
    @Autowired
    public SecurityConfiguration(XsuaaServiceConfiguration xsuaaServiceConfiguration) {
        this.xsuaaServiceConfiguration = xsuaaServiceConfiguration;
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.NEVER)
            .and()
                .authorizeRequests()
                    .antMatchers(GET, "/actuator/**").anonymous() // accepts unauthenticated user (w/o JWT)
                    .antMatchers(GET, "/", "/products/**").hasAuthority("read") // scope check
                    .antMatchers(GET, "/productsByParam").authenticated()  // find scope check in ProductRepo using @PreAuthorize
                    .anyRequest().denyAll() // deny anything not configured above
            .and()
                .oauth2ResourceServer()
                .jwt()
                .jwtAuthenticationConverter(getJwtAuthoritiesConverter());
    }

    Converter<Jwt, AbstractAuthenticationToken> getJwtAuthoritiesConverter() {
        TokenAuthenticationConverter converter = new TokenAuthenticationConverter(xsuaaServiceConfiguration);
        converter.setLocalScopeAsAuthorities(true);
        return converter;
    }
}
```

## Step 4.4: Apply Method Security

After the previous step still one JUnit test will fail as the `/productsByParam` endpoint is not protected with scope checks.
With `@EnableGlobalMethodSecurity` annotation (see `SecurityConfiguration` class) Spring Method Security is enabled. Now you can apply fine granular authorization checks on method level. 

* In the `ProductRepo` class annotate the `findByName(String)` method with:
    ```java
    @PreAuthorize("hasAuthority('read')")
    ```  
  ... and fix the missing import issue.

## Step 4.5: Build the Project
* Build the project in Eclipse (`Context Menu -> Run As -> Maven install`) -> Result: **BUILD SUCCESS**
  * Or, alternatively build the project on the console with the following commands:
    ```
    D:
    cd D:\Files\Session\SEC364\cloud-cf-product-list-teched2019\samples\spring
    mvn clean install
    ```
* Finally, make sure that the folder `D:\Files\Session\SEC364\cloud-cf-product-list-teched2019\samples\spring\target` contains a `product-list.jar` file. 

## Further References
- Github XSUAA Spring Security library: https://github.com/SAP/cloud-security-xsuaa-integration/tree/master/spring-xsuaa

***
<dl>
  <dd>
  <div class="footer">&copy; 2019 SAP SE</div>
  </dd>
</dl>
<hr>
<a href="/docs/09_secure/README.md#step-5-deploy-approuter-and-application-to-cloud-foundry">
  <img src="/docs/img/arrow_left.png" height="80" border="10" align="left" alt="Continue Exercise" title="Continue Exercise: Secure application">
</a>

