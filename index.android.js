import React, {
    requireNativeComponent,
    PropTypes,
    NativeModules,
    StyleSheet,
    View } from 'react-native';

const CameraNativeModule = NativeModules.IONCameraModule;

const constants = {
    Aspect: CameraNativeModule.Aspect,
    Type: CameraNativeModule.Type,
    CaptureTarget: CameraNativeModule.CaptureTarget,
    PermissionStatus: {
        notDetermined: 0,
        authorized: 1,
        denied: -1
    }
};

const CAMERA_REF = 'camera';

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

    setNativeProps(props) {
        this.refs[CAMERA_REF].setNativeProps(props);
    },

    getDefaultProps() {
        return ({
            aspect: constants.Aspect.fill,
            type: constants.Type.back,
            captureTarget: constants.CaptureTarget.cameraRoll
        });
    },

    render() {
        const style = [styles.base, this.props.style];

        let aspect = this.props.aspect;
        let type = this.props.type;

        if (typeof aspect === 'string') {
            aspect = constants.Aspect[aspect];
        }

        if (typeof type === 'string') {
            type = constants.Type[type];
        }

        const nativeProps = Object.assign({}, this.props, {
            style,
            aspect: aspect,
            type: type
        });

        return <ReactCameraView ref={CAMERA_REF} {...nativeProps} />;
    },

    async capture(options) {
        options = Object.assign({}, {
            type: this.props.type,
            target: this.props.captureTarget
        }, options);

        if (typeof options.target === 'string') {
            options.target = constants.CaptureTarget[options.target];
        }

        if (typeof options.type === 'string') {
            options.type = constants.Type[options.type];
        }

        return CameraNativeModule.capture(options);
    }
});

const ReactCameraView = requireNativeComponent('RCTIONCameraView', CameraView);

const styles = StyleSheet.create({
    base: { },
});

CameraView.constants = constants;
CameraView.getVideoPermissionStatus = async () => constants.PermissionStatus.authorized;
CameraView.requestVideoPermission = async () => null;
CameraView.getAudioPermissionStatus = async () => constants.PermissionStatus.authorized;
CameraView.requestAudioPermission = async () => null;

export default CameraView;
