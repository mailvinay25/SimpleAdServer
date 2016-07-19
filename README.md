# SimpleAdServer
A simple web application that allows a user to create ad campaigns.
The web application meets the following functional requirements:

####################### Functional Requirements ####################### 

1. Create Ad Campaign via HTTP POST
A user should be able to create an ad campaign by sending a POST request to the ad server at http://<host>/ad.  The body of the POST request must be a JSON object containing the following data:
 
{
"partner_id": "unique_string_representing_partner',
"duration": "int_representing_campaign_duration_in_seconds_from_now"
"ad_content": "string_of_content_to_display_as_ad"
}

2. Fetch Ad Campaign for a Partner
A partner should be able to get their ad data by sending a GET request to the ad server at http://<host>/ad/<partner_id>.  Response can be delivered as a JSON object representing the active ad
 
If the current time is greater than a campaign's creation time + duration, then the server's response should be an error indicating that no active ad campaigns exist for the specified partner.
####################### ####################### ####################### 

####################### Unit Tests #######################

To run Junit tests written for the application, run the following command from the directory containing the pom.xml:

Command : mvn -Dtest=com.vinay.simpleadserver.tests.SimpleAdServerTests test

