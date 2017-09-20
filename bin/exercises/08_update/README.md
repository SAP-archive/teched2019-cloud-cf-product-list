# Exercise 08: Update the application

## Estimated time

:clock4: 15 minutes

## Objective

In this exercise you'll learn how you can...

# Exercise description
Same as for the scaling exercise, to show-case the update we need two running instances of the application at the same time and to achieve this in the limits of the developer account, we will use again the sample Node.js application: https://github.com/SAP/cf-sample-app-nodejs

## Blue-green deployment
Blue-green deployment is a technique that reduces downtime and risk by running two identical production environments called Blue and Green. At any time, only one of the environments is live, serving all production traffic.

:link:[Blue-Green steps](https://docs.cloudfoundry.org/devguide/deploy-apps/blue-green.html)

Let's assume Blue is currently live and Green is idle. As you prepare a new version of your software, deployment and the final stage of testing takes place in the environment that is not live: in this example, Green. Once you have deployed and fully tested the software in Green, you switch the router so all incoming requests now go to Green instead of Blue. Green is now live, and Blue is idle.

This technique can eliminate downtime due to application deployment. In addition, blue-green deployment reduces risk: if something unexpected happens with your new version on Green, you can immediately roll back to the last version by switching back routing the traffic to Blue.

:bulb: **Note:** If your app uses a relational database, blue-green deployment can lead to discrepancies between your Green and Blue databases during an update. To maximize data integrity, configure a single database for backward and forward compatibility.

### Let's see this in action

//TODO - use SpringBoot application, not Node.js

Prepare a change for the update: let's add a new endpoint to the application that prints some text. To do so, open **server.js** file and add the following snippet:
```Node
app.get('/ready', function(req, res) {res.send('Working!')})
```
2. Open the application manifest file and change the application name to e.g. changed-cf-nodejs and save the manifest.yml
3. Open console in the node.js sample application root folder (where manifest.yml that you just changed resides) and push the new version under different name: ```cf push```
4. Now you have two instances of the Node.js application running. The "productive" where all the trafic is directed and the new one that you can test accessing via different URL. Check that the changed version works as expected accessing it under the different URL.
5. Now that both apps are up and running, switch the router so all incoming requests go to the new  app version (Green) and the Blue app. Do this by mapping the original URL route to the Green application using the cf map-route command ```cf map-route GREEN_APP_NAME DOMAIN -n BLUE_APP_HOSTNAME```
6. After the cf map-route command:
The Cloud Foundry Router continues sending traffic for temporary URL to Green.
Within a few seconds, the CF Router begins load balancing traffic for the original productive URL between Blue and Green version of the application.
7. Unmap the route to Blue version
Once you verify Green version is running as expected, stop routing requests to Blue version using the cf unmap-route command:
```
cf unmap-route BLUE_APP_NAME DOMAIN -n HOSTNAME
```
After the command the CF Router stops sending traffic to Blue version. Now all traffic for the productive domain is sent to Green version.

To remove temporary route to Green version you can use cf unmap-route. The route can be deleted using cf delete-route or reserved for later use. You can also decommission Blue, or keep it in case you need to roll back your changes.

## MTA and automated blue-green deployment

//TODO: Clarification how the mta.jar file can be made available to participants

### Build an MTA archive
1. Get the mta.jar file from //TODO and copy it to next to the root folder of your application
2. Create an empty mta.yml file in the root folder of you application
3. Now besides the application you can specifiy the service instances to be created and bound to the application via the mta.yml.
```Configuration
_schema-version: "2.0.0"
ID: product-list
version: 1.0.0

modules:
  - name: product-list
    type: java
    path: .
    parameters:
      memory: 496M
      buildpack: java_buildpack
    properties:
      SPRING_PROFILES_ACTIVE: cloud
    provides:
      - name: product-list
        properties:
          url: "${default-url}"
    requires:
      - name: postgres
      - name: logging
    build-parameters:
      build-result: target/*.jar

resources:
  - name: postgres
    type: postgresql
  - name: logging
    type: application-logs
    parameters:
      service-plan: lite
```
**Save** the file after editing.

4. Build your mta archive
```
java -jar ../mta.jar --build-target=CF build
```

As result the archive product-list.mtar is created in the root folder of your application.

### Deploy MTA
Prequisite: You are logged in to the Cloud Foundry environment via command line. Refer to Target & Login from previous exercise.

1. Deploy the mta archive product-list.mtar
```
cf deploy product-list.mtar
```

2. As a result the application is deployed. You can verify that the application is up and running via
```
cf a 
```

### Show the automated blue-green deployment
1. Deploy the mta archive product-list.mtar
```
cf bg-deploy product-list.mtar
```
