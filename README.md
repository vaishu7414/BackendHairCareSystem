# Hair Care E-commerce Backend

Spring Boot backend for a hair care e-commerce system with JWT authentication, role-based authorization, product recommendations, cart management, order placement, and mock payments.

## Tech Stack

- Java 17
- Spring Boot 3
- Spring Security
- Spring Data JPA
- JWT
- H2 by default, MySQL supported via environment variables

## Default Admin Login

- Email: `admin@haircare.com`
- Password: `Admin@123`

## Main API Groups

- `POST /api/auth/register`
- `POST /api/auth/login`
- `PATCH /api/users/me/hair-type`
- `GET /api/products`
- `GET /api/products/recommendations`
- `GET /api/cart`
- `POST /api/cart/items`
- `PUT /api/cart/items/{cartItemId}`
- `DELETE /api/cart/items/{cartItemId}`
- `POST /api/orders/place`
- `GET /api/orders/my`
- `POST /api/payments/order/{orderId}`
- `POST /api/products` for admin
- `PUT /api/products/{id}` for admin
- `DELETE /api/products/{id}` for admin
- `GET /api/users` for admin
- `GET /api/orders` for admin
- `PATCH /api/orders/{orderId}/status` for admin

## Database Configuration

The project runs with H2 in-memory defaults. To switch to MySQL, provide:

- `DB_URL`
- `DB_USERNAME`
- `DB_PASSWORD`
- `DB_DRIVER=com.mysql.cj.jdbc.Driver`
- `DB_DIALECT=org.hibernate.dialect.MySQL8Dialect`

## Notes

- Product recommendations are based on the logged-in customer's selected hair type.
- Payments are mocked through a request payload and default to `SUCCESS` if no status is supplied.
- All non-auth endpoints require a valid JWT bearer token.
