# Dynamic Form

DynamicForm is a simple form generator project. Admin can add html fields and set validation rules for each field.
The form will be generated based on the fields and validation rules set by the admin.

## Problem 
Say in a School Management Application, each School has its own needs regarding forms, so instead of changing the code base
for each school we can use Dynamic Forms which allows schools to create forms based on there requirement.

## Steps To Create Dynamic Form

First of all Signup using and get the Access(Bearer) & Refresh token, then add Authorization Header in Request for the Bearer Token 
```json
    {
      "firstName": "string",
      "lastName": "string",
      "email": "string",
      "password": "string"
    }
```

1. To create a new form, it required a name and a remarks (reason for creating form).
    **Request URI: /dynamic-form/create-form**
    ```json
    {
        "name": "Form Name",
        "remarks": "Small Description about the Form"
    }
    ```
    _name_ and _remark_ are required fields.
    Initially, the form will be created with *IN_ACTIVE* status.
    **form name is unique** across all forms, no duplicate name allowed.


2. To add fields to the form, it required a form id, field name, field type, and field validation rules.
   **Request URI: /dynamic-form/add-form-field**

    Sample Request For TEXT type Form Field:
   ```json
    {
        "formId": 1,
        "name": "Field Name",
        "type": "TEXT",
        "label": "Field Label",
        "validationRules": {
            "REQUIRED": true,
            "MIN_LENGTH": 5,
            "MAX_LENGTH": 10,
            "PATTERN": "^[a-zA-Z0-9]*$"
        },
        "remarks": "Reason for adding field",
        "placeHolder": "Field Placeholder",
        "helpDescription": "Field Help Description"
    }
    ```
   
     Available validation rules are:
   ```
        REQUIRED, // for all fields
        MIN_LENGTH, MAX_LENGTH, PATTERN, //for text fields
        MIN_VALUE, MAX_VALUE, // for number fields
        ALLOWED_FILE_TYPES, MIN_FILES, MAX_FILES, // for file fields
        MIN_DATE, MAX_DATE, // for date fields
        MIN_TIME, MAX_TIME, // for time fields
        MIN_DATE_TIME, MAX_DATE_TIME // for datetime fields
   ```
3. After creation of form field, it is required to activate the form field.

   **Request URI: /dynamic-form/make-form-field-active/{formId}/{formFieldId}**<br>
    formId and formFieldId are path variables.
    
4. After activating at least one form field, the form can be activated.

   **Request URI: /dynamic-form/make-form-active/{formId}**<br>
    formId is path variable.    

5. When the form is active, it will be available for filling it.
   
   **Request URI: /dynamic-form/fetch-form-to-fill/{formId}**<br>
    formId is path variable.<br>
    Where neither form is active not it's form field active, it will return 404.<br>
    When formFields are active, but form is not active, it will return 400.<br>
    Sample Response For Form Is:
    ```json
   {
      "response": {
          "formId": 1,
          "name": "Form Name",
          "remarks": "Reason for creating form",
          "formStatus": "ACTIVE",
          "htmlFormFields": [
              {
                  "id": 1,
                  "name": "Field Name",
                  "type": "TEXT",
                  "label": "Field Label",
                  "placeHolder": "Field Placeholder",
                  "helpDescription": "Field Help Description",
                  "sortingOrder": 1,
                  "formFieldStatus": "ACTIVE",
                  "remarks": "Reason for adding field",
                  "createdDate": "2021-08-01T00:00:00",
                  "lastModifiedDate": "2021-08-01T00:00:00",
                  "version": 1,
                  "displayOptions": {},
                  "validationRules": {
                      "REQUIRED": true,
                      "MIN_LENGTH": 5,
                      "MAX_LENGTH": 10,
                      "PATTERN": "^[a-zA-Z0-9]*$"
                  }
              }
          ]
      }
   }
    ```
   
7. To submit form, it required form id and form fields with values.

   **Request URI: /dynamic-form/submit-form**<br>
    Sample Request For Submitting Form:
    ```json
    {
        "formId": 7,
        "userId": 4,
        "fieldValues": {
            "student_name": "Sudhanshu",
            "gender": "male",
            "interests": ["a", "b", "c"],
            "dob": "2003-05-16",
            "..."
        }
    }
    ```
    formId, userId and fieldValues are required fields.<br>

----------------------------------------------------------------------------------------------------------------------------
