[![License](https://img.shields.io/badge/license-Apache%202-4EB1BA.svg)](https://www.apache.org/licenses/LICENSE-2.0.html)
[![](https://jitpack.io/v/limuyang2/LPhotoPicker.svg)](https://jitpack.io/#limuyang2/LPhotoPicker)
# LPhotoPicker
这是一个漂亮的、纯粹的`AndroidX`图片选择框架，已适配：
- `Android 10`的沙盒机制、Android 12\13隐私权限
- 支持`Dark Mode`，
- `Android 10`的`HEIF`图片格式支持

以及对`kotlin`的良好支持，`java`也可很好的使用。不带裁剪、不带压缩、不带权限管理，没有冗余的第三方库，只为最纯粹的使用，让你更灵活与其他裁剪、压缩库组合使用。  
如果你喜欢毛玻璃效果、如果你想拥有最大化的自定义，那么这个库你不容错过。  

> 如果需要图片裁剪，推荐uCrop开源库组合使用[uCrop](https://github.com/Yalantis/uCrop)  

欢迎在[Issues](https://github.com/limuyang2/LPhotoPicker/issues)中提出问题、建议  

## 预览
|      |      |      |
| ---- | ---- | ---- |
|![](https://github.com/limuyang2/LPhotoPicker/blob/master/pic/Screenshot3.jpg)|![](https://github.com/limuyang2/LPhotoPicker/blob/master/pic/Screenshot4.jpg)|![](https://github.com/limuyang2/LPhotoPicker/blob/master/pic/Screenshot5.jpg)|

|Dark Mode（暗黑模式）|||
| ---- | ---- | ---- |
|![](https://github.com/limuyang2/LPhotoPicker/blob/master/pic/dark1.png)|![](https://github.com/limuyang2/LPhotoPicker/blob/master/pic/dark2.png)|![](https://github.com/limuyang2/LPhotoPicker/blob/master/pic/dark3.png)|

### demo下载地址
[apk下载](https://wwxd.lanzoue.com/igXDa118lh4d)  
![](https://github.com/limuyang2/LPhotoPicker/blob/master/pic/apk.png)

## 获取 
添加依赖：  
> [最新版本](https://central.sonatype.com/artifact/io.github.limuyang2/LPhotoPicker)
```gradle
dependencies {
    // only support AndroidX
	implementation("io.github.limuyang2:LPhotoPicker:3.0")
	implementation("io.github.limuyang2:renderscrip-toolkit:1.0.1")
}
```

## 使用
> 使用前，记得获取权限！`Manifest.permission.READ_EXTERNAL_STORAGE`，因为大家各自项目中使用的权限框架各不相同，库中再集成的话会显得非常臃肿多余。  

> 以下均以kotlin为示例，java的写法基本无差别，不在单独列出  

以下选项根据需要选择性添加：
```kotlin
LPhotoHelper.Builder()
    .maxChooseCount(6) //最多选几个
    .columnsNumber(3) //每行显示几列图片
    .imageType(LPPImageType.ofAll()) // 文件类型
    .pauseOnScroll(false) // 是否滑动暂停加载图片显示
    .isSingleChoose(false) // 是否是单选
    .isOpenLastAlbum(false) // 是否直接打开最后一次选择的相册
    .theme(theme) // 设置主题
    .build()
    .start(this, CHOOSE_PHOTO_REQUEST)
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
### Dark Mode
如果使用默认主题设置，无需特别处理，自带支持`Dark Mode`

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
	<item name="l_pp_status_bar_lightMode">false</item><!--状态栏高亮模式-->
	<item name="l_pp_picker_bottomBar_background">#96ffffff</item><!--底栏的颜色，如果需要毛玻璃效果，颜色加上透明度-->
	<item name="l_pp_picker_bottomBar_enabled_text_color">#333333</item><!--底部按钮启用时的颜色-->
	<item name="l_pp_picker_bottomBar_disabled_text_color">#acacac</item><!--底栏按钮关闭时的颜色-->

	<!--图片分割线宽度-->
	<item name="l_pp_picker_pic_spacing">2dp</item>

	<!--圆形选择框样式（图片选择、图片预览共用，有特别说明的除外）-->
	<item name="l_pp_checkBox_color_tick" format="color">#fff</item><!--勾勾颜色-->
	<item name="l_pp_checkBox_duration" format="integer">100</item><!--动画时间-->
	<item name="l_pp_checkBox_color_checked" format="color">#fa5d5d</item><!--选择后的背景色-->
	<item name="l_pp_checkBox_stroke_width" format="dimension">2dp</item><!--（仅预览页面）圆圈边框宽度-->
	<item name="l_pp_checkBox_tick_width" format="dimension">2dp</item><!--勾勾的线条宽度-->
</style>
```

### 动态设置主题，需要多种风格切换的(例如主题切换的情况)
同上先在xml中定义属性，但```style```名字可以随意：
```xml
<style name="MyDarkTheme" parent="LPPBaseTheme">
	……
	属性内容同上一样
</style>
```
在代码中调用```theme()```方法设置xml主题：  
```
LPhotoHelper.Builder()
	……
	.theme(R.style.MyDarkTheme)
	.build()
    .start(this, CHOOSE_PHOTO_REQUEST)
```

## 混淆
本库已经自带混淆，已经过测试。  
(如果自定义了其他图片加载库，请自行添加混淆内容即可)

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
                if (selectedPhotos.size == 1) {
                    //使用UCrop裁剪图片
                    val outUri = Uri.fromFile(File(cacheDir, "${System.currentTimeMillis()}.jpg"))
                    UCrop.of(Uri.fromFile(File(selectedPhotos[0])), outUri)
                        .withAspectRatio(1f, 1f)
                        .withMaxResultSize(800, 800)
                        .start(this)
                }
            }

            //接收uCrop的参数
            UCrop.REQUEST_CROP -> {
                data?.let {
                    val resultUri = UCrop.getOutput(data)
                    Log.d("UCrop.REQUEST_CROP", resultUri.toString())
                    Glide.with(this).load(resultUri).into(imgView)
                }
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
