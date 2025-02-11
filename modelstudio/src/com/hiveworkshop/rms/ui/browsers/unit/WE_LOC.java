package com.hiveworkshop.rms.ui.browsers.unit;

enum WE_LOC {
	ASHENVALE("WESTRING_LOCALE_ASHENVALE"),
	BARRENS("WESTRING_LOCALE_BARRENS"),
	BLACKCITADEL("WESTRING_LOCALE_BLACKCITADEL"),
	CITYSCAPE("WESTRING_LOCALE_CITYSCAPE"),
	DALARAN("WESTRING_LOCALE_DALARAN"),
	DALARANRUINS("WESTRING_LOCALE_DALARANRUINS"),
	DUNGEON("WESTRING_LOCALE_DUNGEON"),
	FELWOOD("WESTRING_LOCALE_FELWOOD"),
	ICECROWN("WESTRING_LOCALE_ICECROWN"),
	LORDAERON_FALL("WESTRING_LOCALE_LORDAERON_FALL"),
	LORDAERON_SUMMER("WESTRING_LOCALE_LORDAERON_SUMMER"),
	LORDAERON_WINTER("WESTRING_LOCALE_LORDAERON_WINTER"),
	NORTHREND("WESTRING_LOCALE_NORTHREND"),
	OUTLAND("WESTRING_LOCALE_OUTLAND"),
	RUINS("WESTRING_LOCALE_RUINS"),
	DUNGEON2("WESTRING_LOCALE_DUNGEON2"),
	VILLAGE("WESTRING_LOCALE_VILLAGE"),
	VILLAGEFALL("WESTRING_LOCALE_VILLAGEFALL");

	String string;

	WE_LOC(String s) {
		string = s;
	}

	String getString() {
		return string;
	}

	@Override
	public String toString() {
		return string;
	}
}
