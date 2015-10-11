DROP TABLE IF EXISTS repository_document;
CREATE TABLE repository_document(
  id VARCHAR (255),
  path varchar(2000),
  name VARCHAR(255),
  last_modified_date DATE,
  created_at_date DATE,
  version INTEGER ,
  mime_type VARCHAR(255),
  tags CLOB,
  binary BLOB,
  text_content CLOB
);