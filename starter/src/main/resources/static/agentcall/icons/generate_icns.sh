
###
 # @Author: jackning 270580156@qq.com
 # @Date: 2024-03-27 10:52:01
 # @LastEditors: jackning 270580156@qq.com
 # @LastEditTime: 2025-05-10 10:50:19
 # @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 #   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 #  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 #  仅支持企业内部员工自用，严禁私自用于销售、二次销售或者部署SaaS方式销售 
 #  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 #  contact: 270580156@qq.com 
 # 联系：270580156@qq.com
 # Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
### 
mkdir logo.iconset
sips -z 16 16     logo.png --out logo.iconset/icon_16x16.png
sips -z 32 32     logo.png --out logo.iconset/icon_16x16@2x.png
sips -z 32 32     logo.png --out logo.iconset/icon_32x32.png
sips -z 64 64     logo.png --out logo.iconset/icon_32x32@2x.png
sips -z 128 128   logo.png --out logo.iconset/icon_128x128.png
sips -z 256 256   logo.png --out logo.iconset/icon_128x128@2x.png
sips -z 256 256   logo.png --out logo.iconset/icon_256x256.png
sips -z 512 512   logo.png --out logo.iconset/icon_256x256@2x.png
sips -z 512 512   logo.png --out logo.iconset/icon_512x512.png
sips -z 1024 1024   logo.png --out logo.iconset/icon_512x512@2x.png
iconutil -c icns logo.iconset -o icon.icns