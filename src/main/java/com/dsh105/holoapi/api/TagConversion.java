package com.dsh105.holoapi.api;

import java.util.HashMap;
import java.util.Map.Entry;

import org.bukkit.entity.Player;

public class TagConversion {
	private static HashMap<String, TagFunctor> conversions = new HashMap<>();

	public static void addConverion(String txt, TagFunctor functor) {
		if (txt != null && functor != null) {
			conversions.put(txt, functor);
		}
	}

	public static String Transform(Player pl, String txt) {
		for (Entry<String, TagFunctor> ent : conversions.entrySet()) {
			String res = ent.getValue().getValue(pl);
			if (res != null) {
				txt=txt.replaceAll("%" + ent.getKey() + "%",
						ent.getValue().getValue(pl));
			}

		}
		return txt;
	}
}
