# Dynamic Form(Backend)

DynamicForm is a simple form generator project. Admin can add html fields and set validation rules for each field.
The form will be generated based on the fields and validation rules set by the admin.

## Introduction
This project addresses the need for customizable forms in some specific Applications. For example in School Management Software which is shared among multiple schools,
each school has unique requirements for student admission forms and other documentation.
By using Dynamic Forms, schools can create and manage forms according to their specific needs without changing the underlying codebase.

## Types of Users: <br>
From End user perspective there will be two types of user, one will be ADMIN like School Management, which is going to create the Form.
The others will be Students(Visitors), who will fill the form. <br>

## Steps To Create Dynamic Form

First of all Signup to get the Access(Bearer) & Refresh token. The Signup Request is as follows :
```json
    {
      "firstName": "Sudhanshu",
      "lastName": "Arya",
      "email": "sudhanshu@gmail.com",
      "password": "#4Ypu*78H026"
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

6. To submit form, it required form id and form fields with values.

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

## Sample Form Field Creation Request

Text Field Creation Request:
```json
{
    "formId": 1,
    "name": "student_name",
    "type": "TEXT",
    "label": "Student Name",
    "validationRules": {
        "REQUIRED": true,
        "MIN_LENGTH": 5,
        "MAX_LENGTH": 10,
        "PATTERN": "^[a-zA-Z0-9]*$"
    },
    "remarks": "Student Name Field",
    "placeHolder": "Enter Student Name",
    "helpDescription": "Enter Student Name"
}
```

Number Field Creation Request:
```json
{
    "formId": 1,
    "name": "family_members_count",
    "type": "NUMBER",
    "label": "Family Members Count",
    "validationRules": {
        "REQUIRED": true,
        "MIN_VALUE": 1,
        "MAX_VALUE": 10
    },
    "remarks": "Family Members Count Field",
    "placeHolder": "Enter Family Members Count",
    "helpDescription": "Enter Family Members Count"
}
```

Radio Field Creation Request:
```json
{
    "formId": 7,
    "name": "gender",
    "type": "RADIO",
    "label": "Select Your Gender",
    "validationRules": {
        "REQUIRED": "true"
    },
    "remarks": "To Get Gender Of The Student",
    "placeHolder": "",
    "helpDescription": "Select Others If Don't Want To Reveal It",
    "displayOptions": {
        "male": "Male",
        "female": "Female",
        "others": "Others"
    }
}
```

Checkbox Field Creation Request:
```json
{
    "formId": 7,
    "name": "interests",
    "type": "CHECKBOX",
    "label": "Select Your Interests",
    "validationRules": {
        "REQUIRED": "true"
    },
    "remarks": "To Get Interests Of The Student",
    "placeHolder": "",
    "helpDescription": "Select All That Apply",
    "displayOptions": {
        "a": "Interest A",
        "b": "Interest B",
        "c": "Interest C"
    }
}
```

File Field Creation Request:
```json
{
    "formId": 7,
    "name": "student_avtar",
    "type": "FILE",
    "label": "Upload Your Avatar",
    "validationRules": {
        "REQUIRED": "true",
        "ALLOWED_FILE_TYPES": ["AUDIO", "VIDEO", "IMAGE", "DOC"],
        "MIN_FILES": 1,
        "MAX_FILES": 1
    },
    "remarks": "To Get Avatar Of The Student",
    "placeHolder": "",
    "helpDescription": "Upload Your Avatar",
    "displayOptions": {}
}
```

Date Field Creation Request:
```json
{
    "formId": 7,
    "name": "dob",
    "type": "DATE",
    "label": "Select Your Date Of Birth",
    "validationRules": {
        "REQUIRED": "true",
        "MIN_DATE": "2000-01-01",
        "MAX_DATE": "2010-12-31"
    },
    "remarks": "To Get Date Of Birth Of The Student",
    "placeHolder": "",
    "helpDescription": "Select Your Date Of Birth",
    "displayOptions": {}
}
```

Time Field Creation Request:
```json
{
    "formId": 7,
    "name": "meeting_time",
    "type": "TIME",
    "label": "Select Meeting Time",
    "validationRules": {
        "REQUIRED": "true",
        "MIN_TIME": "09:00",
        "MAX_TIME": "18:00"
    },
    "remarks": "To Get Meeting Time Of The Student",
    "placeHolder": "",
    "helpDescription": "Select Meeting Time",
    "displayOptions": {}
}
```

DateTime Field Creation Request:
```json
{
    "formId": 7,
    "name": "event_date",
    "type": "DATETIME_LOCAL",
    "label": "Select Event Date",
    "validationRules": {
        "REQUIRED": "true",
        "MIN_DATE_TIME": "2024-07-01T00:00",
        "MAX_DATE_TIME": "2024-08-31T23:59"
    },
    "remarks": "To Get Event Date Of The Student",
    "placeHolder": "",
    "helpDescription": "Select Event Date",
    "displayOptions": {}
}
```

----------------------------------------------------------------------------------------------------------------------------

Update Existing Filled Form

**Request URI: /dynamic-form/update-form**<br>
Sample Request For Updating Filled Form:<br>

```json
    {
        "formId": 7,
        "userId": 4,
        "fieldValues": {
            "student_name": "Manish",
            "family_members_count": 5,
            "gender": "male",
            "interests": ["a", "b", "c"],
            "dob": "1999-11-01",
            "meeting_time": "09:50",
            "event_date": "2024-08-10T10:40:20"
        }
    }
```

the only change in submitting form and updating filled form is when sending data of FILE type form field,<br>
it required oldIds, newIds(if any) and deletedIds(if any).

<br>
Update Display Name for Field:<br>

**Request URI: /dynamic-form/change-display-name**
Sample Request For Updating Display Name of a Field:<br>

```json
    {
      "formId" : 9,
      "fieldNameToChangeDisplayName" : "Name of the field",
      "newDisplayNameForField" : "New Display Name"
    }

```
<br>
Update Display Order for Field : <br>

**Request URI: /dynamic-form/change-display-order**<br>
The Fields should be present in the Form<br>
Sample Request For Updating Display Order of Fields of a Form:<br>

```json
    {
      "formId": 123,
      "fieldNames": ["FirstName", "LastName", "Mother's Name", "Father's Name", "Email", "Mobile No."]
    }
```

---------------------------------------------------------------------------
---------------------------------------------------------------------------

Filter Filled Forms

Available operations For Text Fields are:
```
    EQUALS, NOT_EQUALS, CONTAINS, DOES_NOT_CONTAINS, STARTS_WITH, DOES_NOT_STARTS_WITH, ENDS_WITH, DOES_NOT_ENDS_WITH, IS_BLANK, IS_NOT_BLANK
```

**Request URI: /dynamic-form/filter-filled-forms**<br>
Sample Request For Filtering Filled Forms:<br>

```json
{
  "fieldFilters": [
    {
      "fieldType": "TEXT",
      "fieldName": "student_name",
      "operation": "EQUALS",
      "filterValue": "Manish Ovbtu",
      "caseSensitive": false
    }
  ]
}
```

<br>
<br>
<br>
<br>
<br>
<br>
<br>
<br>
<br>
<br>
<br>
<br>
<br>
<br>
<br>
<br>
<br>