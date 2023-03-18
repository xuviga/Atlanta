-dontshrink
-dontoptimize
-dontusemixedcaseclassnames
-dontskipnonpubliclibraryclasses
-ignorewarnings
-verbose

-libraryjars  <java.home>/lib/rt.jar
-libraryjars ../../mods2
-libraryjars ../../../server/forge-1.7.10-10.13.4.1558-1.7.10-srg.jar
-libraryjars ../../../server/forge_bld
-libraryjars ../../../server/authlib-1.5.16.jar
-libraryjars ../../../server/libraries

-keepattributes Exceptions,InnerClasses,Signature,Deprecated, SourceFile,LineNumberTable,*Annotation*,EnclosingMethod 

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
