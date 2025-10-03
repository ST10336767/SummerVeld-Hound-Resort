package com.example.summerveldhoundresort.network.repository;

/**
 * Repository for handling image operations with the REST API
 */
@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000\u0092\u0001\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\u0010\b\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0003\n\u0002\u0010\u0002\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010 \n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0006\u0018\u0000 D2\u00020\u0001:\u0001DB\u0005\u00a2\u0006\u0002\u0010\u0002J4\u0010\u0007\u001a\u000e\u0012\u0004\u0012\u00020\t\u0012\u0004\u0012\u00020\t0\b2\u0006\u0010\n\u001a\u00020\t2\u0006\u0010\u000b\u001a\u00020\t2\u0006\u0010\f\u001a\u00020\t2\u0006\u0010\r\u001a\u00020\tH\u0002J6\u0010\u000e\u001a\u00020\u000f2\u0006\u0010\u0010\u001a\u00020\u00112\u0006\u0010\u0012\u001a\u00020\u00132\b\b\u0002\u0010\f\u001a\u00020\t2\b\b\u0002\u0010\r\u001a\u00020\t2\b\b\u0002\u0010\u0014\u001a\u00020\tH\u0002J\u0018\u0010\u0015\u001a\u00020\u000f2\u0006\u0010\u0010\u001a\u00020\u00112\u0006\u0010\u0016\u001a\u00020\u0013H\u0002J&\u0010\u0017\u001a\b\u0012\u0004\u0012\u00020\u00190\u00182\u0006\u0010\u001a\u001a\u00020\u001b2\b\b\u0002\u0010\u001c\u001a\u00020\tH\u0086@\u00a2\u0006\u0002\u0010\u001dJ\u001c\u0010\u001e\u001a\b\u0012\u0004\u0012\u00020\u001f0\u00182\u0006\u0010\u001a\u001a\u00020\u001bH\u0086@\u00a2\u0006\u0002\u0010 J:\u0010!\u001a\u0002H\"\"\u0004\b\u0000\u0010\"2\u001c\u0010#\u001a\u0018\b\u0001\u0012\n\u0012\b\u0012\u0004\u0012\u0002H\"0%\u0012\u0006\u0012\u0004\u0018\u00010\u00010$2\u0006\u0010&\u001a\u00020\u001bH\u0082@\u00a2\u0006\u0002\u0010\'J\n\u0010(\u001a\u0004\u0018\u00010\u001bH\u0002J\u0018\u0010)\u001a\u00020\t2\u0006\u0010\u0010\u001a\u00020\u00112\u0006\u0010\u0012\u001a\u00020\u0013H\u0002J\u001c\u0010*\u001a\b\u0012\u0004\u0012\u00020+0\u00182\u0006\u0010\u001a\u001a\u00020\u001bH\u0086@\u00a2\u0006\u0002\u0010 J\u001a\u0010,\u001a\u0004\u0018\u00010\u001b2\u0006\u0010\u0010\u001a\u00020\u00112\u0006\u0010\u0016\u001a\u00020\u0013H\u0002J2\u0010-\u001a\b\u0012\u0004\u0012\u00020.0\u00182\b\b\u0002\u0010/\u001a\u00020\u001b2\b\b\u0002\u00100\u001a\u00020\t2\b\b\u0002\u00101\u001a\u00020\tH\u0086@\u00a2\u0006\u0002\u00102J\u0018\u00103\u001a\u0002042\u0006\u00105\u001a\u0002042\u0006\u00106\u001a\u00020\tH\u0002J4\u00107\u001a\b\u0012\u0004\u0012\u0002080\u00182\u0006\u0010\u0010\u001a\u00020\u00112\f\u00109\u001a\b\u0012\u0004\u0012\u00020\u00130:2\b\b\u0002\u0010;\u001a\u00020<H\u0086@\u00a2\u0006\u0002\u0010=J,\u0010>\u001a\b\u0012\u0004\u0012\u00020?0\u00182\u0006\u0010\u0010\u001a\u00020\u00112\u0006\u0010\u0012\u001a\u00020\u00132\u0006\u0010@\u001a\u00020\u001bH\u0086@\u00a2\u0006\u0002\u0010AJ.\u0010B\u001a\b\u0012\u0004\u0012\u00020?0\u00182\u0006\u0010\u0010\u001a\u00020\u00112\u0006\u0010\u0012\u001a\u00020\u00132\b\b\u0002\u0010;\u001a\u00020<H\u0086@\u00a2\u0006\u0002\u0010CR\u000e\u0010\u0003\u001a\u00020\u0004X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0005\u001a\u00020\u0006X\u0082\u0004\u00a2\u0006\u0002\n\u0000\u00a8\u0006E"}, d2 = {"Lcom/example/summerveldhoundresort/network/repository/ImageRepository;", "", "()V", "auth", "Lcom/google/firebase/auth/FirebaseAuth;", "imageApiService", "Lcom/example/summerveldhoundresort/network/api/ImageApiService;", "calculateNewDimensions", "Lkotlin/Pair;", "", "originalWidth", "originalHeight", "maxWidth", "maxHeight", "compressImage", "Ljava/io/File;", "context", "Landroid/content/Context;", "imageUri", "Landroid/net/Uri;", "quality", "createFileFromUri", "uri", "createSignedUrl", "Lcom/example/summerveldhoundresort/db/AppResult;", "Lcom/example/summerveldhoundresort/network/models/SignedUrlData;", "filePath", "", "expiresIn", "(Ljava/lang/String;ILkotlin/coroutines/Continuation;)Ljava/lang/Object;", "deleteImage", "", "(Ljava/lang/String;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "executeWithRetry", "T", "operation", "Lkotlin/Function1;", "Lkotlin/coroutines/Continuation;", "operationName", "(Lkotlin/jvm/functions/Function1;Ljava/lang/String;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "getAuthToken", "getImageOrientation", "getImageUrl", "Lcom/example/summerveldhoundresort/network/models/ImageUrlData;", "getMimeType", "listImages", "Lcom/example/summerveldhoundresort/network/models/ImageListData;", "folder", "limit", "offset", "(Ljava/lang/String;IILkotlin/coroutines/Continuation;)Ljava/lang/Object;", "rotateBitmap", "Landroid/graphics/Bitmap;", "bitmap", "orientation", "uploadMultipleImages", "Lcom/example/summerveldhoundresort/network/models/MultipleImageData;", "imageUris", "", "options", "Lcom/example/summerveldhoundresort/network/models/ImageUploadOptions;", "(Landroid/content/Context;Ljava/util/List;Lcom/example/summerveldhoundresort/network/models/ImageUploadOptions;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "uploadPetProfileImage", "Lcom/example/summerveldhoundresort/network/models/ImageData;", "petId", "(Landroid/content/Context;Landroid/net/Uri;Ljava/lang/String;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "uploadSingleImage", "(Landroid/content/Context;Landroid/net/Uri;Lcom/example/summerveldhoundresort/network/models/ImageUploadOptions;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "Companion", "app_debug"})
public final class ImageRepository {
    @org.jetbrains.annotations.NotNull()
    private final com.example.summerveldhoundresort.network.api.ImageApiService imageApiService = null;
    @org.jetbrains.annotations.NotNull()
    private final com.google.firebase.auth.FirebaseAuth auth = null;
    @org.jetbrains.annotations.NotNull()
    private static final java.lang.String TAG = "ImageRepository";
    private static final int MAX_RETRY_ATTEMPTS = 3;
    private static final long BASE_RETRY_DELAY_MS = 1000L;
    private static final long MAX_RETRY_DELAY_MS = 10000L;
    @org.jetbrains.annotations.NotNull()
    public static final com.example.summerveldhoundresort.network.repository.ImageRepository.Companion Companion = null;
    
    public ImageRepository() {
        super();
    }
    
    /**
     * Get authentication token from Firebase Auth
     */
    private final java.lang.String getAuthToken() {
        return null;
    }
    
    /**
     * Execute operation with retry logic for network failures
     */
    private final <T extends java.lang.Object>java.lang.Object executeWithRetry(kotlin.jvm.functions.Function1<? super kotlin.coroutines.Continuation<? super T>, ? extends java.lang.Object> operation, java.lang.String operationName, kotlin.coroutines.Continuation<? super T> $completion) {
        return null;
    }
    
    /**
     * Compress image to reduce file size and upload time
     */
    private final java.io.File compressImage(android.content.Context context, android.net.Uri imageUri, int maxWidth, int maxHeight, int quality) {
        return null;
    }
    
    /**
     * Get image orientation from EXIF data
     */
    private final int getImageOrientation(android.content.Context context, android.net.Uri imageUri) {
        return 0;
    }
    
    /**
     * Calculate new dimensions maintaining aspect ratio
     */
    private final kotlin.Pair<java.lang.Integer, java.lang.Integer> calculateNewDimensions(int originalWidth, int originalHeight, int maxWidth, int maxHeight) {
        return null;
    }
    
    /**
     * Rotate bitmap based on orientation
     */
    private final android.graphics.Bitmap rotateBitmap(android.graphics.Bitmap bitmap, int orientation) {
        return null;
    }
    
    /**
     * Upload single image
     */
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Object uploadSingleImage(@org.jetbrains.annotations.NotNull()
    android.content.Context context, @org.jetbrains.annotations.NotNull()
    android.net.Uri imageUri, @org.jetbrains.annotations.NotNull()
    com.example.summerveldhoundresort.network.models.ImageUploadOptions options, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super com.example.summerveldhoundresort.db.AppResult<com.example.summerveldhoundresort.network.models.ImageData>> $completion) {
        return null;
    }
    
    /**
     * Upload multiple images
     */
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Object uploadMultipleImages(@org.jetbrains.annotations.NotNull()
    android.content.Context context, @org.jetbrains.annotations.NotNull()
    java.util.List<? extends android.net.Uri> imageUris, @org.jetbrains.annotations.NotNull()
    com.example.summerveldhoundresort.network.models.ImageUploadOptions options, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super com.example.summerveldhoundresort.db.AppResult<com.example.summerveldhoundresort.network.models.MultipleImageData>> $completion) {
        return null;
    }
    
    /**
     * Upload pet profile image
     */
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Object uploadPetProfileImage(@org.jetbrains.annotations.NotNull()
    android.content.Context context, @org.jetbrains.annotations.NotNull()
    android.net.Uri imageUri, @org.jetbrains.annotations.NotNull()
    java.lang.String petId, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super com.example.summerveldhoundresort.db.AppResult<com.example.summerveldhoundresort.network.models.ImageData>> $completion) {
        return null;
    }
    
    /**
     * Get image URL
     */
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Object getImageUrl(@org.jetbrains.annotations.NotNull()
    java.lang.String filePath, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super com.example.summerveldhoundresort.db.AppResult<com.example.summerveldhoundresort.network.models.ImageUrlData>> $completion) {
        return null;
    }
    
    /**
     * Create signed URL for private access
     */
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Object createSignedUrl(@org.jetbrains.annotations.NotNull()
    java.lang.String filePath, int expiresIn, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super com.example.summerveldhoundresort.db.AppResult<com.example.summerveldhoundresort.network.models.SignedUrlData>> $completion) {
        return null;
    }
    
    /**
     * List images in folder
     */
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Object listImages(@org.jetbrains.annotations.NotNull()
    java.lang.String folder, int limit, int offset, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super com.example.summerveldhoundresort.db.AppResult<com.example.summerveldhoundresort.network.models.ImageListData>> $completion) {
        return null;
    }
    
    /**
     * Delete image
     */
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Object deleteImage(@org.jetbrains.annotations.NotNull()
    java.lang.String filePath, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super com.example.summerveldhoundresort.db.AppResult<kotlin.Unit>> $completion) {
        return null;
    }
    
    /**
     * Create a temporary file from URI
     */
    private final java.io.File createFileFromUri(android.content.Context context, android.net.Uri uri) {
        return null;
    }
    
    /**
     * Get MIME type from URI
     */
    private final java.lang.String getMimeType(android.content.Context context, android.net.Uri uri) {
        return null;
    }
    
    @kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000 \n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0010\t\n\u0000\n\u0002\u0010\b\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0000\b\u0086\u0003\u0018\u00002\u00020\u0001B\u0007\b\u0002\u00a2\u0006\u0002\u0010\u0002R\u000e\u0010\u0003\u001a\u00020\u0004X\u0082T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0005\u001a\u00020\u0006X\u0082T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0007\u001a\u00020\u0004X\u0082T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\b\u001a\u00020\tX\u0082T\u00a2\u0006\u0002\n\u0000\u00a8\u0006\n"}, d2 = {"Lcom/example/summerveldhoundresort/network/repository/ImageRepository$Companion;", "", "()V", "BASE_RETRY_DELAY_MS", "", "MAX_RETRY_ATTEMPTS", "", "MAX_RETRY_DELAY_MS", "TAG", "", "app_debug"})
    public static final class Companion {
        
        private Companion() {
            super();
        }
    }
}