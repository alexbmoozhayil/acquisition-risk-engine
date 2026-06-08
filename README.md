Acquisition Risk Engine

A Java Spring Boot service for ingesting public defense contract data from USAspending, storing it in PostgreSQL, and producing explainable vendor risk scores.

This project is a Java companion to my Acquisition Intel project. Acquisition Intel focused on RAG, semantic search, graph workflows, and AI-powered contract analysis. Acquisition Risk Engine focuses on Java, Spring Boot, SQL, backend API design, database migrations, ingestion, and deterministic risk scoring.

Purpose

The goal of this project is to simulate a defense acquisition workflow:

Given a vendor and its contract history, identify review signals an acquisition analyst may want to inspect.

The system does not claim that a vendor is unsafe or risky in an absolute sense. Instead, it surfaces explainable signals based on observed contract data.

Example questions the service can answer:

What contracts are in the database?
Which vendors have the highest total award value?
What contracts belong to a specific vendor?
Why did a vendor receive a particular risk score?
What evidence supports each risk signal?
Tech Stack
Java 17
Spring Boot
Maven
PostgreSQL
Docker Compose
Flyway
JDBC / JdbcTemplate
JUnit 5
Mockito
USAspending.gov API
Current Features
Health check endpoint
PostgreSQL database running in Docker
Flyway-managed schema migrations
Seed contract data
USAspending API ingestion
Vendor summary API
Contract listing API
Vendor contract history API
Vendor risk scoring API
Unit tests for risk scoring logic
Rule-based risk engine with separate rule classes
Architecture
USAspending.gov API
        ↓
UsaSpendingClient
        ↓
UsaSpendingIngestionService
        ↓
ContractIngestionRepository
        ↓
PostgreSQL
        ↓
Spring Boot REST APIs
        ↓
Vendor and contract risk responses

The project separates responsibilities across layers:

Controllers  → handle HTTP requests
Services     → orchestrate business logic
Repositories → query or write PostgreSQL data
Risk rules   → evaluate contract history and produce explainable signals
Database Tables

The initial schema includes:

agencies
vendors
contracts
flyway_schema_history

Flyway manages schema creation through versioned SQL migration files.

Current migrations:

V1__create_initial_tables.sql
V2__seed_sample_contracts.sql
Running the Project

Start PostgreSQL:

docker compose up -d

Run the Spring Boot app:

./mvnw spring-boot:run

The app runs on:

http://localhost:8080
API Endpoints
Health Check
GET /health

Example:

curl -s http://localhost:8080/health

Response:

{
  "status": "ok"
}
List Contracts
GET /contracts

Example:

curl -s http://localhost:8080/contracts | python3 -m json.tool

Returns contract records joined with vendor and agency names.

List Vendors
GET /vendors

Example:

curl -s http://localhost:8080/vendors | python3 -m json.tool

Returns vendor summaries including contract count and total award amount.

Example response:

[
  {
    "id": 6,
    "name": "LOCKHEED MARTIN CORPORATION",
    "contractCount": 4,
    "totalAwardAmount": 123949443292.22
  }
]
Vendor Contract History
GET /vendors/{vendorId}/contracts

Example:

curl -s http://localhost:8080/vendors/6/contracts | python3 -m json.tool

Returns only the contracts associated with one vendor.

Vendor Risk Score
GET /vendors/{vendorId}/risk

Example:

curl -s http://localhost:8080/vendors/6/risk | python3 -m json.tool

Example response:

{
  "vendorId": 6,
  "vendorName": "LOCKHEED MARTIN CORPORATION",
  "overallRiskScore": 70,
  "riskLevel": "HIGH",
  "signals": [
    {
      "name": "HIGH_VALUE_CONTRACT",
      "severity": "HIGH",
      "scoreImpact": 35,
      "explanation": "Vendor has at least one contract above $200M.",
      "evidence": "N0001917C0001 is worth $35,135,514,910.20."
    },
    {
      "name": "MULTIPLE_CONTRACTS",
      "severity": "MEDIUM",
      "scoreImpact": 15,
      "explanation": "Vendor has multiple contracts in the database.",
      "evidence": "Vendor has 4 contracts."
    },
    {
      "name": "ENDED_CONTRACT",
      "severity": "MEDIUM",
      "scoreImpact": 20,
      "explanation": "Vendor has at least one contract with an end date in the past.",
      "evidence": "N0001902C3002 ended on 2022-12-16."
    }
  ]
}
Ingest USAspending Data
POST /ingest/usaspending

Example:

curl -s -X POST http://localhost:8080/ingest/usaspending | python3 -m json.tool

Example response:

{
  "recordsFetched": 10,
  "recordsSaved": 10
}

This endpoint calls the USAspending Advanced Award Search API, fetches Department of Defense contract awards, parses the JSON response, and upserts agencies, vendors, and contracts into PostgreSQL.

Risk Scoring

The risk engine is deterministic and explainable.

Current risk signals include:

HIGH_VALUE_CONTRACT
MULTIPLE_CONTRACTS
ENDED_CONTRACT
SPARSE_DESCRIPTION

Each signal includes:

name
severity
score impact
explanation
evidence

Example:

{
  "name": "HIGH_VALUE_CONTRACT",
  "severity": "HIGH",
  "scoreImpact": 35,
  "explanation": "Vendor has at least one contract above $200M.",
  "evidence": "N0001917C0001 is worth $35,135,514,910.20."
}

The score is capped at 100.

Risk levels:

0-39   LOW
40-69  MEDIUM
70-100 HIGH
Risk Rule Design

The risk engine uses a rule interface:

RiskRule

Each rule is implemented as its own class:

HighValueContractRiskRule
MultipleContractsRiskRule
EndedContractRiskRule
SparseDescriptionRiskRule

RiskService orchestrates the rules instead of containing all rule logic directly.

This makes the system easier to extend. Adding a new risk signal mostly requires adding a new class that implements RiskRule.

Testing

Run tests:

./mvnw test

The project includes unit tests for RiskService.

The tests use Mockito to mock repositories so risk scoring can be tested without starting the server or connecting to PostgreSQL.

The tests cover:

high-risk vendor scenario
low-risk vendor scenario
missing vendor 404 behavior
Why This Project Exists

This project was built for Govini interview preparation.

Govini works in the defense acquisition space, and this project is designed to demonstrate:

Java backend development
Spring Boot REST APIs
SQL and PostgreSQL
database migrations
external API ingestion
clean service/repository/controller layering
deterministic and explainable business logic
unit testing
defense acquisition domain familiarity
Interview Explanation

I built Acquisition Risk Engine as a Java Spring Boot companion to my Acquisition Intel project. Acquisition Intel focused on AI workflows like RAG, semantic search, and knowledge graphs. This project focuses on Java backend engineering.

The service ingests real Department of Defense contract award data from USAspending, normalizes agencies, vendors, and contracts into PostgreSQL, and exposes REST APIs for contract search, vendor summaries, vendor contract history, and explainable vendor risk scoring.

The risk engine is intentionally deterministic. Instead of asking an LLM to invent a score, the Java service applies auditable rules over contract history and returns evidence for each signal. This makes the output easier to test, explain, and trust.

Future Improvements

Potential next steps:

Add pagination to /contracts and /vendors
Add query filters by vendor name or award amount
Add more risk rules
Add integration tests with Testcontainers
Add GitHub Actions CI
Add a React frontend
Add a Python RAG explanation service for natural-language summaries
Rename recordsSaved to recordsUpserted for more precise ingestion reporting