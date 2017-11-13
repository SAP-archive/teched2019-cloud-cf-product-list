# Hands-on session - 2 hours

This tutorial shows how to:
* get access to SAP Cloud Platform Cloud Foundry Environment trial
* work with your Cloud Foundry trial using the SAP Cloud Platform cockpit and Cloud Foundry CLI
* use basic concepts of Cloud Foundry, like backing services,
* use application frameworks like Spring and Spring Boot to efficiently develop an application,
* monitor and scale your application.


# Exercises

:one: **Setup the environment**

In this exercise, you will start a free SAP Cloud Platform Cloud Foundry Environment trial which you can use to deploy and run applications. You will also setup your local development environment and tools: [setup](../01_setup)

:two: **Clone the sample application**

Clone the target version of the application that we will develop during the session. As a result you will have the target version of the application imported in Eclipse in case you need it as a reference or to copy easily some snippets: [clone](../02_clone)

:three: **Push to cloud**

In this exercise, you will push the Product List application to your Clooud Foundry environment trial, change the application to use PostgreSQL service for persistence layer: [push](../04_push)

:four: **Observe the application**

Check what information is available for the running application via CF CLI and SAP Cloud Platform cockpit: application events, logs, metrics, service instances, etc.: [observe](../05_observe)

:five: **Integrate application logging**

In this exercise, we improve the application with supportability enhancements e.g. integrate with Application Logging service and understand how health-checks work and can be changed: [application logging](../12_app_logs)

:six: **(Optional) Scale**

Explore different options for application scaling that are available in the SAP Cloud Platform Cloud Foundry Environment.  [scale](../07_scale)
