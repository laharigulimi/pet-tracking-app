# Pet Tracking Application 

## Introduction

The Pet Tracking Application is a Spring Boot-based RESTful service that allows users to manage pet information, including cats and dogs. The application supports various operations such as adding, updating, retrieving, and deleting pets.

## Code Structure

### Configuration

The application configuration for the in-memory H2 database is specified in the [application.properties](src/main/resources/application.properties) file.

### Testing the Application

### Controller

The main controller for handling pet-related API requests is [PetController.java](src/main/java/com/screening/pettrackingapp/controller/PetController.java). It provides several endpoints for interacting with pet data. Here’s a brief overview of its methods:

### Testing with CURL

You can use CURL or postman tool to test the RESTful endpoints of the application. Here are the example curl commands for each of the API operations:

- Run the application and open command prompt to test these commands.
#### Add a Cat
- POST `/api/pets/cat:` Adds a new cat to the system.
    ```shell
    curl -X POST http://localhost:8080/api/pets/cat -H "Content-Type: application/json" -d "{\"petType\": \"CAT\", \"trackerType\": \"SMALL\", \"ownerId\": 1, \"inZone\": false, \"lostTracker\": true}"
    
    ```

#### Add a Dog
- POST `/api/pets/dog`: Adds a new dog to the system.
    ```shell
    curl -X POST http://localhost:8080/api/pets/dog -H "Content-Type: application/json" -d "{\"petType\": \"DOG\", \"trackerType\": \"SMALL\", \"ownerId\": 2, \"inZone\": true}"
    ```

#### Get All Pets
- GET `/api/pets`: Retrieves all pets.
    ```shell
    curl -X GET http://localhost:8080/api/pets
    ```

#### Get Pets Outside Zone
- GET `/api/pets/outside-zone`: Retrieves pets that are outside their designated zones.
    ```shell
    curl -X GET http://localhost:8080/api/pets/outside-zone
    ```

#### Get Lost Trackers Pets
- GET `/api/pets/lost-trackers`: Retrieves cats with lost trackers.
    ```shell
    curl -X GET http://localhost:8080/api/pets/lost-trackers
    ```

#### Update a Pet
- PUT `/api/pets/{id}`: Updates an existing pet by ID.
    ```shell
    curl -X PUT http://localhost:8080/api/pets/1 -H "Content-Type: application/json" -d "{\"petType\": \"CAT\", \"trackerType\": \"BIG\", \"ownerId\": 1, \"inZone\": true, \"lostTracker\": true}"
    ```

#### Delete a Pet
- DELETE `/api/pets/{id}`: Deletes a pet by ID.
    ```shell
    curl -X DELETE http://localhost:8080/api/pets/1
    ```

#### Get Pet By ID
- GET /api/pets/{id}: Retrieves a pet by its ID.
    ```shell
    curl -X GET http://localhost:8080/api/pets/2
    ```

#### Get Pets By Owner ID
- GET `/api/pets/owner/{ownerId}`: Retrieves pets by the owner's ID.
    ```shell
    curl -X GET http://localhost:8080/api/pets/owner/1
    ```

#### Get All Cats
- GET `/api/pets/cats`: Retrieves all cats.
    ```shell
    curl -X GET http://localhost:8080/api/pets/cats
    ```

#### Get All Dogs
- GET /api/pets/dogs: Retrieves all dogs.
    ```shell
    curl -X GET http://localhost:8080/api/pets/dogs
    ```
Alternatively, these end-points can be tested from postman
- To test your API endpoints using Postman, follow these steps:

  - Testing the /api/pets/cat Endpoint (POST)
     Steps:
     - Open Postman and create a new request.
     - Set the request method to POST.
     - Enter the request URL:{{base_url}}/api/pets/cat. Example:  http://localhost:8080/api/pets/cat.
     - Set the headers:
       - Key: Content-Type
       - Value: application/json
     - Select the Body tab, choose raw, and set the type to JSON from the dropdown.
       - Enter the request body as JSON and Click Send to execute the request.
           ```
            {
            "petType": "CAT",
            "trackerType": "SMALL",
            "ownerId": 1,
            "inZone": false,
            "lostTracker": false
            }
          ```


### Unit Tests

The unit tests for the application are located in the [test](src/test) directory.
You can run these tests using your IDE or by executing the following command in the terminal:
```shell
./mvnw test
```
Alternatively, any IDE can be used to run the tests.

### Integration Tests

The Integration tests for the application are located in the [integration](src/test/java/com/screening/pettrackingapp/integration) directory and the properties are defined in [application-test.properties](src/test/resources/application-test.properties)
To run the integration tests, use the following command:

```shell
./mvnw integration-test
```
alternatively, any IDE can be used to run the tests.