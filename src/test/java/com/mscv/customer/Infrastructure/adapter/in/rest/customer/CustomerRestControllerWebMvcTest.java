package com.mscv.customer.Infrastructure.adapter.in.rest.customer;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.BDDMockito.willThrow;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mscv.customer.Infrastructure.adapter.in.rest.address.dto.AddressRequestDto;
import com.mscv.customer.Infrastructure.adapter.in.rest.customer.dto.CustomerRequestDto;
import com.mscv.customer.Infrastructure.adapter.in.rest.customer.mapper.CustomerRestMapper;
import com.mscv.customer.application.exception.AddressNotFoundException;
import com.mscv.customer.application.exception.CustomerNotFoundException;
import com.mscv.customer.application.exception.DuplicateDocumentException;
import com.mscv.customer.domain.models.Address;
import com.mscv.customer.domain.models.Customer;
import com.mscv.customer.domain.port.in.CustomerUseCase;

@WebMvcTest(CustomerRestController.class)
@Import(CustomerRestMapper.class)
public class CustomerRestControllerWebMvcTest {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

	@MockitoBean
	private CustomerUseCase customerUseCase;

	@Test
	void testCreateReturnsCreatedWhenRequiredIsValid() throws Exception {
		CustomerRequestDto request = new CustomerRequestDto(
				"jeferson", "ruiz", "1004", "CC", 25, true, List.of());

		Customer create = new Customer(1L, "jeferson", "ruiz", "1004",
				"CC", 25, true, List.of());

		given(customerUseCase.create(any(Customer.class))).willReturn(create);

		mockMvc.perform(post("/customers")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(request)))
				.andExpect(status().isCreated())
				.andExpect(jsonPath("$.id").value(1))
				.andExpect(jsonPath("$.documentNumber").value("1004"));
	}

	@Test
	void testCreateReturnBadRequestWhenFieldIsBlank() throws Exception {
		CustomerRequestDto request = new CustomerRequestDto(
				" ", "ruiz", "1004", "CC", 25, true, List.of());

		mockMvc.perform(post("/customers")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsBytes(request)))
				.andExpect(status().isBadRequest());
	}

	@Test
	void testCreateReturnBadRequestWhenAgeIsNegative() throws Exception {
		CustomerRequestDto request = new CustomerRequestDto(
				" ", "ruiz", "1004", "CC", -25, true, List.of());
		mockMvc.perform(post("/customers")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsBytes(request)))
				.andExpect(status().isBadRequest());
	}

	@Test
	void testCreateReturnBadRequestWhenAddresFildIsBlank() throws Exception {
		CustomerRequestDto request = new CustomerRequestDto(
				" ", "ruiz", "1004", "CC", 25, true,
				List.of(new AddressRequestDto("atlantico", "barranquilla", "")));
		mockMvc.perform(post("/customers")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsBytes(request)))
				.andExpect(status().isBadRequest());
	}

	@Test
	void testCreateReturnsBadRequestWhenBodyIsMissing() throws Exception {
		mockMvc.perform(post("/customers")
				.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isBadRequest());
	}

	@Test
	void testCreateReturnBadRequestWhenDocumentNumberIsDuplicate() throws Exception {
		CustomerRequestDto request = new CustomerRequestDto(
				"jeferson", "ruiz", "1004", "CC", 25, true, List.of());

		given(customerUseCase.create(any(Customer.class)))
				.willThrow(new DuplicateDocumentException("1005"));

		mockMvc.perform(post("/customers")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(request)))
				.andExpect(status().isConflict())
				.andExpect(jsonPath("$.status").value(409))
				.andExpect(jsonPath("$.error").value("Conflict"))
				.andExpect(jsonPath("$.message").exists())
				.andExpect(jsonPath("$.path").value("/customers"));
	}

	@Test
	void testGetByIdReturnsCustomerWhenExists() throws Exception {
		Customer found = new Customer(1L, "jeferson", "ruiz", "1004", "CC", 25, true,
				List.of(new Address(10L, "atlantico", "barranquilla", "Calle 1 # 2-3")));
		given(customerUseCase.searchById(1L)).willReturn(found);

		mockMvc.perform(get("/customers/1"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.id").value(1))
				.andExpect(jsonPath("$.name").value("jeferson"))
				.andExpect(jsonPath("$.lastName").value("ruiz"))
				.andExpect(jsonPath("$.documentNumber").value("1004"))
				.andExpect(jsonPath("$.addresses[0].city").value("barranquilla"));
	}

	@Test
	void testGetByIdReturnsNotFoundWhenCustomerDoesNotExist() throws Exception {
		given(customerUseCase.searchById(99L))
				.willThrow(new CustomerNotFoundException(99L));

		mockMvc.perform(get("/customers/99"))
				.andExpect(status().isNotFound())
				.andExpect(jsonPath("$.status").value(404))
				.andExpect(jsonPath("$.error").value("Not Found"))
				.andExpect(jsonPath("$.message").exists())
				.andExpect(jsonPath("$.path").value("/customers/99"));
	}

	@Test
	void testGetByIdReturnsBadRequestWhenIdIsNotNumeric() throws Exception {
		mockMvc.perform(get("/customers/abc"))
				.andExpect(status().isBadRequest());
	}

	@Test
	void testGetByDocumentNumberReturnsCustomerWhenExists() throws Exception {
		Customer found = new Customer(1L, "jeferson", "ruiz", "1004", "CC", 25, true,
				List.of(new Address(10L, "atlantico", "barranquilla", "Calle 1 # 2-3")));
		given(customerUseCase.searchByDocumentNumber("1004")).willReturn(found);

		mockMvc.perform(get("/customers/search")
				.param("document", "1004"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.id").value(1))
				.andExpect(jsonPath("$.name").value("jeferson"))
				.andExpect(jsonPath("$.lastName").value("ruiz"))
				.andExpect(jsonPath("$.documentNumber").value("1004"))
				.andExpect(jsonPath("$.addresses[0].city").value("barranquilla"));
	}

	@Test
	void testGetByDocumentNumberReturnsNotFoundWhenCustomerDoesNotExist() throws Exception {
		given(customerUseCase.searchByDocumentNumber("1004"))
				.willThrow(new CustomerNotFoundException("1004"));

		mockMvc.perform(get("/customers/search").param("document", "1004"))
				.andExpect(status().isNotFound())
				.andExpect(jsonPath("$.status").value(404))
				.andExpect(jsonPath("$.error").value("Not Found"))
				.andExpect(jsonPath("$.message").exists())
				.andExpect(jsonPath("$.path").value("/customers/search"));
	}

	@Test
	void testGetByDocumentNumberReturnsBadRequestWhenDocumentParamIsMissing() throws Exception {
		mockMvc.perform(get("/customers/search"))
				.andExpect(status().isBadRequest());
	}

	@Test
	void testChangeNameUpdatesName() throws Exception {
		mockMvc.perform(patch("/customers/1/name").param("name", "Ana Maria"))
				.andExpect(status().isOk());
		then(customerUseCase).should().changeName(1L, "Ana Maria");
	}

	@Test
	void testChangeLastNameUpdatesLastName() throws Exception {
		mockMvc.perform(patch("/customers/1/last-name").param("lastName", "Perez"))
				.andExpect(status().isOk());
		then(customerUseCase).should().changeLastName(1L, "Perez");
	}

	@Test
	void testChangeDocumentNumberUpdatesDocumentNumber() throws Exception {
		mockMvc.perform(patch("/customers/1/document-number").param("documentNumber", "2001"))
				.andExpect(status().isOk());
		then(customerUseCase).should().changeDocumentNumber(1L, "2001");
	}

	@Test
	void testChangeDocumentTypeUpdatesDocumentType() throws Exception {
		mockMvc.perform(patch("/customers/1/document-type").param("documentType", "PAS"))
				.andExpect(status().isOk());
		then(customerUseCase).should().changeDocumentType(1L, "PAS");
	}

	@Test
	void testChangeAgeUpdatesAge() throws Exception {
		mockMvc.perform(patch("/customers/1/age").param("age", "30"))
				.andExpect(status().isOk());
		then(customerUseCase).should().changeAge(1L, 30);
	}

	@Test
	void testChangeActiveUpdatesActive() throws Exception {
		mockMvc.perform(patch("/customers/1/active").param("active", "false"))
				.andExpect(status().isOk());
		then(customerUseCase).should().changeActive(1L, false);
	}

	@Test
	void testChangeNameReturnsBadRequestWhenParamIsMissing() throws Exception {
		mockMvc.perform(patch("/customers/1/name"))
				.andExpect(status().isBadRequest());
	}

	@Test
	void testChangeAgeReturnsBadRequestWhenAgeIsNotNumeric() throws Exception {
		mockMvc.perform(patch("/customers/1/age").param("age", "abc"))
				.andExpect(status().isBadRequest());
	}

	@Test
	void testChangeActiveReturnsBadRequestWhenActiveIsNotBoolean() throws Exception {
		mockMvc.perform(patch("/customers/1/active").param("active", "quizas"))
				.andExpect(status().isBadRequest());
	}

	@Test
	void testChangeNameReturnsBadRequestWhenNameIsBlank() throws Exception {
		willThrow(new IllegalArgumentException("name no puede ser nulo"))
				.given(customerUseCase).changeName(eq(1L), eq(" "));

		mockMvc.perform(patch("/customers/1/name").param("name", " "))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.status").value(400))
				.andExpect(jsonPath("$.error").value("Bad Request"))
				.andExpect(jsonPath("$.message").exists())
				.andExpect(jsonPath("$.path").value("/customers/1/name"));
	}

	@Test
	void testChangeAgeReturnsBadRequestWhenAgeIsNegative() throws Exception {
		willThrow(new IllegalArgumentException("La edad no puede ser negativa"))
				.given(customerUseCase).changeAge(eq(1L), eq(-1));

		mockMvc.perform(patch("/customers/1/age").param("age", "-1"))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.status").value(400));
	}

	@Test
	void testChangeNameReturnsNotFoundWhenCustomerDoesNotExist() throws Exception {
		willThrow(new CustomerNotFoundException(99L))
				.given(customerUseCase).changeName(eq(99L), eq("Ana"));

		mockMvc.perform(patch("/customers/99/name").param("name", "Ana"))
				.andExpect(status().isNotFound())
				.andExpect(jsonPath("$.status").value(404))
				.andExpect(jsonPath("$.message").exists())
				.andExpect(jsonPath("$.path").value("/customers/99/name"));
	}

	@Test
	void testChangeDocumentNumberReturnsConflictWhenDocumentIsDuplicated() throws Exception {
		willThrow(new DuplicateDocumentException("2001"))
				.given(customerUseCase).changeDocumentNumber(eq(1L), eq("2001"));

		mockMvc.perform(patch("/customers/1/document-number").param("documentNumber", "2001"))
				.andExpect(status().isConflict())
				.andExpect(jsonPath("$.status").value(409))
				.andExpect(jsonPath("$.error").value("Conflict"))
				.andExpect(jsonPath("$.path").value("/customers/1/document-number"));
	}

	@Test
	void testRemoveAddressWhenIsValid() throws Exception {
		mockMvc.perform(delete("/customers/1/addresses/1")).andExpect(status().isNoContent());
		then(customerUseCase).should().removeAddress(1L, 1L);
	}

	@Test
	void testRemoveAddressReturnBadRequestWhenCustomerNoExist() throws Exception {
		willThrow(new CustomerNotFoundException(1L))
				.given(customerUseCase).removeAddress(1L, 1L);

		mockMvc.perform(delete("/customers/1/addresses/1"))
				.andExpect(status().isNotFound())
				.andExpect(jsonPath("$.status").value(404))
				.andExpect(jsonPath("$.error").value("Not Found"))
				.andExpect(jsonPath("$.message").exists())
				.andExpect(jsonPath("$.path").value("/customers/1/addresses/1"));
	}

	@Test
	void testRemoveAddressReturnBadRequestWhenAddressNoExist() throws Exception {
		willThrow(new AddressNotFoundException(1L, 1L))
				.given(customerUseCase).removeAddress(1L, 1L);

		mockMvc.perform(delete("/customers/1/addresses/1"))
				.andExpect(status().isNotFound())
				.andExpect(jsonPath("$.status").value(404))
				.andExpect(jsonPath("$.error").value("Not Found"))
				.andExpect(jsonPath("$.message").exists())
				.andExpect(jsonPath("$.path").value("/customers/1/addresses/1"));
	}

	@Test
	void testRemoveAddressReturnsMethodNotAllowedWhenAddressIdIsMissing() throws Exception {
		mockMvc.perform(delete("/customers/1/addresses"))
				.andExpect(status().isMethodNotAllowed());
		verifyNoInteractions(customerUseCase);
	}

}