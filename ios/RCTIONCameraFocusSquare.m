#import <QuartzCore/QuartzCore.h>
#import "RCTIONCameraFocusSquare.h"

const float squareLength = 80.0f;

@implementation RCTIONCameraFocusSquare

- (id)initWithFrame:(CGRect)frame
{
    self = [super initWithFrame:frame];
    if (self) {
        [self setBackgroundColor:[UIColor clearColor]];
        [self.layer setBorderWidth:2.0];
        [self.layer setCornerRadius:4.0];
        [self.layer setBorderColor:[UIColor whiteColor].CGColor];
        
        CABasicAnimation* selectionAnimation = [CABasicAnimation
                                                animationWithKeyPath:@"borderColor"];
        selectionAnimation.toValue = (id)[UIColor blueColor].CGColor;
        selectionAnimation.repeatCount = 8;
        [self.layer addAnimation:selectionAnimation
                          forKey:@"selectionAnimation"];
        
    }
    return self;
}
@end
