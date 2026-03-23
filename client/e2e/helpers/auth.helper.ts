import { type Page, type APIRequestContext } from '@playwright/test'

const BASE_URL = process.env.BASE_URL || 'http://localhost:3000'
const API_URL = `${BASE_URL}/api`

let userCounter = 0

export function uniqueEmail(): string {
  userCounter++
  return `testuser_${Date.now()}_${userCounter}@test.ru`
}

export interface TestUser {
  fullName: string
  email: string
  password: string
  phone: string | null
  city: string | null
  interests: string | null
  birthDate: string
}

export function generateTestUser(overrides?: Partial<TestUser>): TestUser {
  return {
    fullName: `Тест Пользователь ${Date.now()}`,
    email: uniqueEmail(),
    password: 'TestPass123!',
    phone: null,
    city: 'Москва',
    interests: 'Тестирование',
    birthDate: '2000-01-15',
    ...overrides,
  }
}

export async function registerViaApi(ctx: APIRequestContext, user: TestUser) {
  const res = await ctx.post(`${API_URL}/auth/register`, {
    data: {
      fullName: user.fullName,
      email: user.email,
      phone: user.phone,
      password: user.password,
      city: user.city,
      interests: user.interests,
      birthDate: user.birthDate,
    },
  })
  return res
}

export async function loginViaApi(ctx: APIRequestContext, login: string, password: string) {
  const res = await ctx.post(`${API_URL}/auth/login`, {
    data: { login, password },
  })
  return res
}

export async function registerAndGetToken(ctx: APIRequestContext, user: TestUser): Promise<string> {
  await registerViaApi(ctx, user)
  const loginRes = await loginViaApi(ctx, user.email, user.password)
  const body = await loginRes.json()
  return body.token
}

export async function loginAsNewUser(page: Page): Promise<TestUser> {
  const ctx = page.request
  const user = generateTestUser()
  const token = await registerAndGetToken(ctx, user)
  await page.goto('/login')
  await page.waitForSelector('#login', { timeout: 10000 })
  await page.evaluate((t) => localStorage.setItem('token', t), token)
  await page.goto('/')
  await page.waitForURL('**/clubs')
  return user
}

export async function loginViaUi(page: Page, login: string, password: string) {
  await page.goto('/login')
  await page.locator('#login').fill(login)
  await page.locator('#password input').fill(password)
  await page.locator('button[type="submit"]').click()
}

export async function loginAsAdmin(page: Page): Promise<string> {
  const ctx = page.request
  const loginRes = await loginViaApi(ctx, 'admin@club.ru', 'admin123')
  const body = await loginRes.json()
  const token = body.token as string
  await page.goto('/login')
  await page.waitForLoadState('domcontentloaded')
  await page.evaluate((t) => localStorage.setItem('token', t), token)
  await page.goto('/')
  await page.waitForURL('**/clubs')
  return token
}
