package net.xdclass.service;

import net.xdclass.model.BannerDO;
import com.baomidou.mybatisplus.extension.service.IService;
import net.xdclass.vo.BannerVO;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author qyf
 * @since 2023-06-06
 */
public interface BannerService {
    List<BannerVO> list();
}
