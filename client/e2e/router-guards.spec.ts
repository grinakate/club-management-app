import { test, expect } from '@playwright/test'
import { loginAsNewUser } from './helpers/auth.helper'

test.describe('Защита маршрутов по ролям', () => {
  test('PARTICIPANT не попадает на /clubs/create', async ({ page }) => {
    await loginAsNewUser(page)

    await page.goto('/clubs/create')
    await page.waitForURL('**/clubs', { timeout: 10000 })
    expect(page.url()).not.toContain('/clubs/create')
  })

  test('PARTICIPANT не попадает на /events/create', async ({ page }) => {
    await loginAsNewUser(page)

    await page.goto('/events/create')
    await page.waitForURL('**/clubs', { timeout: 10000 })
    expect(page.url()).not.toContain('/events/create')
  })

  test('PARTICIPANT не попадает на /clubs/1/applications', async ({ page }) => {
    await loginAsNewUser(page)

    await page.goto('/clubs/1/applications')
    await page.waitForURL('**/clubs', { timeout: 10000 })
    expect(page.url()).not.toContain('/applications')
  })

  test('PARTICIPANT не попадает на /events/1/participants', async ({ page }) => {
    await loginAsNewUser(page)

    await page.goto('/events/1/participants')
    await page.waitForURL('**/clubs', { timeout: 10000 })
    expect(page.url()).not.toContain('/participants')
  })
})
