import request from './request'

/** 执行 Agent 智能指令 */
export function executeAgent(data) {
  return request({
    url: '/api/agent/execute',
    method: 'post',
    data
  })
}
