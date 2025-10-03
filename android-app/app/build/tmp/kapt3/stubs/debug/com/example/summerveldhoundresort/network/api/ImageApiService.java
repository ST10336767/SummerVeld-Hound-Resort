package com.example.summerveldhoundresort.network.api;

/**
 * Retrofit API service interface for image operations
 */
@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000^\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\b\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0006\n\u0002\u0018\u0002\n\u0002\b\u0006\bf\u0018\u00002\u00020\u0001J(\u0010\u0002\u001a\b\u0012\u0004\u0012\u00020\u00040\u00032\b\b\u0001\u0010\u0005\u001a\u00020\u00062\b\b\u0001\u0010\u0007\u001a\u00020\bH\u00a7@\u00a2\u0006\u0002\u0010\tJ(\u0010\n\u001a\b\u0012\u0004\u0012\u00020\u000b0\u00032\b\b\u0001\u0010\u0005\u001a\u00020\u00062\b\b\u0001\u0010\f\u001a\u00020\u0006H\u00a7@\u00a2\u0006\u0002\u0010\rJ(\u0010\u000e\u001a\b\u0012\u0004\u0012\u00020\u000f0\u00032\b\b\u0001\u0010\u0005\u001a\u00020\u00062\b\b\u0001\u0010\f\u001a\u00020\u0006H\u00a7@\u00a2\u0006\u0002\u0010\rJB\u0010\u0010\u001a\b\u0012\u0004\u0012\u00020\u00110\u00032\b\b\u0001\u0010\u0005\u001a\u00020\u00062\n\b\u0001\u0010\u0012\u001a\u0004\u0018\u00010\u00062\n\b\u0001\u0010\u0013\u001a\u0004\u0018\u00010\u00142\n\b\u0001\u0010\u0015\u001a\u0004\u0018\u00010\u0014H\u00a7@\u00a2\u0006\u0002\u0010\u0016Jj\u0010\u0017\u001a\b\u0012\u0004\u0012\u00020\u00180\u00032\b\b\u0001\u0010\u0005\u001a\u00020\u00062\u000e\b\u0001\u0010\u0019\u001a\b\u0012\u0004\u0012\u00020\u001b0\u001a2\n\b\u0001\u0010\u0012\u001a\u0004\u0018\u00010\u001c2\n\b\u0001\u0010\u001d\u001a\u0004\u0018\u00010\u001c2\n\b\u0001\u0010\u001e\u001a\u0004\u0018\u00010\u001c2\n\b\u0001\u0010\u001f\u001a\u0004\u0018\u00010\u001c2\n\b\u0001\u0010 \u001a\u0004\u0018\u00010\u001cH\u00a7@\u00a2\u0006\u0002\u0010!J2\u0010\"\u001a\b\u0012\u0004\u0012\u00020#0\u00032\b\b\u0001\u0010\u0005\u001a\u00020\u00062\b\b\u0001\u0010$\u001a\u00020\u001b2\b\b\u0001\u0010%\u001a\u00020\u001cH\u00a7@\u00a2\u0006\u0002\u0010&Jd\u0010\'\u001a\b\u0012\u0004\u0012\u00020#0\u00032\b\b\u0001\u0010\u0005\u001a\u00020\u00062\b\b\u0001\u0010$\u001a\u00020\u001b2\n\b\u0001\u0010\u0012\u001a\u0004\u0018\u00010\u001c2\n\b\u0001\u0010\u001d\u001a\u0004\u0018\u00010\u001c2\n\b\u0001\u0010\u001e\u001a\u0004\u0018\u00010\u001c2\n\b\u0001\u0010\u001f\u001a\u0004\u0018\u00010\u001c2\n\b\u0001\u0010 \u001a\u0004\u0018\u00010\u001cH\u00a7@\u00a2\u0006\u0002\u0010(\u00a8\u0006)"}, d2 = {"Lcom/example/summerveldhoundresort/network/api/ImageApiService;", "", "createSignedUrl", "Lretrofit2/Response;", "Lcom/example/summerveldhoundresort/network/models/SignedUrlResponse;", "token", "", "request", "Lcom/example/summerveldhoundresort/network/models/SignedUrlRequest;", "(Ljava/lang/String;Lcom/example/summerveldhoundresort/network/models/SignedUrlRequest;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "deleteImage", "Lcom/example/summerveldhoundresort/network/models/ApiErrorResponse;", "filePath", "(Ljava/lang/String;Ljava/lang/String;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "getImageUrl", "Lcom/example/summerveldhoundresort/network/models/ImageUrlResponse;", "listImages", "Lcom/example/summerveldhoundresort/network/models/ImageListResponse;", "folder", "limit", "", "offset", "(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Integer;Ljava/lang/Integer;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "uploadMultipleImages", "Lcom/example/summerveldhoundresort/network/models/MultipleImageUploadResponse;", "images", "", "Lokhttp3/MultipartBody$Part;", "Lokhttp3/RequestBody;", "width", "height", "quality", "format", "(Ljava/lang/String;Ljava/util/List;Lokhttp3/RequestBody;Lokhttp3/RequestBody;Lokhttp3/RequestBody;Lokhttp3/RequestBody;Lokhttp3/RequestBody;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "uploadPetProfileImage", "Lcom/example/summerveldhoundresort/network/models/ImageUploadResponse;", "image", "petId", "(Ljava/lang/String;Lokhttp3/MultipartBody$Part;Lokhttp3/RequestBody;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "uploadSingleImage", "(Ljava/lang/String;Lokhttp3/MultipartBody$Part;Lokhttp3/RequestBody;Lokhttp3/RequestBody;Lokhttp3/RequestBody;Lokhttp3/RequestBody;Lokhttp3/RequestBody;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "app_debug"})
public abstract interface ImageApiService {
    
    /**
     * Upload single image
     */
    @retrofit2.http.Multipart()
    @retrofit2.http.POST(value = "images/upload")
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object uploadSingleImage(@retrofit2.http.Header(value = "Authorization")
    @org.jetbrains.annotations.NotNull()
    java.lang.String token, @retrofit2.http.Part()
    @org.jetbrains.annotations.NotNull()
    okhttp3.MultipartBody.Part image, @retrofit2.http.Part(value = "folder")
    @org.jetbrains.annotations.Nullable()
    okhttp3.RequestBody folder, @retrofit2.http.Part(value = "width")
    @org.jetbrains.annotations.Nullable()
    okhttp3.RequestBody width, @retrofit2.http.Part(value = "height")
    @org.jetbrains.annotations.Nullable()
    okhttp3.RequestBody height, @retrofit2.http.Part(value = "quality")
    @org.jetbrains.annotations.Nullable()
    okhttp3.RequestBody quality, @retrofit2.http.Part(value = "format")
    @org.jetbrains.annotations.Nullable()
    okhttp3.RequestBody format, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super retrofit2.Response<com.example.summerveldhoundresort.network.models.ImageUploadResponse>> $completion);
    
    /**
     * Upload multiple images
     */
    @retrofit2.http.Multipart()
    @retrofit2.http.POST(value = "images/upload-multiple")
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object uploadMultipleImages(@retrofit2.http.Header(value = "Authorization")
    @org.jetbrains.annotations.NotNull()
    java.lang.String token, @retrofit2.http.Part()
    @org.jetbrains.annotations.NotNull()
    java.util.List<okhttp3.MultipartBody.Part> images, @retrofit2.http.Part(value = "folder")
    @org.jetbrains.annotations.Nullable()
    okhttp3.RequestBody folder, @retrofit2.http.Part(value = "width")
    @org.jetbrains.annotations.Nullable()
    okhttp3.RequestBody width, @retrofit2.http.Part(value = "height")
    @org.jetbrains.annotations.Nullable()
    okhttp3.RequestBody height, @retrofit2.http.Part(value = "quality")
    @org.jetbrains.annotations.Nullable()
    okhttp3.RequestBody quality, @retrofit2.http.Part(value = "format")
    @org.jetbrains.annotations.Nullable()
    okhttp3.RequestBody format, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super retrofit2.Response<com.example.summerveldhoundresort.network.models.MultipleImageUploadResponse>> $completion);
    
    /**
     * Upload pet profile image
     */
    @retrofit2.http.Multipart()
    @retrofit2.http.POST(value = "images/pet-profile")
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object uploadPetProfileImage(@retrofit2.http.Header(value = "Authorization")
    @org.jetbrains.annotations.NotNull()
    java.lang.String token, @retrofit2.http.Part()
    @org.jetbrains.annotations.NotNull()
    okhttp3.MultipartBody.Part image, @retrofit2.http.Part(value = "petId")
    @org.jetbrains.annotations.NotNull()
    okhttp3.RequestBody petId, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super retrofit2.Response<com.example.summerveldhoundresort.network.models.ImageUploadResponse>> $completion);
    
    /**
     * Get image URL
     */
    @retrofit2.http.GET(value = "images/url/{filePath}")
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object getImageUrl(@retrofit2.http.Header(value = "Authorization")
    @org.jetbrains.annotations.NotNull()
    java.lang.String token, @retrofit2.http.Path(value = "filePath")
    @org.jetbrains.annotations.NotNull()
    java.lang.String filePath, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super retrofit2.Response<com.example.summerveldhoundresort.network.models.ImageUrlResponse>> $completion);
    
    /**
     * Create signed URL for private access
     */
    @retrofit2.http.POST(value = "images/signed-url")
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object createSignedUrl(@retrofit2.http.Header(value = "Authorization")
    @org.jetbrains.annotations.NotNull()
    java.lang.String token, @retrofit2.http.Body()
    @org.jetbrains.annotations.NotNull()
    com.example.summerveldhoundresort.network.models.SignedUrlRequest request, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super retrofit2.Response<com.example.summerveldhoundresort.network.models.SignedUrlResponse>> $completion);
    
    /**
     * List images in folder
     */
    @retrofit2.http.GET(value = "images/list")
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object listImages(@retrofit2.http.Header(value = "Authorization")
    @org.jetbrains.annotations.NotNull()
    java.lang.String token, @retrofit2.http.Query(value = "folder")
    @org.jetbrains.annotations.Nullable()
    java.lang.String folder, @retrofit2.http.Query(value = "limit")
    @org.jetbrains.annotations.Nullable()
    java.lang.Integer limit, @retrofit2.http.Query(value = "offset")
    @org.jetbrains.annotations.Nullable()
    java.lang.Integer offset, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super retrofit2.Response<com.example.summerveldhoundresort.network.models.ImageListResponse>> $completion);
    
    /**
     * Delete image
     */
    @retrofit2.http.DELETE(value = "images/{filePath}")
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object deleteImage(@retrofit2.http.Header(value = "Authorization")
    @org.jetbrains.annotations.NotNull()
    java.lang.String token, @retrofit2.http.Path(value = "filePath")
    @org.jetbrains.annotations.NotNull()
    java.lang.String filePath, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super retrofit2.Response<com.example.summerveldhoundresort.network.models.ApiErrorResponse>> $completion);
}