package com.tumbleweed.io.adapter;

import com.tumbleweed.io.domain.CommDomain;


public interface CommAdapter {
	public CommDomain invokeHandler(byte[] packets);
}
