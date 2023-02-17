# assigner

## Setting up `cplex.jar` library

In order to run build this project you need to add your own `cplex.jar` library under `lib/cplex.jar` (relative to the
project root, create `lib` directory if it does not exist). Also copy the native library e.g.: `x64_windows_msvc14`.

If CPLEX Optimization Studio was installed, `cplex.jar` and the native library can be found in a path similar
to: `C:\Program Files\IBM\ILOG\CPLEX_Studio2211\cplex\lib`.

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