# Contribuyendo a QR-Scan — Talara Motors · Lens

Gracias por querer sumarte. Este proyecto se rige por una premisa: **ligero no es primitivo, privado no es feo, eficiente no es sin alma.** Tres principios que revisamos en cada Pull Request — y que aquí abajo dejamos por escrito para que tu trabajo pase la primera sin fricción.

---

## 1. Configurar Firestore en modo de prueba (desarrollo local)

El 90 % del trabajo de *setup* es no romper la base de datos de producción. Para desarrollo local usa una **instancia separada** o el **modo prueba**.

1. En [Firebase Console](https://console.firebase.google.com) crea (o selecciona) el proyecto **de desarrollo**.
2. Registra una app **Android** con el paquete `org.foss.lens`.
3. Descarga el `google-services.json` y colócalo en `app/`.
4. Abre **Cloud Firestore → Reglas** y pon las reglas **en modo prueba** solo para desarrollo (20 líneas de lecturas/escrituras abiertas al mes):

```
rules_version = '2';
service cloud.firestore {
  match /databases/{database}/documents {
    match /{document=**} {
      allow read, write: if false;
    }
  }
}
```

> **Nunca** pongas reglas abiertas (`allow read, write: if true`) en producción y **nunca** subas tu `google-services.json` real al repositorio (lo ignora `.gitignore`). Rota cualquier clave que haya quedado en un commit.

5. La app ya detecta Firestore al arrancar; si prefieres no depender de la red, deja `lens.mockapiUrl` vacío en `gradle.properties` para usar el **mock gateway**.

Una colección típica es `vehicles`, donde el id de cada documento es la **placa normalizada** (= clave natural, igual que en el dominio).

---

## 2. Estándares de código

- **Kotlin moderno y estricto.** Sin `any`; usa `unknown` con *type guards*. Tipos de retorno explícitos en API públicas (excepto en tests/expresiones triviales).
- **No promesas huérfanas.** Todo `suspend` / llamada asíncrona se `await` o maneja con `runCatching` — nunca se traga.
- **Clean Architecture de verdad.** Las **interfaces viven en `domain/`**; Firestore, Room, ML Kit y la cámara son implementaciones inyectadas desde `infrastructure/`, `data/`, `remote/`. El dominio **no** importa Android ni Firebase.
- **Contratos de QR versionados.** Si cambia el payload de un QR, nace `talara.vehicle.v2` / `lens.asset.v2` — el código viejo sigue leyendo v1. No reutilices claves de forma ambigua, y trata `year` con tolerancia de tipo (el plantel acepta número y texto).
- **Nombres con intención.** Variables y funciones cuentan una historia del dominio. Un nombre como `plateNormalizer` dice más que `data`.
- **Comentarios con voz.** Narran *por qué*, no qué (`// ` para razones no obvias). No comentario genéricos tipo `// obtener datos`.
- **Estilo.** Corre las tareas de calidad antes de cada commit:
  ```bash
  ./gradlew detekt
  ./gradlew testDebugUnitTest
  ```
  La configuración de **detekt** vive en `config/detekt/detekt.yml`. No la rebajes para silenciar un hallazgo tuyo: mándale una línea mejor.
- **Sin `console.log`** en producción; usa el logger estructurado de `app/src/main/java/org/foss/lens/observability/AppLogger.kt`.
- **Respeto al hardware.** No agregues librerías de animación pesadas ni dependencias que rompan el presupuesto de ~100 ms FCP / 4 GB de RAM. Antes de sumar una dependencia, pregúntate si el CSS/usuario nativo ya lo resuelve.

---

## 3. Flujo de ramas y Pull Request

### Crear una rama de feature

Las ramas se nombran con prefijo semántico:

```bash
git checkout -b feature/qr-autofill
# o
git checkout -b fix/placa-con-guion
```

### Entregar el trabajo

```bash
git add .
git commit -m "feat(qr): habilita autocompletado de la ficha del vehículo"
git push -u origin feature/qr-autofill
```

Convenciones de commits (convencional), ejemplos:
- `feat(vehicle): wire del código QR en el formulario`
- `fix(codec): tolera year en formato texto`
- `docs(readme): documenta contrato talara.vehicle.v1`
- `refactor(domain): aísla PlateContract del gateway`
- `ci(gradle): añade job detekt`

### Abrir la Pull Request

1. Ve a **GitHub → Pull requests → New pull request** y apunta **hacia `main`**.
2. Título breve + descripción que explique el *porqué* (no el qué) y pruebas realizadas.
3. Checklist en el cuerpo:
   - [ ] `./gradlew detekt` pasa.
   - [ ] `./gradlew testDebugUnitTest` pasa.
   - [ ] El dominio no importa implementaciones concretas (Firestore/Room/ML Kit).
   - [ ] No subí secretos (`google-services.json`, keystores). Revisé `git diff` de archivos sensibles.
4. Un **reviewer** la revisa contra esta guía; espera comentarios y corrige en tu propia rama, *pushed-back* (no rebases compartidas).

> Consejo de seguridad para cada PR: ejecuta un `git status --ignored` antes del push para asegurarte de que tu `google-services.json` y keystores siguen ignorados — y jamás pongas una API key nueva en un `.mock`/`.example` real.

### Ramas largas y rebase

Antes de fusionar, mantén `main` actualizada:

```bash
git fetch origin
git rebase origin/main        # (nunca rebases una rama que hayas compartido ya)
```

---

## Repositorio en una frase

*Documentación honesta del estado real del código es mejor que una hoja de ruta fantasiosa.* Si un esquema cambia, actualiza **este** `README` y el ejemplo `google-services.json.example` al mismo tiempo que el código.
