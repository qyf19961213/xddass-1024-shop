package net.xdclass.service;

import net.xdclass.model.ProductDO;
import com.baomidou.mybatisplus.extension.service.IService;
import net.xdclass.model.ProductMessage;
import net.xdclass.request.LockProductRequest;
import net.xdclass.util.JsonData;
import net.xdclass.vo.ProductVO;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 服务类
 * </p>
 *
 * @author qyf
 * @since 2023-06-06
 */
public interface ProductService {
    /**
     * 分页查询商品列表
     *
     * @param page
     * @param size
     * @return
     */
    Map<String, Object> page(int page, int size);

    /**
     * 根据id找商品详情
     *
     * @param productId
     * @return
     */
    ProductVO findDetailById(long productId);

    /**
     * 根据id批量查询商品
     *
     * @param productIdList
     * @return
     */
    List<ProductVO> findProductsByIdBatch(List<Long> productIdList);

    /**
     * 锁定商品库存
     *
     * @param lockProductRequest
     * @return
     */
    JsonData lockProductStock(LockProductRequest lockProductRequest);

    /**
     * 释放商品库存
     * @param productMessage
     * @return
     */
    boolean releaseProductStock(ProductMessage productMessage);
}
