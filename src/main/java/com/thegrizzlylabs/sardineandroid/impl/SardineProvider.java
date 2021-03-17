package com.thegrizzlylabs.sardineandroid.impl;

import com.burgstaller.okhttp.AuthenticationCacheInterceptor;
import com.burgstaller.okhttp.DispatchingAuthenticator;
import com.burgstaller.okhttp.digest.CachingAuthenticator;
import com.burgstaller.okhttp.digest.Credentials;
import com.burgstaller.okhttp.basic.BasicAuthenticator;
import com.burgstaller.okhttp.digest.DigestAuthenticator;
import com.thegrizzlylabs.sardineandroid.Sardine;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import okhttp3.OkHttpClient;

public class SardineProvider {

    public static Sardine getSardineWithBasic(String login, String password) {
        Sardine sardine = new OkHttpSardine();
        sardine.setCredentials(login, password);
        return sardine;
    }

    public static Sardine getSardineWithPreemptive(String login, String password) {
        Sardine sardine = new OkHttpSardine();
        sardine.setCredentials(login, password, true);
        return sardine;
    }

    public static Sardine getSardineWithDigest(String login, String password) {
        final Map<String, CachingAuthenticator> authCache = new ConcurrentHashMap<>();

        Credentials credentials = new Credentials(login, password);
        final BasicAuthenticator basicAuthenticator = new BasicAuthenticator(credentials);
        final DigestAuthenticator digestAuthenticator = new DigestAuthenticator(credentials);

        OkHttpClient client = new OkHttpClient.Builder()
                .authenticator(new DispatchingAuthenticator.Builder()
                        .with("digest", digestAuthenticator)
                        .with("basic", basicAuthenticator)
                        .build())
                .addInterceptor(new AuthenticationCacheInterceptor(authCache))
                .build();

        return new OkHttpSardine(client);
    }

}
