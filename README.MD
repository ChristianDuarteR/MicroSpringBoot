# MicroSpring: AREP - Christian Duarte - TALLER DE ARQUITECTURAS DE SERVIDORES DE APLICACIONES, META PROTOCOLOS DE OBJETOS, PATRÓN IOC, REFLEXIÓN

Este proyecto consiste en la creacion de un FrameWork basado en anotaciones y IOC(Inversion de Control)
de esta manera el flujo de el codigo no lo manejara el desarrollador y solo debera centrarse en crear los
controladores, el codigo se encargara de buscarlos y dejarlos funcionesles 
con anotaciones sencillas como @GetMapling, @RestMapping, RestController. 

## Getting Started

Estas instrucciones te guiarán para obtener una copia del proyecto y ejecutarlo en tu máquina local para desarrollo y pruebas.

### Prerequisites

Para instalar y ejecutar el software, necesitas tener instalado Java Development Kit (JDK) 8 o superior.

- **Java Development Kit (JDK):** Puedes descargarlo desde [la página oficial de Oracle](https://www.oracle.com/java/technologies/javase-jdk11-downloads.html) o utilizar OpenJDK.

### Installing

Sigue estos pasos para configurar el entorno de desarrollo:
1. Abrir una terminal git en su máquina. Puedes descargar Git desde [la página oficial](https://git-scm.com).
2. Clonar el repositorio con el comando: git clone https://github.com/ChristianDuarteR/MicroSpringBoot.git Esto creará una copia del proyecto en tu máquina local.
3. Abrir el proyecto en tu IDE favorito (Visual Studio, IntelliJ, NetBeans, etc.).
4. Compilar el proyecto con Maven usando el comando: mvn package
5. Ejecutar el proyecto utilizando la clase que contiene el método `main`: `SpringECI.
6. Verás que el servidor está listo y corriendo en el puerto 8080.
7. Sera capaz de hacer llamado a todos los servicios que haya definido en sus Controladores

## Running the tests

Para ejecutar las pruebas, utiliza el siguiente comando: mvn test

Esto ejecutará todas las pruebas en el proyecto y generará un informe detallado sobre las pruebas que se completaron con éxito.

## And coding style tests

1. testLoadControllers(): Verifica que los controladores se carguen correctamente desde el paquete base especificado.
2. testHandleApiRequest(): Simula una solicitud GET a una ruta específica (/api/greeting) y verifica que el servidor responda correctamente.
3. testDetermineContentType(): Prueba la lógica que determina el tipo de contenido (Content-Type) de la respuesta en función del objeto de respuesta.
4. testSendJSONResponse(): Verifica que el método de envío de respuestas JSON funcione correctamente.
5. testSendErrorResponse(): Verifica que las respuestas de error se envíen correctamente.

![img.png](img.png)

## Built With

* [Maven](https://maven.apache.org/) - Dependency Management

## Architecture

Este proyecto utiliza una arquitectura cliente-servidor, donde los clientes envían solicitudes HTTP al servidor. El servidor maneja la lógica de los servicios REST, responde con datos en formato JSON y HTML, y también proporciona archivos estáticos como HTML, CSS e imágenes.

## REST Endpoints

El servidor proporciona los siguientes endpoints REST:

## Static Files

El servidor también puede servir archivos estáticos, como HTML, CSS, e imágenes, ubicados en una carpeta especificada por el desarrolador gracias al metodo staticFiles(), por defecto /webroot.

## Authors

* **Christian Duarte


