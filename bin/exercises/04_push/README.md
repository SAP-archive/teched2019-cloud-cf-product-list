# Exercise 04: Push the application to SAP Cloud Platform Cloud Foundry environment

## Estimated time

:clock4: 10 minutes

## Objective
In this exercise you'll learn how you can run your application on the SAP Cloud Platform in your developer account for Cloud Foundry environment. We will use the Cloud Foundry Command Line Interface. Open a command prompt and follow the steps below.

# Exercise description
## Target & Login

1. You need to tell the CF CLI which Cloud Foundry you will use. To do this you have to set the API endpoint to the Cloud Controller of the Cloud Foundry region where you created your Cloud Foundry trial using ```cf api CLOUD_FOUNDRY_API_ENDPOINT```:
```
cf api https://api.cf.us20.hana.ondemand.com
```

 :bulb: **Note:** You can find the API endpoints for the different regions where Cloud Foundry environment is available in the [SAP Cloud Platform Documentation](https://help.sap.com/viewer/65de2977205c403bbc107264b8eccf4b/Cloud/en-US/350356d1dc314d3199dca15bd2ab9b0e.html)

2. Login with your user account. At the command prompt type:
```
cf login
```
  You will be prompted to fill in the e-mail and password you used when you registered for the SAP Cloud Platform developer account:
```
Email> enter your e-mail
Password> password for your user
```
3. You have to select the Cloud Foundry organization and space that you will use type following command:
```
cf target -o ORGANIZATION -s SPACE
```
:bulb: **Note:** If you are assigned to only one Cloud Foundry organization and space, the system automatically targets you to the relevant Cloud Foundry organization and space once you log on and you will see it in the previous step under OK.
## Application Manifest
When you push an application to the Cloud Foundry environment you can either provide parameters in CF CLI to the push command or define an application deployment descriptor file (manifest.yml) and summarize there the push parameters instead of typing these every time you push. For more information regarding application manifest file you can check the [Cloud Foundry documentation](https://docs.cloudfoundry.org/devguide/deploy-apps/manifest.html)
- Now we will create an application manifest.yml file for our sample product list application: In the project explorer in Eclipse -> right click -> create file and name it **manifest.yml**. Now open the file and insert the following snippet adjusting it to your application:
:bulb: **Note** Path should point to the jar file produced in you project's target folder as an outcome of the Maven build, so maybe you will have to replace the name of the jar with your jar file name
:bulb: **Note** Host should be unique as the host namespace is shared with all applications within Cloud Foundry, so add at the end your working station number.

```Configuration
 applications:
 # Application
 - name: product-list
   instances: 1
   memory: 896M
   host: product-list-xxxx
   path: target/my-product-list-0.0.1-SNAPSHOT.jar
   buildpack: https://github.com/cloudfoundry/java-buildpack.git#v4.3
```
**Save** the file after editing.

## Push
- At the command prompt, go to the root directory of your SpringBoot application. Type the following command:
```
cf push
```
This triggers the application deployment process using the application manifest.yml you just created. The fist step is uploading the application binaries to the cloud. In the next phase, the so called staging happens which results into a :droplet: droplet. Then the release phase tells the Cloud Foundry how to start the application. In our example this will finish without an error and our application will use in-memory persistence in the cloud.Once the command finishes with sucess you can request the application in a browser - copy and paste the url printed in console.

## Services
As a next step we will see how to use a backing service in the cloud (PostgreSQL) as a persistence layer for our example instead of in-memory persistence. We have to create a service instance and bind it to the application. Follow the steps below.

1. List the services available for your user with ```cf marketplace```
You should see a list of services, short description and information for the service plans.

2. In our example we will use a PostgreSQL. To list more information about concrete service, use ```cf marketplace -s SERVICE```:
```
cf marketplace -s postgresql
```
3. Before your application can use a service, you need to create a service instance. Create a PostgreSQL service instance with ```cf create-service SERVICE PLAN SERVICE_INSTANCE```:
```
cf create-service postgresql v9.4-dev postgres
```
4. You should now see your newly created postgresql service. Type in the command prompt:
```
cf services
```
You should see in the output the list of services within your space.
5. The last step is to bind the service instance to your application. You can do it with command in CF CLI ```cf bind-service APP_NAME SERVICE_INSTANCE```
6. Now that we already have the backing service to ensure that our application environment gets updated we re-stage our application so that the app is wired to use PostgreSQL for persistence. Use the following command: ``` cf restage APP_NAME```

Once the application is already running, copy the URL and request it in a browser.

:bulb: **Note** You can bind services to your application via the application manifest. (check if that's the case and re-stage is not sufficient in this case) When you use manifest.yml to bind services you have to push the application again so that the environment gets updated and the app can use the service. E.g. for this sample you have to add this snippet and then cf push the application again:
```Config
services:
 - postgres
```

:bulb: **Note** In case you used another name when you created the service instance, replace the postgres with it.
