# Spring Boot + JPA and PostgreSQL

This example is a resource server using Spring Boot and Spring Data JPA. It shows how to use the Okera Spring Data query transformer to provide dynamic access control.

This is forked and based on the Okta tutorial.

Please read [Build a Basic App with Spring Boot and JPA using PostgreSQL](https://developer.okta.com/blog/2018/12/13/build-basic-app-spring-boot-jpa) to see how this app was created.

**Prerequisites:** [Java 8](http://www.oracle.com/technetwork/java/javase/downloads/jdk8-downloads-2133151.html).

> [Okta](https://developer.okta.com/) has Authentication and User Management APIs that reduce development time with instant-on, scalable user infrastructure. Okta's intuitive API and expert support make it easy for developers to authenticate, manage, and secure users and roles in any application.

* [Getting Started](#getting-started)
* [Okera Integration](#okera-integration)
* [Links](#links)
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

## Okera Integration

This repository also demonstrates how to use Okera's spring data connector to provide dynamic access control. With this integration, Okera's libraries will wrap the Spring Data JPA Data Source and provide dynamic query rewrite. The application query, before being sent to the original Data Source, will be authorized by the Okera server which returns an altered SQL statement. 

For example, if the original application query was:

```sql
SELECT name FROM customers
```

Based on the access policies, the server could return
```sql
SELECT name FROM customers WHERE country = 'US'
```

In this repository, all this functionality is encapsulated in `DatasourceProxyBeanPostProcessor.java`. The functionality is enabled by simply including the class in the application.

The query transformation mechanism uses an open source library for integrating with Spring Data, which provides multiple ways to integrate with existing applications.

### Setup

There are multiple ways to have Okera understand the schema of the application queries. In production, this is typically done with Okera's JDBC registration features. For this example, the kayak table can be created explicitly.

In Okera, run:

```sql
CREATE DATABASE springbootjpa;

CREATE TABLE springbootjpa.kayak(
  id INT,
  name STRING,
  owner STRING,
  value DECIMAL(9,2),
  make_model STRING
);

CREATE ROLE springbootjpa_user;
GRANT ROLE springbootjpa_user TO GROUPS springbootjpa_user;
GRANT SHOW ON DATABASE springbootjpa TO ROLE springbootjpa_user;

-- Create a policy that restricts one user from only being able to see 'loaner' kayaks.
GRANT SELECT ON TABLE springbootjpa.kayak
WHERE name = 'loaner'
TO ROLE springbootjpa_user;
```

### User authentication

The example application has basic authentication enabled, for the `root` and `springbootjpa_user` test users. Both users use `password` as the password. 

After running `./gradlew bootRun`, you can view the data via curl.

As the admin root user:

```bash
curl localhost:8080/kayaks -u root:password | jq '._embedded.kayaks | .[] | "Owner=\(.owner) name=\(.name) model=\(.makeModel) value=\(.value)"'
```

As the springbootjpa_user:

```bash
curl localhost:8080/kayaks -u springbootjpa_user:password | jq '._embedded.kayaks | .[] | "Owner=\(.owner) name=\(.name) model=\(.makeModel) value=\(.value)"'
```

It is expected that the root user see all data and the springbootjpa_user only see loaners, as per the created dynamic row filter.

## Links

This example uses [Okta's Spring Boot Starter](https://github.com/okta/okta-spring-boot).

This is the spring data [framework](http://ttddyy.github.io/datasource-proxy/docs/current/user-guide/).

## License

Apache 2.0, see [LICENSE](LICENSE).
