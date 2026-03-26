# Java Fez

A fully-featured Java SDK for interacting with [Fez Delivery](https://fezdelivery.co)'s RESTful APIs. This SDK enables developers to integrate Fez Delivery's logistics services seamlessly into Java-based applications, providing simple, strongly-typed methods for creating orders, requesting pick-ups, and more.

Whether you're building a backend service, desktop application, or microservice architecture, this SDK is designed to provide an idiomatic Java experience when communicating with Fez Delivery's platform.

## Requirements

- Java 17 or higher
- Maven (for dependency management)

## Installation

1. Add the GitHub Maven repository to your `pom.xml`:

```xml
<repositories>
  <repository>
    <id>github</id>
    <name>GitHub Packages - java-fez</name>
    <url>https://maven.pkg.github.com/OdunlamiZO/java-fez</url>
  </repository>
</repositories>
```

2. Add the dependency:

```xml
<dependency>
  <groupId>io.github.odunlamizo</groupId>
  <artifactId>java-fez</artifactId>
  <version>1.0-SNAPSHOT</version>
</dependency>
```

3. Configure GitHub credentials in your Maven settings.xml (usually located at ~/.m2/settings.xml):

```xml
<settings>
  <servers>
    <server>
      <id>github</id>
      <username>YOUR_GITHUB_USERNAME</username>
      <password>YOUR_PERSONAL_ACCESS_TOKEN</password>
    </server>
  </servers>
</settings>
```

> 📌&nbsp;&nbsp;&nbsp; Step 1 & 3 is required for now, since we are only deploying to github packages.

## Getting Started

### Setup

```java
Fez fez = new FezOkHttp("YOUR_USER_ID", "YOUR_PASSWORD");
```

Authentication is handled automatically. The SDK will authenticate on the first API call and re-authenticate whenever the token expires.

### Example: Creating an Order

```java
CreateOrderResponse response = fez.createOrder(
    List.of(
        CreateOrderRequest.builder()
            .recipientAddress("Idumota")
            .recipientState("Lagos")
            .recipientName("Femi")
            .recipientPhone("08000000000000")
            .uniqueId("KingOne-1234")
            .batchId("KingOne-1")
            .valueOfItem("20000")
            .weight(1)
            .build()
    )
);
System.out.println(response);
// CreateOrderResponse(code=201, status=Success, description=Order Successfully Created, orderNos={KingOne-1234=ASAC27012319})
```

## Contributing

We welcome contributions to improve this SDK! To contribute:

1. **Fork** the repository.
2. **Create a new branch** for your feature or bugfix.
3. Make your changes and write appropriate tests.
4. **Open a Pull Request (PR)** to the `main` branch with a clear description of your changes.

> 📌&nbsp;&nbsp;&nbsp;Please ensure your code adheres to the project's style and passes all tests before submitting a PR.
