package com.vetflow.api.web.v1;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vetflow.api.application.owner.CreateOwnerCommand;
import com.vetflow.api.application.owner.OwnerApplicationService;
import com.vetflow.api.application.owner.OwnerResult;
import com.vetflow.api.application.owner.UpdateOwnerCommand;
import com.vetflow.api.application.shared.ResourceNotFoundException;
import com.vetflow.api.application.shared.ValidationException;
import com.vetflow.api.web.v1.error.GlobalExceptionHandler;
import com.vetflow.api.web.v1.owner.CreateOwnerRequest;
import com.vetflow.api.web.v1.owner.UpdateOwnerRequest;

@WebMvcTest(controllers = OwnerController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import(GlobalExceptionHandler.class)
class OwnerControllerTest {

  @Autowired
  MockMvc mockMvc;

  @Autowired
  ObjectMapper objectMapper;

  @SuppressWarnings("removal")
  @MockBean
  OwnerApplicationService ownerApplicationService;

  @Test
  void createOwnerReturnsCreatedOwner() throws Exception {
    CreateOwnerRequest request = new CreateOwnerRequest("John Doe", "1234567890", "john@doe.com", "Address");
    OwnerResult result = new OwnerResult(1L, "John Doe", "1234567890", "john@doe.com", "Address",
        LocalDateTime.now(), LocalDateTime.now());
    given(ownerApplicationService.createOwner(any(CreateOwnerCommand.class))).willReturn(result);

    mockMvc.perform(post("/api/v1/owners")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isCreated())
        .andExpect(header().string("Location", containsString("/api/v1/owners/1")))
        .andExpect(jsonPath("$.name", is("John Doe")));

    verify(ownerApplicationService)
        .createOwner(new CreateOwnerCommand("John Doe", "1234567890", "john@doe.com", "Address"));
  }

  @Test
  void createOwnerValidationFailureReturnsBadRequest() throws Exception {
    CreateOwnerRequest request = new CreateOwnerRequest("", "123", "invalid", "A");

    mockMvc.perform(post("/api/v1/owners")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.message", is("Validation failed")));
  }

  @Test
  void getOwnerReturnsOwner() throws Exception {
    OwnerResult result = new OwnerResult(2L, "Jane Doe", "1234567890", "jane@doe.com", "Address",
        LocalDateTime.now(), LocalDateTime.now());
    given(ownerApplicationService.getById(2L)).willReturn(result);

    mockMvc.perform(get("/api/v1/owners/2"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.email", is("jane@doe.com")));
  }

  @Test
  void getOwnerNotFoundReturns404() throws Exception {
    given(ownerApplicationService.getById(99L)).willThrow(new ResourceNotFoundException("Owner 99 not found"));

    mockMvc.perform(get("/api/v1/owners/99"))
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.message", is("Owner 99 not found")));
  }

  @Test
  void updateOwnerHandlesApplicationValidationException() throws Exception {
    UpdateOwnerRequest request = new UpdateOwnerRequest("1234567890", "john@doe.com", "New Address");
    given(ownerApplicationService.updateOwner(new UpdateOwnerCommand(1L, "1234567890", "john@doe.com", "New Address")))
        .willThrow(new ValidationException("ownerId is required"));

    mockMvc.perform(put("/api/v1/owners/1")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.message", is("ownerId is required")));
  }

  @Test
  void createOwnerConflictReturns409() throws Exception {
    CreateOwnerRequest request = new CreateOwnerRequest("John Doe", "1234567890", "john@doe.com", "Address");
    given(ownerApplicationService.createOwner(any(CreateOwnerCommand.class)))
        .willThrow(new DataIntegrityViolationException("duplicate"));

    mockMvc.perform(post("/api/v1/owners")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isConflict())
        .andExpect(jsonPath("$.message", is("Request conflicts with existing data")));
  }
}
