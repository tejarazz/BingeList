# BingeList ProGuard Rules

# --- General Android & Kotlin ---
-keepattributes *Annotation*, Signature, EnclosingMethod
-keep class kotlin.coroutines.Continuation { *; }

# --- Room Database ---
# Room uses reflection to find the generated implementation of the database and DAOs.
-keep class * extends androidx.room.RoomDatabase
-keep class * extends androidx.room.Entity
-keep @androidx.room.Entity class * { *; }
-dontwarn androidx.room.paging.**

# --- Retrofit & Gson ---
# Keep Retrofit interfaces and their method annotations
-keepattributes RuntimeVisibleAnnotations, RuntimeVisibleParameterAnnotations
-keepclassmembers,allowobfuscation interface * {
    @retrofit2.http.* <methods>;
}
-dontwarn retrofit2.**
-keep class retrofit2.** { *; }

# Gson: Keep DTOs and their fields to prevent breakage during JSON parsing
-keep class com.example.bingelist.data.remote.model.** { *; }
-keepattributes InnerClasses
-dontwarn sun.misc.**
-keep class com.google.gson.** { *; }

# --- Coil 3 (Image Loading) ---
# Coil uses reflection for some of its components and relies on OkHttp
-keep class coil3.** { *; }
-dontwarn coil3.**
-keep class okhttp3.** { *; }
-dontwarn okhttp3.**

# --- Hilt (Dependency Injection) ---
# Hilt/Dagger generated classes need to be kept
-keep class dagger.hilt.** { *; }
-keep class com.example.bingelist.di.** { *; }
-dontwarn dagger.hilt.android.internal.**

# --- Paging 3 ---
-dontwarn androidx.paging.compose.**

# --- BingeList Domain Models ---
# Keep domain models if they are passed through reflection or serialization layers
-keep class com.example.bingelist.domain.model.** { *; }
