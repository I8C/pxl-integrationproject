## Import API using OpenAPI

There are multiple ways to import APIs into Azure API Management. One option would be to manually add operations to an API.This approach is mostly used when there is a need to follow a design first approach. For this exercise an Open API specification is alreayd available.

### OpenAPI
First open the OpenAPI specification for the API that you want to import. The OpenAPI specification is a standard way to describe a RESTful API. It is a JSON or YAML file that contains all the information about the API, such as the endpoints, operations, request and response schemas, and more.

Navigate to the website https://editor.swagger.io and through the **File** menu, select **Import file**. Select the file [PXL_EANConsumptions_API.yaml](../../assets/openapispec/PXL_EANConsumptions_API.yaml) and click **Open**. The OpenAPI specification will be loaded into the editor.

![Swagger editor](../../assets/images/swagger-editor.png)

Have a look at the different sections of the OpenAPI specification. The specification contains information about the API such as the title, version, description, and the different endpoints. The endpoints contain information about the operations that can be performed on the endpoint, such as the HTTP method, request and response schemas, and more.

### Import API


![Import API](../../assets/images/import-api.png)

1) On the left menu, open the **APIs** blade.  
2) Click on **Add API**.  
3) Under **Create from definition** select **OpenAPI**.  
4) Select the **Full** option in the **Create from OpenAPI specification** dialog.  
5) In the **Specification URL** field, click **Select a file** and upload the [PXL_EANConsumptions_API.yaml](../../assets/openapispec/PXL_EANConsumptions_API.yaml) file.

> **Note the intentional use of `http` instead of `https` as this backend does not presently support `https`.**  

6) While the backend service only runs on `HTTP`, we need to **set URL scheme to `Both`** to allow for Azure API Management ingress to occur on HTTPS for callers such as the Developer Portal.  
7) Set the **API URL suffix** to `api/digital-meter-readingxx`.  Replace xx with your student number for this excercise.
8) Change the **Display name** to `EANConsumptions API xx`. Replace xx with your student number for this excercise.
9) Change the **Name** to `eanconsumptions-api-xx`. Replace xx with your student number for this excercise.
10) Press **Create**.  

![APIM Add EAN consumption API](../../assets/images/apim-create-ean-openapispec.png)

- Once the API is created, it will show in the list of APIs along with all of its operations.

> The backend HTTP endpoint should have been set to 'http://placeholder.be/api/v1'. This is a placeholder and will be replaced with the actual backend endpoint in the next exercise.

  ![APIM Add EAN consumption API](../../assets/images/apim-create-ean-openapispec2.png)


## Add mock response
Since the backend service is not yet available, we will add a mock response to the API operation. This will allow us to test the API operation without having to wait for the backend service to be available.

1) Click on the **POST /eanconsumptions** operation. Display name: 'Add new quarter EAN consumptions'
2) Click on 'Add policy' in the **Inbound processing** section.
3) Select 'Mock responses' from the list of policies.
4) Leave the default value '200 OK, application/json' for 'API Management response' and click 'Save'.
5) The portal will now show the mock response policy in the **Inbound processing** section and show the banner value 'Mocking is enabled'.

![APIM mocking](../../assets/images/apîm-mocking.png)
---

## Test the API
There are multiple ways to test an API in Azure API Management. One way is to use the **Test** tab in the Azure portal. This tab allows you to test the API operation by sending a request to the API and viewing the response.

### Test the API operation inside the Azure portal
1) Click on the **Test** tab.
2) In the **Request body** section, enter the following JSON object if it's not present already:

```json
[
    {
        "EANNumber": "541440110000000101",
        "MeterReadings": [
            {
                "meterID": "1SAG1234567890",
                "dailyEnergy": [
                    {
                        "timestampStart": "2020-01-01T11:00:00Z",
                        "timestampEnd": "2020-01-02T11:00:00Z",
                        "measurement": [
                            {
                                "unit": "kwh",
                                "offtakeValue": 10.478,
                                "offtakeValidationState": "VAL",
                                "injectionValue": 8.377,
                                "injectionValidationState": "VAL"
                            }
                        ]
                    }
                ]
            }
        ]
    }
]
```
3) Click on **Send** to send the request to the API.
4) The response will be displayed in the **Response body** section. The response should be the mock response that was added to the API operation.
![APIM Mock Reponse](../../assets/images/apim-mock-response.png)

### Test the API operation using Postman
Another way to test the API operation is to use a tool like Postman. Postman is a popular tool for testing APIs. It allows you to send requests to an API and view the response. You can download Postman from the following link: [Postman](https://www.postman.com/downloads/).

1) Open Postman.
2) Create a new request by clicking on the **New** button.
3) Enter the request URL. The request URL is the URL of the API operation. You can find the URL in the **Request URL** section of the **Test** tab in the Azure portal.
4) Select the **POST** method.
5) Click on the **Body** tab.
6) Select **raw** and **JSON** from the dropdown menu.
7) Enter the request body in the text area. The request body is the same as the request body that was entered in the **Request body** section of the **Test** tab in the Azure portal.
8) Click on the **Send** button to send the request to the API.
9) The response will be displayed in the **Response** section. The response should be the mock response that was added to the API operation.
![APIM Postman Mock Reponse](../../assets/images/apim-postman-mock-response.png)

