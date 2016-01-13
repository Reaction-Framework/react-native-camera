import React, {
    StyleSheet,
    requireNativeComponent,
    PropTypes,
    NativeModules,
    DeviceEventEmitter } from 'react-native';

const CameraNativeModule = NativeModules.IONCameraManager;

const constants = {
    Aspect: CameraNativeModule.Aspect,
    BarCodeType: CameraNativeModule.BarCodeType,
    Type: CameraNativeModule.Type,
    CaptureMode: CameraNativeModule.CaptureMode,
    CaptureTarget: CameraNativeModule.CaptureTarget,
    Orientation: CameraNativeModule.Orientation,
    FlashMode: CameraNativeModule.FlashMode,
    TorchMode: CameraNativeModule.TorchMode
};

const CAMERA_REF = 'camera';

const CameraView = React.createClass({

    propTypes: {
        aspect: PropTypes.oneOfType([
            PropTypes.string,
            PropTypes.number
        ]),
        captureAudio: PropTypes.bool,
        captureMode: PropTypes.oneOfType([
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
        ]),
        orientation: PropTypes.oneOfType([
            PropTypes.string,
            PropTypes.number
        ]),
        flashMode: PropTypes.oneOfType([
            PropTypes.string,
            PropTypes.number
        ]),
        torchMode: PropTypes.oneOfType([
            PropTypes.string,
            PropTypes.number
        ]),
        defaultOnFocusComponent: PropTypes.bool,
        onFocusChanged: PropTypes.func,
        onZoomChanged: PropTypes.func
    },

    setNativeProps(props) {
        this.refs[CAMERA_REF].setNativeProps(props);
    },

    getDefaultProps() {
        return {
            aspect: constants.Aspect.fill,
            type: constants.Type.back,
            orientation: constants.Orientation.auto,
            captureAudio: true,
            captureMode: constants.CaptureMode.still,
            captureTarget: constants.CaptureTarget.cameraRoll,
            flashMode: constants.FlashMode.off,
            torchMode: constants.TorchMode.off
        };
    },

    getInitialState() {
        return {
            isRecording: false
        };
    },

    async componentWillMount() {
        try {
            await CameraView.requestVideoPermission();
        } catch (error) { }

        this.cameraBarCodeReadListener = DeviceEventEmitter.addListener('CameraBarCodeRead', this._onBarCodeRead);
    },

    componentWillUnmount() {
        this.cameraBarCodeReadListener.remove();

        if (this.state.isRecording) {
            this.stopCapture();
        }
    },

    render() {
        const style = [styles.base, this.props.style];

        let aspect = this.props.aspect;
        let type = this.props.type;
        let orientation = this.props.orientation;
        let flashMode = this.props.flashMode;
        let torchMode = this.props.torchMode;

        if (typeof aspect === 'string') {
            aspect = constants.Aspect[aspect];
        }

        if (typeof flashMode === 'string') {
            flashMode = constants.FlashMode[flashMode];
        }

        if (typeof orientation === 'string') {
            orientation = constants.Orientation[orientation];
        }

        if (typeof torchMode === 'string') {
            torchMode = constants.TorchMode[torchMode];
        }

        if (typeof type === 'string') {
            type = constants.Type[type];
        }

        const nativeProps = Object.assign({}, this.props, {
            style,
            aspect: aspect,
            type: type,
            orientation: orientation,
            flashMode: flashMode,
            torchMode: torchMode
        });

        return <ReactCameraView ref={CAMERA_REF} {...nativeProps} />;
    },

    _onBarCodeRead(e) {
        if (this.props.onBarCodeRead) {
            this.props.onBarCodeRead(e);
        }
    },

    async capture(options) {
        options = Object.assign({}, {
            audio: this.props.captureAudio,
            mode: this.props.captureMode,
            target: this.props.captureTarget
        }, options);

        if (typeof options.mode === 'string') {
            options.mode = constants.CaptureMode[options.mode];
        }

        if (options.mode === constants.CaptureMode.video) {
            options.totalSeconds = (options.totalSeconds > -1 ? options.totalSeconds : -1);
            options.preferredTimeScale = options.preferredTimeScale || 30;

            if (options.audio) {
                try {
                    await CameraView.requestAudioPermission();
                } catch (error) { }
            }

            this.setState({ isRecording: true });
        }

        if (typeof options.target === 'string') {
            options.target = constants.CaptureTarget[options.target];
        }

        return CameraNativeModule.capture(options);
    },

    stopCapture() {
        if (this.state.isRecording) {
            CameraNativeModule.stopCapture();
            this.setState({ isRecording: false });
        }
    }

});

const ReactCameraView = requireNativeComponent('RCTIONCamera', CameraView);

const styles = StyleSheet.create({
    base: { },
});

CameraView.constants = constants;
CameraView.checkVideoPermission = CameraNativeModule.checkVideoPermission
CameraView.requestVideoPermission = CameraNativeModule.requestVideoPermission
CameraView.checkAudioPermission = CameraNativeModule.checkAudioPermission
CameraView.requestAudioPermission = CameraNativeModule.requestAudioPermission

module.exports = CameraView;
