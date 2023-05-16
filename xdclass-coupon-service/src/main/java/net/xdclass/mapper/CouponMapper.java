package net.xdclass.mapper;

import net.xdclass.model.CouponDO;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author qyf
 * @since 2023-05-11
 */
public interface CouponMapper extends BaseMapper<CouponDO> {

    int reduceStock(long couponId);
}
