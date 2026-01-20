# VetFlow 🐕‍🦺

> Modern veterinary clinic management system made simple, powerful, and free

[![License: MIT](https://img.shields.io/badge/License-MIT-blue.svg)](LICENSE)
[![Backend](https://img.shields.io/badge/Backend-Spring%20Boot%203.5-brightgreen)](backend/)
[![Frontend](https://img.shields.io/badge/Frontend-React%2018-blue)](frontend/)
[![Database](https://img.shields.io/badge/Database-PostgreSQL%2015-blue)](database/)

## 🎯 Project Status

| Component | Status | Version | Completion |
|-----------|--------|---------|------------|
| 🗄️ Database Design | ✅ Complete | v1.0 | 100% |
| 🔌 Backend API | ✅ Complete | v1.0 | 100% |
| 🖥️ Frontend | ✅ Complete | v0.11.0 | 95% |
| 📱 Mobile App | ⏳ Planned | - | 0% |
| 🚀 Deployment | 🔄 In Progress | - | 50% |

## ✨ Features

### ✅ Implemented
- 🏥 **Patient Management** - Complete CRUD with medical history
- 📅 **Appointment Scheduling** - Calendar view with drag-and-drop
- 👤 **Owner Management** - Track pet owners and relationships
- 📋 **Medical Records** - Clinical notes, prescriptions, lab results
- 🔒 **Authentication & Authorization** - JWT-based with role management (Admin, Veterinarian, Assistant)
- 📊 **Dashboard & Analytics** - Real-time statistics and charts
- 🔔 **Notifications** - Real-time notification system
- 🌓 **Dark Mode** - Theme switcher with light/dark modes
- 🔍 **Global Search** - Cmd+K command palette
- 📤 **Export Functionality** - CSV and PDF export
- ☑️ **Bulk Operations** - Select, delete, and export multiple records
- 📄 **PDF Generation** - Professional medical record PDFs

### 🔄 In Progress
- 📊 Advanced reporting and analytics
- 👥 User management (admin panel)
- 📝 Audit logging

### ⏳ Planned
- 💰 Billing and invoices
- 💊 Inventory management
- 🏢 Multi-clinic support
- 📱 Mobile application
- 📧 Email notifications
- 🔄 Appointment reminders

## 🚀 Quick Start

### Prerequisites
- Docker & Docker Compose (recommended)
- OR: Java 17+, Node.js 18+, PostgreSQL 15+

### Option 1: Docker (Recommended)

```bash
# Clone repository
git clone git@github.com:mrp4sten/vetflow-system.git
cd vetflow-system

# Configure environment
cd docker
cp .env.example .env
# Edit .env and update passwords/secrets

# Start all services
docker compose up --build
```

**Access the application:**
- Frontend: http://localhost:5173
- Backend API: http://localhost:8080
- Swagger UI: http://localhost:8080/swagger-ui/index.html

**Default credentials:**
- Username: `admin`
- Password: `Vetflow#2024`

### Option 2: Local Development

See [SETUP.md](SETUP.md) for detailed instructions.

## 📚 Documentation

- **[Complete Setup Guide](SETUP.md)** - Detailed installation and configuration
- **[Backend Documentation](backend/README.md)** - API reference and backend architecture
- **[Frontend Documentation](frontend/README.md)** - UI components and development guide
- **[Database Schema](database/README.md)** - Database design and migrations
- **[AI Assistant Guide](AGENTS.md)** - Guide for AI coding assistants

## 🏗️ Technology Stack

### Backend
- **Framework:** Spring Boot 3.5.6
- **Language:** Java 17
- **Database:** PostgreSQL 15
- **Authentication:** Spring Security 6 + JWT
- **Migrations:** Flyway
- **Mapping:** MapStruct
- **Documentation:** SpringDoc OpenAPI (Swagger)
- **Testing:** JUnit 5, Mockito, TestContainers

### Frontend
- **Framework:** React 18
- **Language:** TypeScript 5
- **Build Tool:** Vite 5
- **UI Library:** ShadCN UI + Tailwind CSS
- **State Management:** Zustand
- **Server State:** React Query (TanStack Query)
- **Forms:** React Hook Form + Zod
- **Routing:** React Router v6
- **Calendar:** FullCalendar
- **Charts:** Recharts
- **PDF:** jsPDF
- **Notifications:** Sonner

### Infrastructure
- **Containerization:** Docker + Docker Compose
- **Web Server:** Nginx (production)
- **CI/CD:** GitHub Actions (planned)

## 📂 Project Structure

```
vetflow-system/
├── backend/           # Spring Boot REST API
├── frontend/          # React TypeScript SPA
├── database/          # PostgreSQL schemas and migrations
├── docker/            # Docker Compose configuration
├── docs/              # Additional documentation
└── .github/           # GitHub workflows (planned)
```

## 🌳 Git Workflow

- **`master`** - Main production branch
- **`frontend-development`** - Active frontend development
- **`feature/*`** - Feature branches

## 🧪 Testing

```bash
# Backend tests
cd backend
./mvnw test

# Frontend tests (coming soon)
cd frontend
npm run test
```

## 🤝 Contributing

Contributions are welcome! Please follow these steps:

1. Fork the repository
2. Create a feature branch: `git checkout -b feature/amazing-feature`
3. Commit your changes: `git commit -m 'feat: add amazing feature'`
4. Push to the branch: `git push origin feature/amazing-feature`
5. Open a Pull Request

## 📄 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## 👨‍💻 Author

**mrp4sten**
- GitHub: [@mrp4sten](https://github.com/mrp4sten)

## 🙏 Acknowledgments

- ShadCN UI for beautiful components
- Spring Boot team for excellent framework
- React and Vite communities

---

**Status:** Active Development | **Version:** v0.11.0 | **Last Updated:** January 19, 2026
