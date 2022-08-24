package br.com.govbr.quarkussocial.dto;

import br.com.govbr.quarkussocial.domain.Follower;
import lombok.Data;

@Data
public class FollowerResponse {

	private Long id;
	private String nome;
	
    public FollowerResponse() {
    }

    public FollowerResponse(Follower follower){
        this(follower.getId(), follower.getFollower().getNome());
    }

    public FollowerResponse(Long id, String name) {
        this.id = id;
        this.nome = name;
    }
}
