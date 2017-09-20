# Exercise 07: Scale the application

## Estimated time

:clock4: 20 minutes

## Objective

In this exercise you'll learn how you can scale up and down your application.

# Exercise description

Factors such as user load, or the number and nature of tasks performed by an application, can change the disk space and memory the application uses. For many applications, increasing the available disk space or memory can improve overall performance. Similarly, running additional instances of an application can allow the application to handle increases in user load and concurrent requests. These adjustments are called scaling an application. Scaling of an application is needed to optimize resource utilization and to ensure continuation of specific tasks or activities for user benefit.

You can scale your application up or down to meet changes in traffic or demand manually via CF CLI or Cockpit and also automatically using the SAP Cloud Platform Application Autoscaler service.


## Stop the running applications

In case you have more than one application already running in the account, we need to clean up a bit to be sure we have quota left to try out the scaling options. What you need is one instance of the SpringBoot product-list application running in the account.
- Open the command line
- List all applications in your target space
```
cf apps
```
- Stop all running applications to free memory quota for the next exercises executing the stop command for each running application
```
cf stop APP_NAME
```

## Vertical scale

Vertically scaling an application changes the disk space limit or memory limit that Cloud Foundry applies to all instances of the application.

### CF CLI

Use ```cf scale APP_NAME -k DISK``` to change the disk space limit applied to all instances of your application. DISK must be an integer followed by either an M, for megabytes, or G, for gigabytes.

Use ```cf scale APP_NAME -m MEMORY``` to change the memory limit applied to all instances of your application. MEMORY must be an integer followed by either an M, for megabytes, or G, for gigabytes.

- We want to scale down the application and make our sample application run with less memory - 256MB.
```
cf scale APP_NAME -m 256M
```
Executing this command in CF CLI will cause the application to restart (you should confirm with yes in console that this is ok). Then once the application is running again, you can refresh it in the browser and see that now it uses 256MB memory. You can check this also in console, listing the application:
```
cf app APP-NAME
```

### Cockpit
We can do the same operation vertically scaling the applications using cockpit. Navigate to the the dedicated view for the running application. In the Quota Information section you see the memory quota and disk quota for your application. You can change this with **Change Quota** button. Specify less disk space e.g. 512 MB. You have to manually restart the application for the change to take effect - you can do it with the **Restart** button. You can check the changed size of disk the application uses.

## Horizontal scale

Horizontally scaling an application creates or destroys instances of your application. Incoming requests to the application are automatically load balanced across all application instances, and each instance handles tasks in parallel with every other instance. Adding more instances allows your application to handle increased traffic and demand.

### CF CLI
Use ```cf scale APP_NAME -i INSTANCES``` to horizontally scale your application. Cloud Foundry will increase or decrease the number of instances of your application to match INSTANCES.
- We can scale horizontally the sample application and have 2 instances running. In the command line:
```
cf scale APP_NAME -i 2
```

Wait a little and execute the command to list information for the application:
```
cf app APP_NAME
```
The result you see is two running instances.
You can try requesting the application URL in the browser and also incognito tab and you may result accessing the different instances (there is INSTANCE INDEX printed in the application - you should see 0 and 1 in the different browser windows)

### Cockpit
You can also scale horizontally via Cockpit. Navigate in cockpit to the sample application view. You should see in the instances section the two running instances. On the top there are **+ Instance** and **- Instance** buttons. Click on **- Instance** which should result in stopping one of the running instances of the app.

## Auto-scale

//TODO - service binding failed. Check how to show the autoscaler in action when the problem is fixed. If we can show time based scaling, if not we need to load the application to show how autoscaling works.

Scale your application up or down automatically based on user defined scaling policies to meet changes in traffic or demand using the SAP Cloud Platform Application Autoscaler service.

We explored the manual options for scaling up and down an application using the ```cf scale``` command or via Cockpit. The drawback using these is that only if you observe that the performance of your application has degraded, you perform this activity. Your application will crash if action is not taken at the appropriate time. So  it's sometimes better to have a dynamic option that is triggered automatically when needed and prevents the application from crashing.

If you are unsure of the number of application instances that might be required at runtime, use Application Autoscaler. Load variation causes the service to scale up or down your application as per the minimum and the maximum threshold values that you have defined.

A dynamic scale up of application instance ensures that the application does not crash or encounter performance problems as the load increases. As the load reduces, a dynamic scale down ensures that your application utilizes optimal resources. Benefits:
- Enables applications to dynamically adapt to changing application load
- Helps efficient utilization of resources
- Avoids manual intervention while scaling up or scaling down application instances


### Create an instance of the service and define scaling rules
You can do this not only with the CF CLI but also via cokpit. Navigate to you trial space. In the left-hand navigation menu select **Service Marketplace**. You should see **autoscaler** - click on it.  
Now in the left-hand navigation menu select **Instances**.
Click on the **New Instance** button - a pop-up will appear. In the first step you have to select a service plan. For developer accounts only lite is available, so click on the **Next** button. Then you should specify parameters for scaling. The rules to scale an application instance are defined in a JSON format. Usually you will create a JSON file and provide it as configuration. For our sample you can copy and paste the snippet below in the big text field:
```Config
{
    "instance_min_count": 1,
    "instance_max_count": 3,
    "scaling_rules": [
        {
            "metric_type": "memoryused",
            "stat_window_secs": 30,
            "breach_duration_secs": 60,
            "threshold": 90,
            "operator": ">=",
            "cool_down_secs": 300,
            "adjustment": "+1"
        }
    ]
}
```

Now click **Next**
Select the sample application from the drop-down Application list. Click on the **Next** button.
As a last step choose a service instance name e.g. myautoscaler and click **Finish** button.
