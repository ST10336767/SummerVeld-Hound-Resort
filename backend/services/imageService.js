const { supabase } = require('../config/supabase')
const sharp = require('sharp')
const { v4: uuidv4 } = require('uuid')
const path = require('path')

class ImageService {
  constructor () {
    this.bucketName = process.env.SUPABASE_BUCKET_NAME || 'dog_images'
    this.maxFileSize = parseInt(process.env.MAX_FILE_SIZE) || 5 * 1024 * 1024 // 5MB default
    this.allowedMimeTypes = [
      'image/jpeg',
      'image/jpg',
      'image/png',
      'image/webp',
      'image/gif'
    ]
  }

  /**
   * Upload image to Supabase Storage
   * @param {Buffer} fileBuffer - Image file buffer
   * @param {string} originalName - Original filename or custom filename
   * @param {string} folder - Folder path in bucket (optional)
   * @param {Object} options - Upload options
   * @returns {Promise<Object>} Upload result
   */
  async uploadImage (fileBuffer, originalName, folder = '', options = {}) {
    try {
      // Validate file size
      if (fileBuffer.length > this.maxFileSize) {
        throw new Error(`File size exceeds maximum allowed size of ${this.maxFileSize / (1024 * 1024)}MB`)
      }

      // Use provided filename if it's already formatted (contains pet_ or user_ prefix)
      // Otherwise generate unique UUID filename
      const fileExtension = path.extname(originalName).toLowerCase()
      let fileName

      if (originalName.startsWith('pet_') || originalName.startsWith('user_')) {
        // Use the provided custom filename
        fileName = originalName
      } else {
        // Generate UUID filename for backward compatibility
        fileName = `${uuidv4()}${fileExtension}`
      }

      const filePath = folder ? `${folder}/${fileName}` : fileName

      // Process image with Sharp (resize, optimize)
      const processedBuffer = await this.processImage(fileBuffer, options)

      // Upload to Supabase Storage
      const { error } = await supabase.storage
        .from(this.bucketName)
        .upload(filePath, processedBuffer, {
          contentType: this.getContentType(fileExtension),
          cacheControl: '3600',
          upsert: false
        })

      if (error) {
        throw new Error(`Upload failed: ${error.message}`)
      }

      // Get public URL
      const { data: urlData } = supabase.storage
        .from(this.bucketName)
        .getPublicUrl(filePath)

      return {
        success: true,
        data: {
          fileName,
          filePath,
          publicUrl: urlData.publicUrl,
          size: processedBuffer.length,
          originalName
        }
      }
    } catch (error) {
      console.error('Image upload error:', error)
      return {
        success: false,
        error: error.message
      }
    }
  }

  /**
   * Process image with Sharp
   * @param {Buffer} buffer - Image buffer
   * @param {Object} options - Processing options
   * @returns {Promise<Buffer>} Processed image buffer
   */
  async processImage (buffer, options = {}) {
    const {
      width = 1200,
      height = 1200,
      quality = 85,
      format = 'jpeg'
    } = options

    try {
      let sharpInstance = sharp(buffer)

      // Get image metadata
      const metadata = await sharpInstance.metadata()

      // Resize if needed
      if (metadata.width > width || metadata.height > height) {
        sharpInstance = sharpInstance.resize(width, height, {
          fit: 'inside',
          withoutEnlargement: true
        })
      }

      // Convert and optimize
      switch (format.toLowerCase()) {
        case 'jpeg':
        case 'jpg':
          sharpInstance = sharpInstance.jpeg({ quality })
          break
        case 'png':
          sharpInstance = sharpInstance.png({ quality })
          break
        case 'webp':
          sharpInstance = sharpInstance.webp({ quality })
          break
        default:
          sharpInstance = sharpInstance.jpeg({ quality })
      }

      return await sharpInstance.toBuffer()
    } catch (error) {
      console.error('Image processing error:', error)
      throw new Error('Failed to process image')
    }
  }

  /**
   * Delete image from Supabase Storage
   * @param {string} filePath - File path in bucket
   * @returns {Promise<Object>} Delete result
   */
  async deleteImage (filePath) {
    try {
      const { error } = await supabase.storage
        .from(this.bucketName)
        .remove([filePath])

      if (error) {
        throw new Error(`Delete failed: ${error.message}`)
      }

      return {
        success: true,
        message: 'Image deleted successfully'
      }
    } catch (error) {
      console.error('Image delete error:', error)
      return {
        success: false,
        error: error.message
      }
    }
  }

  /**
   * Get image URL from Supabase Storage
   * @param {string} filePath - File path in bucket
   * @returns {string} Public URL
   */
  getImageUrl (filePath) {
    const { data } = supabase.storage
      .from(this.bucketName)
      .getPublicUrl(filePath)

    return data.publicUrl
  }

  /**
   * List images in a folder
   * @param {string} folder - Folder path (optional)
   * @param {Object} options - List options
   * @returns {Promise<Object>} List result
   */
  async listImages (folder = '', options = {}) {
    try {
      const { data, error } = await supabase.storage
        .from(this.bucketName)
        .list(folder, {
          limit: options.limit || 100,
          offset: options.offset || 0,
          sortBy: { column: 'created_at', order: 'desc' }
        })

      if (error) {
        throw new Error(`List failed: ${error.message}`)
      }

      // Add public URLs to each file
      const imagesWithUrls = data.map(file => ({
        ...file,
        publicUrl: this.getImageUrl(folder ? `${folder}/${file.name}` : file.name)
      }))

      return {
        success: true,
        data: imagesWithUrls
      }
    } catch (error) {
      console.error('List images error:', error)
      return {
        success: false,
        error: error.message
      }
    }
  }

  /**
   * Validate file type
   * @param {string} mimeType - File MIME type
   * @returns {boolean} Is valid
   */
  isValidFileType (mimeType) {
    return this.allowedMimeTypes.includes(mimeType.toLowerCase())
  }

  /**
   * Get content type from file extension
   * @param {string} extension - File extension
   * @returns {string} Content type
   */
  getContentType (extension) {
    const contentTypes = {
      '.jpg': 'image/jpeg',
      '.jpeg': 'image/jpeg',
      '.png': 'image/png',
      '.webp': 'image/webp',
      '.gif': 'image/gif'
    }

    return contentTypes[extension.toLowerCase()] || 'image/jpeg'
  }

  /**
   * Create signed URL for private access
   * @param {string} filePath - File path in bucket
   * @param {number} expiresIn - Expiration time in seconds
   * @returns {Promise<Object>} Signed URL result
   */
  async createSignedUrl (filePath, expiresIn = 3600) {
    try {
      const { data, error } = await supabase.storage
        .from(this.bucketName)
        .createSignedUrl(filePath, expiresIn)

      if (error) {
        throw new Error(`Signed URL creation failed: ${error.message}`)
      }

      return {
        success: true,
        data: {
          signedUrl: data.signedUrl,
          expiresAt: new Date(Date.now() + expiresIn * 1000)
        }
      }
    } catch (error) {
      console.error('Signed URL creation error:', error)
      return {
        success: false,
        error: error.message
      }
    }
  }
}

module.exports = new ImageService()
