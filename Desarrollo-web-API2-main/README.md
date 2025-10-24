# Documentación del sistema contable "prototipo1"

## 1. Visión general
El repositorio contiene una aplicación Spring Boot orientada a la gestión contable y financiera de una pyme. Se inicia mediante la clase `Prototipo1Application`, que arranca el contexto de Spring Boot y expone los servicios REST definidos en los distintos módulos de dominio.【F:src/main/java/com/prototipo/prototipo1/Prototipo1Application.java†L1-L13】

## 2. Tecnologías principales
El proyecto usa Java 21, Spring Boot 3.5, Spring Data JPA, Spring Web, Spring Security, validación, Actuator y soporte para JWT (jjwt). Incluye conectores para PostgreSQL y H2, más Lombok para reducción de código repetitivo.【F:pom.xml†L1-L140】

## 3. Requisitos previos
* Java Development Kit 21.
* Maven Wrapper incluido (`mvnw` / `mvnw.cmd`).
* Acceso a una base de datos PostgreSQL accesible mediante JDBC.

## 4. Configuración de la aplicación
### 4.1 Propiedades principales
El archivo `application.properties` define el puerto de servidor, la conexión a PostgreSQL (URL, usuario y contraseña), los parámetros del token JWT y la configuración básica de Hibernate y del pool Hikari.【F:src/main/resources/application.properties†L1-L22】

### 4.2 Metadatos adicionales
El archivo `META-INF/additional-spring-configuration-metadata.json` documenta las propiedades personalizadas `security.jwt.secret` y `security.jwt.exp-minutes`, utilizadas para firmar y expirar los JWT emitidos por el servicio de autenticación.【F:src/main/resources/META-INF/additional-spring-configuration-metadata.json†L1-L15】

## 5. Ejecución y tareas básicas
1. Instalar las dependencias y compilar: `./mvnw clean install`.
2. Ejecutar la aplicación en modo desarrollo: `./mvnw spring-boot:run`.
3. Correr las pruebas automatizadas: `./mvnw test`.

## 6. Seguridad y autenticación
* **Filtros y configuración**: `SecurityConfig` deshabilita CSRF, habilita CORS, establece sesiones sin estado y protege todas las rutas salvo `/api/auth/**`, documentación OpenAPI y `/api/dashboard/health`. También registra `JwtAuthFilter` antes del filtro estándar de autenticación y expone beans para CORS, codificación BCrypt y el `AuthenticationManager`.【F:src/main/java/com/prototipo/prototipo1/Config/SecurityConfig.java†L1-L75】
* **JWT**: `JwtService` firma tokens HS256 usando la clave y el tiempo de expiración definidos en las propiedades. Expone métodos para generar, validar y obtener el subject del token.【F:src/main/java/com/prototipo/prototipo1/Auth/JwtService.java†L1-L57】
* **Autenticación**: `AuthController` publica `/api/auth/login` y `/api/auth/register`. El servicio asociado recupera usuarios por correo electrónico, genera JWT con el rol asociado y crea cuentas nuevas codificando la contraseña con BCrypt al registrarse.【F:src/main/java/com/prototipo/prototipo1/Auth/AuthController.java†L1-L25】【F:src/main/java/com/prototipo/prototipo1/Auth/AuthService.java†L1-L41】
* **Usuarios y roles**: las entidades `Usuario` y `Rol` representan credenciales y perfiles autorizados. La implementación `JpaUserDetailsService` carga usuarios por correo y les asigna la autoridad `ROLE_{ROL}` para la autorización por anotaciones.【F:src/main/java/com/prototipo/prototipo1/Auth/Usuario.java†L1-L23】【F:src/main/java/com/prototipo/prototipo1/Auth/Rol.java†L1-L16】【F:src/main/java/com/prototipo/prototipo1/Auth/JpaUserDetailsService.java†L1-L23】

## 7. Estructura funcional
La aplicación se organiza por paquetes de dominio: autenticación (`Auth`), bancos y caja (`Banco`, `BancoCaja`), clientes y proveedores, inventario, contabilidad (`Cuenta`, `LibroDiario`, `LibroMayor`), reportes (`BalanceSaldo`, `EstadoResultado`) y el módulo analítico de dashboard. Cada paquete contiene entidad, repositorio, servicio y controlador REST correspondientes.

## 8. Módulos y endpoints principales
Las siguientes tablas resumen cada API y los roles requeridos (según anotaciones `@PreAuthorize`).

### 8.1 Autenticación (`/api/auth`)
| Método | Ruta | Descripción | Roles |
| --- | --- | --- | --- |
| POST | `/api/auth/login` | Emite un JWT para el usuario identificado por correo electrónico. | Público |
| POST | `/api/auth/register` | Registra un usuario con rol existente y devuelve JWT. | Público |

_Fuente: `AuthController`._【F:src/main/java/com/prototipo/prototipo1/Auth/AuthController.java†L8-L25】

### 8.2 Catálogo de bancos (`/api/bancos`)
| Método | Ruta | Descripción | Roles |
| --- | --- | --- | --- |
| GET | `/api/bancos?activos=true` | Lista bancos activos o todos. | Administrador, Contador, Cajero |
| GET | `/api/bancos/{id}` | Recupera un banco por identificador. | Administrador, Contador, Cajero |
| POST | `/api/bancos` | Crea un banco. | Administrador, Contador |
| PUT | `/api/bancos/{id}` | Actualiza datos del banco. | Administrador, Contador |
| DELETE | `/api/bancos/{id}` | Elimina un banco. | Administrador |

_Fuente: `BancoController`._【F:src/main/java/com/prototipo/prototipo1/Banco/BancoController.java†L10-L52】

### 8.3 Movimientos bancarios y de caja (`/api/bancos-caja`)
| Método | Ruta | Descripción | Roles |
| --- | --- | --- | --- |
| GET | `/api/bancos-caja` | Lista transacciones y permite filtrar por banco, tipo y fechas. | Administrador, Contador, Cajero |
| GET | `/api/bancos-caja/{id}` | Obtiene una transacción individual. | Administrador, Contador, Cajero |
| POST | `/api/bancos-caja` | Registra una transacción con referencia a un banco. | Administrador, Contador, Cajero |
| PUT | `/api/bancos-caja/{id}` | Modifica una transacción existente. | Administrador, Contador |
| DELETE | `/api/bancos-caja/{id}` | Borra una transacción. | Administrador |
| GET | `/api/bancos-caja/resumen/total` | Calcula el total de movimientos en un periodo opcionalmente filtrado por banco. | Administrador, Contador, Cajero |
| GET | `/api/bancos-caja/resumen/por-tipo` | Devuelve totales agrupados por tipo de transacción. | Administrador, Contador, Cajero |

_Fuente: `BancoCajaController`._【F:src/main/java/com/prototipo/prototipo1/BancoCaja/BancoCajaController.java†L14-L79】

### 8.4 Clientes (`/api/clientes`)
| Método | Ruta | Descripción | Roles |
| --- | --- | --- | --- |
| GET | `/api/clientes` | Lista clientes con filtros por nombre, correo y rango de saldo. | Administrador, Contador |
| GET | `/api/clientes/{id}` | Recupera un cliente. | Administrador, Contador |
| POST | `/api/clientes` | Crea un cliente. | Administrador, Contador |
| PUT | `/api/clientes/{id}` | Actualiza un cliente. | Administrador, Contador |
| DELETE | `/api/clientes/{id}` | Elimina un cliente. | Administrador |
| PATCH | `/api/clientes/{id}/cargar` | Incrementa el saldo pendiente. | Administrador, Contador |
| PATCH | `/api/clientes/{id}/abonar` | Reduce el saldo pendiente. | Administrador, Contador |
| PATCH | `/api/clientes/{id}/saldo` | Fija el saldo en un valor específico. | Administrador, Contador |

_Fuente: `ClienteController`._【F:src/main/java/com/prototipo/prototipo1/Cliente/ClienteController.java†L11-L88】

### 8.5 Proveedores (`/api/proveedores`)
| Método | Ruta | Descripción | Roles |
| --- | --- | --- | --- |
| GET | `/api/proveedores` | Lista proveedores con filtros por nombre, correo y rango de saldo. | Administrador, Contador |
| GET | `/api/proveedores/{id}` | Recupera un proveedor. | Administrador, Contador |
| POST | `/api/proveedores` | Crea un proveedor. | Administrador, Contador |
| PUT | `/api/proveedores/{id}` | Actualiza datos del proveedor. | Administrador, Contador |
| DELETE | `/api/proveedores/{id}` | Elimina un proveedor. | Administrador |
| PATCH | `/api/proveedores/{id}/cargar` | Incrementa el saldo pendiente con el proveedor. | Administrador, Contador |
| PATCH | `/api/proveedores/{id}/abonar` | Registra un pago que disminuye el saldo. | Administrador, Contador |
| PATCH | `/api/proveedores/{id}/saldo` | Ajusta el saldo a un valor exacto. | Administrador, Contador |

_Fuente: `ProveedorController`._【F:src/main/java/com/prototipo/prototipo1/Proveedor/ProveedorController.java†L11-L86】

### 8.6 Inventario (`/api/inventario`)
| Método | Ruta | Descripción | Roles |
| --- | --- | --- | --- |
| GET | `/api/inventario` | Lista productos con filtros por nombre o tipo. | Administrador, Contador, Cajero |
| GET | `/api/inventario/{id}` | Consulta un producto. | Administrador, Contador, Cajero |
| POST | `/api/inventario` | Crea un producto. | Administrador, Contador |
| PUT | `/api/inventario/{id}` | Actualiza un producto. | Administrador, Contador |
| DELETE | `/api/inventario/{id}` | Elimina un producto. | Administrador, Contador |
| PATCH | `/api/inventario/{id}/ajustar-stock` | Ajusta el stock sumando o restando unidades. | Administrador, Contador, Cajero |
| PATCH | `/api/inventario/{id}/precio` | Actualiza el precio unitario. | Administrador, Contador, Cajero |

_Fuente: `InventarioController`._【F:src/main/java/com/prototipo/prototipo1/Inventario/InventarioController.java†L11-L73】

### 8.7 Contabilidad diaria (`/api/libro-diario`)
| Método | Ruta | Descripción | Roles |
| --- | --- | --- | --- |
| GET | `/api/libro-diario` | Lista todos los asientos del libro diario. | Administrador, Contador |
| GET | `/api/libro-diario/{id}` | Obtiene un asiento específico. | Administrador, Contador |
| POST | `/api/libro-diario` | Registra un asiento y replica el movimiento al libro mayor. | Administrador, Contador |
| PUT | `/api/libro-diario/{id}` | Modifica un asiento. | Administrador, Contador |
| DELETE | `/api/libro-diario/{id}` | Elimina un asiento. | Administrador |

_Fuente: `LibroDiarioController`._【F:src/main/java/com/prototipo/prototipo1/LibroDiario/LibroDiarioController.java†L14-L72】

### 8.8 Contabilidad mayor (`/api/libro-mayor`)
| Método | Ruta | Descripción | Roles |
| --- | --- | --- | --- |
| GET | `/api/libro-mayor` | Lista movimientos acumulados del libro mayor. | Administrador, Contador |
| GET | `/api/libro-mayor/{cuentaId}` | Consulta movimientos por cuenta contable. | Administrador, Contador |
| GET | `/api/libro-mayor/saldo/{cuentaId}` | Calcula el saldo total de una cuenta. | Administrador, Contador |

_Fuente: `LibroMayorController`._【F:src/main/java/com/prototipo/prototipo1/LibroMayor/LibroMayorController.java†L15-L58】

### 8.9 Catálogo de cuentas (`/api/cuentas`)
| Método | Ruta | Descripción | Roles |
| --- | --- | --- | --- |
| GET | `/api/cuentas` | Lista todas las cuentas contables. | Administrador, Contador |
| GET | `/api/cuentas/{id}` | Recupera una cuenta por identificador. | Administrador, Contador |
| GET | `/api/cuentas/by-codigo/{codigo}` | Busca una cuenta por código único. | Administrador, Contador |
| POST | `/api/cuentas` | Crea una cuenta. | Administrador, Contador |
| PUT | `/api/cuentas/{id}` | Actualiza la cuenta. | Administrador, Contador |
| DELETE | `/api/cuentas/{id}` | Elimina la cuenta. | Administrador |

_Fuente: `CuentaController`._【F:src/main/java/com/prototipo/prototipo1/Cuenta/CuentaController.java†L14-L76】

### 8.10 Balance de saldos (`/api/balance-saldos`)
| Método | Ruta | Descripción | Roles |
| --- | --- | --- | --- |
| POST | `/api/balance-saldos/generar` | Calcula el balance de saldos para todas las cuentas en una fecha dada. | Administrador, Contador |
| GET | `/api/balance-saldos` | Lista los balances almacenados. | Administrador, Contador, Cajero |
| GET | `/api/balance-saldos/{cuentaId}` | Devuelve el último balance de una cuenta. | Administrador, Contador, Cajero |

_Fuente: `BalanceSaldoController`._【F:src/main/java/com/prototipo/prototipo1/BalanceSaldo/BalanceSaldoController.java†L14-L74】

### 8.11 Estado de resultados (`/api/estado-resultados`)
| Método | Ruta | Descripción | Roles |
| --- | --- | --- | --- |
| GET | `/api/estado-resultados` | Lista estados de resultados almacenados. | Administrador, Contador |
| GET | `/api/estado-resultados/{id}` | Recupera un estado de resultados. | Administrador, Contador |
| POST | `/api/estado-resultados` | Registra un estado calculado externamente. | Administrador, Contador |
| PUT | `/api/estado-resultados/{id}` | Actualiza un estado existente. | Administrador, Contador |
| DELETE | `/api/estado-resultados/{id}` | Elimina un estado. | Administrador |
| POST | `/api/estado-resultados/generar` | Calcula y persiste un estado de resultados para un periodo. | Administrador, Contador |

_Fuente: `EstadoResultadoController`._【F:src/main/java/com/prototipo/prototipo1/EstadoResultado/EstadoResultadoController.java†L12-L68】

### 8.12 Dashboard analítico (`/api/dashboard`)
| Método | Ruta | Descripción | Roles |
| --- | --- | --- | --- |
| GET | `/api/dashboard/metrics` | Devuelve métricas personalizadas según el rol autenticado. | Administrador, Contador, Cajero |
| GET | `/api/dashboard/metrics/all` | Retorna todas las métricas disponibles. | Administrador |
| GET | `/api/dashboard/health` | Expone un chequeo básico de estado. | Público |

_Fuente: `DashboardController`._【F:src/main/java/com/prototipo/prototipo1/Dashboard/DashboardController.java†L16-L111】

## 9. Modelo de datos
Las entidades principales describen los objetos de negocio:
* **Banco**: nombre, código único, estado de actividad y fecha de creación.【F:src/main/java/com/prototipo/prototipo1/Banco/Banco.java†L1-L35】
* **BancoCaja**: transacciones bancarias con monto, fecha, tipo y referencia a un banco.【F:src/main/java/com/prototipo/prototipo1/BancoCaja/BancoCaja.java†L1-L33】
* **Cliente y Proveedor**: datos de contacto y saldo pendiente como `BigDecimal` inicializado en cero.【F:src/main/java/com/prototipo/prototipo1/Cliente/Cliente.java†L1-L35】【F:src/main/java/com/prototipo/prototipo1/Proveedor/Proveedor.java†L1-L35】
* **Inventario**: productos con nombre, descripción, stock, precio unitario y tipo de producto.【F:src/main/java/com/prototipo/prototipo1/Inventario/Inventario.java†L1-L25】
* **Cuenta**: catálogo contable con código único, nombre, tipo y relaciones con los libros diario y mayor.【F:src/main/java/com/prototipo/prototipo1/Cuenta/Cuenta.java†L1-L36】
* **LibroDiario** y **LibroMayor**: asientos contables con fecha, descripción, débitos, créditos y vínculos entre ambos libros.【F:src/main/java/com/prototipo/prototipo1/LibroDiario/LibroDiario.java†L1-L34】【F:src/main/java/com/prototipo/prototipo1/LibroMayor/LibroMayor.java†L1-L37】
* **BalanceSaldo** y **EstadoResultado**: reportes persistidos con cortes de fecha y totales contables.【F:src/main/java/com/prototipo/prototipo1/BalanceSaldo/BalanceSaldo.java†L1-L27】【F:src/main/java/com/prototipo/prototipo1/EstadoResultado/EstadoResultado.java†L1-L27】
* **Usuario** y **Rol**: representan credenciales y roles autorizados para proteger los endpoints.【F:src/main/java/com/prototipo/prototipo1/Auth/Usuario.java†L1-L23】【F:src/main/java/com/prototipo/prototipo1/Auth/Rol.java†L1-L16】

## 10. Lógica de negocio destacada
* **Clientes y proveedores**: los servicios validan campos obligatorios, restringen saldos negativos y permiten ajustes parciales o totales de cuentas por cobrar/pagar.【F:src/main/java/com/prototipo/prototipo1/Cliente/ClienteService.java†L13-L121】【F:src/main/java/com/prototipo/prototipo1/Proveedor/ProveedorService.java†L13-L95】
* **Inventario**: el servicio garantiza nombres, stock y precio válidos, con operaciones para ajustar inventario y precio unitario.【F:src/main/java/com/prototipo/prototipo1/Inventario/InventarioService.java†L13-L82】
* **Banco y caja**: `BancoCajaService` valida tipos de transacción permitidos, realiza búsquedas por filtros y genera resúmenes totales; `BancoService` controla operaciones CRUD sobre entidades bancarias.【F:src/main/java/com/prototipo/prototipo1/BancoCaja/BancoCajaService.java†L20-L130】【F:src/main/java/com/prototipo/prototipo1/Banco/BancoService.java†L12-L51】
* **Libros contables**: `LibroDiarioService` registra asientos y replica automáticamente en `LibroMayorService`, que calcula saldos y puede generar balances por SQL; el repositorio de libro mayor permite obtener saldos históricos por cuenta.【F:src/main/java/com/prototipo/prototipo1/LibroDiario/LibroDiarioService.java†L14-L70】【F:src/main/java/com/prototipo/prototipo1/LibroMayor/LibroMayorService.java†L17-L86】【F:src/main/java/com/prototipo/prototipo1/LibroMayor/LibroMayorRepository.java†L1-L21】
* **Reportes**: `BalanceSaldoService` recalcula saldos por cuenta con fecha de corte, y `EstadoResultadoService` obtiene ingresos, costos y gastos desde consultas agregadas para generar nuevos estados.【F:src/main/java/com/prototipo/prototipo1/BalanceSaldo/BalanceSaldoService.java†L19-L83】【F:src/main/java/com/prototipo/prototipo1/EstadoResultado/EstadoResultadoService.java†L15-L64】【F:src/main/java/com/prototipo/prototipo1/EstadoResultado/EstadoResultadoRepository.java†L1-L32】
* **Dashboard**: `DashboardService` construye métricas específicas según el rol (administrador, contador o cajero) consultando agregados del `DashboardRepository`. El repositorio ejecuta consultas nativas para ventas, gastos, inventario, cobros, pagos y listados de asientos y saldos pendientes.【F:src/main/java/com/prototipo/prototipo1/Dashboard/DashboardService.java†L17-L200】【F:src/main/java/com/prototipo/prototipo1/Dashboard/DashboardRepository.java†L11-L119】

## 11. Respuestas DTO relevantes
* `BancoCajaDTO` enriquece las transacciones con información del banco asociado y permite convertir entre DTO y entidad.【F:src/main/java/com/prototipo/prototipo1/BancoCaja/BancoCajaDTO.java†L1-L40】
* `BalanceSaldoDTO` proyecta balances a una estructura plana para las respuestas REST.【F:src/main/java/com/prototipo/prototipo1/BalanceSaldo/BalanceSaldoDTO.java†L1-L21】
* `DashboardResponse` agrega métricas para los tres roles junto con listas de asientos y cuentas por cobrar/pagar.【F:src/main/java/com/prototipo/prototipo1/Dashboard/dto/DashboardResponse.java†L1-L46】

## 12. Pruebas automatizadas
Existe una prueba de arranque (`contextLoads`) en `Prototipo1ApplicationTests`, que verifica la carga correcta del contexto de Spring Boot.【F:src/test/java/com/prototipo/prototipo1/Prototipo1ApplicationTests.java†L1-L13】

## 13. Diagramas de arquitectura
Los diagramas de componentes y despliegue que resumen la interacción entre capas, filtros de seguridad y la infraestructura de base de datos se encuentran en [`docs/diagramas.md`](docs/diagramas.md).

