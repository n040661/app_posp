package xdt.dto.nbs.base;

import java.util.Map;

import xdt.dto.nbs.AbstractBase;

public abstract class WechatWebPayRequestBase extends AbstractBase{
	public abstract Map<String, Object> toMap();
}
