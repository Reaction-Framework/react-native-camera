#import <UIKit/UIKit.h>
#import <AVFoundation/AVFoundation.h>
#import "RCTIONCameraFocusSquare.h"

@class RCTIONCameraManager;

@interface RCTIONCameraView : UIView

@property (nonatomic) RCTIONCameraManager *manager;
@property (nonatomic) RCTBridge *bridge;
@property (nonatomic) RCTIONCameraFocusSquare *camFocus;

- (id)initWithManager:(RCTIONCameraManager*)manager
               bridge:(RCTBridge *)bridge;

@end
