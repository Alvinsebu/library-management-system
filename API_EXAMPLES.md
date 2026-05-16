# Library Management System - API Examples

## Complete Workflow Example

This document provides a complete workflow for using the Library Management System APIs.

## 1. User Registration

### Request
```bash
curl -X POST http://localhost:8080/api/auth/signup \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Alice Johnson",
    "email": "alice@example.com",
    "password": "SecurePassword123!"
  }'
```

### Response
```json
{
  "success": true,
  "message": "User registered successfully",
  "data": {
    "id": "507f1f77bcf86cd799439011",
    "name": "Alice Johnson",
    "email": "alice@example.com",
    "role": "USER",
    "token": "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJhbGljZUBleGFtcGxlLmNvbSIsInVzZXJJZCI6IjUwN2YxZjc3YmNmODZjZDc5OTQzOTAxMSIsImlhdCI6MTcxNTg5NjAwMCwiZXhwIjoxNzE1OTgyNDAwfQ.xxx",
    "message": "User registered successfully"
  },
  "timestamp": 1715896000000
}
```

**Note:** Save the `token` for subsequent requests.

## 2. User Login

### Request
```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "alice@example.com",
    "password": "SecurePassword123!"
  }'
```

### Response
```json
{
  "success": true,
  "message": "Login successful",
  "data": {
    "id": "507f1f77bcf86cd799439011",
    "name": "Alice Johnson",
    "email": "alice@example.com",
    "role": "USER",
    "token": "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJhbGljZUBleGFtcGxlLmNvbSIsInVzZXJJZCI6IjUwN2YxZjc3YmNmODZjZDc5OTQzOTAxMSIsImlhdCI6MTcxNTg5NjAwMCwiZXhwIjoxNzE1OTgyNDAwfQ.xxx"
  },
  "timestamp": 1715896000000
}
```

## 3. Admin Registration

### Request
```bash
curl -X POST http://localhost:8080/api/auth/signup \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Bob Admin",
    "email": "admin@example.com",
    "password": "AdminPassword123!"
  }'
```

**Note:** First user can be promoted to ADMIN manually in MongoDB:
```bash
db.users.updateOne(
  { email: "admin@example.com" },
  { $set: { role: "ADMIN" } }
)
```

## 4. Admin: Create Books

### Request
```bash
curl -X POST http://localhost:8080/api/admin/books \
  -H "Authorization: Bearer <ADMIN_TOKEN>" \
  -H "Content-Type: application/json" \
  -d '{
    "title": "The Great Gatsby",
    "author": "F. Scott Fitzgerald",
    "policy": "NORMAL"
  }'
```

### Response
```json
{
  "success": true,
  "message": "Book added successfully",
  "data": {
    "id": "507f1f77bcf86cd799439012",
    "title": "The Great Gatsby",
    "author": "F. Scott Fitzgerald",
    "available": true,
    "borrowedBy": null,
    "borrowedAt": null,
    "expiryAt": null,
    "policy": "NORMAL"
  },
  "timestamp": 1715896000000
}
```

### Add More Books

```bash
# Add EXPIRY book
curl -X POST http://localhost:8080/api/admin/books \
  -H "Authorization: Bearer <ADMIN_TOKEN>" \
  -H "Content-Type: application/json" \
  -d '{
    "title": "To Kill a Mockingbird",
    "author": "Harper Lee",
    "policy": "EXPIRY"
  }'

# Add END_OF_DAY book
curl -X POST http://localhost:8080/api/admin/books \
  -H "Authorization: Bearer <ADMIN_TOKEN>" \
  -H "Content-Type: application/json" \
  -d '{
    "title": "1984",
    "author": "George Orwell",
    "policy": "END_OF_DAY"
  }'
```

## 5. User: View Available Books

### Request
```bash
curl -X GET http://localhost:8080/api/books \
  -H "Authorization: Bearer <USER_TOKEN>"
```

### Response
```json
{
  "success": true,
  "message": "Available books retrieved",
  "data": [
    {
      "id": "507f1f77bcf86cd799439012",
      "title": "The Great Gatsby",
      "author": "F. Scott Fitzgerald",
      "available": true,
      "borrowedBy": null,
      "borrowedAt": null,
      "expiryAt": null,
      "policy": "NORMAL"
    },
    {
      "id": "507f1f77bcf86cd799439013",
      "title": "To Kill a Mockingbird",
      "author": "Harper Lee",
      "available": true,
      "borrowedBy": null,
      "borrowedAt": null,
      "expiryAt": null,
      "policy": "EXPIRY"
    }
  ],
  "timestamp": 1715896000000
}
```

## 6. User: Borrow a Book

### Request (NORMAL Policy)
```bash
curl -X POST http://localhost:8080/api/books/507f1f77bcf86cd799439012/borrow \
  -H "Authorization: Bearer <USER_TOKEN>" \
  -H "Content-Type: application/json" \
  -d '{}' # No expiry for NORMAL policy
```

### Request (EXPIRY Policy - 7 days = 10080 minutes)
```bash
curl -X POST http://localhost:8080/api/books/507f1f77bcf86cd799439013/borrow \
  -H "Authorization: Bearer <USER_TOKEN>" \
  -H "Content-Type: application/json" \
  -d '{
    "expiryMinutes": 10080
  }'
```

### Response
```json
{
  "success": true,
  "message": "Book borrowed successfully",
  "data": {
    "id": "507f1f77bcf86cd799439012",
    "title": "The Great Gatsby",
    "borrowedBy": "507f1f77bcf86cd799439011",
    "borrowedAt": "2024-05-16T14:30:00",
    "expiryAt": null,
    "message": "Book borrowed successfully"
  },
  "timestamp": 1715896000000
}
```

## 7. User: View Borrowed Books

### Request
```bash
curl -X GET http://localhost:8080/api/books/user/borrowed \
  -H "Authorization: Bearer <USER_TOKEN>"
```

### Response
```json
{
  "success": true,
  "message": "Borrowed books retrieved",
  "data": {
    "userId": "507f1f77bcf86cd799439011",
    "books": [
      {
        "id": "507f1f77bcf86cd799439012",
        "title": "The Great Gatsby",
        "author": "F. Scott Fitzgerald",
        "available": false,
        "borrowedBy": "507f1f77bcf86cd799439011",
        "borrowedAt": "2024-05-16T14:30:00",
        "expiryAt": null,
        "policy": "NORMAL"
      },
      {
        "id": "507f1f77bcf86cd799439013",
        "title": "To Kill a Mockingbird",
        "author": "Harper Lee",
        "available": false,
        "borrowedBy": "507f1f77bcf86cd799439011",
        "borrowedAt": "2024-05-16T14:30:00",
        "expiryAt": "2024-05-23T14:30:00",
        "policy": "EXPIRY"
      }
    ],
    "count": 2
  },
  "timestamp": 1715896000000
}
```

## 8. User: Return a Book

### Request
```bash
curl -X POST http://localhost:8080/api/books/507f1f77bcf86cd799439012/return \
  -H "Authorization: Bearer <USER_TOKEN>"
```

### Response
```json
{
  "success": true,
  "message": "Book returned successfully",
  "data": {
    "id": "507f1f77bcf86cd799439012",
    "title": "The Great Gatsby",
    "message": "Book returned successfully"
  },
  "timestamp": 1715896000000
}
```

## 9. User: Get User Profile

### Request
```bash
curl -X GET http://localhost:8080/api/users/me \
  -H "Authorization: Bearer <USER_TOKEN>"
```

### Response
```json
{
  "success": true,
  "message": "User profile retrieved",
  "data": {
    "id": "507f1f77bcf86cd799439011",
    "name": "Alice Johnson",
    "email": "alice@example.com",
    "role": "USER",
    "active": true,
    "createdAt": "2024-05-15T10:00:00",
    "updatedAt": "2024-05-15T10:00:00"
  },
  "timestamp": 1715896000000
}
```

## 10. Admin: Get All Books

### Request
```bash
curl -X GET http://localhost:8080/api/admin/books \
  -H "Authorization: Bearer <ADMIN_TOKEN>"
```

### Response
```json
{
  "success": true,
  "message": "Books retrieved successfully",
  "data": [
    {
      "id": "507f1f77bcf86cd799439012",
      "title": "The Great Gatsby",
      "author": "F. Scott Fitzgerald",
      "available": true,
      "borrowedBy": null,
      "borrowedAt": null,
      "expiryAt": null,
      "policy": "NORMAL"
    },
    {
      "id": "507f1f77bcf86cd799439013",
      "title": "To Kill a Mockingbird",
      "author": "Harper Lee",
      "available": false,
      "borrowedBy": "507f1f77bcf86cd799439011",
      "borrowedAt": "2024-05-16T14:30:00",
      "expiryAt": "2024-05-23T14:30:00",
      "policy": "EXPIRY"
    }
  ],
  "timestamp": 1715896000000
}
```

## Error Scenarios

### Invalid Token

```bash
curl -X GET http://localhost:8080/api/books \
  -H "Authorization: Bearer invalid_token"
```

**Response (401):**
```json
{
  "success": false,
  "message": "Invalid or expired token",
  "status": 401,
  "timestamp": 1715896000000
}
```

### Book Not Available

```bash
curl -X POST http://localhost:8080/api/books/507f1f77bcf86cd799439012/borrow \
  -H "Authorization: Bearer <USER_TOKEN>" \
  -H "Content-Type: application/json" \
  -d '{}'
```

**Response (400):**
```json
{
  "success": false,
  "message": "Book is not available for borrowing",
  "status": 400,
  "timestamp": 1715896000000
}
```

### Unauthorized Action

```bash
curl -X POST http://localhost:8080/api/admin/books \
  -H "Authorization: Bearer <USER_TOKEN>" \
  -H "Content-Type: application/json" \
  -d '{
    "title": "Test",
    "author": "Author",
    "policy": "NORMAL"
  }'
```

**Response (403):**
```json
{
  "success": false,
  "message": "Access denied",
  "status": 403,
  "timestamp": 1715896000000
}
```

### Resource Not Found

```bash
curl -X GET http://localhost:8080/api/books/invalid_id \
  -H "Authorization: Bearer <USER_TOKEN>"
```

**Response (404):**
```json
{
  "success": false,
  "message": "Book not found - ID: invalid_id",
  "status": 404,
  "timestamp": 1715896000000
}
```

## Testing with Postman

1. Create a new Postman Collection
2. Add all endpoints from the examples above
3. Set environment variables:
   - `base_url` = `http://localhost:8080/api`
   - `user_token` = (from signup response)
   - `admin_token` = (from admin signup response)
4. Use `{{base_url}}`, `{{user_token}}`, `{{admin_token}}` in requests

## Performance Testing

### Load Testing with Apache JMeter

1. Create thread group with 100 threads
2. Add HTTP Request sampler
3. Target: `POST /books/{id}/borrow`
4. Headers: `Authorization: Bearer <TOKEN>`
5. Run and analyze results

### Response Time Targets

- Sign up: < 500ms
- Login: < 300ms
- Borrow book: < 200ms
- Return book: < 200ms
- List books: < 100ms

## API Response Codes

| Code | Meaning |
|------|---------|
| 200 | OK - Request successful |
| 201 | Created - Resource created |
| 400 | Bad Request - Invalid input |
| 401 | Unauthorized - Invalid token |
| 403 | Forbidden - Insufficient permissions |
| 404 | Not Found - Resource not found |
| 409 | Conflict - Resource already exists |
| 500 | Internal Server Error |

