# **Microservicios de Productos e Inventario**

Este proyecto es una solución de arquitectura de microservicios que gestiona la información de productos e inventario. Está compuesto por dos servicios principales:

* **`product-service`**: Un microservicio desarrollado con **Spring Boot (Java)** que se encarga de gestionar la información de los productos.
* **`inventory-service`**: Un microservicio desarrollado con **Node.js** que gestiona la cantidad de inventario de cada producto.

Ambos servicios se comunican entre sí y utilizan una base de datos **PostgreSQL** separada para cada uno.

---

## **Requisitos**

Asegúrate de tener instalado **Docker** y **Docker Compose** en tu máquina para poder ejecutar el proyecto.

---

## **Configuración y Ejecución**

Sigue estos sencillos pasos para levantar el entorno completo.

1.  **Clona el repositorio**
    Clona este proyecto en tu máquina local.

2.  **Configura las variables de entorno**
    Las claves de la API ya están configuradas en el archivo `docker-compose.yml`. No necesitas hacer nada más.

3.  **Inicia los contenedores**
    Desde la raíz del proyecto, ejecuta el siguiente comando en tu terminal. Esto construirá las imágenes y levantará todos los servicios.

    ```bash
    docker-compose up --build
    ```
    * `--build`: Fuerza a Docker a reconstruir las imágenes de los servicios de Java y Node.js para incluir cualquier cambio.

4.  **Verifica los servicios**
    Después de que el comando termine, puedes verificar que los contenedores estén corriendo con el siguiente comando:

    ```bash
    docker-compose ps
    ```
    Si todo salió bien, verás los cuatro contenedores (`product-db`,`inventory-db` `product-service` y `inventory-service`) en estado `Up`.

---

## **Pruebas de la API**

Una vez que los servicios estén activos, puedes probar los endpoints usando una herramienta como **cURL**, **Postman** o **Insomnia**.

### **1. Probar el Servicio de Productos (Puerto 8080)**

* **Crear un nuevo producto**
    ```bash
    curl --request POST \
      --url http://localhost:8080/api/v1/products \
      --header 'Content-Type: application/json' \
      --data '{
    	"name": "Laptop XPS 15",
    	"price": 1800.00
    }'
    ```
    La respuesta incluirá el ID del producto creado. Guárdalo para la siguiente prueba.

* **Obtener un producto por ID**
    ```bash
    curl --request GET \
      --url http://localhost:8080/api/v1/products/{id}
    ```

### **2. Probar el Servicio de Inventario (Puerto 3000)**

* **Actualizar el inventario**
    ```bash
    curl --request PUT \
      --url http://localhost:3000/api/v1/inventories/{id} \
      --header 'X-API-Key: super-secret-key-12345' \
      --header 'Content-Type: application/json' \
      --data '{
        "quantity": 50
    }'
    ```
    **Importante:** La cabecera `X-API-Key` es necesaria para la autenticación.

* **Consultar el inventario de un producto**
    ```bash
    curl --request GET \
      --url http://localhost:3000/api/v1/inventories/{id}
    ```
    El servicio de inventario se comunicará internamente con el servicio de productos para obtener la información. La respuesta mostrará los detalles del producto y la cantidad de inventario.
---

## **Pruebas de Código**

### **Ejecutar Pruebas de Integración y de Cobertura**

1.  **Servicio de Productos (Java)**
    Desde la carpeta `product-service`, ejecuta el siguiente comando para correr las pruebas y generar un reporte de cobertura.

    ```bash
    ./mvnw clean test
    ```
    El reporte de cobertura se genera en `target/site/jacoco/index.html`.

2.  **Servicio de Inventario (Node.js)**
    Desde la carpeta `inventory-service`, usa el siguiente comando para correr las pruebas y ver el resumen de cobertura en la consola.

    ```bash
    npm test -- --coverage
    ```
    El reporte de cobertura detallado se encuentra en la carpeta `coverage/lcov-report/index.html`.
