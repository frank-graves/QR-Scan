# QR-Scan — Talara Motors · Lens

Escáner de códigos QR pensado para el taller **Talara Motors**: apunta la cámara a un vehículo y la app clasifica el contenido — un activo genérico o la ficha de un vehículo — lo persiste en Firestore y habilita la **autocompletación de la ficha** con los datos que ya viajaban en el código.

Privado, sin telemetría, sin CDNs de terceros: todo corre localmente y se sincroniza contra tu propia instancia de Firebase.

---

## ✨ Características

- **Escaneo en vivo** con la cámara (ML Kit, GPU-composited) y arranque instantáneo de la UI.
- **Clasificación inteligente de QR**: distinguimos el payload talar (`talara.vehicle.v1`) del activo genérico (`lens.asset.v1`) y de cualquier código ajeno.
- **Modo placa desnuda**: un QR que solo contiene el texto de la placa también abre la ficha.
- **Fichas persistentes** en Firestore, con historial local de escaneos sin conexión.
- **Doble provisión de datos**: síncrono a Firestore o *mock* para desarrollo sin red.
- **Rendimiento para hardware humilde**: objetivo **< 100 ms FCP** en dual-core i3 / 4 GB RAM, sin dependencias pesadas de animación.
- **100 % autoalojable y auditable** — cero telemetría hacia el exterior.

---

## 🧩 Arquitectura (Clean Architecture)

Separación estricta **Dominio → Infraestructura**, regida por inyección de dependencias **Koin**. La regla de dependencias apunta hacia dentro: el dominio no conoce a Android, a Firestore ni a la cámara.

```
src/main/java/org/foss/lens/
├── domain/                  ← Núcleo: modelos y contratos, sin dependencias externas
│   ├── Asset.kt             Activo genérico + clasificador (lens.asset.v1)
│   ├── AssetRepository.kt   Interfaz de persistencia de activos
│   ├── Codex.kt             Abstracción de la cámara / decodificador QR
│   ├── ScanState.kt         Máquina de estados del escaneo
│   └── vehicle/
│       ├── Vehicle.kt          Ficha del taller (plate = clave natural)
│       ├── VehicleCodec.kt     Contrato + parser del QR talar.vehicle.v1
│       └── VehicleRepository.kt Persistencia de fichas
│
├── infrastructure/          ← Puertos hacia el mundo real (cámara, red)
│   ├── Lens.kt / CameraLens.kt
│   ├── CodexDecoder.kt         ML Kit
│   └── NetworkMonitor.kt
│
├── data/                    ← Adaptadores de persistencia
│   ├── local/                  Room (AssetDao, ScanHistoryDao, LensDatabase)
│   ├── sync/AssetSyncer.kt
│   └── vehicle/VehicleStore.kt
│
├── remote/                  ← Adaptadores de la fuente de verdad
│   ├── VehicleGateway.kt       Abstracción de la puerta de vehículos
│   ├── FirestoreVehicleGateway.kt
│   ├── AssetSyncGateway.kt
│   └── MockApiGateway.kt
│
├── ui/                      ← Jetpack Compose (LensApp, screens/, theme/)
├── di/AppModule.kt              Wiring Koin
├── observability/              AppLogger, CrashHandler, GoldenSignals
└── MainActivity.kt / ScribeApplication.kt
```

**Reglas que respetamos:**

1. El **Dominio no importa** clases de Android, Firestore ni de la cámara.
2. Las **interfaces** viven en el dominio; sus implementaciones concretas (Firestore, Room, ML Kit) se inyectan vía **Koin** en `infrastructure/`, `data/` y `remote/`.
3. Cambiar la base de datos o la cámara no toca una sola línea de la ficha de negocio.

---

## 📦 Formato del Código QR

Los QR que esta app reconoce son **JSON UTF-8** con una clave `schema` versionada. Dos esquemas conviven:

| Esquema                     | Propósito                                          |
| --------------------------- | -------------------------------------------------- |
| `talara.vehicle.v1`         | Ficha de vehículo del taller (autocompletado).     |
| `lens.asset.v1`             | Activo genérico (inventario / clasificación).      |

> Un QR en **texto plano** con solo una placa (p. ej. `ABC-123`) también se acepta: la app lo *normaliza* y resuelve la ficha. Esta es la generosidad que menciona `JsonVehicleCodec` — no todo taller imprime JSON.

### `talara.vehicle.v1` — Ficha del vehículo

Contrato definido en `VehicleQrContract`. La clave **`plate` es obligatoria** y es la clave natural del documento en Firestore: dos QR con la misma placa jamás duplican la ficha. Las fechas viajan **ISO-8601** (`yyyy-MM-dd`), igual que en Firestore.

Campos del JSON → modelo `Vehicle`:

| Clave JSON          | Tipo     | Obligatorio | Nota                                        |
| ------------------- | -------- | :---------: | ------------------------------------------- |
| `schema`            | string   |     ✔️      | Constante, debe ser `talara.vehicle.v1`.    |
| `plate`             | string   |     ✔️      | Se normaliza a mayúsculas, sin guion.       |
| `client`            | string   |             | Nombre del cliente.                         |
| `brand`             | string   |             |                                             |
| `model`             | string   |             |                                             |
| `year`              | int      |             | Acepta número *o* texto (`"2021"`).         |
| `color`             | string   |             |                                             |
| `lastServiceDate`   | string   |             | Último cambio de aceite, `yyyy-MM-dd`.      |
| `nextServiceDate`   | string   |             | Próximo cambio recomendado, `yyyy-MM-dd`.   |

**Ejemplo para probar el escaneo** (genera este payload con cualquier herramienta de QR):

```json
{
  "schema": "talara.vehicle.v1",
  "plate": "ABC-123",
  "client": "Laura Méndez",
  "brand": "Renault",
  "model": "Clio",
  "year": 2020,
  "color": "Gris",
  "lastServiceDate": "2026-02-10",
  "nextServiceDate": "2026-11-10"
}
```

Al escanearlo, la app muestra el formulario **pre-rellenado** — el mecánico solo revisa y confirma.

### `lens.asset.v1` — Activo genérico

`jsonIgnoreUnknownKeys` hace el parser tolerante: campos desconocidos no rompen la lectura (pensado para cuando el esquema del QR evolucione sin que la app se entere).

---

## 📋 Requisitos Previos

Hardware objetivo del diseño: **dual-core i3 · 4 GB RAM · gráficos integrados**. Herramientas:

| Herramienta    | Versión            | Cómo obtenerla (Windows, con [Scoop](https://scoop.sh)) |
| -------------- | ------------------ | ------------------------------------------------------- |
| **Java**       | Temurin 17         | `scoop install java/temurin17-jdk`                      |
| **Android SDK**| compileSdk 34      | Android Studio (SDK Manager) o `scoop install android-sdk` |
| **adb**        | Android Platform Tools | `scoop install adb`                                   |
| **scrcpy**     | (recomendado)      | `scoop install scrcpy` (`scrcpy` para espejar el emulador/dispositivo) |

El `JDK` debe apuntarse con la variable `JAVA_HOME` y el `local.properties` debe contener `sdk.dir` según tu instalación (este archivo **no se sube** al repositorio, ver `.gitignore`).

---

## 🚀 Instalación y Build

```bash
# 1. Clona el repositorio
git clone https://github.com/<tu-usuario>/QR-Scan.git
cd QR-Scan

# 2. (Opcional) abre Android Studio, que genera local.properties automáticamente.
#    Si no usas Android Studio, créalo a mano:
echo "sdk.dir=C:/Users/<tu-usuario>/AppData/Local/Android/Sdk" > local.properties

# 3. Aporta tu google-services.json de Firebase.
#    Descárgalo de Firebase Console → Ajustes del proyecto → Tu app Android.
#    Si es solo una prueba, copia la plantilla y rellena los campos:
cp app/google-services.json.example app/google-services.json
#    Después edítalo con tus project_id, mobilesdk_app_id, package_name y api_key.

# 4. Compila el APK de depuración
./gradlew assembleDebug

# 5. (Opcional) instala y abre en tu dispositivo / emulador
adb install -r app/build/outputs/apk/debug/app-debug.apk
adb shell am start -n org.foss.lens/.MainActivity
```

El APK de depuración queda en `app/build/outputs/apk/debug/app-debug.apk`.

> El **mock** de datos se activa dejando en blanco `lens.mockapiUrl` (ver `gradle.properties`). Aún sin una base Firestore configurada puedes recorrer la app y probar el flujo de escaneo.

---

## 🧑‍⚖️ Licencia

Distribuido bajo [GNU GPL v3](LICENSE).

---

## 🤝 Contribuciones

Consulta [`CONTRIBUTING.md`](CONTRIBUTING.md): cómo levantar Firestore en modo prueba, estándares de código Kotlin/Clean Architecture y el flujo de Pull Request.
