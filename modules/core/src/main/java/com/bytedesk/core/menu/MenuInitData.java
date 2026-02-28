/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-03-11 08:54:35
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-06-04 17:12:37
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 * 
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.menu;

import java.util.ArrayList;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Default menu seeds for admin/desktop dashboards.
 * Based on current frontend routes and dashboard menus, excluding auth/redirect/debug/404 routes.
 */
public final class MenuInitData {

        private MenuInitData() {
        }

        public static List<MenuSeed> adminMenus() {
                List<MenuSeed> seeds = new ArrayList<>();
                // Top-level
                seeds.add(MenuSeed.root("/welcome", "dashboard", "dashboard", "menu.dashboard"));
                seeds.add(MenuSeed.root("/team", "team", "team", "menu.team"));
                seeds.add(MenuSeed.child("/team/member", "member", "user", "/team", "menu.team.member"));
                seeds.add(MenuSeed.child("/team/role", "role", "safety", "/team", "menu.team.role"));
                seeds.add(MenuSeed.child("/team/action", "action", "lock", "/team", "menu.team.action"));
                seeds.add(MenuSeed.child("/team/auth", "auth", "idcard", "/team", "menu.team.auth"));
                seeds.add(MenuSeed.child("/team/task", "task", "calendar", "/team", "menu.team.task"));
                seeds.add(MenuSeed.child("/team/org", "company", "bank", "/team", "menu.team.company"));

                seeds.add(MenuSeed.root("/service", "service", "customerService", "menu.service"));
                seeds.add(MenuSeed.child("/service/agent", "agent", "user", "/service", "menu.service.agent"));
                seeds.add(MenuSeed.child("/service/workgroup", "group", "apartment", "/service", "menu.service.group"));
                seeds.add(MenuSeed.child("/service/unified", "unified", "deploymentUnit", "/service", "menu.service.unified"));
                seeds.add(MenuSeed.child("/service/thread", "thread", "message", "/service", "menu.service.thread"));
                seeds.add(MenuSeed.child("/service/message", "message", "mail", "/service", "menu.service.message"));
                seeds.add(MenuSeed.child("/service/tag", "tag", "tags", "/service", "menu.service.tag"));
                seeds.add(MenuSeed.child("/service/channel", "channel", "appstore", "/service", "menu.service.channel"));
                seeds.add(MenuSeed.child("/service/quickbutton", "quickbutton", "thunderbolt", "/service", "menu.service.quickbutton"));
                seeds.add(MenuSeed.child("/service/form", "form", "form", "/service", "menu.service.form"));
                seeds.add(MenuSeed.child("/service/workflow", "workflow", "branches", "/service", "menu.service.workflow"));

                seeds.add(MenuSeed.root("/video", "video", "videoCamera", "menu.video"));
                seeds.add(MenuSeed.child("/video/recording", "recording", "videoCamera", "/video", "menu.video.recording"));

                seeds.add(MenuSeed.root("/call", "callcenter", "phone", "menu.callcenter"));
                seeds.add(MenuSeed.child("/call/cdr", "cdr", "fileText", "/call", "menu.callcenter.cdr"));
                seeds.add(MenuSeed.child("/call/conference", "conference", "team", "/call", "menu.callcenter.conference"));
                seeds.add(MenuSeed.child("/call/gateway", "gateway", "cloudServer", "/call", "menu.callcenter.gateway"));
                seeds.add(MenuSeed.child("/call/number", "number", "number", "/call", "menu.callcenter.number"));

                seeds.add(MenuSeed.root("/ai", "robot", "robot", "menu.robot"));
                seeds.add(MenuSeed.child("/ai/robot", "robot", "robot", "/ai", "menu.robot.robot"));
                seeds.add(MenuSeed.child("/ai/agent", "agent", "user", "/ai", "menu.robot.agent"));
                seeds.add(MenuSeed.child("/ai/prompt", "prompt", "message", "/ai", "menu.robot.prompt"));
                seeds.add(MenuSeed.child("/ai/model", "model", "bulb", "/ai", "menu.robot.model"));
                seeds.add(MenuSeed.child("/ai/message", "message", "mail", "/ai", "menu.robot.message"));
                seeds.add(MenuSeed.child("/ai/tools", "tools", "tool", "/ai", "menu.robot.tools"));
                seeds.add(MenuSeed.child("/ai/mcp", "mcp", "api", "/ai", "menu.robot.mcp"));
                seeds.add(MenuSeed.child("/ai/skills", "skills", "rocket", "/ai", "menu.robot.skills"));

                seeds.add(MenuSeed.root("/kb", "kbase", "book", "menu.kbase"));
                seeds.add(MenuSeed.child("/kb/article", "helpcenter", "fileText", "/kb", "menu.kbase.helpcenter"));
                seeds.add(MenuSeed.child("/kb/llm", "llm", "experiment", "/kb", "menu.kbase.llm"));
                seeds.add(MenuSeed.child("/kb/autoreply", "autoreply", "thunderbolt", "/kb", "menu.kbase.autoreply"));
                seeds.add(MenuSeed.child("/kb/quickreply", "quickreply", "snippets", "/kb", "menu.kbase.quickreply"));
                seeds.add(MenuSeed.child("/kb/taboo", "taboo", "stop", "/kb", "menu.kbase.taboo"));
                seeds.add(MenuSeed.child("/kb/upload", "upload", "upload", "/kb", "menu.kbase.upload"));

                seeds.add(MenuSeed.root("/ticket", "ticket", "profile", "menu.ticket"));
                seeds.add(MenuSeed.child("/ticket/data", "data", "table", "/ticket", "menu.ticket.data"));
                seeds.add(MenuSeed.child("/ticket/process", "process", "branches", "/ticket", "menu.ticket.process"));
                seeds.add(MenuSeed.child("/ticket/settings", "settings", "setting", "/ticket", "menu.ticket.settings"));

                
                seeds.add(MenuSeed.root("/voc", "voc", "muted", "menu.voc"));
                seeds.add(MenuSeed.child("/voc/feedback", "feedback", "message", "/voc", "menu.voc.feedback"));
                seeds.add(MenuSeed.child("/voc/complaint", "complaint", "warning", "/voc", "menu.voc.complaint"));
                seeds.add(MenuSeed.child("/voc/comment", "comment", "comment", "/voc", "menu.voc.comment"));
                seeds.add(MenuSeed.child("/voc/opinion", "opinion", "bulb", "/voc", "menu.voc.opinion"));

                seeds.add(MenuSeed.root("/marketing", "marketing", "form", "menu.marketing"));
                seeds.add(MenuSeed.child("/marketing/blog", "blog", "book", "/marketing", "menu.marketing.blog"));

                seeds.add(MenuSeed.root("/crm", "crm", "aim", "menu.crm"));
                seeds.add(MenuSeed.child("/crm/customer", "customer", "user", "/crm", "menu.crm.customer"));
                seeds.add(MenuSeed.child("/crm/lead", "lead", "profile", "/crm", "menu.crm.lead"));
                seeds.add(MenuSeed.child("/crm/opportunity", "opportunity", "trophy", "/crm", "menu.crm.opportunity"));
                seeds.add(MenuSeed.child("/crm/product", "product", "shopping", "/crm", "menu.crm.product"));
                seeds.add(MenuSeed.child("/crm/contract", "contract", "file", "/crm", "menu.crm.contract"));
                seeds.add(MenuSeed.child("/crm/tender", "tender", "fileText", "/crm", "menu.crm.tender"));

                seeds.add(MenuSeed.root("/bi", "bi", "fund", "menu.bi"));
                seeds.add(MenuSeed.root("/quality", "quality", "check", "menu.quality"));
                seeds.add(MenuSeed.root("/setting", "setting", "setting", "menu.setting"));
                seeds.add(MenuSeed.root("/open", "open", "subnode", "menu.open"));
                seeds.add(MenuSeed.root("https://www.weiyuai.cn/docs/zh-CN/", "docs", "question", "menu.docs"));

                seeds.add(MenuSeed.root("/super", "super", "crown", "menu.super"));
                seeds.add(MenuSeed.child("/super/org", "org", "bank", "/super", "menu.super.org"));
                seeds.add(MenuSeed.child("/super/user", "user", "user", "/super", "menu.super.user"));
                seeds.add(MenuSeed.child("/super/role", "role", "safety", "/super", "menu.super.role"));
                seeds.add(MenuSeed.child("/super/auth", "auth", "idcard", "/super", "menu.super.auth"));
                seeds.add(MenuSeed.child("/super/push", "push", "notification", "/super", "menu.super.push"));
                seeds.add(MenuSeed.child("/super/thread", "thread", "message", "/super", "menu.super.thread"));
                seeds.add(MenuSeed.child("/super/message", "message", "mail", "/super", "menu.super.message"));
                seeds.add(MenuSeed.child("/super/action", "action", "lock", "/super", "menu.super.action"));
                seeds.add(MenuSeed.child("/super/agent", "agent", "user", "/super", "menu.super.agent"));
                seeds.add(MenuSeed.child("/super/workgroup", "workgroup", "apartment", "/super", "menu.super.workgroup"));
                seeds.add(MenuSeed.child("/super/robot", "robot", "robot", "/super", "menu.super.robot"));
                seeds.add(MenuSeed.child("/super/ticket", "ticket", "profile", "/super", "menu.super.ticket"));
                seeds.add(MenuSeed.child("/super/menu", "menu", "menu", "/super", "menu.super.menu"));
                seeds.add(MenuSeed.child("/super/kbase", "kbase", "book", "/super", "menu.super.kbase"));
                seeds.add(MenuSeed.child("/super/city", "city", "environment", "/super", "menu.super.city"));
                seeds.add(MenuSeed.child("/super/provider", "llmodel", "bulb", "/super", "menu.super.llmodel"));
                seeds.add(MenuSeed.child("/super/license", "license", "safety", "/super", "menu.super.license"));
                seeds.add(MenuSeed.child("/super/server", "server", "cloudServer", "/super", "menu.super.server"));

                return seeds;
        }

        public static List<MenuSeed> desktopMenus() {
                List<MenuSeed> seeds = new ArrayList<>();
                seeds.add(MenuSeed.root("/chat", "chat", "message", "menu.dashboard.chat"));
                seeds.add(MenuSeed.root("/ticket", "ticket", "inbox", "menu.dashboard.ticket"));
                seeds.add(MenuSeed.root("/data", "data", "lineChart", "menu.dashboard.data"));
                seeds.add(MenuSeed.root("/task", "calendar", "schedule", "menu.dashboard.calendar"));
                seeds.add(MenuSeed.root("/callcenter", "callcenter", "phone", "menu.dashboard.callcenter"));
                seeds.add(MenuSeed.root("/contact", "contact", "contacts", "menu.dashboard.contact"));
                seeds.add(MenuSeed.root("/setting", "mine", "setting", "menu.dashboard.mine"));
                return seeds;
        }

        @Getter
        @AllArgsConstructor
        public static class MenuSeed {
                private final String key;
                private final String name;
                private final String icon;
                private final String link;
                private final Integer order;
                private final String parentKey;
                private final String description;
                private final String color;
                private final String nickname;

                public static MenuSeed root(String link, String name, String icon, String nickname) {
                        return new MenuSeed(link, name, icon, link, 0, null, null, MenuEntity.DEFAULT_COLOR, nickname);
                }

                public static MenuSeed child(String link, String name, String icon, String parentKey, String nickname) {
                        return new MenuSeed(link, name, icon, link, 0, parentKey, null, MenuEntity.DEFAULT_COLOR, nickname);
                }
        }
}