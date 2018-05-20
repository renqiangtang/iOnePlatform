package com.tradeland.engine;

import com.tradeland.engine.LandEngine;

public class Engine {
	private static LandEngine tradeEngine;

	public static LandEngine getLandEngine() {
		if (tradeEngine == null)
			tradeEngine = new LandEngine();
		return tradeEngine;
	}

}
