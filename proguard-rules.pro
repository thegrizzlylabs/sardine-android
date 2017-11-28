
## SimpleXml
-dontwarn org.simpleframework.xml.stream.**
-keep class org.simpleframework.xml.**{ *; }
-keepclassmembers,allowobfuscation class * {
    @org.simpleframework.xml.* <fields>;
    @org.simpleframework.xml.* <init>(...);
}

## Sardine Android model classes: needed for XML serialization
-keep class com.thegrizzlylabs.sardineandroid.model.**{ *; }
