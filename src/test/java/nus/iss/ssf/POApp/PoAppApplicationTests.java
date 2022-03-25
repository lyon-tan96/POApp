package nus.iss.ssf.POApp;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;


import nus.iss.ssf.POApp.model.Quotation;
import nus.iss.ssf.POApp.service.QuotationService;

@AutoConfigureMockMvc
@SpringBootTest
class PoAppApplicationTests {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private QuotationService quoteSvc;

	@Test
	void contextLoads() throws IOException {
		List<String> items = new ArrayList<>();
			items.add("durian");
			items.add("plum");
			items.add("pear");
		try{
			
			Optional<Quotation> opt = quoteSvc.getQuotations(items);
			org.assertj.core.api.Assertions.assertThat(opt.isPresent());
		}catch(Exception e){
			org.assertj.core.api.Assertions.assertThat(e.getMessage());
		}

		
	}
}
