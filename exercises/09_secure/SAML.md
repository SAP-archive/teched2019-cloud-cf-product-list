An SAML service provider interacts with an SAML 2.0 identity provider to authenticate users signing in by means of a single sign-on (SSO) mechanism. In this scenario, the UAA acts as a service provider representing a single subaccount. To establish trust between an identity provider and a subaccount, you must provide the SAML details for web-based authentication in the identity provider itself. Administrators must configure trust on both sides, in the subaccount of the service provider and in the SAML identity provider.  

Establishing Trust from a Subaccount in an SAML 2.0 Identity Provider
Establishing Trust from an SAML 2.0 Identity Provider in a Subaccount

:bulb: **Note:**  You find the official documentation on the SAP Help Portal ([SAML 2.0 Identity Provider](https://help.sap.com/viewer/65de2977205c403bbc107264b8eccf4b/Cloud/en-US/2d088cedeaf24038acb3533be8092fe4.html)).

In this section, we describe the case that you use a tenant in SAP Cloud Platform Identity Authentication Service as SAML identity provider.

# Establishing Trust from a Subaccount in an SAML 2.0 Identity Provider

You want to use an SAML 2.0 identity provider, for example SAP Cloud Identity Authentication service. This is where the business users for SAP Cloud Platform are stored. In the next step, you must establish a trust relationship with SAP Cloud Platform.

Prerequisites:

- You have already created a subaccount.
- You have downloaded the SAML 2.0 Configuration File from SAP Cloud Platform Identity Authentication, at https://<tenant>.accounts400.ondemand.com/saml2/metadata e.g. https://xs2security.accounts400.ondemand.com/saml2/metadata
- You must establish a trust relationship with an SAML 2.0 identity provider in your subaccount in SAP Cloud Platform. The following procedure describes how you establish trust in the SAP Cloud Platform Identity Authentication service.

## 1. Go to your subaccount and choose Security > Trust Configuration in the SAP Cloud Platform cockpit.
![SAML in Cockpit](./09_secure/saml/add_idp.png?raw=true)
## 2. Choose **New Trust Configuration**.
![SAML in Cockpit](./09_secure/saml/idp.png?raw=true)
## 3. Enter a name and a description that make clear that the trust configuration refers to the identity provider.
![SAML in Cockpit](./09_secure/saml/idp_metadata.png?raw=true)
## 4. Get the metadata from the following URL: https://<SCI_tenant>.accounts.ondemand.com/saml2/metadata

Example: 
https://xs2security.accounts400.ondemand.com/saml2/metadata
## 5. Copy the SAML 2.0 metadata and paste it into the Metadata field.
![SAML in Cockpit](./09_secure/saml/idp_metadata2.png?raw=true)
## 6. To validate the metadata, choose the Parse button. This will fill the Subject and Issuer fields.
## 7. Save your changes.
## 8. The name of the new trust configuration now shows the value xs2security.accounts400.ondemand.com. It represents the custom identity provider SAP Cloud Platform Identity Authentication.
![SAML in Cockpit](./09_secure/saml/idp_done.png?raw=true)

## 9. (Optional) If you do not need SAP ID Service, set it to inactive.

