import React, { requireNativeComponent, PropTypes, NativeModules, View } from 'react-native';

const CameraNativeModule = NativeModules.CameraModule;

// TODO
const constants = {
    //Aspect: CameraNativeModule.Aspect,
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

    capture(options, callback) {
        if (arguments.length == 1) {
            callback = options;
            options = {};
        }

        let defaultOptions = {
            type: this.props.type,
            target: this.props.captureTarget,
            sampleSize: 0
        };

        CameraNativeModule.capture(Object.assign(defaultOptions, options || {}), callback);
    }
});

const ReactCameraView = requireNativeComponent('RCTIONCameraView', CameraView);

CameraView.constants = constants;

export default CameraView;
