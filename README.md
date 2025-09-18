# Install/安装

```kotlin
// build.gradle.kts config dependency
// 在 build.gradle.kts 文件中配置
dependencies {
    implementation("io.github.cakioe:signature:${version}") // eg: 1.0.0
}
```

## Usage/用法

```kotlin
import io.github.cakioe.Signatory

fun main() {
    val signer = Signatory("appkey")
    signer.genSignature(params)
    // signer.toBase64String(params)
    // signer.checkSignature(params, sign)
}
```

## prepareParams / 准备参数

According to the input map, the null value is filtered out, sign the remaining data, and then generate a new map.

根据传入的 map，过滤掉空值后进行签名再生成 map

## genSignature / 生成签名

According to the input map, the null value is filtered out and the signature is performed

根据传入的 map，过滤掉空值后进行签名

## toBase64String / 将数据转成 base64

Append the map to the sign signature field and convert it to a base64 string

将 map 追加 sign 签名字段，并转为 base64 字符串

## checkSignature / 校验签名

Verify the signature of the incoming map and the signature

校验签名
