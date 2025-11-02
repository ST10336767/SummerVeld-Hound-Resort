const { verifyToken } = require('../config/jwt')
const { verifyFirebaseToken } = require('../config/firebase')
const User = require('../models/User')

const auth = async (req, res, next) => {
  try {
    const token = req.header('Authorization')?.replace('Bearer ', '')

    // Debug logging
    console.log('Auth middleware - Token received:', token ? 'YES' : 'NO')
    console.log('Auth middleware - Token value:', token)

    if (!token) {
      return res.status(401).json({
        success: false,
        message: 'No token, authorization denied'
      })
    }

    // Temporary bypass for testing - remove in production
    if (token === 'test-token') {
      console.log('Auth middleware - Using test token bypass')
      // Create a temporary user object for testing (no database required)
      req.user = {
        _id: 'test-user-id',
        email: 'test@example.com',
        firstName: 'Test',
        lastName: 'User',
        role: 'user',
        firebaseUid: 'test-firebase-uid'
      }
      return next()
    }

    let user

    try {
      // Try Firebase token validation first
      const firebaseDecoded = await verifyFirebaseToken(token)

      // Create or find user based on Firebase UID
      user = await User.findOne({ firebaseUid: firebaseDecoded.uid })

      if (!user) {
        // Create new user from Firebase data
        user = new User({
          firebaseUid: firebaseDecoded.uid,
          email: firebaseDecoded.email,
          name: firebaseDecoded.name || firebaseDecoded.email,
          role: 'user', // Default role
          isEmailVerified: firebaseDecoded.email_verified || false
        })
        await user.save()
      }

      req.user = user
      next()
    } catch (firebaseError) {
      // If Firebase validation fails, try custom JWT validation
      try {
        const decoded = verifyToken(token)
        user = await User.findById(decoded.id).select('-password')

        if (!user) {
          return res.status(401).json({
            success: false,
            message: 'Token is not valid'
          })
        }

        req.user = user
        next()
      } catch (jwtError) {
        return res.status(401).json({
          success: false,
          message: 'Token is not valid'
        })
      }
    }
  } catch (error) {
    res.status(401).json({
      success: false,
      message: 'Token is not valid'
    })
  }
}

const authorize = (...roles) => {
  return (req, res, next) => {
    if (!req.user) {
      return res.status(401).json({
        success: false,
        message: 'Not authorized to access this route'
      })
    }

    if (!roles.includes(req.user.role)) {
      return res.status(403).json({
        success: false,
        message: `User role ${req.user.role} is not authorized to access this route`
      })
    }

    next()
  }
}

module.exports = { auth, authorize }
