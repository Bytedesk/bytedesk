//
//  WBiCloudManager.h
//  WBDocumentBrowserDemo
//
//  Created by Mr_Lucky on 2018/11/5.
//  Copyright © 2018 wenbo. All rights reserved.
//

#import <Foundation/Foundation.h>

typedef void(^downloadCallBack)(id contents, NSString *type);

NS_ASSUME_NONNULL_BEGIN

@interface BDWBiCloudManager : NSObject

/*  < 查询文件列表 > */
@property (nonatomic, strong) NSMetadataQuery *dataQuery;
/*  < 文件查询列表 > */
@property (nonatomic, copy) void (^queryDidFinished)(NSArray *queryList);

/*  < 判断是否有iCloud权限 > */
+ (BOOL)iCloudEnable;

/*  < 默认iCloud地址 > */
+ (NSURL *)defaultiCloudURL;

/*  < 指定identifier容器 > */
+ (NSURL *)iCloudURLForIdentifier:(NSString *)identifier;

/*  < 从iCloud下载文件 > */
+ (void)wb_downloadWithDocumentURL:(NSURL *)url
                    completedBlock:(downloadCallBack)completedBlock;
/*  < 查询文件列表 > */
- (BOOL)wb_queryDocumentList;
/*  < 上传文件 > */
- (void)wb_uploadDocumentWithFileName:(NSString *)fileName;

@end

NS_ASSUME_NONNULL_END
