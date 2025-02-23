<div align="left" style="position: relative;">
<h1>K1TE-AUTH</h1>
<p align="left">
	<img src="https://img.shields.io/github/last-commit/pragmasoft-ukraine/k1te-auth?style=flat&logo=git&logoColor=white&color=0080ff" alt="last-commit">
	<img src="https://img.shields.io/github/languages/top/pragmasoft-ukraine/k1te-auth?style=flat&color=0080ff" alt="repo-top-language">
	<img src="https://img.shields.io/github/languages/count/pragmasoft-ukraine/k1te-auth?style=flat&color=0080ff" alt="repo-language-count">
	<img src="https://img.shields.io/maintenance/yes/2025?style=flat&color=0080ff" alt="maintained">
</p>
<p align="left">Built with the tools and technologies:</p>
<p align="left">
	<img src="https://img.shields.io/badge/Prometheus-E6522C.svg?style=flat&logo=Prometheus&logoColor=white" alt="Prometheus">
	<img src="https://img.shields.io/badge/Grafana-F46800.svg?style=flat&logo=Grafana&logoColor=white" alt="Grafana">
	<img src="https://img.shields.io/badge/NGINX-009639.svg?style=flat&logo=NGINX&logoColor=white" alt="NGINX">
	<img src="https://img.shields.io/badge/Docker-2496ED.svg?style=flat&logo=Docker&logoColor=white" alt="Docker">
	<img src="https://img.shields.io/badge/GitHub%20Actions-2088FF.svg?style=flat&logo=GitHub-Actions&logoColor=white" alt="GitHub%20Actions">
	<img src="https://img.shields.io/badge/java-%23ED8B00.svg?style=flat&logo=openjdk&logoColor=white" alt="java">
	<img src="https://img.shields.io/badge/HTML5-E34F26.svg?style=flat&logo=HTML5&logoColor=white" alt="HTML5">
	<img src="https://img.shields.io/badge/CSS3-1572B6.svg?style=flat&logo=CSS3&logoColor=white" alt="CSS3">
	<img src="https://img.shields.io/badge/JavaScript-F7DF1E.svg?style=flat&logo=JavaScript&logoColor=black" alt="JavaScript">
</p>
</div>
<br clear="right">

## Overview

K1te-Auth is an OAuth 2.0 and OpenID Connect implementation built with Micronaut.
It provides a secure, standards-compliant authorization server that enables applications to implement delegated authentication and authorization flows.
<br><br>
The authorization server implements the OAuth 2.0 specification with the OpenID Connect extension, allowing secure authentication and authorization across different applications and services.
It provides a complete solution for managing user consent, generating secure tokens, and handling various OAuth 2.0 grant types.
<br><br>
Built with security and flexibility, K1te Auth Server offers a comprehensive set of endpoints for authorization, token management, and OpenID Connect discovery.
The server is designed to be production-ready, with features such as support for PKCE (Proof Key for Code Exchange) and robust token handling.

---

## Features

### OAuth 2.0 Core Implementation
- Multiple grant types support:
  - Authorization Code Grant
  - Refresh Token Grant
  - Client Credentials Grant
- Resource Owner authorization and consent request
- PKCE requirement for enhanced security
- JWT-based access, refresh, and ID tokens
- Secure token generation and validation
- Built-in client application management
- Comprehensive scope-based authorization

### OpenID Connect Support
- ID Token generation with customizable claims
- UserInfo endpoint for retrieving authenticated end-user details
- Discovery endpoint (.well-known/openid-configuration)
- Nonce mechanism implementation

The server is designed to be easily integrated into existing systems, maintaining high-security standards and complying with OAuth 2.0 and OpenID Connect specifications.

---

## Project Structure

```
└── k1te-auth/
    ├── .github
    │   └── workflows
    ├── .mvn
    │   └── wrapper
    │       ├── maven-wrapper.jar
    │       └── maven-wrapper.properties
    ├── grafana
    │   └── grafana.ini
    ├── nginx
    │   └── nginx.conf.template
    ├── prometheus
    │   └── prometheus.yml
    ├── src
    │   └── main
    │       ├── java
    │       │   └── ...
    │       └── resources
    │           └── ...
    ├── .gitignore
    ├── aot-jar.properties
    ├── docker-compose.yml
    ├── Dockerfile
    ├── micronaut-cli.yml
    ├── mvnw
    ├── mvnw.bat
    ├── openapi.properties
    ├── pom.xml
    └── README.md
```

---

## Getting Started

### Prerequisites

Before getting started, ensure your runtime environment meets the following requirements:

- **Programming Language:** [Java 21](https://www.oracle.com/java/technologies/downloads/#java21)
- **Container Runtime:** [Docker](https://www.docker.com)

### Setup and Configuration

1. Clone the project from the GitHub repository:
```
$ git clone https://github.com/pragmasoft-ukraine/k1te-auth.git
```

2. Navigate to the project directory:
```
$ cd k1te-auth
```

3. OpenSSL key generation:
- Create `.key-pair` directory:
```
$ mkdir .key-pair
```
- Generate an Elliptic Curve private + public key pair for use with ES512 signatures:
```
$ openssl ecparam -genkey -name secp521r1 -noout -out .key-pair/ec512-key-pair.pem
```

4. Application Configuration:
- Create a local configuration file (e.g., `local-config.yml`) in the project's root. The content of the file must be as follows:
```
micronaut:
  security:
    token:
      jwt:
        signatures:
          secret:
            generator:
              secret: # Secret key used to sign JWT tokens for authentication
    csrf:
      signature-key: # Secret key for CSRF token signature to prevent cross-site request forgery
  email:
    from:
      email: # Sender email address for outgoing emails
      name: # Sender name for outgoing emails

smtp:
  auth: # Whether SMTP authentication is required (true/false)
  starttls:
    enable: # Whether STARTTLS should be enabled for secure email transmission (true/false)
  session:
    username: # SMTP username for authentication
    password: # SMTP password for authentication
  host: # SMTP server host
  port: # SMTP server port

turnstile:
  siteKey: # Cloudflare Turnstile site key for CAPTCHA verification
  secretKey: # Cloudflare Turnstile secret key for server-side CAPTCHA validation

admin:
  name: # Admin user display name
  email: # Admin user email for login
  password: # Admin user password

server:
  url: # Base URL of the server

pem:
  path: # Path to the PEM file (private + public key pair)

datasources:
  default:
    url: # Database connection URL (e.g., jdbc:postgresql://localhost:5432/dbname)
    username: # Database username for authentication
    password: # Database password for authentication
    driver-class-name: # Database driver class name (e.g., org.postgresql.Driver for PostgreSQL)
```
- Create a `.env` file in the project's root. The content of the file must be as follows:
```
# PostgreSQL
DB_NAME=                 # PostgreSQL database name
DB_USER=                 # PostgreSQL username for authentication
DB_PASSWORD=             # PostgreSQL password for authentication
DB_PORT=                 # The port PostgreSQL should use

# NGINX
NGINX_SERVER_NAME=       # The server name used by Nginx

# Grafana
GRAFANA_ADMIN_USER=      # The administrator username for Grafana
GRAFANA_ADMIN_PASSWORD=  # The administrator password for Grafana

```

5. Run the entire application:
```
$ docker-compose up -d
```

---

## Usage
- `/.well-known/openid-configuration` - OpenID Connect Discovery endpoint
- `/auth/login` and `/auth/registration` - Authentication endpoints
- `/swagger-ui/index.html` - OpenAPI/Swagger endpoint

---
