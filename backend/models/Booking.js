const mongoose = require('mongoose');

const bookingSchema = new mongoose.Schema({
  user: {
    type: mongoose.Schema.ObjectId,
    ref: 'User',
    required: true
  },
  pet: {
    name: {
      type: String,
      required: [true, 'Please add pet name'],
      trim: true
    },
    species: {
      type: String,
      required: [true, 'Please specify pet species'],
      enum: ['dog', 'cat', 'bird', 'other']
    },
    breed: String,
    age: Number,
    weight: Number,
    specialNeeds: String,
    medicalConditions: String,
    vaccinationStatus: {
      type: String,
      enum: ['up-to-date', 'partial', 'not-vaccinated'],
      default: 'up-to-date'
    }
  },
  service: {
    type: {
      type: String,
      required: [true, 'Please select a service type'],
      enum: ['boarding', 'grooming', 'training', 'daycare', 'vet-visit']
    },
    name: {
      type: String,
      required: [true, 'Please specify service name']
    },
    description: String,
    duration: Number, // in hours
    price: {
      type: Number,
      required: [true, 'Please specify service price']
    }
  },
  schedule: {
    checkIn: {
      type: Date,
      required: [true, 'Please specify check-in date and time']
    },
    checkOut: {
      type: Date,
      required: [true, 'Please specify check-out date and time']
    },
    estimatedDuration: Number // in hours
  },
  status: {
    type: String,
    enum: ['pending', 'confirmed', 'in-progress', 'completed', 'cancelled'],
    default: 'pending'
  },
  notes: {
    customerNotes: String,
    staffNotes: String,
    specialInstructions: String
  },
  payment: {
    amount: {
      type: Number,
      required: true
    },
    status: {
      type: String,
      enum: ['pending', 'paid', 'refunded', 'partial'],
      default: 'pending'
    },
    method: {
      type: String,
      enum: ['cash', 'card', 'bank-transfer', 'online'],
      default: 'online'
    },
    transactionId: String,
    paidAt: Date
  },
  assignedStaff: [{
    type: mongoose.Schema.ObjectId,
    ref: 'User'
  }],
  rating: {
    score: {
      type: Number,
      min: 1,
      max: 5
    },
    review: String,
    createdAt: Date
  }
}, {
  timestamps: true
});

// Calculate total amount before saving
bookingSchema.pre('save', function(next) {
  if (this.service.price && this.schedule.estimatedDuration) {
    this.payment.amount = this.service.price * this.schedule.estimatedDuration;
  }
  next();
});

// Index for better query performance
bookingSchema.index({ user: 1, createdAt: -1 });
bookingSchema.index({ 'schedule.checkIn': 1, 'schedule.checkOut': 1 });
bookingSchema.index({ status: 1 });

module.exports = mongoose.model('Booking', bookingSchema);
