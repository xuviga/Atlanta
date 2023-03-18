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

-keepattributes *Annotation* 

-keep, allowobfuscation class ru.imine.*
-keepclassmembers, allowobfuscation class * {
    *;
}

-keepclassmembers class * implements java.io.Serializable { 
    *;
} 