# Architecture Diagram

+------------------------+ +-------------------------------+
| Client | X-Tenant | TenantFilter |
| (Postman/cURL/UI)      | + Headers | -> TenantContext(ThreadLocal)|
+-----------+------------+ +-------------------------------+
| |
| v
| +-----------------------------+
| | MultiTenantDataSource |
| | (AbstractRoutingDataSource) |
| +--------------+--------------+
| |
v v
+------------------------+ +-----------------------------+
| Spring Data JPA / | uses DS --->  | TenantRepository |
| Repositories/Services | | (queries master.tenants)    |
+------------------------+ +--------------+--------------+
/ \
/   \
v v
+---------------+---------------+
| Tenant DBs (Postgres/MySQL/ |
| Oracle via JNDI or JDBC)    |
+-------------------------------+
