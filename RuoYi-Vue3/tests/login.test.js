import assert from 'node:assert/strict'
import {readFile} from 'node:fs/promises'
import test from 'node:test'

test('登录请求只提交账号密码，并保留匿名登录和重复提交配置', async () => {
    const source = await readFile(new URL('../src/api/login.js', import.meta.url), 'utf8')
    const calls = []
    const request = options => {
        calls.push(options)
        return Promise.resolve({code: 200, token: 'fixture-token'})
    }
    const load = new Function('request', source
        .replace(/^import request from .*$/m, '')
        .replace(/^export /gm, '') + '\nreturn { login }')
    const {login} = load(request)
    const result = await login('fixture-user', 'fixture-password', 'legacy-code', 'legacy-uuid')
    assert.equal(result.token, 'fixture-token')
    assert.equal(calls.length, 1)
    assert.deepEqual(calls[0], {
        url: '/login',
        headers: {isToken: false, repeatSubmit: false},
        method: 'post',
        data: {username: 'fixture-user', password: 'fixture-password'}
    })
})
