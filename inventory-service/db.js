const { Pool } = require('pg');

const pool = new Pool({
    connectionString: process.env.DATABASE_URL,
});

const connectWithRetry = async (retries = 5, delay = 5000) => {
    while (retries) {
        try {
            const client = await pool.connect();
            console.log('Successfully connected to the database');
            client.release();
            return;
        } catch (err) {
            console.error('Failed to connect to the database. Retrying...', err.message);
            retries -= 1;
            if (retries === 0) {
                console.error('Could not connect to the database after multiple retries. Exiting.');
                process.exit(1);
            }
            await new Promise(res => setTimeout(res, delay));
        }
    }
};

module.exports = {
    query: (text, params) => pool.query(text, params),
    connectWithRetry,
};

