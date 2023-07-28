package com.zhl.zconsistency.annotation;

import org.springframework.context.annotation.ImportSelector;
import org.springframework.core.type.AnnotationMetadata;

/**
 * @author hailang.zhang
 * @since 2023-07-28
 */
public class ConsistencySelector implements ImportSelector {

    @Override
    public String[] selectImports(AnnotationMetadata importingClassMetadata) {
        return new String[]{ComponentScanConfig.class.getName()};
    }
}