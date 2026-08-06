# Robotsito-Esp32
«Grabar y Reproducir»: memoria de movimientos para el Robot ESP32

# Módulo de GrabacionServlet

Este módulo se encarga de recibir las peticiones enviadas desde `control.jsp` mediante el parámetro `accion`, el cual determina la tarea que debe ejecutar el servlet.

Para gestionar correctamente las solicitudes, el servlet incorpora diferentes componentes y propiedades, entre las que se encuentran:

- **Sesión (`HttpSession`)**, utilizada para almacenar y mantener información del usuario durante la ejecución de la aplicación.
- **`AtomicBoolean`**, empleado para controlar el estado de la grabación de forma segura y evitar conflictos entre múltiples hilos de ejecución.
- **`ExecutorService`**, encargado de administrar los procesos en segundo plano, evitando bloqueos o fallos en el servidor y permitiendo que la grabación se ejecute de manera eficiente.

El módulo implementa los métodos **`doPost()`** y **`doGet()`**, los cuales procesan las solicitudes provenientes de `control.jsp`. A través de estos métodos se atienden las diferentes acciones disponibles, como:

- Iniciar la grabación.
- Detener la grabación.
- Reproducir la grabación.
- Limpiar la grabación.

Además, el servlet dispone de métodos encargados de iniciar, ejecutar y monitorear el proceso de grabación, respondiendo en tiempo real sobre el estado de la operación. La comunicación con la interfaz se realiza mediante respuestas en formato **JSON**, permitiendo actualizar dinámicamente la información mostrada al usuario sin necesidad de recargar la página.

## Funcionamiento del módulo de grabación

Una vez ejecutado el proyecto y establecida la conexión con el robot, en la barra superior de la aplicación encontrará la opción **Grabar**. Al ingresar a este módulo se mostrará una interfaz con los controles necesarios para registrar y reproducir movimientos del robot.

### Iniciar una grabación

1. Oprima el botón **Grabar**.
2. La aplicación mostrará los botones de movimiento predeterminados del robot.
3. Cada vez que presione uno de estos botones, el movimiento quedará registrado.
4. En la parte inferior de la pantalla podrá visualizar el estado de la grabación, indicando la cantidad de pasos almacenados hasta el momento.

### Detener la grabación

Una vez haya registrado todos los movimientos deseados, oprima el botón **Detener**. Esta acción finalizará la grabación y la dejará lista para su reproducción.

### Reproducir la grabación

Después de detener la grabación, oprima el botón **Reproducir**. El robot ejecutará exactamente la secuencia de movimientos registrada.

Durante la reproducción, en la parte inferior de la ventana se mostrará el progreso mediante el indicador:

```text
Paso actual / Total de pasos
```

Al finalizar la reproducción, la aplicación mostrará un mensaje indicando que el proceso ha concluido correctamente.

### Limpiar la grabación

Si desea descartar la secuencia registrada, oprima el botón **Limpiar**. Esta opción eliminará todos los pasos almacenados, permitiéndole iniciar una nueva grabación desde cero.

## Decisiones técnicas tomadas

Para implementar el funcionamiento del módulo de grabación se optó por crear una nueva clase dentro del modelo, cuya responsabilidad es encapsular toda la información relacionada con una sesión de grabación en un único objeto.

Esta clase almacena los atributos necesarios para gestionar el proceso, entre ellos:

- La lista de comandos registrados.
- El tiempo asociado a cada comando.
- El estado actual de la grabación.

Además de contener estos datos, la clase también encapsula los métodos utilizados por `GrabacionServlet` para administrar el ciclo de vida de la grabación.

Esta decisión de diseño permitió centralizar toda la lógica relacionada con la grabación en una única entidad, evitando la creación de múltiples variables de sesión para cada atributo. Como resultado, se redujo la duplicación de código, se mejoró la organización del proyecto y se evitó que el servlet concentrara demasiadas responsabilidades, previniendo así el conocido problema del **código espagueti**.

Gracias a esta estructura, el código es más limpio, modular, fácil de mantener y de comprender, facilitando futuras modificaciones o ampliaciones del módulo de grabación.
