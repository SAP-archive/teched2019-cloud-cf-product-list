
## Step 1: Import the sample project into Eclipse
1. Open the Windows Start menu and enter ```Eclipse...``` in the input field. Under ```Programs``` you will see ```Eclipse Oxygen -...```. Click on this entry to open Eclipse.
2. If you are prompted for the workspace during startup, select ```<student directory>\workspace```.
3. Now import the target state of the sample project as Maven project into your Eclipse workspace: In the Eclipse menu, chose ```File```> ```Import...```.
4. In the ```Import``` wizard, select ```Maven``` > ```Existing Maven Projects``` and click ```Next```.
5. In the next step of the ```Import Maven Projects``` popup, click ```Browse```, navigate into the ```cloud-cf-product-list-sample-advanced\java``` project in your student directory folder, then click ```Finish```.
6. The project is now imported in Eclipse. You should see the project in the Project Explorer.
7. Build the project in Eclipse (`Context Menu -> Run As -> Maven install`) -> Result: BUILD SUCCESS

## Step 2: Security configuration

**This step is mandatory only if you work on the master branch. For the advanced branch you can go through it to understand what is happening (no need to change anything)**

The web.xml of the application must use auth-method with value XSUAA. This enables authentication of requests using incoming OAuth authentication tokens.

```
<web-app>
<display-name>sample</display-name>
  <login-config> 
    <auth-method>XSUAA</auth-method>
  </login-config> 
</web-app> 
```

In the Java coding, use the @ServletSecurity annotations:
```
package com.sap.cp.cf.demoapps;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.annotation.ServletSecurity;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.annotation.HttpConstraint;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;

@WebServlet({ "/products/*", "/productsByParam" })
// configure servlet to check against scope "$XSAPPNAME.read"
@ServletSecurity(@HttpConstraint(rolesAllowed = { "read" }))
public class Controller extends HttpServlet {

	private static final long serialVersionUID = 1L;
	private Gson gson = new Gson();
	private ProductService productService = new ProductService();
	private static final Logger logger = LoggerFactory.getLogger(Controller.class);

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		String productsJson;
		if (request.getParameter("name") != null) {
			String name = request.getParameter("name");
			logger.info("***First - Retrieving details for '{}'.", name);
			logger.info("***Second - Retrieving details for '{}'.", name);
			productsJson = this.gson.toJson(productService.getProductByName(name));

		} else {
			productsJson = this.gson.toJson(productService.getProducts());
		}

		response.setContentType("application/json");
		PrintWriter out = response.getWriter();
		out.print(productsJson);
		out.flush();

	}
}
```

Now all endpoints are blocked.
