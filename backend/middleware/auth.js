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
    // Try multiple ways to get the Authorization header (handles different Express versions)
    const authHeader = req.header('Authorization') || req.headers.authorization || req.get('Authorization')
    // Extract token and remove any potential encoding issues
    let token = authHeader?.replace(/^Bearer\s+/i, '') || ''
    // Remove any null bytes, carriage returns, or other problematic characters
    token = token.replace(/\0/g, '').replace(/\r/g, '').trim()

    // Debug logging - don't log actual token value for security
    // Use explicit console.log statements that will definitely show in Render logs
    console.log('=== AUTH MIDDLEWARE START ===')
    console.log('Auth middleware - Token received:', token ? 'YES' : 'NO')
    
    if (token) {
      console.log('Token length:', token.length)
      console.log('Token starts with:', token.substring(0, 20))
      // Check if token looks like a valid JWT (should have 3 parts separated by dots)
      const parts = token.split('.')
      console.log('Token parts count:', parts.length)
      console.log('Token validation - About to validate token')
    } else {
      console.log('Token validation - NO TOKEN PROVIDED')
    }

    // Validate token
    const { isValid, normalizedToken } = validateToken(token)
    console.log('Token validation - isValid:', isValid)
    console.log('Token validation - normalizedToken exists:', !!normalizedToken)
    
    if (!isValid) {
      console.log('❌ Token validation failed - returning 401')
      return res.status(401).json({
        success: false,
        message: 'No token, authorization denied'
      })
    }

    // Log normalized token info for debugging
    if (normalizedToken) {
      console.log('Normalized token length:', normalizedToken.length)
      const normalizedParts = normalizedToken.split('.')
      console.log('Normalized token parts:', normalizedParts.length)
    } else {
      console.log('WARNING: Normalized token is null/undefined')
    }

    // Check for test token bypass (development only)
    const testUser = handleTestToken(normalizedToken)
    if (testUser) {
      console.log('Using test token bypass')
      req.user = testUser
      return next()
    }

    // Try Firebase token validation first, fallback to JWT
    console.log('Starting Firebase token verification...')
    try {
      // Pass the normalized token directly - ensure it's a clean string
      // Remove any potential encoding issues, null bytes, or control characters
      let cleanToken = String(normalizedToken).trim()
      // Remove any null bytes, carriage returns, and ensure no extra whitespace
      cleanToken = cleanToken.replace(/\0/g, '').replace(/\r/g, '').replace(/\n/g, '').trim()
      
      // Validate token format before passing to Firebase
      const tokenParts = cleanToken.split('.')
      if (tokenParts.length !== 3) {
        console.error('Invalid token format: expected 3 parts, got', tokenParts.length)
        throw new Error(`Invalid JWT format: expected 3 parts, got ${tokenParts.length}`)
      }
      
      console.log('Clean token length:', cleanToken.length)
      console.log('Clean token first 50 chars:', cleanToken.substring(0, 50))
      console.log('Clean token last 20 chars:', cleanToken.substring(Math.max(0, cleanToken.length - 20)))
      console.log('Token parts count:', tokenParts.length)
      console.log('Calling validateFirebaseToken...')
      
      const user = await validateFirebaseToken(cleanToken)
      console.log('SUCCESS: Firebase token verified successfully')
      console.log('User ID:', user.firebaseUid || user._id)
      req.user = user
      console.log('=== AUTH MIDDLEWARE SUCCESS ===')
      return next()
    } catch (firebaseError) {
      // Always log Firebase errors - they're critical for debugging
      console.log('=== FIREBASE TOKEN VALIDATION FAILED ===')
      console.log('Error name:', firebaseError.name)
      console.log('Error message:', firebaseError.message)
      console.log('Error code:', firebaseError.code || 'N/A')
      if (firebaseError.errorInfo) {
        console.log('Firebase error info:', JSON.stringify(firebaseError.errorInfo, null, 2))
      }
      if (firebaseError.stack) {
        console.log('Error stack (first 500 chars):', firebaseError.stack.substring(0, 500))
      }
      console.log('========================================')
      
      // If Firebase Admin SDK is not initialized, provide a clearer error
      if (firebaseError.message && firebaseError.message.includes('Firebase Admin SDK not initialized')) {
        console.error('❌ Firebase Admin SDK not initialized - check environment variables')
        return res.status(500).json({
          success: false,
          message: 'Server configuration error: Firebase Admin SDK not initialized'
        })
      }
      
      // Try JWT fallback
      console.log('Attempting JWT token validation as fallback...')
      try {
        const user = await validateJwtToken(normalizedToken)
        console.log('SUCCESS: JWT token verified successfully')
        req.user = user
        console.log('=== AUTH MIDDLEWARE SUCCESS (JWT) ===')
        return next()
      } catch (jwtError) {
        console.log('JWT token validation also failed')
        console.log('JWT error message:', jwtError.message)
        console.log('JWT error name:', jwtError.name)
        
        // Return error with details - include Firebase error info for debugging
        const errorResponse = {
          success: false,
          message: 'Token is not valid',
          // Include Firebase error details to help debug
          error: firebaseError.message || 'Token verification failed',
          code: firebaseError.code || 'UNKNOWN'
        }
        
        console.log('Returning 401 error response')
        return res.status(401).json(errorResponse)
      }
    }
  } catch (error) {
    // Catch any unexpected errors
    console.log('=== UNEXPECTED ERROR in auth middleware ===')
    console.log('Error message:', error.message)
    console.log('Error name:', error.name)
    console.log('Error stack:', error.stack)
    console.log('=== AUTH MIDDLEWARE ERROR END ===')
    return res.status(401).json({
      success: false,
      message: 'Token is not valid',
      details: process.env.NODE_ENV === 'development' ? error.message : undefined
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
