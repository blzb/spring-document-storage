DROP TABLE IF EXISTS repository_document;
CREATE TABLE repository_document (
  id                 VARCHAR(255),
  path               VARCHAR(2000),
  name               VARCHAR(255),
  last_modified_date DATE    DEFAULT CURRENT_TIMESTAMP(),
  created_at_date    DATE    DEFAULT CURRENT_TIMESTAMP(),
  version            INTEGER DEFAULT 1,
  mime_type          VARCHAR(255),
  tags               CLOB,
  binary             BLOB,
  text_content       CLOB
);