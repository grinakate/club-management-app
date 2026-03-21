import { test, expect, type Page } from '@playwright/test'
import { loginAsAdmin, loginAsNewUser, type TestUser } from './helpers/auth.helper'

test.describe('Мероприятия', () => {
  let clubId: number
  let eventId: number
  let adminToken: string

  test.beforeAll(async ({ request }) => {
    const loginRes = await request.post('/api/auth/login', {
      data: { login: 'admin@club.ru', password: 'admin123' },
    })
    expect(loginRes.ok()).toBeTruthy()
    const body = await loginRes.json()
    adminToken = body.token

    const clubRes = await request.post('/api/v1/clubs', {
      headers: { Authorization: `Bearer ${adminToken}` },
      data: {
        name: `Клуб событий ${Date.now()}`,
        description: 'Для тестирования мероприятий',
        categoryId: 3,
        ageLimitMin: null,
        ageLimitMax: null,
        membershipFee: null,
      },
    })
    expect(clubRes.ok()).toBeTruthy()
    const club = await clubRes.json()
    clubId = club.id

    const tomorrow = new Date()
    tomorrow.setDate(tomorrow.getDate() + 1)
    const dayAfter = new Date()
    dayAfter.setDate(dayAfter.getDate() + 2)

    const eventRes = await request.post('/api/v1/events', {
      headers: { Authorization: `Bearer ${adminToken}` },
      data: {
        clubId,
        title: `Тестовое мероприятие ${Date.now()}`,
        description: 'Мероприятие для e2e тестов',
        startAt: tomorrow.toISOString(),
        endAt: dayAfter.toISOString(),
        participantLimit: 50,
        price: null,
      },
    })
    expect(eventRes.ok()).toBeTruthy()
    const event = await eventRes.json()
    eventId = event.id
  })

  test('Просмотр списка мероприятий', async ({ page }) => {
    await loginAsNewUser(page)

    await page.goto('/events')
    await expect(page.locator('h1:has-text("Мероприятия")')).toBeVisible({ timeout: 10000 })
  })

  test('Просмотр деталей мероприятия', async ({ page }) => {
    await loginAsNewUser(page)

    await page.goto(`/events/${eventId}`)
    await expect(page.locator('text=Клуб:').first()).toBeVisible({ timeout: 10000 })
    await expect(page.locator('text=Начало:').first()).toBeVisible()
    await expect(page.locator('text=Окончание:').first()).toBeVisible()
  })

  test('Регистрация на мероприятие', async ({ page }) => {
    await loginAsNewUser(page)

    await page.goto(`/events/${eventId}`)
    const registerBtn = page.locator('button:has-text("Записаться")')
    await expect(registerBtn).toBeVisible({ timeout: 10000 })
    await registerBtn.click()

    await expect(page.locator('button:has-text("Отменить запись")')).toBeVisible({ timeout: 10000 })
  })

  test('Отмена регистрации на мероприятие', async ({ page }) => {
    const user = await loginAsNewUser(page)
    const token = await getTokenForUser(page, user)

    const regRes = await page.request.post(`/api/v1/events/${eventId}/registrations`, {
      headers: { Authorization: `Bearer ${token}` },
    })
    expect(regRes.ok()).toBeTruthy()

    await page.goto(`/events/${eventId}`)
    const cancelBtn = page.locator('button:has-text("Отменить запись")')
    await expect(cancelBtn).toBeVisible({ timeout: 10000 })
    await cancelBtn.click()

    await expect(page.locator('button:has-text("Записаться")')).toBeVisible({ timeout: 10000 })
  })

  test('Создание мероприятия через UI (админ)', async ({ page }) => {
    await loginAsAdmin(page)

    await page.goto('/events/create')
    await expect(page.locator('text=Создание мероприятия')).toBeVisible({ timeout: 10000 })
    await expect(page.locator('text=Название').first()).toBeVisible()
    await expect(page.locator('text=Клуб').first()).toBeVisible()
  })

  test('Мои мероприятия', async ({ page }) => {
    await loginAsNewUser(page)

    await page.goto('/my-events')
    await expect(page.locator('h1:has-text("Мои мероприятия")')).toBeVisible({ timeout: 10000 })
  })

  test('Кнопка "Создать" на списке мероприятий видна только для админа', async ({ page }) => {
    await loginAsNewUser(page)

    await page.goto('/events')
    await expect(page.locator('h1:has-text("Мероприятия")')).toBeVisible({ timeout: 10000 })
    await expect(page.locator('button:has-text("Создать")')).not.toBeVisible()
  })

  test('Отмена мероприятия владельцем', async ({ page }) => {
    const token = await loginAsAdmin(page)

    const tomorrow = new Date()
    tomorrow.setDate(tomorrow.getDate() + 3)
    const dayAfter = new Date()
    dayAfter.setDate(dayAfter.getDate() + 4)

    const eventRes = await page.request.post('/api/v1/events', {
      headers: { Authorization: `Bearer ${token}` },
      data: {
        clubId,
        title: `Мероприятие для отмены ${Date.now()}`,
        description: 'Будет отменено',
        startAt: tomorrow.toISOString(),
        endAt: dayAfter.toISOString(),
        participantLimit: 10,
        price: null,
      },
    })
    expect(eventRes.ok()).toBeTruthy()
    const evt = await eventRes.json()

    await page.goto(`/events/${evt.id}`)
    const cancelBtn = page.locator('button:has-text("Отменить мероприятие")')
    await expect(cancelBtn).toBeVisible({ timeout: 10000 })
    await cancelBtn.click()

    await expect(page.locator('text=Отменено').first()).toBeVisible({ timeout: 5000 })
  })

  test('Страница участников мероприятия (админ)', async ({ page }) => {
    await loginAsAdmin(page)

    await page.goto(`/events/${eventId}/participants`)
    await expect(page.locator('h1:has-text("Участники мероприятия")')).toBeVisible({ timeout: 10000 })
    await expect(page.locator('button:has-text("Назад к мероприятию")')).toBeVisible()
  })
})

async function getTokenForUser(page: Page, user: TestUser): Promise<string> {
  const res = await page.request.post('/api/auth/login', {
    data: { login: user.email, password: user.password },
  })
  const body = await res.json()
  return body.token
}
