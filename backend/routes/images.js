const express = require('express')
const { body, query, param } = require('express-validator')
const { auth } = require('../middleware/auth')
const { uploadSingle, uploadMultiple, validateSingleUpload, validateMultipleUpload } = require('../middleware/upload')
const {
  uploadSingleImage,
  uploadMultipleImages,
  getImageUrl,
  createSignedUrl,
  listImages,
  deleteImage,
  uploadPetProfileImage
} = require('../controllers/imageController')

const router = express.Router()

// Validation rules
const uploadValidation = [
  body('folder')
    .optional()
    .isString()
    .isLength({ max: 100 })
    .withMessage('Folder name must be less than 100 characters'),
  body('width')
    .optional()
    .isInt({ min: 50, max: 4000 })
    .withMessage('Width must be between 50 and 4000 pixels'),
  body('height')
    .optional()
    .isInt({ min: 50, max: 4000 })
    .withMessage('Height must be between 50 and 4000 pixels'),
  body('quality')
    .optional()
    .isInt({ min: 1, max: 100 })
    .withMessage('Quality must be between 1 and 100'),
  body('format')
    .optional()
    .isIn(['jpeg', 'jpg', 'png', 'webp'])
    .withMessage('Format must be one of: jpeg, jpg, png, webp')
]

const petProfileValidation = [
  body('petId')
    .notEmpty()
    .withMessage('Pet ID is required')
    .isMongoId()
    .withMessage('Invalid Pet ID format')
]

const signedUrlValidation = [
  body('filePath')
    .notEmpty()
    .withMessage('File path is required')
    .isString()
    .withMessage('File path must be a string'),
  body('expiresIn')
    .optional()
    .isInt({ min: 60, max: 86400 })
    .withMessage('Expires in must be between 60 and 86400 seconds')
]

const listValidation = [
  query('folder')
    .optional()
    .isString()
    .isLength({ max: 100 })
    .withMessage('Folder name must be less than 100 characters'),
  query('limit')
    .optional()
    .isInt({ min: 1, max: 100 })
    .withMessage('Limit must be between 1 and 100'),
  query('offset')
    .optional()
    .isInt({ min: 0 })
    .withMessage('Offset must be a non-negative integer')
]

const filePathValidation = [
  param('filePath')
    .notEmpty()
    .withMessage('File path is required')
    .isString()
    .withMessage('File path must be a string')
]

// @route   POST /api/images/upload
// @desc    Upload single image
// @access  Private
router.post('/upload',
  auth,
  uploadSingle('image'),
  validateSingleUpload,
  uploadValidation,
  uploadSingleImage
)

// @route   POST /api/images/upload-multiple
// @desc    Upload multiple images
// @access  Private
router.post('/upload-multiple',
  auth,
  uploadMultiple('images', 5),
  validateMultipleUpload,
  uploadValidation,
  uploadMultipleImages
)

// @route   POST /api/images/pet-profile
// @desc    Upload pet profile image
// @access  Private
router.post('/pet-profile',
  auth,
  uploadSingle('image'),
  validateSingleUpload,
  petProfileValidation,
  uploadPetProfileImage
)

// @route   GET /api/images/url/:filePath
// @desc    Get public URL for image
// @access  Private
router.get('/url/:filePath',
  auth,
  filePathValidation,
  getImageUrl
)

// @route   POST /api/images/signed-url
// @desc    Create signed URL for private access
// @access  Private
router.post('/signed-url',
  auth,
  signedUrlValidation,
  createSignedUrl
)

// @route   GET /api/images/list
// @desc    List images in folder
// @access  Private
router.get('/list',
  auth,
  listValidation,
  listImages
)

// @route   DELETE /api/images/:filePath
// @desc    Delete image
// @access  Private
router.delete('/:filePath',
  auth,
  filePathValidation,
  deleteImage
)

module.exports = router
