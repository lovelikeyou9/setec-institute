### Import `computer_store_final_java_iii.sql` in MySQL Workbench

1. Open **MySQL Workbench**.
2. Click your local connection (usually `Local instance` / `root@localhost`).
3. Enter password if asked → connect.
4. Menu bar: **Server** → **Data Import**.
5. Select **Import from Self-Contained File**.
6. Click the `...` button → choose  
   `computer_store_final_java_iii.sql`
7. Under **Default Target Schema**:
   - Click **New...**
   - Name it the same as in your app, e.g. `comupter_store_final_java_iii`  
     (or `computer_store_final_java_iii` if you also change `application.properties`)
   - Click **Create** / OK, then select that schema in the dropdown.
8. Bottom right → **Start Import**.
9. Wait until it finishes (log panel shows success).
10. Left sidebar → refresh schemas (refresh icon) → open the database → check **Tables**.

---

### After import

In `application.properties`, this must match the schema name you created:

```text
spring.datasource.url=jdbc:mysql://localhost:3306/YOUR_SCHEMA_NAME?...
spring.datasource.username=root
spring.datasource.password=REMOVED
```

(If password is empty, leave `password=` blank.)

---

### If import fails

- File is too large → **Edit** → **Preferences** → **SQL Editor** → raise **DBMS connection read timeout**, or use CMD `mysql` import instead.
- Wrong connection → make sure you connected to the same MySQL that runs on port **3306**.