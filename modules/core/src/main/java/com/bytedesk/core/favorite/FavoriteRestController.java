/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-05-11 18:25:36
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-08-01 15:10:33
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.favorite;

import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bytedesk.core.base.BaseRestController;
import com.bytedesk.core.utils.JsonResult;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;

@RestController
@RequestMapping("/api/v1/favorite")
@AllArgsConstructor
@Tag(name = "Favorite Management", description = "Favorite management APIs for managing user favorites and bookmarks")
public class FavoriteRestController extends BaseRestController<FavoriteRequest> {

    private final FavoriteRestService favoriteRestService;

    // @PreAuthorize(RolePermissions.ROLE_ADMIN)
    @Operation(summary = "Query Favorites by Organization", description = "Retrieve favorites for the current organization (Admin only)")
    @Override
    public ResponseEntity<?> queryByOrg(FavoriteRequest request) {
        
        Page<FavoriteResponse> favorites = favoriteRestService.queryByOrg(request);

        return ResponseEntity.ok(JsonResult.success(favorites));
    }

    @Operation(summary = "Query Favorites by User", description = "Retrieve favorites for the current user")
    @Override
    public ResponseEntity<?> queryByUser(FavoriteRequest request) {
        
        Page<FavoriteResponse> favorites = favoriteRestService.queryByUser(request);

        return ResponseEntity.ok(JsonResult.success(favorites));
    }

    @Operation(summary = "Create Favorite", description = "Create a new favorite item")
    @Override
    public ResponseEntity<?> create(FavoriteRequest request) {
        
        FavoriteResponse favorite = favoriteRestService.create(request);

        return ResponseEntity.ok(JsonResult.success(favorite));
    }

    @Operation(summary = "Update Favorite", description = "Update an existing favorite item")
    @Override
    public ResponseEntity<?> update(FavoriteRequest request) {
        
        FavoriteResponse favorite = favoriteRestService.update(request);

        return ResponseEntity.ok(JsonResult.success(favorite));
    }

    @Operation(summary = "Delete Favorite", description = "Delete a favorite item")
    @Override
    public ResponseEntity<?> delete(FavoriteRequest request) {
        
        favoriteRestService.delete(request);

        return ResponseEntity.ok(JsonResult.success());
    }

    @Override
    public Object export(FavoriteRequest request, HttpServletResponse response) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'export'");
    }

    @Override
    public ResponseEntity<?> queryByUid(FavoriteRequest request) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'queryByUid'");
    }
    
}