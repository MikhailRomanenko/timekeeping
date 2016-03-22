package com.timekeeping.shop.support;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.timekeeping.shop.Shop;

public interface ShopRepository extends JpaRepository<Shop, Long> {
	
	@Query("SELECT s FROM Shop s WHERE ?1 IN (SELECT u.login FROM s.users u)")
	List<Shop> findByUserLogin(String login);
	
}
