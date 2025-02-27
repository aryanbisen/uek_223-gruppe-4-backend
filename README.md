# uek_223-gruppe-4-backend
(Aryan Bisen, Karol Krawiec & Neslihan Avsar)

## Description

This repository is the backend of our OurSpace application.

## API Documentation

You can run the backend and then go to this endpoint to read the Swagger UI documentation of our API.
http://localhost:8080/swagger-ui/index.html

## How to start the backend

First, we need to create a docker container for a PostgreSQL database. We can do that by running this command.
```
docker run --name postgres_db -e POSTGRES_USER=postgres -e POSTGRES_PASSWORD=postgres -p 5432:5432 -d postgres
```

After creating the container, you can check if the container is running by executing `docker ps`.
Once that is complete, you should execute the `bootRun` Gradle task under `application`.
It will take some time, but once it's done, you should be able to access the API documentation.

## Troubleshooting

If you get this error:
```
org.postgresql.util.PSQLException: ERROR: relation "role_authority" does not exist
```

Simply restart the application.
Hibernate sometimes does not initialize the tables fast enough and causes this error.
Restarting the application fixes this.

## Testing
The JSON file for Postman component tests can be found in the file Component-testing.postman_colletion.json.
