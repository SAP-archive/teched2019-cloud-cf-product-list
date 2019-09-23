
## Step 4.1: Prerequisite
Make sure that you've imported the Product List sample application (Java) as part of this [Exercise](/docs/02_clone/README.md).
Within Eclipse IDE you should see the `product-list-java` project in the Project Explorer View.

* Build the project in Eclipse (`Context Menu -> Run As -> Maven install`) -> Result: **BUILD SUCCESS**

## Step 4.2: Security Configuration
The `web.xml` of the application must use auth-method with value XSUAA. This enables authentication of requests using incoming OAuth authentication tokens.

```xml
<web-app>
<display-name>Products App</display-name>
  <login-config> 
    <auth-method>XSUAA</auth-method>
  </login-config> 
</web-app> 
```

## Step 4.3.: Usage of the Security API in the application
In the Java coding, add the `@ServletSecurity` annotation to the Servlet `com.sap.cp.cf.demoapps.ProductHttpServlet` in order to apply scope checks to its endpoints:
```java

@WebServlet({ "/products/*", "/productsByParam" })
// configure servlet to check against scope "$XSAPPNAME.read"
@ServletSecurity(@HttpConstraint(rolesAllowed = { "read" }))
public class ProductHttpServlet extends HttpServlet {

    ...
}

```

## Step 4.4: Build the Project
* Build the project in Eclipse (`Context Menu -> Run As -> Maven install`) -> Result: **BUILD SUCCESS**
  * Or, alternatively build the project on the console with the following commands:
    ```
    D:
    cd D:\Files\Session\SEC364\teched2019-cloud-cf-product-list-teched2019\samples\java
    mvn clean install
    ```
* Finally, make sure that the folder `D:\Files\Session\SEC364\teched2019-cloud-cf-product-list-teched2019\samples\java\target` contains a `product-list.war` file. 

## Further References
- Java Web Application Sample:  
https://github.com/SAP/cloud-security-xsuaa-integration/tree/master/samples/sap-java-buildpack-api-usage
- XSUAA Token Client and Token Flow API:  
https://github.com/SAP/cloud-security-xsuaa-integration/tree/master/token-client

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
