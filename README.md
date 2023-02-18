# Spring Boot Template for IAM

- This template is intended for the IAM functionalities only
- Kafka is included in this template
- Sample classes are included
- **This template uses annotation based routing**
- **Intended for personal use only as this does not include complete features like JHipster**

# Technologies Used:

- Kotlin
- Spring Boot 3
- Spring Actuator
- Spring Cache
- Spring Cloud AWS S3
- Spring Configuration Processor
- Spring Data JPA
- Spring Data Redis
- Spring Devtools
- Spring Kafka
- Spring Retry
- Spring Security OAuth2 JOSE
- Spring Thymeleaf
- Spring Validation
- Spring Web
- SpringDoc OpenAPI
- Docker
- JUnit
- Jackson
- Kubernetes
- Liquibase
- MapStruct
- Micrometer Tracing
- OpenAPI
- PostgreSQL
- Prometheus
- Zipkin

# Spring IAM Template

### To run the code:

`./gradlew bootRun`

### To run tests:

`./gradlew test`

### To build a JAR file:

`./gradlew build -x test`

### To generate a certificate:

`keytool -genkeypair -alias spring-boot -keyalg RSA -keysize 2048 -validity 3650 -keypass spring-boot -storetype PKCS12 -keystore keystore.p12`
