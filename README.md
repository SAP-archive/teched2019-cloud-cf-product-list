# Secure Microservices in Cloud Foundry Environment on SAP Cloud Platform

In this session you will learn how to secure Microservices in Cloud Foundry Environment on SAP Cloud Platform. 
Secure the Product List application and configure the OAuth 2.0 Authorization Code Grant (human to service communication). 


This tutorial shows how to...
* get access to SAP Cloud Platform Cloud Foundry Environment trial account
* secure the Product List application and configure the OAuth 2.0 Authorization Code Grant

# Scenario

Secure the Product List application by using a flexible authorization framework - OAuth 2.0. The authorization code grant of OAuth 2.0 provides an excellent security mechanism to grant only authorized users access to your application and its data. With the SAP XS Advanced Application Router, the SAP XSUAA OAuth authorization service and Spring Boot you have outstanding tools at your fingertips to configure roles, assign them to users and, finally, implement role checks in your application.

# Understanding OAuth 2.0 Components

To better understand the content of this repository, you should gain a rough understanding about the SAP CP OAuth 2.0 components, which are depicted in figure below.

<br><br>
![OAuth 2.0 Components Overview](/docs/img/overview_oauth2_components.png?raw=true)
<br><br>


#### OAuth Resource Server
First, we still have a **microservice**, in our case the **Product List application** that we want to secure. In OAuth terminology this is the **Resource Server** that protects the resources by checking the existence and validity of an OAuth2 access token before allowing a request from the Client to succeed.

#### OAuth Access Token (JWT)
Access and refresh tokens in the form of **JSON Web Token (JWT)** represent the user’s identity and authorization claims. If the access token is compromised, it can be revoked, which forces the generation of a new access token via the user’s refresh token.

**Example JWT** [[rfc7519](https://tools.ietf.org/html/rfc7519)]
```json
{
  "client_id": "sb-xsapplication!t895",
  "cid": "sb-xsapplication!t895",
  "exp": 2147483647,
  "user_name": "John Doe",
  "user_id": "P0123456",
  "email": "johndoe@test.org",
  "zid": "1e505bb1-2fa9-4d2b-8c15-8c3e6e6279c6",
  "grant_type": "urn:ietf:params:oauth:grant-type:saml2-bearer",
  "scope": [ "xsapplication!t895.Display" ],
  "xs.user.attributes": {
    "country": [
      "DE"
    ]
  }
}
```

#### OAuth Authorization Server
The **Extended Services for User Account and Authentication (XSUAA service)** is a multi-tenant identity management service. Its primary role is as an **OAuth Authorization Server**, issuing authorization codes and JWT tokens after the user was successfully authenticated by an identity provider with their Cloud Foundry credentials. Furthermore it can act as an SSO service using those credentials (or others). It has endpoints for managing user accounts and for registering OAuth 2.0 clients, as well as various other management functions.

#### OAuth Client
The **Application Router (approuter)** is an edge service that provides a single entry point to a business application that consists of several backend microservices. It acts as reverse proxy that routes incoming HTTP requests to the configured target microservice, which allows handling Cross-origin resource sharing (CORS) between the microservices. It plays a central role in the OAuth flow.

Just like HTTP, token-based authentication is stateless, and therefore for scalability reasons an OAuth Resource Server must not store a JWT. The consequence would be that the JWT is stored client side as it must be provided with every request. Here, the Application Router takes over this responsibility and acts an **OAuth Client** and is mainly responsible for managing authentication flows.

The Application Router takes incoming, unauthenticated requests from users and initiates an OAuth2 flow with the XSUAA. After the user has successfully logged on the Identity Provider the XSUAA considers this request as authenticated and uses the information of the Bearer Assertion to finally create a JWT containing the authenticated user as well as all scopes that he or she has been granted. Furthermore the Application Router enriches each subsequent request with the JWT, before the request is routed to a dedicated microservice (instance), so that they are freed up from this task.


> In this flow it is important to notice that the JWT never appears in the browser as the Application Router acts as OAuth client where the user “authorizes” the approuter to obtain the authorizations - the JWT - from the XSUAA component.

#### Conclusion

You need to configure the Application Router for your business application. Note that the Application Router can be bypassed and the microservice 
can directly be accessed. So the backend microservices must protect all its endpoints by validating the JWT access token and implementing proper scope checks.

In order to validate an access token, the JWT must be decoded and its signature must be verified with one of the JSON Web Keys (JWK) such as public RSA keys. Furthermore the claims found inside the access token must be validated. For example, the client id (`cid`), the issuer (`iss`), the audience (`aud`), and the expiry time (`exp`).  
Hence, every microservice has to maintain a service binding to the XSUAA that provides the XSUAA url as part of `VCAP_SERVICES` to get the current JWKs and has to configure the XSUAA as OAuth 2.0 Resource Server with its XSUAA access token validators by making use of one of SAP's Container Security Libraries.


# Development  & Tools

To go through the exercises you will need these components in your local development environment. 
If you use a TechEd provided laptop then they should be already installed and configured there.

- Eclipse
- CF CLI
- Maven, Git, Java

# Exercise Steps

:one: **Setup the environment**

In this exercise, you will start a free SAP Cloud Platform Cloud Foundry Environment trial which you can use to deploy and run applications.

[Start the Exercise](/docs/01_setup/README.md)

:two: **Download the sample application**

Clone the Product List sample application from Github and import it in into your Eclipse IDE. This sample application is secured along this session. 

[Start the Exercise](/docs/02_clone/README.md)

:three: **Secure your application and push it into the cloud**

Secure the Product List application.

[Start the Exercise](/docs/09_secure/README.md)

# Further References
- [sap.help.com: Authorization and Trust Management](https://help.sap.com/viewer/product/CP_AUTHORIZ_TRUST_MNG/Cloud/en-US)
- [sap.help.com: Authorization and Trust Management - Tutorials](https://help.sap.com/viewer/65de2977205c403bbc107264b8eccf4b/Cloud/en-US/902ae800c1d04c7388e407b7815e5cc8.html)

# License
Copyright (c) 2019 SAP SE or an SAP affiliate company. All rights reserved. This project is licensed under the SAP Sample Code License, except as noted otherwise in the [LICENSE](LICENSE) file.