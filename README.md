# SummerVeld Hound Resort

A comprehensive dog resort management system with Android mobile app and RESTful API backend.

## Project Structure

```
SummerVeld-Hound-Resort/
├── android-app/                 # Android mobile application
│   ├── app/                    # Main Android app module
│   │   ├── src/main/java/      # Kotlin source code
│   │   ├── src/main/res/       # Android resources (layouts, drawables, etc.)
│   │   └── build.gradle.kts    # App-level build configuration
│   ├── build.gradle.kts        # Project-level build configuration
│   ├── settings.gradle.kts     # Project settings
│   └── gradle/                 # Gradle wrapper and dependencies
├── backend/                    # Node.js RESTful API
│   ├── controllers/            # API route controllers
│   ├── models/                 # Database models
│   ├── routes/                 # API route definitions
│   ├── middleware/             # Express middleware
│   ├── services/               # Business logic services
│   ├── config/                 # Configuration files
│   ├── utils/                  # Utility functions
│   └── docs/                   # API documentation
└── README.md                   # This file
```

## Features

### Android App
- User authentication (login/register)
- Dog profile management
- Event management and RSVP
- Image upload and management
- Admin dashboard
- Push notifications

### Backend API
- RESTful API endpoints
- Firebase integration
- Image storage and processing
- User authentication
- Service management
- Database operations

## Getting Started

### Prerequisites
- Android Studio (for mobile app)
- Node.js and npm (for backend)
- Firebase project setup

### Backend Setup
1. Navigate to the `backend/` directory
2. Install dependencies: `npm install`
3. Copy `env.example` to `.env` and configure your environment variables
4. Start the server: `npm start`

### Android App Setup
1. Open the `android-app/` directory in Android Studio
2. Sync the project with Gradle files
3. Configure Firebase in your project
4. Build and run the app

## Technology Stack

### Android App
- **Language**: Kotlin
- **Framework**: Android SDK
- **Architecture**: MVVM with ViewBinding
- **Database**: Firebase Firestore
- **Authentication**: Firebase Auth
- **Image Loading**: Glide
- **Networking**: Retrofit + OkHttp

### Backend
- **Runtime**: Node.js
- **Framework**: Express.js
- **Database**: Firebase Firestore
- **Authentication**: JWT + Firebase Auth
- **File Storage**: Firebase Storage
- **Image Processing**: Sharp

## Contributing

1. Fork the repository
2. Create a feature branch
3. Make your changes
4. Test thoroughly
5. Submit a pull request

## License

This project is licensed under the MIT License.