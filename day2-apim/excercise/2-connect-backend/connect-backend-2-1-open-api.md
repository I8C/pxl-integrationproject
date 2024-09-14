## Connect API to backend

The next step is to connect the API to the backend service. This will allow the API to forward requests to the backend service and return the response to the client.

### Connect the API to the backend service

1. In the Azure portal, go to your API Management instance and click on **APIs** in the left-hand menu.
2. Click on the **EANConsumptions API xx** API.
3. Click on the **Settings** tab.
4. Change the value of the **Backend URL** to the URL of the Camel API created during day 1. The URL should resemble `http://<your-instance-name>-.eu-central-1.compute.amazonaws.com:8080/api/v1`. Replace `<your-instance-name>` with the values from the previous exercise in Camel.
5. Press **Save**.

  ![APIM Change backend](../../assets/images/apim-change-backend.png)

### Remove mocking policy
1. Click on the **POST /ean-consumptions** operation.
2. Click on the **Mock responses** policy in the **Inbound processing** section.
3. The policy editor will open. Locate the following line and remove it:

```xml
    <mock-response status-code="200" content-type="application/json" />
```
4. Press **Save**.

### Test the API
In order to test the API, we will use Postman to send a request to the API and view the response.

1. Open Postman.
2. Create a new request.
3. Set the request type to **POST**.
4. Enter the URL of the API. The URL should resemble `https://<your-apim-instance-name>.azure-api.net/ean-consumptions`.
5. Go to the **Headers** tab and add a new header with the key `Ocp-Apim-Subscription-Key` and the value of the subscription key you copied earlier.
6. Go to the **Body** tab and enter the following JSON object:

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
7. Send the request.
8. You should receive a response with a status code of 200 OK and a JSON object containing the response from the backend service.
