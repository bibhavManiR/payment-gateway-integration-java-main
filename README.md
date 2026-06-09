# Payment Gateway Integration (Stripe + Razorpay) — Java Spring Boot

A production-style backend payment service built with **Java + Spring Boot + PostgreSQL** that integrates:

✅ **Stripe Checkout** (International Payments)
✅ **Razorpay Orders + Signature Verification** (India Payments)
✅ Secure **Webhook Handling (Stripe + Razorpay)**
✅ Payment lifecycle tracking in DB
✅ Webhook audit logs + idempotency (safe for duplicate events)

This project is designed as a **portfolio-quality payment service** for **Upwork clients** and **backend engineering interviews**.

---

## 🚀 Features

### Payments
- Create internal Order
- Stripe: Create Checkout Session
- Stripe: Webhook verification + DB updates
- Razorpay: Create Razorpay Order
- Razorpay: Verify payment signature (server-side)
- Razorpay: Webhook verification + audit logs

### Engineering / Production
- PostgreSQL persistence using Spring Data JPA (Hibernate)
- Tables: `orders`, `payments`, `webhook_events`
- Payment lifecycle: `CREATED → PENDING → PAID/FAILED`
- Webhook payload storage for audit/debugging
- Webhook idempotency using `(provider + event_id)` uniqueness

---

## 🧠 Workflow Overview

### 1) Orders
1. Client creates an order (stored in DB)
2. Default order status:
    - `CREATED`

### 2) Stripe Flow
1. Backend creates Stripe Checkout Session
2. User pays on Stripe hosted page
3. Stripe sends webhook event to backend
4. Backend verifies signature and updates:
    - `payments.status = PAID`
    - `orders.status = PAID`

### 3) Razorpay Flow
1. Backend creates Razorpay order (`order_...`)
2. Frontend opens Razorpay checkout using Razorpay orderId
3. Razorpay returns payment details
4. Backend verifies signature and updates:
    - `payments.status = PAID`
    - `orders.status = PAID`

---

## 🛠️ Tech Stack
- Java 17
- Spring Boot 3.2.3
- Spring Web
- Spring Data JPA (Hibernate)
- PostgreSQL
- Stripe Java SDK
- Razorpay Java SDK
- Gradle

---

## 🗃️ Database Tables

### `orders`
- `id`
- `amount`
- `currency`
- `status` (`CREATED`, `PAID`, `PAYMENT_FAILED`)
- `created_at`

### `payments`
- `id`
- `order_id`
- `provider` (`STRIPE`, `RAZORPAY`)
- `status` (`CREATED`, `PENDING`, `PAID`, `FAILED`)
- `provider_session_id` (Stripe session: `cs_test_...`)
- `provider_order_id` (Razorpay order: `order_...`)
- `provider_payment_id` (Stripe `pi_...` / Razorpay `pay_...`)
- `created_at`

### `webhook_events`
- `id`
- `provider`
- `event_id`
- `payload`
- `received_at`

---

# ✅ Setup (Local)

## Prerequisites
- Java 17+
- Gradle
- PostgreSQL installed + running
- Stripe CLI (for webhook forwarding)
- Ngrok (recommended for Razorpay webhooks)

---

## 1) Clone Project
```bash
git clone https://github.com/your-username/payment-gateway-integration-java-main.git
cd payment-gateway-integration-java-main/payment-gateway-integration-java-main/demo
```

---

## 2) PostgreSQL Setup

### 2.1 Check PostgreSQL roles/users
```bash
psql -d postgres
```

Inside psql:
```sql
\du
```

> On many Mac/Homebrew setups, the role is your system username.
> Use that same username in application.properties.

Exit:
```sql
\q
```

---

### 2.2 Create database
```bash
psql -d postgres
```

Inside psql:
```sql
CREATE DATABASE payment_db;
\q
```

---

### 2.3 Verify DB created
```bash
psql -d payment_db
```

Inside psql:
```sql
SELECT current_database();
\q
```

---

## 3) Configure `application.properties`

File path:
```text
demo/src/main/resources/application.properties
```

Example configuration:
```properties
# PostgreSQL
spring.datasource.url=jdbc:postgresql://localhost:5432/payment_db
spring.datasource.username=<your_pg_username>
spring.datasource.password=<your_pg_password>
spring.datasource.driver-class-name=org.postgresql.Driver
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true

# Stripe
stripe.api.key=sk_test_your_stripe_secret_key_here
stripe.webhook.secret=whsec_your_stripe_webhook_secret_here

# Razorpay
razorpay.api.key=rzp_test_your_razorpay_key_here
razorpay.api.secret=your_razorpay_secret_here
razorpay.webhook.secret=your_razorpay_webhook_secret_here
```

---

## 4) Run the backend
Using IntelliJ IDEA:
1.  Open the project.
2.  Wait for Gradle to sync.
3.  Open `src/main/java/com/gateway/payment/PaymentApplication.java`.
4.  Click the green play button to run the application.

Using Command Line:
```bash
./gradlew bootRun
```

Backend runs on:
```text
http://localhost:8080
```

---

## 5) Confirm tables are created
Once backend starts successfully, run:

```bash
psql -d payment_db
```

Inside psql:
```sql
\dt
```

Expected output:
- `orders`
- `payments`
- `webhook_events`

Exit:
```sql
\q
```

---

# 🔌 API Endpoints

## Orders
- `POST /api/orders` → Create Order
- `GET /api/orders/{id}` → Get Order

## Stripe
- `POST /api/payments/stripe/checkout` → Create Checkout Session
- `POST /api/webhooks/stripe` → Stripe Webhook

## Razorpay
- `POST /api/payments/razorpay/order` → Create Razorpay Order
- `POST /api/payments/razorpay/verify` → Verify signature + mark PAID
- `POST /api/webhooks/razorpay` → Razorpay Webhook

---

# 🧪 Webhook Testing

## Stripe Webhooks (Local)

Login:
```bash
stripe login
```

Forward webhooks to localhost:
```bash
stripe listen --forward-to localhost:8080/api/webhooks/stripe
```

Stripe CLI shows a webhook secret:
```text
whsec_...
```

Update:
```properties
stripe.webhook.secret=whsec_...
```

Trigger test event:
```bash
stripe trigger checkout.session.completed
```

---

## Razorpay Webhooks (Local)

Razorpay cannot send webhooks to localhost directly.
Expose your local server using ngrok:

```bash
ngrok http 8080
```

Use webhook URL:
```text
https://<ngrok-id>.ngrok-free.app/api/webhooks/razorpay
```

Create webhook in Razorpay Dashboard:
```text
Settings → Webhooks → Add New Webhook
```

Set any webhook secret (you choose it), and set the same secret in:
```properties
razorpay.webhookSecret=your_custom_secret_here
```

---

# ✅ Verify in Database

```bash
psql -d payment_db
```

Run:
```sql
SELECT * FROM orders;
SELECT * FROM payments;
SELECT * FROM webhook_events ORDER BY id DESC;
```

Expected:
- `payments.status = PAID` on success
- `orders.status = PAID`
- webhook events stored in `webhook_events`

Exit:
```sql
\q
```

---

## 📌 Future Enhancements
- Docker + docker-compose
- Flyway DB migrations
- Refund API support
- Scheduled payment reconciliation job
- Kafka events: `payment.success`, `payment.failed`

---

## 👨‍💻 Author
This project was adapted from a Maven project by Manas Goyal.
