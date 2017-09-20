# Hands-on session - 2 hours

This tutorial shows how to:
* get access to SAP Cloud Platform Cloud Foundry environment trial account
* work with your Cloud Foundry trial account and application using the SAP Cloud Platform cockpit, Cloud Foundry CLI and plugins, Eclipse Cloud Foundry plugin
* use basic concepts of Cloud Foundry, like backing services,
* use application frameworks like Spring and Spring Boot to efficiently develop microservices,
* monitor and scale your application.


# Exercises

:one: **Setup the environment**

In this exercise, you will sign up for a free SAP Cloud Platform Cloud Foundry environment trial account which you can use to deploy and run the application. You will also setup your development environment and tools. Then you will clone the Git repository that contains the complete sample project from github into your local environment: [setup](../01_setup)

:two: **Clone the sample application**

 Clone the target version of the application that we will develop during the session. As a result you will have the target version of the application imported in Eclipse in case you need it as a reference or to copy easily some snippets: [clone](../02_clone)

:three: **Develop the application**

You will develop from scratch the Product List application. As a result you will have the initial version of the application ready and running locally: [develop](../03_develop)


:four: **Push to cloud**

In this exercise, you will push the Product List application to your trial account in the Cloud Foundry environment, change the application to use SAP Cloud Platform backing service PostgreSQL for persistence layer: [push](../04_push)

:five: **Observe the application**

Check what information is available for the running application via CF CLI and SAP Cloud Platform cockpit: application events, logs, metrics, service instances, etc.: [observe](../05_observe)

:six: **Enhance the application**

In this exercise, we improve the application with supportability enhancements e.g. integrate with Application Logging service and understand how health-checks work and can be changed: [enhance](../06_enhance)

:seven: **(Optional) Scale**

Explore different options for application scaling that are available in the SAP Cloud Platform Cloud Foundry environment.  [scale](../07_scale)
