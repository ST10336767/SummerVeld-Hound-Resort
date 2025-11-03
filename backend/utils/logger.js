/**
 * Simple logging utility
 */

const colors = {
  reset: '\x1b[0m',
  bright: '\x1b[1m',
  dim: '\x1b[2m',
  red: '\x1b[31m',
  green: '\x1b[32m',
  yellow: '\x1b[33m',
  blue: '\x1b[34m',
  magenta: '\x1b[35m',
  cyan: '\x1b[36m',
  white: '\x1b[37m'
}

const getTimestamp = () => {
  return new Date().toISOString()
}

const formatMessage = (level, message, data = null) => {
  const timestamp = getTimestamp()
  const levelColor = colors[level] || colors.white
  const resetColor = colors.reset

  let formattedMessage = `${levelColor}[${level.toUpperCase()}]${resetColor} ${timestamp} - ${message}`

  if (data) {
    formattedMessage += `\n${JSON.stringify(data, null, 2)}`
  }

  return formattedMessage
}

const logger = {
  info: (message, data = null) => {
    console.log(formatMessage('green', message, data))
  },

  warn: (message, data = null) => {
    console.warn(formatMessage('yellow', message, data))
  },

  error: (message, data = null) => {
    console.error(formatMessage('red', message, data))
  },

  debug: (message, data = null) => {
    if (process.env.NODE_ENV === 'development') {
      console.log(formatMessage('cyan', message, data))
    }
  }
}

module.exports = logger
