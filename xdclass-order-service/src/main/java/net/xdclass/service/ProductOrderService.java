package net.xdclass.service;

import net.xdclass.model.ProductOrderDO;
import com.baomidou.mybatisplus.extension.service.IService;
import net.xdclass.request.ConfirmOrderRequest;
import net.xdclass.util.JsonData;

/**
 * <p>
 * 服务类
 * </p>
 *
 * @author qyf
 * @since 2023-06-13
 */
public interface ProductOrderService extends IService<ProductOrderDO> {

    /**
     * 创建订单
     *
     * @param orderRequest
     * @return
     */
    JsonData confirmOrder(ConfirmOrderRequest orderRequest);

    /**
     * 查询订单状态
     *
     * @param outTradeNo
     * @return
     */
    String queryProductOrderState(String outTradeNo);

}
