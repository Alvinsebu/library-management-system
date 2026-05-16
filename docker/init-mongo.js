db = db.getSiblingDB('library_db');

db.createCollection('users');
db.createCollection('books');

// Create indexes
db.users.createIndex({ email: 1 }, { unique: true });
db.books.createIndex({ available: 1 });
db.books.createIndex({ borrowedBy: 1 });

// Insert sample data
db.users.insertMany([
  {
    _id: ObjectId(),
    name: "Admin User",
    email: "admin@library.com",
    password: "$2a$10$1234567890abcdefghijklmnopqrstuvwxyz", // BCrypted password
    role: "ADMIN",
    active: true,
    createdAt: new Date(),
    updatedAt: new Date()
  },
  {
    _id: ObjectId(),
    name: "John Doe",
    email: "john@library.com",
    password: "$2a$10$1234567890abcdefghijklmnopqrstuvwxyz", // BCrypted password
    role: "USER",
    active: true,
    createdAt: new Date(),
    updatedAt: new Date()
  }
]);

db.books.insertMany([
  {
    _id: ObjectId(),
    title: "The Great Gatsby",
    author: "F. Scott Fitzgerald",
    available: true,
    borrowedBy: null,
    borrowedAt: null,
    expiryAt: null,
    policy: "NORMAL",
    createdAt: new Date(),
    updatedAt: new Date()
  },
  {
    _id: ObjectId(),
    title: "To Kill a Mockingbird",
    author: "Harper Lee",
    available: true,
    borrowedBy: null,
    borrowedAt: null,
    expiryAt: null,
    policy: "EXPIRY",
    createdAt: new Date(),
    updatedAt: new Date()
  },
  {
    _id: ObjectId(),
    title: "1984",
    author: "George Orwell",
    available: true,
    borrowedBy: null,
    borrowedAt: null,
    expiryAt: null,
    policy: "END_OF_DAY",
    createdAt: new Date(),
    updatedAt: new Date()
  }
]);

print("MongoDB initialization completed successfully!");
