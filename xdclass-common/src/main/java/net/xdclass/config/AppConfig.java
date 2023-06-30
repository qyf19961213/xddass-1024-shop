package net.xdclass.config;

import feign.RequestInterceptor;
import lombok.Data;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;

/**
 * 小滴课堂,愿景：让技术不再难学
 *
 * @Description
 * @Author 二当家小D
 * @Remark 有问题直接联系我，源码-笔记-技术交流群
 * @Version 1.0
 **/
@Configuration
@Data
public class AppConfig {


    @Value("${spring.redis.host}")
    private String redisHost;

    @Value("${spring.redis.port}")
    private String redisPort;


    /*@Value("${spring.redis.password}")
    private String redisPwd;*/


    /**
     * 配置分布式锁的redisson
     * @return
     */
    @Bean
    public RedissonClient redissonClient(){
        Config config = new Config();

        //单机方式
        //config.useSingleServer().setPassword(redisPwd).setAddress("redis://"+redisHost+":"+redisPort);
        config.useSingleServer().setAddress("redis://"+redisHost+":"+redisPort);
        //集群
        //config.useClusterServers().addNodeAddress("redis://192.31.21.1:6379","redis://192.31.21.2:6379")

        RedissonClient redissonClient = Redisson.create(config);
        return redissonClient;
    }


    /**
     * 避免存储的key乱码，hash结构依旧会乱码
     * @param factory
     * @return
     */
    @Bean
    public RedisTemplate<String,Object> redisTemplate(RedisConnectionFactory factory){

        RedisTemplate<String,Object> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(factory);

        RedisSerializer redisSerializer = new StringRedisSerializer();

        redisTemplate.setKeySerializer(redisSerializer);
        redisTemplate.setValueSerializer(redisSerializer);

        return redisTemplate;
    }

    /**
     * feign调用丢失token解决方式，新增拦截器
     * @return
     */
    @Bean
    public RequestInterceptor requestInterceptor(){

        return template -> {

            ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();

            if(attributes!=null){

                HttpServletRequest httpServletRequest = attributes.getRequest();

                if(httpServletRequest == null){
                    return;
                }
                String token = httpServletRequest.getHeader("token");
                template.header("token",token);
            }
        };

    }

}
