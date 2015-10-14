# FacebookContentProvider

![](art/facebook-content-provider.png)

Avoid ImageLoader could not load from `https://graph.facebook.com/{uid}/picture`. Use android `content://` scheme instead, it's look like the following uri: `content://facebook.content.ContentProvider/{uid}/picture`.

## Usage

Use

```java
FacebookContentProvider.getPictureStringFromUid(uid);
```

instead

```java
"https://graph.facebook.com/{uid}/picture"
```

For examples:

AUIL:

```java
ImageLoader.getInstance().displayImage(FacebookContentProvider.getPictureStringFromUid(uid), mImageView);
```

Picasso:

```java
Picasso.with(context).load(FacebookContentProvider.getPictureStringFromUid(uid)).into(mImageView);
```

Glide:

```java
Glide.with(context).load(FacebookContentProvider.getPictureStringFromUid(uid)).into(mImageView);
```

Fresco:

```java
mImageView.setImageURI(FacebookContentProvider.getPictureUriFromUid(uid)));
```

## Installation

AndroidManifest.xml:

```xml
<provider android:name="facebook.content.FacebookContentProvider"
  android:authorities="facebook.content.FacebookContentProvider" />
```

build.gradle:

```gradle
repositories {
  maven { url "https://jitpack.io" }
}
dependencies {
  compile 'com.github.yongjhih:facebook-content-provider:-SNAPSHOT'
}
```

## TODO

* Test Android Universal Image Loader(AUIL)
* Test Fresco
* Test Glide
* Test Picasso

## LICENSE

Copyright 2015 8tory, Inc.

Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
