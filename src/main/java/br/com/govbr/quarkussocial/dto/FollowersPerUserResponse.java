package br.com.govbr.quarkussocial.dto;

import java.util.List;

import br.com.govbr.quarkussocial.domain.Follower;
import lombok.Data;

@Data
public class FollowersPerUserResponse {

	private Integer followersCount;
	private List<FollowerResponse> response;

	public static FollowerResponse fromEntity(Follower fol) {
		FollowerResponse response = new FollowerResponse();
		response.setNome(fol.getFollower().getNome());
		response.setId(fol.getId());
		
		return response;
	}

}
