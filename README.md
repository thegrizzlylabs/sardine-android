# sardine-android

[![](https://jitpack.io/v/Pigcasso/sardine-android.svg)](https://jitpack.io/#Pigcasso/sardine-android)

A WebDAV client for Android, using [OkHttp](https://github.com/square/okhttp) as HTTP client.

Forked from [thegrizzlylabs/sardine-android](https://github.com/thegrizzlylabs/sardine-android)

## Getting started

- Edit your app-level `build.gradle` (see top of this page for the latest version):

```
dependencies {
  ...
  implementation 'com.thegrizzlylabs.sardine-android:sardine-android:<VERSION_NUMBER>'
}
```

- Create a `Sardine` client:
```
Sardine sardine = new OkHttpSardine();
sardine.setCredentials("username", "password");
```

- Use the client to make requests to your WebDAV server:
```
List<DavResource> resources = sardine.list("http://webdav.server.com");
```

## Legacy

Originally forked from [Sardine](https://github.com/lookfirst/sardine)

[Apache HTTP Client](http://hc.apache.org/) was replaced by [OkHttp](https://github.com/square/okhttp)

JAXB was replaced by [SimpleXml](http://simple.sourceforge.net/)
