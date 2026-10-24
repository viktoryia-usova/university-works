CREATE TABLE resource_favorites (
    profile_id  BIGINT NOT NULL REFERENCES profiles(id)            ON DELETE CASCADE,
    resource_id BIGINT NOT NULL REFERENCES financial_resources(id) ON DELETE CASCADE,
    created_at  TIMESTAMP NOT NULL DEFAULT now(),
    PRIMARY KEY (profile_id, resource_id)
);
