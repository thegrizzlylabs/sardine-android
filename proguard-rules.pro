
## SimpleXml
-dontwarn org.simpleframework.xml.stream.**
-keep class org.simpleframework.xml.**{ *; }
-keepclassmembers,allowobfuscation class * {
    @org.simpleframework.xml.* <fields>;
    @org.simpleframework.xml.* <init>(...);
}

## Sardine Android model classes: needed for XML serialization
-keep class com.thegrizzlylabs.sardineandroid.model.**{ *; }

## OkHTTP
-dontwarn okhttp3.internal.platform.ConscryptPlatform

-dontwarn org.simpleframework.xml.Element
-dontwarn org.simpleframework.xml.ElementList
-dontwarn org.simpleframework.xml.ElementListUnion
-dontwarn org.simpleframework.xml.Namespace
-dontwarn org.simpleframework.xml.Root
-dontwarn org.simpleframework.xml.convert.Converter