# Scale the application

## Estimated time

:clock4: 20 minutes

## Objective

In this exercise you'll learn how you can scale up and down your application.

# Exercise description

Factors such as user load, or the number and nature of tasks performed by an application, can change the disk space and memory the application uses. For many applications, increasing the available disk space, memory or launching more instances (CPUs) can improve overall performance. Similarly, running additional instances of an application can allow the application to handle increases in user load and concurrent requests. These adjustments are called scaling an application.

You can scale an application up or down to meet changes in traffic or demand manually via CF CLI or Cockpit and also automatically using the SAP Cloud Platform Application Autoscaler service.


## Prepare the stage for scaling

In case you have more than one application already running in the account, we need to clean up a bit to be sure we have quota left to try out the scaling options. What you need is one instance of the SpringBoot Product List application running in the account.
- Open the command line
- List all applications in your target space
```
cf apps
```
- If there are more applications besides one Product List app running in the space, stop them to free memory quota for the next exercises - executing the stop command for each running application
```
cf stop APP_NAME
```
:bulb:**Note:** Prior continuing with the next exercises make sure you have one instance of the Product List application running in the account. You can clone the sample from master branch and push it.

## Vertical scale

Vertically scaling an application changes the disk space limit or memory limit that Cloud Foundry applies to all instances of the application.

### CF CLI

Use ```cf scale APP_NAME -k DISK``` to change the disk space limit applied to all instances of your application. DISK must be an integer followed by either an M, for megabytes, or G, for gigabytes.

Use ```cf scale APP_NAME -m MEMORY``` to change the memory limit applied to all instances of your application. MEMORY must be an integer followed by either an M, for megabytes, or G, for gigabytes.

- We want to scale down the application and make our sample application run with less memory (712MB instead of 896MB):
```
cf scale APP_NAME -m 712M
```
Executing this command in CF CLI will cause the application to restart (you should confirm with yes in console that this is ok). Then once the application is running again, you can refresh the application view in cockpit and see that now it uses 712 MB memory. You can see this also in console - the output of scale command or listing the application:
```
cf app APP-NAME
```

### Cockpit
We can do the same operation vertically scaling the applications using cockpit.

Navigate to the the dedicated view for the running application. In the Overview - Quota Information section you see the memory quota and disk quota for your application. You can change this with **Change Quota** button.
<br><br>
![Change Quota](/img/cockpit_change_quota.png?raw=true)
<br><br>
<br><br>
![Change Quota Pop-up](/img/cockpit_change_quota_popup.png?raw=true)
<br><br>

Specify less disk space e.g. 512 MB and click **Save** button. You can check the changed size of disk the application uses.
<br><br>
![Check disc quota](/img/cockpit_scale_disc.png?raw=true)
<br><br>

In case the change is not reflected, you have to manually restart the application for the change to take effect - you can do it with the **Restart** button.
<br><br>
![Restart](/img/cockpit_restart.png?raw=true)
<br><br>

## Horizontal scale

Horizontally scaling an application creates or destroys application instances. Incoming requests to the application are automatically load balanced across all application instances, and each instance handles tasks in parallel with every other instance. Adding more instances allows your application to handle increased traffic and demand.

### CF CLI
Use ```cf scale APP_NAME -i INSTANCES``` to horizontally scale your application. This will lead to increased or decreased number of instances of your application to match INSTANCES.
- We can scale up horizontally the sample application and have 2 instances running. In the command line:
```
cf scale APP_NAME -i 2
```

Executing the command to list information for the application will already show you one running instance and one starting:
```
cf app APP_NAME
```
<br><br>
![Restart](/img/scaling_console.png?raw=true)
<br><br>

### Cockpit
You can also scale horizontally via Cockpit. Navigate in cockpit to the sample application view. You should see in the instances section the two running instances. On the top there are **+ Instance** and **- Instance** buttons. Click on **- Instance** which should result in stopping one of the running instances of the app.
<br><br>
![Restart](/img/cockpit_scaling.png?raw=true)
<br><br>

## Auto-scale

We explored the manual options for scaling up and down an application using the ```cf scale``` command or via Cockpit. The drawback using these is that only if you observe that the performance of your application has degraded, you perform this activity. Your application will crash if action is not taken at the appropriate time. So  it's sometimes better to have a scaling option that is triggered automatically when needed and prevents the application from crashing.

You will learn now how to scale your application up or down automatically based on user defined scaling policies to meet changes in traffic or demand using the SAP Cloud Platform Application Autoscaler service.

### Generate load on the application
We will explore the automatic scaling based on increased memory usage, so that's why we will prepare a simulation that will increase the memory usage of our sample product list application.
- Open **class Controller** and add the following **API endpoint**. This will create a high number of unwanted objects thereby increasing the memory usage
```java
	@GetMapping("/scaleup")
	public String scaleUp() {
		String str = "";
		HashMap<String, Double> h = null;
	    for (int i = 0; i < 100000; i++) {
			h = new HashMap<>();
			String key = new String("key");
			Double value = new Double(100.98);
	        h.put(key, value);
			str = str + h.toString();
	    }
		return "success";
	}
```
Organize imports -> add `import java.util.HashMap;`
Next, let us add an action to trigger the API which will increase memory usage.
- Navigate to `src/main/resources/index.html` and add the below snippet right below the comment ```Insert the code to increase the memory of the application```
```javascript
/* Adding Toolbar to the UI */
var toolBar = new sap.m.Toolbar();
var memButton = new sap.m.Button({
    title: "Scale Up",
    press: function (oEvent) {
        jQuery.sap.require("sap.m.MessageBox");
        sap.m.MessageBox.show(
            "This will scale up the memory of the application", {
                icon: sap.m.MessageBox.Icon.INFORMATION,
                title: "ScaleUp Application",
                actions: [sap.m.MessageBox.Action.OK],
                onClose: function(oAction) {
                    var oScaleModel = new sap.ui.model.json.JSONModel();
                    var scaleUpUrl = document.URL + "scaleup";
                    oScaleModel.loadData(scaleUpUrl);
                    console.log(scaleUpUrl);
                }
            }
        );
    }
});
memButton.setText("Scale Up");
toolBar.addContent(memButton);
productList.setHeaderToolbar(toolBar);
```

Now, let us build the project and deploy the application
- Right click on the project in the Project Explorer view -> Run as -> Maven build
- Go to command prompt and run the command ```cf push```
- Launch the application in the browser
- You will see a new action button **Scale Up**
- Check the memory usage of the application running the command ```cf app <app_name>``` (or in cockpit application view)
- Click on the action button **Scale Up**
- Check the memory usage again after few seconds, run the command ```cf app <app_name>``` (or in cockpit application view)

Now that we have prepared the application to scale up the memory usage, let us see how to use the service, ```Application Autoscaler``` to automatically scale the application when the memory usage increases beyond a threshold.

### Create an instance of the service and define scaling rules
You can do this not only with the CF CLI but also via cockpit. Navigate to you trial space - usually the name of the space is **dev**. In the left-hand navigation menu select **Service Marketplace**. You should see **autoscaler** - click on it.  
<br><br>
![Autoscaler in Marketplace](/img/marketplace_autoscaler.png?raw=true)
<br><br>

Now in the left-hand navigation menu select **Instances**.
Click on the **New Instance** button - a pop-up will appear.
<br><br>
![Autoscaler in Marketplace](/img/autoscaler_newinstance.png?raw=true)
<br><br>

In the first step you have to select a service plan. For trial accounts only lite is available, so click on the **Next** button.
<br><br>
![Autoscaler in Marketplace](/img/create_instance_cockpit_1.png?raw=true)
<br><br>

You can skip this step as you can specify the parameters for scaling while binding the application. So click on the **Next** button.
<br><br>
![Autoscaler in Marketplace](/img/create_instance_cockpit_2.png?raw=true)
<br><br>

Skip this step as well, so, So click on the **Next** button.
<br><br>
![Autoscaler in Marketplace](/img/create_instance_cockpit_3.png?raw=true)
<br><br>

As a last step choose a service instance name e.g. myautoscaler and click **Finish** button.
<br><br>
![Autoscaler in Marketplace](/img/create_instance_cockpit_4.png?raw=true)
<br><br>

This should result in Application Autoscaler instance created.

### Bind the application to the service instance
Once the service instance is created you will be in the App Autoscaler instances view. Click on the instance you just created:
<br><br>
![Autoscaler in Marketplace](/img/bind_instance_cockpit_1.png?raw=true)
<br><br>

On the instances page, click on the **Bind Instance** button:
<br><br>
![Autoscaler in Marketplace](/img/bind_instance_cockpit_1.1.png?raw=true)
<br><br>

The below pop-up opens:
<br><br>
![Autoscaler in Marketplace](/img/bind_instance_cockpit_2.png?raw=true)
<br><br>

Select the sample application from the drop-down Application list.
<br><br>
![Autoscaler in Marketplace](/img/bind_instance_cockpit_3.png?raw=true)
<br><br>

Then you should specify parameters for scaling. The rules to scale an application instance are defined in a JSON format. Usually you will create a JSON file and provide it as configuration. For our sample you can copy and paste the snippet below in the big text field:
```Config
{
    "instance_min_count": 1,
    "instance_max_count": 2,
    "scaling_rules": [
        {
            "metric_type": "memoryused",
            "stat_window_secs": 60,
            "breach_duration_secs": 60,
            "threshold": 400,
            "operator": ">=",
            "cool_down_secs": 60,
            "adjustment": "+1"
        }
    ]
}
```
The threshold value indicates the service to scale up the instance by 1 [adjustment: +1] when the memory goes higher than the threshold value. Adjust this value as required by your application.
You can similarly add another scaling rule to scale down when the memory comes down a defined threshold value.

In the pop-up, choose Specify Parameters. In the Enter Parameters text area, copy the above json. Click on **Save** button
<br><br>
![Autoscaler in Marketplace](/img/bind_instance_cockpit_4.png?raw=true)
<br><br>

Now, let's see how `App Autoscaler` helps the application to scale automatically based on application metrics
1. Restart the application to reset the memory usage (you can navigate in cockpit to `Application Overview` page and click **Restart** button)
2. Launch the application in the browser
3. Check the memory of the application using CF CLI command, `cf app <app_name>` or in Cockpit in the `Application Overview` page
4. Click on the **Scale Up** button
5. Check the memory of the application using CF CLI command, `cf app <app_name>` or in Cockpit in the `Application Overview` page after few seconds.
6. It should have increased, ensure that it is higher than the `threshold` value defined in the scaling policy. If not, repeat steps 3 and 4
7. When memory usage is above the `threshold` value for time higher than the `breach_duration` as specified in the scaling policy, the `App Autoscaler` will scale up the instance of the application by 1
8. Check the number of instances of the application using CF CLI command, `cf app <app_name>` or in Cockpit in the `Application Overview` page
<br><br>
![Autoscaler Starting new app instance](/img/autoscaler_starting_new.png?raw=true)
<br><br>
