# 📱 TiendaStore - Aplicación Móvil Android

Cliente móvil oficial para la plataforma de comercio electrónico **TiendaStore**. Desarrollada con tecnologías modernas de Android para ofrecer una experiencia de usuario fluida y robusta.

## 🚀 Características

- **Autenticación Segura**: Login y Registro de usuarios conectados al backend.
- **Catálogo de Productos**: Visualización de productos con imágenes y detalles.
- **Carrito de Compras**: Gestión local del carrito con persistencia y cálculos en tiempo real.
- **Panel de Administración**:
  - CRUD completo de productos (Crear, Editar, Eliminar).
  - **Recurso Nativo**: Selección de imágenes desde la galería (Photo Picker).
- **Integraciones**:
  - **API Externa**: Generación de avatares automáticos con [UI Avatars](https://ui-avatars.com).
  - **Backend Health Check**: Verificación de estado del servidor al inicio.
- **UI/UX**:
  - Diseño **Material Design 3**.
  - Animaciones fluidas y transiciones.
  - Validación de formularios en tiempo real.

## 🛠️ Stack Tecnológico

- **Lenguaje**: Kotlin
- **UI Toolkit**: Jetpack Compose
- **Arquitectura**: MVVM (Model-View-ViewModel)
- **Red**: Retrofit 2 + OkHttp + Gson
- **Carga de Imágenes**: Coil
- **Asincronía**: Coroutines & Flow
- **Testing**: JUnit 4, MockK, Turbine

## ⚙️ Configuración y Ejecución

### Requisitos Previos

- Android Studio Koala o superior.
- JDK 17.
- Backend de TiendaStore ejecutándose (ver instrucciones en carpeta `backend/`).

### Pasos para ejecutar

1.  Clona este repositorio.
2.  Abre la carpeta en **Android Studio**.
3.  Espera a que Gradle sincronice las dependencias.
4.  **Configura la URL del Backend**:
    - Abre `app/src/main/java/com/example/tiendastore/data/remote/RetrofitClient.kt`.
    - Asegúrate de que `BASE_URL` apunte a tu servidor (por defecto `http://10.0.2.2:8080/` para el emulador).
5.  Ejecuta la app en un emulador o dispositivo físico.

## 🧪 Testing

El proyecto incluye pruebas unitarias para los ViewModels críticos.

Para ejecutar las pruebas:

```bash
./gradlew testDebugUnitTest
```

## 📦 Generación de APK

Para generar un APK firmado para producción:

1.  Ve a **Build > Generate Signed Bundle / APK**.
2.  Selecciona **APK**.
3.  Usa el keystore del proyecto (`keystore.jks`) o crea uno nuevo.
4.  Selecciona la variante **release**.

## 👥 Autor

Proyecto desarrollado para la asignatura de Aplicaciones Móviles.
