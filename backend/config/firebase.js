const admin = require('firebase-admin')

// Initialize Firebase Admin SDK
// You'll need to add your Firebase service account key
// Download it from Firebase Console > Project Settings > Service Accounts
let firebaseApp

const initializeFirebase = () => {
  try {
    // Option 1: Use service account key file (recommended for development)
    // const serviceAccount = require('../path/to/your/serviceAccountKey.json');
    // firebaseApp = admin.initializeApp({
    //   credential: admin.credential.cert(serviceAccount)
    // });

    // Option 2: Use environment variables (recommended for production)
    const hasProjectId = !!process.env.FIREBASE_PROJECT_ID
    const hasPrivateKey = !!process.env.FIREBASE_PRIVATE_KEY
    const hasClientEmail = !!process.env.FIREBASE_CLIENT_EMAIL

    console.log('Checking Firebase initialization...')
    console.log('FIREBASE_PROJECT_ID:', hasProjectId ? 'SET' : 'MISSING')
    console.log('FIREBASE_PRIVATE_KEY:', hasPrivateKey ? 'SET' : 'MISSING')
    console.log('FIREBASE_CLIENT_EMAIL:', hasClientEmail ? 'SET' : 'MISSING')

    if (hasProjectId && hasPrivateKey && hasClientEmail) {
      try {
        firebaseApp = admin.initializeApp({
          credential: admin.credential.cert({
            projectId: process.env.FIREBASE_PROJECT_ID,
            privateKey: process.env.FIREBASE_PRIVATE_KEY.replace(/\\n/g, '\n'),
            clientEmail: process.env.FIREBASE_CLIENT_EMAIL
          })
        })
        console.log('✅ Firebase Admin SDK initialized successfully')
        console.log('Firebase Project ID:', process.env.FIREBASE_PROJECT_ID)
        return firebaseApp
      } catch (initError) {
        console.error('❌ Error initializing Firebase Admin SDK:', initError.message)
        console.error('Init error details:', initError)
        return null
      }
    } else {
      console.warn('⚠️  Firebase Admin SDK not initialized. Missing required environment variables.')
      console.warn('Required: FIREBASE_PROJECT_ID, FIREBASE_PRIVATE_KEY, FIREBASE_CLIENT_EMAIL')
      return null
    }
  } catch (error) {
    console.error('❌ Unexpected error initializing Firebase Admin SDK:', error)
    return null
  }
}

const verifyFirebaseToken = async (idToken) => {
  try {
    // Check if Firebase Admin SDK is initialized (either via our variable or already initialized)
    const isInitialized = firebaseApp || admin.apps.length > 0
    
    if (!isInitialized) {
      console.error('❌ Firebase Admin SDK not initialized - check environment variables')
      console.error('Required env vars: FIREBASE_PROJECT_ID, FIREBASE_PRIVATE_KEY, FIREBASE_CLIENT_EMAIL')
      console.error('Current state - firebaseApp:', firebaseApp ? 'exists' : 'null')
      console.error('Current state - admin.apps.length:', admin.apps.length)
      throw new Error('Firebase Admin SDK not initialized')
    }
    
    console.log('✅ Firebase Admin SDK is initialized, proceeding with token verification')

    if (!idToken || typeof idToken !== 'string') {
      throw new Error('Invalid token: token is not a string')
    }

    // Validate JWT format (should have 3 parts)
    const parts = idToken.split('.')
    if (parts.length !== 3) {
      throw new Error(`Invalid JWT format: expected 3 parts, got ${parts.length}`)
    }

    console.log('Attempting to verify Firebase token...')
    console.log('Token length:', idToken.length)
    console.log('Token parts:', parts.map((p, i) => `Part ${i + 1}: ${p.length} chars`).join(', '))
    
    // Trim any whitespace that might have been introduced
    const trimmedToken = idToken.trim()
    
    // Use the default app if firebaseApp is not set but admin is initialized
    const authInstance = firebaseApp ? admin.auth(firebaseApp) : admin.auth()
    const decodedToken = await authInstance.verifyIdToken(trimmedToken)
    console.log('Firebase token verified successfully for user:', decodedToken.uid)
    return decodedToken
  } catch (error) {
    console.error('Error verifying Firebase token:', error.message)
    console.error('Error code:', error.code)
    if (error.errorInfo) {
      console.error('Error info:', JSON.stringify(error.errorInfo, null, 2))
    }
    console.error('Error stack:', error.stack)
    throw error
  }
}

const getFirebaseUser = async (uid) => {
  try {
    if (!firebaseApp) {
      throw new Error('Firebase Admin SDK not initialized')
    }

    const userRecord = await admin.auth().getUser(uid)
    return userRecord
  } catch (error) {
    console.error('Error getting Firebase user:', error)
    throw error
  }
}

module.exports = {
  initializeFirebase,
  verifyFirebaseToken,
  getFirebaseUser,
  admin
}
