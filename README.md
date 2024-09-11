## Install/安装

```
// build.gradle.kts config dependency
// 在 build.gradle.kts 文件中配置
dependencies {
    implementation("io.github.cakioe:signature:${version}") // eg: 1.0.0
}
```

#### Usage/用法
```
import io.github.cakioe.Signatory

fun main() {
    val signer = Signatory("appkey")
    signer.genSignature(params)
    // signer.toBase64String(params)
    // signer.checkSignature(params, sign)
}
```

### genSignature / 生成签名
According to the incoming map, the null value is filtered out and the signature is performed

根据传入的map，过滤掉空值后进行签名

### toBase64String / 将数据转成base64
Append the map to the sign signature field and convert it to a base64 string

将map追加sign签名字段，并转为base64字符串

### checkSignature / 校验签名
Verify the signature of the incoming map and the signature

校验签名
