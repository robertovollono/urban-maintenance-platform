-- 1. Abilita estensione spaziale (FONDAMENTALE)
CREATE EXTENSION IF NOT EXISTS postgis;

-- 2. Crea gli schemi per i Tenant (Comuni)
CREATE SCHEMA IF NOT EXISTS schema_salerno;
CREATE SCHEMA IF NOT EXISTS schema_napoli;

-- 3. Definizione della tabella Tickets (da replicare per ogni schema)
-- Postgres non supporta l'ereditarietà delle tabelle tra schemi facilmente,
-- quindi definiamo la struttura per ogni tenant.

-- === SALERNO ===
CREATE TABLE IF NOT EXISTS schema_salerno.tickets (
    id BIGSERIAL PRIMARY KEY,
    creator_id VARCHAR(255) NOT NULL,
    category VARCHAR(255) NOT NULL,
    description VARCHAR(1000) NOT NULL,
    status VARCHAR(50) NOT NULL,
    image_storage_key VARCHAR(255),
    location geometry(Point, 4326), -- Colonna Spaziale PostGIS
    created_at TIMESTAMP,
    updated_at TIMESTAMP
);

-- Index spaziale per velocizzare le query geografiche su Salerno
CREATE INDEX IF NOT EXISTS idx_tickets_location_salerno 
ON schema_salerno.tickets USING GIST (location);


-- === NAPOLI ===
CREATE TABLE IF NOT EXISTS schema_napoli.tickets (
    id BIGSERIAL PRIMARY KEY,
    creator_id VARCHAR(255) NOT NULL,
    category VARCHAR(255) NOT NULL,
    description VARCHAR(1000) NOT NULL,
    status VARCHAR(50) NOT NULL,
    image_storage_key VARCHAR(255),
    location geometry(Point, 4326),
    created_at TIMESTAMP,
    updated_at TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_tickets_location_napoli 
ON schema_napoli.tickets USING GIST (location);


-- === PUBLIC (Default/Fallback) ===
CREATE TABLE IF NOT EXISTS public.tickets (
    id BIGSERIAL PRIMARY KEY,
    creator_id VARCHAR(255) NOT NULL,
    category VARCHAR(255) NOT NULL,
    description VARCHAR(1000) NOT NULL,
    status VARCHAR(50) NOT NULL,
    image_storage_key VARCHAR(255),
    location geometry(Point, 4326),
    created_at TIMESTAMP,
    updated_at TIMESTAMP
);