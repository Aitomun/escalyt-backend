package com.decadev.escalayt.payload.response;


import com.decadev.escalayt.enums.Priority;
import com.decadev.escalayt.enums.Status;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TicketResponse {

	private Long createdBy;

	private String title;

	private Priority priority;

	private Status status;

	private String location;

	private String description;

	private String attachment;

	private Long categoryId;





}