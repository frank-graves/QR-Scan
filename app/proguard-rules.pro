# AppLens / proguard-rules.pro

# --- kotlinx.serialization ---
# Los serializers generados se referencian por reflection desde el plugin de
# serialización; hay que conservarlos junto con sus anotaciones.
-keepattributes *Annotation*, InnerClasses
-dontnote kotlinx.serialization.AnnotationsKt
-keep,includedescriptorclasses class org.foss.lens.**$$serializer { *; }
-keepclassmembers class org.foss.lens.** {
    *** Companion;
}
-keepclasseswithmembers class org.foss.lens.** {
    kotlinx.serialization.KSerializer serializer(...);
}

# --- Ktor ---
-keep class io.ktor.** { *; }
-dontwarn io.ktor.**
-dontwarn org.conscrypt.**
-dontwarn org.bouncycastle.**
-dontwarn org.openjsse.**

# --- Room ---
-dontwarn androidx.room.paging.**
