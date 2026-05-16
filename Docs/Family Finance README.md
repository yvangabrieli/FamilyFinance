# 💰 FamilyFinance API

A RESTful backend API for a family budget tracking application built with **Java 21 + Spring Boot 3.x**.
Supports multi-user family groups, transaction management, budgets, and financial insights.

---

## 📁 Project Structure

```
familyfinance-api/
├── .github/
│   └── workflows/
│       └── ci.yml                        # GitHub Actions CI pipeline
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/familyfinance/api/
│   │   │       ├── FamilyFinanceApplication.java       # @SpringBootApplication entry point
│   │   │       │
│   │   │       ├── config/
│   │   │       │   ├── SecurityConfig.java             # Spring Security + JWT filter chain
│   │   │       │   ├── JwtConfig.java                  # JWT secret, expiry settings
│   │   │       │   └── CorsConfig.java                 # CORS rules for mobile client
│   │   │       │
│   │   │       ├── controller/
│   │   │       │   ├── AuthController.java             # POST /auth/register, /auth/login
│   │   │       │   ├── UserController.java             # GET/PUT /users/{id}
│   │   │       │   ├── FamilyController.java           # CRUD /families, invite members
│   │   │       │   ├── TransactionController.java      # CRUD /transactions
│   │   │       │   ├── BudgetController.java           # CRUD /budgets
│   │   │       │   ├── CategoryController.java         # GET /categories
│   │   │       │   └── InsightController.java          # GET /insights (aggregations)
│   │   │       │
│   │   │       ├── service/
│   │   │       │   ├── AuthService.java                # Register, login, token issue
│   │   │       │   ├── UserService.java                # User profile management
│   │   │       │   ├── FamilyService.java              # Family group business logic
│   │   │       │   ├── TransactionService.java         # Transaction CRUD + filtering
│   │   │       │   ├── BudgetService.java              # Budget tracking + alerts
│   │   │       │   ├── CategoryService.java            # Seed + manage categories
│   │   │       │   └── InsightService.java             # Monthly summaries, trends
│   │   │       │
│   │   │       ├── repository/
│   │   │       │   ├── UserRepository.java             # JPA repo for users
│   │   │       │   ├── FamilyRepository.java
│   │   │       │   ├── FamilyMemberRepository.java
│   │   │       │   ├── TransactionRepository.java      # Custom JPQL queries for filtering
│   │   │       │   ├── BudgetRepository.java
│   │   │       │   └── CategoryRepository.java
│   │   │       │
│   │   │       ├── model/
│   │   │       │   ├── entity/
│   │   │       │   │   ├── User.java                   # @Entity: id, name, email, passwordHash, role
│   │   │       │   │   ├── Family.java                 # @Entity: id, name, createdBy
│   │   │       │   │   ├── FamilyMember.java           # @Entity: userId, familyId, role (ADMIN/MEMBER)
│   │   │       │   │   ├── Transaction.java            # @Entity: amount, date, category, userId, familyId
│   │   │       │   │   ├── Budget.java                 # @Entity: categoryId, limit, period (MONTHLY/WEEKLY)
│   │   │       │   │   └── Category.java               # @Entity: name, icon, type (INCOME/EXPENSE)
│   │   │       │   │
│   │   │       │   └── enums/
│   │   │       │       ├── TransactionType.java        # INCOME, EXPENSE
│   │   │       │       ├── BudgetPeriod.java           # WEEKLY, MONTHLY, YEARLY
│   │   │       │       └── FamilyRole.java             # ADMIN, MEMBER
│   │   │       │
│   │   │       ├── dto/
│   │   │       │   ├── request/
│   │   │       │   │   ├── RegisterRequest.java        # name, email, password
│   │   │       │   │   ├── LoginRequest.java           # email, password
│   │   │       │   │   ├── TransactionRequest.java     # amount, date, categoryId, note
│   │   │       │   │   ├── BudgetRequest.java          # categoryId, limitAmount, period
│   │   │       │   │   └── InviteMemberRequest.java    # email, role
│   │   │       │   │
│   │   │       │   └── response/
│   │   │       │       ├── AuthResponse.java           # accessToken, refreshToken, user
│   │   │       │       ├── TransactionResponse.java    # Flat view with category name, icon
│   │   │       │       ├── BudgetSummaryResponse.java  # budget limit + spent + percentage
│   │   │       │       └── InsightResponse.java        # monthly totals, top categories
│   │   │       │
│   │   │       ├── security/
│   │   │       │   ├── JwtTokenProvider.java           # Generate + validate JWT tokens
│   │   │       │   ├── JwtAuthFilter.java              # OncePerRequestFilter for JWT
│   │   │       │   └── UserPrincipal.java              # Spring Security UserDetails impl
│   │   │       │
│   │   │       └── exception/
│   │   │           ├── GlobalExceptionHandler.java     # @RestControllerAdvice
│   │   │           ├── ResourceNotFoundException.java  # 404
│   │   │           ├── UnauthorizedException.java      # 401
│   │   │           └── ValidationException.java        # 400
│   │   │
│   │   └── resources/
│   │       ├── application.yml                         # Main config (DB, JWT, server port)
│   │       ├── application-dev.yml                     # Dev overrides (local Postgres)
│   │       ├── application-prod.yml                    # Prod overrides (env vars)
│   │       └── db/
│   │           └── migration/
│   │               ├── V1__init_schema.sql             # Initial tables
│   │               ├── V2__seed_categories.sql         # Default categories (Food, Rent…)
│   │               └── V3__add_budget_alerts.sql       # Budget threshold columns
│   │
│   └── test/
│       └── java/
│           └── com/familyfinance/api/
│               ├── controller/
│               │   ├── AuthControllerTest.java
│               │   └── TransactionControllerTest.java
│               └── service/
│                   ├── TransactionServiceTest.java
│                   └── BudgetServiceTest.java
│
├── docker/
│   ├── Dockerfile                                      # Multi-stage: build + runtime image
│   └── docker-compose.yml                             # App + Postgres + Redis stack
│
├── .env.example                                        # Template for required env variables
├── .gitignore
├── mvnw                                                # Maven wrapper (no Maven install needed)
├── pom.xml                                            # Dependencies
└── README.md
```

---

## 🚀 Getting Started

### Prerequisites

| Tool | Version | Download |
|------|---------|----------|
| Java JDK | 21+ | [adoptium.net](https://adoptium.net) |
| Docker Desktop | Latest | [docker.com](https://www.docker.com/products/docker-desktop/) |
| IntelliJ IDEA | 2023.3+ | [jetbrains.com](https://www.jetbrains.com/idea/) |
| Git | Any | [git-scm.com](https://git-scm.com) |

> **Postman** (optional but recommended for testing): [postman.com](https://www.postman.com)

---

### 1. Clone & Open in IntelliJ

```bash
git clone https://github.com/YOUR_USERNAME/familyfinance-api.git
cd familyfinance-api
```

Open IntelliJ → **File → Open** → select the `familyfinance-api` folder.
IntelliJ will auto-detect the Maven project and download dependencies.

---

### 2. Start the Database with Docker

```bash
docker-compose -f docker/docker-compose.yml up -d postgres redis
```

This starts:
- **PostgreSQL** on `localhost:5432` (db: `familyfinance`, user: `ffuser`, pass: `ffpass`)
- **Redis** on `localhost:6379` (used for session caching in Tier 3)

> Verify it's running: `docker ps` — you should see both containers with status `Up`.

---

### 3. Configure Environment

Copy the example env file:

```bash
cp .env.example .env
```

Edit `.env` with your values (see **Environment Variables** section below).

---

### 4. Run the Application

**Option A — IntelliJ (recommended):**
- Open `FamilyFinanceApplication.java`
- Click the green ▶ Run button next to the `main` method
- Check the **Run** console for `Started FamilyFinanceApplication on port 8080`

**Option B — Terminal:**
```bash
./mvnw spring-boot:run -Dspring-boot.run.profiles=dev
```

The API will be available at: `http://localhost:8080`

---

## ⚙️ Environment Variables

Defined in `.env` (never commit this file — it's in `.gitignore`):

```dotenv
# Database
DB_HOST=localhost
DB_PORT=5432
DB_NAME=familyfinance
DB_USER=ffuser
DB_PASSWORD=ffpass

# JWT
JWT_SECRET=your-256-bit-secret-key-here-change-this-in-production
JWT_EXPIRATION_MS=86400000        # 24 hours in milliseconds
JWT_REFRESH_EXPIRATION_MS=604800000  # 7 days

# Server
SERVER_PORT=8080
SPRING_PROFILES_ACTIVE=dev

# Redis (optional for now, needed in Tier 3)
REDIS_HOST=localhost
REDIS_PORT=6379
```

> **Generate a secure JWT secret:**
> ```bash
> openssl rand -base64 64
> ```

---

## 🗄️ Database Schema

### Core Tables

```
users
  ├── id (UUID, PK)
  ├── name (VARCHAR 100)
  ├── email (VARCHAR 255, UNIQUE)
  ├── password_hash (VARCHAR 255)
  ├── role (ENUM: USER, ADMIN)
  └── created_at (TIMESTAMP)

families
  ├── id (UUID, PK)
  ├── name (VARCHAR 100)
  ├── created_by (UUID, FK → users.id)
  └── created_at (TIMESTAMP)

family_members
  ├── id (UUID, PK)
  ├── family_id (UUID, FK → families.id)
  ├── user_id (UUID, FK → users.id)
  ├── role (ENUM: ADMIN, MEMBER)
  └── joined_at (TIMESTAMP)

categories
  ├── id (UUID, PK)
  ├── name (VARCHAR 50)           -- e.g. "Groceries"
  ├── icon (VARCHAR 50)           -- e.g. "shopping-cart"
  ├── type (ENUM: INCOME, EXPENSE)
  └── is_default (BOOLEAN)

transactions
  ├── id (UUID, PK)
  ├── amount (NUMERIC 19,4)       -- NEVER float/double for money!
  ├── type (ENUM: INCOME, EXPENSE)
  ├── category_id (UUID, FK → categories.id)
  ├── user_id (UUID, FK → users.id)
  ├── family_id (UUID, FK → families.id)
  ├── note (TEXT, nullable)
  ├── date (DATE)
  └── created_at (TIMESTAMP)

budgets
  ├── id (UUID, PK)
  ├── family_id (UUID, FK → families.id)
  ├── category_id (UUID, FK → categories.id)
  ├── limit_amount (NUMERIC 19,4)
  ├── period (ENUM: WEEKLY, MONTHLY, YEARLY)
  ├── alert_threshold (INTEGER)   -- alert at X% of limit (e.g. 80)
  └── created_at (TIMESTAMP)
```

> **Why `NUMERIC(19,4)` for money?** Float/double types have rounding errors (e.g. 0.1 + 0.2 ≠ 0.3). `NUMERIC` is exact. Always use `BigDecimal` in Java, never `double`.

Database migrations are handled by **Flyway** automatically on startup. Migration files live in `src/main/resources/db/migration/`.

---

## 🔌 API Endpoints

Base URL: `http://localhost:8080/api/v1`

### 🔓 Auth (no token required)

| Method | Endpoint | Description | Body |
|--------|----------|-------------|------|
| POST | `/auth/register` | Create a new account | `{ name, email, password }` |
| POST | `/auth/login` | Login and get JWT tokens | `{ email, password }` |
| POST | `/auth/refresh` | Get new access token | `{ refreshToken }` |

**Sample Register Request:**
```json
POST /api/v1/auth/register
{
  "name": "María García",
  "email": "maria@example.com",
  "password": "SecurePass123!"
}
```

**Sample Response:**
```json
{
  "accessToken": "eyJhbGciOi...",
  "refreshToken": "eyJhbGciOi...",
  "user": {
    "id": "uuid-here",
    "name": "María García",
    "email": "maria@example.com"
  }
}
```

---

### 🔒 All routes below require: `Authorization: Bearer <accessToken>`

---

### 👥 Families

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/families` | Create a new family group |
| GET | `/families/{id}` | Get family details + members |
| POST | `/families/{id}/invite` | Invite a member by email |
| DELETE | `/families/{id}/members/{userId}` | Remove a member |

---

### 💸 Transactions

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/transactions` | List all (supports filters below) |
| POST | `/transactions` | Add a new transaction |
| GET | `/transactions/{id}` | Get single transaction |
| PUT | `/transactions/{id}` | Update a transaction |
| DELETE | `/transactions/{id}` | Delete a transaction |

**Query Parameters for GET `/transactions`:**

| Param | Type | Example | Description |
|-------|------|---------|-------------|
| `familyId` | UUID | `?familyId=abc-123` | Filter by family |
| `userId` | UUID | `?userId=xyz-456` | Filter by member |
| `categoryId` | UUID | `?categoryId=...` | Filter by category |
| `type` | String | `?type=EXPENSE` | INCOME or EXPENSE |
| `from` | Date | `?from=2024-01-01` | Start date (ISO 8601) |
| `to` | Date | `?to=2024-01-31` | End date (ISO 8601) |
| `page` | Integer | `?page=0` | Page number (0-indexed) |
| `size` | Integer | `?size=20` | Items per page |

---

### 📊 Budgets

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/budgets` | List all budgets for a family |
| POST | `/budgets` | Create a budget |
| GET | `/budgets/{id}/summary` | Budget vs actual spending |
| PUT | `/budgets/{id}` | Update a budget |
| DELETE | `/budgets/{id}` | Delete a budget |

**Budget Summary Response:**
```json
{
  "budgetId": "...",
  "category": "Groceries",
  "period": "MONTHLY",
  "limitAmount": 500.00,
  "spentAmount": 342.50,
  "remainingAmount": 157.50,
  "percentageUsed": 68.5,
  "isOverBudget": false
}
```

---

### 🏷️ Categories

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/categories` | List all categories |
| GET | `/categories?type=EXPENSE` | Filter by type |
| POST | `/categories` | Create a custom category |

**Default seeded categories:**

| Expense | Income |
|---------|--------|
| 🛒 Groceries | 💼 Salary |
| 🏠 Rent / Mortgage | 🎁 Gift |
| 🚗 Transport | 📈 Investment |
| 🍽️ Restaurants | 💰 Other Income |
| 💊 Health | |
| 📱 Subscriptions | |
| 🎮 Entertainment | |
| 📚 Education | |

---

### 📈 Insights

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/insights/monthly-summary` | Total income/expenses for a month |
| GET | `/insights/top-categories` | Top spending categories |
| GET | `/insights/trend` | Month-over-month spending trend |

**Monthly Summary Response:**
```json
{
  "year": 2024,
  "month": 5,
  "totalIncome": 3500.00,
  "totalExpenses": 2150.75,
  "netBalance": 1349.25,
  "satisfactionScore": 72,
  "topExpenseCategory": "Groceries"
}
```

> `satisfactionScore` is your custom "satisfaction metric" — calculated as how well the family stayed within their budgets (0–100).

---

## 🔐 Authentication Flow

```
1. Client          →  POST /auth/register or /auth/login
2. Server          →  Validate credentials → Issue JWT (access) + JWT (refresh)
3. Client          →  Store tokens securely (not localStorage on web)
4. Client          →  Include header: Authorization: Bearer <accessToken>
5. JwtAuthFilter   →  Intercepts request → validates token → sets SecurityContext
6. Server          →  Processes request as authenticated user
7. (Token expires) →  Client sends POST /auth/refresh with refreshToken
```

Access token lifetime: **24 hours**
Refresh token lifetime: **7 days**

---

## 🏗️ Key Dependencies (`pom.xml`)

```xml
<!-- Spring Boot Starters -->
<dependency>spring-boot-starter-web</dependency>          <!-- REST API -->
<dependency>spring-boot-starter-data-jpa</dependency>     <!-- ORM / Hibernate -->
<dependency>spring-boot-starter-security</dependency>     <!-- Auth + filter chain -->
<dependency>spring-boot-starter-validation</dependency>   <!-- @Valid, @NotNull etc -->

<!-- Database -->
<dependency>postgresql</dependency>                        <!-- JDBC driver -->
<dependency>flyway-core</dependency>                      <!-- DB migrations -->

<!-- JWT -->
<dependency>jjwt-api</dependency>                        <!-- JWT generation/validation -->
<dependency>jjwt-impl</dependency>
<dependency>jjwt-jackson</dependency>

<!-- Utilities -->
<dependency>lombok</dependency>                           <!-- @Getter, @Builder, @Slf4j -->
<dependency>mapstruct</dependency>                        <!-- Entity ↔ DTO mapping -->

<!-- Testing -->
<dependency>spring-boot-starter-test</dependency>         <!-- JUnit 5, Mockito -->
<dependency>testcontainers</dependency>                   <!-- Real Postgres in tests -->
```

---

## 🧪 Testing

```bash
# Run all tests
./mvnw test

# Run a specific test class
./mvnw test -Dtest=TransactionServiceTest

# Run with coverage report (output: target/site/jacoco/index.html)
./mvnw verify
```

Tests use **Testcontainers** to spin up a real PostgreSQL instance — no mocking the DB.

---

## 🐳 Docker

### Run the full stack locally

```bash
docker-compose -f docker/docker-compose.yml up --build
```

Services started:
- `api` → Spring Boot app on port `8080`
- `postgres` → PostgreSQL on port `5432`
- `redis` → Redis on port `6379`

### Build just the API image

```bash
docker build -f docker/Dockerfile -t familyfinance-api:latest .
```

The `Dockerfile` uses a **multi-stage build**:
1. Stage 1 (`builder`): Compiles with Maven + JDK 21
2. Stage 2 (`runtime`): Runs with JRE 21 slim image (~200MB final size)

---

## 📋 Development Workflow

### Recommended IntelliJ setup

1. **Enable annotation processing** (for Lombok + MapStruct):
   `Settings → Build, Execution, Deployment → Compiler → Annotation Processors → Enable`

2. **Set active Spring profile**:
   `Run → Edit Configurations → Environment variables → SPRING_PROFILES_ACTIVE=dev`

3. **Install plugins**:
    - Lombok Plugin (already bundled in recent IntelliJ)
    - Database Tools (built-in Ultimate, or use DBeaver for Community)
    - HTTP Client (built-in — use `.http` files instead of Postman)

### Making a change (feature workflow)

```bash
git checkout -b feature/add-recurring-transactions
# ... make changes ...
./mvnw test                   # all tests pass?
git add .
git commit -m "feat: add recurring transaction support"
git push origin feature/add-recurring-transactions
# → open Pull Request on GitHub
```

---

## 📦 Deployment (later — Week 15-16)

### Free options

| Platform | Free Tier | Notes |
|----------|-----------|-------|
| **Railway** | 500h/month | Easiest, auto-deploys from GitHub |
| **Render** | 750h/month | Sleeps after 15min inactivity |
| **Fly.io** | 3 shared VMs | More control, Docker-native |
| **Supabase** | Postgres free tier | For the DB only |

### Deploy to Railway (simplest)

1. Push your code to GitHub
2. Go to [railway.app](https://railway.app) → New Project → Deploy from GitHub
3. Add a PostgreSQL plugin
4. Set environment variables (same as `.env`)
5. Done — Railway gives you a public URL

---

## 🗺️ Roadmap

- [x] Project scaffolding
- [ ] Week 1-2: Auth (register, login, JWT)
- [ ] Week 3-4: Transactions CRUD + filtering
- [ ] Week 5-6: Budgets + alerts
- [ ] Week 7-8: Insights + satisfaction score
- [ ] Week 9-10: Family groups + member roles
- [ ] Week 11-12: Connect mobile frontend
- [ ] Week 13-14: Push notifications (budget alerts)
- [ ] Week 15-16: Deploy to cloud

---

## 🤝 Contributing (for family members)

1. Never commit directly to `main`
2. Always create a feature branch
3. Write at least one test per new endpoint
4. Money fields: always `BigDecimal`, never `double`

---

## 📄 License

Private — family use only (for now).