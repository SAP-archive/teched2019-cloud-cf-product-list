# Exercise 06: Blue-Green Deployment

## Estimated time

:clock4: 10 minutes

## Exercise description

1. Open the `manifest.yml` file and change the application name from `product-list` to `product-list-green`. Change also the host to `product-list-YOUR_BIRTH_DATE-green`

2. Push the application using `cf push`

3. Switch the router so that all incoming requests go to the new application version (green). Enter the original URL route to the green application using the command
    ```
    cf map-route GREEN_APP_NAME DOMAIN -n BLUE_APP_HOSTNAME
    ```
    where `DOMAIN` is:
    - `cfapps.us10.hana.ondemand.com` for TechEd Las Vegas
    - `cfapps.eu10.hana.ondemand.com` for TechEd Barcelona

  **Result:**
  After the `cf map-route` command, the Cloud Foundry router continues sending traffic for temporary URL to the Green application. Within a few seconds, the Cloud Foundry router begins load balancing traffic for the original productive URL between Blue and Green version of the application.

4. Unmap the route to Blue version after you verify that the Green version is running as expected, stop routing requests to Blue version using the **cf unmap-route** command.
    ```
    cf unmap-route BLUE_APP_NAME DOMAIN -n HOSTNAME
    ```
    **Result:**
    The Cloud Foundry router stops sending traffic to Blue version. Now all traffic for the productive domain is sent to Green version.
