# amazonmq-dynamodb


This is an event-driven service application generated from `event-driven-service-archetype`.



To build a fat `JAR` file, execute the following command:

```bash
mvn clean compile package assembly:single
```

To build the image, execute the following command in the project directory:

```bash
docker build -t amazonmq-dynamodb:1.0-SNAPSHOT .
```


To run the [previously built](#building-a-fat-jar) `JAR` file, execute the following command:

```bash
java -jar target/amazonmq-dynamodb-1.0-SNAPSHOT.jar
```


To run the main method without building the `JAR` first, execute the following command:

```bash
mvn exec:java -Dexec.mainClass="com.jdwsearch.EventDrivenServiceApplication"
```


To run all tests with coverage, execute the following command:

```bash
mvn clean test verify
```


|   Variable name   | Default value |                    Description                   |
|:-----------------:|:-------------:|:------------------------------------------------:|
|    SERVER_HOST    |   localhost   |          Host the server will listen on          |
|    SERVER_PORT    |      3000     |          Port that server will listen on         |
|     QUEUE_HOST    |   localhost   |              Host of the AMQP queue              |
|     QUEUE_PORT    |     61616     |              Port of the AMQP queue              |
|     QUEUE_USER    |     admin     |           Username for login to Broker           |
|   QUEUE_PASSWORD  |     admin     |           Password for login to Broker           |
|     QUEUE_NAME    |   TestQueue   |                 Name of the queue                |
| QUEUE_SSL_ENABLED |      true     | Determines if SSL is used in the AMQP connection |



This project is using `Lombok` and `Spock`, for which plugins need to be downloaded.
Otherwise, you will see plenty of errors in your IDE.
(This will not affect compilation with Maven)


Open `File` -> `Settings` -> `Plugins` -> `Marketplace` and download:
 * Lombok Plugin
 * Spock Enhancements Plugin

After restarting the IDE, [enable annotation processing](https://www.jetbrains.com/help/idea/compiler-annotation-processors.html#58758823).

Open `File` -> `Settings` -> `Editor` -> `Code Style` and import `AND Intellij Code Style.xml` file.