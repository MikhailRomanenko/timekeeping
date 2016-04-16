package com.timekeeping.support;

import java.io.Serializable;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.PermissionEvaluator;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import com.timekeeping.shop.Shop;
import com.timekeeping.shop.support.ShopRepository;

/**
 * Custom strategy implementation to determine whether or not authenticated user has 
 * a permission for a {@link Shop} object. See {@link org.springframework.security.access.PermissionEvaluator}.
 * @author Mikhail Romanenko
 *
 */
@Component
public class ShopPermissionEvaluator implements PermissionEvaluator {
	
	private final ShopRepository shopRepository;
	
	@Autowired
	public ShopPermissionEvaluator(ShopRepository shopRepository) {
		this.shopRepository = shopRepository;
	}

	/*
	 * (non-Javadoc)
	 * @see org.springframework.security.access.PermissionEvaluator#hasPermission(Authentication authentication, 
	 * Object targetDomainObject, Object permission)
	 */
	@Override
	public boolean hasPermission(Authentication authentication, Object targetDomainObject, Object permission) {
		if(authentication == null)
			return false;
		Long shopId = (Long)targetDomainObject;
		if(shopId == null)
			return false;
		Set<Long> shopIds = shopRepository.findByUserLogin(authentication.getName())
				.stream().collect(Collectors.mapping(Shop::getId, Collectors.toSet()));
		return shopIds.contains(shopId);
	}

	@Override
	public boolean hasPermission(Authentication authentication, Serializable targetId, String targetType,
			Object permission) {
		throw new UnsupportedOperationException();
	}

}
