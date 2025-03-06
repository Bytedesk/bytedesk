package com.bytedesk.ai.utils.output;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ActorsFilms {

	private String actor;

	private List<String> movies;

	@Override
	public String toString() {
		return "ActorsFilms{" + "actor='" + actor + '\'' + ", movies=" + movies + '}';
	}

}
