# cloud-cf-product-list-sample

Sample application running on SAP Cloud Platform Cloud Foundry environment and using some of the services.

This tutorial shows how to...
* get access to SAP Cloud Platform Cloud Foundry environment trial account
* develop from scratch an application and enhance it with additional functionality using basic concepts of Cloud Foundry like backing services
* work with your Cloud Foundry trial account and application using the SAP Cloud Platform cockpit and Cloud Foundry Command Line Interface (CF CLI)
* use application frameworks like Spring and Spring Boot to efficiently develop applications
* monitor, scale and update your application.

# Scenario

Put yourself in the shoes of a developer... Your boss comes one morning and tells you that you need to develop a new eCommerce site so your company can sell products in a modern way. Your company has already selected and uses SAP Cloud Platform but you don't yet have much experience with it and for sure you're pretty new to the recently introduced Cloud Foundry environment. You've heard that this is the proper choice for cloud-native applications development, so you should get started pretty fast and explore it. Your boss wants to get as soon as possible a prototype of the eCommerce site running on SAP Cloud Platform so that he can show it to the sales department and discuss the next steps. The initial requirements are:
* Have a basic UI that displays the list of products available in a product catalogue.
* When you select a product you get a dedicated view about it.

So you decide you will develop a prototype application with SpringBoot.

You already have experience with such projects that start like PoC and easily evolve into productive application, so you want to explore the options for monitoring and operating the application. You know that once the sales team sees the prototype they will want it released as soon as possible and will of course think it's almost ready for production, but you want to know what will it mean for you to prepare the application for efficient DevOps before it's launched. So you want to see what are the options to observe the application, scale it and update it.

As a next step you will have to secure the Product List application, configure oAuth and trust. Then extend the application to consume connectivity service and access on-premise systems exposed by Cloud Connector.

# Components Overview
<br><br>
![Components Overview](/img/overview_components.png?raw=true)
<br><br>

The involved software components shown in the figure above are:

**Development Environment & Tools**

To go through the exercises you will need these components setup in your local dev environment. If you use a TechEd provided laptop then these should be already installed and configured there.

Mini CodeJam and Basic Hands-on
- Eclipse
- CF CLI
- Maven, Git, Java

Advanced Hands-on
- MTA Archive Builder
- SAP Cloud Connector

SAP Cloud Connector

SAP Cloud Platform cockpit


## Basic Hands-on session

**Product List application**

This is the sample application that will be developed and enhanced during the exercises. It will run on SAP Cloud Platform Cloud Foundry environment using different services like e.g. PostgreSQL for persistence layer in the cloud, Application Logging, etc. It's a SpringBoot application and will have a simple UI written with SAPUI5.

**PostgreSQL**

PostgreSQL, the open source object-relational database management system. It is an enterprise-class, ACID compliant database. It is available for consumption as a backing service in the Cloud Foundry environment.

**Application Logging service**

You can create, access, and analyze application logs in  Cloud Foundry environment using the Application Logging Service. It is based on the open source logging platform Elasticsearch, Logstash, Kibana (the Elastic Stack). You can have both application logs that originate from the Cloud Foundry components and logs explicitly issued by the application itself. Your application requires some preparation before application logs can be streamed to the Application Logging service.

**Application Autoscaler service**

Application Autoscaler is a service designed to automatically scale up or scale down bound application instances based on user-defined policies. A dynamic scale up of application instance ensures that the application does not crash or encounter performance problems as the load increases. As the load reduces, a dynamic scale down ensures that your application utilizes optimal resources.

## Advanced Hands-on session

In addition to the components described above in the advanced exercises we will explore more services and will see how to consume and configure these to add more sophisticated functionality to our Product List sample application.

**UAA service**

The User Account and Authentication (UAA) service is a multi-tenant identity management service, used in Cloud Foundry. Its primary role is as an OAuth2 provider, issuing tokens for client applications to use when they act on behalf of Cloud Foundry users. It can also authenticate users with their Cloud Foundry credentials, and can act as an SSO service using those credentials (or others). It has endpoints for managing user accounts and for registering OAuth2 clients, as well as various other management functions.


**Application Router**

Business application embracing microservice architecture, are decomposed into multiple services that can be managed independently. This approach brings some challenges for application developers, like handling security in a consistent way and dealing with same origin policy. The application router is a separate component that exposes the endpoint accessed by a browser to access the application and addresses some of these challenges. It provides three major functions:
- Reverse proxy - provides a single entry point to a business application and forwards user requests to respective backend services
- Serves static content from the file system
- Security – provides security related functionality like login, logout, user authentication, authorization and CSRF protection in a central place.


**Connectivity Service**

The connectivity service provides a standard HTTP Proxy for on-premise connectivity that is accessible by any application. Proxy host and port are available as environment variables. Applications are responsible to propagate the user JWT token via the SAP-Connectivity-Authentication header. This is needed by the connectivity service in order to open a tunnel to the subaccount for which a configuration is made in the Cloud Connector.

# Sessions

:one: **Mini CodeJam (1 hour)**

In this session, you will develop a simple SpringBoot application, run it locally and on SAP Cloud Platform Cloud Foundry Environment. You will explore what you see for the application in SAP Cloud Platform cockpit and via CF CLI, check basic logs and metrics, etc.

Detailed steps description for the session: [Mini CodeJam](/exercises/basic-codeJam)

:two: **Basic Hands-on (2 hours)**

In this session, you will develop a simple SpringBoot application, run it locally and then push to SAP Cloud Platform Cloud Foundry Environment. After you explore what you see for the application and check basic logs and metrics, you will enhance the application with supportability features e.g. integration with Application Logging service. You will also see how you can scale the application.

Detailed steps description for this session: [Basic Hands-on](exercises/basic-hands-on)

:three: **Advanced Hands-on (2 hours)**

In this session you will explore a bit more sophisticated enhancements and operations of the sample Product List application:
* Explore how you can update applications running in the Cloud-Foundry Environment, using blue/green deployment approach.
* Create an MTA archive and check how the deployment and update is simplified in case you have a more complex application that consists of multiple modules and uses multiple services.
* Secure the Product List application, configure the OAuth 2.o Client Credentials Grant (service to service communication) and the OAuth 2.o Authorization Code Grant (human to service communication).
* Extend the application to consume connectivity service and access on-premise systems exposed by Cloud Connector

Detailed steps description for this session: [Advanced Hands-on](exercises/advanced-hands-on)
