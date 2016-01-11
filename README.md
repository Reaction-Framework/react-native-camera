# react-native-camera

A camera module for React Native.

![](https://i.imgur.com/5j2JdUk.gif)

## Getting started

`npm install https://github.com/Reaction-Framework/react-native-camera.git --save`

## iPhone setup 

* In XCode, in the project navigator, right click `Libraries` ➜ `Add Files to [your project's name]`
* Go to `node_modules` ➜ `react-native-camera` and add `RCTCamera.xcodeproj`
* In XCode, in the project navigator, select your project. Add `libRCTCamera.a` to your project's `Build Phases` ➜ `Link Binary With Libraries`
* Click `RCTCamera.xcodeproj` in the project navigator and go the `Build Settings` tab. Make sure 'All' is toggled on (instead of 'Basic'). In the `Search Paths` section, look for `Header Search Paths` and make sure it contains both `$(SRCROOT)/../../react-native/React` and `$(SRCROOT)/../../../React` - mark both as `recursive`.

## Android setup 

* Run:
```
npm install https://github.com/Reaction-Framework/rction-image-android.git --save
```

* Add to your `settings.gradle`:
```
include ':io.reactionframework.android.image'
project(':io.reactionframework.android.image').projectDir = new File(settingsDir, '../node_modules/rction-image-android')

include ':io.reactionframework.android.react.camera'
project(':io.reactionframework.android.react.camera').projectDir = new File(settingsDir, '../node_modules/react-native-camera/android')
```
* Add to your `app/build.gradle`:
```
dependencies {
	...
	compile project(':io.reactionframework.android.react.camera')
}
```
* Add to your `MainActivity.java`:
  * `import io.reactionframework.android.react.camera.CameraPackage;`
  * in `getPackages`:
  ```
  return Arrays.<ReactPackage>asList(
          new MainReactPackage(),
          ...,
          new CameraPackage(this)
  );
  ```

## Usage

All you need is to `require` the `react-native-camera` module and then use the
`<Camera/>` tag.

```javascript
'use strict';

import React, { AppRegistry, StyleSheet, Image, TouchableHighlight, Text } from 'react-native';
import Camera from 'react-native-camera';

let styles = StyleSheet.create({
    container: {
        flex: 1,
        flexDirection: 'row',
        justifyContent: 'space-between',
        alignItems: 'flex-end',
        backgroundColor: '#000000'
    },
    aspect: {
        height: 40,
        margin: 10,
        color: '#ffffff',
        alignSelf: 'center'
    },
    flipButton: {
        width: 40,
        height: 40,
        margin: 10,
        alignSelf: 'flex-start'
    },
    captureButton: {
        width: 40,
        height: 40,
        margin: 10,
        alignSelf: 'flex-end'
    }
});

let cameraApp = React.createClass({
    getInitialState() {
        return {
            cameraType: Camera.constants.Type.back,
            aspect: Camera.constants.Aspect.fill,
            aspectText: 'Fill'
        }
    },
    render() {
        return (
            <Camera ref="cam"
                    style={styles.container}
                    type={this.state.cameraType}
                    aspect={this.state.aspect}>
                <TouchableHighlight onPress={this.flipCamera}>
                    <Image style={styles.flipButton}
                           source={require('image!flip')}/>
                </TouchableHighlight>
                <TouchableHighlight onPress={this.changeAspect}>
                    <Text style={styles.aspect}>
                        {this.state.aspectText}
                    </Text>
                </TouchableHighlight>
                <TouchableHighlight onPress={this.takePicture}>
                    <Image style={styles.captureButton}
                           source={require('image!capture')}/>
                </TouchableHighlight>
            </Camera>
        );
    },
    changeAspect() {
        const state = this.state;

        switch (state.aspect) {
            case Camera.constants.Aspect.fill:
                state.aspect = Camera.constants.Aspect.fit;
                state.aspectText = 'Fit';
                break;
            case Camera.constants.Aspect.fit:
                state.aspect = Camera.constants.Aspect.stretch;
                state.aspectText = 'Stretch';
                break;
            case Camera.constants.Aspect.stretch:
                state.aspect = Camera.constants.Aspect.fill;
                state.aspectText = 'Fill';
                break;
        }

        this.setState(state);
    },
    flipCamera() {
        const state = this.state;
        state.cameraType = state.cameraType === Camera.constants.Type.back
            ? Camera.constants.Type.front : Camera.constants.Type.back;
        this.setState(state);
    },
    takePicture() {
        this.refs.cam.capture(function(err, data) {
            console.log(err, data);
        });
    }
});

AppRegistry.registerComponent('cameraApp', () => cameraApp);
```

Full example at [React Native Camera Experiments](https://github.com/Reaction-Framework/react-native-camera-experiments)

## Properties

#### `aspect` (iOS; Android)

Values: `Camera.constants.Aspect.fit` or `"fit"`, `Camera.constants.Aspect.fill` or `"fill"` (default), `Camera.constants.Aspect.stretch` or `"stretch"`

The `aspect` property allows you to define how your viewfinder renders the camera's view. For instance, if you have a square viewfinder and you want to fill the it entirely, you have two options: `"fill"`, where the aspect ratio of the camera's view is preserved by cropping the view or `"stretch"`, where the aspect ratio is skewed in order to fit the entire image inside the viewfinder. The other option is `"fit"`, which ensures the camera's entire view fits inside your viewfinder without altering the aspect ratio.

#### `captureAudio` (iOS)

Values: `true` (default), `false` (Boolean)

*Applies to video capture mode only.* Specifies whether or not audio should be captured with the video.


#### `captureMode` (iOS)

Values: `Camera.constants.CaptureMode.still` (default), `Camera.constants.CaptureMode.video`

The type of capture that will be performed by the camera - either a still image or video.

#### `captureTarget` (iOS; Android)

Values: `Camera.constants.CaptureTarget.cameraRoll` (default), `Camera.constants.CaptureTarget.disk`, ~~`Camera.constants.CaptureTarget.memory`~~ (deprecated),

This property allows you to specify the target output of the captured image data. By default the image binary is sent back as a base 64 encoded string. The disk output has been shown to improve capture response time, so that is the recommended value.


#### `type` (iOS; Android)

Values: `Camera.constants.Type.front` or `"front"`, `Camera.constants.Type.back` or `"back"` (default)

Use the `type` property to specify which camera to use.


#### `orientation` (iOS)

Values:
`Camera.constants.Orientation.auto` or `"auto"` (default),
`Camera.constants.Orientation.landscapeLeft` or `"landscapeLeft"`, `Camera.constants.Orientation.landscapeRight` or `"landscapeRight"`, `Camera.constants.Orientation.portrait` or `"portrait"`, `Camera.constants.Orientation.portraitUpsideDown` or `"portraitUpsideDown"`

The `orientation` property allows you to specify the current orientation of the phone to ensure the viewfinder is "the right way up."

#### `onBarCodeRead` (iOS)

Will call the specified method when a barcode is detected in the camera's view.

Event contains `data` (the data in the barcode) and `bounds` (the rectangle which outlines the barcode.)

The following barcode types can be recognised:

- `aztec`
- `code138`
- `code39`
- `code39mod43`
- `code93`
- `ean13`
- `ean8`
- `pdf417`
- `qr`
- `upce`
- `datamatrix` (when available)

The barcode type is provided in the `data` object.

#### `flashMode` (iOS)

Values:
`Camera.constants.FlashMode.on`,
`Camera.constants.FlashMode.off`,
`Camera.constants.FlashMode.auto`

Use the `flashMode` property to specify the camera flash mode.

#### `torchMode` (iOS)

Values:
`Camera.constants.TorchMode.on`,
`Camera.constants.TorchMode.off`,
`Camera.constants.TorchMode.auto`

Use the `torchMode` property to specify the camera torch mode.

#### `onFocusChanged` (iOS)

Args:
```
e: {
  nativeEvent: {
    touchPoint: { x, y }
  }
}
```
Will call when touch to focus has been made.
By default, `onFocusChanged` is not defined and tap-to-focus is disabled.

#### `defaultOnFocusComponent` (iOS)

Values:
`true` (default)
`false`

If `defaultOnFocusComponent` set to false, default internal implementation of visual feedback for tap-to-focus gesture will be disabled.

#### `onZoomChanged` (iOS)

Args:
```
  e: {
    nativeEvent: {
      velocity, zoomFactor
    }
  }
```
Will call when focus has changed.
By default, `onZoomChanged` is not defined and pinch-to-zoom is disabled.

## Component methods

You can access component methods by adding a `ref` (ie. `ref="camera"`) prop to your `<Camera>` element, then you can use `this.refs.camera.capture(cb)`, etc. inside your component.

#### `capture([options,] callback)` (iOS; Android)

Captures data from the camera. What is captured is based on the `captureMode` and `captureTarget` props. `captureMode` tells the camera whether you want a still image or video. `captureTarget` allows you to specify how you want the data to be captured and sent back to you. See `captureTarget` under Properties to see the available values.

Supported options:

 - `audio` (See `captureAudio` under Properties) *(iOS)*
 - `mode` (See  `captureMode` under Properties) *(iOS)*
 - `target` (See `captureTarget` under Properties) *(iOS; Android)*
 - `metadata` This is metadata to be added to the captured image. *(iOS)*
   - `location` This is the object returned from `navigator.geolocation.getCurrentPosition()` (React Native's geolocation polyfill). It will add GPS metadata to the image.
 - `rotation` This will rotate the image by the number of degrees specified. *(iOS)*
 
#### `stopCapture()` (iOS)

Ends the current capture session for video captures. Only applies when the current `captureMode` is `video`.

## Subviews
This component supports subviews on iOS and Android, so if you wish to use the camera view as a background or if you want to layout buttons/images/etc. inside the camera then you can do that.

------------

Thanks to [@lwansbrough](https://github.com/lwansbrough) for original module and to [@timmh](https://github.com/timmh) on first Android implementation. Some credits goes to [@boxme](https://github.com/boxme) and to [SquareCamera](https://github.com/boxme/SquareCamera) library for Android.

## Roadmap
1. Android
	* Add all missing features implemented on iOS
