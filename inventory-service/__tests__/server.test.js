const request = require('supertest');
const sinon = require('sinon');
const axios = require('axios');
const db = require('../db');
const { app } = require('../server'); // Assuming server.js exports the Express app

describe('Inventory Service API', () => {
    let dbQueryStub;
    let axiosGetStub;

    beforeEach(() => {
        dbQueryStub = sinon.stub(db, 'query');
        axiosGetStub = sinon.stub(axios, 'get');
    });

    afterEach(() => {
      sinon.restore();
    });

    it('should return inventory and product details for a valid product ID', async () => {
        const productId  = 1;
        const inventoryQuantity = 50;

          // Simula la respuesta de la base de datos
        dbQueryStub.withArgs('SELECT quantity FROM inventories WHERE product_id = $1', [productId])
                   .returns(Promise.resolve({ rows: [{ quantity: inventoryQuantity }] }));

        // Simula la respuesta del microservicio de productos (JSON API)
        const productResponse = {
            data: {
                data: {
                    id: '1',
                    type: 'products',
                    attributes: { name: 'Smartphone', price: 699.99 }
                }
            }
        };
        axiosGetStub.withArgs(`http://product-service:8080/api/v1/products/${productId}`).returns(Promise.resolve(productResponse));

        // Realiza la petición con supertest
        const response = await request(app).get(`/api/v1/inventories/${productId}`);

        // Verificaciones
        expect(response.statusCode).toBe(200);
        expect(response.body.data.attributes.quantity).toBe(inventoryQuantity);
        expect(response.body.data.attributes.productName).toBe('Smartphone');
        expect(dbQueryStub.calledOnce).toBe(true);
        expect(axiosGetStub.calledOnce).toBe(true);
    });

    it('should return 404 if product is not found', async () => {
            const productId = 999;

            // Simula el error del microservicio de productos
            axiosGetStub.withArgs(`http://product-service:8080/api/v1/products/${productId}`)
                        .returns(Promise.reject(new Error('Product not found')));

            // Realiza la petición
            const response = await request(app).get(`/api/v1/inventories/${productId}`);

            // Verificaciones
            expect(response.statusCode).toBe(404);
            expect(response.body).toHaveProperty('error', 'Product or inventory not found');
            expect(axiosGetStub.calledOnce).toBe(true);
        });

        // Pruebas de integración para el endpoint PUT
        it('should update the product quantity and log a message', async () => {
            const productId = 1;
            const newQuantity = 45;
            const authKey = 'your-secret-api-key'; // Asegúrate de que coincida con el de tu código

            // Simula una actualización exitosa de la DB
            dbQueryStub.returns(Promise.resolve({ rowCount: 1 }));

            // Espía el método console.log para verificar que se llamó
            const consoleSpy = sinon.spy(console, 'log');

            const response = await request(app)
                .put(`/api/v1/inventories/${productId}`)
                .set('x-api-key', authKey)
                .send({ quantity: newQuantity });

            expect(response.statusCode).toBe(200);
            expect(response.body.message).toBe('Inventory updated successfully');
            expect(dbQueryStub.calledOnce).toBe(true);
            expect(consoleSpy.calledWith(`Inventory changed for product ID: ${productId}, new quantity: ${newQuantity}`)).toBe(true);
        });
});
