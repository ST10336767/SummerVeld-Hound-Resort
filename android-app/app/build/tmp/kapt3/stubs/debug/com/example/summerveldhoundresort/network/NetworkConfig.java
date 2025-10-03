package com.example.summerveldhoundresort.network;

/**
 * Network configuration for API client
 */
@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u00006\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\b\u00c6\u0002\u0018\u00002\u00020\u0001B\u0007\b\u0002\u00a2\u0006\u0002\u0010\u0002R\u000e\u0010\u0003\u001a\u00020\u0004X\u0082T\u00a2\u0006\u0002\n\u0000R\u0011\u0010\u0005\u001a\u00020\u0006\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0007\u0010\bR\u0011\u0010\t\u001a\u00020\n\u00a2\u0006\b\n\u0000\u001a\u0004\b\u000b\u0010\fR\u000e\u0010\r\u001a\u00020\u000eX\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u000f\u001a\u00020\u0010X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0016\u0010\u0011\u001a\n \u0013*\u0004\u0018\u00010\u00120\u0012X\u0082\u0004\u00a2\u0006\u0002\n\u0000\u00a8\u0006\u0014"}, d2 = {"Lcom/example/summerveldhoundresort/network/NetworkConfig;", "", "()V", "BASE_URL", "", "authApiService", "Lcom/example/summerveldhoundresort/network/api/AuthApiService;", "getAuthApiService", "()Lcom/example/summerveldhoundresort/network/api/AuthApiService;", "imageApiService", "Lcom/example/summerveldhoundresort/network/api/ImageApiService;", "getImageApiService", "()Lcom/example/summerveldhoundresort/network/api/ImageApiService;", "loggingInterceptor", "Lokhttp3/logging/HttpLoggingInterceptor;", "okHttpClient", "Lokhttp3/OkHttpClient;", "retrofit", "Lretrofit2/Retrofit;", "kotlin.jvm.PlatformType", "app_debug"})
public final class NetworkConfig {
    @org.jetbrains.annotations.NotNull()
    private static final java.lang.String BASE_URL = "https://summerveld-api.onrender.com/api/";
    @org.jetbrains.annotations.NotNull()
    private static final okhttp3.logging.HttpLoggingInterceptor loggingInterceptor = null;
    @org.jetbrains.annotations.NotNull()
    private static final okhttp3.OkHttpClient okHttpClient = null;
    private static final retrofit2.Retrofit retrofit = null;
    @org.jetbrains.annotations.NotNull()
    private static final com.example.summerveldhoundresort.network.api.ImageApiService imageApiService = null;
    @org.jetbrains.annotations.NotNull()
    private static final com.example.summerveldhoundresort.network.api.AuthApiService authApiService = null;
    @org.jetbrains.annotations.NotNull()
    public static final com.example.summerveldhoundresort.network.NetworkConfig INSTANCE = null;
    
    private NetworkConfig() {
        super();
    }
    
    @org.jetbrains.annotations.NotNull()
    public final com.example.summerveldhoundresort.network.api.ImageApiService getImageApiService() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final com.example.summerveldhoundresort.network.api.AuthApiService getAuthApiService() {
        return null;
    }
}