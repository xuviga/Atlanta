package ru.will0376.cases.server.json;

public class Enchants {
	int id;
	int lvl;

	public Enchants(int id, int lvl) {
		this.id = id;
		this.lvl = lvl;
	}

	public String toString() {
		return id + " : " + lvl;
	}

	public int getId() {
		return id;
	}

	public int getLvl() {
		return lvl;
	}
}
