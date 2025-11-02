const { verifyToken } = require('../config/jwt')
const { verifyFirebaseToken } = require('../config/firebase')
const User = require('../models/User')

const auth = async (req, res, next) => {
  try {
    const token = req.header('Authorization')?.replace('Bearer ', '')

    // Debug logging - don't log actual token value for security
    console.log('Auth middleware - Token received:', token ? 'YES' : 'NO')
    // Never log the actual token value as it's sensitive

    // Security: Validate token in a single, non-bypassable check
    // Perform all validation upfront to prevent user-controlled bypass attempts
    let normalizedToken = null

    // Single comprehensive validation - no multiple checks on user-controlled data
    if (token && typeof token === 'string') {
      const trimmed = token.trim()
      // Only accept tokens with meaningful length (prevent whitespace-only tokens)
      if (trimmed.length > 0 && trimmed.length < 10000) { // Reasonable upper bound
        normalizedToken = trimmed
      }
    }

    // Reject if validation failed - this is NOT a user-controlled check,
    // it's based on our internal validation state
    if (!normalizedToken) {
      return res.status(401).json({
        success: false,
        message: 'No token, authorization denied'
      })
    }

    // Security: Only allow test token bypass in development environment
    // Never allow user-controlled security bypasses in production
    // Use normalizedToken to ensure consistent comparison
    if (process.env.NODE_ENV === 'development' && normalizedToken === 'test-token') {
      console.log('Auth middleware - Using test token bypass (development only)')
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
      // Use normalizedToken for validation
      const firebaseDecoded = await verifyFirebaseToken(normalizedToken)

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
        // Use normalizedToken for validation
        const decoded = verifyToken(normalizedToken)
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
