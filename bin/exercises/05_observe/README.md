# Exercise 05: Observe the application @ SAP Cloud Platform Cloud Foundry environment

## Estimated time

:clock4: 5 minutes

## Objective

In this exercise you'll learn how you can get more info for the applications and services running in your developer account in the Cloud Foundry environment, how you can get basic monitoring metrics and logs. For all these exercises you have the information available in both tools: CF CLI and Cockpit.

# Exercise description
## List applications
List all applications in the target space with basic information for them.
### CF CLI
```
cf apps
```
### Cockpit
Navigate to your trial space that was created as part of Start Cloud Foundry Trial and click on **Applications** in the left-hand navigation menu.
## List Services
List the created service instances and bindings in a target space
### CF CLI
```
cf services
```
### Cockpit
Go one level up and navigate to your space. Then in the left-hand navigation you have the services menu and if you select the **Service Instances** you will see all instances created in your space and basic info about them.
## Logs
Show logs for an application
### CF CLI
-- Tail logs for an app
```
cf logs APP_NAME
```
:bulb: Request the application in case you don't see any logs with the first command that tails logs.
:bulb: To exit logs real-time stream mode in console click **Ctrl + C**
-- Show recent logs for the application.
```
cf logs APP_NAME --recent
```
### Cockpit
To view application logs in Cockpit you navigate again to the list of applications in your space and select the application you pushed and is not running. Then in the left-hand navigation click on **Logs** and you will see a table displaying the recent application logs. In case your application has more than one application instance running you can select with a drop-down for which instance you want to see logs.
## Metrics
Display health and status for an application including information for CPU, memory and disk usage.
### CF CLI
```
cf app APP_NAME
```
### Cockpit
If you are already in the concrete application view in the left-hand navigation select **Overview** and this is where you see similar information for your running application.
## Events
Show recent application events (e.g. create, re-stage, update, etc.), when they happened and who triggered them.
### CF CLI
```
cf events APP_NAME
```
### Cockpit
Show events in Cockpit - in the dedicated application view, select in the left-hand navigation **Events**.
## Environment Variables
Show the environment variables available for the application.
### CF CLI
```
cf env APP_NAME
```
### Cockpit
In the dedicated application view, select in the left-hand navigation **Environment Variables**.
