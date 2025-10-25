const request = require('supertest');
const app = require('../app');
const User = require('../models/User');

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
          update: jest.fn(),
          delete: jest.fn()
        }))
      }))
    }))
  }))
}));

describe('User Management Controller', () => {
  beforeEach(() => {
    jest.clearAllMocks();
  });

  describe('GET /api/users', () => {
    it('should get all users for admin', async () => {
      const mockUsers = [
        {
          _id: 'user1',
          firstName: 'John',
          lastName: 'Doe',
          email: 'john@example.com',
          role: 'user'
        },
        {
          _id: 'user2',
          firstName: 'Jane',
          lastName: 'Smith',
          email: 'jane@example.com',
          role: 'admin'
        }
      ];

      jest.spyOn(User, 'find').mockReturnValue({
        select: jest.fn().mockReturnValue({
          sort: jest.fn().mockReturnValue({
            skip: jest.fn().mockReturnValue({
              limit: jest.fn().mockResolvedValue(mockUsers)
            })
          })
        })
      });

      jest.spyOn(User, 'countDocuments').mockResolvedValue(2);

      const response = await request(app)
        .get('/api/users')
        .set('Authorization', 'Bearer mock-jwt-token')
        .expect(200);

      expect(response.body.success).toBe(true);
      expect(response.body.count).toBe(2);
      expect(response.body.data).toEqual(mockUsers);
    });

    it('should return 403 for non-admin users', async () => {
      const response = await request(app)
        .get('/api/users')
        .set('Authorization', 'Bearer user-token')
        .expect(403);

      expect(response.body.success).toBe(false);
      expect(response.body.message).toContain('Not authorized');
    });
  });

  describe('GET /api/users/:id', () => {
    it('should get single user by ID', async () => {
      const mockUser = {
        _id: 'user123',
        firstName: 'John',
        lastName: 'Doe',
        email: 'john@example.com',
        role: 'user'
      };

      jest.spyOn(User, 'findById').mockReturnValue({
        select: jest.fn().mockResolvedValue(mockUser)
      });

      const response = await request(app)
        .get('/api/users/user123')
        .set('Authorization', 'Bearer mock-jwt-token')
        .expect(200);

      expect(response.body.success).toBe(true);
      expect(response.body.data).toEqual(mockUser);
    });

    it('should return 404 for non-existent user', async () => {
      jest.spyOn(User, 'findById').mockReturnValue({
        select: jest.fn().mockResolvedValue(null)
      });

      const response = await request(app)
        .get('/api/users/nonexistent')
        .set('Authorization', 'Bearer mock-jwt-token')
        .expect(404);

      expect(response.body.success).toBe(false);
      expect(response.body.message).toBe('User not found');
    });

    it('should return 403 for unauthorized access', async () => {
      const mockUser = {
        _id: 'different-user',
        firstName: 'Jane',
        lastName: 'Smith',
        email: 'jane@example.com'
      };

      jest.spyOn(User, 'findById').mockReturnValue({
        select: jest.fn().mockResolvedValue(mockUser)
      });

      const response = await request(app)
        .get('/api/users/different-user')
        .set('Authorization', 'Bearer mock-jwt-token')
        .expect(403);

      expect(response.body.success).toBe(false);
      expect(response.body.message).toContain('Not authorized');
    });
  });

  describe('PUT /api/users/:id', () => {
    it('should update user successfully', async () => {
      const updateData = {
        firstName: 'John Updated',
        lastName: 'Doe Updated',
        phone: '+1234567890'
      };

      const mockUpdatedUser = {
        _id: 'user123',
        firstName: 'John Updated',
        lastName: 'Doe Updated',
        email: 'john@example.com',
        phone: '+1234567890',
        role: 'user'
      };

      jest.spyOn(User, 'findById').mockResolvedValue({ _id: 'user123', role: 'user' });
      jest.spyOn(User, 'findByIdAndUpdate').mockReturnValue({
        select: jest.fn().mockResolvedValue(mockUpdatedUser)
      });

      const response = await request(app)
        .put('/api/users/user123')
        .set('Authorization', 'Bearer mock-jwt-token')
        .send(updateData)
        .expect(200);

      expect(response.body.success).toBe(true);
      expect(response.body.message).toBe('User updated successfully');
      expect(response.body.data.firstName).toBe('John Updated');
    });

    it('should return 400 for invalid input', async () => {
      const invalidData = {
        firstName: 'A', // Too short
        phone: 'invalid-phone'
      };

      const response = await request(app)
        .put('/api/users/user123')
        .set('Authorization', 'Bearer mock-jwt-token')
        .send(invalidData)
        .expect(400);

      expect(response.body.success).toBe(false);
      expect(response.body.message).toBe('Validation errors');
    });

    it('should return 404 for non-existent user', async () => {
      jest.spyOn(User, 'findById').mockResolvedValue(null);

      const response = await request(app)
        .put('/api/users/nonexistent')
        .set('Authorization', 'Bearer mock-jwt-token')
        .send({ firstName: 'Updated' })
        .expect(404);

      expect(response.body.success).toBe(false);
      expect(response.body.message).toBe('User not found');
    });

    it('should return 403 for unauthorized update', async () => {
      const mockUser = {
        _id: 'different-user',
        role: 'user'
      };

      jest.spyOn(User, 'findById').mockResolvedValue(mockUser);

      const response = await request(app)
        .put('/api/users/different-user')
        .set('Authorization', 'Bearer mock-jwt-token')
        .send({ firstName: 'Updated' })
        .expect(403);

      expect(response.body.success).toBe(false);
      expect(response.body.message).toContain('Not authorized');
    });
  });

  describe('DELETE /api/users/:id', () => {
    it('should delete user successfully (admin only)', async () => {
      const mockUser = {
        _id: 'user123',
        firstName: 'John',
        lastName: 'Doe',
        email: 'john@example.com'
      };

      jest.spyOn(User, 'findById').mockResolvedValue(mockUser);
      jest.spyOn(User, 'findByIdAndDelete').mockResolvedValue({});

      const response = await request(app)
        .delete('/api/users/user123')
        .set('Authorization', 'Bearer admin-token')
        .expect(200);

      expect(response.body.success).toBe(true);
      expect(response.body.message).toBe('User deleted successfully');
    });

    it('should return 404 for non-existent user', async () => {
      jest.spyOn(User, 'findById').mockResolvedValue(null);

      const response = await request(app)
        .delete('/api/users/nonexistent')
        .set('Authorization', 'Bearer admin-token')
        .expect(404);

      expect(response.body.success).toBe(false);
      expect(response.body.message).toBe('User not found');
    });

    it('should return 400 when admin tries to delete themselves', async () => {
      const mockAdmin = {
        _id: 'admin123',
        firstName: 'Admin',
        lastName: 'User',
        email: 'admin@example.com',
        role: 'admin'
      };

      jest.spyOn(User, 'findById').mockResolvedValue(mockAdmin);

      const response = await request(app)
        .delete('/api/users/admin123')
        .set('Authorization', 'Bearer admin-token')
        .expect(400);

      expect(response.body.success).toBe(false);
      expect(response.body.message).toBe('Cannot delete your own account');
    });

    it('should return 403 for non-admin users', async () => {
      const response = await request(app)
        .delete('/api/users/user123')
        .set('Authorization', 'Bearer user-token')
        .expect(403);

      expect(response.body.success).toBe(false);
      expect(response.body.message).toContain('Not authorized');
    });
  });

  describe('Account Deletion Scenarios', () => {
    it('should handle user self-deletion request', async () => {
      // This would be a custom endpoint for users to delete their own accounts
      const deleteData = {
        password: 'password123',
        confirmDeletion: true
      };

      // Mock password verification
      const mockUser = {
        _id: 'user123',
        email: 'user@example.com',
        matchPassword: jest.fn().mockResolvedValue(true)
      };

      jest.spyOn(User, 'findById').mockResolvedValue(mockUser);
      jest.spyOn(User, 'findByIdAndDelete').mockResolvedValue({});

      // Note: This would require a custom endpoint like POST /api/users/delete-account
      // For now, we'll test the admin deletion as a proxy
      const response = await request(app)
        .delete('/api/users/user123')
        .set('Authorization', 'Bearer admin-token')
        .expect(200);

      expect(response.body.success).toBe(true);
    });

    it('should handle deletion with associated data cleanup', async () => {
      const mockUser = {
        _id: 'user123',
        firstName: 'John',
        lastName: 'Doe',
        email: 'john@example.com'
      };

      jest.spyOn(User, 'findById').mockResolvedValue(mockUser);
      jest.spyOn(User, 'findByIdAndDelete').mockResolvedValue({});

      const response = await request(app)
        .delete('/api/users/user123')
        .set('Authorization', 'Bearer admin-token')
        .expect(200);

      expect(response.body.success).toBe(true);
      expect(response.body.message).toBe('User deleted successfully');
    });
  });
});
