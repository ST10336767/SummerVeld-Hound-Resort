# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# Keep line number information for debugging stack traces
-keepattributes SourceFile,LineNumberTable
-renamesourcefileattribute SourceFile

# Keep Kotlin metadata for reflection
-keepattributes *Annotation*
-keepattributes Signature
-keepattributes Exceptions
-keepattributes InnerClasses
-keepattributes EnclosingMethod

# Keep Kotlin data classes
-keep class kotlin.** { *; }
-keep class kotlin.Metadata { *; }
-dontwarn kotlin.**

# Keep all data classes (used by Gson and Firestore)
-keep class com.summerveldhoundresort.app.db.entities.** { *; }
-keep class com.summerveldhoundresort.app.network.models.** { *; }

# Keep Gson model classes (all classes with @SerializedName)
-keepattributes Signature
-keepattributes *Annotation*
-keep class com.summerveldhoundresort.app.network.models.** {
    <fields>;
}
-keepclassmembers,allowobfuscation class * {
    @com.google.gson.annotations.SerializedName <fields>;
}

# Keep Retrofit interfaces and implementations
-keepattributes Exceptions, Signature, InnerClasses, EnclosingMethod
-keepattributes RuntimeVisibleAnnotations, RuntimeVisibleParameterAnnotations
-keepclassmembers,allowshrinking,allowobfuscation interface * {
    @retrofit2.http.* <methods>;
}
-keep,allowobfuscation,allowshrinking class retrofit2.Response
-keep,allowobfuscation,allowshrinking class kotlin.coroutines.Continuation

# Keep Retrofit service interfaces
-keep interface com.summerveldhoundresort.app.network.api.** { *; }

# Keep Retrofit response types
-keep class retrofit2.Response { *; }
-keep class retrofit2.Response$* { *; }

# Keep OkHttp classes
-dontwarn okhttp3.**
-dontwarn okio.**
-keep class okhttp3.** { *; }
-keep interface okhttp3.** { *; }

# Keep Firebase classes
-keep class com.google.firebase.** { *; }
-keep class com.google.android.gms.** { *; }
-dontwarn com.google.firebase.**
-dontwarn com.google.android.gms.**

# Keep Firebase Firestore classes
-keep class com.google.firebase.firestore.** { *; }
-keep class com.google.firebase.firestore.**$* { *; }

# Keep Firebase Auth classes
-keep class com.google.firebase.auth.** { *; }

# Keep Glide classes
-keep public class * implements com.bumptech.glide.module.GlideModule
-keep class * extends com.bumptech.glide.module.AppGlideModule {
    <init>(...);
}
-keep public class * implements com.bumptech.glide.module.GlideModule
-keep class com.bumptech.glide.load.data.ParcelFileDescriptorRewinder$InternalRewinder {
    *** rewind();
}

# Keep ViewBinding classes
-keep class * extends androidx.viewbinding.ViewBinding {
    *;
}

# Keep all ViewBinding generated classes
-keep class *$$ViewBinding {
    *;
}

# Keep data classes used by Firestore
-keep class com.summerveldhoundresort.app.db.entities.User { *; }
-keep class com.summerveldhoundresort.app.db.entities.Dog { *; }
-keep class com.summerveldhoundresort.app.db.entities.Event { *; }
-keep class com.summerveldhoundresort.app.db.entities.Comment { *; }

# Keep Kotlin Coroutines
-keepnames class kotlinx.coroutines.internal.MainDispatcherFactory {}
-keepnames class kotlinx.coroutines.CoroutineExceptionHandler {}
-keepclassmembers class kotlinx.** {
    volatile <fields>;
}
-dontwarn kotlinx.coroutines.**

# Keep parcelable classes
-keep class * implements android.os.Parcelable {
    public static final android.os.Parcelable$Creator *;
}

# Keep Serializable classes
-keepclassmembers class * implements java.io.Serializable {
    static final long serialVersionUID;
    private static final java.io.ObjectStreamField[] serialPersistentFields;
    private void writeObject(java.io.ObjectOutputStream);
    private void readObject(java.io.ObjectInputStream);
    java.lang.Object writeReplace();
    java.lang.Object readResolve();
}

# Keep network models for Gson serialization
-keep class com.summerveldhoundresort.app.network.models.AuthModels$* { *; }
-keep class com.summerveldhoundresort.app.network.models.ImageModels$* { *; }
-keep class com.summerveldhoundresort.app.network.models.* { *; }

# Keep all classes in network package
-keep class com.summerveldhoundresort.app.network.** { *; }

# Keep repository classes
-keep class com.summerveldhoundresort.app.network.repository.** { *; }
-keep class com.summerveldhoundresort.app.db.implementations.** { *; }

# Keep ViewModel classes
-keep class * extends androidx.lifecycle.ViewModel {
    <init>(...);
}

# Keep LiveData classes
-keep class androidx.lifecycle.** { *; }

# Keep Navigation Component classes
-keep class androidx.navigation.** { *; }
-keep class * extends androidx.navigation.fragment.NavHostFragment { *; }

# Keep Image Picker classes
-keep class com.github.dhaval2404.imagepicker.** { *; }

# Keep application class
-keep class com.summerveldhoundresort.app.SummerveldApplication { *; }

# Keep all activities and fragments
-keep class * extends androidx.fragment.app.Fragment {
    *;
}
-keep class * extends androidx.appcompat.app.AppCompatActivity {
    *;
}

# Prevent R8 from removing classes that are only used via reflection
-keepclassmembers class * {
    @android.webkit.JavascriptInterface <methods>;
}

# Keep native methods
-keepclasseswithmembernames class * {
    native <methods>;
}

# Keep enums
-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

# Keep custom exceptions
-keep public class * extends java.lang.Exception

# Remove logging in release (optional - removes Log calls)
-assumenosideeffects class android.util.Log {
    public static boolean isLoggable(java.lang.String, int);
    public static int v(...);
    public static int i(...);
    public static int w(...);
    public static int d(...);
    public static int e(...);
}

# Suppress Google Play Games warnings (not used in this app)
-dontwarn com.google.android.gms.games.**
-dontwarn com.google.android.gms.optional_games.**
-keep class com.google.android.gms.games.** { *; }
-keep class com.google.android.gms.optional_games.** { *; }