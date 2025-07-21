/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-05-11 18:25:36
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-07-21 10:28:07
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.ai.alibaba.booking;

import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
// import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.context.annotation.Description;

import com.bytedesk.core.annotation.ActionAnnotation;
import com.bytedesk.core.base.BaseRestController;
import com.bytedesk.core.utils.JsonResult;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;

@RestController
@RequestMapping("/api/v1/booking")
@AllArgsConstructor
@Tag(name = "Booking Management", description = "Booking management APIs for organizing and categorizing content with bookings")
@Description("Booking Management Controller - Content booking and categorization APIs")
public class BookingRestController extends BaseRestController<BookingRequest> {

    private final BookingRestService bookingRestService;

    // @PreAuthorize(RolePermissions.ROLE_ADMIN)
    @ActionAnnotation(title = "标签", action = "组织查询", description = "query booking by org")
    @Operation(summary = "Query Bookings by Organization", description = "Retrieve bookings for the current organization")
    @Override
    public ResponseEntity<?> queryByOrg(BookingRequest request) {
        
        Page<BookingResponse> bookings = bookingRestService.queryByOrg(request);

        return ResponseEntity.ok(JsonResult.success(bookings));
    }

    @ActionAnnotation(title = "标签", action = "用户查询", description = "query booking by user")
    @Operation(summary = "Query Bookings by User", description = "Retrieve bookings for the current user")
    @Override
    public ResponseEntity<?> queryByUser(BookingRequest request) {
        
        Page<BookingResponse> bookings = bookingRestService.queryByUser(request);

        return ResponseEntity.ok(JsonResult.success(bookings));
    }

    @ActionAnnotation(title = "标签", action = "查询详情", description = "query booking by uid")
    @Operation(summary = "Query Booking by UID", description = "Retrieve a specific booking by its unique identifier")
    @Override
    public ResponseEntity<?> queryByUid(BookingRequest request) {
        
        BookingResponse booking = bookingRestService.queryByUid(request);

        return ResponseEntity.ok(JsonResult.success(booking));
    }

    @ActionAnnotation(title = "标签", action = "新建", description = "create booking")
    @Operation(summary = "Create Booking", description = "Create a new booking")
    @Override
    // @PreAuthorize("hasAuthority('TAG_CREATE')")
    public ResponseEntity<?> create(BookingRequest request) {
        
        BookingResponse booking = bookingRestService.create(request);

        return ResponseEntity.ok(JsonResult.success(booking));
    }

    @ActionAnnotation(title = "标签", action = "更新", description = "update booking")
    @Operation(summary = "Update Booking", description = "Update an existing booking")
    @Override
    // @PreAuthorize("hasAuthority('TAG_UPDATE')")
    public ResponseEntity<?> update(BookingRequest request) {
        
        BookingResponse booking = bookingRestService.update(request);

        return ResponseEntity.ok(JsonResult.success(booking));
    }

    @ActionAnnotation(title = "标签", action = "删除", description = "delete booking")
    @Operation(summary = "Delete Booking", description = "Delete a booking")
    @Override
    // @PreAuthorize("hasAuthority('TAG_DELETE')")
    public ResponseEntity<?> delete(BookingRequest request) {
        
        bookingRestService.delete(request);

        return ResponseEntity.ok(JsonResult.success());
    }

    @ActionAnnotation(title = "标签", action = "导出", description = "export booking")
    @Operation(summary = "Export Bookings", description = "Export bookings to Excel format")
    @Override
    // @PreAuthorize("hasAuthority('TAG_EXPORT')")
    @GetMapping("/export")
    public Object export(BookingRequest request, HttpServletResponse response) {
        return exportTemplate(
            request,
            response,
            bookingRestService,
            BookingExcel.class,
            "标签",
            "booking"
        );
    }

    
    
}