package com.example.newssummary.repository.admin;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.newssummary.dao.User;
import com.example.newssummary.dto.SavedSummaryDTO;
import com.example.newssummary.dto.admin.AdminUserDetailResponse;
import com.example.newssummary.dto.admin.AdminUserView;

public interface AdminUserRepository extends JpaRepository<User, Long> {
	
	@Query("""
	select u.id as id,
			u.email as email,
			u.username as username,
			u.role as role,
			(case when u.passwordHash is null then true else false end) as socialLogin,
			coalesce(ub.balance, 0) as coinBalance,
			u.createdAt as createdAt
	from User u
	left join UserBalance ub on ub.user.id = u.id
	where
		(:keyword is null
			or u.email like %:keyword%
			or u.username like %:keyword%)
		and (:role is null or u.role = :role)
		and (:social is null
			or (:social = true and u.passwordHash is null)
			or (:social = false and u.passwordHash is not null))
	""")
	Page<AdminUserView> findAdminUsers(
		@Param("keyword") String keyword,
		@Param("role") String role,
		@Param("social") Boolean social,
		Pageable pageable
	);

	@Query("""
	select
	    u.id as id,
	    u.email as email,
	    u.username as username,
	    u.role as role,
	    (case when u.passwordHash is null then true else false end) as socialLogin,
	    coalesce(ub.balance, 0) as coinBalance,
	    u.createdAt as createdAt
	from User u
	left join UserBalance ub on ub.user.id = u.id
	where
	    (:keyword is null
	        or u.email like concat('%', :keyword, '%')
	        or u.username like concat ('%', :keyword, '%'))
	  and (:role is null or u.role = :role)
	  and (:social is null
	        or (:social = true and u.passwordHash is null)
	        or (:social = false and u.passwordHash is not null))
	order by u.createdAt desc
	""")
	List<AdminUserView> findAdminUsersForExport(
		@Param("keyword") String keyword, 
		@Param("role") String role, 
		@Param("social") Boolean social);
	
	@Query("""
	select new com.example.newssummary.dto.admin.AdminUserDetailResponse( 
			u.email,
			u.username,
			u.createdAt,
			coalesce(ub.balance, 0),
			(select count(sr) from SummaryRequest sr where sr.user.id = u.id),
			(select max(sr.createdAt) from SummaryRequest sr where sr.user.id = u.id),
			(select count(sr) from SummaryRequest sr 
					where sr.user.id = u.id and sr.status = com.example.newssummary.dao.SummaryStatus.DONE),
			(select count(sr) * 300 from SummaryRequest sr
					where sr.user.id = u.id and sr.status = com.example.newssummary.dao.SummaryStatus.DONE)
	)
	from User u
	left join UserBalance ub on ub.user.id = u.id
	where u.id = :userId
	""")
	AdminUserDetailResponse findAdminUserDetail(@Param("userId") Long userId);
	
	@Query(value = """
	select new com.example.newssummary.dto.SavedSummaryDTO(
		ss.id,
		sr.createdAt,
		ss.savedAt,
		ss.title
	)
	from SavedSummary ss
	join ss.summaryRequest sr
	where ss.user.id = :userId
	""",
	countQuery = "select count(ss) from SavedSummary ss where ss.user.id = :userId")
	Page<SavedSummaryDTO> findAdminUserSummaries(@Param("userId") Long userId, Pageable pageable);
}
