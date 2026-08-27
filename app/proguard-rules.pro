# app/proguard-rules.pro
-keep class * extends androidx.room.RoomDatabase
-keep @androidx.room.Entity class *
-keep @androidx.room.Dao class *
-keepclassmembers class * {
    @androidx.room.* <methods>;
}

-keep class com.google.zxing.** { *; }
-dontwarn com.google.zxing.**

-keepclassmembers class kotlinx.coroutines.** { *; }
-dontwarn kotlinx.coroutines.**

-keep class androidx.camera.** { *; }
-dontwarn androidx.camera.**

-keepattributes Signature
-keepattributes *Annotation*
-keepclassmembers class * {
    @androidx.annotation.Keep *;
}