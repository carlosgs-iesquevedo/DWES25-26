# Cómo activar perfiles en Spring Boot / Maven

## Qué queremos hacer
* Activar dos perfiles (dev y prod)
* dev: 
  * Base de datos H2 en memoria
  * Carga de datos de prueba desde aplicación (create-drop)
  * Logging detallado
  * DevTools activadas
  * Pebble cache desactivado
* prod:
  * Base de datos postgres en docker (persiste los datos)
  * NO se cargan datos de prueba (se cargan desde el contenedor docker para que la app se los encuentre)
  * Optimizado para Docker (logging mínimo)
  * DevTools desactivadas
  * Pebble cache activado

## Instrucciones
###  application.properties

1. Llevar propiedades relacionadas del application.properties a nuevo fichero application-dev.properties
2. Crear fichero application-prod.properties
3. En aplication.properties activar dinámicamente el perfil:
   ```
   spring.profiles.active=@activatedProperties@ 
   ```
   Con esto el valor que le llegue en el arranque a la aplicación (dev o prod) lo propaga al maven (pom.xml)
de modo que cargue la librería de bases de datos apropiadas (h2 o postgres)

### pom.xml
Crear sección <profiles>
    ```xml
   <profiles>
    <profile>
      <id>dev</id>
      <activation>
        <activeByDefault>true</activeByDefault>
      </activation>
      <properties>
        <activatedProperties>dev</activatedProperties>
      </properties>
      <dependencies>
        <dependency>
          <groupId>com.h2database</groupId>
          <artifactId>h2</artifactId>
          <scope>runtime</scope>
        </dependency>
      </dependencies>
    </profile>
    <profile>
      <id>prod</id>
      <properties>
        <activatedProperties>prod</activatedProperties>
      </properties>
      <dependencies>
        <dependency>
          <groupId>org.postgresql</groupId>
          <artifactId>postgresql</artifactId>
          <scope>runtime</scope>
        </dependency>
      </dependencies>
    </profile>
    </profiles>
    ```
### Empaquetar (generar el jar) con maven desde Terminal
#### JAVA_HOME
Apuntar la variable de entorno JAVA_HOME al directorio donte tengamos instalado el JDK

#### Versión del jar para profile dev
Como dev es el perfil por defecto, no hace falta explicitarlo
```
mvnw package 
```
Si los tests fallan y no queremos detenernos en este momento a repararlos o simplemente queremos agilizar el proceso:
```
mvnw package -DskipTests
```
#### Versión del jar para prod
```
mvnw package -Pprod
```

### Empaquetar con comando mvn ejecutado desde imagen docker
Se puede ejecutar el comando mvn desde una imagen que contenga los binarios de maven
```
docker pull maven:3-eclipse-temurin-25-alpine
docker run -it --rm -v "$(pwd):/build" -v "$HOME/.m2:/root/.m2" -v "$(pwd)/target:/build/target" -w /build maven:3-eclipse-temurin-25-alpine mvn package -DskipTests
```

### Empaquetar (generar el jar) con maven desde Panel maven de IntelliJ
1. Abrir el panel Maven
2. Botón derecho en Lifecycle -> package
3. Modify Run Configuration
4. En el comando añadir -DskipTests -Pprod


### Desplegar en docker
#### Para perfil dev
La base de datos está en memoria así que solo tenemos un servicio
```
docker compose -f docker-compose-dev.yml up
```
#### Para perfil prod
```
docker compose -f docker-compose-prod.yml up
```
Si el servicio de base de datos tarda en arrancar, el servicio app puede empezar a dar errores.

Se puede sofisticar la dependencia entre los dos servicios con algún healthceck

A falta de eso, para hacer prueba de concepto se puede con arrancar cada servicio por separado
```
docker compose -f docker-compose-prod.yml up db
docker compose -f docker-compose-prod.yml up app
```
Si se quiere incorporar algún cambio en el código hay que rehacer el build

```
mvnw package -Pprod
docker compose -f docker-compose-prod.yml build app
docker compose -f docker-compose-prod.yml up app
```

Si se quiere automatizar un poco más este proceso, 
se puede incluir el comando de empaquetado en el Dockerfile

### Comprobaciones
1. En el arranque de la app, después del banner, debe aparecer una línea con el perfil activo
```
The following 1 profile is active: "prod"
```








