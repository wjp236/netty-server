package com.tumbleweed.netty.io.adapter;


import com.tumbleweed.netty.domain.CommDomain;

public interface CommAdapter {
	public CommDomain invokeHandler(byte[] packets);
}
