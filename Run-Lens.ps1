# Detener ejecución si ocurre un error crítico
$ErrorActionPreference = "Stop"

Write-Host ">>> 1. Verificando conexión ADB..." -ForegroundColor Cyan
$deviceList = adb devices
if (!($deviceList -match "(?m)^[^\s]+\s+device$")) {
    Write-Host "[ERROR] No se detectó ningún dispositivo (o está 'unauthorized'). Verifica el cable y la depuración USB." -ForegroundColor Red
    exit
}
Write-Host "[OK] Dispositivo detectado." -ForegroundColor Green

Write-Host ">>> 2. Levantando Scrcpy..." -ForegroundColor Cyan
# Lanza scrcpy en un proceso independiente y oculta su ventana de consola (si es posible)
try {
    Start-Process -FilePath "scrcpy" -ArgumentList "--max-size 1080 --stay-awake" -WindowStyle Hidden -ErrorAction SilentlyContinue
    Write-Host "[OK] Scrcpy ejecutándose en segundo plano." -ForegroundColor Green
} catch {
    Write-Host "[WARN] Scrcpy no encontrado en el PATH. Omitiendo espejo de pantalla..." -ForegroundColor Yellow
}

Write-Host ">>> 3. Compilando APK (Debug)..." -ForegroundColor Cyan
# Ejecuta Gradle. En PowerShell usamos .\ para ejecutar binarios locales.
.\gradlew.bat clean assembleDebug
if ($LASTEXITCODE -ne 0) {
    Write-Host "[ERROR] La compilación de Gradle ha fallado." -ForegroundColor Red
    exit
}

$apkPath = "app\build\outputs\apk\debug\app-debug.apk"
if (!(Test-Path $apkPath)) {
    Write-Host "[ERROR] No se encontró el APK compilado en $apkPath" -ForegroundColor Red
    exit
}

Write-Host ">>> 4. Instalando APK en el dispositivo..." -ForegroundColor Cyan
adb install -r -t $apkPath
if ($LASTEXITCODE -ne 0) {
    Write-Host "[ERROR] Fallo al instalar el APK." -ForegroundColor Red
    exit
}

Write-Host ">>> 5. Lanzando LensActivity..." -ForegroundColor Cyan
# Se puede usar el punto para indicar ruta relativa al paquete base
adb shell am start -n org.foss.lens/.presentation.LensActivity

Write-Host ">>> 6. Conectando a Logcat..." -ForegroundColor Cyan
# Damos 2 segundos para que la app inicie su proceso en Android
Start-Sleep -Seconds 2

# Obtenemos el PID desde el entorno Linux de Android
$appPid = (adb shell pidof -s org.foss.lens).Trim()

if ([string]::IsNullOrWhiteSpace($appPid)) {
    Write-Host "[WARN] No se pudo capturar el PID de la app. Mostrando logcat general (usa Ctrl+C para salir)." -ForegroundColor Yellow
    adb logcat -s "AppLogger"
} else {
    Write-Host "[OK] Capturando logs para PID: $appPid (Presiona Ctrl+C para salir)." -ForegroundColor Green
    adb logcat --pid=$appPid
}