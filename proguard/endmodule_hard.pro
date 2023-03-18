-dontshrink
-dontoptimize
-dontusemixedcaseclassnames
-dontskipnonpubliclibraryclasses
-ignorewarnings
-verbose

-libraryjars  <java.home>/lib/rt.jar
-libraryjars ../mods2
-libraryjars ../.gradle/minecraft
#-libraryjars ../serverfiles/forge-1.12.2-14.23.2.2611-srgBin.jar
#-libraryjars ../serverfiles/forge-1.12.2-14.23.2.2611-universal.jar
#-libraryjars ../serverfiles/authlib-1.5.16.jar
#-libraryjars ../serverfiles/libraries

-keepattributes *Annotation* 

#-keepclasseswithmembernames class ru.imine.shared.util.annotations.KeepClass
#-keepclasseswithmembernames class ru.imine.shared.util.annotations.KeepName

-keepclasseswithmembernames @ru.imine.shared.util.annotations.KeepClass class *
-keepclasseswithmembernames @cpw.mods.fml.common.Mod class *
-keepclassmembernames class * {
    @ru.imine.shared.util.annotations.KeepName *;
}

-keep, allowobfuscation class ru.imine.*
-keepclassmembers, allowobfuscation class * {
    *;
}

#-keepclasseswithmembernames class * implements java.io.Serializable { 
#    *;
#} 

-keepclassmembers class * extends java.lang.Enum {
    <fields>;
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

-keepnames class * implements java.io.Serializable
-keepclassmembers class * implements java.io.Serializable {
    static final long serialVersionUID;
    private static final java.io.ObjectStreamField[] serialPersistentFields;
    !static !transient <fields>;
    private void writeObject(java.io.ObjectOutputStream);
    private void readObject(java.io.ObjectInputStream);
    java.lang.Object writeReplace();
    java.lang.Object readResolve();
}
