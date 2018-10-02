An SAML service provider interacts with an SAML 2.0 identity provider to authenticate users signing in by means of a single sign-on (SSO) mechanism. In this scenario, the UAA acts as a service provider representing a single subaccount. To establish trust between an identity provider and a subaccount, you must provide the SAML details for web-based authentication in the identity provider itself. Administrators must configure trust on both sides, in the subaccount of the service provider and in the SAML identity provider.  

:bulb: **Note:**  You find the official documentation on the SAP Help Portal ([SAML 2.0 Identity Provider](https://help.sap.com/viewer/65de2977205c403bbc107264b8eccf4b/Cloud/en-US/2d088cedeaf24038acb3533be8092fe4.html)).

# Establishing Trust from a Subaccount in an SAML 2.0 Identity Provider

You want to use an SAML 2.0 identity provider, for example SAP Cloud Identity Authentication service. This is where the business users for SAP Cloud Platform are stored. In the next step, you must establish a trust relationship with SAP Cloud Platform.

Prerequisites:
- You have already created a subaccount.

### 1. Go to your subaccount and choose Security > Trust Configuration in the SAP Cloud Platform cockpit.
![SAML in Cockpit](./saml/add_idp.png?raw=true)
### 2. Choose **New Trust Configuration**.
![SAML in Cockpit](./saml/idp.png?raw=true)
### 3. Enter a name and a description that make clear that the trust configuration refers to the identity provider.
![SAML in Cockpit](./saml/idp_metadata.png?raw=true)
### 4. Get the metadata from the following URL: https://\<IAS Tenant\>.accounts.ondemand.com/saml2/metadata
Example: 
https://xs2security.accounts400.ondemand.com/saml2/metadata
### 5. Copy the SAML 2.0 metadata and paste it into the Metadata field.
![SAML in Cockpit](./saml/idp_metadata2.png?raw=true)
### 6. To validate the metadata, choose the Parse button. This will fill the Subject and Issuer fields.
### 7. Save your changes.
### 8. The name of the new trust configuration now shows the value xs2security.accounts400.ondemand.com. It represents the custom identity provider SAP Cloud Platform Identity Authentication.
![SAML in Cockpit](./saml/idp_done.png?raw=true)
### 9. (Optional) If you do not need SAP ID Service, set it to inactive.

# Establishing Trust from an SAML 2.0 Identity Provider in a Subaccount

To establish trust with an SAML identity provider, you must assign the identity provider’s metadata file and define attribute mappings. The attributes are included in the SAML 2.0 assertion. With the UAA as SAML service provider, they are used for assignment of UAA authorizations based on information maintained in the identity provider.

### 1. Open the administration console of SAP Cloud Platform Identity Authentication service.
Example: https://\<IAS Tenant\>.accounts.ondemand.com/admin 

### 2. To add a new SAML 2.0 identity provider, create a new application in Applications section of Applications & Resources by using the "+ Add" button. 
![SAML in Cockpit](./saml/ias_add.png?raw=true)
### 3. Choose a name for the application that clearly identifies it as your new identity provider and save your changes.
![SAML in Cockpit](./saml/ias_add_app.png?raw=true)
### 4. Choose SAML 2.0 Configuration and import the relevant metadata file.
![SAML in Cockpit](./saml/ias_metadata.png?raw=true)

Use the metadata file of your subaccount. You find the metadata file in the following location:

**Note:** Replace subdomain with a corresponding value for your subaccount

**EU10**: https://\<subdomain\>.authentication.eu10.hana.ondemand.com/saml/metadata

**US10**: https://\<subdomain\>.authentication.us10.hana.ondemand.com/saml/metadata
### 5. Choose Name ID Attribute, select E-Mail as the unique attribute, and choose Save.
![SAML in Cockpit](./saml/ias_configure_email.png?raw=true)
### 6. Choose Assertion Attributes and enter Groups (capitalized) in the Groups user attribute. Save your changes.
![SAML in Cockpit](./saml/ias_groups.png?raw=true)
![SAML in Cockpit](./saml/ias_groups_2.png?raw=true)  
![SAML in Cockpit](./saml/ias_groups3.png?raw=true)  
### 7. Test the SAML 2.0 configuration, use the following URL:

**Note:** Replace the subdomain with the corresponding value for your subaccount

**EU10**: https://\<subdomain\>.authentication.eu10.hana.ondemand.com/config?action=who
  
**US10**: https://\<subdomain\>.authentication.us10.hana.ondemand.com/config?action=who
