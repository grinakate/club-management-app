import { test, expect } from '@playwright/test'
import { loginAsNewUser, loginAsAdmin } from './helpers/auth.helper'

test.describe('Навигация', () => {
  test('Сайдбар содержит основные ссылки', async ({ page }) => {
    await loginAsNewUser(page)

    await page.goto('/clubs')
    await page.waitForTimeout(2000)

    await expect(page.locator('a[href="/clubs"], a:has-text("Клубы")').first()).toBeVisible({ timeout: 10000 })
    await expect(page.locator('a[href="/events"], a:has-text("Мероприятия")').first()).toBeVisible()
    await expect(page.locator('a[href="/my-events"], a:has-text("Мои мероприятия")').first()).toBeVisible()
    await expect(page.locator('a[href="/my-clubs"], a:has-text("Мои клубы")').first()).toBeVisible()
    await expect(page.locator('a[href="/profile"], a:has-text("Профиль")').first()).toBeVisible()
  })

  test('Переход на страницу клубов через сайдбар', async ({ page }) => {
    await loginAsNewUser(page)
    await page.goto('/profile')
    await page.waitForTimeout(2000)

    await page.locator('a[href="/clubs"], a:has-text("Клубы")').first().click()
    await page.waitForURL('**/clubs', { timeout: 10000 })
    expect(page.url()).toContain('/clubs')
  })

  test('Переход на страницу мероприятий через сайдбар', async ({ page }) => {
    await loginAsNewUser(page)
    await page.goto('/clubs')
    await page.waitForTimeout(2000)

    await page.locator('a[href="/events"], a:has-text("Мероприятия")').first().click()
    await page.waitForURL('**/events', { timeout: 10000 })
    expect(page.url()).toContain('/events')
  })

  test('Переход на страницу профиля через сайдбар', async ({ page }) => {
    await loginAsNewUser(page)
    await page.goto('/clubs')
    await page.waitForTimeout(2000)

    await page.locator('a[href="/profile"], a:has-text("Профиль")').first().click()
    await page.waitForURL('**/profile', { timeout: 10000 })
    expect(page.url()).toContain('/profile')
  })

  test('Переход на "Мои мероприятия"', async ({ page }) => {
    await loginAsNewUser(page)
    await page.goto('/clubs')
    await page.waitForTimeout(2000)

    await page.locator('a[href="/my-events"], a:has-text("Мои мероприятия")').first().click()
    await page.waitForURL('**/my-events', { timeout: 10000 })
    expect(page.url()).toContain('/my-events')
  })

  test('Переход на "Мои клубы"', async ({ page }) => {
    await loginAsNewUser(page)
    await page.goto('/clubs')
    await page.waitForTimeout(2000)

    await page.locator('a[href="/my-clubs"], a:has-text("Мои клубы")').first().click()
    await page.waitForURL('**/my-clubs', { timeout: 10000 })
    expect(page.url()).toContain('/my-clubs')
  })

  test('Кнопка "Создать клуб" видна для админа', async ({ page }) => {
    await loginAsAdmin(page)

    await page.goto('/clubs')
    await page.waitForSelector('h1:has-text("Клубы")', { timeout: 10000 })

    await expect(page.locator('button:has-text("Создать клуб")')).toBeVisible()
  })

  test('Кнопка "Создать клуб" не видна для обычного пользователя', async ({ page }) => {
    await loginAsNewUser(page)

    await page.goto('/clubs')
    await page.waitForSelector('h1:has-text("Клубы")', { timeout: 10000 })

    await expect(page.locator('button:has-text("Создать клуб")')).not.toBeVisible()
  })
})
