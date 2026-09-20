#Database
-PostgreSQL 17
-pgadmin 4
-pgvector

#tables

-ai_claim
-result


#ai_claim
Columns:
-ai_claim_id   (Primary Key) bigint
-ai_claim_text text
-created_at timestamp


#result
-result_id     (Primary Key)  bigint
-ai_claim_id   (Forign Key - ai_claim table) bigint
-verdict char
-score   numeric
-created_at timestamp