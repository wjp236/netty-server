package com.tumbleweed.netty.client.adapter;


import com.tumbleweed.netty.client.domain.CommDomain;

public interface CommAdapter {
	public CommDomain invokeHandler(byte[] packets);
}
