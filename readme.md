# Recipify Backend

This backend serves an API for the JS clients

# Running the backend (using docker-compose)

To run the backend using `docker compose`, use the following `docker-compose.yml`-file.

> Note: The backend runs on port `8080`, so make sure this port is available on your machine.

- You can check if the backend runs correctly by opening the swagger UI on http://localhost:8080/swagger-ui/index.html

```
name: recipify-backend
services:
  backend:
    image: 'ghcr.io/nilshartmann-workshops/entwicklerde-recipify-backend:latest'
    environment:
      - SPRING_DATASOURCE_URL=jdbc:postgresql://postgres:5432/mydatabase
      - SPRING_DATASOURCE_USERNAME=myuser
      - SPRING_DATASOURCE_PASSWORD=secret
      - RECIPIFY_WEB_IMAGES_DIR=file:///web-root/images/
      - RECIPIFY_WEB_UPLOAD_DIR=file:///web-root/images/upload/
      - RECIPIFY_WEB_UPLOAD_URL-PATH=/images/upload/
    ports:
      - '8080:8080'
    depends_on:
      - 'postgres'

  postgres:
    image: 'postgres:16-alpine'
    environment:
      - 'POSTGRES_DB=mydatabase'
      - 'POSTGRES_PASSWORD=secret'
      - 'POSTGRES_USER=myuser'
```

# Running the backend (using Java)

- The easiest is to launch the class `nh.recipify.BackendApplication` from `src/main`. Thanks to Spring Boot's Docker support, it will launch the required Postgres database during startup.
- You can also start the database yourself using the `docker-compose.yaml` file in this project's root folder.
- When Postgres is running, run `BackendApplication` from `src/main`.

