package com.decadev.escalayt.payload.request;


import com.decadev.escalayt.enums.Priority;
import com.decadev.escalayt.enums.Status;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TicketRequest {

	private String title;

	private Priority priority;

	private Status status;

	private String location;

	private String description;

	private MultipartFile attachment;;

	private Long categoryId;

}