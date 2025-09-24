# Image API Integration

This document describes the integration of the RESTful API image endpoints into the SummerVeld Hound Resort mobile app.

## Overview

The mobile app now includes comprehensive image upload, storage, and management functionality using the RESTful API endpoints. This integration allows users to:

- Upload profile images
- Upload pet profile images
- Upload general images
- View uploaded images
- Manage image galleries

## Architecture

### Network Layer
- **ImageApiService**: Retrofit interface defining all image API endpoints
- **ImageRepository**: Repository pattern implementation for image operations
- **NetworkConfig**: Configuration for Retrofit client and API base URL

### Data Models
- **ImageModels.kt**: Contains all data classes for API requests and responses
- **ImageData**: Represents uploaded image information
- **ImageUploadOptions**: Configuration options for image uploads

### UI Components
- **ImageViewModel**: ViewModel for managing image operations
- **ImageUploadFragment**: Dedicated fragment for image upload functionality
- **ImagePickerUtils**: Utility class for camera and gallery integration

## Features Implemented

### 1. User Profile Images
- Users can upload profile pictures by tapping on their profile image
- Images are automatically resized to 300x300px
- Images are stored in the `users/{userId}/profile/` folder

### 2. Pet Profile Images
- Admin users can upload pet profile pictures when creating dogs
- Images are automatically resized to 400x400px
- Images are stored in the `pets/{petId}/profile/` folder

### 3. General Image Upload
- Dedicated image upload fragment for general image uploads
- Support for custom folder organization
- Configurable image processing options

### 4. Image Processing
- Automatic image resizing and optimization
- Support for multiple formats (JPEG, PNG, WebP)
- Quality control and compression

## API Endpoints Used

1. **POST /api/images/upload** - Upload single image
2. **POST /api/images/upload-multiple** - Upload multiple images
3. **POST /api/images/pet-profile** - Upload pet profile image
4. **POST /api/images/user-profile** - Upload user profile image
5. **GET /api/images/url/{filePath}** - Get image URL
6. **POST /api/images/signed-url** - Create signed URL
7. **GET /api/images/list** - List images in folder
8. **DELETE /api/images/{filePath}** - Delete image

## Configuration

### Base URL Configuration
Update the `BASE_URL` in `NetworkConfig.kt`:

```kotlin
// For Android emulator
private const val BASE_URL = "http://10.0.2.2:3000/api/"

// For physical device (replace with your computer's IP)
private const val BASE_URL = "http://192.168.1.100:3000/api/"

// For production
private const val BASE_URL = "https://your-api-domain.com/api/"
```

### Authentication
The app uses Firebase Authentication tokens for API authentication. The `ImageRepository` automatically retrieves the current user's ID token and includes it in API requests.

## Dependencies Added

```kotlin
// Network dependencies
implementation("com.squareup.retrofit2:retrofit:2.9.0")
implementation("com.squareup.retrofit2:converter-gson:2.9.0")
implementation("com.squareup.okhttp3:okhttp:4.12.0")
implementation("com.squareup.okhttp3:logging-interceptor:4.12.0")

// Image picker
implementation("com.github.dhaval2404:imagepicker:2.1")

// Coroutines
implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3")
```

## Permissions Added

```xml
<uses-permission android:name="android.permission.INTERNET" />
<uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />
<uses-permission android:name="android.permission.CAMERA" />
<uses-permission android:name="android.permission.READ_EXTERNAL_STORAGE" />
<uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE" 
    android:maxSdkVersion="28" />
```

## Usage Examples

### Upload Pet Profile Image
```kotlin
imageViewModel.uploadPetProfileImage(imageUri, petId)
```

### Upload General Image with Options
```kotlin
val options = ImageUploadOptions(
    folder = "general",
    width = 800,
    height = 600,
    quality = 85,
    format = "jpeg"
)
imageViewModel.uploadSingleImage(imageUri, options)
```

## Error Handling

The integration includes comprehensive error handling:
- Network connectivity issues
- Authentication failures
- File upload errors
- Image processing errors

All errors are displayed to users via Toast messages and logged for debugging.

## Security Considerations

1. **Authentication**: All API calls require valid Firebase Auth tokens
2. **File Validation**: Images are validated for type and size
3. **HTTPS**: Production should use HTTPS endpoints
4. **Permissions**: Proper Android permissions are requested

## Testing

To test the integration:

1. Ensure your RESTful API server is running
2. Update the `BASE_URL` in `NetworkConfig.kt`
3. Build and run the app
4. Navigate to profile or create dog screens
5. Test image upload functionality

## Troubleshooting

### Common Issues

1. **Network Connection Failed**
   - Check if API server is running
   - Verify BASE_URL configuration
   - Check network connectivity

2. **Authentication Errors**
   - Ensure user is logged in
   - Check Firebase Auth token validity

3. **Image Upload Fails**
   - Check file size limits
   - Verify image format support
   - Check storage permissions

### Debug Mode

Enable debug logging by setting the logging level in `NetworkConfig.kt`:

```kotlin
private val loggingInterceptor = HttpLoggingInterceptor().apply {
    level = HttpLoggingInterceptor.Level.BODY
}
```

## Future Enhancements

- [ ] Image cropping and editing
- [ ] Multiple image selection
- [ ] Image gallery view
- [ ] Image compression options
- [ ] Offline image caching
- [ ] Image metadata extraction
