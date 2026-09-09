# SPEC — Lens v2 · Escáner QR universal + registro de activos (PC)

> Fase 0 — Contrato de producto y datos. Antes de escribir UI/feature, se congela aquí
> QUÉ hace la app, QUÉ formato tienen los datos y CÓMO llegan al Excel vía mockAPI.
> Estado: **borrador para validar** — los puntos [ ] son los que quedan abiertos para quien
> pidió el rework.

---

## 1. Identidad del producto

**Lens** deja de ser "solo un escáner" y pasa a ser:

> Un escáner de QR **universal** que, cuando el contenido es un JSON de inventario de PC,
> lo trata como **registro de activo** (confirmar → guardar local → sincronizar a mockAPI
> → consultable en Excel). Cualquier otro contenido se trata como escaneo genérico
> (historial).

La clave del diseño: **el escáner no sabe qué es una PC**. Entrega bytes; una capa de
clasificación decide. Si mañana aparece otro tipo de QR estructurado, solo se añade un
clasificador nuevo.

---

## 2. Features congeladas (v1)

| ID | Feature | Criterio de aceptación |
|----|---------|------------------------|
| F1 | Escanear cualquier QR | Cámara abre, vista previa en vivo, decode con rotación (ya implementado). |
| F2 | Clasificar contenido | QR con JSON `schema: "lens.asset.v1"` → flujo activo. Cualquier otro → resultado genérico + historial. |
| F3 | Confirmar antes de enviar | Tras escanear un activo se muestra la info parseada con acciones **Editar / Confirmar y enviar / Descartar**. Nada se envía sin confirmación. |
| F4 | Registro manual | Formulario táctil para registrar una PC sin QR (`origen: "manual"`). Mismo flujo F3-F6. |
| F5 | Detección de "ya registrada" | Al escanear/registrar un `serial` existente en local → aviso con acciones **Ver registro / Actualizar datos**. |
| F6 | Historial local organizado | Tabla **assets** (registro de PCs, clave `serial`) + historial genérico para lo que no es PC. El historial no se mezcla con el inventario. |
| F7 | Sincronización diferida (offline-first) | Sin red: se guarda local con estado `PENDING`. Al volver la red (o al abrir la app): `POST` a mockAPI y estado `SYNCED`. Reintento manual ante error. |
| F8 | Consulta de inventario local | Lista de activos registrados con su estado de sync (Enviado/Pendiente/Error) y acceso al detalle. |
| F9 | UI renovada (Compose/M3) | Pantallas: Scanner, Confirmación de activo, Detalle, Inventario, Registro manual, Historial. Tema oscuro/claro. |

**Fuera de alcance v1** (para no inflar): búsqueda avanzada (se hace en el Excel),
multi-dispositivo en tiempo real, autenticación, edición remota.

---

## 3. Contrato de datos — JSON del activo (v1)

QR esperado (también lo genera el formulario manual):

```json
{
  "schema": "lens.asset.v1",
  "serial": "LNV-TC-M75Q-0001",
  "marca": "Lenovo",
  "modelo": "ThinkCentre M75q",
  "tipo": "desktop",
  "estado": "operativo",
  "ubicacion": "Aula-3",
  "componentes": {
    "cpu": "AMD Ryzen 5 PRO 4650G",
    "ramGb": 16,
    "discoGb": 512,
    "so": "Windows 11 Pro"
  },
  "notas": ""
}
```

Reglas del contrato:

- `schema` **constante** `lens.asset.v1`. Si cambian los campos en el futuro → `v2`
  (el clasificador convive con varias versiones).
- `serial`: **requerido**; es la clave de identidad local (detección de duplicados).
- `marca`, `modelo`, `tipo`, `estado`, `ubicacion`, `componentes.*`, `notas`: **opcionales**;
  un campo ausente no rompe el parseo (queda vacío/oculto en UI).
- `tipo` sugerido: `desktop | laptop | monitor | otro`.
- `estado` sugerido: `operativo | en_reparacion | baja | pendiente`.
- Campos extra desconocidos en el QR: se **conservan y se muestran** en la pantalla de
  confirmación como texto libre (sin romper el envío).

Payload que la app envía a mockAPI: el mismo objeto + `origen` (`"qr" | "manual"`).
mockAPI añade por su cuenta `id` y `createdAt` (no los enviamos nosotros).

---

## 4. Backend de demostración — mockAPI

### Colección a crear en mockAPI: `assets`

Cada recurso debe aceptar **exactamente** esta forma (ejemplo de fila):

```json
{
  "id": "…(lo pone mockAPI)…",
  "createdAt": "…(lo pone mockAPI)…",
  "schema": "lens.asset.v1",
  "serial": "LNV-TC-M75Q-0001",
  "marca": "Lenovo",
  "modelo": "ThinkCentre M75q",
  "tipo": "desktop",
  "estado": "operativo",
  "ubicacion": "Aula-3",
  "componentes": {
    "cpu": "AMD Ryzen 5 PRO 4650G",
    "ramGb": 16,
    "discoGb": 512,
    "so": "Windows 11 Pro"
  },
  "notas": "",
  "origen": "qr"
}
```

### Flujo HTTP

- `POST {baseUrl}/assets` → crear (app → mockAPI)
- `GET  {baseUrl}/assets` → opcional; útil para depurar / poblar el Excel
- `DELETE {baseUrl}/assets/{id}` → opcional (limpieza de pruebas)

> Endpoint, colección y DTO quedan detrás de la interfaz `AssetSyncGateway`, así que si
> mañana el destino es otro (API real, Google Sheets vía Apps Script…), solo cambia una
> implementación.

---

## 5. Tabla Excel (la que consume el plugin de API)

Encabezado de columna **en este orden** (el plugin mapea el JSON de mockAPI a filas):

| createdAt | serial | marca | modelo | tipo | estado | ubicacion | cpu | ramGb | discoGb | so | origen | notas |
|---|---|---|---|---|---|---|---|---|---|---|---|---|

Reglas de mapeo:

- `createdAt` proviene de mockAPI (fecha del registro remoto).
- `componentes.*` se aplana: `cpu`, `ramGb`, `discoGb`, `so`.
- Campo ausente → celda vacía (no se rompe la fila).
- Si un mismo `serial` aparece varias veces (re-registro), la fila **vigente** es la de
  `createdAt` más reciente — la deduplicación se hace en Excel (tabla dinámica / filtro).

---

## 6. Arquitectura (mantiene los principios: ligero, FOSS, testeable)

```
┌─ presentation (Compose/M3) ──────────────────────────────┐
│ Scanner · AssetConfirm · AssetDetail · Inventory ·       │
│ ManualForm · History  +  ViewModels                      │
└───────────────┬──────────────────────────────────────────┘
                │
┌─ domain ──────▼──────────────────────────────────────────┐
│ Codex · Asset · ScanOutcome · Classifier (interfaz)      │
│ Archive · AssetRepository · SyncGateway (interfaces)     │
└───────┬───────────────────────────────┬──────────────────┘
        │                               │
┌─ data ─▼──────────────┐   ┌─ remote ──▼──────────────────┐
│ Room: assets (+estado │   │ Ktor Client → mockAPI        │
│ sync) · history ·     │   │ DTOs + kotlinx.serialization │
│ manual-entry          │   └──────────────────────────────┘
└───────────────────────┘
```

Decisiones técnicas (ADR cortos):

1. **Escáner:** mantener CameraX + ZXing actual (puro, sin Google Play Services → apto F-Droid).
2. **UI:** reescribir la capa de presentación a **Jetpack Compose + Material 3**
   (el proyecto actual es Views/XML; migrar incremental sería más costoso que rehacer la
   presentación reutilizando `domain`).
3. **Red:** **Ktor Client + kotlinx.serialization** (Kotlin puro, corrutinas; sin codegen pesado).
4. **DI:** **Koin** (ligero, sin generación de código).
5. **Persistencia:** **Room + KSP** (en el rewrite se abandona `kapt`). Dos tablas: `assets`
   (con `syncState`) y `history` (escaneos genéricos).
6. **Sync offline:** cola local por estado `PENDING` + disparo al abrir la app y al detectar
   conectividad (`ConnectivityManager`). Sin WorkManager en v1 (la app no necesita
   sincronizar en segundo plano sin estar abierta); se documenta como mejora futura.
7. **Envío:** append-only (una fila nueva por registro confirmado). Si se re-registra una PC,
   se envía una fila nueva con su `createdAt`; el Excel decide la vigente por serial.
   (Evita tener que implementar PUT/DELETE y dedupe remoto en v1.)
8. **F-Droid:** sin trackers, iconos y fuentes locales, build reproducible — ya alineado.

---

## 7. Pantallas (mapa de navegación)

```
ScannerScreen (F1) ── QR genérico ──────────────► Resultado + HistoryScreen
      │
      └─ JSON asset (F2) ──► AssetConfirmScreen (F3: editar/confirmar/descartar)
                                 │
                                 ├─ serial ya existe (F5) ─► "Ya registrada" → AssetDetailScreen / Actualizar
                                 └─ guardar local (PENDING) ─► sync (F7) ─► Inventario (F8)

InventoryScreen (F8) ── FAB ─► ManualFormScreen (F4) ──► mismo flujo de confirmación
```

**Estética (F9):** Material 3 con tema claro/oscuro siguiendo el sistema, edge-to-edge,
tipografía local, micro-animaciones con `AnimatedContent`/`spring`, viewfinder dibujado con
`Canvas`. Prohibido: librerías de animación pesadas, fuentes/iconos de CDN, bloat visual.

---

## 8. Definición de "hecho" — Fase 1 (esqueleto Compose)

- Proyecto compila con Compose + Koin + Room(KSP) configurados.
- Tema M3 aplicado (claro/oscuro).
- `ScannerScreen` muestra el viewfinder en vivo con el escáner actual embebido y emite
  resultados genéricos al historial.
- El clasificador distingue `lens.asset.v1` de contenido plano (sin UI de activo aún).

---

## 9. Pendientes de validación con quien pidió el rework

- [ ] Aprobación de este contrato (features + esquema JSON + flujo de envío).
- [ ] URL del mockAPI y confirmación de que la colección `assets` se crea con estos campos.
- [ ] Confirmar que el Excel se puebla vía el plugin consumiendo `GET /assets` de mockAPI.
- [ ] Si en clase les dieron un formato de QR distinto (con otros campos), traer UN ejemplo
      real para ajustar `lens.asset.v1` antes de codificar.
