const express = require('express');
const { body } = require('express-validator');
const { auth } = require('../middleware/auth');
const {
  getBookings,
  getBooking,
  createBooking,
  updateBooking,
  deleteBooking
} = require('../controllers/bookingController');

const router = express.Router();

// Validation rules for booking creation/update
const bookingValidation = [
  body('pet.name')
    .trim()
    .isLength({ min: 1, max: 50 })
    .withMessage('Pet name is required and must be less than 50 characters'),
  body('pet.species')
    .isIn(['dog', 'cat', 'bird', 'other'])
    .withMessage('Pet species must be one of: dog, cat, bird, other'),
  body('service.type')
    .isIn(['boarding', 'grooming', 'training', 'daycare', 'vet-visit'])
    .withMessage('Service type must be one of: boarding, grooming, training, daycare, vet-visit'),
  body('service.name')
    .trim()
    .isLength({ min: 1, max: 100 })
    .withMessage('Service name is required and must be less than 100 characters'),
  body('service.price')
    .isNumeric()
    .isFloat({ min: 0 })
    .withMessage('Service price must be a positive number'),
  body('schedule.checkIn')
    .isISO8601()
    .withMessage('Check-in date must be a valid date'),
  body('schedule.checkOut')
    .isISO8601()
    .withMessage('Check-out date must be a valid date')
    .custom((value, { req }) => {
      if (new Date(value) <= new Date(req.body.schedule.checkIn)) {
        throw new Error('Check-out date must be after check-in date');
      }
      return true;
    })
];

// @route   GET /api/bookings
// @desc    Get all bookings
// @access  Private
router.get('/', auth, getBookings);

// @route   GET /api/bookings/:id
// @desc    Get single booking
// @access  Private
router.get('/:id', auth, getBooking);

// @route   POST /api/bookings
// @desc    Create new booking
// @access  Private
router.post('/', auth, bookingValidation, createBooking);

// @route   PUT /api/bookings/:id
// @desc    Update booking
// @access  Private
router.put('/:id', auth, bookingValidation, updateBooking);

// @route   DELETE /api/bookings/:id
// @desc    Delete booking
// @access  Private
router.delete('/:id', auth, deleteBooking);

module.exports = router;
