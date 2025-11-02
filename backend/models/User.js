const { admin } = require('../config/firebase')
const bcrypt = require('bcryptjs')

class User {
  constructor (data) {
    this.firstName = data.firstName
    this.lastName = data.lastName
    this.email = data.email
    this.password = data.password
    this.phone = data.phone
    this.role = data.role || 'user'
    this.address = data.address || {}
    this.emergencyContact = data.emergencyContact || {}
    this.isActive = data.isActive !== undefined ? data.isActive : true
    this.lastLogin = data.lastLogin
    this.refreshTokens = data.refreshTokens || []
    this.createdAt = data.createdAt || new Date()
    this.updatedAt = data.updatedAt || new Date()
  }

  // Get Firestore collection reference
  static getCollection () {
    if (!admin.apps.length) {
      throw new Error('Firebase Admin SDK not initialized')
    }
    return admin.firestore().collection('users')
  }

  // Create a new user
  static async create (userData) {
    const collection = User.getCollection()

    // Check if user already exists
    const existingUser = await User.findOne({ email: userData.email })
    if (existingUser) {
      throw new Error('User already exists with this email')
    }

    // Hash password
    const salt = await bcrypt.genSalt(10)
    const hashedPassword = await bcrypt.hash(userData.password, salt)

    // Create user document
    const userDoc = {
      firstName: userData.firstName,
      lastName: userData.lastName,
      email: userData.email.toLowerCase(),
      password: hashedPassword,
      phone: userData.phone,
      role: userData.role || 'user',
      address: userData.address || {},
      emergencyContact: userData.emergencyContact || {},
      isActive: userData.isActive !== undefined ? userData.isActive : true,
      lastLogin: null,
      refreshTokens: [],
      createdAt: new Date(),
      updatedAt: new Date()
    }

    // Add to Firestore
    const docRef = await collection.add(userDoc)

    // Return user with ID
    return {
      _id: docRef.id,
      ...userDoc
    }
  }

  // Find user by criteria
  static async findOne (criteria) {
    const collection = User.getCollection()

    if (criteria.email) {
      const snapshot = await collection
        .where('email', '==', criteria.email.toLowerCase())
        .limit(1)
        .get()

      if (snapshot.empty) {
        return null
      }

      const doc = snapshot.docs[0]
      return {
        _id: doc.id,
        ...doc.data()
      }
    }

    if (criteria._id || criteria.id) {
      const doc = await collection.doc(criteria._id || criteria.id).get()

      if (!doc.exists) {
        return null
      }

      return {
        _id: doc.id,
        ...doc.data()
      }
    }

    return null
  }

  // Find user by ID
  static async findById (id) {
    const collection = User.getCollection()
    const doc = await collection.doc(id).get()

    if (!doc.exists) {
      return null
    }

    return {
      _id: doc.id,
      ...doc.data()
    }
  }

  // Save user (update existing)
  async save () {
    const collection = User.getCollection()
    this.updatedAt = new Date()

    if (this._id) {
      // Update existing user
      await collection.doc(this._id).update({
        ...this,
        _id: undefined // Don't save the ID in the document
      })
      return this
    } else {
      // Create new user
      const docRef = await collection.add({
        ...this,
        _id: undefined
      })
      this._id = docRef.id
      return this
    }
  }

  // Update user by ID
  static async findByIdAndUpdate (id, updateData, _options = {}) {
    const collection = User.getCollection()
    const updatePayload = {
      ...updateData,
      updatedAt: new Date()
    }

    // Handle MongoDB-style $push and $pull operations
    if (updateData.$push) {
      const user = await User.findById(id)
      if (user) {
        const field = Object.keys(updateData.$push)[0]
        const value = updateData.$push[field]

        if (!user[field]) {
          user[field] = []
        }

        if (Array.isArray(user[field])) {
          user[field].push(value)
        }

        await collection.doc(id).update({
          [field]: user[field],
          updatedAt: new Date()
        })
      }
    } else if (updateData.$pull) {
      const user = await User.findById(id)
      if (user) {
        const field = Object.keys(updateData.$pull)[0]
        const pullData = updateData.$pull[field]

        if (user[field] && Array.isArray(user[field])) {
          user[field] = user[field].filter(item => {
            if (typeof pullData === 'object' && pullData.token) {
              return item.token !== pullData.token
            }
            return item !== pullData
          })
        }

        await collection.doc(id).update({
          [field]: user[field],
          updatedAt: new Date()
        })
      }
    } else {
      // Regular update
      await collection.doc(id).update(updatePayload)
    }

    // Return updated user
    return await User.findById(id)
  }

  // Match password
  async matchPassword (enteredPassword) {
    return await bcrypt.compare(enteredPassword, this.password)
  }

  // Get full name
  get fullName () {
    return `${this.firstName} ${this.lastName}`
  }

  // Convert to JSON (hide sensitive data)
  toJSON () {
    const user = { ...this }
    delete user.password
    delete user.refreshTokens
    return user
  }
}

module.exports = User
