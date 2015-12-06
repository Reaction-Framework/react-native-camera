import React, { requireNativeComponent, PropTypes, NativeModules, View } from 'react-native';

const ReactNativeCameraModule = NativeModules.ReactCameraModule;

const constants = {
    'Aspect': {
        'stretch': 'stretch',
        'fit': 'fit',
        'fill': 'fill'
    },
    'BarCodeType': {
        'upca': 'upca',
        'upce': 'upce',
        'ean8': 'ean8',
        'ean13': 'ean13',
        'code39': 'code39',
        'code93': 'code93',
        'codabar': 'codabar',
        'itf': 'itf',
        'rss14': 'rss14',
        'rssexpanded': 'rssexpanded',
        'qr': 'qr',
        'datamatrix': 'datamatrix',
        'aztec': 'aztec',
        'pdf417': 'pdf417'
    },
    'Type': {
        'front': 'front',
        'back': 'back'
    },
    'CaptureMode': {
        'still': 'still',
        'video': 'video'
    },
    'CaptureTarget': {
        'memory': 'base64',
        'disk': 'disk',
        'cameraRoll': 'gallery'
    },
    'Orientation': {
        'auto': 'auto',
        'landscapeLeft': 'landscapeLeft',
        'landscapeRight': 'landscapeRight',
        'portrait': 'portrait',
        'portraitUpsideDown': 'portraitUpsideDown'
    },
    'FlashMode': {
        'off': 'off',
        'on': 'on',
        'auto': 'auto'
    },
    'TorchMode': {
        'off': 'off',
        'on': 'on',
        'auto': 'auto'
    }
};

const viewPropTypes = View.propTypes;

const CameraView = React.createClass({

    propTypes: {
        ...viewPropTypes,
        type: PropTypes.oneOf(['back', 'front'])
    },

    getDefaultProps() {
        return ({
            type: constants.Type.back,
            captureTarget: constants.CaptureTarget.cameraRoll
        });
    },

    render() {
        return (
            <ReactCameraView {...this.props}></ReactCameraView>
        );
    },

    capture(options, callback) {
        var component = this;
        var defaultOptions = {
            type: component.props.type,
            target: component.props.captureTarget,
            sampleSize: 0,
            title: '',
            description: ''
        };
        return new Promise(function(resolve, reject) {
            if(!callback && typeof options === 'function') {
                callback = options;
                options = {};
            }
            ReactNativeCameraModule.capture(Object.assign(defaultOptions, options || {}), function(encoded) {
                if(typeof callback === 'function') callback(encoded);
                resolve(encoded);
            });
        });
    }
});

const ReactCameraView = requireNativeComponent('ReactCameraView', CameraView);

CameraView.constants = constants;

export default CameraView;
