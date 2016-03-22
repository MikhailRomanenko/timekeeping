package com.timekeeping.shop.support;

import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.assertThat;

import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.timekeeping.AbstractRepositoryIntegerationTest;
import com.timekeeping.shop.Shop;

public class ShopServiceITTest extends AbstractRepositoryIntegerationTest {

	private static final String USER1 = "user1";
	private static final String USER2 = "user2";
	
	@Autowired
	private ShopRepository shopRepository;
	private ShopService shopService;
	
	@Before
	public void setUp() {
		shopService = new ShopService(shopRepository);
	}
	
	@Test
	public void shouldFindSingleShop() {
		List<Shop> result = shopService.findByUserLogin(USER2);
		
		assertThat(result, hasSize(1));
	}
	
	@Test
	public void shouldFindTwoShops() {
		List<Shop> result = shopService.findByUserLogin(USER1);
		
		assertThat(result, hasSize(2));
	}
	
	@Test
	public void shouldReturnEmptyList() {
		List<Shop> result = shopService.findByUserLogin("unknown");
		
		assertThat(result, hasSize(0));
	}

}
