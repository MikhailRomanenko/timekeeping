package com.timekeeping.schedule.support;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import com.timekeeping.TimekeepingWebApplication;
import com.timekeeping.shop.Shop;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(TimekeepingWebApplication.class)
@WebAppConfiguration
@ActiveProfiles("dev")
public class ScheduleControllerSecurityTest {

	@Autowired
	private WebApplicationContext context;
	private MockMvc mvc;
	
	@Before
	public void setUp() {
		mvc = MockMvcBuilders
				.webAppContextSetup(context)
				.alwaysDo(print())
                .apply(SecurityMockMvcConfigurers.springSecurity())
                .build();
	}
	
	@Test
	@WithUserDetails("user1")
	public void scheduleAccessibleOnlyforAuthenticated() throws Exception {
		mvc.perform(get("/schedule"))
			.andExpect(model().attribute("shops", new BaseMatcher<List<Shop>>() {
                @Override
                public boolean matches(Object other) {
                    @SuppressWarnings("unchecked")
                    List<Shop> shops = (List<Shop>) other;
                    return shops.size() == 2 
                    		&& shops.get(0).getName().equals("Shop1")
                    		&& shops.get(1).getName().equals("Shop2");
                }

                @Override
                public void describeTo(Description d) {
                }
            }));
	}
	
	@Test
	@WithMockUser(roles={"ADMIN"})
	public void scheduleNotAccessibleForAdmins() throws Exception {
		mvc.perform(get("/schedule"))
			.andExpect(status().isForbidden());
	}
	
	@Test
	@WithUserDetails("user1")
	public void employeesAccessibleOnlyForOwningUser() throws Exception {
		mvc
			.perform(get("/api/shop/1/employees"))
			.andExpect(jsonPath("$.1.id").value(1))
			.andExpect(jsonPath("$.2.id").value(2))
			.andExpect(jsonPath("$.3.id").value(3))
			.andExpect(jsonPath("$.4.id").value(4));
	}

	@Test
	@WithUserDetails("user2")
	public void employeesNotAccessibleOnlyForNonOwningUser() throws Exception {
		mvc
			.perform(get("/api/shop/1/employees")
						.accept(MediaType.APPLICATION_JSON_UTF8))
			.andExpect(status().isForbidden());
	}
	
	@Test
	@WithUserDetails("user1")
	public void scheduleAccessibleOnlyForOwningUser() throws Exception {
		mvc
			.perform(get("/api/schedule/1/2016-03-01")
						.accept(MediaType.APPLICATION_JSON_UTF8))
			.andExpect(jsonPath("$.shopId").value(1))
			.andExpect(jsonPath("$.date[0]").value(2016))
			.andExpect(jsonPath("$.date[1]").value(3))
			.andExpect(jsonPath("$.date[2]").value(1))
			.andExpect(jsonPath("$.items").isNotEmpty());
	}
	
	@Test
	@WithUserDetails("user2")
	public void scheduleNotAccessibleForNonOwningUser() throws Exception {
		mvc
			.perform(get("/api/schedule/1/2016-03-01")
						.accept(MediaType.APPLICATION_JSON_UTF8))
			.andExpect(status().isForbidden());
	}
	
	@Test
	@WithUserDetails("user1")
	@Transactional
	public void scheduleSavingAccessibleOnlyForOwningUser() throws Exception {
		String content = 
				"{\"shopId\":1, \"date\":[2016, 3, 1], \"version\":0, \"items\":{}}";
		mvc
			.perform(post("/api/schedule")
						.contentType(MediaType.APPLICATION_JSON_UTF8)
						.content(content).with(csrf().asHeader()))
			.andExpect(status().isOk());
	}
	
	@Test
	@WithUserDetails("user2")
	public void scheduleSavingNotAccessibleForNonOwningUser() throws Exception {
		String content = 
				"{\"shopId\":1, \"date\":[2016, 3, 2], \"version\":0, \"items\":{}}";
		mvc
		.perform(post("/api/schedule")
				.contentType(MediaType.APPLICATION_JSON_UTF8)
				.content(content).with(csrf().asHeader()))
		.andExpect(status().isForbidden());
	}
}
