package com.qpang.userservice.application.service;

import com.qpang.common.entity.UserRole;
import com.qpang.common.exception.CustomException;
import com.qpang.userservice.application.dto.user.UserInfo;
import com.qpang.userservice.application.dto.user.UserUpdateCommand;
import com.qpang.userservice.domain.entity.User;
import com.qpang.userservice.domain.repository.UserRepository;
import com.qpang.userservice.exception.UserErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class UserService {

    private final UserRepository userRepository;

    /**
     * [단건 조회]
     * 식별자로 사용자를 조회합니다. 삭제된 사용자는 조회되지 않습니다.
     */
    @Transactional(readOnly = true)
    public UserInfo getUser(UUID id) {
        // soft delete 된 데이터는 조회 결과에서 제외한다.
        User user = userRepository.findById(id)
                .filter(u -> u.getDeletedAt() == null)
                .orElseThrow(() -> new CustomException(UserErrorCode.USER_NOT_FOUND));
        return UserInfo.from(user);
    }

    /**
     * [사용자 목록 조회]
     * 삭제되지 않은 전체 사용자를 페이징 조회합니다.
     */
    @Transactional(readOnly = true)
    public Page<UserInfo> getAllUsers(Pageable pageable) {
        return userRepository.findAll(pageable)
                .map(UserInfo::from);
    }

    /**
     * [내 정보 수정]
     * 엔티티의 updateInfo 메서드에 위임하여 상태를 변경합니다.
     */
    public UserInfo updateUser(UUID id, UserUpdateCommand command) {
        // 존재하지 않는 사용자는 수정 대상이 아니다.
        User user = userRepository.findById(id)
                .orElseThrow(() -> new CustomException(UserErrorCode.USER_NOT_FOUND));

        // 프로필 변경 규칙은 엔티티 내부로 위임한다.
        user.updateInfo(command.nickname(), command.email(), command.isPublic());
        return UserInfo.from(user);
    }

    /**
     * [사용자 소프트 삭제]
     * BaseUserEntity의 delete 메서드에 위임하여 삭제 시간과 삭제자를 기록합니다.
     */
    public void deleteUser(UUID id, Long deletedBy) {
        // 삭제는 실제 row 삭제가 아니라 soft delete 처리다.
        User user = userRepository.findById(id)
                .orElseThrow(() -> new CustomException(UserErrorCode.USER_NOT_FOUND));
        user.delete(deletedBy);
    }

    /**
     * [가입 승인 처리]
     * MASTER 권한 관리자가 PENDING 상태인 가입을 APPROVED 로 변경합니다.
     * 엔티티의 approve 메서드에 위임합니다.
     */
    public void approveUser(UUID id) {
        // 승인 가능 여부는 엔티티 상태 전이 규칙에 맡긴다.
        User user = userRepository.findById(id)
                .orElseThrow(() -> new CustomException(UserErrorCode.USER_NOT_FOUND));
        user.approve();
    }

    /**
     * [가입 거절 처리]
     * MASTER 권한 관리자가 PENDING 상태인 가입을 REJECTED 로 변경합니다.
     * 엔티티의 reject 메서드에 위임합니다.
     */
    public void rejectUser(UUID id) {
        // 거절 가능 여부도 엔티티 상태 전이 규칙을 따른다.
        User user = userRepository.findById(id)
                .orElseThrow(() -> new CustomException(UserErrorCode.USER_NOT_FOUND));
        user.reject();
    }

    /**
     * [권한 수정]
     * MASTER 권한 관리자가 사용자의 역할을 변경시킵니다.
     */
    public void updateRole(UUID id, UserRole newRole) {
        // 권한 변경은 관리자 유스케이스로만 허용한다.
        User user = userRepository.findById(id)
                .orElseThrow(() -> new CustomException(UserErrorCode.USER_NOT_FOUND));
        user.updateRole(newRole);
    }

    /**
     * [허브 소속 사용자 목록]
     * 허브 매니저나 배송 관리자 중 특정 허브에 속한 사용자 목록을 반환합니다.
     */
    @Transactional(readOnly = true)
    public Page<UserInfo> getUsersByHubId(UUID hubId, Pageable pageable) {
        // 허브 매핑이 있는 하위 타입만 조회한다.
        return userRepository.findAllByHubId(hubId, pageable)
                .map(UserInfo::from);
    }
}
