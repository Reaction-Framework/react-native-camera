#import "RCTViewManager.h"
#import <AVFoundation/AVFoundation.h>

@class RCTIONCameraView;

typedef NS_ENUM(NSInteger, RCTIONCameraAspect) {
  RCTIONCameraAspectFill = 0,
  RCTIONCameraAspectFit = 1,
  RCTIONCameraAspectStretch = 2
};

typedef NS_ENUM(NSInteger, RCTIONCameraCaptureMode) {
  RCTIONCameraCaptureModeStill = 0,
  RCTIONCameraCaptureModeVideo = 1
};

typedef NS_ENUM(NSInteger, RCTIONCameraCaptureTarget) {
  RCTIONCameraCaptureTargetMemory = 0,
  RCTIONCameraCaptureTargetDisk = 1,
  RCTIONCameraCaptureTargetCameraRoll = 2
};

typedef NS_ENUM(NSInteger, RCTIONCameraOrientation) {
  RCTIONCameraOrientationAuto = 0,
  RCTIONCameraOrientationLandscapeLeft = AVCaptureVideoOrientationLandscapeLeft,
  RCTIONCameraOrientationLandscapeRight = AVCaptureVideoOrientationLandscapeRight,
  RCTIONCameraOrientationPortrait = AVCaptureVideoOrientationPortrait,
  RCTIONCameraOrientationPortraitUpsideDown = AVCaptureVideoOrientationPortraitUpsideDown
};

typedef NS_ENUM(NSInteger, RCTIONCameraType) {
  RCTIONCameraTypeFront = AVCaptureDevicePositionFront,
  RCTIONCameraTypeBack = AVCaptureDevicePositionBack
};

typedef NS_ENUM(NSInteger, RCTIONCameraFlashMode) {
  RCTIONCameraFlashModeOff = AVCaptureFlashModeOff,
  RCTIONCameraFlashModeOn = AVCaptureFlashModeOn,
  RCTIONCameraFlashModeAuto = AVCaptureFlashModeAuto
};

typedef NS_ENUM(NSInteger, RCTIONCameraTorchMode) {
  RCTIONCameraTorchModeOff = AVCaptureTorchModeOff,
  RCTIONCameraTorchModeOn = AVCaptureTorchModeOn,
  RCTIONCameraTorchModeAuto = AVCaptureTorchModeAuto
};

@interface RCTIONCameraManager : RCTViewManager<AVCaptureMetadataOutputObjectsDelegate, AVCaptureFileOutputRecordingDelegate>

@property (nonatomic) dispatch_queue_t sessionQueue;
@property (nonatomic) AVCaptureSession *session;
@property (nonatomic) AVCaptureDeviceInput *audioCaptureDeviceInput;
@property (nonatomic) AVCaptureDeviceInput *videoCaptureDeviceInput;
@property (nonatomic) AVCaptureStillImageOutput *stillImageOutput;
@property (nonatomic) AVCaptureMovieFileOutput *movieFileOutput;
@property (nonatomic) AVCaptureMetadataOutput *metadataOutput;
@property (nonatomic) id runtimeErrorHandlingObserver;
@property (nonatomic) NSInteger presetCamera;
@property (nonatomic) AVCaptureVideoPreviewLayer *previewLayer;
@property (nonatomic) NSInteger videoTarget;
@property (nonatomic, strong) RCTPromiseResolveBlock recordingResolve;
@property (nonatomic, strong) RCTPromiseRejectBlock recordingReject;
@property (nonatomic, strong) RCTIONCameraView *camera;

- (AVCaptureDevice *)deviceWithMediaType:(NSString *)mediaType
                      preferringPosition:(AVCaptureDevicePosition)position;

- (void)initializeCaptureSessionInput:(NSString*)type;

- (void)startSession;

- (void)stopSession;

- (void)focusAtThePoint:(CGPoint) atPoint;

- (void)zoom:(CGFloat)velocity reactTag:(NSNumber *)reactTag;


- (void)checkVideoPermission:(RCTPromiseResolveBlock)resolve
                    rejecter:(RCTPromiseRejectBlock)reject;

- (void)requestVideoPermission:(RCTPromiseResolveBlock)resolve
                      rejecter:(RCTPromiseRejectBlock)reject;

- (void)checkAudioPermission:(RCTPromiseResolveBlock)resolve
                    rejecter:(RCTPromiseRejectBlock)reject;

- (void)requestAudioPermission:(RCTPromiseResolveBlock)resolve
                      rejecter:(RCTPromiseRejectBlock)reject;

- (void)capture:(NSDictionary *)options
       resolver:(RCTPromiseResolveBlock)resolve
       rejecter:(RCTPromiseRejectBlock)reject;

- (void)stopCapture;

- (void)changeAspect:(NSString *)aspect;

- (void)changeCamera:(NSInteger)camera;

- (void)changeOrientation:(NSInteger)orientation;

- (void)changeFlashMode:(NSInteger)flashMode;

- (void)changeTorchMode:(NSInteger)torchMode;

@end
