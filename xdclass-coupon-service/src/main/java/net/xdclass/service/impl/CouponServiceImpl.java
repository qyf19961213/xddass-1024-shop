package net.xdclass.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import net.xdclass.enums.BizCodeEnum;
import net.xdclass.enums.CouponCategoryEnum;
import net.xdclass.enums.CouponPublishEnum;
import net.xdclass.enums.CouponStateEnum;
import net.xdclass.exception.BizException;
import net.xdclass.interceptor.LoginInterceptor;
import net.xdclass.mapper.CouponMapper;
import net.xdclass.mapper.CouponRecordMapper;
import net.xdclass.model.CouponDO;
import net.xdclass.model.CouponRecordDO;
import net.xdclass.model.LoginUser;
import net.xdclass.request.NewUserCouponRequest;
import net.xdclass.service.CouponService;
import net.xdclass.util.CommonUtil;
import net.xdclass.util.JsonData;
import net.xdclass.vo.CouponVO;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * 小滴课堂,愿景：让技术不再难学
 *
 * @Description
 * @Author 二当家小D
 * @Remark 有问题直接联系我，源码-笔记-技术交流群
 * @Version 1.0
 **/

@Service
@Slf4j
public class CouponServiceImpl implements CouponService {


    @Autowired
    private CouponMapper couponMapper;

    @Autowired
    private CouponRecordMapper couponRecordMapper;

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private RedissonClient redissonClient;

    @Override
    public Map<String, Object> pageCouponActivity(int page, int size) {

        Page<CouponDO> pageInfo = new Page<>(page, size);

        IPage<CouponDO> couponDOIPage = couponMapper.selectPage(pageInfo, new QueryWrapper<CouponDO>()
                .eq("publish", CouponPublishEnum.PUBLISH)
                .eq("category", CouponCategoryEnum.PROMOTION)
                .orderByDesc("create_time"));


        Map<String, Object> pageMap = new HashMap<>(3);
        //总条数
        pageMap.put("total_record", couponDOIPage.getTotal());
        //总页数
        pageMap.put("total_page", couponDOIPage.getPages());

        pageMap.put("current_data", couponDOIPage.getRecords().stream().map(obj -> beanProcess(obj)).collect(Collectors.toList()));


        return pageMap;
    }


    /**
     * 领劵接口
     *
     * @param couponId
     * @param category
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public JsonData addCoupon(long couponId, CouponCategoryEnum category) {

        LoginUser loginUser = LoginInterceptor.threadLocal.get();

        String lockKey = "lock:coupon:" + couponId;
        RLock rLock = redissonClient.getLock(lockKey);

        //多个线程进入，会阻塞等待释放锁
        rLock.lock();
        log.info("领劵接口加锁成功:{}", Thread.currentThread().getId());
        try {
            CouponDO couponDO = couponMapper.selectOne(new QueryWrapper<CouponDO>()
                    .eq("id", couponId)
                    .eq("category", category.name()));

            //优惠券是否可以领取
            this.checkCoupon(couponDO, loginUser.getId());

            //构建领劵记录
            CouponRecordDO couponRecordDO = new CouponRecordDO();
            BeanUtils.copyProperties(couponDO, couponRecordDO);
            couponRecordDO.setCreateTime(new Date());
            couponRecordDO.setUseState(CouponStateEnum.NEW.name());
            couponRecordDO.setUserId(loginUser.getId());
            couponRecordDO.setUserName(loginUser.getName());
            couponRecordDO.setCouponId(couponId);
            couponRecordDO.setId(null);

            //扣减库存
            int rows = couponMapper.reduceStock(couponId);
            if (rows == 1) {
                couponRecordMapper.insert(couponRecordDO);
            } else {
                log.warn("发放优惠卷失败:{},用户:{}", couponDO, loginUser);
                throw new BizException(BizCodeEnum.COUPON_NO_STOCK);
            }
        } finally {
            //解锁
            rLock.unlock();
            log.info("解锁成功");
        }

        return JsonData.buildSuccess();
    }

    /**
     * 新用户注册发放优惠券
     *
     * @param newUserCouponRequest
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public JsonData initNewUserCoupon(NewUserCouponRequest newUserCouponRequest) {
        LoginUser loginUser = new LoginUser();
        loginUser.setId(newUserCouponRequest.getUserId());
        loginUser.setName(newUserCouponRequest.getName());
        LoginInterceptor.threadLocal.set(loginUser);

        //查询用户有哪些优惠劵
        List<CouponDO> couponDOList = couponMapper.selectList(new QueryWrapper<CouponDO>()
                .eq("category", CouponCategoryEnum.NEW_USER.name()));
        for(CouponDO couponDO : couponDOList){
            //幂等操作，调用需要加锁
            this.addCoupon(couponDO.getId(),CouponCategoryEnum.NEW_USER);
        }
        return JsonData.buildSuccess();
    }

    /**
     * 校验是否可以领取
     *
     * @param couponDO
     * @param userId
     */
    private void checkCoupon(CouponDO couponDO, Long userId) {

        if (couponDO == null) {
            throw new BizException(BizCodeEnum.COUPON_NO_EXITS);
        }
        //库存是否足够
        if (couponDO.getStock() <= 0) {
            throw new BizException(BizCodeEnum.COUPON_NO_STOCK);
        }
        //判断是否是发布状态
        if (!couponDO.getPublish().equals(CouponPublishEnum.PUBLISH.name())) {
            throw new BizException(BizCodeEnum.COUPON_GET_FAIL);
        }

        //是否在领取时间范围
        long time = CommonUtil.getCurrentTimestamp();
        long start = couponDO.getStartTime().getTime();
        long end = couponDO.getEndTime().getTime();
        if (time < start || time > end) {
            throw new BizException(BizCodeEnum.COUPON_OUT_OF_TIME);
        }
        //用户是否超过限制
        int recordNum = couponRecordMapper.selectCount(new QueryWrapper<CouponRecordDO>()
                .eq("coupon_id", couponDO.getId())
                .eq("user_id", userId));
        if (recordNum >= couponDO.getUserLimit()) {
            throw new BizException(BizCodeEnum.COUPON_OUT_OF_LIMIT);
        }
    }

    private CouponVO beanProcess(CouponDO couponDO) {
        CouponVO couponVO = new CouponVO();
        BeanUtils.copyProperties(couponDO, couponVO);
        return couponVO;
    }
}
