package com.example.newssummary.repository.admin;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.example.newssummary.dao.User;
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
	order by u.createdAt desc
	""")
	List<AdminUserView> findAdminUserList();
}
