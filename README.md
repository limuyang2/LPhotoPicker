[![License](https://img.shields.io/badge/license-Apache%202-4EB1BA.svg)](https://www.apache.org/licenses/LICENSE-2.0.html)
# LPhotoPicker
这是一个漂亮的、纯粹的图片选择框架。不带裁剪、不带压缩、不带权限管理，没有冗余的第三方库，只为最纯粹的使用，让你更灵活与其他裁剪、压缩库组合使用。  
如果你喜欢毛玻璃效果、如果你想拥有最大化的自定义，那么这个库你不容错过。  

> 如果需要图片裁剪，推荐uCrop开源库组合使用[uCrop](https://github.com/Yalantis/uCrop)

## 预览
|      |      |      |
| ---- | ---- | ---- |
|![](https://github.com/limuyang2/LPhotoPicker/blob/master/pic/Screenshot3.jpg)|![](https://github.com/limuyang2/LPhotoPicker/blob/master/pic/Screenshot4.jpg)|![](https://github.com/limuyang2/LPhotoPicker/blob/master/pic/Screenshot5.jpg)|

|模拟器刘海屏||
| ---- | ---- |
|![](https://github.com/limuyang2/LPhotoPicker/blob/master/pic/Screenshot1.jpg)|![](https://github.com/limuyang2/LPhotoPicker/blob/master/pic/Screenshot2.jpg)|

### demo下载地址
[暂无](https://)

## 获取 
先在 build.gradle 的 repositories 添加仓库：  
```gradle
allprojects {
	repositories {
        ...
        maven { url 'https://jitpack.io' }
	}
}
```

再在dependencies添加：  
```gradle
dependencies {
	
	implementation 'com.github.limuyang2:LPhotoPicker:1.0.1'
}
```

在build.gradle中添加以下配置：  
```gradle
android {
    compileSdkVersion 27
    defaultConfig {
        ………………

        //添加以下两句代码
        renderscriptTargetApi 27  //版本号请与compileSdkVersion保持一致
        renderscriptSupportModeEnabled true

    }
}
```

## 使用
```kotlin
val intent = LPhotoPickerActivity.IntentBuilder(this)
               .maxChooseCount(3) //最大多选数目
               .columnsNumber(4) //以几列显示图片
               .imageType(LPPImageType.ofAll()) //需要显示的图片类型(webp/PNG/GIF/JPG)
               .pauseOnScroll(false) //滑动时，是否需要暂停图片加载
               .isSingleChoose(false) //单选模式
               .imageEngine(LGlideEngine()) //添加自定义的图片加载引擎(库中已经自带Glide加载引擎，如果你不需要自定义，可不添加此句)
               .theme(theme) //主题
               .selectedPhotos(ArrayList<String>()) //已选择的图片数组
               .build()

startActivityForResult(intent, CHOOSE_PHOTO_REQUEST)
```