# Integrate with Application Logging service

## Estimated time

:clock4: 15 minutes

## Objective

In this exercise you'll learn how the application integrates with the SAP Cloud Platform Application Logging service and how to visualize the application logs in Kibana dashboards together with the Cloud Foundry components logs.


# Exercise description


## Logging
First we will have a look at the source code of the application. It already uses the SAP library - Logging Support for Cloud Foundry: https://github.com/SAP/cf-java-logging-support
The pom.xml has the required dependencies declared, configuration (logback.xml) and implementation (ConfigLogging) is available.

* Open Eclipse and go to the class  `Controller.java` in my-product-list application"
  - a `Logger` object is already declared inside the class:
```java
private static final Logger logger = LoggerFactory.getLogger(Application.class);
```

  - The method `getProductByName` writes the following log entries:
  ```java
  logger.info("***First - Retrieving details for '{}'.", name);
  logger.info("***Second - Retrieving details for '{}'.", name);
  ```
* The application-logs service is already created and bound to the product-list.
* Before generating logs, the `logback.xml` file must be changed.
  * In Eclipse open `logback.xml` and change `<appender-ref ref="STDOUT-JSON" />` to `<appender-ref ref="STDOUT" />`
  * In Eclipse build the project - Right click on the project -> Run As... -> Maven Build
* Go into the root folder of the application via command line and push it with `cf push product-list`
<br><br>

## Generate logs
* To generate logs it is sufficient to request the application via the URL.
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

* In Eclipse open `logback.xml` Change `<appender-ref ref="STDOUT" />` to `<appender-ref ref="STDOUT-JSON" />`
* In Eclipse build the project - Right click on the project -> Run As... -> Maven Build
* Go into the root folder of the application via command line and push it with `cf push product-list`
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
