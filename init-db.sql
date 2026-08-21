-- Creates one database per microservice, simulating database-per-service
-- on a single local Postgres container.

CREATE DATABASE circulation_db;
CREATE DATABASE payment_db;
CREATE DATABASE notification_db;

-- catalog_db already exists via POSTGRES_DB env var in docker-compose.yml
