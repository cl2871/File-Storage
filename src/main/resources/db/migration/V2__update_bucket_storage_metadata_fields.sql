
-- Add column bucket_name with not null constraint
-- A default is required; no default will lead to null values and fail the not null constraint
ALTER TABLE bucket_storage_metadata
ADD COLUMN bucket_name varchar(255) NOT NULL DEFAULT 'placeholder';

-- Drop the default for the bucket_name column
ALTER TABLE bucket_storage_metadata
ALTER COLUMN bucket_name DROP DEFAULT;

-- Rename the storage_location column to key_name
ALTER TABLE bucket_storage_metadata
RENAME storage_location TO key_name;