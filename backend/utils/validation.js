/**
 * Custom validation helper functions
 */

const isValidObjectId = (id) => {
  return /^[0-9a-fA-F]{24}$/.test(id)
}

const isValidEmail = (email) => {
  const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/
  return emailRegex.test(email)
}

const isValidPhone = (phone) => {
  const phoneRegex = /^\+?[\d\s-()]+$/
  return phoneRegex.test(phone)
}

const isValidDate = (date) => {
  return date instanceof Date && !isNaN(date)
}

const isFutureDate = (date) => {
  return new Date(date) > new Date()
}

const sanitizeInput = (input) => {
  if (typeof input === 'string') {
    return input.trim().replace(/[<>]/g, '')
  }
  return input
}

const validatePagination = (page, limit) => {
  const pageNum = parseInt(page) || 1
  const limitNum = parseInt(limit) || 10

  return {
    page: Math.max(1, pageNum),
    limit: Math.min(100, Math.max(1, limitNum))
  }
}

module.exports = {
  isValidObjectId,
  isValidEmail,
  isValidPhone,
  isValidDate,
  isFutureDate,
  sanitizeInput,
  validatePagination
}
