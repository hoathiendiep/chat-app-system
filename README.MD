# Chat App System

## Overview

The Chat App System is a real-time chat application enabling secure user authentication, instant messaging, and file uploads. The backend, developed in **Kotlin** using the **Spring Boot** framework, adheres to **clean architecture** principles, ensuring modular, maintainable, and testable code through distinct layers (presentation, domain, application, infrastructure). Real-time communication is powered by a **WebSocket mechanism**, facilitating seamless conversation initiation between users. The frontend is built with Angular, with PostgreSQL for data persistence, Keycloak for authentication, and Azurite for blob storage emulation. The application is containerized with Docker and supports deployment locally or on AWS EC2 via a GitHub Actions CI/CD pipeline defined in `cicd.yml`.

## Features

- Real-time messaging via WebSockets for instant user communication.
- Secure user authentication with Keycloak OAuth2/OpenID Connect.
- File uploads stored in Azurite (Azure Blob Storage emulator).
- Angular-based frontend.
- Kotlin/Spring Boot REST API with PostgreSQL.
- HTTP access for simplified deployment.
- Automated CI/CD pipeline for EC2 deployment.

## Architecture

The backend follows **clean architecture**, implemented in Kotlin with Spring Boot, and is organized into:
- **Presentation Layer** (`api`): REST controllers handle HTTP/WebSocket requests.
- **Domain Layer** (`domain`): Business logic, entities, and service interfaces, independent of frameworks.
- **Application Layer** (`app`): Service implementations, ports, and utilities orchestrate domain logic.
- **Infrastructure Layer** (`infrastructure`): Adapters, persistence, and configurations integrate with external systems.

The system comprises five services, orchestrated with Docker Compose:
- **Frontend** (`chat-app-frontend`): Angular app served by Nginx, accessible at `http://localhost:4200` locally or `http://<your-ec2-public-ip>:4200` on EC2.
- **Backend** (`chat-app-backend`): Kotlin/Spring Boot REST API with WebSocket support, at `http://localhost:8081/chat-app-api/rs/v1` or `http://<your-ec2-public-ip>:8081/chat-app-api/rs/v1`.
- **Database** (`postgres-db`): PostgreSQL for user data and messages.
- **Authentication** (`keycloak`): Keycloak for authentication, at `http://localhost:8080` or `http://<your-ec2-public-ip>:8080`.
- **Storage** (`azurite`): Azurite for blob storage emulation.

## Prerequisites

- GitHub repository for source code and CI/CD.
- Docker and Docker Compose for containerization.
- Node.js (v22.12.0) for frontend build.
- Kotlin 2.0.21 and Maven for building the Spring Boot backend, requiring Java 21 runtime.
- AWS account with EC2 t3.small instance (for AWS deployment).
- SSH key for EC2 access (e.g., `chat-app-key.pem`).
- Domain name with a hosted zone in Route 53, mapping to the EC2 public IP.
## Local Deployment Guide (Docker)

1. **Clone the Repository**:
   ```bash
   git clone https://github.com/your-username/chat-app-system.git
   cd chat-app-system
   ```

2. **Configure Environment Variables**:
   - Ensure `.env` is in the project root (refer to code for sample values).

3. **Run Docker Compose**:
   - The `docker-compose.yaml` file configures all services, with `Dockerfile` paths for `chat-app-api` and `chat-backend-api` correctly specified.
   ```bash
   docker-compose.yml up -d
   ```

4. **Configure Keycloak**:
   - Access Keycloak at `http://localhost:8080`.
   - Log in with username `admin` and password `admin`.
   - **Create Realm**:
     - Click **Add Realm**.
     - Name: `chatapp-realm`.
     - Click **Create**.
   - **Create Client**:
     - In `chatapp-realm`, go to **Clients** > **Create**.
     - In General settings tab:
        - Client Type: `OpenID Connect`.
        - Client ID: `chatapp`.
        - Click next.
     - In Capability tab:
       - Leave default settings.(tick `Standard Flow` and  `Direct access grants`).
       - Click next.
     - In Login settings tab:
       - **Root URL**: `http://localhost:4200`.
       - **Home URL**: `http://localhost:4200`.
       - **Valid redirect URIs**: `http://localhost:4200/*`.
       - **Web Origins**: `http://localhost:4200`.
     - Save changes.
   - **Configure Settings**:
     - In `chatapp-realm`, go to **Realm settings** > **Login** tab.
     - Enable `User registration `, `Remember Me`, `Email as username` and `Login with email`.

7. **Access the Application**:
   - Open `http://localhost:4200`.

8. **Stop Containers**:
    ```bash
    docker-compose.yml down
    ```

## AWS Deployment

1. **Set Up EC2 Instance**:
   - Launch a t3.small instance with Amazon Linux 2.
   - Configure security group:
     ```
     Type   | Protocol | Port Range | Source
     -------|----------|------------|--------
     SSH    | TCP      | 22         | Your IP
     Custom | TCP      | 4200       | 0.0.0.0/0
     Custom | TCP      | 8080       | 0.0.0.0/0
     Custom | TCP      | 8081       | 0.0.0.0/0
     Custom | TCP      | 10000-10002| 0.0.0.0/0
     HTTP   | TCP      | 80         | 0.0.0.0/0 
     ```
   - Save private key (e.g., `chat-app-key.pem`).

2. **Configure Domain in Route 53**:
   - Create a hosted zone for your domain in Route 53.
   - Add an A record mapping your domain (e.g., `<your-domain>`) to your EC2’s public IP.
   - Update your domain’s nameservers at Hostinger to match those provided by Route 53.

3. **Configure Nginx.conf, Keycloak, PostgreSQL, and Azurite**:
   - In chat-app-frontend/nginx.conf, update the `server_name` and `Access-Control-Allow-Origin` directive to your domain
   - Follow the respective sections above, using the `http://<your-ec2-public-ip>` for URLs.
   - Make sure in Keycloak setup URL and web origins based on your domain
   - Update cicd.yml

4. **Access the Application**:
   - Open `http://<your-domain>`. 

## CI/CD Pipeline

The repository includes a GitHub Actions workflow (`cicd.yml`) for automated deployment to an AWS EC2 t3.small instance running Linux. The pipeline:
- Pushes source code (`chat-app-api`, `chat-app-backend`, `docker-compose.aws.yaml`) to EC2.
- Installs Docker, Node.js, Java, and Maven on EC2.
- Compiles frontend and backend code.
- Builds Docker images and deploys services using Docker Compose.

### Configure GitHub Secrets
In `Settings > Secrets and variables > Actions`, add:
- `EC2_PUBLIC_IP`: Your EC2 instance’s public IP.
- `EC2_SSH_PRIVATE_KEY`: Contents of `chat-app-key.pem`.
- `KC_BOOTSTRAP_ADMIN_PASSWORD`: Keycloak admin password.
- `KC_DB_PASSWORD`: Keycloak database password.
- `DATASOURCE_PASSWORD`: PostgreSQL password.
- `DOMAIN_URL`: Your domain

## Troubleshooting

- **Keycloak Login Issues**:
  - Verify `chatapp-realm` and `chatapp` client settings.
  - Check redirect URIs and web origins. 

- **Container Issues**:
  ```bash
  docker-compose logs -f <container-name> 
  ```
 
## Contact

For issues or contributions, open a GitHub issue or contact [hoathiendiep@gmail.com].