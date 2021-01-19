# Spring Boot + JPA and PostgreSQL

This example is a resource server using Spring Boot and Spring Data JPA. It shows how to use the Okera Spring Data query transformer to provide dynamic access control.

This is forked and based on the Okta tutorial.

Please read [Build a Basic App with Spring Boot and JPA using PostgreSQL](https://developer.okta.com/blog/2018/12/13/build-basic-app-spring-boot-jpa) to see how this app was created.

**Prerequisites:** [Java 8](http://www.oracle.com/technetwork/java/javase/downloads/jdk8-downloads-2133151.html).

> [Okta](https://developer.okta.com/) has Authentication and User Management APIs that reduce development time with instant-on, scalable user infrastructure. Okta's intuitive API and expert support make it easy for developers to authenticate, manage, and secure users and roles in any application.

* [Getting Started](#getting-started)
* [Links](#links)
* [Help](#help)
* [License](#license)

## Getting Started

To install this example application, run the following commands:

```bash
git clone https://github.com/okera/okta-spring-boot-jpa-example.git
cd okta-spring-boot-jpa-example
```

This will get a copy of the project installed locally. To install all of its dependencies and start the app, run:
 
```bash
./gradlew bootRun
```

This will likely fail. You need to configure a PostgreSQL database with the following settings for everything to work out-of-the-box.

    url: "jdbc:postgresql://localhost:5432/springbootjpa"
    username: jpatutorial
    password: abcd1234

## Links

This example uses [Okta's Spring Boot Starter](https://github.com/okta/okta-spring-boot).

## License

Apache 2.0, see [LICENSE](LICENSE).
