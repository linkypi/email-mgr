/**
 * 访客用户检查工具函数测试
 * 用于验证访客用户身份判断逻辑的正确性
 */

import { isGuestUser, isForbiddenForGuest } from './guestUserCheck';

// 测试用例
const testCases = [
  // 字符串角色测试
  {
    name: '字符串角色 - guest',
    roles: ['guest'],
    expected: true
  },
  {
    name: '字符串角色 - admin',
    roles: ['admin'],
    expected: false
  },
  {
    name: '字符串角色 - 包含guest',
    roles: ['guest_user'],
    expected: true
  },
  
  // 对象角色测试
  {
    name: '对象角色 - roleKey为guest',
    roles: [{ roleKey: 'guest', roleName: '访客账号' }],
    expected: true
  },
  {
    name: '对象角色 - roleName为访客账号',
    roles: [{ roleKey: 'user', roleName: '访客账号' }],
    expected: true
  },
  {
    name: '对象角色 - roleName包含访客',
    roles: [{ roleKey: 'user', roleName: '临时访客' }],
    expected: true
  },
  {
    name: '对象角色 - 普通用户',
    roles: [{ roleKey: 'user', roleName: '普通用户' }],
    expected: false
  },
  
  // 混合角色测试
  {
    name: '混合角色 - 包含访客',
    roles: ['admin', { roleKey: 'guest', roleName: '访客账号' }],
    expected: true
  },
  {
    name: '混合角色 - 不包含访客',
    roles: ['admin', { roleKey: 'user', roleName: '普通用户' }],
    expected: false
  },
  
  // 边界情况测试
  {
    name: '空数组',
    roles: [],
    expected: false
  },
  {
    name: 'null',
    roles: null,
    expected: false
  },
  {
    name: 'undefined',
    roles: undefined,
    expected: false
  }
];

// 运行测试
console.log('=== 访客用户检查工具函数测试 ===');

testCases.forEach(testCase => {
  const result = isGuestUser(testCase.roles);
  const status = result === testCase.expected ? '✅ 通过' : '❌ 失败';
  console.log(`${status} ${testCase.name}: 期望 ${testCase.expected}, 实际 ${result}`);
});

// 测试禁止访问的接口
console.log('\n=== 访客用户禁止访问接口测试 ===');

const apiTests = [
  {
    name: '收件箱未读数量接口',
    url: '/email/personal/inbox/unread/count/track',
    expected: true
  },
  {
    name: '发件箱未读数量接口',
    url: '/email/personal/sent/unread/count/track',
    expected: true
  },
  {
    name: '星标邮件数量接口',
    url: '/email/personal/starred/unread/count',
    expected: true
  },
  {
    name: '已删除邮件数量接口',
    url: '/email/personal/deleted/unread/count',
    expected: true
  },
  {
    name: '其他接口',
    url: '/email/personal/list',
    expected: false
  }
];

apiTests.forEach(test => {
  const result = isForbiddenForGuest(test.url);
  const status = result === test.expected ? '✅ 通过' : '❌ 失败';
  console.log(`${status} ${test.name}: 期望 ${test.expected}, 实际 ${result}`);
});

export { testCases, apiTests };












