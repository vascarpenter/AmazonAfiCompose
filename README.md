# AmazonAfiCompose

- AmazonAffiの Jetpack Compose Desktopバージョン

- Swing DesignerをIntelliJでGradleと使うのは無理そうだったのでやむなく Jetpack Compose Desktopにした
- 馬鹿でかいからいやだったがやむなし たぶんJava+Swing Designerなら数キロバイトだが kotlin+JCでは 31MB..


### ビルド前に追加すること

- amazonがSDKを zip形式で提供しています
  - ここからダウンロード：https://webservices.amazon.com/paapi5/documentation/with-sdk.html
- プロジェクトディレクトリに `libs` ディレクトリを作成して zipを展開してでてきた外部 jar を中へ入れる
- `build.gradle.kts`を編集
  - `libs` 内の jar をプロジェクトの依存関係に追加する
    - `kotlin` `sourceSets` `dependencies` に追加

```
      dependencies {
        implementation(compose.desktop.currentOs)
        implementation(fileTree(mapOf("dir" to "libs", "include" to listOf("*.jar"))))  // この行を追加
      }
```

- `plugin` に追加
  - これはfatJarを作るためなので任意
  - `id("com.github.johnrengelman.shadow") version "7.1.0"`

### ビルド
- gradleやIntelliJで行ってください
- shadowJarでMainKtを指定した

### todo
- `java -jar AmazonAfiCompose.jar`で立ち上げると ACCESS KEYや SECRET KEYの環境変数を呼んでくれるが
Wクリックで立ち上げるとそんな環境変数は見ないからエラーになるのでどげんかせんといかん
