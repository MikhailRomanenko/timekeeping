package com.timekeeping.shop.support;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.timekeeping.shop.Shop;

/**
 * Service providing high-level data access and other {@link Shop}-related
 * operations.
 * 
 * @author Mikhail Romanenko
 *
 */
@Service
@Transactional(readOnly = true)
public class ShopService {

	private final ShopRepository shopRepository;

	@Autowired
	public ShopService(ShopRepository shopRepository) {
		this.shopRepository = shopRepository;
	}

	/**
	 * Find a list of the {@link Shop}s objects belonging to the {@link User}
	 * with specified {@code login}.
	 * 
	 * @param login
	 *            {@link User} login
	 * @return list of the shops
	 */
	public List<Shop> findByUserLogin(String login) {
		List<Shop> result = shopRepository.findByUserLogin(login);
		return result;
	}

	/**
	 * Finds a shop by its id value.
	 * @param id value of the shop
	 * @return shop
     */
	public Shop find(Long id) {
		return shopRepository.findOne(id);
	}

}
