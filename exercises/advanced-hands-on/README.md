# Advanced Hands-on session - 2 hours

This tutorial shows how to:
* get access to SAP Cloud Platform Cloud Foundry Environment trial
* work with your trial using the SAP Cloud Platform cockpit and Cloud Foundry CLI
* secure the application and configure different roles
* setup and use connectivity to obtain data from a backend system.
* scale and update your application

# Exercises

:one: **Setup the environment**

In this exercise, you will start a free SAP Cloud Platform Cloud Foundry Environment trial which you can use to deploy and run applications. You will also setup your development environment and tools: [setup](../01_setup)

:two:
There are two options here. Either the final application is cloned and used as base for the next steps (:a:), or the app is developed from scratch by yourself (:b:).
Please note that developing the app from scratch could require most of the session time, leaving no room for other topics like security and connectivity.

:a: **Clone the sample application**

Clone also the **advanced** branch of the product list application: [clone advanced branch](../11_clonebranch)

:b: **Develop the application**

You will develop from scratch the Product List application. As a result you will have the initial version of the application ready and running locally: [develop](../03_develop)

:three: **Push to cloud**

In this exercise, you will push the Product List application to your Cloud Foundry Environment trial, change the application to use PostgreSQL service for persistence layer: [push](../04_push)

:four: **Security**

Secure the Product List application, configure the OAuth 2.0 Client Credentials Grant (service to service communication) and the OAuth 2.0 Authorization Code Grant (human to service communication):  [secure](../09_secure)

:five: **Connectivity**

Use Cloud Connector and Connectivity service to retrieve data from on-premise backend systems [connectivity](../10_connectivity)

:six: **(Optional) Scale**

Explore different options for application scaling that are available in the SAP Cloud Platform Cloud Foundry Environment.  [scale](../07_scale)

:seven: **(Optional) Update**

Blue-green deployment applied for update of the application - manually with CF CLI commands, then build an MTA and use the MTA plugin automated blue-green deployment: [update](../08_update)
