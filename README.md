#### 使用示例

```
val signer = Signatory("appkey")
signer.genSignature(params)
// signer.toBase64String(params)
// signer.checkSignature(params, sign)
```

#### genSignature
根据传入的map，过滤掉空值后进行签名

#### toBase64String
将map追加sign签名字段，并转为base64字符串

#### checkSignature
校验签名
