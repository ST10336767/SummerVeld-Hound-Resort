# SummerVeld Hound Resort - Android App

A comprehensive Android application for the SummerVeld Hound Resort pet care management system.

## Features

- **Authentication** - Secure login and registration with Firebase Auth
- **Dog Management** - Create and manage dog profiles
- **Event Management** - View and RSVP to resort events
- **Image Upload** - Upload and manage pet photos
- **User Profiles** - Manage user information and preferences
- **Notifications** - Stay updated with resort news and events
- **Dashboard** - Personalized home screen with quick access
- **Admin Panel** - Administrative tools for staff

## Tech Stack

- **Language**: Kotlin
- **Framework**: Android SDK
- **Architecture**: MVVM (Model-View-ViewModel)
- **UI**: ViewBinding, Material Design 3
- **Database**: Firebase Firestore
- **Authentication**: Firebase Auth
- **Image Loading**: Glide
- **Networking**: Retrofit + OkHttp
- **Image Picker**: Dhaval2404 ImagePicker
- **Navigation**: Android Navigation Component

## Project Structure

```
android-app/
├── app/
│   ├── src/main/java/com/example/summerveldhoundresort/
│   │   ├── db/                    # Database layer
│   │   │   ├── entities/          # Data models
│   │   │   ├── implementations/   # Repository implementations
│   │   │   └── repos/            # Repository interfaces
│   │   ├── network/              # Network layer
│   │   │   ├── api/              # API service interfaces
│   │   │   ├── models/           # Network data models
│   │   │   └── repository/       # Network repositories
│   │   ├── ui/                   # UI layer
│   │   │   ├── admin/            # Admin-related activities
│   │   │   ├── auth/             # Authentication screens
│   │   │   ├── dashboard/        # Dashboard functionality
│   │   │   ├── events/           # Event management
│   │   │   ├── home/             # Home screen
│   │   │   ├── images/           # Image upload functionality
│   │   │   ├── notifications/    # Notifications
│   │   │   └── profile/          # User profile management
│   │   └── utils/                # Utility classes
│   ├── src/main/res/             # Android resources
│   │   ├── drawable/             # Images and drawables
│   │   ├── layout/               # XML layouts
│   │   ├── menu/                 # Menu definitions
│   │   ├── navigation/           # Navigation graphs
│   │   ├── values/               # Strings, colors, dimensions
│   │   └── mipmap/               # App icons
│   └── build.gradle.kts          # App-level build configuration
├── build.gradle.kts              # Project-level build configuration
├── settings.gradle.kts           # Project settings
└── gradle/                       # Gradle wrapper
```

## Getting Started

### Prerequisites

- Android Studio Arctic Fox or later
- Android SDK API level 25 or higher
- Firebase project setup
- Google Services configuration

### Setup

1. **Clone the repository**
   ```bash
   git clone <repository-url>
   cd SummerVeld-Hound-Resort/android-app
   ```

2. **Open in Android Studio**
   - Open Android Studio
   - Select "Open an existing project"
   - Navigate to the `android-app` folder

3. **Configure Firebase**
   - Ensure `google-services.json` is in the `app/` directory
   - Configure Firebase Authentication
   - Set up Firestore database
   - Configure Firebase Storage for image uploads

4. **Sync and Build**
   - Let Android Studio sync the project
   - Build the project (Build → Make Project)
   - Run on device or emulator

## Key Components

### Authentication
- Firebase Authentication integration
- Login and registration screens
- Password reset functionality
- User session management

### Database
- Firebase Firestore for data storage
- Repository pattern for data access
- Real-time data synchronization

### Image Management
- Image picker integration
- Firebase Storage for image uploads
- Glide for image loading and caching

### Network Layer
- Retrofit for API communication
- OkHttp for HTTP client
- Gson for JSON serialization

## Dependencies

### Core Android
- AndroidX Core KTX
- AndroidX AppCompat
- Material Design Components
- ConstraintLayout

### Architecture Components
- ViewBinding
- Navigation Component
- Lifecycle components
- ViewModel and LiveData

### Firebase
- Firebase Auth
- Firebase Firestore
- Firebase Storage
- Firebase Analytics

### Networking
- Retrofit 2.9.0
- OkHttp 4.12.0
- Gson converter

### Image Handling
- Glide 4.16.0
- Dhaval2404 ImagePicker 2.1

### Utilities
- Kotlin Coroutines
- Google Play Services Auth

## Development

### Building the Project

```bash
# Debug build
./gradlew assembleDebug

# Release build
./gradlew assembleRelease
```

### Running Tests

```bash
# Unit tests
./gradlew test

# Instrumented tests
./gradlew connectedAndroidTest
```

## Configuration

### Firebase Setup
1. Create a Firebase project
2. Add Android app to the project
3. Download `google-services.json`
4. Place it in the `app/` directory
5. Configure authentication methods
6. Set up Firestore database
7. Configure Storage rules

### API Configuration
Update `NetworkConfig.kt` with your backend API base URL:

```kotlin
object NetworkConfig {
    const val BASE_URL = "https://your-api-domain.com/api/"
}
```

## Contributing

1. Fork the repository
2. Create a feature branch
3. Make your changes
4. Test thoroughly on different devices
5. Submit a pull request

## License

This project is licensed under the MIT License.