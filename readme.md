# Recipify Backend

This backend serves an API for the JS clients

# Running the backend

- The easiest is to launch the class `nh.recipify.BackendApplication` from `src/main`. Thanks to Spring Boot's Docker support, it will launch the required Postgres database during startup.
- You can also start the database yourself using the `docker-compose.yaml` file in this project's root folder.
- When Postgres is running, run `BackendApplication` from `src/main`.

