#  Mini CodeJam - 1 hour

This tutorial shows how to:
* get access to SAP Cloud Platform Cloud Foundry Environment trial
* work with your Cloud Foundry Environment trial using the SAP Cloud Platform cockpit and Cloud Foundry CLI
* use basic concepts of Cloud Foundry Environment, like backing services
* observe what your application has done and is doing

# Exercises

:one: **Setup the environment**

In this exercise, you will sign up for a free SAP Cloud Platform Cloud Foundry Environment trial account which you can use to deploy and run the application. You will also setup your development environment and tools: [setup](../01_setup)

:two: **Clone the sample application**

Clone the target version of the application that will be shown during the session. As a result you will have the target version of the application imported in Eclipse in case you need it as a reference or to copy easily some snippets: [clone](../02_clone)

:three: **Push to cloud**

In this exercise, you will push the Product List application to your Cloud Foundry Environment trial, change the application to use PostgreSQL service for persistence layer: [push](../04_push)

:four: **(Optional) Observe the application**

Check what information is available for the running application via CF CLI and SAP Cloud Platform cockpit: application events, logs, metrics, service instances, etc.: [observe](../05_observe)

:five: **(Optional) Blue-Green Deployment**

Reducing downtime is important. Learn how to achieve that with the "blue green" deployment approach: [bluegreen](../13_bluegreen)
