/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-01-29 16:20:17
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-04-02 16:50:05
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  仅支持企业内部员工自用，严禁私自用于销售、二次销售或者部署SaaS方式销售 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.team.member;

import java.util.Optional;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import com.bytedesk.core.constant.AvatarConsts;
import com.bytedesk.core.constant.TypeConsts;
// import com.bytedesk.core.auth.AuthService;
import com.bytedesk.core.rbac.user.User;
import com.bytedesk.core.rbac.user.UserService;
import com.bytedesk.core.utils.BdConvertUtils;
import com.bytedesk.core.utils.JsonResult;
import com.bytedesk.core.utils.Utils;
import com.bytedesk.team.department.Department;
import com.bytedesk.team.department.DepartmentRepository;
import lombok.AllArgsConstructor;

// @Slf4j
@Service
@AllArgsConstructor
public class MemberService {

    private UserService userService;

    private DepartmentRepository departmentRepository;

    private MemberRepository memberRepository;

    private ModelMapper modelMapper;

    public JsonResult<?> create(MemberRequest memberRequest) {
        // 
        // if (userService.existsByMobile(memberRequest.getMobile())) {
        //     return JsonResult.error("mobile already exist");
        // }
        // if (userService.existsByEmail(memberRequest.getEmail())) {
        //     return JsonResult.error("email already exist");
        // }
        //
        Optional<Member> memberOptional = memberRepository.findByUser_Mobile(memberRequest.getMobile());
        if (!memberOptional.isPresent()) {
            Member member = modelMapper.map(memberRequest, Member.class);
            member.setMid(Utils.getUid());
            //
            Optional<Department> depOptional = departmentRepository.findByDid(memberRequest.getDepDid());
            if (depOptional.isPresent()) {
                member.setDepartment(depOptional.get());
                member.setOrganization(depOptional.get().getOrganization());
            } else {
                return null;
            }
            // 
            User user;
            Optional<User> userOptional = userService.findByMobile(memberRequest.getMobile());
            if (!userOptional.isPresent()) {
                user = userService.createUser(
                        memberRequest.getNickname(),
                        AvatarConsts.DEFAULT_AVATAR_URL,
                        memberRequest.getPassword(),
                        memberRequest.getMobile(),
                        memberRequest.getEmail(),
                        memberRequest.isVerified(),
                        member.getOrganization().getOid()
                    );
            } else {
                    user = userOptional.get();
                // user = userService.updateUser(
                //         userOptional.get(),
                //         memberRequest.getPassword(),
                //         memberRequest.getMobile(),
                //         memberRequest.getEmail()
                //     );
            }
            member.setUser(user);
            // 
            MemberResponse result = save(member);

            return JsonResult.success(result);
        }
        //
        return update(memberOptional.get(), memberRequest);
    }

    public JsonResult<?> update(Member member, MemberRequest memberRequest) {

        member.setJobNo(memberRequest.getJobNo());
        member.setNickname(memberRequest.getNickname());
        member.setSeatNo(memberRequest.getSeatNo());
        member.setTelephone(memberRequest.getTelephone());
        member.setEmail(memberRequest.getEmail());
        //
        Optional<Department> depOptional = departmentRepository.findByDid(memberRequest.getDepDid());
        if (depOptional.isPresent()) {
            member.setDepartment(depOptional.get());
            member.setOrganization(depOptional.get().getOrganization());
        } else {
            return null;
        }

        MemberResponse result = save(member);

        return JsonResult.success(result);
    }

    // public void delete(MemberRequest memberRequest) {
        // Optional<Member> memberOptional =
        // memberRepository.findById(memberRequest.getId());
        // if (memberOptional.isPresent()) {
        // memberRepository.delete(memberOptional.get());
    // }
    // }

    @SuppressWarnings("null")
    private MemberResponse save(Member member) {
        return convertToMemberResponse(memberRepository.save(member));
    }

    private MemberResponse convertToMemberResponse(Member member) {
        MemberResponse memberResponse = modelMapper.map(member, MemberResponse.class);
        memberResponse.setUser(BdConvertUtils.convertTUserResponseSimple(member.getUser()));
        return memberResponse;
    }

    public void initData() {

        if (memberRepository.count() > 0) {
            return;
        }

        // 初始化-人力部门
        Optional<Department> departHROptional = departmentRepository.findByName(TypeConsts.DEPT_HR);
        if (departHROptional.isPresent()) {
            MemberRequest memberRequest = MemberRequest.builder()
                .jobNo("001")
                .password("123456")
                .nickname("User001")
                .seatNo("001")
                .telephone("881")
                .mobile("18888888881")
                    .email("001@email.com")
                .verified(true)
                .depDid(departHROptional.get().getDid())
                .build();
            create(memberRequest);
            MemberRequest memberRequest2 = MemberRequest.builder()
                .jobNo("002")
                .password("123456")
                .nickname("User002")
                .seatNo("002")
                .telephone("882")
                .mobile("18888888882")
                    .email("002@email.com")
                .verified(true)
                .depDid(departHROptional.get().getDid())
                .build();
            create(memberRequest2);
        }
        
        // 行政
        Optional<Department> departORGOptional = departmentRepository.findByName(TypeConsts.DEPT_ORG);
        if (departORGOptional.isPresent()) {
            MemberRequest memberRequest = MemberRequest.builder()
                .jobNo("003")
                .password("123456")
                .nickname("User003")
                .seatNo("003")
                .telephone("883")
                .mobile("18888888883")
                .email("003@email.com").verified(true)
                .depDid(departORGOptional.get().getDid())
                .build();
            create(memberRequest);
            MemberRequest memberRequest2 = MemberRequest.builder()
                .jobNo("004")
                .password("123456")
                .nickname("User004")
                .seatNo("004")
                .telephone("884")
                .mobile("18888888884")
                .email("004@email.com").verified(true)
                .depDid(departORGOptional.get().getDid())
                .build();
            create(memberRequest2);
        }
        
        // IT
        Optional<Department> departITOptional = departmentRepository.findByName(TypeConsts.DEPT_IT);
        if (departITOptional.isPresent()) {
            MemberRequest memberRequest = MemberRequest.builder()
                    .jobNo("005")
                    .password("123456")
                    .nickname("User005")
                    .seatNo("005")
                    .telephone("885")
                    .mobile("18888888885")
                    .email("005@email.com").verified(true)
                    .depDid(departITOptional.get().getDid())
                    .build();
            create(memberRequest);
            MemberRequest memberRequest2 = MemberRequest.builder()
                    .jobNo("006")
                    .password("123456")
                    .nickname("User006")
                    .seatNo("006")
                    .telephone("886")
                    .mobile("18888888886")
                    .email("006@email.com").verified(true)
                    .depDid(departITOptional.get().getDid())
                    .build();
            create(memberRequest2);
        }
        
        // 财务
        Optional<Department> departMoneyOptional = departmentRepository.findByName(TypeConsts.DEPT_MONEY);
        if (departMoneyOptional.isPresent()) {
            MemberRequest memberRequest = MemberRequest.builder()
                    .jobNo("007")
                    .password("123456")
                    .nickname("User007")
                    .seatNo("007")
                    .telephone("887")
                    .mobile("18888888887")
                    .email("007@email.com").verified(true)
                    .depDid(departMoneyOptional.get().getDid())
                    .build();
            create(memberRequest);
            MemberRequest memberRequest2 = MemberRequest.builder()
                    .jobNo("008")
                    .password("123456")
                    .nickname("User008")
                    .seatNo("008")
                    .telephone("888")
                    .mobile("18888888888")
                    .email("008@email.com").verified(true)
                    .depDid(departMoneyOptional.get().getDid())
                    .build();
            create(memberRequest2);
        }
        
        // 营销
        Optional<Department> departMarketingOptional = departmentRepository.findByName(TypeConsts.DEPT_MARKETING);
        if (departMarketingOptional.isPresent()) {
            MemberRequest memberRequest = MemberRequest.builder()
                    .jobNo("009")
                    .password("123456")
                    .nickname("User009")
                    .seatNo("009")
                    .telephone("889")
                    .mobile("18888888889")
                    .email("009@email.com").verified(true)
                    .depDid(departMarketingOptional.get().getDid())
                    .build();
            create(memberRequest);
            MemberRequest memberRequest2 = MemberRequest.builder()
                    .jobNo("010")
                    .password("123456")
                    .nickname("User010")
                    .seatNo("010")
                    .telephone("810")
                    .mobile("18888888810")
                    .email("010@email.com").verified(true)
                    .depDid(departMarketingOptional.get().getDid())
                    .build();
            create(memberRequest2);
        }
        
        // 销售
        Optional<Department> departSalesOptional = departmentRepository.findByName(TypeConsts.DEPT_SALES);
        if (departSalesOptional.isPresent()) {
            MemberRequest memberRequest = MemberRequest.builder()
                .jobNo("011")
                .password("123456")
                .nickname("User011")
                .seatNo("011")
                .telephone("811")
                .mobile("18888888811")
                .email("011@email.com").verified(true)
                .depDid(departSalesOptional.get().getDid())
                .build();
            create(memberRequest);
            MemberRequest memberRequest2 = MemberRequest.builder()
                .jobNo("012")
                .password("123456")
                .nickname("User012")
                .seatNo("012")
                .telephone("812")
                .mobile("18888888812")
                .email("012@email.com").verified(true)
                .depDid(departSalesOptional.get().getDid())
                .build();
            create(memberRequest2);
        }
    
        // 客服
        Optional<Department> departCSOptional = departmentRepository.findByName(TypeConsts.DEPT_CUSTOMER_SERVICE);
        if (departCSOptional.isPresent()) {
            MemberRequest memberRequest = MemberRequest.builder()
                    .jobNo("013")
                    .password("123456")
                    .nickname("User013")
                    .seatNo("013")
                    .telephone("813")
                    .mobile("18888888813")
                    .email("013@email.com").verified(true)
                    .depDid(departCSOptional.get().getDid())
                    .build();
            create(memberRequest);
            MemberRequest memberRequest2 = MemberRequest.builder()
                    .jobNo("014")
                    .password("123456")
                    .nickname("User014")
                    .seatNo("014")
                    .telephone("814")
                    .mobile("18888888814")
                    .email("014@email.com").verified(true)
                    .depDid(departCSOptional.get().getDid())
                    .build();
            create(memberRequest2);
        }
    
    }
}
