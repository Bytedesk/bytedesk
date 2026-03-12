package com.bytedesk.core.city;

// import org.springframework.ai.tool.annotation.Tool;
// import org.springframework.ai.tool.annotation.ToolParam;
// import org.springframework.stereotype.Component;

// import com.bytedesk.core.base.BaseTools;
// import com.fasterxml.jackson.databind.ObjectMapper;

// @Component
// public class CityTools extends BaseTools<CityRequest, CityResponse> {

//     public CityTools(CityRestService restService, ObjectMapper objectMapper) {
//         super("city", CityRequest.class, restService, objectMapper);
//     }

//     @Tool(description = "Query city by uid")
//     public Object cityQueryByUid(
//             @ToolParam(description = "uid") String uid,
//             @ToolParam(description = "orgUid", required = false) String orgUid) {
//         return doQueryByUid(uid, orgUid);
//     }

//     @Tool(description = "Query city by org with request json")
//     public Object cityQueryByOrg(@ToolParam(description = "CityRequest json") String requestJson) {
//         return doQueryByOrg(requestJson);
//     }

//     @Tool(description = "Query city by user with request json")
//     public Object cityQueryByUser(@ToolParam(description = "CityRequest json") String requestJson) {
//         return doQueryByUser(requestJson);
//     }

//     @Tool(description = "Create city with request json")
//     public Object cityCreate(@ToolParam(description = "CityRequest json") String requestJson) {
//         return doCreate(requestJson);
//     }

//     @Tool(description = "Update city with request json")
//     public Object cityUpdate(@ToolParam(description = "CityRequest json") String requestJson) {
//         return doUpdate(requestJson);
//     }

//     @Tool(description = "Delete city by uid")
//     public Object cityDeleteByUid(@ToolParam(description = "uid") String uid) {
//         return doDeleteByUid(uid);
//     }
// }
