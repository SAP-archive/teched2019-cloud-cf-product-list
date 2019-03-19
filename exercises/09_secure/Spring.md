## Step 1: Adding required security libraries

**This step is mandatory for the master as well as for the advanced branch**

To secure the application we have to add Spring Security to the classpath. By configuring Spring Security in the application, Spring Boot automatically secures all HTTP endpoints with BASIC authentication. Since we want to use OAuth 2.0 together with [Java Web Tokens (JWT)](https://tools.ietf.org/html/rfc7519) instead, we need to add the Spring OAUTH and Spring JWT dependencies as well.

* The following dependencies are already added to the advanced `pom.xml` file:

```xml
<dependency>
    <groupId>org.springframework.security</groupId>
    <artifactId>spring-security-oauth2-jose</artifactId>
</dependency>
<dependency>
    <groupId>org.springframework.security</groupId>
    <artifactId>spring-security-config</artifactId>
</dependency>
<dependency>
    <groupId>org.springframework.security</groupId>
    <artifactId>spring-security-oauth2-resource-server</artifactId>
</dependency>
<dependency>
    <groupId>com.sap.cloud.security.xsuaa</groupId>
    <artifactId>spring-xsuaa</artifactId>
    <version>1.3.0</version>
</dependency>

```
**Additional information:** https://github.com/SAP/cloud-security-xsuaa-integration/tree/master/spring-xsuaa

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

import com.sap.cloud.security.xsuaa.XsuaaServiceConfigurationDefault;
import com.sap.cloud.security.xsuaa.XsuaaServicePropertySourceFactory;
import com.sap.cloud.security.xsuaa.token.TokenAuthenticationConverter;
import com.sap.cloud.security.xsuaa.token.authentication.XsuaaJwtDecoderBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;

import static org.springframework.http.HttpMethod.GET;

@Configuration
@EnableWebSecurity
@PropertySource(factory = XsuaaServicePropertySourceFactory.class, value = { "" })
public class ConfigSecurity extends WebSecurityConfigurerAdapter {

	@Value("${vcap.services.xsuaa.credentials.xsappname:product-list}")
	private String xsAppName;

	@Autowired
	XsuaaServiceConfigurationDefault xsuaaServiceConfiguration;

	@Override
	public void configure(final HttpSecurity http) throws Exception {

		// @formatter:off
		http.sessionManagement()
				.sessionCreationPolicy(SessionCreationPolicy.NEVER)
				.and()
					.authorizeRequests()
					.antMatchers(GET, "/health").permitAll()
					.antMatchers(GET, "/**").hasAuthority("read")
					.anyRequest().denyAll() // deny anything not configured above
				.and()
					.oauth2ResourceServer()
					.jwt()
					.jwtAuthenticationConverter(getJwtAuthenticationConverter());

		// @formatter:on
	}

	/**
	 * Customizes how GrantedAuthority are derived from a Jwt
	 */
	Converter<Jwt, AbstractAuthenticationToken> getJwtAuthenticationConverter() {
		TokenAuthenticationConverter converter = new TokenAuthenticationConverter(xsuaaServiceConfiguration);
		converter.setLocalScopeAsAuthorities(true);
		return converter;
	}

	@Bean
	JwtDecoder jwtDecoder() {
		return new XsuaaJwtDecoderBuilder(xsuaaServiceConfiguration).build();
	}

	@Bean
	XsuaaServiceConfigurationDefault config() {
		return new XsuaaServiceConfigurationDefault();
	}

}
```

To build the project, run the following commands:
```
D:
D:\Files\Session\SEC366\cloud-cf-product-list-sample-advanced\spring
mvn clean install
```
