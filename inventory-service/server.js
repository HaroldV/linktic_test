const express = require('express');
const axios = require('axios');
const db = require('./db');
const swaggerJsdoc = require('swagger-jsdoc');
const swaggerUi = require('swagger-ui-express');

const app = express();
const PORT = 3000;
const API_KEY = process.env.INVENTORY_API_KEY;

app.use(express.json());

const swaggerOptions = {
    definition: {
        openapi: '3.0.0',
        info: {
            title: 'Inventory Microservice API',
            version: '1.0.0',
            description: 'API for managing product inventories, following JSON API standard.',
        },
    },
    servers: [
        {
            url: `http://localhost:${PORT}/api/v1`,
        },
    ],
    apis: ['./server.js'],
}

const swaggerDocs = swaggerJsdoc(swaggerOptions);
app.use('/api-docs', swaggerUi.serve, swaggerUi.setup(swaggerDocs));

const PRODUCT_SERVICE_URL = 'http://product-service:8080/api/v1/products';


const authMiddleware = (req, res, next) => {
    const apiKey = req.headers['x-api-key'];
    if (apiKey && apiKey === API_KEY) {
        next();
    } else {
        res.status(401).send("Unauthorized");
    }
}

// Endpoints

/**
 * @swagger
 * /api/v1/inventories/{productId}:
 * get:
 * summary: Get a product's inventory count
 * parameters:
 * - in: path
 * name: productId
 * required: true
 * schema:
 * type: integer
 * description: The product ID
 * responses:
 * '200':
 * description: Inventory details for a product
 * content:
 * application/json:
 * schema:
 * type: object
 * properties:
 * data:
 * type: object
 * properties:
 * type:
 * type: string
 * example: inventories
 * id:
 * type: string
 * example: "123"
 * attributes:
 * type: object
 * properties:
 * productId:
 * type: integer
 * example: 123
 * productName:
 * type: string
 * example: "Smartphone"
 * quantity:
 * type: integer
 * example: 50
 * '404':
 * description: Product or inventory not found
 */
app.get('/api/v1/inventories/:productId', async (req, res) => {
    const { productId } = req.params;
    try {
        // fetch product details from product service
        const productResponse = await axios.get(`${PRODUCT_SERVICE_URL}/${productId}`, {
            headers: { 'X-API-Key': API_KEY, 'Content-Type': 'application/json' }
        });
        const productData = productResponse.data.data.attributes;


        const result = await db.query('SELECT quantity FROM inventories WHERE product_id = $1', [productId]);
        const quantity = result.rows.length > 0 ? result.rows[0].quantity : 0;

        const response = {
            data: {
                type: "inventories",
                id: productId,
                attributes: {
                    productId: parseInt(productId),
                    productName: productData.name,
                    quantity: quantity,
                },
            },
        };
        res.status(200).json(response);
    } catch (error) {
        console.error('Error fetching inventory:', error.message);
        res.status(404).json({ error: 'Product or inventory not found' });
    }
});

/**
 * @swagger
 * /api/v1/inventories/{productId}:
 *   put:
 *     summary: Update a product's inventory quantity
 *     security:
 *       - ApiKeyAuth: []
 *     parameters:
 *       - in: path
 *         name: productId
 *         required: true
 *         schema:
 *           type: integer
 *         description: The product ID
 *     requestBody:
 *       required: true
 *       content:
 *         application/json:
 *           schema:
 *             type: object
 *             properties:
 *               quantity:
 *                 type: integer
 *                 example: 100
 *     responses:
 *       '200':
 *         description: Inventory updated successfully
 *         content:
 *           application/json:
 *             schema:
 *               type: object
 *               properties:
 *                 message:
 *                   type: string
 *                   example: Inventory updated successfully
 *       '500':
 *         description: Failed to update inventory
 */
app.put('/api/v1/inventories/:productId', authMiddleware, async (req, res) => {
    const { productId } = req.params;
    const { quantity } = req.body;

    try {
        // Try to update the inventory
        const updateResult = await db.query(
            'UPDATE inventories SET quantity = $1 WHERE product_id = $2 RETURNING *',
            [quantity, productId]
        );

        // If no rows were updated, insert a new record
        if (updateResult.rowCount === 0) {
            await db.query('INSERT INTO inventories (product_id, quantity) VALUES ($1, $2)', [productId, quantity]);
        }

        console.log(`Inventory changed for product ID: ${productId}, new quantity: ${quantity}`);
        res.status(200).json({ message: 'Inventory updated successfully' });
    } catch (error) {
        console.error('Error updating inventory:', error);
        res.status(500).json({ error: 'Failed to update inventory' });
    }
});

app.listen(PORT, () => {
    console.log(`Inventory service listening on port ${PORT}`);
});

