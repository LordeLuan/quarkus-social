package br.com.govbr.quarkussocial.dto;

import java.time.LocalDateTime;

import br.com.govbr.quarkussocial.domain.Post;
import lombok.Data;

@Data
public class PostResponse {

	private String texto;
	private LocalDateTime data;	
	
	public static PostResponse fromEntity(Post post) {
		PostResponse response = new PostResponse();
		response.setData(post.getData());
		response.setTexto(post.getTexto());
		
		return response;
	}
}
