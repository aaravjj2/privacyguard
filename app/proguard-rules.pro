# PrivacyGuard ProGuard Rules

# Keep Melange SDK classes
-keep class com.zeticai.** { *; }
-keep class ai.zetic.** { *; }
-keepclassmembers class com.zeticai.** { *; }

# Keep model classes used by reflection
-keep class com.privacyguard.ml.** { *; }
-keep class com.privacyguard.data.** { *; }

# Keep Gson serialization
-keepattributes Signature
-keepattributes *Annotation*
-keep class com.google.gson.** { *; }
-keep class * implements com.google.gson.TypeAdapterFactory
-keep class * implements com.google.gson.JsonSerializer
-keep class * implements com.google.gson.JsonDeserializer

# Keep data classes for Gson
-keepclassmembers class com.privacyguard.data.DetectionEvent { *; }
-keepclassmembers class com.privacyguard.ml.PIIEntity { *; }

# Strip debug logs in release
-assumenosideeffects class android.util.Log {
    public static int d(...);
    public static int v(...);
}

# Keep Compose
-keep class androidx.compose.** { *; }

# Keep accessibility service
-keep class com.privacyguard.service.PrivacyAccessibilityService { *; }

# Keep boot receiver
-keep class com.privacyguard.service.BootReceiver { *; }

# JNI
-keepclasseswithmembernames class * {
    native <methods>;
}
