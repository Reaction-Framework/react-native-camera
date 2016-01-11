import React, { requireNativeComponent, PropTypes, NativeModules, View } from 'react-native';

const CameraNativeModule = NativeModules.CameraModule;

// TODO
const constants = {
    Aspect: CameraNativeModule.Aspect,
    //BarCodeType: CameraNativeModule.BarCodeType,
    Type: CameraNativeModule.Type,
    //CaptureMode: CameraNativeModule.CaptureMode,
    CaptureTarget: CameraNativeModule.CaptureTarget,
    //Orientation: CameraNativeModule.Orientation,
    //FlashMode: CameraNativeModule.FlashMode,
    //TorchMode: CameraNativeModule.TorchMode,
};

const viewPropTypes = View.propTypes;

const CameraView = React.createClass({

    propTypes: {
        ...viewPropTypes,
        aspect: PropTypes.oneOfType([
            PropTypes.string,
            PropTypes.number
        ]),
        captureTarget: PropTypes.oneOfType([
            PropTypes.string,
            PropTypes.number
        ]),
        type: PropTypes.oneOfType([
            PropTypes.string,
            PropTypes.number
        ])
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

    capture(options, cb) {

        if (arguments.length == 1) {
            cb = options;
            options = {};
        }

        options = Object.assign({}, {
            type: this.props.type,
            target: this.props.captureTarget,
            sampleSize: 0
        }, options);

        if (typeof options.aspect === 'string') {
            options.aspect = constants.Aspect[options.aspect];
        }

        if (typeof options.target === 'string') {
            options.target = constants.CaptureTarget[options.target];
        }

        if (typeof options.type === 'string') {
            options.type = constants.Type[options.type];
        }

        CameraNativeModule.capture(options, cb);
    }
});

const ReactCameraView = requireNativeComponent('RCTIONCameraView', CameraView);

CameraView.constants = constants;

export default CameraView;
