# Cricket Multi-Tenant System (Ready)

This archive contains a runnable multi-module Spring Boot project.

Modules:

- multi-tenant-transaction-manager : library providing tenant routing (JNDI/JDBC)
- cricket-tournament-management : demo application using the library

Quickstart (local dev):

1. Prepare a master DB and run `master-db.sql` to create `tenants` table and sample tenants.
2. Create per-tenant DBs and run `schema.sql` & `data.sql` into each tenant DB.
3. Build and run:
   mvn -DskipTests clean install
   mvn -pl cricket-tournament-management spring-boot:run
4. Call APIs with header `X-Tenant-Id: tenant1` or `tenant2`.

Notes:

- Use JNDI names in `tenants.jndi_name` if running in app server with container-managed pools.
