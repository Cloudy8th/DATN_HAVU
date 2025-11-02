import pg from "pg";

const pool = new pg.Pool({
  host: process.env.DB_HOST,
  port: process.env.DB_PORT || 5432,
  user: process.env.DB_USER,
  password: process.env.DB_PASSWORD,
  database: process.env.DB_NAME,
  max: 10, // connection pool size
  idleTimeoutMillis: 10000,
  connectionTimeoutMillis: 5000,
});

export async function runReadOnly(sql, params = [], queryTimeoutMs = 6000) {
  const client = await pool.connect();
  try {
    // Bắt buộc transaction ở chế độ read only (safety)
    await client.query("BEGIN READ ONLY");

    // Set timeout query
    await client.query(`SET LOCAL statement_timeout = '${queryTimeoutMs}ms'`);

    const result = await client.query(sql, params);

    await client.query("COMMIT");

    return {
      rowCount: result.rowCount,
      rows: result.rows,
      columns: result.fields?.map((f) => f.name),
    };
  } catch (err) {
    await client.query("ROLLBACK");
    console.error("[PG ERROR]", err);
    throw err;
  } finally {
    client.release();
  }
}
