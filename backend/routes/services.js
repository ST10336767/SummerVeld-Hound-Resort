const express = require('express')
const { auth, authorize } = require('../middleware/auth')

const router = express.Router()

// Sample services data - in a real app, this would come from a database
const services = [
  {
    id: 1,
    type: 'boarding',
    name: 'Overnight Boarding',
    description: 'Safe and comfortable overnight accommodation for your pet',
    duration: 24,
    price: 150,
    features: ['24/7 supervision', 'Daily exercise', 'Feeding included', 'Medication administration']
  },
  {
    id: 2,
    type: 'grooming',
    name: 'Full Grooming Service',
    description: 'Complete grooming package including bath, brush, and nail trim',
    duration: 2,
    price: 80,
    features: ['Bath and dry', 'Brushing', 'Nail trimming', 'Ear cleaning']
  },
  {
    id: 3,
    type: 'training',
    name: 'Basic Obedience Training',
    description: 'One-on-one training sessions for basic commands',
    duration: 1,
    price: 120,
    features: ['Sit, stay, come commands', 'Leash training', 'Behavior modification']
  },
  {
    id: 4,
    type: 'daycare',
    name: 'Day Care Service',
    description: 'Supervised play and care during the day',
    duration: 8,
    price: 60,
    features: ['Socialization', 'Playtime', 'Feeding', 'Exercise']
  },
  {
    id: 5,
    type: 'vet-visit',
    name: 'Veterinary Consultation',
    description: 'Professional veterinary care and consultation',
    duration: 1,
    price: 200,
    features: ['Health checkup', 'Vaccinations', 'Medical advice', 'Prescription medication']
  }
]

// @route   GET /api/services
// @desc    Get all available services
// @access  Public
router.get('/', (req, res) => {
  try {
    const { type } = req.query

    let filteredServices = services
    if (type) {
      filteredServices = services.filter(service => service.type === type)
    }

    res.json({
      success: true,
      count: filteredServices.length,
      data: filteredServices
    })
  } catch (error) {
    res.status(500).json({
      success: false,
      message: 'Server error',
      error: error.message
    })
  }
})

// @route   GET /api/services/:id
// @desc    Get single service
// @access  Public
router.get('/:id', (req, res) => {
  try {
    const service = services.find(s => s.id === parseInt(req.params.id))

    if (!service) {
      return res.status(404).json({
        success: false,
        message: 'Service not found'
      })
    }

    res.json({
      success: true,
      data: service
    })
  } catch (error) {
    res.status(500).json({
      success: false,
      message: 'Server error',
      error: error.message
    })
  }
})

// @route   POST /api/services
// @desc    Create new service (admin only)
// @access  Private/Admin
router.post('/', auth, authorize('admin'), [
  require('express-validator').body('name')
    .trim()
    .isLength({ min: 1, max: 100 })
    .withMessage('Service name is required and must be less than 100 characters'),
  require('express-validator').body('type')
    .isIn(['boarding', 'grooming', 'training', 'daycare', 'vet-visit'])
    .withMessage('Invalid service type'),
  require('express-validator').body('price')
    .isNumeric()
    .isFloat({ min: 0 })
    .withMessage('Price must be a positive number')
], (req, res) => {
  try {
    const { validationResult } = require('express-validator')
    const errors = validationResult(req)
    if (!errors.isEmpty()) {
      return res.status(400).json({
        success: false,
        message: 'Validation errors',
        errors: errors.array()
      })
    }

    const newService = {
      id: services.length + 1,
      ...req.body,
      createdAt: new Date()
    }

    services.push(newService)

    res.status(201).json({
      success: true,
      message: 'Service created successfully',
      data: newService
    })
  } catch (error) {
    res.status(500).json({
      success: false,
      message: 'Server error',
      error: error.message
    })
  }
})

module.exports = router
