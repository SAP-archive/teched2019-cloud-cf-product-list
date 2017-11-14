# Running the app locally

## Estimated time
:clock4: 5 minutes

# Exercise description
## Build the project and run locally
- If not yet done, right click on the project in the Project Explorer view -> Run as -> Maven build... -> goal: clean install
- Once the build is finished run locally the application: right click on the project in the Project Explorer view -> Run as -> Spring Boot App

Now you can open, for example, the Chrome browser and see the application running on http://localhost:8080/

You should see a list of 3 products and when you click on a product get a dedicated product view with product description.

:bulb: **Note:** In case the default port 8080 that is used to run the application on localhost is occupied by another process, you can go to `application.properties` file in `src/main/resources` folder of the SprigBoot project and change it with the following property:

```Config
server.port=9090
```
