# sardine-android

[![Build Status](https://circleci.com/gh/thegrizzlylabs/sardine-android.svg?&style=shield)](https://circleci.com/gh/thegrizzlylabs/sardine-android)

A WebDAV client for Android, using [OkHttp](https://github.com/square/okhttp) as HTTP client.

## Getting started

- Check out this repository and add it to your Android project
- Include this Android library into your app by editing your `build.gradle`:

```
dependencies {
  ...
  implementation project(':sardine-android')
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


