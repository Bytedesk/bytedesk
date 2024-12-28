@Entity
@Data
@Table(name = "bytedesk_team_group_invites")
public class GroupInviteEntity extends BaseEntity {
    
    @ManyToOne
    private GroupEntity group;
    
    @ManyToOne
    private UserEntity inviter;
    
    @ManyToOne
    private UserEntity invitee;
    
    private GroupInviteStatus status = GroupInviteStatus.PENDING;
    
    private LocalDateTime expireTime;
    
    private String message;  // 邀请消息
} 