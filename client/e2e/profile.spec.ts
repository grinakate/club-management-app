import { test, expect } from '@playwright/test'
import { loginAsNewUser } from './helpers/auth.helper'

test.describe('Управление профилем', () => {
  test('Просмотр профиля — данные пользователя отображаются', async ({ page }) => {
    await loginAsNewUser(page)

    await page.goto('/profile')
    await expect(page.locator('text=Профиль').first()).toBeVisible({ timeout: 10000 })

    await expect(page.locator('label:has-text("ФИО")')).toBeVisible()
    await expect(page.locator('label:has-text("Email")')).toBeVisible()
    await expect(page.locator('label:has-text("Город")')).toBeVisible()
  })

  test('Редактирование профиля — изменение города', async ({ page }) => {
    await loginAsNewUser(page)

    await page.goto('/profile')
    await expect(page.locator('text=Профиль').first()).toBeVisible({ timeout: 10000 })

    const cityLabel = page.locator('label:has-text("Город")')
    const cityInput = cityLabel.locator('..').locator('input')
    await cityInput.clear()
    await cityInput.fill('Санкт-Петербург')

    await page.locator('button:has-text("Сохранить изменения")').click()
    await expect(page.locator('.p-toast-message, [data-pc-name="toast"]').first()).toBeVisible({ timeout: 5000 })

    await page.reload()
    await expect(page.locator('text=Профиль').first()).toBeVisible({ timeout: 10000 })

    const cityInputAfter = page.locator('label:has-text("Город")').locator('..').locator('input')
    await expect(cityInputAfter).toHaveValue('Санкт-Петербург')
  })

  test('Смена пароля', async ({ page }) => {
    const user = await loginAsNewUser(page)

    await page.goto('/profile')
    await expect(page.locator('text=Смена пароля')).toBeVisible({ timeout: 10000 })

    const oldPasswordInput = page.locator('label:has-text("Текущий пароль")').locator('..').locator('input')
    const newPasswordInput = page.locator('label:has-text("Новый пароль")').locator('..').locator('input')

    await oldPasswordInput.fill(user.password)
    await newPasswordInput.fill('NewPassword456!')

    await page.locator('button:has-text("Изменить пароль")').click()
    await expect(page.locator('.p-toast-message, [data-pc-name="toast"]').first()).toBeVisible({ timeout: 5000 })
  })
})
