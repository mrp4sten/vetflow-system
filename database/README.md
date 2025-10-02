# VetFlow Database 🗃️

> "Managing pet data like we managed our... pharmaceutical operations" - Lenny

## 📁 Directory Structure

```shell
database/
├── schema.sql              # 🏗️ Main database schema
├── seed-data.sql           # 🌱 Sample data for demos  
├── setup-db.sh            # 🚀 Deployment script (Lenny-style)
└── README.md              # 📖 This file
```

## 🚀 Quick Start

### Prerequisites

- Docker and Docker Compose installed
- `vetflow_db` container running

### Deployment

```bash

# Make the script executable (one time)

chmod +x setup-db.sh

# Run the deployment

./setup-db.sh
```

## 📊 Database Overview

### Core Tables

| Table | Icon | Purpose |
|-------|------|---------|
| `system_users` | 🔐 | Clinic staff authentication |
| `owners` | 👨‍💼 | Pet owners information |
| `patients` | 🐕 | Pets and their medical basics |
| `appointments` | 📅 | Scheduling and visit management |
| `medical_records` | 🏥 | Complete medical history |
| `audit_log` | 📝 | Security and compliance tracking |

## 🔧 Manual Operations

### Connect to Database

```bash
docker exec -it vetflow_db psql -U mrp4sten -d vetflow
```

### Common Queries

```sql
-- View all tables
\dt

-- Check data counts
SELECT table_name, COUNT(*) FROM information_schema.tables
WHERE table_schema = 'public' GROUP BY table_name;

-- Sample query: Pets with their owners
SELECT p.name as pet, o.name as owner
FROM patients p JOIN owners o ON p.owner_id = o.id;
```

## 🛡️ Security Features

- **Password Hashing**: No plain text passwords
- **Role-Based Access**: admin/veterinarian/assistant roles
- **Audit Trail**: Complete change history
- **Data Validation**: Constraints and checks

## 🐛 Troubleshooting

### "Permission Denied" Errors

```bash

## Fix file permissions

chmod 644 *.sql
chmod +x setup-db.sh
```

### Container Not Found

```bash

## Start the database

docker-compose up -d postgres

### Wait for it to be ready

sleep 10
```

## 🎯 Pro Tips

> "Always test your schema like you test your... business logic" - Kira

- Run `./setup-db.sh` after container restarts
- Use the audit_log for debugging data changes
- The seed data includes realistic clinic scenarios

---

### Built with 💊 by MrP4sten - Because even database scripts need personality
