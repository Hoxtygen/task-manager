CREATE TABLE IF NOT EXISTS tasks (
    id SERIAL PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    description TEXT NOT NULL,
    status VARCHAR(255) NOT NULL,
    due_date TIMESTAMP NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- CREATE
-- OR REPLACE FUNCTION update_timestamp() RETURNS TRIGGER AS $$ BEGIN NEW.updated_at = CURRENT_TIMESTAMP;

-- RETURN NEW;

-- END;

-- $$ LANGUAGE plpgsql;

-- CREATE TRIGGER update_taskmanager_updated_at BEFORE
-- UPDATE
--     ON taskmanager FOR EACH ROW EXECUTE FUNCTION update_timestamp();