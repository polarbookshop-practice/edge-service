# Edge Service

This application is part of the Polar Bookshop system and provides an API gateway. It's part of the project
built in the [Cloud Native Spring in Action](https://www.manning.com/books/cloud-native-spring-in-action) book
by [Thomas Vitale](https://www.thomasvitale.com).

## Useful Commands

| Gradle Command	         | Description                                   |
|:---------------------------|:----------------------------------------------|
| `./gradlew bootRun`        | Run the application.                          |
| `./gradlew build`          | Build the application.                        |
| `./gradlew test`           | Run tests.                                    |
| `./gradlew bootJar`        | Package the application as a JAR.             |
| `./gradlew bootBuildImage` | Package the application as a container image. |

After building the application, you can also run it from the Java CLI:

```bash
java -jar build/libs/edge-service-0.0.1-SNAPSHOT.jar
```
# API Gateway and Circuit Breaker
![img.png](assets/api-1.png)
![img.png](assets/api-2.png)
![img.png](assets/api-3.png)
![img.png](assets/api-4.png)
![img.png](assets/api-5.png)

# Security: Authentication and SPA
![img.png](assets/security-1.png)
![img.png](assets/security-2.png)
- **Authorization Server**—The entity responsible for authenticating users and issuing
tokens. In Polar Bookshop, this will be Keycloak.
- **User**—Also called the Resource Owner, this is the human logging in with the
Authorization Server to get authenticated access to the Client application. In
Polar Bookshop, it’s either a customer or an employee.
- **Client**—The application requiring the user to be authenticated. This can be a
mobile application, a browser-based application, a server-side application, or
even a smart TV application. In Polar Bookshop, it’s Edge Service.

![img.png](assets/security-3.png)
![img.png](assets/security-4.png)
![img.png](assets/security-5.png)

# Security: Authorization and auditing
![img.png](assets/security-6.png)
![img.png](assets/security-7.png)
![img.png](assets/security-8.png)
![img.png](assets/security-9.png)