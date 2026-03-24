/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-05-11 18:25:36
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-11-29 12:00:00
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.training.course;

import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.context.annotation.Description;

import com.bytedesk.core.annotation.ActionAnnotation;
import com.bytedesk.core.base.BaseRestController;
import com.bytedesk.core.constant.I18Consts;
import com.bytedesk.core.utils.JsonResult;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;

@RestController
@RequestMapping("/api/v1/course")
@AllArgsConstructor
@Tag(name = "Course Management", description = "Course management APIs for organizing and categorizing content with courses")
@Description("Course Management Controller - Content courseging and categorization APIs")
public class CourseRestController extends BaseRestController<CourseRequest, CourseRestService> {

    private final CourseRestService courseRestService;

    @ActionAnnotation(title = I18Consts.I18N_COURSE, action = I18Consts.I18N_ACTION_QUERY_ORG, description = "query course by org")
    @Operation(summary = "Query Courses by Organization", description = "Retrieve courses for the current organization")
    @PreAuthorize(CoursePermissions.HAS_COURSE_READ)
    @Override
    @GetMapping("/query/org")
    public ResponseEntity<?> queryByOrg(CourseRequest request) {
        
        Page<CourseResponse> courses = courseRestService.queryByOrg(request);

        return ResponseEntity.ok(JsonResult.success(courses));
    }

    @ActionAnnotation(title = I18Consts.I18N_COURSE, action = I18Consts.I18N_ACTION_QUERY_USER, description = "query course by user")
    @Operation(summary = "Query Courses by User", description = "Retrieve courses for the current user")
    @PreAuthorize(CoursePermissions.HAS_COURSE_READ)
    @Override
    @GetMapping({"/query", "/query/user"})
    public ResponseEntity<?> queryByUser(CourseRequest request) {
        
        Page<CourseResponse> courses = courseRestService.queryByUser(request);

        return ResponseEntity.ok(JsonResult.success(courses));
    }

    @ActionAnnotation(title = I18Consts.I18N_COURSE, action = I18Consts.I18N_ACTION_QUERY_DETAIL, description = "query course by uid")
    @Operation(summary = "Query Course by UID", description = "Retrieve a specific course by its unique identifier")
    @PreAuthorize(CoursePermissions.HAS_COURSE_READ)
    @Override
    @GetMapping("/query/uid")
    public ResponseEntity<?> queryByUid(CourseRequest request) {
        
        CourseResponse course = courseRestService.queryByUid(request);

        return ResponseEntity.ok(JsonResult.success(course));
    }

    @ActionAnnotation(title = I18Consts.I18N_COURSE, action = I18Consts.I18N_ACTION_CREATE, description = "create course")
    @Operation(summary = "Create Course", description = "Create a new course")
    @Override
    @PreAuthorize(CoursePermissions.HAS_COURSE_CREATE)
    @PostMapping("/create")
    public ResponseEntity<?> create(@RequestBody CourseRequest request) {
        
        CourseResponse course = courseRestService.create(request);

        return ResponseEntity.ok(JsonResult.success(course));
    }

    @ActionAnnotation(title = I18Consts.I18N_COURSE, action = I18Consts.I18N_ACTION_UPDATE, description = "update course")
    @Operation(summary = "Update Course", description = "Update an existing course")
    @Override
    @PreAuthorize(CoursePermissions.HAS_COURSE_UPDATE)
    @PostMapping("/update")
    public ResponseEntity<?> update(@RequestBody CourseRequest request) {
        
        CourseResponse course = courseRestService.update(request);

        return ResponseEntity.ok(JsonResult.success(course));
    }

    @ActionAnnotation(title = I18Consts.I18N_COURSE, action = I18Consts.I18N_ACTION_DELETE, description = "delete course")
    @Operation(summary = "Delete Course", description = "Delete a course")
    @Override
    @PreAuthorize(CoursePermissions.HAS_COURSE_DELETE)
    @PostMapping("/delete")
    public ResponseEntity<?> delete(@RequestBody CourseRequest request) {
        
        courseRestService.delete(request);

        return ResponseEntity.ok(JsonResult.success());
    }

    @ActionAnnotation(title = I18Consts.I18N_COURSE, action = I18Consts.I18N_ACTION_EXPORT, description = "export course")
    @Operation(summary = "Export Courses", description = "Export courses to Excel format")
    @Override
    @PreAuthorize(CoursePermissions.HAS_COURSE_EXPORT)
    @GetMapping("/export")
    public Object export(CourseRequest request, HttpServletResponse response) {
        return exportTemplate(
            request,
            response,
            courseRestService,
            CourseExcel.class,
            "Course",
            "course"
        );
    }

    
    
}