# Cloning sample from Github

## Estimated time

:clock4: 5 minutes

## Objective

In this exercise you'll learn how you can clone the target version of the Product List sample application, import it in your local Eclipse environment and build it. This is the target source code of the Product List application that you will actually develop as part of the basic hands-on session starting from scratch. We will it in the basic hands-on session as a reference or to copy easily some snippets or files.

# Exercise description

## 1 Download the sample code as zip
1. Go to the root of the project in github: https://github.com/SAP/cloud-cf-product-list-sample
2. Click on **Clone or download** button and select Download zip
<br><br>
![Download ZIP](/img/github_download_zip.png?raw=true)
<br><br>

3.  Unzip the downloaded file in your students directory - a new directory ```cloud-cf-product-list-sample``` will be created.

## 2. Import the sample project into Eclipse
1. Open the Windows Start menu and enter ```CPL...``` in the input field. Under ```Programs``` you will see ```ABAP in Eclipse - CPL...```. Click on this entry to open Eclipse.
2. Now import the target state of the sample project as Maven project into your Eclipse workspace: In the Eclipse menu, chose ```File```> ```Import...```.
3. In the ```Import``` wizard, select ```Maven``` > ```Existing Maven Projects``` and click ```Next```.
<br><br>
![Import Maven Project](/img/import_maven_project.png?raw=true)
<br><br>

4. In the next step of the ```Import Maven Projects``` popup, click ```Browse```, navigate into the unziped root folder of ```cloud-cf-product-list-sample-master``` project in your student directory folder (```D:\Files\Session\CPL...```), then click ```Finish```.
5. The project is now imported in Eclipse. You should see the project in the Project Explorer like in the screenshot below.  
<br><br>
![Import Maven Project](/img/imported_project_eclipse.png?raw=true)
<br><br>

## 3. Build the project in Eclipse using Maven  

1. Now build the project in Eclipse: Select the project in the Project Explorer, open its context menu and click on ```Run As``` > ```Maven build...```.
<br><br>
![Import Maven Project](/img/run_maven_build.png?raw=true)
<br><br>

2. In the ```Edit Configuration```popup, enter ```clean install``` into the ```Goals```field and click ```Run```. The Maven build should finish without errors.
<br><br>
![Import Maven Project](/img/maven_clean_install.png?raw=true)
<br><br>
