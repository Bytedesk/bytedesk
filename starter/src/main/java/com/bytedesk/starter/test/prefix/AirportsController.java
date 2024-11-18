/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-09-28 13:06:16
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-09-28 13:50:17
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  仅支持企业内部员工自用，严禁私自用于销售、二次销售或者部署SaaS方式销售 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.starter.test.prefix;

import static org.springframework.web.bind.annotation.RequestMethod.DELETE;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.OPTIONS;
import static org.springframework.web.bind.annotation.RequestMethod.PATCH;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

import java.util.List;

import com.redis.om.spring.autocomplete.Suggestion;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.redis.om.spring.repository.query.autocomplete.AutoCompleteOptions;

// https://github.com/redis/redis-om-spring
// https://github.com/redis-developer/redis-om-autocomplete-demo
@CrossOrigin( //
    methods = { POST, GET, OPTIONS, DELETE, PATCH }, //
    maxAge = 3600, //
    allowedHeaders = { //
        "x-requested-with", "origin", "content-type", "accept", "accept-patch" //
    }, //
    origins = "*" //
)
@RestController
@RequestMapping("/airports")
public class AirportsController {

  @Autowired
  private AirportsRepository repository;

  // http://localhost:9003/airports/search/b
  @GetMapping("/search/{q}")
  public List<Suggestion> query(@PathVariable("q") String query) {
    List<Suggestion> suggestions = repository //
        .autoCompleteNameOptions(query, AutoCompleteOptions.get().withPayload());
    return suggestions;
  }

}
