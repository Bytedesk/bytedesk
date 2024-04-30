/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-01-29 16:20:17
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-04-26 18:21:20
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
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.cache.annotation.CachePut;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bytedesk.core.constant.AvatarConsts;
import com.bytedesk.core.constant.TypeConsts;
// import com.bytedesk.core.auth.AuthService;
import com.bytedesk.core.rbac.user.User;
import com.bytedesk.core.rbac.user.UserService;
import com.bytedesk.core.uid.UidUtils;
import com.bytedesk.core.utils.JsonResult;
import com.bytedesk.team.department.Department;
import com.bytedesk.team.department.DepartmentService;

import lombok.AllArgsConstructor;

// @Slf4j
@Service
@AllArgsConstructor
public class MemberService {

    private UserService userService;

    private DepartmentService departmentService;

    private MemberRepository memberRepository;

    private ModelMapper modelMapper;

    private UidUtils uidUtils;

    public Page<MemberResponse> queryAll(MemberRequest memberRequest) {

        Pageable pageable = PageRequest.of(memberRequest.getPageNumber(), memberRequest.getPageSize(), Sort.Direction.ASC,
                "id");
        
        Page<Member> memberPage = memberRepository.findByOrgOid(memberRequest.getOrgOid(), pageable);

        return memberPage.map(this::convertToMemberResponse);
    }

    public Page<MemberResponse> query(MemberRequest memberRequest) {

        Pageable pageable = PageRequest.of(memberRequest.getPageNumber(), memberRequest.getPageSize(), Sort.Direction.ASC,
                "id");
        
        Page<Member> memberPage = memberRepository.findByDepartmentsDidIn(new String[]{memberRequest.getDepDid()}, pageable);

        return memberPage.map(this::convertToMemberResponse);
    }


    @Transactional
    public JsonResult<?> create(MemberRequest memberRequest) {
        // 
        // if (userService.existsByMobile(memberRequest.getMobile())) {
        //     return JsonResult.error("mobile already exist");
        // }
        // if (userService.existsByEmail(memberRequest.getEmail())) {
        //     return JsonResult.error("email already exist");
        // }
        //
        Optional<Member> memberOptional = findByEmail(memberRequest.getEmail());
        if (!memberOptional.isPresent()) {
            Member member = modelMapper.map(memberRequest, Member.class);
            member.setUid(uidUtils.getCacheSerialUid());
            //
            Optional<Department> depOptional = departmentService.findByDid(memberRequest.getDepDid());
            if (depOptional.isPresent()) {
                member.addDepartment(depOptional.get());
                // member.setDepartment(depOptional.get());
                // member.setOrganization(depOptional.get().getOrganization());
                // member.setOrgOid(depOptional.get().getOrganization().getOid());
                member.setOrgOid(depOptional.get().getOrgOid());
            } else {
                return JsonResult.error("department not exist");
            }
            // 
            User user;
            Optional<User> userOptional = userService.findByEmail(memberRequest.getEmail());
            if (!userOptional.isPresent()) {
                user = userService.createUser(
                        memberRequest.getNickname(),
                        AvatarConsts.DEFAULT_AVATAR_URL,
                        memberRequest.getPassword(),
                        memberRequest.getMobile(),
                        memberRequest.getEmail(),
                        memberRequest.getVerified(),
                        depOptional.get().getOrgOid()
                        );
            } else {
                // just return user
                user = userOptional.get();
            }
            member.setUser(user);
            // 
            Member result = save(member);

            return JsonResult.success(convertToMemberResponse(result));
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
        Optional<Department> depOptional = departmentService.findByDid(memberRequest.getDepDid());
        if (depOptional.isPresent()) {
            member.addDepartment(depOptional.get());
            // member.setDepartment(depOptional.get());
            // member.setOrganization(depOptional.get().getOrganization());
            // member.setOrgOid(depOptional.get().getOrganization().getOid());
            member.setOrgOid(depOptional.get().getOrgOid());
        } else {
            return null;
        }

        Member result = save(member);

        return JsonResult.success(convertToMemberResponse(result));
    }

    // public void delete(MemberRequest memberRequest) {
    // Optional<Member> memberOptional =
    // memberRepository.findById(memberRequest.getId());
    // if (memberOptional.isPresent()) {
    // memberRepository.delete(memberOptional.get());
    // }
    // }

    @Cacheable(value = "member", key = "#uid", unless="#result == null")
    public Optional<Member> findByUid(String uid) {
        return memberRepository.findByUid(uid);
    }

    @Cacheable(value = "member", key = "#mobile", unless="#result == null")
    public Optional<Member> findByMobile(String mobile) {
        return memberRepository.findByUser_Mobile(mobile);
    }

    @Cacheable(value = "member", key = "#email", unless="#result == null")
    public Optional<Member> findByEmail(String email) {
        return memberRepository.findByUser_Email(email);
    }

    @Caching(put = {
        @CachePut(value = "member", key = "#member.uid"),
        @CachePut(value = "member", key = "#member.user.mobile"),
        @CachePut(value = "member", key = "#member.user.email")
    })
    private Member save(Member member) {
        return memberRepository.save(member);
    }

    private MemberResponse convertToMemberResponse(Member member) {
        MemberResponse memberResponse = modelMapper.map(member, MemberResponse.class);
        // memberResponse.setUser(BdConvertUtils.convertTUserResponseSimple(member.getUser()));
        return memberResponse;
    }

    // 
    private static final String[] departments = {
        TypeConsts.DEPT_HR,
        TypeConsts.DEPT_ORG,
        TypeConsts.DEPT_IT,
        TypeConsts.DEPT_MONEY,
        TypeConsts.DEPT_MARKETING,
        TypeConsts.DEPT_SALES,
        TypeConsts.DEPT_CUSTOMER_SERVICE
    };

    public void initData() {
        if (memberRepository.count() > 0) {
            return;
        }
        // 
        for (int i = 1; i <= departments.length; i++) {
            String department = departments[i-1];
            Optional<Department> depOptional = departmentService.findByName(department);
            if (depOptional.isPresent()) {
                String userNo = String.format("%03d", i);
                MemberRequest memberRequest = MemberRequest.builder()
                        .jobNo(userNo)
                        .password("123456")
                        .nickname("User" + userNo)
                        .seatNo(userNo)
                        .telephone(userNo)
                        .mobile("18888888" + userNo)
                        .email(userNo + "@email.com")
                        .verified(true)
                        .depDid(depOptional.get().getDid())
                        .build();
                create(memberRequest);
            }
        }
        
    }
    


}
