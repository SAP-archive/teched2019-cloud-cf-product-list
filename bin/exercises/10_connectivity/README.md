# Exercise 10: Connectivity

## Estimated time

:clock4: 20 minutes

## Objective

In this exercise you'll learn how to configure Cloud Connector, how to expose on-premise systems to be consumable by cloud applications and how to connect Cloud Connector to subaccount on SAP Cloud Platform. After all configurations on Cloud Connector side are done you will learn how to extend a cloud application to consume connectivity service and access on-premise systems exposed by Cloud Connector. 

# Exercise description

In many cases cloud applications needs to access on-premise systems. In many cases these applications are not exposed in Internet for security reasons. For such scenario Cloud connector can be used to expose some on-premise systems to some cloud applications. After Cloud connector is configured and connected cloud applications can access them using connectivity service. 

## Steps Overview 

* Start simple backend system on the local machine. 
* Start and connect cloud connector to your trial subaccount 
* Configure Cloud Connector and expose the backend system form the previous step.
* Modify the application to consume connectivity service 
* Update it and bind it to a connectivity service instance 
* Test the application. Ensure that on-premise systems are not accessible when they are disabled in Cloud Connector. 


# Detailed steps

## Prepare simple backend system 

Simple local http server can be started using following steps: 
* Copy the example static files to an arbitrary folder on the local file system. It is possible also to use own files. 
* Start simple http server from the folder where the files were copied using : python -m SimpleHTTPServer 10080. 
* Ensure that the started server is working by opening http://localhost:10080 from a browser.

## Start and connect Cloud Connector 

Cloud connector is installed on the local machines so you need to simple start and configure it. 
* Open Cloud Connector configuration UI https://localhost:8443 from a browser. Initial credentials are Administrator:manager. Change the initial password with your own. 
* Open cloud cockpit on https://account.us1.hana.ondemand.com/cockpit and navigate to the cloud foundry region via Home -> Cloud Foundry Environment -> US East (VA).
* Go to your trial subaccount and click on the "refresh" icon on the bottom right corner. Copy the ID into the clip board. 
* Go to Cloud Connector UI and click on "Add Subaccount" button , sellect region : cf.us10.hana.ondemand.com. Subaccount is the ID from previous step. Enter your email address used to create the trial subaccout and password. You can put some description and display name. Note: In this exercise Location ID will not be used. So do not specify any.  
* If all configurations are successful Cloud Connector will report "Connected status."

## Configure Cloud Connector and expose the test backend system

By default backend systems are not accessuble byt the cloud application and have to be configured in Cloud Connector. 
* In Cloud Connector UI go to "Cloud To On-Premise" -> "Access Control" and add mapping. Select backend type "Non SAP System" -> Protocol "HTTP" -> Internal host "localhost", Internal port "10080" -> Virtual host can be "myBackend" and you can keep the port 10080. -> Principal propagation type "None" and Finish. 

Now you can connected and configured Cloud Connector and one backend system that is exposed to the cloud applications. 

## Modify the cloud application 

Connectivity service can be consumed with following code sample:
* Configure Connectivity service endpoint as proxy: 
```java

	JSONObject credentials = getServiceCredentials(CONNECTIVITY_SERVICE_NAME);
	String proxyHost = credentials.getString(OP_HTTP_PROXY_HOST);
	int proxyPort = Integer.parseInt(credentials.getString(OP_HTTP_PROXY_PORT));
	Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(proxyHost, proxyPort));
	urlConnection = (HttpURLConnection) url.openConnection(proxy);
```
* Create access token used to call connectivity service 
```java
	JSONObject credentials = getServiceCredentials(serviceName);
	String clientId = credentials.getString(CLIENT_ID);
	String clientSecret = credentials.getString(CLIENT_SECRET);

	// Make request to UAA to retrieve JWT token
	JSONObject xsuaaCredentials = getServiceCredentials(XSUAA_SERVICE_NAME, xsuaaInstanceName);
	URI xsUaaUri = new URI(xsuaaCredentials.getString(XSUAA_SERVICE_PROP_URL));
	UaaContextFactory factory = UaaContextFactory.factory(xsUaaUri).authorizePath(UAA_AUTHORIZE_PATH).tokenPath(UAA_TOKEN_PATH);

	TokenRequest tokenRequest = factory.tokenRequest();
	tokenRequest.setGrantType(GrantType.CLIENT_CREDENTIALS);
	tokenRequest.setClientId(clientId);
	tokenRequest.setClientSecret(clientSecret);

	// Skipping of SSL should be done only on Dev environment 
	Boolean skipSslValidation = Boolean.parseBoolean(System.getenv(SKIP_SSL_VALIDATION));
	LOGGER.info("Skip SSL validation: " + skipSslValidation);
	tokenRequest.setSkipSslValidation(skipSslValidation);

	UaaContext xsUaaContext = factory.authenticate(tokenRequest);
	return xsUaaContext.getToken();
```

* Configure autorization header 

urlConnection.setRequestProperty(HEADER_PROXY_AUTHORIZATION, BEARER + token); 

* Execute request 
```java
	urlConnection.connect();
	OutputStream clientOutStream = responseToClient.getOutputStream();
	copyStream(backendInStream, clientOutStream);
	responseToClient.setStatus(backendResponseCode);
```		
		
## Create connectivity service instsnce and bind it to the application 
* create connectivity service instance
 cf create-service connectivity lite connInstance<Unique-ID>
 
* bind it to your application
  cf bind-service <application name> connInstance<Unique-ID>

## Test the application 
* Open the application and ensure that the static files are taken form on-premise system. 
* Disable the exposed backend system from Cloud Connector UI -> "Clloud To On-Premise" -> Access Control -> select the virtual mapping that you have created -> disable the check box in the the Resources table below. Ensure that the cloud application is not able to access the backend system now.   



