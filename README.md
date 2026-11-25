# msvc-customer

Microservicio REST para la gestión de clientes y sus direcciones, desarrollado con **Java 21** y **Spring Boot 3.5.16** . Implementa una **arquitectura hexagonal (puertos y adaptadores)** para mantener el dominio aislado de los frameworks y de la infraestructura, con persistencia en **H2 en memoria** , documentación interactiva mediante **OpenAPI / Swagger UI** y endpoints de health con **Actuator** .

## Arquitectura

El microservicio implementa una **arquitectura hexagonal (puertos y adaptadores)** con dependencias dirigidas hacia el núcleo: el dominio no conoce Spring, JPA ni detalles de infraestructura.

```
CustomerRestController → CustomerUseCase (CustomerService) → CustomerRepositoryPort (CustomerPersistenceAdapter) → CustomerJpaRepository
```

- `domain`: contiene los modelos y las reglas de negocio. Es independiente de frameworks y de la persistencia.
- `application`: orquesta los casos de uso y define los puertos de entrada y salida
- `adaptadores de entrada`: exponen la API REST y traducen las peticiones HTTP a casos de uso.
- `Adaptadores de salida`:implementan la persistencia JPA sobre H2 y satisfacen los puertos definidos por la aplicación.
- `Infrastructure`: adaptadores REST (controller, DTOs, mapper manual, `@RestControllerAdvice`) y JPA (entidades, repositorio Spring Data, adaptador de persistencia).

Este diseño aísla las reglas de negocio, reduce el acoplamiento entre capas y permite sustituir la infraestructura, por ejemplo, cambiar H2 por PostgreSQL o añadir otro consumidos, sin modificar el núcleo del dominio.

## Estructura de paquetes

```
com.mscv.customer
├── MsvcCustomerApplication.java
├── application
│   ├── CustomerService.java
│   └── exception
│       ├── AddressNotFoundException.java
│       ├── CustomerNotFoundException.java
│       └── DuplicateDocumentException.java
├── domain
│   ├── models
│   │   ├── Address.java
│   │   └── Customer.java
│   └── port
│       ├── in/CustomerUseCase.java
│       └── out/CustomerRepositoryPort.java
└── Infrastructure
    └── adapter
        ├── in/rest
        │   ├── address/dto/{AddressRequestDto,AddressResponseDto}.java
        │   ├── customer/{CustomerRestController.java, dto/, mapper/}
        │   └── exception/{ApiErrorResponse,GlobalExceptionHandler}.java
        └── out/persistence/customer
            ├── AddressEntityJpa.java
            ├── CustomerEntityJpa.java
            ├── CustomerJpaRepository.java
            └── CustomerPersistenceAdapter.java
```

## Requisitos previos

- JDK 21
- Maven 3.10.1; no obligatorio.
- Docker con Compose para `compose.yaml`.
- kubernetes: el proyecto incluye manifiestos en k8s/ como referencia, kubernetes en local
- Opcional: copiar `.env.example` a `.env` (lo usa la configuración de depuración de VS Code y Docker Compose).

Verificación rápida: `java -version`, `./mvnw -v`, `docker --version`.

## Ejecutar localmente con Maven

```bash
./mvnw spring-boot:run
```

- App: http://localhost:8080
- Swagger UI: http://localhost:8080/swagger-ui/index.html y OpenAPI JSON: http://localhost:8080/v3/api-docs
- Health: http://localhost:8080/actuator/health (también `/actuator/health/liveness` y `/actuator/health/readiness`)
- Para desactivar la documentación: `SPRINGDOC_ENABLED=false ./mvnw spring-boot:run`
- La base H2 es en memoria y se recrea al arrancar (`create-drop`): los datos se pierden al reiniciar.

Pruebas:

```bash
./mvnw test       # pruebas + reporte JaCoCo en target/site/jacoco/
./mvnw verify     # además exige ≥80% de cobertura de línea
```

Para generar y ejecutar el jar:

```bash
./mvnw package
java -jar target/msvc-customer-0.0.1-SNAPSHOT.jar
```

### Endpoints

| Método | Ruta                                                                                          | Resultado                                          |
| ------ | --------------------------------------------------------------------------------------------- | -------------------------------------------------- |
| POST   | `/customers`                                                                                  | 201 – crea un cliente                              |
| GET    | `/customers/{id}`                                                                             | 200 – busca por id                                 |
| GET    | `/customers/search?document={numero}`                                                         | 200 – busca por número de documento                |
| PATCH  | `/customers/{id}/name`, `/last-name`, `/document-number`, `/document-type`, `/age`, `/active` | 200 sin cuerpo                                     |
| POST   | `/customers/{id}/addresses`                                                                   | 201 – devuelve el cliente con la dirección añadida |
| DELETE | `/customers/{id}/addresses/{addressId}`                                                       | 204                                                |

Errores: 404 (no encontrado), 409 (documento duplicado) y 400 (argumento inválido), con cuerpo `ApiErrorResponse` (`status`, `error`, `message`, `path`, `timestamp`).

## Construir y ejecutar con Docker

```bash
docker build -t msvc-customer:local .
docker run --rm -p 8080:8080 msvc-customer:local
```

O con Compose (lee `APP_PORT` y `SPRINGDOC_ENABLED` desde `.env`, con valores por defecto 8080 y true):

```bash
docker compose up --build
docker compose down
```

El Dockerfile es multi-etapa (JDK 21 para compilar, JRE 21 para ejecutar) y el contenedor corre como usuario no root.

## Kubernetes

Los manifiestos en `k8s/` son solo una **referencia del despliegue**; el proyecto **no se conecta a ningún clúster**.

**Qué incluyen**

- `namespace.yaml` → namespace `msvc-customer`.
- `deployment.yaml` → Deployment `customer-service`: imagen `msvc-customer:local`, 1 réplica, puerto `8080`, recursos, `securityContext` y sondas a `/actuator/health*`.
- `service.yaml` → Service `customer-service` (puerto `80` → `8080`).
- `kustomization.yaml` → agrupa los tres archivos anteriores.

**Health**

En local, las rutas `/actuator/health`, `/actuator/health/liveness` y `/actuator/health/readiness` las expone **Spring Boot Actuator**.

## Colección Postman

La colección con todos los métodos está incluida en este paquete: [`postman/msvc-customer.postman_collection.json`](postman/msvc-customer.postman_collection.json).

Para usarla: extrae el paquete, abre Postman y usa **Import → Upload Files**, seleccionando ese JSON desde la carpeta `postman/`. Incluye 11 peticiones (crear cliente, buscar por id y por documento, los PATCH, alta y baja de direcciones). Todas apuntan a `http://localhost:8080`
