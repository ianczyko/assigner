# assigner-fe

## Building and running docker image

```sh
docker build -t assigner-fe .
```

```sh
docker run -p 8081:80 --rm -e BE_ADDRESS=<BE_ADDRESS> -e BE_PORT=<BE_PORT> assigner-fe
```