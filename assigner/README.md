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
```