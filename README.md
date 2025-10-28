TiendaStore (EV2)
=================

Resumen
-------
Aplicación de Tienda offline con Room, Jetpack Compose y ViewModel. Incluye login/registro, catálogo, carrito/checkout y panel admin con CRUD de productos e imágenes.

Arquitectura (MVVM)
-------------------
- `data/`: Room (AppDatabase, DAOs, Entities) + helper de imágenes.
- `domain/validation`: validaciones desacopladas (`AuthValidator`, `ProductValidator`).
- `viewmodel/`: estado con `StateFlow` (Auth, Product, Cart).
- `ui/`: pantallas Compose, navegación por estado (enum `Screen`) y componentes.

Persistencia
------------
- Room para: usuarios, sesión, productos, carrito.
- Semillas: usuario admin (admin@local/admin123) y productos demo al inicio si la DB está vacía.
- Imágenes: copia en almacenamiento interno (`filesDir/images`) y se guarda su `imagePath`.

Flujos principales
------------------
- Autenticación: login por correo + contraseña; registro con nombre/correo/contraseña; edición de perfil.
- Catálogo: Home en grilla 2×, detalle con botón "Agregar al carrito".
- Carrito: badge en TopBar, vista rápida (bottom sheet) con stepper, checkout con confirmación y limpieza.
- Admin: listado con búsqueda por ID, CRUD con imagen (almacenamiento interno) y confirmación al cambiar imagen/eliminar.

Animaciones y feedback
----------------------
- Se quitó la transición global (Crossfade) para evitar el “flash” blanco entre pantallas.
- Botones con micro-animación de presión (escala) y AlertDialog con entrada/salida animada (`AnimatedAlert`).
- Snackbar “Producto agregado” y loader al cargar imágenes.

Estructura del código
---------------------
- `app/src/main/java/com/example/tiendastore/data/DataBaseHelper.kt`: Entities, DAOs, `AppDatabase`, utilidades de imágenes y helpers de mapeo.
- `app/src/main/java/com/example/tiendastore/domain/validation/*`: validadores de autenticación y productos.
- `app/src/main/java/com/example/tiendastore/viewmodel/*`: `AuthViewModel`, `ProductViewModel`, `CartViewModel`.
- `app/src/main/java/com/example/tiendastore/ui/navigation/AppNavigation.kt`: router por estado (`enum class Screen`).
- `app/src/main/java/com/example/tiendastore/ui/view/*`: pantallas (Login, Register, Home, Detalle, Admin, Checkout, Perfil).
- `app/src/main/java/com/example/tiendastore/ui/view/components/*`: componentes reutilizables (botones animados, alertas, sheet de carrito, loader de imagen).

Requisitos de la rúbrica (mapeo)
--------------------------------
- IE 2.1.1 Interfaz/navegación: Home, Detalle, BackHandler, acciones claras en TopBar.
- IE 2.1.2 Formularios/feedback: Login, Registro, Editar perfil con errores por campo y mensajes.
- IE 2.2.2 Animaciones/retro: botones y alertas animadas, snackbars, loader (sin transición global que cause flash).
- IE 2.3.1 Modularidad/persistencia: MVVM, separación por capas y Room.
- IE 2.3.2 Colaboración/entrega: repo con README; sugerido tablero (Trello/Similar) y capturas.
- IL 2.4 Recurso nativo: SQLite/Room (DB local) considerado como recurso nativo por la cátedra.

Cómo ejecutar
-------------
1) Abrir el proyecto en Android Studio y sincronizar Gradle.
2) Ejecutar en un dispositivo/emulador (minSdk 25, targetSdk 36).
3) Ingresar con: correo `admin@local`, contraseña `admin123`.
4) Probar: Home (2 columnas), agregar al carrito, abrir bottom sheet, “Ir a pagar”, CRUD en Admin y edición de perfil.

Notas
-----
- Si tu evaluación exige explícitamente “dos recursos nativos”, aquí se contabiliza `SQLite/Room`. El flujo de imágenes usa almacenamiento interno; si tu docente no considera Photo Picker como recurso independiente, se mantiene Room como principal.
- Para la entrega, añade capturas de las pantallas clave (Login, Home, Detalle, Carrito, Admin) a este README.
