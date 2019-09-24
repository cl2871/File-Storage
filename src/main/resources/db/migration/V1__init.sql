
CREATE TABLE filestorage.bucket_storage_metadata
(
  id uuid NOT NULL PRIMARY KEY,
  created_at timestamp with time zone NOT NULL,
  updated_at timestamp with time zone NOT NULL,
  storage_location varchar(255),
  storage_provider varchar(255)
);