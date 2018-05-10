package xdt.dto.nbs.base;
import java.util.Map;

import xdt.dto.nbs.AbstractBase;

/**
 * @author zhang.hui@pufubao.net
 * @version v1.0
 * @date 2016/11/2 17:02
 */
public abstract class WechatRequestBase extends AbstractBase {

    public abstract Map<String, Object> toMap();

}
