## 微信小程序解密encryptedData报错pad block corrupted

通关前端（微信小程序）传来的code调用微信api获取sessionKey对前端传来的encrytData加密字符串，出现了解密失败的情况。

通过百度分析，发现是前端获取code和获取加密串encrytData的顺序出了问题，腾讯在每次获取code的时候，会重新生成encryptData，所以必须先获得code之后，再获取encryptData。

解决方案：每次授权时，调用wx.login 接口，将sessionKey更新，然后再调其他接口。