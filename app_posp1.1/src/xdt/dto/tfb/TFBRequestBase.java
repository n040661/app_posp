package xdt.dto.tfb;

import java.util.Map;
import java.util.TreeMap;

import xdt.dto.nbs.AbstractBase;

public abstract class TFBRequestBase extends AbstractBase{

	 public abstract TreeMap<String, Object> toMap();
}
