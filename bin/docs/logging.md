# Logging
[Logging overview from cloud curriculum](https://github.wdf.sap.corp/cc-java-dev/cc-coursematerial/tree/master/LoggingTracing)
* Eclipse:Controller.java
```java
private static final Logger logger = LoggerFactory.getLogger(Application.class);
```
* Eclipse:Controller.java:getProductByName
```java
logger.info("***First - Retrieving details for '{}'.", name);
logger.info("***Second - Retrieving details for '{}'.", name);
```
* Terminal: cf push product-list
* Terminal: cf logs product-list
* Browser: GET /productsByParam?name=Notebook%20Basic%2015
* Terminal: Show that there is NO logger output other than the standard HTTP Access Logs
* Terminal: CTRL-X
* Terminal: cf m // displaying the logging service on the marketplace
* Terminal: cf cs application-logs lite app-logs // creating an instance of the application-logs service
* Terminal: cf bs product-list app-logs // bind logging service to the application
* Terminal: cf restage product-list // restage application
* Terminal: cf logs product-list
* Browser: GET /productsByParam?name=Notebook%20Basic%2015
* Terminal: Show that there is now logger output
* Terminal: CTRL-X
* Terminal: cf logs product-list --recent
* Browser: https://logs.cf.us20.hana.ondemand.com
 * Select the product-list app
 * <Press> Requests and Logs
 * Show Requests and Application Logs
 * Show that the msg field in Application logs looks strange
 * Show that they are unrelated - application logs to not have a Correlation Id
 * That makes it hard to know which application log belongs to which request
* Eclipse - SAP library: Logging Support for Cloud Foundry: https://github.com/SAP/cf-java-logging-support
 * pom.xml
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
 * Eclipse: /product-list/src/main/resources
  * Create new file logback.xml with example minimal example from https://github.com/SAP/cf-java-logging-support
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
    <root level="${LOG_ROOT_LEVEL:-WARN}">
       <!-- Use 'STDOUT' instead for human-readable output -->
       <appender-ref ref="STDOUT-JSON" />
    </root>
  	<!-- request metrics are reported using INFO level, so make sure the instrumentation loggers are set to that level -->
    <logger name="com.sap.hcp.cf" level="INFO" />
</configuration>
```
 * Browser: GET localhost:8080/productsByParam?name=Notebook%20Basic%2015
 * Eclipse/Terminal: Show that the format of the log msg has changed to a JSON format
 * Eclipse: logback.xml: Change <appender-ref> to <appender-ref ref="STDOUT" />
 * Browser: GET localhost:8080/productsByParam?name=Notebook%20Basic%2015
 * Eclipse/Terminal: Show that the format of the log msg have changed to human readable format
 * Eclipse: logback.xml: Change <appender-ref> to <appender-ref ref="STDOUT-JSON" />
 * Eclipse: Maven install
 * cf push product-list
 * Browser: productsByParam?name=Notebook%20Basic%2015
 * Browser: https://logs.cf.us20.hana.ondemand.com
  * Show that the msg field in applications logs displays now the beautiful formatted message
  * Show that the messages are now linked via the Correlation Id
