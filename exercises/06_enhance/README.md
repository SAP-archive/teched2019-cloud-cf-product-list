# Enhance the application - integrate with Application Logging service

## Estimated time

:clock4: 15 minutes

## Objective

In this exercise you'll learn how you can enhance the application integrating with the SAP Cloud Platform Application Logging service and visualize the application logs in Kibana dashboards together with the Cloud Foundry components logs.


# Exercise description


## Logging
We will now enhance a bit the sample product list application so that the application logs are integrated in the Application Logging and visible in the Kibana dashboards you can use to visualize them, perform queries, etc. We will need to modify a bit the source code of the application for this integration.

* Open Eclipse andgo to the class  `Controller.java` in my-product-list application that we created together so far"
  - Create `Logger` object inside the class:
```java
private static final Logger logger = LoggerFactory.getLogger(Controller.class);
```
  - Add needed imports:
  ```java
  import org.slf4j.Logger;
  import org.slf4j.LoggerFactory;
  ```

  - Add the following lines to the method `getProductByName`
  ```java
  logger.info("***First - Retrieving details for '{}'.", name);
  logger.info("***Second - Retrieving details for '{}'.", name);
  ```
 - Build the application - Run As.. Maven Build, goal: clean install.
* Open the terminal in the root folder of my-product-list application and push it via CF CLI: `cf push product-list`
* Terminal: `cf m` // You should see in the list of services on the marketplace the application-logging service
* Terminal: `cf cs application-logs lite app-logs` // creating an instance of the application-logs service
* Terminal: `cf bs product-list app-logs` // bind logging service to the application
* Terminal: `cf restage product-list` // restage application
* Browser - go to cockpit and navigate to your application - request it via the URL.
<br><br>
![Application Routes](/img/application_routes_cockpit.png?raw=true)
<br><br>
* To generate application logs request in browser the relative path:
`YOUR_APPLICATION_URL/productsByParam?name=Notebook Basic 15`
* In cockpit navigate to Logs for my-product-list application (click on Logs tab in the left hand navigation panel). Then click on **Open Log Analysis** link like shown below:
<br><br>
![Application Log Analysis](/img/cockpit_open_log_analysis.png?raw=true)
<br><br>
* This should lead to opening in the browser the following URL (for applications running on EU10 region, the URL is with eu10 instead of us10): https://logs.cf.us10.hana.ondemand.com
* Login with the e-mail and password you used for cockpit and with which you created the trial account.
* Select the product-list app in Kibana
<br><br>
![Kibana select app](/img/kibana_product_list_app.png?raw=true)
<br><br>
* `<Press>` Requests and Logs
<br><br>
![Kibana requests and logs](/img/kibana_requests_logs.png?raw=true)
<br><br>
* Show Requests and Application Logs
* The msg field in Application logs looks strange
  * Show that they are unrelated - application logs do not have a Correlation Id
  * That makes it hard to know which application log belongs to which request
  <br><br>
  ![Kibana Message](/img/kibana_msg_no_correlationid.png?raw=tru)
  <br><br>

:bulb:**Note:** In case you don't see logs in Kibana it may be as by default you see the logs from the last 15 minutes. You can change this interval - on the right top corner:
<br><br>
![Kibana recent logs](/img/kibana_recent_logs.png?raw=true)
<br><br>
* Navigate back to **Eclipse** - we will now see how you can use SAP library: Logging Support for Cloud Foundry: https://github.com/SAP/cf-java-logging-support
* Open my-product-list pom.xml and add the following dependencies:
 ```xml
        <!-- Logging Support for Cloud Foundry -->
		<dependency>
			<groupId>com.sap.hcp.cf.logging</groupId>
			<artifactId>cf-java-logging-support-logback</artifactId>
			<version>2.0.10</version>
		</dependency>
		<dependency>
			<groupId>com.sap.hcp.cf.logging</groupId>
			<artifactId>cf-java-logging-support-servlet</artifactId>
			<version>2.0.10</version>
		</dependency>
```
* In Eclipse go to: `/product-list/src/main/resources`
* Create new file logback.xml and copy and paste the content below:
```xml
<configuration debug="false" scan="false">
	<appender name="STDOUT-JSON" class="ch.qos.logback.core.ConsoleAppender">
       <encoder class="com.sap.hcp.cf.logback.encoder.JsonEncoder"/>
    </appender>
    <!-- for local development, you may want to switch to a more human-readable layout -->
    <appender name="STDOUT" class="ch.qos.logback.core.ConsoleAppender">
        <encoder>
            <pattern>%date %-5level [%thread] - [%logger] [%mdc] - %msg%n</pattern>
        </encoder>
    </appender>
    <root level="${LOG_ROOT_LEVEL:-INFO}">
       <!-- Use 'STDOUT' instead for human-readable output -->
       <appender-ref ref="STDOUT-JSON" />
    </root>
  	<!-- request metrics are reported using INFO level, so make sure the instrumentation loggers are set to that level -->
    <logger name="com.sap.hcp.cf" level="INFO" />
</configuration>
```

To enable the logging configuration we have to create a bean that returns a `RequestLoggingFilter`.

* Create one new class in the project - click on the package for the app -> New Class -> ConfigLogging
* Copy and paste the below snippet in the class:
```
package com.sap.cp.cf.demoapps;

import javax.servlet.Filter;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.sap.hcp.cf.logging.servlet.filter.RequestLoggingFilter;

@Configuration
public class ConfigLogging {

	@SuppressWarnings("static-method")
	@Bean
	public Filter requestLoggingFilter() {
		return new RequestLoggingFilter();
	}
}
```

* Save the file and build the project - Run As -> Maven Build
* Now run locally the application. In the project explorer -> right click on the application -> Run As -> SpringBoot App
* Now request the app running on your local machine in a browser: `localhost:8080/productsByParam?name=Notebook%20Basic%2015`
* Eclipse/Console: You can now see that the format of the log msg has changed to a JSON format
* In Eclipse open **logback.xml**. Change `<appender-ref ref="STDOUT-JSON" />` to `<appender-ref ref="STDOUT" />`
* Request again the URL in a browser to generate new logs: `localhost:8080/productsByParam?name=Notebook%20Basic%2015`
* Eclipse/Console: Check now that the format of the log msg have changed to human readable format.
* Switch back to JSON format -> Eclipse: logback.xml: Change `<appender-ref>` to `<appender-ref ref="STDOUT-JSON" />`
* In Eclipse build the project - Right click on the project -> Run As... -> Maven Build
* Go to command line the root folder of the application and push it with `cf push product-list`
* Once the application is up and running in your Cloud Foundry trial account, go to cockpit, navigate to the application and request the application route in a browser adding the relative path that generates logs: `YOUR_APP_URL/productsByParam?name=Notebook%20Basic%2015`
* If Kibana is still open, you can just refresh the tab to show latest logs. If you closed it, go again to cockpit, in the application view, click on Logs tab in the left hand navigation and click the Kibana link (click on **Open Log Analysis**)
  * You can see now that the msg field in applications logs displays now the beautiful formatted message
  <br><br>
  ![Kibana logs format](/img/kibana_logs_format.png?raw=true)
  <br><br>
  * You can also see that the Application Logs messages are now linked via the Correlation Id to the Requests:
  <br><br>
  ![Kibana correlatioIDs](/img/kibana_correlationIDs.png?raw=true)
  <br><br>

:bulb: If you want to learn more about Application Logging in SAP Cloud Platform Cloud Foundry Environment take a look at the official documentation:

:link:[Application Logging overview in official documentation](https://help.sap.com/viewer/65de2977205c403bbc107264b8eccf4b/Cloud/en-US/68454d44ad41458788959485a24305e2.html)
