package com.timekeeping.user.support;

import org.springframework.data.jpa.repository.JpaRepository;

import com.timekeeping.user.User;

public interface UserRepository extends JpaRepository<User, Long> {

}
