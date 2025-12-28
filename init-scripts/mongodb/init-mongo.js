// Initialize MongoDB database for Smart Clinic
db = db.getSiblingDB('prescriptions');

// Create collections with validation
db.createCollection('prescriptions', {
  validator: {
    $jsonSchema: {
      bsonType: 'object',
      required: ['appointmentId', 'patientId', 'doctorId', 'prescriptionDate'],
      properties: {
        appointmentId: {
          bsonType: 'long',
          description: 'Appointment ID must be a long and is required'
        },
        patientId: {
          bsonType: 'long',
          description: 'Patient ID must be a long and is required'
        },
        doctorId: {
          bsonType: 'long',
          description: 'Doctor ID must be a long and is required'
        },
        patientName: {
          bsonType: 'string',
          description: 'Patient name must be a string'
        },
        doctorName: {
          bsonType: 'string',
          description: 'Doctor name must be a string'
        },
        prescriptionDate: {
          bsonType: 'date',
          description: 'Prescription date must be a date and is required'
        },
        expiryDate: {
          bsonType: 'date',
          description: 'Expiry date must be a date'
        },
        status: {
          bsonType: 'string',
          enum: ['active', 'expired', 'cancelled'],
          description: 'Status must be one of: active, expired, cancelled'
        },
        medications: {
          bsonType: 'array',
          description: 'Medications must be an array',
          items: {
            bsonType: 'object',
            properties: {
              name: { bsonType: 'string' },
              genericName: { bsonType: 'string' },
              dosage: { bsonType: 'string' },
              form: { bsonType: 'string' },
              frequency: { bsonType: 'string' },
              duration: { bsonType: 'string' },
              quantity: { bsonType: 'int' },
              instructions: { bsonType: 'string' }
            }
          }
        },
        diagnosis: {
          bsonType: 'string',
          description: 'Diagnosis must be a string'
        },
        doctorNotes: {
          bsonType: 'string',
          description: 'Doctor notes must be a string'
        }
      }
    }
  }
});

// Create indexes for better query performance
db.prescriptions.createIndex({ appointmentId: 1 });
db.prescriptions.createIndex({ patientId: 1 });
db.prescriptions.createIndex({ doctorId: 1 });
db.prescriptions.createIndex({ status: 1 });
db.prescriptions.createIndex({ prescriptionDate: -1 });

print('MongoDB initialization completed for Smart Clinic');
