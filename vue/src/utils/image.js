/**
 * 图片URL处理工具
 */

/**
 * 修复七牛云测试域名的HTTPS证书问题
 * 如果是七牛云测试域名的HTTPS链接，自动转换为HTTP
 * @param {string} url 图片URL
 * @returns {string} 修复后的URL
 */
export function fixImageUrl(url) {
  if (!url) return url
  
  // 如果是七牛云测试域名的HTTPS链接，转换为HTTP
  if (url.startsWith('https://') && url.includes('.hd-bkt.clouddn.com')) {
    return url.replace('https://', 'http://')
  }
  
  return url
}

