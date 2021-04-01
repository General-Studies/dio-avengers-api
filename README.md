# Avengers API - LiveCode DIO - 01/04/2021

Desenvolvimento de uma API utilizando SpringBoot + Kotlin com o intuito de cadastro de Vingadores.

## Tecnologias / Frameworks / IDE

- Intellij
- SpringBoot 2.4.4
- Maven
- Kotlin
- SpringData JPA
- PostgreSQL
- Flyway
- Java 8
- Heroku

## Criação do esqueleto do projeto

- https://start.spring.io/

## Definir contrato API

- https://editor.swagger.io/

- Recurso `avenger`
- GET - 200 OK
- GET {id}/detail - 200 OK ou 404 Not Found
- POST - 201 Created ou 400 Bad Request
- PUT {id} - 202 Accepted ou 404 Not Found
- DELETE {id} - 202 Accepted ou 404 Not Found

```json
{
  "nick": "spider-man",
  "person": "Peter Parker",
  "description": "sobre poderes",
  "history": "a história"
}
```

## Design Arquitetural

- Camada de Aplicação (controllers, configs, exception handle, request e response dtos, bean validations)
- Camada de Domínio (modelo avenger, interface repositório, serviço)
- Camada de Infraestrutura (jpa repository, entidade avenger, proxy implementa interface repositorio e utiliza o jpa repositorio para comunicação com banco de dados)
- Testes 

### Flyway (db/migration)

```sql
create table avenger (
    id bigserial not null,
    nick varchar(36),
    person varchar(128),
    description varchar(128),
    history text
    primary key (id)
);

alter table avenger add constraint UK_5r88eemotwgru6k0ilqb2ledh unique (nick);
```

### Profiles

- application.yaml
- application-dev.yaml
- application-heroku.yaml

```yaml
spring:
  application:
    name: avengers
  config:
    # This configuration allow use profiles as spring 2.3.x version
    # In spring 2.4.x version, has changed to:
    # spring:
    #  profiles:
    #    group:
    #      <group>: dev, auth
    use-legacy-processing: true
  profiles:
    active: dev
  jmx:
    enabled: false
  data:
    jpa:
      repositories:
        bootstrap-mode: deferred
  jpa:
    open-in-view: false
    properties:
      hibernate.jdbc.time_zone: UTC
      hibernate.generate_statistics: false
      hibernate.jdbc.batch_size: 25
      hibernate.order_inserts: true
      hibernate.order_updates: true
      hibernate.query.fail_on_pagination_over_collection_fetch: true
      hibernate.query.in_clause_parameter_padding: true
    hibernate:
      ddl-auto: none
      naming:
        physical-strategy: org.springframework.boot.orm.jpa.hibernate.SpringPhysicalNamingStrategy
        implicit-strategy: org.springframework.boot.orm.jpa.hibernate.SpringImplicitNamingStrategy
  main:
    allow-bean-definition-overriding: true
  task:
    execution:
      thread-name-prefix: avengers-task-
      pool:
        core-size: 2
        max-size: 50
        queue-capacity: 10000
    scheduling:
      thread-name-prefix: avengers-scheduling-
      pool:
        size: 2
  output:
    ansi:
      console-available: true

server:
  port: 9090
  servlet:
    session:
      cookie:
        http-only: true
    context-path: /avengers
```

```yaml
spring:
  profiles:
    active: dev
  jackson:
    serialization:
      indent-output: true
  datasource:
    type: com.zaxxer.hikari.HikariDataSource
    url: jdbc:postgresql://localhost:25433/${DB_NAME}
    username: ${DB_USER}
    password: ${DB_PASSWORD}
  jpa:
    database-platform: org.hibernate.dialect.PostgreSQLDialect
    show-sql: true
```

```yaml
spring:
  profiles:
    active: heroku
  jackson:
    serialization:
      indent-output: true
  datasource:
    type: com.zaxxer.hikari.HikariDataSource
    url: jdbc:postgresql://${HOST}/${DB_NAME}
    username: ${DB_USER}
    password: ${DB_PASSWORD}
  jpa:
    database-platform: org.hibernate.dialect.PostgreSQLDialect
    show-sql: false
```

## Dcoker 

### Environment Config

```sh 
DB_USER=dio.avenger
DB_PASSWORD=dio.avenger
DB_NAME=avengers
```

### YAML (backend-services.yaml)

```yaml
version: '3.2'
services:
  postgres:
    image: postgres:12-alpine
    environment:
      POSTGRES_USER: ${DB_USER}
      POSTGRES_DB: ${DB_NAME}
      POSTGRES_PASSWORD: ${DB_PASSWORD}
      ALLOW_IP_RANGE: 0.0.0.0/0
    ports:
      - "25433:5432"
    volumes:
      - pdb12:/var/lib/postgresql/data
    networks:
      - postgres-compose-network

  teste-pgadmin-compose:
    image: dpage/pgadmin4
    environment:
      PGADMIN_DEFAULT_EMAIL: "avengers@email.com"
      PGADMIN_DEFAULT_PASSWORD: "123456"
    ports:
      - "5556:80"
    depends_on:
      - postgres
    networks:
      - postgres-compose-network

volumes:
  pdb12:
networks:
  postgres-compose-network:
    driver: bridge
```

### Script / Comandos

- `docker-compose -f backend-services.yaml up -d` (deploy) / `docker-compose -f backend-services.yaml down` (undeploy) 

- Start API 
```sh
./mvnw spring-boot:run -Dspring-boot.run.profiles=dev -Dspring-boot.run.jvmArguments="-Xmx256m -Xms128m" -Dspring-boot.run.arguments="'--DB_USER=dio.avenger' '--DB_PASSWORD=dio.avenger' '--DB_NAME=avengers'"
``` 

## Heroku

- Criar app
- Linkar com Github
- Setar vairáveis de ambiente

### Procfile

```text
web: java -XX:+UnlockExperimentalVMOptions -XX:+UseCGroupMemoryLimitForHeap $JAVA_OPTS -Dserver.port=$PORT -Dspring.profiles.active=heroku -jar target/*.jar
```
