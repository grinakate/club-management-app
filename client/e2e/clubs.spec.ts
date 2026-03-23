import { test, expect } from '@playwright/test'
import { loginAsNewUser, loginAsAdmin } from './helpers/auth.helper'

test.describe('Клубы', () => {
  test('Просмотр списка клубов', async ({ page }) => {
    await loginAsNewUser(page)

    await page.goto('/clubs')
    await expect(page.locator('h1:has-text("Клубы")')).toBeVisible()
  })

  test('Фильтрация клубов по поисковому запросу', async ({ page }) => {
    await loginAsAdmin(page)

    await page.goto('/clubs')
    await expect(page.locator('h1:has-text("Клубы")')).toBeVisible({ timeout: 10000 })

    const searchInput = page.locator('input[placeholder*="Поиск"]')
    await searchInput.fill('НесуществующийКлуб999')

    await expect(page.locator('text=Клубы не найдены')).toBeVisible({ timeout: 5000 })
  })

  test('Создание клуба (админ)', async ({ page }) => {
    await loginAsAdmin(page)

    await page.goto('/clubs/create')
    await page.waitForSelector('text=Создание клуба', { timeout: 10000 })

    const clubName = `Тестовый клуб ${Date.now()}`
    await page.locator('#name').fill(clubName)
    await page.locator('#description').fill('Описание тестового клуба для e2e тестов')

    await page.locator('#category').click()
    await page.locator('.p-select-option, .p-listbox-option, [role="option"]').first().click()

    await page.locator('button[type="submit"]').click()

    await page.waitForURL('**/clubs/**', { timeout: 15000 })
    await expect(page.locator(`text=${clubName}`).first()).toBeVisible({ timeout: 10000 })
  })

  test('Просмотр деталей клуба', async ({ page }) => {
    const token = await loginAsAdmin(page)

    const clubName = `Клуб деталей ${Date.now()}`
    const createRes = await page.request.post('/api/v1/clubs', {
      headers: { Authorization: `Bearer ${token}` },
      data: {
        name: clubName,
        description: 'Клуб для проверки детальной страницы',
        categoryId: 1,
        ageLimitMin: null,
        ageLimitMax: null,
        membershipFee: null,
      },
    })
    const club = await createRes.json()

    await page.goto(`/clubs/${club.id}`)
    await page.waitForSelector(`text=${clubName}`, { timeout: 10000 })

    await expect(page.locator(`text=${clubName}`).first()).toBeVisible()
    await expect(page.locator('text=Описание').first()).toBeVisible()
    await expect(page.locator('text=Информация').first()).toBeVisible()
  })

  test('Кнопка "Создать клуб" видна только админу', async ({ page }) => {
    await loginAsNewUser(page)

    await page.goto('/clubs')
    await page.waitForSelector('h1:has-text("Клубы")', { timeout: 10000 })

    await expect(page.locator('button:has-text("Создать клуб")')).not.toBeVisible()
  })

  test('Кнопка "Подробнее" переводит на детали клуба', async ({ page }) => {
    const token = await loginAsAdmin(page)

    const clubName = `Клуб подробнее ${Date.now()}`
    const res = await page.request.post('/api/v1/clubs', {
      headers: { Authorization: `Bearer ${token}` },
      data: { name: clubName, description: 'test', categoryId: 1, ageLimitMin: null, ageLimitMax: null, membershipFee: null },
    })
    expect(res.ok()).toBeTruthy()

    await page.goto('/clubs')
    await expect(page.locator('h1:has-text("Клубы")')).toBeVisible({ timeout: 10000 })

    const card = page.locator('.p-card', { hasText: clubName }).first()
    await expect(card).toBeVisible({ timeout: 5000 })
    await card.locator('button:has-text("Подробнее")').click()

    await page.waitForURL('**/clubs/**', { timeout: 10000 })
    await expect(page.locator(`text=${clubName}`).first()).toBeVisible()
  })

  test('Владелец клуба видит кнопки "Участники" и "Заявки"', async ({ page }) => {
    const token = await loginAsAdmin(page)

    const clubName = `Клуб кнопок ${Date.now()}`
    const res = await page.request.post('/api/v1/clubs', {
      headers: { Authorization: `Bearer ${token}` },
      data: { name: clubName, description: 'Для проверки кнопок', categoryId: 2, ageLimitMin: null, ageLimitMax: null, membershipFee: null },
    })
    const club = await res.json()

    await page.goto(`/clubs/${club.id}`)
    await page.waitForSelector(`text=${clubName}`, { timeout: 10000 })

    await expect(page.locator('button:has-text("Участники")')).toBeVisible()
    await expect(page.locator('button:has-text("Заявки")')).toBeVisible()
    await expect(page.locator('button:has-text("Архивировать")')).toBeVisible()
  })

  test('Архивация клуба владельцем', async ({ page }) => {
    const token = await loginAsAdmin(page)

    const clubName = `Клуб архив ${Date.now()}`
    const res = await page.request.post('/api/v1/clubs', {
      headers: { Authorization: `Bearer ${token}` },
      data: { name: clubName, description: 'Архивируемый', categoryId: 3, ageLimitMin: null, ageLimitMax: null, membershipFee: null },
    })
    const club = await res.json()

    await page.goto(`/clubs/${club.id}`)
    await page.waitForSelector(`text=${clubName}`, { timeout: 10000 })

    await page.locator('button:has-text("Архивировать")').click()
    await expect(page.locator('text=ARCHIVED').first()).toBeVisible({ timeout: 10000 })
  })
})
