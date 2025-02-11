# assigner

## Building and running docker image

```sh
docker build -t assigner .
```

```sh
docker run --rm -p 8080:8080  --env-file .env assigner
```

## .env file format

```dotenv
CONSUMER_KEY=...
CONSUMER_SECRET=...
INITIAL_COORDINATOR_USOS_ID=...

SPRING_JPA_HIBERNATE_DDL_AUTO=validate # <-- Remove this line on first run

# Optional DB config:
SPRING_DATASOURCE_URL=jdbc:postgresql://assigner-db:5432/postgres # docker-compose config
SPRING_DATASOURCE_USERNAME=...
SPRING_DATASOURCE_PASSWORD=...
```

## db.env file format

```dotenv
POSTGRES_USER=...
POSTGRES_PASSWORD=...
```