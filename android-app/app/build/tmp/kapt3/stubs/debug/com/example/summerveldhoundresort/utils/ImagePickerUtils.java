package com.example.summerveldhoundresort.utils;

/**
 * Utility class for handling image picker operations
 */
@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000:\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0010\u0002\n\u0000\n\u0002\u0010\b\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0004\b\u00c6\u0002\u0018\u00002\u00020\u0001B\u0007\b\u0002\u00a2\u0006\u0002\u0010\u0002JH\u0010\u0003\u001a\u00020\u00042\u0006\u0010\u0005\u001a\u00020\u00062\u0006\u0010\u0007\u001a\u00020\u00062\b\u0010\b\u001a\u0004\u0018\u00010\t2\u0012\u0010\n\u001a\u000e\u0012\u0004\u0012\u00020\f\u0012\u0004\u0012\u00020\u00040\u000b2\u0012\u0010\r\u001a\u000e\u0012\u0004\u0012\u00020\u000e\u0012\u0004\u0012\u00020\u00040\u000bJ\u000e\u0010\u000f\u001a\u00020\u00042\u0006\u0010\u0010\u001a\u00020\u0011J\u000e\u0010\u0012\u001a\u00020\u00042\u0006\u0010\u0010\u001a\u00020\u0011J\u000e\u0010\u0013\u001a\u00020\u00042\u0006\u0010\u0010\u001a\u00020\u0011J\u000e\u0010\u0014\u001a\u00020\u00042\u0006\u0010\u0010\u001a\u00020\u0011\u00a8\u0006\u0015"}, d2 = {"Lcom/example/summerveldhoundresort/utils/ImagePickerUtils;", "", "()V", "handleImagePickerResult", "", "requestCode", "", "resultCode", "data", "Landroid/content/Intent;", "onImageSelected", "Lkotlin/Function1;", "Landroid/net/Uri;", "onError", "", "launchCamera", "fragment", "Landroidx/fragment/app/Fragment;", "launchGallery", "launchImagePicker", "launchProfileImagePicker", "app_debug"})
public final class ImagePickerUtils {
    @org.jetbrains.annotations.NotNull()
    public static final com.example.summerveldhoundresort.utils.ImagePickerUtils INSTANCE = null;
    
    private ImagePickerUtils() {
        super();
    }
    
    /**
     * Launch image picker for single image selection
     */
    public final void launchImagePicker(@org.jetbrains.annotations.NotNull()
    androidx.fragment.app.Fragment fragment) {
    }
    
    /**
     * Launch image picker for camera capture
     */
    public final void launchCamera(@org.jetbrains.annotations.NotNull()
    androidx.fragment.app.Fragment fragment) {
    }
    
    /**
     * Launch image picker with gallery only
     */
    public final void launchGallery(@org.jetbrains.annotations.NotNull()
    androidx.fragment.app.Fragment fragment) {
    }
    
    /**
     * Launch image picker for profile images (square crop)
     */
    public final void launchProfileImagePicker(@org.jetbrains.annotations.NotNull()
    androidx.fragment.app.Fragment fragment) {
    }
    
    /**
     * Handle the result from ImagePicker in onActivityResult
     */
    public final void handleImagePickerResult(int requestCode, int resultCode, @org.jetbrains.annotations.Nullable()
    android.content.Intent data, @org.jetbrains.annotations.NotNull()
    kotlin.jvm.functions.Function1<? super android.net.Uri, kotlin.Unit> onImageSelected, @org.jetbrains.annotations.NotNull()
    kotlin.jvm.functions.Function1<? super java.lang.String, kotlin.Unit> onError) {
    }
}