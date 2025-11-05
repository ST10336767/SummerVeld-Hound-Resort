const express = require('express')
const cors = require('cors')
const helmet = require('helmet')
const morgan = require('morgan')
const compression = require('compression')
const rateLimit = require('express-rate-limit')
require('dotenv').config()

// const connectDB = require('./config/database'); // MongoDB not needed - using Firebase
const { initializeFirebase } = require('./config/firebase')
const errorHandler = require('./middleware/errorHandler')

// Import routes
const authRoutes = require('./routes/auth')
const userRoutes = require('./routes/users')
const serviceRoutes = require('./routes/services')
const imageRoutes = require('./routes/images')

const app = express()

// Initialize Firebase Admin SDK (Firebase is our database)
const firebaseApp = initializeFirebase()
if (!firebaseApp) {
  console.error('❌ CRITICAL: Firebase Admin SDK not initialized!')
  console.error('❌ Users authenticated via Firebase will NOT be able to access protected endpoints.')
  console.error('❌ To fix this, set the following environment variables:')
  console.error('   - FIREBASE_PROJECT_ID')
  console.error('   - FIREBASE_PRIVATE_KEY')
  console.error('   - FIREBASE_CLIENT_EMAIL')
  console.error('')
  console.error('⚠️  The server will still accept JWT tokens from /api/auth/login,')
  console.error('⚠️  but Firebase tokens will be rejected.')
} else {
  console.log('✅ Firebase Admin SDK initialized - ready to verify Firebase tokens')
}

// Rate limiting
const limiter = rateLimit({
  windowMs: 15 * 60 * 1000, // 15 minutes
  max: 100, // limit each IP to 100 requests per windowMs
  message: 'Too many requests from this IP, please try again later.'
})

// Middleware
app.use(helmet()) // Security headers
app.use(compression()) // Compress responses
app.use(limiter) // Rate limiting
// CORS configuration for mobile app and development
const corsOptions = {
  origin: function (origin, callback) {
    // Allow requests with no origin (like mobile apps, Postman, or curl requests)
    if (!origin) return callback(null, true)

    const allowedOrigins = [
      process.env.FRONTEND_URL,
      'http://localhost:3000',
      'http://localhost:3001',
      'http://10.0.2.2:3000', // Android emulator localhost
      'http://10.0.2.2:3001', // Android emulator localhost alternative
      'https://summerveld-api.onrender.com' // Your API URL
    ].filter(Boolean)

    // Allow all origins for development (you can restrict this in production)
    if (process.env.NODE_ENV === 'development') {
      return callback(null, true)
    }

    if (allowedOrigins.indexOf(origin) !== -1) {
      callback(null, true)
    } else {
      callback(new Error('Not allowed by CORS'))
    }
  },
  credentials: true,
  methods: ['GET', 'POST', 'PUT', 'DELETE', 'OPTIONS'],
  allowedHeaders: ['Content-Type', 'Authorization', 'X-Requested-With']
}

app.use(cors(corsOptions))
app.use(morgan('combined')) // Logging
app.use(express.json({ limit: '10mb' }))
app.use(express.urlencoded({ extended: true, limit: '10mb' }))

// Set server timeout to 10 minutes for image uploads
app.use((req, res, next) => {
  if (req.path.includes('/images/')) {
    req.setTimeout(600000) // 10 minutes for image uploads
    res.setTimeout(600000)
  }
  next()
})

// Health check endpoint
app.get('/health', (req, res) => {
  res.status(200).json({
    status: 'OK',
    message: 'SummerVeld Hound Resort API is running',
    timestamp: new Date().toISOString(),
    version: process.env.npm_package_version || '1.0.0'
  })
})

// API routes
app.use('/api/auth', authRoutes)
app.use('/api/users', userRoutes)
app.use('/api/services', serviceRoutes)
app.use('/api/images', imageRoutes)

// 404 handler
app.use('*', (req, res) => {
  res.status(404).json({
    success: false,
    message: 'Route not found'
  })
})

// Error handling middleware (must be last)
app.use(errorHandler)

const PORT = process.env.PORT || 5000

app.listen(PORT, () => {
  console.log(`🚀 Server running on port ${PORT}`)
  console.log(`📊 Environment: ${process.env.NODE_ENV || 'development'}`)
  console.log(`🔗 Health check: http://localhost:${PORT}/health`)
})

module.exports = app
