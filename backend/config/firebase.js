const admin = require('firebase-admin');

// Initialize Firebase Admin SDK
// You'll need to add your Firebase service account key
// Download it from Firebase Console > Project Settings > Service Accounts
let firebaseApp;

const initializeFirebase = () => {
  try {
    // Option 1: Use service account key file (recommended for development)
    // const serviceAccount = require('../path/to/your/serviceAccountKey.json');
    // firebaseApp = admin.initializeApp({
    //   credential: admin.credential.cert(serviceAccount)
    // });

    // Option 2: Use environment variables (recommended for production)
    if (process.env.FIREBASE_PROJECT_ID && process.env.FIREBASE_PRIVATE_KEY && process.env.FIREBASE_CLIENT_EMAIL) {
      firebaseApp = admin.initializeApp({
        credential: admin.credential.cert({
          projectId: process.env.FIREBASE_PROJECT_ID,
          privateKey: process.env.FIREBASE_PRIVATE_KEY.replace(/\\n/g, '\n'),
          clientEmail: process.env.FIREBASE_CLIENT_EMAIL,
        })
      });
    } else {
      console.warn('Firebase Admin SDK not initialized. Add Firebase credentials to environment variables.');
      return null;
    }

    console.log('Firebase Admin SDK initialized successfully');
    return firebaseApp;
  } catch (error) {
    console.error('Error initializing Firebase Admin SDK:', error);
    return null;
  }
};

const verifyFirebaseToken = async (idToken) => {
  try {
    if (!firebaseApp) {
      throw new Error('Firebase Admin SDK not initialized');
    }

    const decodedToken = await admin.auth().verifyIdToken(idToken);
    return decodedToken;
  } catch (error) {
    console.error('Error verifying Firebase token:', error);
    throw error;
  }
};

const getFirebaseUser = async (uid) => {
  try {
    if (!firebaseApp) {
      throw new Error('Firebase Admin SDK not initialized');
    }

    const userRecord = await admin.auth().getUser(uid);
    return userRecord;
  } catch (error) {
    console.error('Error getting Firebase user:', error);
    throw error;
  }
};

module.exports = {
  initializeFirebase,
  verifyFirebaseToken,
  getFirebaseUser,
  admin
};
