import { NativeEventEmitter, NativeModules, Platform } from 'react-native'

import { EventEmitter } from 'events'

const { RNAppManager } = NativeModules

/**
 * 检测应用是否安装
 * @param {*} packageName
 */
async function checkAppIsInstall(packageName) {
  let result = await RNAppManager.checkAppIsInstall(packageName)
  if (result) {
    alert(result)
    return result
  }
}

module.exports = {
  checkAppIsInstall
}
