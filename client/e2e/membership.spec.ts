import { test, expect } from '@playwright/test'
import {
  loginAsNewUser,
  loginAsAdmin,
  generateTestUser,
  registerAndGetToken,
} from './helpers/auth.helper'

test.describe('Членство в клубах', () => {
  let clubId: number
  let clubName: string

  test.beforeAll(async ({ request }) => {
    const loginRes = await request.post('/api/auth/login', {
      data: { login: 'admin@club.ru', password: 'admin123' },
    })
    expect(loginRes.ok()).toBeTruthy()
    const { token } = await loginRes.json()

    clubName = `Клуб членства ${Date.now()}`
    const createRes = await request.post('/api/v1/clubs', {
      headers: { Authorization: `Bearer ${token}` },
      data: {
        name: clubName,
        description: 'Клуб для тестирования членства',
        categoryId: 2,
        ageLimitMin: null,
        ageLimitMax: null,
        membershipFee: null,
      },
    })
    expect(createRes.ok()).toBeTruthy()
    const club = await createRes.json()
    clubId = club.id
  })

  test('Подача заявки на вступление в клуб', async ({ page }) => {
    const user = await loginAsNewUser(page)

    await page.goto(`/clubs/${clubId}`)
    await page.waitForSelector(`text=${clubName}`, { timeout: 10000 })

    const applyButton = page.locator('button:has-text("Подать заявку")')
    await expect(applyButton).toBeVisible({ timeout: 5000 })
    await applyButton.click()

    await page.waitForURL(`**/clubs/${clubId}/apply`, { timeout: 10000 })
    await page.waitForSelector('text=Подача заявки', { timeout: 10000 })

    await page.locator('textarea').fill('Хочу вступить в этот замечательный клуб!')
    await page.locator('button[type="submit"]').click()

    await page.waitForURL(`**/clubs/${clubId}`, { timeout: 10000 })
  })

  test('Просмотр заявок клуба (админ)', async ({ page }) => {
    await loginAsAdmin(page)

    await page.goto(`/clubs/${clubId}/applications`)
    await expect(page.locator('h1, h2').filter({ hasText: /заявк/i }).first()).toBeVisible({ timeout: 10000 })
  })

  test('Просмотр участников клуба', async ({ page }) => {
    await loginAsAdmin(page)

    await page.goto(`/clubs/${clubId}/members`)
    await expect(page.locator('h1, h2').filter({ hasText: /участник|член/i }).first()).toBeVisible({ timeout: 10000 })
  })

  test('Одобрение заявки на вступление (админ)', async ({ page, request }) => {
    const user = generateTestUser()
    const userToken = await registerAndGetToken(request, user)

    const applyRes = await request.post(`/api/v1/clubs/${clubId}/applications`, {
      headers: { Authorization: `Bearer ${userToken}` },
      data: { comment: 'Хочу вступить!' },
    })
    expect(applyRes.ok()).toBeTruthy()

    await loginAsAdmin(page)
    await page.goto(`/clubs/${clubId}/applications`)

    const approveBtn = page.locator('button:has-text("Одобрить")').first()
    await expect(approveBtn).toBeVisible({ timeout: 5000 })
    await approveBtn.click()

    const dialog = page.locator('[role="dialog"], .p-dialog')
    await expect(dialog).toBeVisible({ timeout: 5000 })
    await dialog.locator('textarea').fill('Добро пожаловать!')
    await dialog.locator('button:has-text("Одобрить")').click()

    await expect(page.locator('.p-toast-message, [data-pc-name="toast"]').first()).toBeVisible({ timeout: 5000 })
  })

  test('Отклонение заявки на вступление (админ)', async ({ page, request }) => {
    const user = generateTestUser()
    const userToken = await registerAndGetToken(request, user)

    const applyRes = await request.post(`/api/v1/clubs/${clubId}/applications`, {
      headers: { Authorization: `Bearer ${userToken}` },
      data: { comment: 'Хочу вступить!' },
    })
    expect(applyRes.ok()).toBeTruthy()

    await loginAsAdmin(page)
    await page.goto(`/clubs/${clubId}/applications`)

    const rejectBtn = page.locator('button:has-text("Отклонить")').first()
    await expect(rejectBtn).toBeVisible({ timeout: 5000 })
    await rejectBtn.click()

    const dialog = page.locator('[role="dialog"], .p-dialog')
    await expect(dialog).toBeVisible({ timeout: 5000 })
    await dialog.locator('textarea').fill('К сожалению, не подходите')
    await dialog.locator('button:has-text("Отклонить")').click()

    await expect(page.locator('.p-toast-message, [data-pc-name="toast"]').first()).toBeVisible({ timeout: 5000 })
  })

  test('Покинуть клуб (активный участник)', async ({ page, request }) => {
    const user = generateTestUser()
    const userToken = await registerAndGetToken(request, user)

    const applyRes = await request.post(`/api/v1/clubs/${clubId}/applications`, {
      headers: { Authorization: `Bearer ${userToken}` },
      data: { comment: 'Хочу вступить!' },
    })
    expect(applyRes.ok()).toBeTruthy()
    const application = await applyRes.json()

    const loginRes = await request.post('/api/auth/login', {
      data: { login: 'admin@club.ru', password: 'admin123' },
    })
    expect(loginRes.ok()).toBeTruthy()
    const { token: adminTok } = await loginRes.json()
    const reviewRes = await request.put(`/api/v1/applications/${application.id}/review`, {
      headers: { Authorization: `Bearer ${adminTok}` },
      data: { decision: 'APPROVED', comment: null },
    })
    expect(reviewRes.ok()).toBeTruthy()

    await page.goto('/login')
    await page.waitForSelector('#login', { timeout: 10000 })
    await page.evaluate((t) => localStorage.setItem('token', t), userToken)
    await page.goto(`/clubs/${clubId}`)
    await page.waitForSelector(`text=${clubName}`, { timeout: 10000 })

    const leaveBtn = page.locator('button:has-text("Покинуть клуб")')
    await expect(leaveBtn).toBeVisible({ timeout: 5000 })
    await leaveBtn.click()

    await expect(page.locator('.p-toast-message, [data-pc-name="toast"]').first()).toBeVisible({ timeout: 5000 })
  })
})
