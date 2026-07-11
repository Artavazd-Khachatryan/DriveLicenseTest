# Most libraries in this app (Compose, Ktor, kotlinx.serialization, SQLDelight,
# Koin, Firebase) ship their own consumer R8 rules — only app-specific gaps go here.

# Keep crash stack traces readable/retraceable.
-keepattributes SourceFile,LineNumberTable
-renamesourcefileattribute SourceFile
