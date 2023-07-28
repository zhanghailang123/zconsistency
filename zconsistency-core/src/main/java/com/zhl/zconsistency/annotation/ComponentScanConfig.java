package com.zhl.zconsistency.annotation;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * @author hailang.zhang
 * @since 2023-07-28
 */
@Configuration
@ComponentScan(value = {"com.zhl.zconsistency"})
@MapperScan(basePackages = {"com.zhl.zconsistency.mapper"})
public class ComponentScanConfig {
}