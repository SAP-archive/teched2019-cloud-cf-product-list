# Update the application

## Estimated time

:clock4: 15 minutes

## Objective

In this exercise you'll learn how you can update an application using blue-green deployment mechanism. In case your application is more complex we will explore also the option to build an Multi-Target Application archive, deploy it and benefit from the automated blue-green deployment with MTAs.

# Exercise description
To show-case the update we need two running instances of the application at the same time and to achieve this in the limits of the trial account we need only one Product List application running in the beginning. Then we will add an updated version.

## Blue-green deployment
Blue-green deployment is a technique that reduces downtime and risk by running two identical production environments called Blue and Green. Let's assume Blue is currently live and Green is idle. As you prepare a new version of your software, deployment and the final stage of testing takes place in the environment that is not live: in this example, Green. Once you have deployed and fully tested the software in Green, you switch the router so all incoming requests now go to Green instead of Blue. Green is now live, and Blue is idle.

<br><br>
![Blue Green 1](/img/blue_green_1.png?raw=true)
<br><br>
![Blue Green 2](/img/blue_green_2.png?raw=true)
<br><br>


This technique can eliminate downtime due to application deployment. In addition, blue-green deployment reduces risk: if something unexpected happens with your new version on Green, you can immediately roll back to the last version by switching back routing the traffic to Blue.

:bulb: **Note:** If your app uses a relational database, blue-green deployment can lead to discrepancies between your Green and Blue databases during an update. To maximize data integrity, configure a single database for backward and forward compatibility.

### Let's see this in action

Prepare a change for the update - let's just change the index.html for the sake of noticing easily the new version of the app with the update. Open the **index.html** file in Eclipse, find the raw where the product list page is created and change the title from Products to GREEN Products:
```java
// create the list page
			var products = new sap.m.Page("products", {
				title : "GREEN Products",
				showNavButton : false,
				content : productList
			});
```
2. Open the application manifest file and change the application name to e.g. changed-product-list and host to something different than the original app e.g. add again prefix changed and save the **manifest.yml**. Run a Maven build after this change (Run As -> Maven build)
3. Open console in the product list sample application root folder (where manifest.yml that you just changed resides) and push the new version under different name: ```cf push```
4. You have two instances of the application running. The "productive" where all the traffic is directed and the new one that you can test accessing via different URL (route). Check that the changed version works as expected accessing it under the different URL - verify the title is now **GREEN Products**.
<br><br>![New app version](/img/changed_app_UI.png?raw=true)
<br><br>
5. Now that both apps are up and running, switch the router so all incoming requests go to the new  app version (Green) and the old (Blue). Do this by mapping the original URL route to the Green application using the cf map-route command ```cf map-route GREEN_APP_NAME DOMAIN -n BLUE_APP_HOSTNAME```
6. After the cf map-route command within a few seconds, the CF Router begins load balancing traffic for the original productive URL between Blue and Green version of the application.
7. Un-map the route to Blue version. Once you verify Green version is running as expected, stop routing requests to Blue version using the cf unmap-route command:
```
cf unmap-route BLUE_APP_NAME DOMAIN -n HOSTNAME
```
After the command the CF Router stops sending traffic to Blue version. Now all traffic for the productive domain is sent to Green version.

To remove temporary route to Green version you can use cf unmap-route. The route can be deleted using cf delete-route or reserved for later use. You can also decommission Blue, or keep it in case you need to roll back your changes.

## MTA and automated blue-green deployment

To continue with the next exercise, you need to cleanup your trial account as there is are limited resources - delete all applications and service instances.

### Build an MTA archive
To build a deployment-ready Multi-Target Application (MTA) we need to define the MTA modules with a deployment descriptor (mta.yaml) and then package the MTA Archive (MTAR) - we will use the [MTA Archive Builder tool](https://help.sap.com/viewer/58746c584026430a890170ac4d87d03b/HANA%202.0%20SPS%2002/en-US/ba7dd5a47b7a4858a652d15f9673c28d.html) for this.

1. You already have the MTA Archive Builder tool available - mta.jar (file location). Copy it next to the root folder of your application
2. Create an empty mta.yaml file in the root folder of you application.
3. Now besides the application you can specify the service instances to be bound to the application via the mta.yaml.

```Configuration
_schema-version: "2.0.0"
ID: product-list
version: 1.0.0

modules:
  - name: product-list
    type: java
    path: .
    parameters:
      memory: 812M
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
4. Build the MTAR
You need to run the MTA archive builder tool. Open a command line in the root folder of your Product List application. The you need to execute in console is: java -jar [path to mta.jar] --build-target=CF build
```
java -jar ../mta.jar --build-target=CF build
```

As result the mtar archive s created in the root folder of your application. In my case the name is product-list.mtar but it may differ for your local environment - you can see it in the end of the command or just check in the root folder of your app.

### Deploy MTA

To deploy the MTAR we need the MTA CF CLI plugin, download the MTA CF CLI plugin from [here](https://tools.hana.ondemand.com/#cloud)

Prerequisite: You are logged in to the Cloud Foundry Environment via command line. Refer to Target & Login from *push* exercise.

1. Deploy the MTAR product-list.mtar

	```
	cf deploy product-list.mtar
	```
2. As a result the application is deployed and service instances are created. You check that the mta is deployed via command in CF CLI:

	```
	cf mtas
	```
You can also verify that the application is up and running via: ```cf apps```

### Automated blue-green deployment
Request the application - you now have the GREEN Products title version of it running.

Prepare the change for the update - let's again change the index.html for the sake of noticing easily the new version of the app with the update. Open the **index.html** file in Eclipse, find the raw where the product list page is created and change the title from Products to GREEN Products:
```java
// create the list page
			var products = new sap.m.Page("products", {
				title : "MTA Blue/Green Products",
				showNavButton : false,
				content : productList
			});
```
1. Build again the MTAR to get the new version of the application packaged for deployment.

	```
	java -jar ../mta.jar --build-target=CF build
	```
2. Deploy the MTAR using blue/green deployment command:

	```
	cf bg-deploy MTAR_FILE
	```
When the command succeeds, you will get in the console similar output:

```
Application "product-list-green" started and available at "Here you will find URL of the GREEN application version"
```
Open it in a browser to see the change of the title is applied.

What the command did is determining there's already a version of the MTA deployed, deployed the new version and registered a temporary route so that it can be validated. If the test was successful you can now bind the new app version to the official route.
```cf map-route GREEN_APP_NAME DOMAIN -n BLUE_APP_HOSTNAME```
