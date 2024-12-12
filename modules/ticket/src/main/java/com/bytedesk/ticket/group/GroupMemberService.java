package com.bytedesk.ticket.group;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class GroupMemberService {

    @Autowired
    private GroupMemberRepository memberRepository;

    // 成员管理
    @Transactional
    public GroupMemberEntity addMember(Long groupId, Long userId, Integer role, Integer maxTickets) {
        // 检查是否已存在
        Optional<GroupMemberEntity> existing = memberRepository.findByGroupIdAndUserId(groupId, userId);
        if (existing.isPresent()) {
            throw new RuntimeException("Member already exists in group");
        }

        GroupMemberEntity member = new GroupMemberEntity();
        member.setGroupId(groupId);
        member.setUserId(userId);
        member.setRole(role);
        member.setMaxTickets(maxTickets);
        return memberRepository.save(member);
    }

    @Transactional
    public void removeMember(Long groupId, Long userId) {
        GroupMemberEntity member = getMember(groupId, userId);
        memberRepository.delete(member);
    }

    public GroupMemberEntity getMember(Long groupId, Long userId) {
        return memberRepository.findByGroupIdAndUserId(groupId, userId)
            .orElseThrow(() -> new RuntimeException("Member not found"));
    }

    public List<GroupMemberEntity> getMembers(Long groupId) {
        return memberRepository.findByGroupIdAndEnabledTrue(groupId);
    }

    // 工单分配相关
    public Optional<Long> selectAvailableMember(Long groupId, String priority, Long categoryId, String skill) {
        // 首先按技能查找
        if (skill != null) {
            List<GroupMemberEntity> skilledMembers = memberRepository.findMembersBySkill(groupId, skill);
            Optional<GroupMemberEntity> bestMatch = findBestMatch(skilledMembers, priority, categoryId);
            if (bestMatch.isPresent()) {
                return Optional.of(bestMatch.get().getUserId());
            }
        }

        // 然后按分类查找
        if (categoryId != null) {
            List<GroupMemberEntity> categoryMembers = memberRepository.findMembersByCategory(groupId, categoryId.toString());
            Optional<GroupMemberEntity> bestMatch = findBestMatch(categoryMembers, priority, categoryId);
            if (bestMatch.isPresent()) {
                return Optional.of(bestMatch.get().getUserId());
            }
        }

        // 最后按工作负载查找
        List<GroupMemberEntity> availableMembers = memberRepository.findMembersByWorkload(groupId);
        Optional<GroupMemberEntity> bestMatch = findBestMatch(availableMembers, priority, categoryId);
        return bestMatch.map(GroupMemberEntity::getUserId);
    }

    private Optional<GroupMemberEntity> findBestMatch(List<GroupMemberEntity> members, String priority, Long categoryId) {
        return members.stream()
            .filter(m -> m.canHandleTicket(priority, categoryId))
            .findFirst();
    }

    // 状态管理
    @Transactional
    public void updateMemberStatus(Long groupId, Long userId, String status) {
        GroupMemberEntity member = getMember(groupId, userId);
        member.setStatus(status);
        member.setLastActiveAt(LocalDateTime.now());
        memberRepository.save(member);
    }

    @Transactional
    public void updateLastAssignedTime(Long groupId, Long userId) {
        GroupMemberEntity member = getMember(groupId, userId);
        member.setLastAssignedAt(LocalDateTime.now());
        member.setTotalAssigned(member.getTotalAssigned() + 1);
        memberRepository.save(member);
    }

    // 配置更新
    @Transactional
    public void updateMaxTickets(Long groupId, Long userId, Integer maxTickets) {
        GroupMemberEntity member = getMember(groupId, userId);
        member.setMaxTickets(maxTickets);
        memberRepository.save(member);
    }

    @Transactional
    public void updateSkills(Long groupId, Long userId, String skills, String skillLevels) {
        GroupMemberEntity member = getMember(groupId, userId);
        member.setSkills(skills);
        member.setSkillLevels(skillLevels);
        memberRepository.save(member);
    }

    @Transactional
    public void updateCategories(Long groupId, Long userId, String categories) {
        GroupMemberEntity member = getMember(groupId, userId);
        member.setCategories(categories);
        memberRepository.save(member);
    }

    @Transactional
    public void updateWorkingHours(Long groupId, Long userId, String workingHours) {
        GroupMemberEntity member = getMember(groupId, userId);
        member.setWorkingHours(workingHours);
        memberRepository.save(member);
    }

    // 统计相关
    @Transactional
    public void updateStatistics(Long groupId, Long userId) {
        GroupMemberEntity member = getMember(groupId, userId);
        
        // 更新平均响应时间
        Double avgResponseTime = memberRepository.getAverageResponseTime(userId);
        if (avgResponseTime != null) {
            member.setAvgResponseTime(avgResponseTime);
        }
        
        // 更新平均解决时间
        Double avgResolutionTime = memberRepository.getAverageResolutionTime(userId);
        if (avgResolutionTime != null) {
            member.setAvgResolutionTime(avgResolutionTime);
        }
        
        memberRepository.save(member);
    }

    // 性能排名
    public Page<GroupMemberEntity> getTopPerformers(Long groupId, Pageable pageable) {
        return memberRepository.findTopPerformers(groupId, pageable);
    }

    public Page<GroupMemberEntity> getFastestResponders(Long groupId, Pageable pageable) {
        return memberRepository.findFastestResponders(groupId, pageable);
    }
} 