# HMart Web Server

A Spring Boot e-commerce backend application with JWT authentication, PostgreSQL database, AWS S3 integration, and email functionality.

## Table of Contents
- [Prerequisites](#prerequisites)
- [Project Overview](#project-overview)
- [Environment Setup](#environment-setup)
- [Installation](#installation)
- [Running the Application](#running-the-application)
- [API Documentation](#api-documentation)
- [Project Structure](#project-structure)
- [Troubleshooting](#troubleshooting)

## Prerequisites

Before running the server, ensure you have the following installed:

- **Java Development Kit (JDK) 17 or higher**
  - Check version: `java -version`
  - Download from: [Oracle](https://www.oracle.com/java/technologies/downloads/) or [OpenJDK](https://openjdk.org/)

- **Apache Maven 3.6+** (Optional - the project includes Maven Wrapper)
  - Check version: `mvn -version`
  
- **PostgreSQL 12+**
  - Check version: `psql --version`
  - Download from: [PostgreSQL Official Site](https://www.postgresql.org/download/)

- **AWS Account** (for S3 file storage)
  - Obtain access key and secret key from AWS Console

- **Gmail Account** (for email functionality)
  - Enable 2-factor authentication
  - Generate an App Password

## Project Overview

This Spring Boot application includes:

- **Spring Boot 3.2.4** - Core framework
- **Spring Security** - Security and authentication
- **JWT (JSON Web Tokens)** - Token-based authentication
- **Spring Data JPA** - Database access layer
- **PostgreSQL** - Relational database
- **AWS SDK** - S3 file storage integration
- **Spring Mail** - Email functionality
- **OpenPDF** - PDF generation
- **Thymeleaf** - Template engine
- **Spring Boot Actuator** - Application monitoring


**API Base Path:** `api/v1`

## Environment Setup

The application requires several environment variables to be configured. Create a `.env` file or set these variables in your system:

### Required Environment Variables

```bash
# Database Configuration
POSTGRES_URL=postgresql://localhost:5432/hmartweb
POSTGRES_USERNAME= postgres
POSTGRES_PASSWORD=123456

# JWT Configuration
JWT_SECRET_KEY=your_secret_key_here_at_least_256_bits

# AWS Configuration
AWS_ACCESS_KEY=your_aws_access_key
AWS_SECRET_KEY=your_aws_secret_key
AWS_REGION=us-east-1
AWS_BUCKET_NAME=your_bucket_name

# Email Configuration
MAIL_USERNAME=your_gmail_address@gmail.com
MAIL_PASSWORD=your_gmail_app_password

# Frontend URL
URL_FRONTEND=http://localhost:8080
```

### Setting Environment Variables

#### Linux/macOS:
```bash
export POSTGRES_URL="postgresql://localhost:5432/hmartweb"
export POSTGRES_USERNAME= postgres
export POSTGRES_PASSWORD= 123456
# ... set other variables
```

#### Windows (Command Prompt):
```cmd
set POSTGRES_URL=postgresql://localhost:5432/hmartweb
set POSTGRES_USERNAME=postgres
set POSTGRES_PASSWORD=123456
# ... set other variables
```

## Installation

### 1. Clone the Repository
```bash
git clone <repository-url>
cd DoAnTN/server
```

### 2. Set Up PostgreSQL Database

Create a new database for the application:

```sql
-- Connect to PostgreSQL
psql -U postgres

-- Create database
CREATE DATABASE hmartweb;

-- Create user (if needed)
CREATE USER postgres WITH PASSWORD 123456;

-- Grant privileges
GRANT ALL PRIVILEGES ON DATABASE hmartweb TO postgres;

-- Exit
\q
```

### 3. Configure AWS S3 Bucket

1. Log in to AWS Console
2. Navigate to S3
3. Create a new bucket or use an existing one
4. Configure bucket permissions for public read access (if needed)
5. Obtain your Access Key ID and Secret Access Key from IAM

### 4. Configure Gmail App Password

1. Go to your Google Account settings
2. Enable 2-Step Verification
3. Go to Security > App passwords
4. Generate a new app password for "Mail"
5. Use this password in the `MAIL_PASSWORD` environment variable

## Running the Application

### Option 1: Using Maven Wrapper (Recommended)

#### Linux/macOS:
```bash
# Make the wrapper executable
chmod +x mvnw

# Run the application
./mvnw spring-boot:run
```

#### Windows:
```cmd
mvnw.cmd spring-boot:run
```

### Option 2: Using Installed Maven

```bash
mvn spring-boot:run
```

### Option 3: Build and Run JAR

```bash
# Build the application
./mvnw clean package

# Run the JAR file
java -jar target/hmartweb-0.0.1-SNAPSHOT.jar
```

### Successful Startup

When the application starts successfully, you should see:


### Common Endpoints (Example)
- Authentication endpoints will be under `/api/v1/auth`
- Protected endpoints require JWT token in Authorization header:
  ```
  Authorization: Bearer <your_jwt_token>
  ```

## Project Structure

```
server/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/project/hmartweb/
│   │   │       └── HmartwebApplication.java
│   │   └── resources/
│   │       ├── application.yml
│   │       ├── fonts/
│   │       ├── i18n/
│   │       ├── mailtemplates/
│   │       └── static/
│   └── test/
├── pom.xml
├── mvnw
├── mvnw.cmd
└── README.md
```

## Troubleshooting

### Common Issues


#### 2. Database Connection Failed
```
Error: Unable to open JDBC Connection
```
**Solution:**
- Verify PostgreSQL is running: `sudo systemctl status postgresql`
- Check database credentials
- Ensure database exists
- Verify the POSTGRES_URL format

#### 3. Environment Variables Not Found
```
Error: Could not resolve placeholder 'POSTGRES_USERNAME'
```
**Solution:**
- Ensure all required environment variables are set
- Restart your terminal/IDE after setting variables
- Verify variables with `echo $POSTGRES_USERNAME` (Linux/macOS) or `echo %POSTGRES_USERNAME%` (Windows)

#### 4. JWT Secret Key Error
```
Error: JWT secret key must be at least 256 bits
```
**Solution:**
- Generate a secure secret key (at least 32 characters)
- Example: `openssl rand -base64 32`

#### 5. AWS Credentials Invalid
```
Error: The AWS Access Key Id you provided does not exist
```
**Solution:**
- Verify AWS credentials in IAM Console
- Ensure correct region is set
- Check bucket name is correct

#### 6. Email Send Failed
```
Error: Authentication failed
```
**Solution:**
- Verify Gmail app password (not regular password)
- Enable 2-factor authentication on Gmail
- Check SMTP settings

### Viewing Logs

To see detailed logs, you can:

1. Check console output
2. Enable debug logging in `application.yml`:
```yaml
logging:
  level:
    com.project.tmartweb: DEBUG
```

### Clean Build

If you encounter build issues:

```bash
./mvnw clean install -U
```

## Development

### Running Tests
```bash
./mvnw test
```

### Building for Production
```bash
./mvnw clean package -DskipTests
```

### Hot Reload (Development)
Add Spring Boot DevTools dependency for automatic restart during development.

## Support

For issues or questions:
1. Check the troubleshooting section above
2. Review application logs
3. Verify all environment variables are correctly set
4. Ensure all prerequisites are installed and configured

## License

[Add your license information here]

---

**Note:** Make sure to keep your environment variables and credentials secure. Never commit sensitive information to version control.

