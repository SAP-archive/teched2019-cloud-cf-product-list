# Setting up the environment


## Estimated time

:clock4: 10 minutes

## Objective

In this exercise you'll learn how you can create free trial account on the SAP Cloud Platform, start your Cloud Foundry Environment trial, setup local development environment and explore a bit the SAP Cloud Platform cockpit which is the web based administration tool for SAP Cloud Platform.

# Exercise description

## Start your Cloud Foundry Trial

:bulb: **Note:** In case you don't have a trial account on the SAP Cloud Platform yet, follow this [step-by-step-tutorial](http://go.sap.com/developer/tutorials/hcp-create-trial-account.html) to get one.

1. Then in [SAP Cloud Platform cockpit](https://account.hana.ondemand.com/#/home/welcome) navigate to your user home. You should see a button *Start Cloud Foundry Trial*.  
<br><br>
![Start Cloud Foundry Trial](/img/start_cf_trial.png?raw=true)
<br><br>
2. Click on the *Start Cloud Foundry Trial* button – a pop-up will appear. Select a region from the dropdown for your trial and click OK
- If you attend TechEd Las Vegas or TechEd Bangalore select US East (VA) region.
- If you attend TechEd Barcelona, select Europe (Frankfurt) region. 
<br><br>
![Select Trial Region](/img/select_trial_region.png?raw=true)
<br><br>

3. Wait for the procedure to finish. You will get a Global Account, Subaccount, Organization and Space in the region you selected. Once these are created you can navigate to your new Space (click on Go to Space button)
<br><br>
![Select Trial Region](/img/go_to_space.png?raw=true)
<br><br>

## Cockpit
This is a simplified picture of the domain model you have in the Cloud Foundry Environment. If you want to learn more about the different entities, check the [documentation.](https://help.sap.com/viewer/65de2977205c403bbc107264b8eccf4b/Cloud/en-US/8ed4a705efa0431b910056c0acdbf377.html)

<br><br>
![Domain Model Overview](/img/domain_model.png?raw=true)
<br><br>

This is what you see now in Cockpit:
<br><br>
![Cockpit Domain Model Overview](/img/cockpit_domain_model.png?raw=true)
<br><br>

## Clean-up

:bulb: **Note:** There is a limited quota in the Cloud Foundry Environment trial account.

In case you already used the trial account quota, you have to clean-up applications and service instances prior continuing with the exercises. You can do so via cockpit:
- navigate to the list of Applications running in your Space and delete all or stop all running applications;
- go to the Service Instances and delete all.   

## Local development environment

:bulb: **Note:** If you use the TechEd provided laptops then your local development environment tools listed below are already installed. In this case no need to install anything additional. If you are using your own laptop, then make sure you install and setup the tools listed below in case you don't have them.

### Cloud Foundry Command Line Interface (CF CLI)

Install or update the Cloud Foundry Command Line Interface (CF CLI)

#### Install
Download the CF CLI and follow the installation instructions: [CF CLI](https://github.com/cloudfoundry/cli#downloads)

- Windows: download the Windows 64bit installer. The file is downloaded into the folder `C:\Users\student\Downloads`. Extract the archive and run the executable.
- Mac: download the OS X installer. Note: as an alternative - you can use the Homebrew open source package management software to download the CLI.
- Linux: download the Linux installer for your Debian/Ubuntu or Red Hat system

#### Update
Download the [CF CLI](https://github.com/cloudfoundry/cli#downloads) binary and replace the old one with the one you have just downloaded. For Windows, move `cf.exe` to `D:\3rdParty\CloudFoundry\`.

### Eclipse
- You should have the STS (Spring Tool Suite) plugin installed

### Java
Check which Java version is available using `java -version`. The default should be `java version "1.8.0_131"`

### Maven
Check if maven is correctly configured by running `mvn --version`

### Git
Check if git is installed by running `git --version`
