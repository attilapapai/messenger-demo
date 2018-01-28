# Messenger Demo

This demo application is a simple messaging service. Users can post and retrieve
messages using the REST API.

## Prerequisites

- [Docker](https://docs.docker.com/install/)


## Running the application locally

There are two options for you to run the demo application locally, the
first one uses `docker-compose`, the latter `docker swarm`.

- Using `Docker Compose`

  ```
  $ docker-compose -f src/main/docker/docker-compose-separate-servers.yml up
  ```

  This command will expose 3 services on your system:

  - Spring Boot application on `127.0.0.1:8080`
  - Spring Boot application on `127.0.0.1:8081`
  - Postgres database on `127.0.0.1:5432`

- Using `Docker Swarm`

  1. Make sure to enable swarm mode

      ```
      $ docker swarm init
      ```

  2. Start the services

      ```
      $ docker stack deploy -c src/main/docker/docker-compose.yml messenger-demo
      ```

  After running these commands you should be able access the application
  on `127.0.0.1:8080`. Please note that by default there are two replicas
  of the backend and one database running. Reverse-proxy is applied by
  `docker swarm`.

## REST API

### Retrieve all messages

__URL__: `/messages`

__Method__: `GET`

__Success response__

Code: 200 (OK)

Example response
```
[
  {
    "id":1
    "content":"Hello"
  },
  {
    "id":2,
    "content":"Hi"
  }
]
```

### Add new message

__URL__: `/messages`

__Method__: `POST`

__Required parameters__

- `content`
  - Content of the message
  - _Type_: _String_

__Example data format__

```
{
  "content": "Hello"
}
```

__Success response__

Code: 201 (Created)

Example response

```
{
  "id":1
  "content":"Hello"
}
```

__Error response__

Code: 400 (Bad Request)

Cause: Data is not in a valid format

## Getting started with developing the application

1. Start Postgres:

    ```
    $ docker run --name messenger-demo-postgres -p 5432:5432 -e POSTGRES_USER=messenger -e POSTGRES_PASSWORD=messenger -d postgres
    ```

    Change the parameters according to your system needs. This will expose
    the default Postgres port (5432).

 2. Start Redis:

    ```
    $ docker run --name messenger-demo-redis -p 6379:6379 -d redis
    ```

    Redis will be available at port 6379.

Please note that none of the containers above have persistent storage.
In case you need the data to survive container restart, add volumes.

Running the application with Gradle:

```
$ ./gradlew bootRun
```

Pushing changes to Docker Hub:

```
$ ./gradlew build dockerPush
```

## Running the tests

```
$ ./gradlew test
```

## Running the integration tests

Make sure that there is Redis instance running, available at 127.0.0.1:6379,
then run the command:

```
$ ./gradlew testInteg
```

## Requirements

The task is to implement a data processing pipeline in the cloud.

- Set up a running environment aligned with the technologies mentioned below
- A Readme file containing information you deem useful for someone getting to know your code and want to try the system out
- Develop the application in Java 8, using either DropWizard or Spring Boot as the foundation
- A REST endpoint is taking a dummy JSON input, and the server puts the REST payload on Redis or another tool you think is well suited for the task
- A Consumer is running in the application, taking the freshly received message and persists it in a database of your choice
- A REST endpoint is implemented for retrieving all the messages persisted in JSON format from the database
- The message should also be pushed through Websockets for listening browser clients at the time the message was received on the REST endpoint
- A simple HTML page is implemented to show the real time message delivery
- Please setup a github repository to host the code and share it with your final note for review

We're looking for that:

- All tasks are solved in the solution
- The application has a solid commit history
- The application is built with scalability and maintainability in mind
- The application is built for testability, demonstrated by actual tests
- Your solution reflects a sense of quality you would be confident in releasing to production
- Documentation is applied to code / repository describing intent and purpose, as well as complicated / non obvious choices in the implementation
