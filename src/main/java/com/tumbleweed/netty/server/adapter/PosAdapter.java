package com.tumbleweed.netty.server.adapter;

import java.util.Map;


public interface PosAdapter {
	public byte[] postPosMessage(Map<String, Object> requestMap);
}
