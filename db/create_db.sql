BEGIN;

drop table IF EXISTS extracted_elements;
drop table IF EXISTS document;

CREATE TABLE "document"(
	"id"              Integer PRIMARY KEY AUTOINCREMENT,
	"pdf_file_name"   Text NOT NULL,
	"ocr_document_number" Text,
	"order"           Integer);

CREATE TABLE "extracted_elements"(
	"id" Integer PRIMARY KEY AUTOINCREMENT,
	"used" Boolean,
	"png" BLOB,
	"designation" Text,
	"part_number" Text,
	"product_characteristic" Text,
	"process_characteristic" Text,
	"nominal_value" Text,
	"lower_limit" Text,
	"upper_limit" Text,
	"unit" Text,
	"customer_classification" Text,
	"characteristic_type" Text,
	"optional_reason" Text,
	"draw_type" Text,
	"document_id" Integer,
	"inspection_method" Text,
	"rank" Integer,
	"img" Text,
	CONSTRAINT "lnk_document_extracted_elements" FOREIGN KEY ( "document_id" ) REFERENCES "document"( "id" ) ON DELETE CASCADE
 );

CREATE INDEX "index_document_id" ON "extracted_elements"( "document_id" );


COMMIT;
