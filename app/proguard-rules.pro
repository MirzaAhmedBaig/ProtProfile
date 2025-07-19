# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

# Uncomment this to preserve the line number information for
# debugging stack traces.
#-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile


# --- General Android/Google Rules ---

# Keep all Activity subclasses
-keep public class * extends android.app.Activity
-keep public class * extends androidx.activity.ComponentActivity

# Keep Application subclass (if used)
-keep public class * extends android.app.Application

# Keep ViewModel subclasses (for Compose/MVVM projects)
-keep public class * extends androidx.lifecycle.ViewModel

# --- Kotlin/Jetpack Compose ---

# Keep Jetpack Compose internal and generated classes
-keep class androidx.compose.** { *; }
-keep class androidx.lifecycle.viewmodel.** { *; }
-dontwarn androidx.compose.**

# --- Kotlin Serialization Support ---

# Keep generated serializers for Kotlinx Serialization
-keep class kotlinx.serialization.** { *; }
-keepclassmembers class ** {
    @kotlinx.serialization.Serializable <fields>;
}
-keep @kotlinx.serialization.Serializable class * { *; }

# Retain serializers for sealed and polymorphic hierarchies
-keep @kotlinx.serialization.Serializable class **

# --- Hilt/Dagger (if used) ---

# Keep generated Hilt/Dagger code (otherwise injection may fail)
-keep class dagger.hilt.** { *; }
-keep class dagger.internal.** { *; }
-keep class javax.inject.** { *; }
-dontwarn dagger.hilt.**

# --- Firebase/Google Play Services ---

# Keep ProGuard for Firebase (general)
-keep class com.google.firebase.** { *; }
-dontwarn com.google.firebase.**
-keep class com.google.android.gms.** { *; }
-dontwarn com.google.android.gms.**

# --- Gson (if used) ---

# Keep Gson model classes (optional, if using Gson)
-keep class com.google.gson.** { *; }
-keepattributes Signature
-keepattributes *Annotation*

# --- Parcelable/Serializable Support ---

# Keep Parcelable implementations
-keep class * implements android.os.Parcelable { *; }
-keepclassmembers class * implements java.io.Serializable {
    static final long serialVersionUID;
    private static final java.io.ObjectStreamField[] serialPersistentFields;
    private void writeObject(java.io.ObjectOutputStream);
    private void readObject(java.io.ObjectInputStream);
    private java.lang.Object writeReplace();
    private java.lang.Object readResolve();
}

# --- Prevent Stripping of Reflection-Based Code ---

# Keep classes referenced via reflection (adjust for your app's needs)
-keepnames class * {
    @androidx.room.*
}

# --- Miscellaneous ---

# Retain annotations
-keepattributes *Annotation*,EnclosingMethod,Signature,InnerClasses

# Improve stack trace readability (optional)
-renamesourcefileattribute SourceFile
-keepattributes SourceFile,LineNumberTable

# --- Remove or modify as needed for your own dependencies ---

