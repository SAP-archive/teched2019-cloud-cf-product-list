# Exercise 02: Cloning sample from Github

## Estimated time

:clock4: 5 minutes

## Objective

In this exercise you'll learn how you can clone the target version of the Product List sample application, import it in your local Eclipse environment and build it.

# Exercise description

You can either download the application as zip 1.1 or clone it using the git client in the local dev environment 1.2.

## 1.1 Download the sample code as zip
1. Go to the root of the project in github: https://github.com/SAP/cloud-cf-product-list-sample
2. Click on **Clone or download** button and select Download zip
<br><br>
![Download ZIP](/img/github_download_zip.png?raw=true)
<br><br>

3.  Unzip the downloaded file in your students directory - a new directory ```cloud-cf-product-list-sample``` will be created.


## 1.2 Clone the sample code from github
1. Switch to the terminal on your computer. For that please:
	* press the Windows key and the 'R' key
	* type ```cmd``` into the input field and press the return key
2. Now switch to your **student directory folder** at D:\Files\Session\CPL...\. Enter:
   ```
   D:
   cd D:\Files\Session\CPL...\
   ```
3. Now type ```git clone https://github.com/SAP/cloud-cf-product-list-sample.git``` and press the return key.
4. A new directory called ```cloud-cf-product-list-sample``` was created in your student directory folder.


## 2. Import the sample project into Eclipse
1. Open the Windows Start menu and enter ```CPL...``` in the input field. Under ```Programs``` you will see ```ABAP in Eclipse - CPL...```. Click on this entry to open Eclipse.
2. Now import the target state of the sample project as Maven project into your Eclipse workspace: In the Eclipse menu, chose ```File```> ```Import...```.
3. In the ```Import``` wizard, select ```Maven``` > ```Existing Maven Projects``` and click ```Next```.
4. In the next step of the ```Import Maven Projects``` popup, click ```Browse```, navigate into the ```cloud-cf-product-list-sample``` project in your student directory folder (```D:\Files\Session\CPL...```), then click ```Finish```.
5. The project is now imported in Eclipse. You should see the project in the Project Explorer like in the screenshot below.   

## 3. Build the project in Eclipse using Maven  

1. Now build the project in Eclipse: Select the project in the Project Explorer, open its context menu and click on ```Run As``` > ```Maven build...```.
2. In the ```Edit Configuration```popup, enter ```clean install``` into the ```Goals```field and click ```Run```. The Maven build should finish without errors.
