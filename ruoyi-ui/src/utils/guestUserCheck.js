/**
 * 用户角色检查工具函数
 * 统一管理用户身份判断逻辑
 */

/**
 * 检查是否为访客用户
 * @param {Array} roles - 用户角色数组
 * @returns {boolean} 是否为访客用户
 */
export function isGuestUser(roles = []) {
  // 更健壮的角色判断逻辑
  if (Array.isArray(roles)) {
    return roles.some(role => {
      if (typeof role === 'string') {
        return role === 'guest' || role.includes('guest');
      } else if (typeof role === 'object' && role !== null) {
        return role.roleKey === 'guest' || role.roleName === '访客账号' || 
               (role.roleName && role.roleName.includes('访客'));
      }
      return false;
    });
  }
  return false;
}

/**
 * 检查是否为普通账号用户
 * @param {Array} roles - 用户角色数组
 * @returns {boolean} 是否为普通账号用户
 */
export function isRegularUser(roles = []) {
  if (Array.isArray(roles)) {
    return roles.some(role => {
      if (typeof role === 'string') {
        return role === 'regular' || role.includes('regular');
      } else if (typeof role === 'object' && role !== null) {
        return role.roleKey === 'regular' || role.roleName === '普通账号' || 
               (role.roleName && role.roleName.includes('普通'));
      }
      return false;
    });
  }
  return false;
}

/**
 * 检查是否为受限用户（仅访客用户）
 * @param {Array} roles - 用户角色数组
 * @returns {boolean} 是否为受限用户
 * 注意：根据新的SQL权限配置，regular角色现在有完整权限，不再被视为受限用户
 */
export function isLimitedUser(roles = []) {
  return isGuestUser(roles);
}

/**
 * Vue组件混入，提供用户角色检查功能
 */
export const userRoleMixin = {
  computed: {
    isGuestUser() {
      const roles = this.$store.getters.roles || [];
      return isGuestUser(roles);
    },
    isRegularUser() {
      const roles = this.$store.getters.roles || [];
      return isRegularUser(roles);
    },
    isLimitedUser() {
      const roles = this.$store.getters.roles || [];
      return isLimitedUser(roles);
    }
  }
};

/**
 * 向后兼容的访客用户混入
 * @deprecated 请使用 userRoleMixin
 */
export const guestUserMixin = userRoleMixin;

/**
 * 访客用户不应该请求的接口列表
 */
export const GUEST_FORBIDDEN_APIS = [
  'email/personal/inbox/unread/count/track',
  'email/personal/sent/unread/count/track',
  'email/personal/starred/unread/count/track',
  'email/personal/deleted/unread/count/track'
];

/**
 * 普通账号用户不应该请求的接口列表
 * 注意：根据新的SQL权限配置，regular角色现在有完整的权限访问所有接口
 * 因此这个列表应该为空，或者只包含一些特殊的限制
 */
export const REGULAR_USER_FORBIDDEN_APIS = [
  // 根据新的SQL权限配置，regular角色现在有权限访问所有邮件相关接口
  // 如果将来需要限制某些特殊接口，可以在这里添加
];

/**
 * 受限用户（仅访客用户）不应该请求的接口列表
 * 注意：根据新的SQL权限配置，现在只有访客用户被限制，regular角色有完整权限
 */
export const LIMITED_USER_FORBIDDEN_APIS = [
  ...GUEST_FORBIDDEN_APIS,
  ...REGULAR_USER_FORBIDDEN_APIS
];

/**
 * 检查接口是否被访客用户禁止访问
 * @param {string} apiUrl - API接口URL
 * @returns {boolean} 是否被禁止
 */
export function isForbiddenForGuest(apiUrl) {
  return GUEST_FORBIDDEN_APIS.some(forbiddenApi => apiUrl.includes(forbiddenApi));
}

/**
 * 检查接口是否被普通账号用户禁止访问
 * @param {string} apiUrl - API接口URL
 * @returns {boolean} 是否被禁止
 */
export function isForbiddenForRegularUser(apiUrl) {
  return REGULAR_USER_FORBIDDEN_APIS.some(forbiddenApi => apiUrl.includes(forbiddenApi));
}

/**
 * 检查接口是否被受限用户禁止访问
 * @param {string} apiUrl - API接口URL
 * @returns {boolean} 是否被禁止
 */
export function isForbiddenForLimitedUser(apiUrl) {
  return LIMITED_USER_FORBIDDEN_APIS.some(forbiddenApi => apiUrl.includes(forbiddenApi));
}


