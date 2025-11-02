const request = require('supertest')
const app = require('../app')
const User = require('../models/User')

// Mock Firebase Admin SDK
jest.mock('../config/firebase', () => ({
  initializeFirebase: jest.fn(() => ({
    firestore: jest.fn(() => ({
      collection: jest.fn(() => ({
        add: jest.fn(),
        where: jest.fn(() => ({
          limit: jest.fn(() => ({
            get: jest.fn()
          }))
        })),
        doc: jest.fn(() => ({
          get: jest.fn(),
          update: jest.fn()
        }))
      }))
    }))
  }))
}))

// Mock JWT functions
jest.mock('../config/jwt', () => ({
  generateToken: jest.fn(() => 'mock-jwt-token'),
  generateRefreshToken: jest.fn(() => 'mock-refresh-token')
}))

describe('Authentication Controller', () => {
  beforeEach(() => {
    jest.clearAllMocks()
  })

  describe('POST /api/auth/register', () => {
    it('should register a new user successfully', async () => {
      const userData = {
        firstName: 'John',
        lastName: 'Doe',
        email: 'john.doe@example.com',
        password: 'password123',
        phone: '+1234567890'
      }

      // Mock User.create to return a user
      jest.spyOn(User, 'create').mockResolvedValue({
        _id: 'user123',
        ...userData,
        role: 'user'
      })

      // Mock User.findByIdAndUpdate for refresh token
      jest.spyOn(User, 'findByIdAndUpdate').mockResolvedValue({})

      const response = await request(app)
        .post('/api/auth/register')
        .send(userData)
        .expect(201)

      expect(response.body.success).toBe(true)
      expect(response.body.message).toBe('User registered successfully')
      expect(response.body.data.user.email).toBe(userData.email)
      expect(response.body.data.token).toBe('mock-jwt-token')
      expect(response.body.data.refreshToken).toBe('mock-refresh-token')
    })

    it('should return 400 for invalid input', async () => {
      const invalidData = {
        firstName: '',
        email: 'invalid-email',
        password: '123' // Too short
      }

      const response = await request(app)
        .post('/api/auth/register')
        .send(invalidData)
        .expect(400)

      expect(response.body.success).toBe(false)
      expect(response.body.message).toBe('Validation errors')
    })

    it('should return 500 for server errors', async () => {
      const userData = {
        firstName: 'John',
        lastName: 'Doe',
        email: 'john.doe@example.com',
        password: 'password123',
        phone: '+1234567890'
      }

      // Mock User.create to throw an error
      jest.spyOn(User, 'create').mockRejectedValue(new Error('Database connection failed'))

      const response = await request(app)
        .post('/api/auth/register')
        .send(userData)
        .expect(500)

      expect(response.body.success).toBe(false)
      expect(response.body.message).toContain('Server error')
    })
  })

  describe('POST /api/auth/login', () => {
    it('should login user with valid credentials', async () => {
      const loginData = {
        email: 'john.doe@example.com',
        password: 'password123'
      }

      const mockUser = {
        _id: 'user123',
        email: 'john.doe@example.com',
        password: '$2a$10$hashedpassword',
        firstName: 'John',
        lastName: 'Doe',
        isActive: true,
        role: 'user'
      }

      // Mock User.findOne
      jest.spyOn(User, 'findOne').mockResolvedValue(mockUser)

      // Mock User constructor and matchPassword
      const mockUserInstance = {
        matchPassword: jest.fn().mockResolvedValue(true)
      }
      jest.spyOn(User, 'constructor').mockImplementation(() => mockUserInstance)

      // Mock User.findByIdAndUpdate
      jest.spyOn(User, 'findByIdAndUpdate').mockResolvedValue({})

      const response = await request(app)
        .post('/api/auth/login')
        .send(loginData)
        .expect(200)

      expect(response.body.success).toBe(true)
      expect(response.body.message).toBe('Login successful')
      expect(response.body.data.user.email).toBe(loginData.email)
    })

    it('should return 401 for invalid credentials', async () => {
      const loginData = {
        email: 'nonexistent@example.com',
        password: 'wrongpassword'
      }

      // Mock User.findOne to return null (user not found)
      jest.spyOn(User, 'findOne').mockResolvedValue(null)

      const response = await request(app)
        .post('/api/auth/login')
        .send(loginData)
        .expect(401)

      expect(response.body.success).toBe(false)
      expect(response.body.message).toBe('Invalid credentials')
    })

    it('should return 401 for inactive user', async () => {
      const loginData = {
        email: 'john.doe@example.com',
        password: 'password123'
      }

      const mockUser = {
        _id: 'user123',
        email: 'john.doe@example.com',
        isActive: false
      }

      jest.spyOn(User, 'findOne').mockResolvedValue(mockUser)

      const response = await request(app)
        .post('/api/auth/login')
        .send(loginData)
        .expect(401)

      expect(response.body.success).toBe(false)
      expect(response.body.message).toBe('Account is deactivated')
    })
  })

  describe('GET /api/auth/me', () => {
    it('should return current user data', async () => {
      const mockUser = {
        _id: 'user123',
        firstName: 'John',
        lastName: 'Doe',
        email: 'john.doe@example.com',
        phone: '+1234567890',
        role: 'user',
        address: {},
        emergencyContact: {},
        lastLogin: new Date(),
        createdAt: new Date()
      }

      // Mock User.findById
      jest.spyOn(User, 'findById').mockResolvedValue(mockUser)

      const response = await request(app)
        .get('/api/auth/me')
        .set('Authorization', 'Bearer mock-jwt-token')
        .expect(200)

      expect(response.body.success).toBe(true)
      expect(response.body.data.user.email).toBe(mockUser.email)
    })
  })

  describe('POST /api/auth/logout', () => {
    it('should logout user successfully', async () => {
      const logoutData = {
        refreshToken: 'mock-refresh-token'
      }

      // Mock User.findByIdAndUpdate
      jest.spyOn(User, 'findByIdAndUpdate').mockResolvedValue({})

      const response = await request(app)
        .post('/api/auth/logout')
        .set('Authorization', 'Bearer mock-jwt-token')
        .send(logoutData)
        .expect(200)

      expect(response.body.success).toBe(true)
      expect(response.body.message).toBe('Logout successful')
    })
  })
})
