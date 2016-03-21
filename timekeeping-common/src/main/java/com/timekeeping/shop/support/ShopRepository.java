package com.timekeeping.shop.support;

import org.springframework.data.jpa.repository.JpaRepository;

import com.timekeeping.shop.Shop;

public interface ShopRepository extends JpaRepository<Shop, Long> {
}
