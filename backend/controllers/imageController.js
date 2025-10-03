const path = require('path');
const imageService = require('../services/imageService');
const { sendSuccess, sendError } = require('../utils/response');

// @desc    Upload single image
// @route   POST /api/images/upload
// @access  Private
const uploadSingleImage = async (req, res) => {
  try {
    const { folder, width, height, quality, format } = req.body;
    const file = req.file;

    if (!file) {
      return sendError(res, 'No file uploaded', 400);
    }

    const options = {
      width: width ? parseInt(width) : 1200,
      height: height ? parseInt(height) : 1200,
      quality: quality ? parseInt(quality) : 85,
      format: format || 'jpeg'
    };

    const result = await imageService.uploadImage(
      file.buffer,
      file.originalname,
      folder || 'general',
      options
    );

    if (!result.success) {
      return sendError(res, result.error, 400);
    }

    sendSuccess(res, 'Image uploaded successfully', result.data, 201);
  } catch (error) {
    console.error('Upload single image error:', error);
    sendError(res, 'Failed to upload image', 500, error.message);
  }
};

// @desc    Upload multiple images
// @route   POST /api/images/upload-multiple
// @access  Private
const uploadMultipleImages = async (req, res) => {
  try {
    const { folder, width, height, quality, format } = req.body;
    const files = req.files;

    if (!files || files.length === 0) {
      return sendError(res, 'No files uploaded', 400);
    }

    const options = {
      width: width ? parseInt(width) : 1200,
      height: height ? parseInt(height) : 1200,
      quality: quality ? parseInt(quality) : 85,
      format: format || 'jpeg'
    };

    const uploadPromises = files.map(file =>
      imageService.uploadImage(
        file.buffer,
        file.originalname,
        folder || 'general',
        options
      )
    );

    const results = await Promise.all(uploadPromises);
    
    // Check if any uploads failed
    const failedUploads = results.filter(result => !result.success);
    if (failedUploads.length > 0) {
      return sendError(res, `Failed to upload ${failedUploads.length} images`, 400);
    }

    const uploadedImages = results.map(result => result.data);
    sendSuccess(res, 'Images uploaded successfully', uploadedImages, 201);
  } catch (error) {
    console.error('Upload multiple images error:', error);
    sendError(res, 'Failed to upload images', 500, error.message);
  }
};

// @desc    Get image URL
// @route   GET /api/images/url/:filePath
// @access  Private
const getImageUrl = async (req, res) => {
  try {
    const { filePath } = req.params;

    if (!filePath) {
      return sendError(res, 'File path is required', 400);
    }

    const result = await imageService.getImageUrl(filePath);

    if (!result.success) {
      return sendError(res, result.error, 400);
    }

    sendSuccess(res, 'Image URL retrieved successfully', result.data);
  } catch (error) {
    console.error('Get image URL error:', error);
    sendError(res, 'Failed to get image URL', 500, error.message);
  }
};

// @desc    Create signed URL for private access
// @route   POST /api/images/signed-url
// @access  Private
const createSignedUrl = async (req, res) => {
  try {
    const { filePath, expiresIn } = req.body;

    if (!filePath) {
      return sendError(res, 'File path is required', 400);
    }

    const result = await imageService.createSignedUrl(filePath, expiresIn || 3600);

    if (!result.success) {
      return sendError(res, result.error, 400);
    }

    sendSuccess(res, 'Signed URL created successfully', result.data);
  } catch (error) {
    console.error('Create signed URL error:', error);
    sendError(res, 'Failed to create signed URL', 500, error.message);
  }
};

// @desc    List images in folder
// @route   GET /api/images/list
// @access  Private
const listImages = async (req, res) => {
  try {
    const { folder = '', limit = 20, offset = 0 } = req.query;

    const result = await imageService.listImages(folder, parseInt(limit), parseInt(offset));

    if (!result.success) {
      return sendError(res, result.error, 400);
    }

    sendSuccess(res, 'Images listed successfully', result.data);
  } catch (error) {
    console.error('List images error:', error);
    sendError(res, 'Failed to list images', 500, error.message);
  }
};

// @desc    Delete image
// @route   DELETE /api/images/:filePath
// @access  Private
const deleteImage = async (req, res) => {
  try {
    const { filePath } = req.params;

    if (!filePath) {
      return sendError(res, 'File path is required', 400);
    }

    const result = await imageService.deleteImage(filePath);

    if (!result.success) {
      return sendError(res, result.error, 400);
    }

    sendSuccess(res, 'Image deleted successfully', { deleted: true });
  } catch (error) {
    console.error('Delete image error:', error);

    sendError(res, 'Failed to delete image', 500, error.message);
  }
};

// @desc    Upload pet profile image
// @route   POST /api/images/pet-profile
// @access  Private
const uploadPetProfileImage = async (req, res) => {
  const startTime = Date.now();
  console.log('📸 Starting pet profile image upload...');
  
  try {
    const { petId } = req.body;
    const file = req.file;

    if (!file) {
      console.log('❌ No file uploaded');
      return sendError(res, 'No file uploaded', 400);
    }

    if (!petId) {
      console.log('❌ Pet ID is required');
      return sendError(res, 'Pet ID is required', 400);
    }

    console.log(`📊 Upload details - Pet ID: ${petId}, File size: ${file.size} bytes, Original name: ${file.originalname}`);

    // Store directly in dog_images bucket without subfolders
    const options = {
      width: 400,
      height: 400,
      quality: 90,
      format: 'jpeg'
    };

    // Generate unique filename with petId prefix
    const fileExtension = path.extname(file.originalname);
    const fileName = `pet_${petId}_${Date.now()}${fileExtension}`;

    console.log(`🔄 Processing image: ${fileName}`);
    const processingStartTime = Date.now();

    const result = await imageService.uploadImage(
      file.buffer,
      fileName,
      '', // Empty folder - store directly in bucket root
      options
    );

    const processingTime = Date.now() - processingStartTime;
    console.log(`⏱️  Image processing took: ${processingTime}ms`);

    if (!result.success) {
      console.error('❌ Image upload failed:', result.error);
      return sendError(res, result.error, 400);
    }

    const totalTime = Date.now() - startTime;
    console.log(`✅ Pet profile image uploaded successfully in ${totalTime}ms: ${result.data.publicUrl}`);
    
    sendSuccess(res, 'Pet profile image uploaded successfully', result.data, 201);
  } catch (error) {
    const totalTime = Date.now() - startTime;
    console.error(`❌ Upload pet profile image error after ${totalTime}ms:`, error);
    
    // Provide more specific error messages
    let errorMessage = 'Failed to upload pet profile image';
    let statusCode = 500;
    
    if (error.message.includes('timeout')) {
      errorMessage = 'Image upload timed out. Please try again with a smaller image.';
      statusCode = 408;
    } else if (error.message.includes('size')) {
      errorMessage = 'Image file is too large. Please use an image smaller than 5MB.';
      statusCode = 413;
    } else if (error.message.includes('format')) {
      errorMessage = 'Invalid image format. Please use JPEG, PNG, WebP, or GIF.';
      statusCode = 400;
    }
    
    sendError(res, errorMessage, statusCode, error.message);
  }
};


module.exports = {
  uploadSingleImage,
  uploadMultipleImages,
  getImageUrl,
  createSignedUrl,
  listImages,
  deleteImage,
  uploadPetProfileImage
};
