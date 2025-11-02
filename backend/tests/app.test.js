const request = require('supertest')
const app = require('../app')

describe('App Health Check', () => {
  it('should return health status', async () => {
    const response = await request(app)
      .get('/health')
      .expect(200)

    expect(response.body.status).toBe('OK')
    expect(response.body.message).toBe('SummerVeld Hound Resort API is running')
    expect(response.body.timestamp).toBeDefined()
    expect(response.body.version).toBeDefined()
  })

  it('should return 404 for non-existent routes', async () => {
    const response = await request(app)
      .get('/api/nonexistent')
      .expect(404)

    expect(response.body.success).toBe(false)
    expect(response.body.message).toBe('Route not found')
  })

  it('should handle CORS preflight requests', async () => {
    const response = await request(app)
      .options('/api/auth/login')
      .expect(204)

    expect(response.headers['access-control-allow-origin']).toBeDefined()
  })

  it('should return proper error for malformed JSON', async () => {
    const response = await request(app)
      .post('/api/auth/login')
      .set('Content-Type', 'application/json')
      .send('invalid json')
      .expect(400)

    expect(response.body.success).toBe(false)
  })
})
