import { test, expect } from '@playwright/test'
import { generateTestUser, loginViaUi, loginAsNewUser } from './helpers/auth.helper'

test.describe('Аутентификация', () => {
  test('Регистрация нового пользователя через UI', async ({ page }) => {
    const user = generateTestUser()

    await page.goto('/register')
    await expect(page.locator('text=Регистрация')).toBeVisible()

    await page.locator('#fullName').fill(user.fullName)
    await page.locator('#email').fill(user.email)
    await page.locator('#password input').fill(user.password)
    await page.locator('#city').fill(user.city!)

    await page.locator('#birthDate input').click()
    await page.locator('.p-datepicker-calendar td:not(.p-datepicker-other-month) >> text=15').first().click()

    await page.locator('button[type="submit"]').click()

    await page.waitForURL('**/clubs', { timeout: 15000 })
    expect(page.url()).toContain('/clubs')
  })

  test('Вход в систему через UI', async ({ page }) => {
    const user = generateTestUser()
    await page.request.post('/api/auth/register', {
      data: {
        fullName: user.fullName,
        email: user.email,
        phone: null,
        password: user.password,
        city: user.city,
        interests: user.interests,
        birthDate: user.birthDate,
      },
    })

    await loginViaUi(page, user.email, user.password)
    await page.waitForURL('**/clubs', { timeout: 15000 })
    expect(page.url()).toContain('/clubs')
  })

  test('Редирект неавторизованного пользователя на /login', async ({ page }) => {
    await page.goto('/clubs')
    await page.waitForURL('**/login', { timeout: 10000 })
    expect(page.url()).toContain('/login')
  })

  test('Выход из системы', async ({ page }) => {
    await loginAsNewUser(page)
    await expect(page.locator('text=Клубы').first()).toBeVisible()

    const logoutButton = page.locator('button:has(.pi-sign-out)')
    await logoutButton.first().click()

    await page.waitForURL('**/login', { timeout: 10000 })
    expect(page.url()).toContain('/login')
  })

  test('Авторизованный пользователь перенаправляется с /login на /', async ({ page }) => {
    await loginAsNewUser(page)
    await page.goto('/login')
    await page.waitForURL('**/clubs', { timeout: 10000 })
    expect(page.url()).toContain('/clubs')
  })

  test('Ошибка при вводе неверных учётных данных', async ({ page }) => {
    await page.goto('/login')
    await page.waitForSelector('#login', { timeout: 10000 })
    await page.locator('#login').fill('nonexistent@mail.ru')
    await page.locator('#password input').fill('WrongPassword999')
    await page.locator('button[type="submit"]').click()

    await expect(page.locator('.p-toast-message, [data-pc-name="toast"]').first()).toBeVisible({ timeout: 5000 })
    expect(page.url()).toContain('/login')
  })

  test('Ссылка "Зарегистрироваться" на странице входа', async ({ page }) => {
    await page.goto('/login')
    await page.waitForSelector('#login', { timeout: 10000 })
    await page.locator('a[href="/register"]').click()
    await page.waitForURL('**/register', { timeout: 10000 })
    expect(page.url()).toContain('/register')
  })

  test('Ссылка "Войти" на странице регистрации', async ({ page }) => {
    await page.goto('/register')
    await page.waitForSelector('#fullName', { timeout: 10000 })
    await page.locator('a[href="/login"]').click()
    await page.waitForURL('**/login', { timeout: 10000 })
    expect(page.url()).toContain('/login')
  })
})
