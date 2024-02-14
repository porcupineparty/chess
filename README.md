# ♕ BYU CS 240 Chess

This project demonstrates mastery of proper software design, client/server architecture, networking using HTTP and WebSocket, database persistence, unit testing, serialization, and security.

Here is a link to [my UML diagram][https://sequencediagram.org/index.html?presentationMode=readOnly#initialData=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].

## 10k Architecture Overview

The application implements a multiplayer chess server and a command line chess client.

[![Sequence Diagram](10k-architecture.png)](https://sequencediagram.org/index.html#initialData=C4S2BsFMAIGEAtIGckCh0AcCGAnUBjEbAO2DnBElIEZVs8RCSzYKrgAmO3AorU6AGVIOAG4jUAEyzAsAIyxIYAERnzFkdKgrFIuaKlaUa0ALQA+ISPE4AXNABWAexDFoAcywBbTcLEizS1VZBSVbbVc9HGgnADNYiN19QzZSDkCrfztHFzdPH1Q-Gwzg9TDEqJj4iuSjdmoMopF7LywAaxgvJ3FC6wCLaFLQyHCdSriEseSm6NMBurT7AFcMaWAYOSdcSRTjTka+7NaO6C6emZK1YdHI-Qma6N6ss3nU4Gpl1ZkNrZwdhfeByy9hwyBA7mIT2KAyGGhuSWi9wuc0sAI49nyMG6ElQQA)

## IntelliJ Support

Open the project directory in IntelliJ in order to develop, run, and debug your code using an IDE.

## Maven Support

You can use the following commands to build, test, package, and run your code.

| Command                    | Description                                     |
| -------------------------- | ----------------------------------------------- |
| `mvn compile`              | Builds the code                                 |
| `mvn package`              | Run the tests and build an Uber jar file        |
| `mvn package -DskipTests`  | Build an Uber jar file                          |
| `mvn install`              | Installs the packages into the local repository |
| `mvn test`                 | Run all the tests                               |
| `mvn -pl shared test`     | Run all the shared tests                        |
| `mvn -pl client exec:java` | Build and run the client `Main`                 |
| `mvn -pl server exec:java` | Build and run the server `Main`                 |

These commands are configured by the `pom.xml` (Project Object Model) files. There is a POM file in the root of the project, and one in each of the modules. The root POM defines any global dependencies and references the module POM files.

### Running the program using Java

Once you have compiled your project into an uber jar, you can execute it with the following command.


```sh
java -jar client/target/client-jar-with-dependencies.jar

♕ 240 Chess Client: chess.ChessPiece@7852e922
```
