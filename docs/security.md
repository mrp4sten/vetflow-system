# VetFlow API Security Overview

## Authentication

- **Endpoint**: `POST /api/v1/auth/token`
- **Headers**: `Content-Type: application/json`
- **Body**:

  ```json
  {
    "username": "dr_rodriguez",
    "password": "<password>"
  }
  ```

- **Response**: `200 OK`

  ```json
  {
    "accessToken": "<jwt-token>",
    "expiresInSeconds": 3600
  }
  ```

Send the `accessToken` on subsequent calls using the `Authorization: Bearer <jwt-token>` header.

## Roles

| Role          | Key Capabilities                                    |
|---------------|------------------------------------------------------|
| `admin`       | Full access across the API.                          |
| `veterinarian`| Medical records, patient management, appointments.   |
| `assistant`   | Owner/patient onboarding and appointment scheduling. |

All endpoints require authentication unless explicitly documented otherwise.

## Auditing

Write operations on owners, patients, appointments, and medical records append entries to the `audit_log` table capturing the actor, operation, and before/after payloads.
