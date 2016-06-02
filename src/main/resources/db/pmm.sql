DROP TABLE IF EXISTS transactions;
DROP TABLE IF EXISTS accounts;
DROP TABLE IF EXISTS categories;
DROP TABLE IF EXISTS users_roles;
DROP TABLE IF EXISTS users;
DROP TABLE IF EXISTS roles;

CREATE TABLE roles (
  id   BIGSERIAL   NOT NULL PRIMARY KEY,
  name VARCHAR(32) NOT NULL UNIQUE
);

CREATE TABLE users (
  id       BIGSERIAL    NOT NULL PRIMARY KEY,
  email    VARCHAR(255) NOT NULL UNIQUE,
  password VARCHAR(255) NOT NULL,
  created  TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
  enable   BOOLEAN      NOT NULL DEFAULT TRUE
);

CREATE TABLE users_roles (
  user_id BIGINT NOT NULL,
  role_id BIGINT NOT NULL,
  CONSTRAINT "users_roles_id" PRIMARY KEY (user_id, role_id),
  FOREIGN KEY (user_id) REFERENCES users (id),
  FOREIGN KEY (role_id) REFERENCES roles (id)
);

CREATE TABLE categories (
  id        BIGSERIAL    NOT NULL PRIMARY KEY,
  name      VARCHAR(128) NOT NULL,
  operation VARCHAR(16)  NOT NULL,
  user_id   BIGINT       NOT NULL,
  FOREIGN KEY (user_id) REFERENCES users (id)
);

CREATE TABLE accounts (
  id       BIGSERIAL   NOT NULL PRIMARY KEY,
  name     VARCHAR(64) NOT NULL,
  balance  NUMERIC     NOT NULL,
  currency VARCHAR(3)  NOT NULL,
  user_id  BIGINT      NOT NULL,
  FOREIGN KEY (user_id) REFERENCES users (id)
);

CREATE TABLE transactions (
  id              BIGSERIAL   NOT NULL PRIMARY KEY,
  to_account_id   BIGINT,
  to_amount       NUMERIC,
  to_currency     VARCHAR(3),
  operation       VARCHAR(16) NOT NULL,
  category_id     BIGINT      NOT NULL,
  user_id         BIGINT      NOT NULL,
  from_account_id BIGINT,
  from_amount     NUMERIC,
  from_currency   VARCHAR(3),
  date            TIMESTAMP   NOT NULL,
  comment         VARCHAR(255),
  FOREIGN KEY (to_account_id) REFERENCES accounts (id),
  FOREIGN KEY (from_account_id) REFERENCES accounts (id),
  FOREIGN KEY (category_id) REFERENCES categories (id),
  FOREIGN KEY (user_id) REFERENCES users (id)
);