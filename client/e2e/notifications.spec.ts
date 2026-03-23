import { test, expect } from '@playwright/test'
import {
  loginAsNewUser,
  loginAsAdmin,
  generateTestUser,
  registerAndGetToken,
} from './helpers/auth.helper'

test.describe('Уведомления', () => {
  test('Просмотр страницы уведомлений', async ({ page }) => {
    await loginAsNewUser(page)

    await page.goto('/notifications')
    await expect(page.locator('h1:has-text("Уведомления")')).toBeVisible({ timeout: 10000 })
  })

  test('Пустой список уведомлений — показывает заглушку', async ({ page }) => {
    await loginAsNewUser(page)

    await page.goto('/notifications')
    await expect(page.locator('h1:has-text("Уведомления")')).toBeVisible({ timeout: 10000 })

    await expect(page.locator('text=Нет уведомлений')).toBeVisible({ timeout: 5000 })
  })

  test('Колокольчик уведомлений в хедере', async ({ page }) => {
    await loginAsNewUser(page)

    await page.goto('/clubs')
    await expect(page.locator('h1:has-text("Клубы")')).toBeVisible({ timeout: 10000 })

    await expect(page.locator('button:has(.pi-bell)').first()).toBeVisible()
  })

  test('Открытие dropdown по клику на колокольчик', async ({ page }) => {
    await loginAsNewUser(page)

    await page.goto('/clubs')
    await expect(page.locator('h1:has-text("Клубы")')).toBeVisible({ timeout: 10000 })

    const bell = page.locator('button:has(.pi-bell)').first()
    await bell.click()

    await expect(page.locator('button:has-text("Все уведомления")')).toBeVisible({ timeout: 5000 })
  })

  test('Переход на "Все уведомления" из dropdown', async ({ page }) => {
    await loginAsNewUser(page)

    await page.goto('/clubs')
    await expect(page.locator('h1:has-text("Клубы")')).toBeVisible({ timeout: 10000 })

    const bell = page.locator('button:has(.pi-bell)').first()
    await bell.click()

    const allLink = page.locator('button:has-text("Все уведомления")')
    await expect(allLink).toBeVisible({ timeout: 5000 })
    await allLink.click()

    await page.waitForURL('**/notifications', { timeout: 10000 })
    expect(page.url()).toContain('/notifications')
  })
})

test.describe('Уведомления с данными', () => {
  let userToken: string
  let clubId: number

  test.beforeAll(async ({ request }) => {
    const loginRes = await request.post('/api/auth/login', {
      data: { login: 'admin@club.ru', password: 'admin123' },
    })
    expect(loginRes.ok()).toBeTruthy()
    const { token: adminToken } = await loginRes.json()

    const clubRes = await request.post('/api/v1/clubs', {
      headers: { Authorization: `Bearer ${adminToken}` },
      data: {
        name: `Клуб уведомлений ${Date.now()}`,
        description: 'Для тестирования уведомлений',
        categoryId: 1,
        ageLimitMin: null,
        ageLimitMax: null,
        membershipFee: null,
      },
    })
    expect(clubRes.ok()).toBeTruthy()
    const club = await clubRes.json()
    clubId = club.id

    const user = generateTestUser()
    userToken = await registerAndGetToken(request, user)

    const applyRes = await request.post(`/api/v1/clubs/${clubId}/applications`, {
      headers: { Authorization: `Bearer ${userToken}` },
      data: { comment: 'Хочу вступить!' },
    })
    expect(applyRes.ok()).toBeTruthy()
    const application = await applyRes.json()

    const reviewRes = await request.put(`/api/v1/applications/${application.id}/review`, {
      headers: { Authorization: `Bearer ${adminToken}` },
      data: { decision: 'APPROVED', comment: 'Добро пожаловать!' },
    })
    expect(reviewRes.ok()).toBeTruthy()
  })

  test('Отображение уведомления на странице', async ({ page }) => {
    await page.goto('/login')
    await page.waitForSelector('#login', { timeout: 10000 })
    await page.evaluate((t) => localStorage.setItem('token', t), userToken)
    await page.goto('/notifications')

    await expect(page.locator('h1:has-text("Уведомления")')).toBeVisible({ timeout: 10000 })
    await expect(page.locator('text=Заявка в клуб одобрена')).toBeVisible({ timeout: 5000 })
  })

  test('Счётчик непрочитанных на колокольчике', async ({ page }) => {
    await page.goto('/login')
    await page.waitForSelector('#login', { timeout: 10000 })
    await page.evaluate((t) => localStorage.setItem('token', t), userToken)
    await page.goto('/clubs')

    await expect(page.locator('h1:has-text("Клубы")')).toBeVisible({ timeout: 10000 })
    const badge = page.locator('button:has(.pi-bell)').first().locator('..').locator('span.bg-red-500, span.absolute')
    await expect(badge).toBeVisible({ timeout: 5000 })
  })

  test('Клик по уведомлению помечает как прочитанное', async ({ page }) => {
    await page.goto('/login')
    await page.waitForSelector('#login', { timeout: 10000 })
    await page.evaluate((t) => localStorage.setItem('token', t), userToken)
    await page.goto('/notifications')

    await expect(page.locator('text=Заявка в клуб одобрена')).toBeVisible({ timeout: 10000 })

    const unreadIndicator = page.locator('.bg-primary-50').first()
    const wasUnread = await unreadIndicator.isVisible()

    if (wasUnread) {
      await unreadIndicator.click()
      await expect(page.locator('.border-primary-200').first()).not.toBeVisible({ timeout: 5000 })
    }
  })

  test('Уведомление в dropdown показывает текст', async ({ page }) => {
    await page.goto('/login')
    await page.waitForSelector('#login', { timeout: 10000 })
    await page.evaluate((t) => localStorage.setItem('token', t), userToken)
    await page.goto('/clubs')

    await expect(page.locator('h1:has-text("Клубы")')).toBeVisible({ timeout: 10000 })

    const bell = page.locator('button:has(.pi-bell)').first()
    await bell.click()

    await expect(page.locator('text=Заявка в клуб одобрена')).toBeVisible({ timeout: 5000 })
  })
})
