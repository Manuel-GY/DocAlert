# DocAlert 📋

Aplicación Android para controlar el vencimiento de documentos (carnet, permiso de circulación, pasaporte, etc.).

## ✨ Características

- **OCR automático**: Toma una foto del documento y detecta la fecha de vencimiento
- **Categorías**: Cédula, carnet de conducir, permiso de circulación, pasaporte, visa, seguro, etc.
- **Calendario**: Agrega eventos de vencimiento al calendario del teléfono o Google Calendar
- **Notificaciones**: Alertas antes del vencimiento (7, 15, 30, 60 días)
- **Modo offline**: Todo funciona sin conexión a internet

## 🚀 Cómo compilar e instalar

### Requisitos previos

1. **Android Studio** (versión Hedgehog 2023.1.1 o superior)
   - Descarga: https://developer.android.com/studio

2. **JDK 17** (viene incluido con Android Studio)

3. **Dispositivo Android** con depuración USB activada
   - Ve a Configuración → Información del teléfono → Toca 7 veces "Número de compilación"
   - Vuelve a Configuración → Opciones de desarrollador → Activa "Depuración USB"

### Pasos para compilar

1. Abre Android Studio
2. Selecciona `File → Open`
3. Selecciona la carpeta `DocAlert`
4. Espera a que Gradle sincronice (5-10 min primera vez)
5. Conecta tu celular por USB
6. Haz clic en el botón ▶️ (Run)

### Si no tienes celular Android

Puedes usar un emulador:
1. `Tools → Device Manager → Create Device`
2. Selecciona un teléfono (ej: Pixel 6)
3. Descarga la imagen del sistema
4. Ejecuta la app en el emulador

## 📱 Cómo usar la app

### Agregar un documento

1. Toca el botón **+** (esquina inferior derecha)
2. Escribe el nombre del documento
3. Selecciona la categoría
4. Elige la fecha de vencimiento
5. Opcional: Toca "Tomar Foto" para escanear con OCR
6. Toca "Guardar"

### Escanear documento con OCR

1. Al agregar un documento, toca "Tomar Foto"
2. Toma una foto del documento
3. La app detectará la fecha automáticamente
4. Si no detecta, podrás ingresarla manualmente

### Agregar al calendario

1. En la pantalla de detalles del documento
2. Toca "Agregar al calendario"
3. Se creará un evento con recordatorio

### Ver documentos vencidos

- Los documentos aparecen ordenados por fecha de vencimiento
- **Verde**: Vigente
- **Naranja**: Vence pronto
- **Rojo**: Vencido

## 📁 Estructura del proyecto

```
DocAlert/
├── app/src/main/java/com/docalert/
│   ├── MainActivity.kt          # Entry point
│   ├── data/                    # Base de datos y repositorios
│   ├── domain/                  # Modelos y casos de uso
│   ├── ui/                      # Pantallas y componentes
│   ├── viewmodel/               # Lógica de negocio
│   └── util/                    # Utilidades
└── app/src/main/res/            # Recursos
```

## 🛠️ Tecnologías usadas

- **Kotlin** - Lenguaje de programación
- **Jetpack Compose** - UI moderna
- **Room Database** - Almacenamiento local
- **Google ML Kit** - OCR (reconocimiento de texto)
- **CameraX** - Cámara
- **Hilt** - Dependency Injection
- **Material Design 3** - Diseño visual

## ⚠️ Permisos que necesita la app

- **Cámara**: Para tomar fotos de documentos
- **Calendario**: Para agregar eventos de vencimiento
- **Notificaciones**: Para alertas de vencimiento

## 🐛 Solución de problemas

### "Error de sincronización Gradle"
- Verifica que tengas internet
- `File → Invalidate Caches → Invalidate and Restart`

### "No se detecta el dispositivo"
- Activa depuración USB en tu celular
- Prueba otro cable USB

### "Error de compilación"
- `Build → Clean Project`
- `Build → Rebuild Project`

## 📝 Próximos pasos (cuando tengas Android Studio)

1. [ ] Instalar Android Studio
2. [ ] Abrir proyecto en Android Studio
3. [ ] Sincronizar Gradle (esperar)
4. [ ] Conectar celular Android
5. [ ] Activar depuración USB
6. [ ] Compilar y ejecutar (botón ▶️)
7. [ ] Probar agregar un documento
8. [ ] Probar OCR con foto
9. [ ] Probar agregar al calendario

## 📄 Licencia

Proyecto creado para uso personal.
