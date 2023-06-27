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
- Spring Security Crypto
- Spring Thymeleaf
- Spring Validation
- Spring Web
- SpringDoc OpenAPI
- AWS CDK
- Docker
- JUnit
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

### To Generate a public/private key pair

Private Key (RSA):

`ssh-keygen -t rsa -b 4096 -m PEM -f jwtRS256.key`

Public Key:

`openssl rsa -in jwtRS256.key -pubout -outform PEM -out jwtRS256.key.pub`

Private Key (PKCS8):

`openssl pkcs8 -topk8 -nocrypt -in jwtRS256.key > private_key.pem`
