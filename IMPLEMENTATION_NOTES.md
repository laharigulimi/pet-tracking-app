# Task: Pet Tracking Application

## Description:

Develop a Java application that calculates the number of pets (dogs and cats)
**outside of the power saving zone** based on data from different types of trackers. There are
two types of trackers for cats (small and big) and three for dogs (small, medium, big). The
application should receive data from these trackers through a **REST API**, store the data, and
allow querying the stored data through the same API. The application should be written in
Java 11+, use Spring Boot 2+, include tests, and support easy replacement of the
storage layer.

## Requirements:
1. Develop a REST API that facilitates the transfer of pet tracking data to the application.
Implement endpoints for receiving data, querying stored data, and providing
information about the number of pets currently outside the power saving zone
grouped by pet type and tracker type.

2. Define a Pet entity with attributes:
     
   - **petType** (Cat or Dog),
   - **trackerType** (Type of tracker),
   - **ownerId** (Integer),
   - **inZone** (Boolean true - in the zone, false - outside the zone)
3. Cat entity has additional attribute
   - **lostTracker** (Boolean true - tracker lost, false - tracker on cat)
4. Utilize an in-memory database for storing the received pet count data. Design the
application to enable straightforward replacement of the storage layer with a
persistent database.
5. Include unit tests and integration tests to ensure the correctness of the application.
Use testing frameworks to cover critical components and scenarios.
6. Provide clear but really short documentation on how to run the application, execute
tests, and interact with the REST API.

Expected output:
 - Code quality and organization - consider applying all the best practices you know
 - Adherence to RESTful principles
 - Test coverage and correctness
 - No UI is required
 - Example requests for curl (https://curl.se/)