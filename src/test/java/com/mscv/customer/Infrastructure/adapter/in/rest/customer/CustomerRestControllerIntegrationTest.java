package com.mscv.customer.Infrastructure.adapter.in.rest.customer;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mscv.customer.Infrastructure.adapter.in.rest.address.dto.AddressRequestDto;
import com.mscv.customer.Infrastructure.adapter.in.rest.customer.dto.CustomerRequestDto;
import com.mscv.customer.Infrastructure.adapter.in.rest.customer.dto.CustomerResponseDto;
import com.mscv.customer.Infrastructure.adapter.out.persistence.customer.CustomerJpaRepository;

@SpringBootTest
@AutoConfigureMockMvc
class CustomerRestControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private CustomerJpaRepository customerJpaRepository;

    @BeforeEach
    void cleanDatabase() {
        customerJpaRepository.deleteAll();
    }

    @Test
    void testCreateAndGetById() throws Exception {
        MvcResult result = mockMvc.perform(post("/customers")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody("1004", "Ana")))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Ana"))
                .andReturn();

        Long id = idFrom(result);

        mockMvc.perform(get("/customers/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id.intValue()))
                .andExpect(jsonPath("$.documentNumber").value("1004"))
                .andExpect(jsonPath("$.addresses").isEmpty());
    }

    @Test
    void testSearchByDocumentNumber() throws Exception {
        Long id = createCustomer("1005");

        mockMvc.perform(get("/customers/search").param("document", "1005"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id.intValue()))
                .andExpect(jsonPath("$.name").value("Ana"));
    }

    @Test
    void testGetByIdReturnsNotFound() throws Exception {
        mockMvc.perform(get("/customers/{id}", 332L))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.path").value("/customers/332"));
    }

    @Test
    void testCreateWithDuplicateDocumentReturnsConflict() throws Exception {
        String body = requestBody("1006", "Ana");

        mockMvc.perform(post("/customers").contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/customers").contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value(409));
    }

    @Test
    void testChangeNamePersists() throws Exception {
        Long id = createCustomer("233");

        mockMvc.perform(patch("/customers/{id}/name", id).param("name", "Ana Maria"))
                .andExpect(status().isOk());

        mockMvc.perform(get("/customers/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Ana Maria"));
    }

    @Test
    void testAddAddressPersists() throws Exception {
        Long id = createCustomer("1008");
        AddressRequestDto address = new AddressRequestDto("Antioquia", "Medellin", "Calle 1 # 2-3");

        MvcResult result = mockMvc.perform(post("/customers/{id}/addresses", id)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(address)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.addresses[0].city").value("Medellin"))
                .andReturn();

        CustomerResponseDto response = objectMapper.readValue(
                result.getResponse().getContentAsString(), CustomerResponseDto.class);
        Long addressId = response.addresses().get(0).id();

        mockMvc.perform(get("/customers/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.addresses[0].id").value(addressId.intValue()))
                .andExpect(jsonPath("$.addresses[0].fullAddress").value("Calle 1 # 2-3"));
    }

    @Test
    void testRemoveAddressPersists() throws Exception {
        Long id = createCustomer("1009");
        AddressRequestDto address = new AddressRequestDto("Antioquia", "Medellin", "Calle 1 # 2-3");

        MvcResult result = mockMvc.perform(post("/customers/{id}/addresses", id)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(address)))
                .andExpect(status().isCreated())
                .andReturn();

        CustomerResponseDto response = objectMapper.readValue(
                result.getResponse().getContentAsString(), CustomerResponseDto.class);
        Long addressId = response.addresses().get(0).id();

        mockMvc.perform(delete("/customers/{id}/addresses/{addressId}", id, addressId))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/customers/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.addresses").isEmpty());
    }

    @Test
    void testCreateWithBlankNameReturnsBadRequest() throws Exception {
        mockMvc.perform(post("/customers")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody("1010", " ")))
                .andExpect(status().isBadRequest());
    }

    private Long createCustomer(String documentNumber) throws Exception {
        MvcResult result = mockMvc.perform(post("/customers")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody(documentNumber, "Ana")))
                .andExpect(status().isCreated())
                .andReturn();
        return idFrom(result);
    }

    private String requestBody(String documentNumber, String name) throws Exception {
        return objectMapper.writeValueAsString(new CustomerRequestDto(
                name, "Lopez", documentNumber, "CC", 30, true, List.of()));
    }

    private Long idFrom(MvcResult result) throws Exception {
        return objectMapper.readTree(result.getResponse().getContentAsString()).get("id").asLong();
    }
}
