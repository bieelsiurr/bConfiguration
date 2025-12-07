# bConfiguration, the file-in-one solution. ⚙️

## Descripción 💻
bConfiguration es una biblioteca que te permite gestionar todas tus configuraciones en formatos YAML, JSON o almacenarlas directamente en MongoDB. Con esta herramienta, puedes mantener tus configuraciones de forma centralizada y acceder a ellas fácilmente desde tu aplicación.

## Características 🚀
- Soporte para configuraciones en formatos YAML y JSON.
- Posibilidad de almacenar configuraciones en una base de datos MongoDB.
- Facilidad de uso para acceder y manipular configuraciones.
- Flexibilidad para integrarse en diferentes tipos de aplicaciones.

## Instalación 🔝
1. Descarga el repositorio de bConfiguration desde [GitHub](https://github.com/bieelsiurr/bConfiguration).
2. Compila el proyecto localmente utilizando Maven:

```bash
mvn clean install
```

3. Añade bConfiguration como dependencia en tu proyecto Maven agregando lo siguiente a tu archivo pom.xml:

```xml
<dependency>
    <groupId>me.biiee3l</groupId>
    <artifactId>bConfiguration</artifactId>
    <version>1.0.0</version> <!-- Reemplaza con la versión actual -->
</dependency>
```

## Uso ❓
Una vez añadida la dependencia en tu proyecto, puedes importar la biblioteca y utilizarla de la siguiente manera:

```java
import me.biiee3l.bconfig.config.Configuration;
import me.biiee3l.bconfig.config.types.YamlConfiguration;

// Create a new YamlConfiguration instance
Configuration configuration = new YamlConfiguration(new File("filename.yml"));

// Set this where you like to load the file
configuration.load();

// Start customizing your Configuration file
configuration.set("key", "value");

// And you can also get values too!
String myFirstValue = configuration.getString("key_of_the_string");
int myNumber = configuration.getInt("key_of_the_number");

// Save the configuration file
configuration.save();
```
