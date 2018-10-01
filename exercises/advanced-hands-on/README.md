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

:two: **Clone the sample application**

Clone also the **advanced** branch of the product list application: [clone advanced branch](../11_clonebranch)

:three: **Secure your app and push it into the cloud**

Secure the Product List application, configure the OAuth 2.0 Client Credentials Grant (service to service communication) and the OAuth 2.0 Authorization Code Grant (human to service communication):  [secure](../09_secure)

:four: **(Optional) Connectivity**

Use Cloud Connector and Connectivity service to retrieve data from on-premise backend systems [connectivity](../10_connectivity)

:five: **(Optional) Scale**

Explore different options for application scaling that are available in the SAP Cloud Platform Cloud Foundry Environment.  [scale](../07_scale)

:six: **(Optional) Update**

Blue-green deployment applied for update of the application - manually with CF CLI commands, then build an MTA and use the MTA plugin automated blue-green deployment: [update](../08_update)
