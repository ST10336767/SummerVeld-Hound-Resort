const imageService = require('../services/imageService');
const { sendSuccess, sendError } = require('../utils/response');
const path = require('path');

// @desc    Upload single image
// @route   POST /api/images/upload
// @access  Private
const uploadSingleImage = async (req, res) => {
  try {
    const { folder = '', width, height, quality, format } = req.body;
    const file = req.file;

    if (!file) {
      return sendError(res, 'No file uploaded', 400);
    }

    const options = {
      width: width ? parseInt(width) : undefined,
      height: height ? parseInt(height) : undefined,
      quality: quality ? parseInt(quality) : undefined,
      format: format || 'jpeg'
    };

    // Generate unique filename if no folder specified (store in bucket root)
    let fileName = file.originalname;
    if (!folder) {
      const fileExtension = path.extname(file.originalname);
      const baseName = path.basename(file.originalname, fileExtension);
      fileName = `${baseName}_${Date.now()}${fileExtension}`;
    }

    const result = await imageService.uploadImage(
      file.buffer,
      fileName,
      folder,
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
    const { folder = '', width, height, quality, format } = req.body;
    const files = req.files;

    if (!files || files.length === 0) {
      return sendError(res, 'No files uploaded', 400);
    }

    const options = {
      width: width ? parseInt(width) : undefined,
      height: height ? parseInt(height) : undefined,
      quality: quality ? parseInt(quality) : undefined,
      format: format || 'jpeg'
    };

    const uploadPromises = files.map(file => 
      imageService.uploadImage(file.buffer, file.originalname, folder, options)
    );

    const results = await Promise.all(uploadPromises);
    
    const successfulUploads = results.filter(result => result.success);
    const failedUploads = results.filter(result => !result.success);

    const response = {
      successful: successfulUploads.map(result => result.data),
      failed: failedUploads.map(result => result.error),
      total: files.length,
      successfulCount: successfulUploads.length,
      failedCount: failedUploads.length
    };

    if (successfulUploads.length === 0) {
      return sendError(res, 'All uploads failed', 400);
    }

    sendSuccess(
      res, 
      `Uploaded ${successfulUploads.length} of ${files.length} images`,
      response,
      successfulUploads.length === files.length ? 201 : 207 // 207 for partial success
    );
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

    const publicUrl = imageService.getImageUrl(decodeURIComponent(filePath));
    
    sendSuccess(res, 'Image URL retrieved successfully', {
      filePath: decodeURIComponent(filePath),
      publicUrl
    });
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
    const { filePath, expiresIn = 3600 } = req.body;
    
    if (!filePath) {
      return sendError(res, 'File path is required', 400);
    }

    const result = await imageService.createSignedUrl(filePath, expiresIn);
    
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
    const { folder = '', limit = 50, offset = 0 } = req.query;
    
    const result = await imageService.listImages(folder, {
      limit: parseInt(limit),
      offset: parseInt(offset)
    });
    
    if (!result.success) {
      return sendError(res, result.error, 400);
    }

    sendSuccess(res, 'Images listed successfully', {
      images: result.data,
      folder,
      count: result.data.length,
      limit: parseInt(limit),
      offset: parseInt(offset)
    });
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

    const result = await imageService.deleteImage(decodeURIComponent(filePath));
    
    if (!result.success) {
      return sendError(res, result.error, 400);
    }

    sendSuccess(res, 'Image deleted successfully');
  } catch (error) {
    console.error('Delete image error:', error);
    sendError(res, 'Failed to delete image', 500, error.message);
  }
};

// @desc    Upload pet profile image
// @route   POST /api/images/pet-profile
// @access  Private
const uploadPetProfileImage = async (req, res) => {
  try {
    const { petId } = req.body;
    const file = req.file;

    if (!file) {
      return sendError(res, 'No file uploaded', 400);
    }

    if (!petId) {
      return sendError(res, 'Pet ID is required', 400);
    }

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

    const result = await imageService.uploadImage(
      file.buffer,
      fileName,
      '', // Empty folder - store directly in bucket root
      options
    );

    if (!result.success) {
      return sendError(res, result.error, 400);
    }

    sendSuccess(res, 'Pet profile image uploaded successfully', result.data, 201);
  } catch (error) {
    console.error('Upload pet profile image error:', error);
    sendError(res, 'Failed to upload pet profile image', 500, error.message);
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
