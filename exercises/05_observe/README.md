# Observe the application @ SAP Cloud Platform Cloud Foundry Environment

## Estimated time

:clock4: 10 minutes

## Objective

In this exercise you'll learn how you can get more info for the applications and services running in your trial account in the Cloud Foundry Environment, how you can get basic monitoring metrics and logs.

For all these exercises you have the information accessible via both tools: CF CLI and Cockpit. You will find below the commands that you should execute in CF CLI to get the info and the respective navigation in cockpit menus for the same.

# Exercise description

Before being able to see logs in Kibana, the service `application-logs` must be bound to the app.

Let's use the CLI to create and bind a service instance to our application.

```
cf marketplace
cf create-service application-logs lite myapplogs
cf services
cf bind-service product-list myapplogs
cf restage product-list
```

## List applications
List all applications in the target space with basic information for them.
### CF CLI
```
cf apps
```
### Cockpit
Navigate to your trial space that was created as part of Start Cloud Foundry Trial and click on **Applications** in the left-hand navigation menu.
<br><br>
![List Applications](/img/running_app_cockpit.png?raw=true)
<br><br>

## List Services
List the created service instances and bindings in a target space
### CF CLI
```
cf services
```
### Cockpit
In the left-hand navigation you have the services menu and if you select the **Service Instances** you will see all instances created in your space and basic info about them.
<br><br>
![List Services](/img/cockpit_services.png?raw=true)
<br><br>

## Logs
Show logs for an application
### CF CLI
- Tail logs for an app `cf logs APP_NAME`
```
cf logs product-list
```

:bulb: **Note:** Request the application in case you don't see any logs with the first command that tails logs.

:bulb: **Note:** To exit logs real-time stream mode in console click **Ctrl + C**

- Show recent logs for the application with `cf logs APP_NAME`
```
cf logs product-list --recent
```
### Cockpit
To view application logs in Cockpit you navigate again to the list of applications in your space and select the application you pushed and is now started (click on the application name). Then in the left-hand navigation click on **Logs** and you will see a table displaying the recent application logs. In case your application has more than one application instance running you can select with a drop-down for which instance you want to see logs.
<br><br>
![Logs in cockpit](/img/cockpit_logs.png?raw=true)
<br><br>
## Metrics
Display health and status for an application including information for CPU, memory and disk usage.
### CF CLI
In the console you can use the command `cf app APP_NAME` to see metrics for the application:
```
cf app product-list
```
### Cockpit
If you are already in the concrete application view in the left-hand navigation select **Overview** and this is where you see similar information for your running application.
<br><br>
![Metrics in cockpit](/img/cockpit_overview_app.png?raw=true)
<br><br>

## Events
Show recent application events (e.g. create, re-stage, update, etc.), when they happened and who triggered them.
### CF CLI
```
cf events APP_NAME
```
### Cockpit
Show events in Cockpit - in the dedicated application view, select in the left-hand navigation **Events**.
<br><br>
![Metrics in cockpit](/img/cockpit_events.png?raw=true)
<br><br>

## Environment Variables
### Accessing Environment Variables

To list the environment variables, one can either use the CF CLI command `cf env APP_NAME`:
```
cf env product-list
```
or the Cockpit by going to the dedicated application view and selecting from the left-hand navigation **Environment Variables**.
<br><br>
![Environment variables](/img/cockpit_env_vars.png?raw=true)
<br><br>

The following examples show how to access environment variables from an application programatically: Java, NodeJS and Ruby samples.
```java
System.getenv("VCAP_SERVICES");
```
```javascript
process.env.VCAP_SERVICES
```
```ruby
ENV['VCAP_SERVICES']
```
### Set Environment Variables
Application specific environment variables can be set by the application developer either using the CF CLI:
```
cf set-env APP_NAME VARIABLE_NAME VARIABLE_VALUE
```
or by writing them directly into the manifest:
```
---
  ...
  env:
    VARIABLE_NAME_1: VARIABLE_VALUE_1
    VARIABLE_NAME_2: VARIABLE_VALUE_2
```
or using cockpit:
<br><br>
![Metrics in cockpit](/img/user_provided_vars.png?raw=true)
<br><br>

### System Environment Variables
These are environment variables that Cloud Foundry makes available to your application container. The full list is:
- `CF_INSTANCE_ADDR`
- `CF_INSTANCE_GUID`
- `CF_INSTANCE_INDEX`
- `CF_INSTANCE_IP`
- `CF_INSTANCE_INTERNAL_IP`
- `CF_INSTANCE_PORT`
- `CF_INSTANCE_PORTS`
- `HOME`
- `MEMORY_LIMIT`
- `PORT`
- `PWD`
- `TMPDIR`
- `USER`
- `VCAP_APP_HOST`
- `VCAP_APP_PORT`
- `VCAP_APPLICATION`
- `VCAP_SERVICES`

The last one (`VCAP_SERVICES`) is important if your application is using bindable services because Cloud Foundry adds connection details to it when you restage your application after a service has been bound to it. The following example is taken from an app that has a postgresql service bound to it.

## Health Checks
An application health check is a monitoring process that continually checks the status of a running Cloud Foundry app. In Cloud Foundry, the default timeout is 60 seconds and the maximum configurable timeout is 180 seconds. There are three types of health checks available in Cloud Foundry:
* `http`: the `http` health check performs a GET request to the configured http endpoint. When the health check receives an `HTTP 200` response, the app is declared healthy. The configured endpoint must respond within 1 second to be considered healthy.
* `port`: this is the default check if nothing is specified. It makes a TCP connection to the port or ports configured for the app (default 8080). The TCP connection must be established within 1 second to be considered healthy.
* `process`: Diego ensures that any process declared for the app stays running. If the process exits, Diego stops and deletes the app instance. The health check can be specified either when pushing:
  ```
  cf push APP_NAME -u HEALTH-CHECK-TYPE -t HEALTH-CHECK-TIMEOUT
  ```
  or directly inside the manifest:

  ```
  ---
    ...
    health-check-type: http
    health-check-http-endpoint: /health
    timeout: 120
  ```

### CF CLI
Usage:
```
cf set-health-check APP_NAME (process | port | http [--endpoint PATH])
```
Examples:
```
cf set-health-check APP_NAME http --endpoint PATH (default to /)
cf set-health-check APP_NAME port
cf set-health-check APP_NAME PROCESS_NAME
```
Show health check type for a given app with CF CLI command `cf get-health-check APP_NAME`. You can check what's the default health check configured for the Product List application:
```
cf get-health-check product-list
```
