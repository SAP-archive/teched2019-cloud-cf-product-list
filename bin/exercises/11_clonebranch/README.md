# Exercise 11: Cloning sample target from advanced branch

## Estimated time

:clock4: 5 minutes

## Objective

In this exercise you'll learn how to download the target version of the Product List sample application for the advanced session which is not in the master branch but in dedicated advanced branch, then import it in your local Eclipse environment.

# Exercise description

//check where the downloaded zip or git clone are stored on the image

## 1. Download the sample code as zip
1. Go to the root of the project in github: https://github.com/SAP/cloud-cf-product-list-sample
2. Click on **Branch: master** dropdown and select **advanced** branch.
3. Now click on **Clone or download** button and select Download zip
4. Unzip the downloaded file in your students directory - a new directory ```cloud-cf-product-list-sample-advanced``` will be created.

## 2. Import the sample project into Eclipse
1. Open the Windows Start menu and enter ```CPL...``` in the input field. Under ```Programs``` you will see ```ABAP in Eclipse - CPL...```. Click on this entry to open Eclipse.
2. Now import the target state of the sample project as Maven project into your Eclipse workspace: In the Eclipse menu, chose ```File```> ```Import...```.
3. In the ```Import``` wizard, select ```Maven``` > ```Existing Maven Projects``` and click ```Next```.
4. In the next step of the ```Import Maven Projects``` popup, click ```Browse```, navigate into the ```cloud-cf-product-list-sample-advanced``` project in your student directory folder (```D:\Files\Session\CPL...```), then click ```Finish```.
5. The project is now imported in Eclipse. You should see the project in the Project Explorer like in the screenshot below.   
