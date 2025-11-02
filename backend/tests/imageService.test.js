// Mock environment variables
process.env.SUPABASE_URL = 'https://test.supabase.co'
process.env.SUPABASE_SERVICE_ROLE_KEY = 'test-key'

// Mock Supabase
jest.mock('@supabase/supabase-js', () => ({
  createClient: jest.fn(() => ({
    storage: {
      from: jest.fn(() => ({
        upload: jest.fn(() => ({
          data: { path: 'test/path.jpg' },
          error: null
        })),
        getPublicUrl: jest.fn(() => ({
          data: { publicUrl: 'https://test.supabase.co/storage/v1/object/public/images/test/path.jpg' }
        }))
      }))
    }
  }))
}))

// Mock Sharp
jest.mock('sharp', () => {
  return jest.fn(() => ({
    resize: jest.fn().mockReturnThis(),
    jpeg: jest.fn().mockReturnThis(),
    png: jest.fn().mockReturnThis(),
    webp: jest.fn().mockReturnThis(),
    toBuffer: jest.fn().mockResolvedValue(Buffer.from('processed-image-data'))
  }))
})

const ImageService = require('../services/imageService')

describe('ImageService', () => {
  let service

  beforeEach(() => {
    service = new ImageService()
  })

  describe('File Validation', () => {
    it('should validate correct file types', () => {
      expect(service.isValidFileType('image/jpeg')).toBe(true)
      expect(service.isValidFileType('image/png')).toBe(true)
      expect(service.isValidFileType('image/webp')).toBe(true)
      expect(service.isValidFileType('image/gif')).toBe(true)
    })

    it('should reject invalid file types', () => {
      expect(service.isValidFileType('text/plain')).toBe(false)
      expect(service.isValidFileType('application/pdf')).toBe(false)
      expect(service.isValidFileType('video/mp4')).toBe(false)
    })
  })

  describe('Image Upload', () => {
    it('should upload image successfully', async () => {
      const mockBuffer = Buffer.from('test-image-data')
      const result = await service.uploadImage(mockBuffer, 'test.jpg', 'test-folder')

      expect(result.success).toBe(true)
      expect(result.data.fileName).toBeDefined()
      expect(result.data.publicUrl).toBeDefined()
    })

    it('should handle file size validation', async () => {
      const largeBuffer = Buffer.alloc(service.maxFileSize + 1)
      const result = await service.uploadImage(largeBuffer, 'large.jpg')

      expect(result.success).toBe(false)
      expect(result.error).toContain('File size exceeds maximum')
    })

    it('should generate UUID filename for non-prefixed files', async () => {
      const mockBuffer = Buffer.from('test-image-data')
      const result = await service.uploadImage(mockBuffer, 'original.jpg', 'test-folder')

      expect(result.success).toBe(true)
      expect(result.data.fileName).not.toBe('original.jpg')
    })

    it('should preserve custom filename for prefixed files', async () => {
      const mockBuffer = Buffer.from('test-image-data')
      const result = await service.uploadImage(mockBuffer, 'pet_12345.jpg', 'pets/12345/profile')

      expect(result.success).toBe(true)
      expect(result.data.fileName).toBe('pet_12345.jpg')
    })
  })

  describe('Image Processing', () => {
    it('should process image with default options', async () => {
      const mockBuffer = Buffer.from('test-image-data')
      const result = await service.processImage(mockBuffer, {})

      expect(result).toBeInstanceOf(Buffer)
    })

    it('should process image with custom dimensions', async () => {
      const mockBuffer = Buffer.from('test-image-data')
      const options = { width: 300, height: 300, quality: 90 }
      const result = await service.processImage(mockBuffer, options)

      expect(result).toBeInstanceOf(Buffer)
    })
  })

  describe('Content Type Detection', () => {
    it('should return correct content type for JPEG', () => {
      expect(service.getContentType('.jpg')).toBe('image/jpeg')
      expect(service.getContentType('.jpeg')).toBe('image/jpeg')
    })

    it('should return correct content type for PNG', () => {
      expect(service.getContentType('.png')).toBe('image/png')
    })

    it('should return correct content type for WebP', () => {
      expect(service.getContentType('.webp')).toBe('image/webp')
    })

    it('should return correct content type for GIF', () => {
      expect(service.getContentType('.gif')).toBe('image/gif')
    })
  })
})
