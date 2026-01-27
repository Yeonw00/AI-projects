package com.example.news_summary.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.ArgumentMatchers.startsWith;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.example.newssummary.dao.CoinLedger;
import com.example.newssummary.dao.LedgerType;
import com.example.newssummary.dao.User;
import com.example.newssummary.dao.UserBalance;
import com.example.newssummary.dto.SignupRequest;
import com.example.newssummary.repository.UserBalanceRepository;
import com.example.newssummary.repository.UserRepository;
import com.example.newssummary.service.UserService;
import com.example.newssummary.service.payment.CoinLedgerService;


@ExtendWith(MockitoExtension.class)
public class UserServiceSignupTest {
	
	@Mock private UserRepository userRepo;
	@Mock private UserBalanceRepository balanceRepo;
	@Mock private CoinLedgerService coinLedgerService;
	@Mock private PasswordEncoder passwordEncoder;
	
	@InjectMocks UserService userService;
	
	@Test
	void singup_success_encodesPassword_savesUser_savesBalance_and_writesLedger() {
		
		SignupRequest req = new SignupRequest();
		req.setUsername("alice");
		req.setPassword("pw1234");
		req.setEmail("a@b.com");
		
		when(userRepo.findByUsername("alice")).thenReturn(Optional.empty());
		when(passwordEncoder.encode("pw1234")).thenReturn("ENC-pw1234");
		
		// save 될 때 ID가 부여되도록
		when(userRepo.save(any(User.class))).thenAnswer(inv -> {
			User u = inv.getArgument(0);
			u.setId(99L);
			return u;
		});
		
		when(balanceRepo.save(any(UserBalance.class)))
			.thenAnswer(inv -> inv.getArgument(0));
		
		// ledger는 반환값이 중요하진 않으니 더미 반환
		when(coinLedgerService.createEntry(
				eq(99L), eq(LedgerType.CHARGE), eq(300L),
				eq("가입 보너스"), anyString(), isNull()))
			.thenReturn(new CoinLedger());
		
		// when
		userService.singup(req);
		
		// then
		// 1 비밀번호 해시
		verify(passwordEncoder, times(1)).encode("pw1234");
		
		// 2 사용자 저장 시 해시가 반영되었는지 확인
		ArgumentCaptor<User>  userCap = ArgumentCaptor.forClass(User.class);
		verify(userRepo, times(1)).save(userCap.capture());
		User savedUser = userCap.getValue();
		assertThat(savedUser.getUsername()).isEqualTo("alice");
		assertThat(savedUser.getPasswordHash()).isEqualTo("ENC-pw1234");
		assertThat(savedUser.getEmail()).isEqualTo("a@b.com");
		assertThat(savedUser.getId()).isEqualTo(99L);
		
		// 3 잔액 300 저장 + 연관관계 사용자 일치
		ArgumentCaptor<UserBalance> balCap = ArgumentCaptor.forClass(UserBalance.class);
		verify(balanceRepo, times(1)).save(balCap.capture());
		UserBalance savedBal = balCap.getValue();
		assertThat(savedBal.getBalance()).isEqualTo(300L);
		assertThat(savedBal.getUser()).isSameAs(savedUser);
		
		// 4 원장 기록 호출(멱등키, orderId=null)
		verify(coinLedgerService, times(1)).createEntry(
				eq(99L), eq(LedgerType.CHARGE), eq(300L), 
				eq("가입 보너스"), startsWith("SIGNUP-"), isNull());
	}
	
	@Test
	void signup_whenUsernameExists_throws_and_noWrites() {
		SignupRequest req = new SignupRequest();
		req.setUsername("alice");
		req.setPassword("pw1234");
		req.setEmail("a@b.com");
		
		when(userRepo.findByUsername("alice"))
			.thenReturn(Optional.of(new User()));
		
		// when & then
		assertThrows(IllegalArgumentException.class, () -> userService.singup(req));
		
		// 부수효과 없음
        verify(passwordEncoder, never()).encode(anyString());
        verify(userRepo, never()).save(any());
        verify(balanceRepo, never()).save(any());
        verify(coinLedgerService, never()).createEntry(any(), any(), anyLong(), any(), any(), any());
		
	}
}
