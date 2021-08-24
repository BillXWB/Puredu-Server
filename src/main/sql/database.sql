-- Database: puredu

-- DROP DATABASE puredu;

CREATE DATABASE puredu
    WITH
    OWNER = postgres
    ENCODING = 'UTF8'
    TABLESPACE = pg_default
    CONNECTION LIMIT = -1;

COMMENT ON DATABASE puredu
    IS 'Puredu Database';
