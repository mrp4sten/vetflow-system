# VetFlow - Database Design Document

## 🎯 Objective

Management system for veterinary clinics allowing patient control, appointments and medical history tracking with security by design principles.

## 🗃️ Entity Relationship Diagram

![Database ER Diagram](./database_er_diagram.png)

## 📊 Core Tables Description

### owners

Stores pet owners information with contact details and activity tracking.

### patients  

Contains pets data and their relationship with owners. Includes medical basics like weight and allergies.

### medical_records

Tracks complete medical history for each patient visit with veterinarian attribution.

### appointments

Manages scheduling system with priority and status tracking.

### system_users

Stores clinic staff accounts with role-based access control and authentication.

### audit_log

Tracks all data changes across the system for security and compliance.

## 🔗 Relationships

### Business Relationships

- One owner can have MULTIPLE patients (1:N)
- One patient can have MULTIPLE medical records (1:N)  
- One patient can have MULTIPLE appointments (1:N)

### Security Relationships

- One system_user can create MULTIPLE medical_records (1:N)
- One system_user can trigger MULTIPLE audit_log entries (1:N)
- Each core table (owners, patients, appointments, medical_records) can have MULTIPLE audit_log entries (1:N)

## 🔒 Security by Design

### Authentication & Authorization

- **system_users** table with hashed passwords (never plain text)
- Role-based access control (admin, veterinarian, assistant)
- User activity tracking and session management

### Audit & Compliance

- **audit_log** table tracks all data modifications
- Records WHO changed WHAT, WHEN, and WHAT VALUES changed
- Essential for debugging, security incidents, and legal compliance

### Data Protection

- Sensitive operations require authentication
- User-specific data visibility based on roles
- Complete change history for critical data

## 🚀 Performance Considerations

### Indexing Strategy

- Indexes on all foreign keys for fast joins
- Indexes on frequently searched fields (dates, emails, usernames)
- Partial indexes for active records (is_active fields)
- Composite indexes for common query patterns

### Data Integrity

- Check constraints for email and phone validation
- Foreign key constraints with appropriate delete/update rules
- Unique constraints on business-critical fields

## 📈 Scalability Considerations

- Audit log designed for high-write scenarios
- Role-based views ready for multi-tenant architecture
- Flexible enough for future clinic branches expansion
