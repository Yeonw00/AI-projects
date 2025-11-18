package com.example.newssummary.service.payment;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.newssummary.dao.CoinLedger;
import com.example.newssummary.dao.LedgerType;
import com.example.newssummary.dao.User;
import com.example.newssummary.dao.UserBalance;
import com.example.newssummary.dto.LedgerEntryResponse;
import com.example.newssummary.dto.LedgerPageResponse;
import com.example.newssummary.repository.CoinLedgerRepository;
import com.example.newssummary.repository.UserBalanceRepository;

@Service
public class CoinLedgerService {
	@Autowired
	private CoinLedgerRepository ledgerRepository;

	@Autowired
	private UserBalanceRepository balanceRepository;
	
	public CoinLedgerService(CoinLedgerRepository ledgerRepository, UserBalanceRepository balanceRepository) {
        this.ledgerRepository = ledgerRepository;
        this.balanceRepository = balanceRepository;
    }

	@Transactional
	public CoinLedger createEntry(Long userId,
	                              LedgerType type,
	                              long amount,
	                              String description,
	                              @Nullable String requestId,
	                              @Nullable String orderId) {

	    if (amount <= 0) {
	        throw new IllegalArgumentException("amount must be positive");
	    }

	    if (requestId != null) {
	        Optional<CoinLedger> dup = ledgerRepository.findByRequestId(requestId);
	        if (dup.isPresent()) return dup.get();
	    }

	    User user = new User();
	    user.setId(userId);

	    UserBalance ub = balanceRepository.findById(userId)
	            .orElseGet(() -> {
	                UserBalance created = new UserBalance();
	                created.setUser(user);
	                created.setBalance(0L);
	                return balanceRepository.save(created);
	            });

	    long current = ub.getBalance();
	    long next;

	    switch (type) {
	        case CHARGE:
	        	next = current + amount;
	        	break;
	        case REFUND:
	            next = current - amount;
	            break;
	        case USE:
	            if (current < amount) {
	                throw new IllegalStateException("INSUFFICIENT_BALANCE");
	            }
	            next = current - amount;
	            break;
	        default:
	            throw new IllegalArgumentException("Unknown type: " + type);
	    }

	    ub.setBalance(next);
	    balanceRepository.save(ub);

	    CoinLedger ledger = new CoinLedger();
	    ledger.setUser(user);
	    ledger.setType(type);
	    ledger.setAmount(amount);
	    ledger.setBalanceAfter(next);
	    ledger.setDescription(description);
	    ledger.setRequestId(requestId);
	    ledger.setOrderId(orderId);
	    ledger.setCreatedAt(LocalDateTime.now());

	    CoinLedger saved = ledgerRepository.save(ledger);
	    return saved;
	}

	// 조회용
	@Transactional(readOnly = true)
	public LedgerPageResponse getUserLedger(Long userId,
											@Nullable LedgerType type,
											int page, int size) {
		Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt", "id"));
		
		Page<CoinLedger> result = (type == null)
				? ledgerRepository.findByUserId(userId, pageable)
				: ledgerRepository.findByUserIdAndType(userId, type, pageable);
		
		long currentBalance = balanceRepository.findById(userId)
				.map(UserBalance::getBalance)
				.orElse(0L);
		
		List<LedgerEntryResponse> items = result.getContent().stream()
			    .map(e -> new LedgerEntryResponse(
			        e.getId(),
			        e.getType(),
			        e.getAmount(),
			        e.getBalanceAfter(),
			        e.getDescription(),
			        e.getOrderId(),
			        e.getCreatedAt()
			    ))
			    .toList();
				
		LedgerPageResponse ledgerPageResponse = new LedgerPageResponse();
		ledgerPageResponse.setItems(items);
		ledgerPageResponse.setTotalCount(result.getTotalElements());
		ledgerPageResponse.setPage(page);
		ledgerPageResponse.setSize(size);
		ledgerPageResponse.setCurrentBalance(currentBalance);

		return ledgerPageResponse;
	}
	
	public List<LedgerEntryResponse> getUserLedgerAll(Long userId,
            @Nullable LedgerType type) {
	// 페이징 없이 전부 조회하는 쿼리
	List<CoinLedger> entities = ledgerRepository.findByUserIdAndTypeOrderByCreatedAtDesc(userId, type);
	return entities
            .stream()
            .map(l -> new LedgerEntryResponse(
                    l.getId(),
                    l.getType(),
                    l.getAmount(),
                    l.getBalanceAfter(),
                    l.getDescription(),
                    l.getOrderId(),
                    l.getCreatedAt()
            ))
            .toList();
	}
}
