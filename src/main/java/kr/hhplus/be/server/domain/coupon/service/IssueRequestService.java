package kr.hhplus.be.server.domain.coupon.service;

import kr.hhplus.be.server.domain.coupon.command.AddIssueRequestCommand;
import kr.hhplus.be.server.domain.coupon.repository.IssueRequestRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class IssueRequestService {
    private final IssueRequestRepository issueRequestRepository;

    public void addIssueRequest(AddIssueRequestCommand command) {
        issueRequestRepository.addIssueRequest(command.userId(), command.couponId());
        issueRequestRepository.addIssueRequestCouponId(command.couponId());
    }

    public List<Long> getIssueRequestCouponIdList() {
        return issueRequestRepository.getIssueRequestCouponIdList();
    }

    public List<Long> getIssueRequest(Long couponId) {
        return issueRequestRepository.getIssueRequestUserIdList(couponId);
    }
}
