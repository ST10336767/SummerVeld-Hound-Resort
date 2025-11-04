# SummerVeld Hound Resort API

A comprehensive Node.js Express API backend service for the SummerVeld Hound Resort pet care management system.

## Features

- **Authentication & Authorization** - JWT-based auth with role-based access control
- **Pet Management** - Complete pet profile and booking management
- **Booking System** - Full booking lifecycle management
- **Service Management** - Pet care services (boarding, grooming, training, etc.)
- **User Management** - User profiles with different roles (user, staff, admin)
- **Security** - Helmet, CORS, rate limiting, input validation
- **Logging** - Comprehensive request logging and error handling
- **Database** - MongoDB with Mongoose ODM

## Tech Stack

- **Runtime**: Node.js (>=18.0.0)
- **Framework**: Express.js
- **Database**: MongoDB with Mongoose
- **Authentication**: JWT (JSON Web Tokens)
- **Validation**: Express Validator
- **Security**: Helmet, CORS, bcryptjs
- **Development**: Nodemon, Jest

## Quick Start

### Prerequisites

- Node.js (>=18.0.0)
- MongoDB (local or cloud instance)
- npm or yarn

### Installation

1. **Clone the repository**
   ```bash
   git clone <repository-url>
   cd SummerVeld-Hound-Resort/backend
   ```

2. **Install dependencies**
   ```bash
   npm install
   ```

3. **Environment Setup**
   ```bash
   # Copy the example environment file
   cp env.example .env
   
   # Edit .env with your configuration
   nano .env
   ```

4. **Start the development server**
   ```bash
   npm run dev
   ```

The API will be available at `http://localhost:5000`

## Environment Variables

Copy `env.example` to `.env` and configure the following variables:

```env
# Server Configuration
NODE_ENV=development
PORT=5000
FRONTEND_URL=http://localhost:3000

# Database Configuration
MONGODB_URI=mongodb://localhost:27017/summerveld-resort

# JWT Configuration
JWT_SECRET=your-super-secret-jwt-key
JWT_EXPIRE=7d
JWT_REFRESH_SECRET=your-super-secret-refresh-key
JWT_REFRESH_EXPIRE=30d
```

## API Endpoints

### Authentication
- `POST /api/auth/register` - Register a new user
- `POST /api/auth/login` - Login user
- `GET /api/auth/me` - Get current user profile
- `POST /api/auth/logout` - Logout user

### Users
- `GET /api/users` - Get all users (admin only)
- `GET /api/users/:id` - Get user by ID
- `PUT /api/users/:id` - Update user
- `DELETE /api/users/:id` - Delete user (admin only)

### Bookings
- `GET /api/bookings` - Get all bookings
- `GET /api/bookings/:id` - Get booking by ID
- `POST /api/bookings` - Create new booking
- `PUT /api/bookings/:id` - Update booking
- `DELETE /api/bookings/:id` - Delete booking

### Services
- `GET /api/services` - Get all available services
- `GET /api/services/:id` - Get service by ID
- `POST /api/services` - Create new service (admin only)

### Health Check
- `GET /health` - API health status

## Project Structure

```
backend/
├── app.js                 # Main application file
├── package.json           # Dependencies and scripts
├── env.example           # Environment variables template
├── README.md             # Project documentation
├── config/               # Configuration files
│   ├── database.js       # Database connection
│   ├── firebase.js       # Firebase configuration
│   ├── jwt.js           # JWT configuration
│   └── supabase.js      # Supabase configuration
├── controllers/          # Route controllers
│   ├── authController.js
│   ├── bookingController.js
│   └── imageController.js
├── middleware/           # Custom middleware
│   ├── auth.js          # Authentication middleware
│   ├── errorHandler.js  # Error handling middleware
│   └── upload.js        # File upload middleware
├── models/              # Database models
│   ├── User.js
│   └── Booking.js
├── routes/              # API routes
│   ├── auth.js
│   ├── users.js
│   ├── bookings.js
│   ├── images.js
│   └── services.js
├── services/            # Business logic services
│   └── imageService.js
├── utils/               # Utility functions
│   ├── response.js      # API response helpers
│   ├── validation.js    # Validation helpers
│   └── logger.js        # Logging utility
└── docs/                # Documentation
    └── IMAGE_SERVICE.md
```

## User Roles

- **User**: Can manage their own bookings and profile
- **Staff**: Can manage bookings and view user information
- **Admin**: Full access to all features and user management

## API Response Format

All API responses follow a consistent format:

### Success Response
```json
{
  "success": true,
  "message": "Operation successful",
  "data": { ... }
}
```

### Error Response
```json
{
  "success": false,
  "message": "Error description",
  "errors": [ ... ] // For validation errors
}
```

## Development

### Available Scripts

- `npm start` - Start production server
- `npm run dev` - Start development server with nodemon
- `npm test` - Run tests
- `npm run test:watch` - Run tests in watch mode

### Adding New Features

1. **Models**: Add new Mongoose models in `models/`
2. **Controllers**: Create controllers in `controllers/`
3. **Routes**: Define routes in `routes/`
4. **Middleware**: Add custom middleware in `middleware/`
5. **Validation**: Use express-validator for input validation

## Security Features

- **Helmet**: Security headers
- **CORS**: Cross-origin resource sharing
- **Rate Limiting**: Prevent abuse
- **Input Validation**: Sanitize and validate all inputs
- **Password Hashing**: bcrypt for secure password storage
- **JWT**: Secure token-based authentication

## Contributing

1. Fork the repository
2. Create a feature branch
3. Make your changes
4. Add tests if applicable
5. Submit a pull request

## License

This project is licensed under the ISC License.

## Support

For support and questions, please contact the development team.
