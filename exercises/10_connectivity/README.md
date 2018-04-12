# Connectivity

## Estimated time

:clock4: 20 minutes

## Objective

In this exercise you'll learn how to configure Cloud Connector, how to expose on-premise systems to be consumable by cloud applications and how to connect Cloud Connector to a subaccount on SAP Cloud Platform. After all configurations on Cloud Connector side are done you will learn how to extend a cloud application to consume the connectivity service and access on-premise systems exposed by Cloud Connector.

In the context of the *product-list* application, you'll change how the icons of the products are retrieved. Rather than having them as a local resource for the cloud application, you will put them on an external server, inaccessible via the Internet. You will make use of the *connectivity* service and Cloud Connector to access this server and get the product icons.

# Exercise description

In many cases cloud applications needs to access on-premise systems. In many cases these applications are not exposed in Internet for security reasons. For such scenario Cloud Connector can be used to expose some on-premise systems to some cloud applications. After Cloud Connector is configured and connected cloud applications can access them using connectivity service.

## Steps Overview

* Start a simple backend system on the local machine;
* Start and connect Cloud Connector to your trial subaccount;
* Configure Cloud Connector and expose the backend system from the first step;
* Modify the application to consume the connectivity service;
* Update it and bind it to a connectivity service instance;
* Test the application. Ensure that on-premise systems are not accessible when they are disabled in Cloud Connector.


# Detailed steps

## Set up Python

As an on-premise system we will use a part of the file system, exposed via Python's built-in http server. To achieve this we will need to download and install Python.
* Download Python from the [official web page](https://www.python.org/ftp/python/3.6.2/python-3.6.2.exe);
* Run the downloaded executable;
* In the install wizard, select **Install Now**;
* Wait for the installation to finish.

## Prepare simple backend system

Simple local http server can be started using following steps:
* Copy the folder, containing the example static files (src/main/resources/static/images) to an arbitrary folder on the local file system. It is possible also to use your own files;
* Start a simple HTTP server:
	* Open a separate CLI from the one you use for the Cloud Foundry commands;
	* Navigate to the parent folder of */images* (the location to which you copied the folder from the first step);
	* Execute the following command:
		```
			C:\Users\student\AppData\Local\Programs\Python\Python36-32\python.exe -m http.server 10080
		```
	:warning: Do not close the CLI used to start the server.
* Ensure that the started server is working by opening http://localhost:10080 from a browser.

	![Open Backend From Browser](/img/connectivity_backend.png?raw=true)


## Start and connect Cloud Connector

Cloud Connector is installed on the local machines so you need to simply start and configure it.
* Open [Cloud Connector configuration UI](https://scc.fair.sap.corp:8443). Credentials are **Administrator** : **welcome**. 
* Open [SAP Cloud Platform cockpit](https://account.hana.ondemand.com/cockpit) and navigate to the Cloud Foundry region via Home -> Cloud Foundry Environment -> US East (VA);
* Go to your trial subaccount and click on the "refresh" icon on the bottom right corner. Copy the ID into the clip board:

	![Get Subaccount ID](/img/connectivity_subaccountid.png?raw=true)
* Go to Cloud Connector UI and click on "Add Subaccount" button:

	![Add Subaccount](/img/connectivity_addsubaccount.png?raw=true)
* Select region : cf.us10.hana.ondemand.com. Subaccount is the ID from previous step. Enter your email address used to create the trial subaccout and password. You can put some description and display name. Note: In this exercise Location ID will not be used so do not specify any.

	![Subaccount Information](/img/connectivity_subaccountinfo.png?raw=true)
* If all configurations are successful Cloud Connector will report "Connected status".

## Configure Cloud Connector and expose the test backend system

By default backend systems are not accessible by the cloud application and have to be configured in Cloud Connector.
* In the Cloud Connector UI, go to "Cloud To On-Premise" -> "Access Control" and add mapping:

	![Add Mapping](/img/connectivity_addaccess.png?raw=true)
* Select backend type "Non SAP System" -> Protocol "HTTP" -> Internal host "localhost", Internal port "10080" -> Virtual host can be "**mybackend**" and you can keep the port 10080 -> Principal propagation type "None" -> Finish;
* Expose the */images* folder and all of its content.
	* Add a new resource to your virtual mapping:

		![Add Resource](/img/connectivity_resourceButton.png?raw=true)
	* Enter **/images** as URL path and select **Path and all sub-paths**:

		![Resource data](/img/connectivity_addresource.png?raw=true)

Now you have connected and configured Cloud Connector and you have exposed one backend system to the cloud applications.

## Modify the cloud application

(**only for master branch**) Now let us modify the application to consume the connectivity service:
* Add the following dependency to the *pom.xml* file:
```xml
	<dependency>
	      <groupId>org.cloudfoundry.identity</groupId>
	      <artifactId>cloudfoundry-identity-client-lib</artifactId>
	      <version>3.16.0</version>
	      <scope>compile</scope>
	</dependency>
	<dependency>
              <groupId>commons-io</groupId>
              <artifactId>commons-io</artifactId>
              <version>2.5</version>
        </dependency>
```
* In Eclipse, add a new class to the *com.sap.cp.cf.demoapps* package and name it **ConnectivityConsumer**;
* Change the content of the newly created **ConnectivityConsumer.java** file to:
```java
	package com.sap.cp.cf.demoapps;

	import java.io.IOException;
	import java.io.InputStream;
	import java.net.HttpURLConnection;
	import java.net.InetSocketAddress;
	import java.net.Proxy;
	import java.net.URI;
	import java.net.URL;

	import org.apache.commons.io.IOUtils;
	import org.cloudfoundry.identity.client.UaaContext;
	import org.cloudfoundry.identity.client.UaaContextFactory;
	import org.cloudfoundry.identity.client.token.GrantType;
	import org.cloudfoundry.identity.client.token.TokenRequest;
	import org.cloudfoundry.identity.uaa.oauth.token.CompositeAccessToken;
	import org.json.JSONArray;
	import org.json.JSONException;
	import org.json.JSONObject;
	import org.slf4j.Logger;
	import org.slf4j.LoggerFactory;
	import org.springframework.security.core.Authentication;
	import org.springframework.security.core.context.SecurityContextHolder;
	import org.springframework.security.oauth2.provider.authentication.OAuth2AuthenticationDetails;

	import com.sap.xs2.security.container.UserInfoException;

	public class ConnectivityConsumer {
		private static final Logger logger = LoggerFactory.getLogger(Application.class);

		public byte[] getImageFromBackend(String fileName) throws IOException {
			HttpURLConnection urlConnection = null;
			URL url = null;
			try {
				// Build the URL to the requested file.
				url = new URL("http://mybackend:10080/images/" + fileName);

				// Build the connectivity proxy and set up the connection.
				Proxy proxy = getProxy();
				urlConnection = (HttpURLConnection) url.openConnection(proxy);

				// Get connectivity access token and configure the proxy authorization header.
				CompositeAccessToken accessToken = getAccessToken();
				urlConnection.setRequestProperty("Proxy-Authorization", "Bearer " + accessToken);

				// Set connection timeouts.
				urlConnection.setConnectTimeout(10000);
				urlConnection.setReadTimeout(60000);

				// Get the user JWT token and configure the connectivity authentication header.
				String token = getClientOAuthToken();
				urlConnection.setRequestProperty("SAP-Connectivity-Authentication", "Bearer " + token);

				// Execute request, returning the response as a byte array.
				urlConnection.connect();
				InputStream in = urlConnection.getInputStream();
				return IOUtils.toByteArray(in);

			} catch (Exception e) {
				String messagePrefix = "Connectivity operation failed with reason: ";
				logger.error(messagePrefix, e);
			} finally {
				if (urlConnection != null) {
					urlConnection.disconnect();
				}
			}
			return null;
		}

		// Parse the credentials for a given service from the environment variables.
		private JSONObject getServiceCredentials(String serviceName) throws JSONException {
			JSONObject jsonObj = new JSONObject(System.getenv("VCAP_SERVICES"));
			JSONArray jsonArr = jsonObj.getJSONArray(serviceName);
			return jsonArr.getJSONObject(0).getJSONObject("credentials");
		}

		// Create a HTTP proxy from the connectivity service credentials.
		private Proxy getProxy() throws JSONException {
			JSONObject credentials = getServiceCredentials("connectivity");
			String proxyHost = credentials.getString("onpremise_proxy_host");
			int proxyPort = Integer.parseInt(credentials.getString("onpremise_proxy_port"));
			return new Proxy(Proxy.Type.HTTP, new InetSocketAddress(proxyHost, proxyPort));
		}

		// Get JWT token for the connectivity service from UAA
		private CompositeAccessToken getAccessToken() throws Exception {
			JSONObject connectivityCredentials = getServiceCredentials("connectivity");
			String clientId = connectivityCredentials.getString("clientid");
			String clientSecret = connectivityCredentials.getString("clientsecret");

			// Make request to UAA to retrieve JWT token
			JSONObject xsuaaCredentials = getServiceCredentials("xsuaa");
			URI xsUaaUri = new URI(xsuaaCredentials.getString("url"));

			UaaContextFactory factory = UaaContextFactory.factory(xsUaaUri).authorizePath("/oauth/authorize")
					.tokenPath("/oauth/token");

			TokenRequest tokenRequest = factory.tokenRequest();
			tokenRequest.setGrantType(GrantType.CLIENT_CREDENTIALS);
			tokenRequest.setClientId(clientId);
			tokenRequest.setClientSecret(clientSecret);

			UaaContext xsUaaContext = factory.authenticate(tokenRequest);
			return xsUaaContext.getToken();
		}

		private String getClientOAuthToken() throws Exception {
			Authentication auth = SecurityContextHolder.getContext().getAuthentication();
			if (auth == null) {
				throw new Exception("User not authenticated");
			}
			OAuth2AuthenticationDetails details = (OAuth2AuthenticationDetails) auth.getDetails();
			return details.getTokenValue();
		}
	}

```
The **getImageFromBackend** method handles the requests to the on-premise system. It is tasked with retrieving an image from the server via the connectivity service. The **getServiceCredentials** method is used to read JSON-formated credentials from the environment variables of the application. The **getProxy** method configures the Connectivity service endpoint as a proxy. The **getAccessToken** method requests a JWT access token from the xsuaa service. The token is used for the authorization of the connectivity proxy. The **getClientOAuthToken** retrieves the client JWT token. Applications are responsible to propagate this token via the **SAP-Connectivity-Authentication** header. This is needed by the connectivity service to open a tunnel to the subaccount for which a configuration is made in the Cloud Connector.
* Now add a way to make calls to the on-premise system. To achieve this, map all *images/\<file\>* requests to go through the *getImageFromBackend(String fileName)* method, with *\<file\>* passed as the argument. Change the content of **Controller.java** to:
```java
	package com.sap.cp.cf.demoapps;

	import java.io.IOException;
	import java.util.Collection;

	import org.slf4j.Logger;
	import org.slf4j.LoggerFactory;
	import org.springframework.beans.factory.annotation.Autowired;
	import org.springframework.http.MediaType;
	import org.springframework.web.bind.annotation.GetMapping;
	import org.springframework.web.bind.annotation.PathVariable;
	import org.springframework.web.bind.annotation.RequestParam;
	import org.springframework.web.bind.annotation.ResponseBody;
	import org.springframework.web.bind.annotation.RestController;

	@RestController
	public class Controller {

		private static final Logger logger = LoggerFactory.getLogger(Application.class);

		@Autowired
		private ProductRepo productRepo;

		@GetMapping("/productsByParam")
		public Collection<Product> getProductByName(@RequestParam(value = "name") final String name) {
			logger.info("***First - Retrieving details for '{}'.", name);
			logger.info("***Second - Retrieving details for '{}'.", name);
			return productRepo.findByName(name);
		}

		@ResponseBody
		@GetMapping(value = "/images/{imageFile:.+}", produces = MediaType.IMAGE_JPEG_VALUE)
		public byte[] getIcon(@PathVariable String imageFile) throws IOException {
			ConnectivityConsumer connConsumer = new ConnectivityConsumer();
			return connConsumer.getImageFromBackend(imageFile);
		}
	}
```		
:bulb: **Note:** If you look at **Application.java**, you will see that the URLs for the icons match the pattern you mapped in the controller, for example *"images/HT-1000.jpg"*. This means that when the list items are loaded, the icons will be retrieved from the on-premise system.

## Create connectivity service instance and bind it to the application
* Create connectivity service instance by executing the following command in the CLI:
```
 	cf create-service connectivity lite connInstance<Unique-ID>
```
* Bind it to your application:
```
	cf bind-service product-list connInstance<Unique-ID>
```

## Push the application
* Modify the `manifest.yml` file to include the binding for your connectivity instance for the application `product-list`:
```
	services:
	    - ...
	    - connInstance<Unique-ID>
```
* In Eclipse, Right-click on the project -> Run As -> Maven install;
* From the CLI, push the application:
```
	cf push product-list
```

## Test the application
* Open the application and ensure that the static files are taken from on-premise system. You can monitor the calls to the on-premise systems using the CLI you used to start the python server:

	![Console](/img/connectivity_onpremiseconsole.png?raw=true)
* Disable the exposed backend system: In the Cloud Connector UI -> "Cloud To On-Premise" -> Access Control -> select the virtual mapping that you have created -> disable the */images* resource from Resources table below:

	![Disable resource](/img/connectivity_disable.png?raw=true)

	Ensure that the cloud application is not able to access the backend system now.  
