[![License](https://img.shields.io/badge/license-Apache%202-4EB1BA.svg)](https://www.apache.org/licenses/LICENSE-2.0.html)
[![](https://jitpack.io/v/limuyang2/LPhotoPicker.svg)](https://jitpack.io/#limuyang2/LPhotoPicker)
# LPhotoPicker
这是一个漂亮的、纯粹的图片选择框架，对```对kotlin```的良好支持，java也可使用。不带裁剪、不带压缩、不带权限管理，没有冗余的第三方库，只为最纯粹的使用，让你更灵活与其他裁剪、压缩库组合使用。  
如果你喜欢毛玻璃效果、如果你想拥有最大化的自定义，那么这个库你不容错过。  

> 如果需要图片裁剪，推荐uCrop开源库组合使用[uCrop](https://github.com/Yalantis/uCrop)  

欢迎在[Issues](https://github.com/limuyang2/LPhotoPicker/issues)中提出问题、建议  

## 预览
|      |      |      |
| ---- | ---- | ---- |
|![](https://github.com/limuyang2/LPhotoPicker/blob/master/pic/Screenshot3.jpg)|![](https://github.com/limuyang2/LPhotoPicker/blob/master/pic/Screenshot4.jpg)|![](https://github.com/limuyang2/LPhotoPicker/blob/master/pic/Screenshot5.jpg)|

|模拟器刘海屏||
| ---- | ---- |
|![](https://github.com/limuyang2/LPhotoPicker/blob/master/pic/Screenshot1.jpg)|![](https://github.com/limuyang2/LPhotoPicker/blob/master/pic/Screenshot2.jpg)|

### demo下载地址
[apk下载](https://www.lanzous.com/i1kx0ba)  
![](https://github.com/limuyang2/LPhotoPicker/blob/master/pic/apk.png)

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
> 最新版本 [![](https://jitpack.io/v/limuyang2/LPhotoPicker.svg)](https://jitpack.io/#limuyang2/LPhotoPicker)
```gradle
dependencies {
	
	implementation 'com.github.limuyang2:LPhotoPicker:1.0.3'
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
> 使用前，记得获取权限！‘Manifest.permission.WRITE_EXTERNAL_STORAGE‘’，因为大家各自项目中使用的权限框架各不相同，库中再集成的话会显得非常臃肿多余。  

> 以下均以kotlin为示例，java的写法基本无差别，不在单独列出  

以下选项根据需要选择性添加：
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
在activity```onActivityResult```中接收数据
```kotlin
override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK) {
            when (requestCode) {
                CHOOSE_PHOTO_REQUEST -> {
                    val selectedPhotos = LPhotoPickerActivity.getSelectedPhotos(data)
                }
        }
}
```

## 自定义图片加载引擎框架
库中已经编写了Glide加载引擎，如果需要自定义其他图片加载框架的，可参照[LGlideEngine](https://github.com/limuyang2/LPhotoPicker/blob/master/photolibrary/src/main/java/top/limuyang2/photolibrary/engine/LGlideEngine.kt)自己重写。  
继承```LImageEngine```接口，重写其中的方法即可（以Glide为例子）：  
```kotlin
class LGlideEngine : LImageEngine {

    private val glideOptions by lazy { RequestOptions().centerCrop() }

    override fun load(context: Context, imageView: ImageView, path: String?, @DrawableRes placeholderRes: Int, resizeX: Int, resizeY: Int) {
        Glide.with(context)
                .load(path)
                .apply(glideOptions.placeholder(placeholderRes).override(resizeX, resizeY))
                .into(imageView)
    }

    //pauseOnScroll打开时，以下两个必须写，否"滑动时暂停加载"不会生效
    //加载暂停
    override fun pause(context: Context) {
        Glide.with(context).pauseRequests()
    }

    //恢复加载
    override fun resume(context: Context) {
        Glide.with(context).resumeRequestsRecursive()
    }
}
```


## 更改主题
### 简单设置，仅需一次性固定设置的
在```style```文中重写以下内容即可（```style```名字必须为```LPhotoTheme```）：
```xml
<style name="LPhotoTheme" parent="LPPBaseTheme">
	<!--页面通用属性（图片选择、图片预览共用）-->
	<item name="l_pp_toolBar_height">56dp</item>
	<item name="l_pp_toolBar_backIcon">@drawable/ic_l_pp_back_android</item><!--返回按钮资源-->
	<item name="l_pp_toolBar_title_size">16sp</item><!--toolBar上字体大小-->
	<item name="l_pp_bottomBar_height">50dp</item><!--底栏的高度-->

	<!--图片选择页面属性-->
	<item name="l_pp_status_bar_color">@color/colorPrimary</item><!--状态栏颜色-->
	<item name="l_pp_toolBar_background">@color/colorPrimary</item><!--toolBar颜色-->
	<item name="l_pp_picker_activity_bg">#F9F9F9</item><!--activity背景颜色-->
	<item name="l_pp_toolBar_title_color">#f2f2f2</item><!--(图片选择页面)顶部toolBar字体颜色-->
	<item name="l_pp_picker_bottomBar_background">#96ffffff</item><!--底栏的颜色，如果需要毛玻璃效果，颜色加上透明度-->
	<item name="l_pp_picker_bottomBar_enabled_text_color">#333333</item><!--底部按钮启用时的颜色-->
	<item name="l_pp_picker_bottomBar_unEnabled_text_color">#acacac</item><!--底栏按钮关闭时的颜色-->

	<!--图片分割线宽度-->
	<item name="l_pp_picker_segmenting_line_width">2dp</item>

	<!--圆形选择框样式（图片选择、图片预览共用，有特别说明的除外）-->
	<item name="l_pp_checkBox_color_tick" format="color">#fff</item><!--勾勾颜色-->
	<item name="l_pp_checkBox_duration" format="integer">100</item><!--动画时间-->
	<item name="l_pp_checkBox_color_checked" format="color">#fa5d5d</item><!--选择后的背景色-->
	<item name="l_pp_checkBox_stroke_width" format="dimension">2dp</item><!--（仅预览页面）圆圈边框宽度-->
	<item name="l_pp_checkBox_tick_width" format="dimension">2dp</item><!--勾勾的线条宽度-->
</style>
```

### 动态设置主题，需要多种风格切换的(例如主题切换的情况)
同上先在xm中定义属性，但```style```名字可以随意：
```xml
<style name="MyDarkTheme" parent="LPPBaseTheme">
	……
	属性内容同上一样
</style>
```
在代码中调用```theme()```方法设置xml主题：  
```
LPhotoPickerActivity.IntentBuilder(this)
	……
	.theme(R.style.MyDarkTheme)
	.build()
```

## 混淆
  添加support.v8规则和Glide混淆规则（如果自定义了其他图片加载库，请自行添加混淆内容即可）
```
# glide 4.x
-keep class com.bumptech.glide.Glide { *; }
-keep public class * implements com.bumptech.glide.module.GlideModule
-keep public class * extends com.bumptech.glide.module.AppGlideModule
-keep public enum com.bumptech.glide.load.resource.bitmap.ImageHeaderParser$** {
  **[] $VALUES;
  public *;
}

# renderscript
-keep class android.support.v8.renderscript.** { *; }
```

## 附录：配合使用uCrop
先添加[uCrop](https://github.com/Yalantis/uCrop)库，
然后在```onActivityResult```中接收数据后，传递给uCrop即可
```kotlin
override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK) {
            when (requestCode) {
                CHOOSE_PHOTO_REQUEST -> {
                    val selectedPhotos = LPhotoPickerActivity.getSelectedPhotos(data)
                    if(selectedPhotos.size == 1) {
                        //使用UCrop裁剪图片
                        val outUri = Uri.fromFile(File(cacheDir, "${System.currentTimeMillis()}.jpg"))
                        UCrop.of(Uri.fromFile(File(selectedPhotos[0])), outUri)
                            .withAspectRatio(1f, 1f)
                            .withMaxResultSize(800, 800)
                            .start(this)
                    }
                }
                
                //接收uCrop的参数
                UCrop.REQUEST_CROP   -> {
                    data?.let {
                        val resultUri = UCrop.getOutput(data)
                        Log.d("UCrop.REQUEST_CROP", resultUri.toString())
                        Glide.with(this).load(resultUri).into(imgView)
                    }
                }
        }
}
```
使用步骤很简单，具体的使用都可以参照demo [MainActivity](https://github.com/limuyang2/LPhotoPicker/blob/master/app/src/main/java/top/limuyang2/pohotopicker/MainActivity.kt)

## License
```
2018 limuyang
Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

   http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```
