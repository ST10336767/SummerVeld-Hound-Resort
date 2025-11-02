const { verifyToken } = require('../config/jwt')
const { verifyFirebaseToken } = require('../config/firebase')
const User = require('../models/User')

/**
 * Validate and normalize authentication token
 * @param {string|null|undefined} token - Raw token from request header
 * @returns {{isValid: boolean, normalizedToken: string|null}}
 */
const validateToken = (token) => {
  let isValidToken = false
  let normalizedToken = null

  if (token && typeof token === 'string') {
    const trimmed = token.trim()
    const hasValidLength = trimmed.length > 0 && trimmed.length < 10000
    if (hasValidLength) {
      isValidToken = true
      normalizedToken = trimmed
    }
  }

  return { isValid: isValidToken, normalizedToken }
}

/**
 * Handle test token bypass (development only)
 * @param {string} normalizedToken - Normalized token
 * @returns {object|null} User object if test token, null otherwise
 */
const handleTestToken = (normalizedToken) => {
  if (process.env.NODE_ENV === 'development' && normalizedToken === 'test-token') {
    console.log('Auth middleware - Using test token bypass (development only)')
    return {
      _id: 'test-user-id',
      email: 'test@example.com',
      firstName: 'Test',
      lastName: 'User',
      role: 'user',
      firebaseUid: 'test-firebase-uid'
    }
  }
  return null
}

/**
 * Validate Firebase token and get/create user
 * @param {string} normalizedToken - Normalized token
 * @returns {Promise<object>} User object
 */
const validateFirebaseToken = async (normalizedToken) => {
  const firebaseDecoded = await verifyFirebaseToken(normalizedToken)
  let user = await User.findOne({ firebaseUid: firebaseDecoded.uid })

  if (!user) {
    user = new User({
      firebaseUid: firebaseDecoded.uid,
      email: firebaseDecoded.email,
      name: firebaseDecoded.name || firebaseDecoded.email,
      role: 'user',
      isEmailVerified: firebaseDecoded.email_verified || false
    })
    await user.save()
  }

  return user
}

/**
 * Validate JWT token and get user
 * @param {string} normalizedToken - Normalized token
 * @returns {Promise<object>} User object
 */
const validateJwtToken = async (normalizedToken) => {
  const decoded = verifyToken(normalizedToken)
  const user = await User.findById(decoded.id).select('-password')

  if (!user) {
    throw new Error('User not found')
  }

  return user
}

const auth = async (req, res, next) => {
  try {
    const token = req.header('Authorization')?.replace('Bearer ', '')

    // Debug logging - don't log actual token value for security
    console.log('Auth middleware - Token received:', token ? 'YES' : 'NO')

    // Validate token
    const { isValid, normalizedToken } = validateToken(token)
    if (!isValid) {
      return res.status(401).json({
        success: false,
        message: 'No token, authorization denied'
      })
    }

    // Check for test token bypass (development only)
    const testUser = handleTestToken(normalizedToken)
    if (testUser) {
      req.user = testUser
      return next()
    }

    // Try Firebase token validation first, fallback to JWT
    try {
      const user = await validateFirebaseToken(normalizedToken)
      req.user = user
      return next()
    } catch (firebaseError) {
      try {
        const user = await validateJwtToken(normalizedToken)
        req.user = user
        return next()
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
