CREATE TABLE users (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    email VARCHAR(255) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    full_name VARCHAR(100) NOT NULL,
    phone VARCHAR(20),
    avatar_url VARCHAR(500),
    role VARCHAR(20) NOT NULL DEFAULT 'USER',
    created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE TABLE listings (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    owner_id BIGINT NOT NULL REFERENCES users (id) ON DELETE CASCADE,
    title VARCHAR(200) NOT NULL,
    description TEXT NOT NULL,
    property_type VARCHAR(20) NOT NULL,
    price_per_month BIGINT NOT NULL CHECK (price_per_month > 0),
    area_m2 NUMERIC(8, 2) NOT NULL CHECK (area_m2 > 0),
    bedrooms INT NOT NULL DEFAULT 0,
    bathrooms INT NOT NULL DEFAULT 0,
    address VARCHAR(255) NOT NULL,
    district VARCHAR(100) NOT NULL,
    city VARCHAR(100) NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    reject_reason VARCHAR(500),
    created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT now()
);
CREATE INDEX idx_listings_status ON listings (status);
CREATE INDEX idx_listings_city_district ON listings (city, district);
CREATE INDEX idx_listings_price ON listings (price_per_month);
CREATE INDEX idx_listings_owner ON listings (owner_id);

CREATE TABLE listing_images (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    listing_id BIGINT NOT NULL REFERENCES listings (id) ON DELETE CASCADE,
    url VARCHAR(500) NOT NULL,
    sort_order INT NOT NULL DEFAULT 0
);
CREATE INDEX idx_listing_images_listing ON listing_images (listing_id);

CREATE TABLE listing_amenities (
    listing_id BIGINT NOT NULL REFERENCES listings (id) ON DELETE CASCADE,
    amenity VARCHAR(50) NOT NULL,
    PRIMARY KEY (listing_id, amenity)
);

CREATE TABLE rental_requests (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    listing_id BIGINT NOT NULL REFERENCES listings (id) ON DELETE CASCADE,
    tenant_id BIGINT NOT NULL REFERENCES users (id) ON DELETE CASCADE,
    message VARCHAR(1000),
    move_in_date DATE NOT NULL,
    duration_months INT NOT NULL CHECK (duration_months BETWEEN 1 AND 60),
    status VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT now()
);
CREATE INDEX idx_rental_requests_listing ON rental_requests (listing_id);
CREATE INDEX idx_rental_requests_tenant ON rental_requests (tenant_id);
CREATE UNIQUE INDEX uq_rental_requests_pending ON rental_requests (listing_id, tenant_id) WHERE status = 'PENDING';

CREATE TABLE favorites (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    user_id BIGINT NOT NULL REFERENCES users (id) ON DELETE CASCADE,
    listing_id BIGINT NOT NULL REFERENCES listings (id) ON DELETE CASCADE,
    created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    CONSTRAINT uq_favorites_user_listing UNIQUE (user_id, listing_id)
);