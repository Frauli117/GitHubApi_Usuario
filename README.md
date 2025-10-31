GitHubApi_Usuario
Cliente de escritorio hecho en JavaFX que permite consultar un perfil público de GitHub, listar hasta 100 repositorios y ver el detalle con gráfico circular de lenguajes por repositorio.

✨ Características

Búsqueda por usuario botón Buscar.

Encabezado de perfil: avatar, nombre/login, bio, seguidores/siguiendo, ubicación y blog.

Tabs: Repos (tabla) y Detalle (info del repo + PieChart de lenguajes).

Tabla con: Nombre, ⭐ Stars, Forks, Lenguaje principal, Última actualización (formato absoluto, zona America/Costa_Rica).

Filtro por nombre de repo.

Placeholders cuando faltan datos (p. ej. “Sin lenguaje”, “No hay datos”).

Manejo de errores y loader visible durante las consultas.

🧰 Stack

JDK 21+ (probado en JDK 23)

JavaFX 21.0.3

Maven (javafx-maven-plugin 0.0.8)

Jackson (core, databind, annotations)

HTTP Client de Java 11+

🚀 Ejecución

Con Maven mvn clean javafx:run NetBeans / IntelliJ / Eclipse

Importar como Maven Project.

Ejecutar la clase githubclient.App.

🌐 Endpoints usados

GET /users/{username}

GET /users/{username}/repos?per_page=100&sort=updated

GET /repos/{owner}/{repo}/languages

Rate limit sin token: ~60 requests/hora.

🖼️ Capturas
